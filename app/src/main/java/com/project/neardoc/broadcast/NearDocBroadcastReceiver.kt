package com.project.neardoc.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.project.neardoc.utils.Constants
import dagger.android.AndroidInjection
import javax.inject.Inject

class NearDocBroadcastReceiver @Inject constructor(): BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
       AndroidInjection.inject(this, context)
        when (intent!!.action) {
            Constants.CONNECTIVITY_ACTION -> {
                //do nothing
            }
            Constants.LOCATION_SERVICE_ACTION ->{
                Log.i("Good: ", "Yes")
            }
            Constants.USER_STATE_ACTION -> {
                Log.i("Good: ", "Yes")
            }
            Constants.STEP_COUNTER_SERVICE_ACTION -> {

            }
        }
    }
}