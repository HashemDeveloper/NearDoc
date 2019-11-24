package com.project.neardoc.utils

import android.hardware.SensorManager
import androidx.lifecycle.LiveData

interface IStepCountSensor {
    fun initiateStepCounterSensor(
        sensorManager: SensorManager
    )
    fun getSensorEvent(): LiveData<Int>
    fun clearDisposable()
    fun getDelayString(): String
    fun flashStepCounter()
    fun unRegisterCounterSensor(sensorManager: SensorManager)
}