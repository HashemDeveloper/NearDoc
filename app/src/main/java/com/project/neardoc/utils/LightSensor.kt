package com.project.neardoc.utils

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import javax.inject.Inject

class LightSensor @Inject constructor(): ILightSensor, SensorEventListener {

    private var mLightSensor: Sensor?= null
    private val sensorEventLiveData: MutableLiveData<SensorEvent>?= MutableLiveData()

    override fun initiateLightSensor(sensorManager: SensorManager): LightSensor {
        this.mLightSensor = sensorManager.getDefaultSensor(SensorManager.SENSOR_DELAY_NORMAL)
        return this
    }

    override fun registerListener(sensorManager: SensorManager) {
        if (this.mLightSensor != null) {
            sensorManager.registerListener(this, this.mLightSensor, Sensor.TYPE_LIGHT)
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

    }

    override fun onSensorChanged(event: SensorEvent?) {
        this.sensorEventLiveData?.value = event
    }

    override fun getSensorEventLiveData(): LiveData<SensorEvent> {
        return this.sensorEventLiveData!!
    }
}