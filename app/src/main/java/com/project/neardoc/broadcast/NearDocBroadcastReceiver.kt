package com.project.neardoc.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.hardware.SensorManager
import android.util.Log
import com.project.neardoc.data.local.ISharedPrefService
import com.project.neardoc.model.WeekDays
import com.project.neardoc.model.WeekDaysType
import com.project.neardoc.services.StepCounterService
import com.project.neardoc.utils.*
import dagger.android.AndroidInjection
import java.util.*
import javax.inject.Inject

class NearDocBroadcastReceiver @Inject constructor(): BroadcastReceiver() {
    @Inject
    lateinit var iStepCounterSensor: IStepCountSensor
    @Inject
    lateinit var iNotificationBuilder: INotificationBuilder
    private var sensorManager: SensorManager? = null
    @Inject
    lateinit var iSharedPrefService: ISharedPrefService

    override fun onReceive(context: Context?, intent: Intent?) {
        AndroidInjection.inject(this, context)
        when (intent!!.action) {
            Constants.CONNECTIVITY_ACTION -> {
                //do nothing
            }
            Constants.LOCATION_SERVICE_ACTION -> {
                Log.i("Good: ", "Yes")
            }
            Constants.USER_STATE_ACTION -> {
                Log.i("Good: ", "Yes")
            }
            Constants.STEP_COUNTER_SERVICE_ACTION -> {
                val isNotificationOn: Boolean = this.iSharedPrefService.getIsNotificationEnabled()
                val name: String = this.iSharedPrefService.getUserName()
                if (isNotificationOn) {
                    sensorManager = context!!.getSystemService(Context.SENSOR_SERVICE) as SensorManager
                    iStepCounterSensor.initiateStepCounterSensor(sensorManager!!)
                    val result: Int = intent.getIntExtra(Constants.STEP_COUNT_VALUE, 0)
                    this.iNotificationBuilder.createNotification(StepCounterService.STEP_COUNT_NOTIFICATION_REQ_CODE, "STEP_COUNT",
                        123,
                        com.project.neardoc.R.drawable.ic_walk_2x,
                        com.project.neardoc.R.drawable.heart, "Hi $name!",
                        getMessage()
                     )
                }
            }
        }
    }
    private fun getMessage(): String {
        val calorieMessageGenerator = CalorieMessageGenerator()
        return calorieMessageGenerator.getStringBasedOnWeekDays()
    }
}