package com.project.neardoc.utils

import android.content.Context
import android.hardware.SensorEvent
import android.hardware.SensorManager
import androidx.lifecycle.LiveData

interface ITempSensor {
    fun initiateTempSensor(sensorManager: SensorManager): TempSensor
    fun registerListener(sensorManager: SensorManager)
    fun getAccuracyChangedLiveData(): LiveData<TempSensor.AccuracyChangedModel>
    fun getSensorEvent(): LiveData<SensorEvent>
}