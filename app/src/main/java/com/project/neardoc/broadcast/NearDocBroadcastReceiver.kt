package com.project.neardoc.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.project.neardoc.data.local.ISharedPrefService
import com.project.neardoc.services.StepCounterService
import com.project.neardoc.utils.Constants
import com.project.neardoc.utils.INotificationBuilder
import com.project.neardoc.utils.INotificationScheduler
import dagger.android.AndroidInjection
import javax.inject.Inject

class NearDocBroadcastReceiver @Inject constructor(): BroadcastReceiver() {
    @Inject
    lateinit var iNotificationBuilder: INotificationBuilder
    @Inject
    lateinit var iSharedPrefService: ISharedPrefService

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
                val counterValue: Int = this.iSharedPrefService.getLastStepCountValue()
                //TODO: Calucate calories from this value and display
                this.iNotificationBuilder.createNotification(StepCounterService.STEP_COUNT_NOTIFICATION_REQ_CODE, "STEP_COUNT",
                    123,
                    com.project.neardoc.R.drawable.ic_walk_2x,
                    com.project.neardoc.R.drawable.ic_walk_2x, "You have walked: ", counterValue.toString())
            }
        }
    }
}