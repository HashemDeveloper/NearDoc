package com.project.neardoc.utils.sensors

import android.hardware.SensorManager
import android.widget.FrameLayout
import androidx.fragment.app.FragmentActivity
import com.google.android.material.textview.MaterialTextView

interface IDeviceSensors {
    fun setupDeviceSensor(
        activity: FragmentActivity,
        roomTempTextView: MaterialTextView,
        stepCountParentLayout: FrameLayout
    )
    fun clearObservers(view: MaterialTextView, stepCountView: MaterialTextView)
    fun unRegisterSensorListener()
    fun flashStepCounter()
}