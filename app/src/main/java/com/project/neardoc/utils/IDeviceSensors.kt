package com.project.neardoc.utils

import android.hardware.SensorManager
import androidx.fragment.app.FragmentActivity
import com.google.android.material.textview.MaterialTextView

interface IDeviceSensors {
    fun setupDeviceSensor(
        activity: FragmentActivity,
        roomTempTextView: MaterialTextView,
        stepCountView: MaterialTextView
    )
    fun clearObservers(view: MaterialTextView, stepCountView: MaterialTextView)
    fun unRegisterSensorListener()
    fun flashStepCounter()
}