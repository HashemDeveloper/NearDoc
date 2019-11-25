package com.project.neardoc.services

import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.SensorManager
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.work.Data
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.project.neardoc.broadcast.NearDocBroadcastReceiver
import com.project.neardoc.data.local.ISharedPrefService
import com.project.neardoc.utils.Constants
import com.project.neardoc.utils.INotificationScheduler
import com.project.neardoc.utils.IStepCountSensor
import dagger.android.AndroidInjection
import io.reactivex.Observer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class StepCounterService @Inject constructor(): Service(), CoroutineScope {
    companion object {
        @JvmStatic val STEP_COUNT_NOTIFICATION_REQ_CODE = 100
    }
    @Inject
    lateinit var iStepCounterSensor: IStepCountSensor
    @Inject
    lateinit var nearDocBroadcastReceiver: NearDocBroadcastReceiver
    @Inject
    lateinit var context: Context
    private var sensorManager: SensorManager?= null
    @Inject
    lateinit var iNotificationScheduler: INotificationScheduler
    @Inject
    lateinit var iSharedPrefService: ISharedPrefService

    private var job = Job()

    override val coroutineContext: CoroutineContext
        get() = this.job + Dispatchers.IO

    override fun onCreate() {
        AndroidInjection.inject(this)
        launch {
            sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
            iStepCounterSensor.initiateStepCounterSensor(sensorManager!!)
        }
        super.onCreate()
    }

    override fun onBind(intent: Intent?): IBinder? {
       return Binder()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        val result: Int = this.iSharedPrefService.getLastStepCountValue()
        this.iNotificationScheduler.scheduleJob(Constants.STEP_COUNTER_SERVICE_ACTION, STEP_COUNT_NOTIFICATION_REQ_CODE,
            1, 0, result)
        return START_STICKY
    }
    override fun onDestroy() {
        super.onDestroy()
    }
}