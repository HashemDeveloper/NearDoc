package com.project.neardoc.worker

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.project.neardoc.BuildConfig
import com.project.neardoc.broadcast.NearDocBroadcastReceiver
import com.project.neardoc.data.local.ISharedPrefService
import com.project.neardoc.data.local.IStepCountDurationListDao
import com.project.neardoc.data.local.IUserInfoDao
import com.project.neardoc.di.workermanager.NearDocWorkerInjection
import com.project.neardoc.model.localstoragemodels.StepCountDurationList
import com.project.neardoc.model.localstoragemodels.UserPersonalInfo
import com.project.neardoc.utils.Constants
import com.project.neardoc.utils.GenderType
import com.project.neardoc.utils.calories.IAdvancedCalorieBurnCalculator
import com.project.neardoc.utils.calories.ICalorieBurnedCalculator
import com.project.neardoc.viewmodel.AccountPageViewModel
import kotlinx.coroutines.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class StepCountNotificationWorker @Inject constructor(
    val context: Context,
    workerParameters: WorkerParameters
) : Worker(context, workerParameters), CoroutineScope {

    companion object {
        @JvmStatic val TAG: String = StepCountNotificationWorker::class.java.canonicalName!!
        @JvmStatic val MALE_STRIDE_LENGTH_CONSTANT: Float = .415f
        @JvmStatic val FEMALE_STRIDE_LENGTH_CONSTANT: Float = .413f
    }
    @Inject
    lateinit var iSharedPrefService: ISharedPrefService
    @Inject
    lateinit var iCalorieBurnedCalculator: ICalorieBurnedCalculator
    @Inject
    lateinit var iAdvancedCalorieBurnCalculator: IAdvancedCalorieBurnCalculator
    @Inject
    lateinit var iUserInfoDao: IUserInfoDao
    @Inject
    lateinit var iStepCountDurationListDao: IStepCountDurationListDao
    private val job = Job()
    private val countDownLatch: CountDownLatch = CountDownLatch(1)

    override fun doWork(): Result {
        NearDocWorkerInjection.inject(this)
        var burnedCalories: Double?= null
        var caloriesBurnedResult: Float?= null // old value
        var firstDuration: StepCountDurationList?
        var lastDuration: StepCountDurationList?
        var stepCountDurationList: List<StepCountDurationList>?
        launch {
            withContext(Dispatchers.IO) {
                val stepCount: Int = iSharedPrefService.getLastStepCountValue()
                val email: String = iSharedPrefService.getUserEmail()
                val userPersonalInfo: UserPersonalInfo = iUserInfoDao.getUserByEmail(email)
                var genderType: GenderType?= null
                val height: Double = userPersonalInfo.userHeight
                val weight: Double = userPersonalInfo.userWeight
                val age: Float = userPersonalInfo.userAge.toFloat()
                val gender: String = userPersonalInfo.genderType
                val strideLengthInMeter: Float?
                if (gender == "Male") {
                    genderType = GenderType.MALE
                    strideLengthInMeter = (height * MALE_STRIDE_LENGTH_CONSTANT).toFloat()
                } else {
                    genderType = GenderType.FEMALE
                    strideLengthInMeter = (height * FEMALE_STRIDE_LENGTH_CONSTANT).toFloat()
                }
                burnedCalories = iCalorieBurnedCalculator.calculateCalorieBurned(height, weight, stepCount)

                stepCountDurationList = iStepCountDurationListDao.getAllDurationList()
                firstDuration = iStepCountDurationListDao.getFirstDuration()
                lastDuration = iStepCountDurationListDao.getLastDuration()
                var startTime = 0L
                var endTime = 0L
                if (firstDuration != null && lastDuration != null) {
                    startTime = TimeUnit.MILLISECONDS.toSeconds(firstDuration!!.time)
                    endTime = TimeUnit.MILLISECONDS.toSeconds(lastDuration!!.time)
                }


                if (BuildConfig.DEBUG) {
                    for (durationList: StepCountDurationList in stepCountDurationList!!) {
                        Log.i(TAG, "Duration ${durationList.time}")
                    }
                }
                if (BuildConfig.DEBUG) {
                    Log.i(TAG, "Start Time: $startTime")
                    Log.i(TAG, "End Time: $endTime")
                }
                val durationInSeconds: Long = endTime - startTime
                caloriesBurnedResult = iAdvancedCalorieBurnCalculator.calculateEnergyExpenditure(height.toFloat(),
                    age, weight.toFloat(), genderType, durationInSeconds, stepCount, strideLengthInMeter)
            }
        }.invokeOnCompletion {
            if (it != null && it.localizedMessage != null) {
                if (BuildConfig.DEBUG) {
                    Log.i(TAG, it.localizedMessage!!)
                }
            } else {
                if (BuildConfig.DEBUG) {
                    Log.i(TAG, "CaloriesBurned $caloriesBurnedResult")
                }

                val broadCastIntent: Intent = Intent(this.context, NearDocBroadcastReceiver::class.java)
                    .putExtra(Constants.CALORIES_BURNED_RESULT, caloriesBurnedResult!!.toDouble())
                    .setAction(Constants.STEP_COUNTER_SERVICE_ACTION)
                this.context.sendBroadcast(broadCastIntent)
                this.countDownLatch.countDown()
            }
        }
        try {
            this.countDownLatch.await()
        } catch (interruptedEx: InterruptedException) {
            if (BuildConfig.DEBUG) {
                Log.i("InterruptedEx: ", interruptedEx.localizedMessage!!)
            }
        }
        return Result.success()
    }

    override val coroutineContext: CoroutineContext
        get() = this.job + Dispatchers.Main
}