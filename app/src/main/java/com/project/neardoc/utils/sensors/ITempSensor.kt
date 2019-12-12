package com.project.neardoc.utils.sensors

import android.hardware.SensorEvent
import android.hardware.SensorManager
import androidx.lifecycle.LiveData
import com.project.neardoc.utils.sensors.TempSensor

interface ITempSensor {
    fun initiateTempSensor(sensorManager: SensorManager): TempSensor
    fun registerListener(sensorManager: SensorManager)
    fun getAccuracyChangedLiveData(): LiveData<TempSensor.AccuracyChangedModel>
    fun getSensorEvent(): LiveData<SensorEvent>
}