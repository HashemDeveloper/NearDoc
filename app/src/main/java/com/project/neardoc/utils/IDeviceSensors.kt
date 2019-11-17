package com.project.neardoc.utils

import androidx.fragment.app.FragmentActivity
import com.google.android.material.textview.MaterialTextView

interface IDeviceSensors {
    fun setupDeviceSensor(
        activity: FragmentActivity,
        fragmentAccountRoomTempViewId: MaterialTextView
    )
    fun clearObservers(view: MaterialTextView)
}