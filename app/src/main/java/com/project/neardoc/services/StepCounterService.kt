package com.project.neardoc.services

import android.app.IntentService
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.SensorManager
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.Observer
import com.project.neardoc.broadcast.NearDocBroadcastReceiver
import com.project.neardoc.utils.Constants
import com.project.neardoc.utils.IStepCountSensor
import dagger.android.AndroidInjection
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class StepCounterService @Inject constructor(): Service(), CoroutineScope {
    @Inject
    lateinit var iStepCounterSensor: IStepCountSensor
    @Inject
    lateinit var nearDocBroadcastReceiver: NearDocBroadcastReceiver
    @Inject
    lateinit var context: Context
    private var sensorManager: SensorManager?= null
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
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}