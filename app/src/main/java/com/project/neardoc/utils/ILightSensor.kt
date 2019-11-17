package com.project.neardoc.utils

import android.hardware.SensorEvent
import android.hardware.SensorManager
import androidx.lifecycle.LiveData

interface ILightSensor {
    fun initiateLightSensor(sensorManager: SensorManager): LightSensor
    fun registerListener(sensorManager: SensorManager)
    fun getSensorEventLiveData(): LiveData<SensorEvent>
}