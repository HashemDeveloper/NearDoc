package com.project.neardoc.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.project.neardoc.utils.Constants
import dagger.android.AndroidInjection
import javax.inject.Inject

class ConnectionBroadcastReceiver @Inject constructor(): BroadcastReceiver() {

    override fun onReceive(p0: Context?, p1: Intent?) {
       AndroidInjection.inject(this, p0)
        when (p1!!.action) {
            Constants.CONNECTIVITY_ACTION -> {
                //do nothing
            }
            Constants.LOCATION_SERVICE_ACTION ->{
                Log.i("Good: ", "Yes")
            }
        }
    }
}