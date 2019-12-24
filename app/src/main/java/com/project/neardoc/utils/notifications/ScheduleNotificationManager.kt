package com.project.neardoc.utils.notifications

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.work.*
import com.project.neardoc.BuildConfig
import com.project.neardoc.data.local.ISharedPrefService
import com.project.neardoc.data.local.IUserInfoDao
import com.project.neardoc.utils.calories.ICalorieBurnedCalculator
import com.project.neardoc.workers.StepCountNotificationWorker
import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class ScheduleNotificationManager @Inject constructor(): IScheduleNotificationManager, CoroutineScope {
    companion object {
       @JvmStatic private val TAG: String = ScheduleNotificationManager::class.java.canonicalName!!
    }
    @Inject
    lateinit var context: Context
    @Inject
    lateinit var iSharedPrefService: ISharedPrefService
    @Inject
    lateinit var iCalorieBurnedCalculator: ICalorieBurnedCalculator
    @Inject
    lateinit var iUserInfoDao: IUserInfoDao
    private var notificationWorkLiveData: LiveData<WorkInfo>?= null

    private val job = Job()

    override suspend fun scheduleRegularNotification() {
        var notificationRequest: PeriodicWorkRequest?= null
        var workManager: WorkManager?= null
        withContext(Dispatchers.IO) {
            launch {
                notificationRequest = PeriodicWorkRequest.Builder(
                    StepCountNotificationWorker::class.java, PeriodicWorkRequest.MIN_PERIODIC_INTERVAL_MILLIS
                    , TimeUnit.MILLISECONDS, PeriodicWorkRequest.MIN_PERIODIC_FLEX_MILLIS, TimeUnit.MILLISECONDS)
                    .build()
                workManager = WorkManager.getInstance(context)
                workManager!!.enqueueUniquePeriodicWork(TAG, ExistingPeriodicWorkPolicy.REPLACE, notificationRequest!!)
            }.invokeOnCompletion {
                if (it != null && it.localizedMessage != null) {
                    if (BuildConfig.DEBUG) {
                        Log.i(TAG, it.localizedMessage!!)
                    }
                } else {
                    launch {
                        withContext(Dispatchers.Main) {
                            notificationWorkLiveData = workManager!!.getWorkInfoByIdLiveData(notificationRequest!!.id)
                            notificationWorkLiveData!!.observeForever(observeNotificationWorkLiveData())
                        }
                    }
                }
            }
        }
    }
    private fun observeNotificationWorkLiveData(): Observer<WorkInfo> {
        return Observer {
            if (it?.state == null) {
                return@Observer
            } else {
                when (it.state) {
                    WorkInfo.State.RUNNING -> {
                        if (BuildConfig.DEBUG) {
                            Log.i(TAG, "Step count notification request schedule is running")
                        }
                    }
                    WorkInfo.State.BLOCKED -> {
                        if (BuildConfig.DEBUG) {
                            Log.i(TAG, "Step count notification request schedule is blocked")
                        }
                    }
                    WorkInfo.State.CANCELLED -> {
                        if (BuildConfig.DEBUG) {
                            Log.i(TAG, "Step count notification request schedule is cancelled")
                        }
                    }
                    WorkInfo.State.ENQUEUED -> {
                        if (BuildConfig.DEBUG) {
                            Log.i(TAG, "Step count notification request schedule is enqueued")
                        }
                    }
                    WorkInfo.State.SUCCEEDED -> {
                        if (BuildConfig.DEBUG) {
                            Log.i(TAG, "Step count notification request schedule is succeeded")
                        }
                    }
                    WorkInfo.State.FAILED -> {
                        if (BuildConfig.DEBUG) {
                            Log.i(TAG, "Step count notification request schedule is failed")
                        }
                    }
                }
            }
        }
    }

    override fun onCleared() {
        if (this.notificationWorkLiveData != null) {
            this.notificationWorkLiveData!!.removeObserver(observeNotificationWorkLiveData())
        }
    }
    override val coroutineContext: CoroutineContext
        get() = this.job + Dispatchers.Main

}