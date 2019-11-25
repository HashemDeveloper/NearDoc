package com.project.neardoc.services

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.SensorManager
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.work.*
import com.project.neardoc.broadcast.NearDocBroadcastReceiver
import com.project.neardoc.data.local.ISharedPrefService
import com.project.neardoc.utils.Constants
import com.project.neardoc.utils.INotificationScheduler
import com.project.neardoc.utils.IStepCountSensor
import com.project.neardoc.worker.StepCountNotificationWorker
import dagger.android.AndroidInjection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class StepCounterService @Inject constructor(): Service(), CoroutineScope {
    companion object {
        @JvmStatic val MIN_PERIODIC_INTERVAL: Long = 15 * 60 * 1000L
        @JvmStatic val MIN_PERIODIC_FLEX_INTERVAL: Long = 5 * 60 * 1000L
        @JvmStatic val STEP_COUNT_NOTIFICATION_REQ_CODE = 100
    }
    @Inject
    lateinit var iStepCounterSensor: IStepCountSensor
    @Inject
    lateinit var nearDocBroadcastReceiver: NearDocBroadcastReceiver
    @Inject
    lateinit var context: Context
    private var sensorManager: SensorManager?= null
    private var workerLiveData: LiveData<WorkInfo>?= null
    @Inject
    lateinit var iNotificationScheduler: INotificationScheduler

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
        this.iNotificationScheduler.scheduleJob(Constants.STEP_COUNTER_SERVICE_ACTION, STEP_COUNT_NOTIFICATION_REQ_CODE,
            1, 0)
        return START_STICKY
    }
    private fun processOneTimeADayNotifyReq() {
        val oneTimeADayReq: PeriodicWorkRequest = PeriodicWorkRequest.Builder(StepCountNotificationWorker::class.java, PeriodicWorkRequest.MIN_PERIODIC_INTERVAL_MILLIS
            , TimeUnit.MINUTES, PeriodicWorkRequest.MIN_PERIODIC_FLEX_MILLIS, TimeUnit.MINUTES)
            .build()
        val workManager: WorkManager = WorkManager.getInstance(this.context)
        workManager.enqueue(oneTimeADayReq)
        this.workerLiveData = workManager.getWorkInfoByIdLiveData(oneTimeADayReq.id)
        this.workerLiveData!!.observeForever(observeStepWorkerLiveData())
    }
    private fun observeStepWorkerLiveData(): Observer<WorkInfo> {
        return Observer {
            if (it?.state == null) {
                return@Observer
            } else {
                when (it.state) {
                    WorkInfo.State.RUNNING -> {
                        Log.i("Running: ", "Shit is running")
                    }
                    WorkInfo.State.BLOCKED -> {
                        Log.i("Blocked: ", "Shit is blocked")
                    }
                    WorkInfo.State.CANCELLED -> {
                        Log.i("Cancled: ", "Shit is canceled")
                    }
                    WorkInfo.State.ENQUEUED -> {
                        Log.i("Enqueued:", "Shit is enqueued")
                    }
                    WorkInfo.State.SUCCEEDED -> {
                        val outputData:Data = it.outputData
                        val calories: Int = outputData.getInt(Constants.STEP_COUNT_VALUE, 0)
                        Log.i("Calories: ", calories.toString())
                    }
                    WorkInfo.State.FAILED -> {
                        Log.i("Failed: ", "Failed")
                    }
                    else -> {
                        return@Observer
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}