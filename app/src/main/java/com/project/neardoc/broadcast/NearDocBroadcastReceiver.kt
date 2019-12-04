package com.project.neardoc.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.hardware.SensorManager
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.project.neardoc.data.local.ISharedPrefService
import com.project.neardoc.events.NotifySilentEvent
import com.project.neardoc.model.WeekDays
import com.project.neardoc.model.WeekDaysType
import com.project.neardoc.services.StepCounterService
import com.project.neardoc.utils.*
import dagger.android.AndroidInjection
import org.greenrobot.eventbus.EventBus
import java.util.*
import javax.inject.Inject

class NearDocBroadcastReceiver @Inject constructor(): BroadcastReceiver(), LifecycleObserver {
    @Inject
    lateinit var iStepCounterSensor: IStepCountSensor
    @Inject
    lateinit var iNotificationBuilder: INotificationBuilder
    private var sensorManager: SensorManager? = null
    @Inject
    lateinit var iSharedPrefService: ISharedPrefService
    private var isOnForeground: Boolean?= false

    override fun onReceive(context: Context?, intent: Intent?) {
        AndroidInjection.inject(this, context)
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
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
                val result: Int = intent.getIntExtra(Constants.STEP_COUNT_VALUE, 0)
                if (isNotificationOn && !this.isOnForeground!!) {
                    sensorManager = context!!.getSystemService(Context.SENSOR_SERVICE) as SensorManager
                    iStepCounterSensor.initiateStepCounterSensor(sensorManager!!)
                    this.iNotificationBuilder.createNotification(StepCounterService.STEP_COUNT_NOTIFICATION_REQ_CODE, "STEP_COUNT",
                        123,
                        com.project.neardoc.R.drawable.ic_walk_2x,
                        com.project.neardoc.R.drawable.heart, "Hi $name!",
                        getMessage(),
                        result
                     )
                } else {
                    EventBus.getDefault().postSticky(NotifySilentEvent(true, result))
                }
            }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onAppIsOnForeground() {
        this.isOnForeground = true
    }
    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun appIsOnBackground() {
        this.isOnForeground = false
    }
    private fun getMessage(): String {
        val calorieMessageGenerator = CalorieMessageGenerator()
        return calorieMessageGenerator.getStringBasedOnWeekDays()
    }
}