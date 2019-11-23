package com.project.neardoc.utils

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build.VERSION_CODES
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.project.neardoc.BuildConfig
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class StepCountSensor @Inject constructor(private val context: Context): IStepCountSensor, SensorEventListener {
    companion object {
        const val DEFAULT_LATENCY: Int = 0
        val TAG: String? = this::class.java.canonicalName
    }

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private var mStepSensor: Sensor?= null
    private val sensorEventLiveData: MutableLiveData<Int>?= MutableLiveData()
    private var mSteps: Int?= null
    private var mStepCounter: Int = 0

    @RequiresApi(VERSION_CODES.KITKAT)
    override fun initiateStepCounterSensor(
        sensorManager: SensorManager
    ) {
        this.mStepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        val isRegistered: Boolean = sensorManager.registerListener(this, this.mStepSensor, SensorManager.SENSOR_DELAY_NORMAL, DEFAULT_LATENCY)
        if (isRegistered) {
            if (BuildConfig.DEBUG) {
                Log.i("StepCountRegisteredAt: " + TAG, sensorManager.toString())
            }
        }
    }

    override fun getSensorEvent(): LiveData<Int> {
        return this.sensorEventLiveData!!
    }

    override fun onAccuracyChanged(sensor: Sensor?, p1: Int) {
        val s: Sensor = sensor!!
        Log.i("Sensor: ", s.name)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        Log.i("Event: ", "${event!!.values}")
        this.compositeDisposable.add(getDiffResult(event)!!
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({values ->
                run {
                    this.sensorEventLiveData!!.value = values
                }
            }, { error ->
                run {
                    if (error.localizedMessage != null) {
                        Log.i("Error: ", error.localizedMessage!!)
                    }
                }
            }))
    }

    private fun getDiffResult(event: SensorEvent?): Single<Int>? {
        return Single.fromCallable {
            val sensor: Sensor = event?.sensor!!
            if (sensor.type == Sensor.TYPE_STEP_COUNTER) {
                val initialStep: Int = event.values[0].toInt()
                if (this.mStepCounter < 1) {
                    // get the initial value
                    this.mStepCounter = initialStep
                }
                // calculate step taken based on the first counter value received
                this.mSteps = initialStep - this.mStepCounter
                // add the number of steps previously taken otherwise counter will start at
                // this will keep the counter consistent across rotation changes
                this.mSteps = this.mSteps!! + this.mStepCounter
            }
            this.mSteps
        }
    }
}