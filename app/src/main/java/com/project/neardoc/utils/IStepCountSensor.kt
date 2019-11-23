package com.project.neardoc.utils

import android.hardware.SensorManager
import androidx.lifecycle.LiveData

interface IStepCountSensor {
    fun initiateStepCounterSensor(
        sensorManager: SensorManager
    )
    fun getSensorEvent(): LiveData<Int>
}