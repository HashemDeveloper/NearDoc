package com.project.neardoc.services

import android.app.Notification
import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.SensorManager
import android.os.Binder
import android.os.Build
import android.os.IBinder
import com.project.neardoc.R
import com.project.neardoc.data.local.ISharedPrefService
import com.project.neardoc.data.local.IUserInfoDao
import com.project.neardoc.model.localstoragemodels.UserPersonalInfo
import com.project.neardoc.utils.Constants
import com.project.neardoc.utils.calories.ICalorieBurnedCalculator
import com.project.neardoc.utils.notifications.INotificationBuilder
import com.project.neardoc.utils.notifications.INotificationScheduler
import com.project.neardoc.utils.notifications.NotificationType
import com.project.neardoc.utils.sensors.IStepCountSensor
import dagger.android.AndroidInjection
import kotlinx.coroutines.*
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class StepCountForegroundService @Inject constructor() : Service(), CoroutineScope {
    companion object {
        @JvmStatic
        val STEP_COUNT_FOREGROUND_SERVICE_REQ_CODE = 121
        @JvmStatic
        private val CHANEL_ID = "STEP_COUNT_FOREGROUND"
    }
    @Inject
    lateinit var iNotificationScheduler: INotificationScheduler
    @Inject
    lateinit var iCalorieBurnedCalculator: ICalorieBurnedCalculator
    @Inject
    lateinit var iUserInfoDao: IUserInfoDao
    @Inject
    lateinit var iSharedPrefService: ISharedPrefService
    @Inject
    lateinit var context: Context
    @Inject
    lateinit var iNotificationBuilder: INotificationBuilder
    @Inject
    lateinit var iStepCounterSensor: IStepCountSensor
    private var sensorManager: SensorManager? = null
    private val mBinder: IBinder = LocalBinder()
    private val job = Job()


    override fun onCreate() {
        AndroidInjection.inject(this)
        launch {
            sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
            iStepCounterSensor.initiateStepCounterSensor(sensorManager!!)
            dispatchForegroundService()
        }
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        if (intent != null) {
            val isStartedFromNotification: Boolean = intent.getBooleanExtra(Constants.STEP_COUNT_FOREGROUND, false)
            if (isStartedFromNotification) {
                stopSelf()
            }
        }

        return START_STICKY
    }
    private suspend fun scheduleRegularNotification() {
        withContext(Dispatchers.IO) {
            launch {
                val stepCount: Int = iSharedPrefService.getLastStepCountValue()
                val email: String = iSharedPrefService.getUserEmail()
                val userPersonalInfo: UserPersonalInfo = iUserInfoDao.getUserByEmail(email)
                val height: Double = userPersonalInfo.userHeight
                val weight: Double = userPersonalInfo.userWeight
                val burnedCalories: Double = iCalorieBurnedCalculator.calculateCalorieBurned(height, weight, stepCount)
                iNotificationScheduler.scheduleJob(Constants.STEP_COUNTER_SERVICE_ACTION,
                    StepCounterService.STEP_COUNT_NOTIFICATION_REQ_CODE,
                    2, 0, burnedCalories.toInt())
            }
        }
    }

    private suspend fun dispatchForegroundService() {
        withContext(Dispatchers.Main) {
           if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
               startForeground(Constants.STEP_COUNT_FOREGROUND_NOTIFICATION_ID, getNotification())
           }
        }
    }
    private fun startForegroundService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForeground(Constants.STEP_COUNT_FOREGROUND_NOTIFICATION_ID, getNotification())
        } else {
            startForeground(Constants.STEP_COUNT_FOREGROUND_NOTIFICATION_ID, getNotification())
        }
    }

    override fun onBind(p0: Intent?): IBinder? {
        stopForegroundService()
        return this.mBinder
    }

    override fun onRebind(intent: Intent?) {
        stopForegroundService()
        super.onRebind(intent)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        super.onUnbind(intent)
        startForegroundService()
        return true
    }

    private fun stopForegroundService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            stopForeground(true)
        } else {
            stopForeground(true)
        }
    }

    private fun getNotification(): Notification {
        return this.iNotificationBuilder.createNotification(
            NotificationType.NOTIFICATION_FOREGROUND,
            STEP_COUNT_FOREGROUND_SERVICE_REQ_CODE,
            CHANEL_ID,
            R.drawable.heart,
            0,
            "",
            "",
            0
        )
    }
    inner class LocalBinder: Binder() {
        val service: StepCountForegroundService
        get() = this@StepCountForegroundService
    }

    override val coroutineContext: CoroutineContext
        get() = this.job + Dispatchers.IO
}