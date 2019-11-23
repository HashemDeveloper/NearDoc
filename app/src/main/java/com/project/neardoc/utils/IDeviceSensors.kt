package com.project.neardoc.utils

import androidx.fragment.app.FragmentActivity
import com.google.android.material.textview.MaterialTextView

interface IDeviceSensors {
    fun setupDeviceSensor(
        activity: FragmentActivity,
        roomTempTextView: MaterialTextView,
        stepCountView: MaterialTextView
    )
    fun clearObservers(view: MaterialTextView, stepCountView: MaterialTextView)
}