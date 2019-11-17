package com.project.neardoc.utils

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import javax.inject.Inject

class TempSensor @Inject constructor(): ITempSensor, SensorEventListener {

    private var mTempSensor: Sensor?= null
    private val accuracyChangedLiveData: MutableLiveData<AccuracyChangedModel> = MutableLiveData()
    private val sensorEventLiveData: MutableLiveData<SensorEvent> = MutableLiveData()

    override fun onAccuracyChanged(sensor: Sensor?, p1: Int) {
        val accuracyChangedModel = AccuracyChangedModel(sensor, p1)
        this.accuracyChangedLiveData.value = accuracyChangedModel
    }

    override fun onSensorChanged(event: SensorEvent?) {
        this.sensorEventLiveData.value = event
    }

    override fun initiateTempSensor(sensorManager: SensorManager): TempSensor {
        this.mTempSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)
        return this
    }

    override fun registerListener(sensorManager: SensorManager) {
       if (this.mTempSensor != null) {
           sensorManager.registerListener(this, this.mTempSensor, SensorManager.SENSOR_DELAY_NORMAL)
       }
    }

    override fun getAccuracyChangedLiveData(): LiveData<AccuracyChangedModel> {
        return this.accuracyChangedLiveData
    }

    override fun getSensorEvent(): LiveData<SensorEvent> {
        return this.sensorEventLiveData
    }

    data class AccuracyChangedModel constructor(val sensor: Sensor?, val accuracy: Int?)
}
