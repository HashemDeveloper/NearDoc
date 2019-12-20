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
import com.project.neardoc.BuildConfig
import com.project.neardoc.data.local.ISharedPrefService
import com.project.neardoc.events.NotifySilentEvent
import com.project.neardoc.events.UserStateEvent
import com.project.neardoc.services.StepCounterService
import com.project.neardoc.utils.*
import com.project.neardoc.utils.calories.CalorieMessageGenerator
import com.project.neardoc.utils.notifications.INotificationBuilder
import com.project.neardoc.utils.notifications.NotificationType
import com.project.neardoc.utils.sensors.IStepCountSensor
import dagger.android.AndroidInjection
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

class NearDocBroadcastReceiver @Inject constructor(): BroadcastReceiver(), LifecycleObserver {
    companion object {
        private val TAG: String = NearDocBroadcastReceiver::class.java.canonicalName!!
    }
    @Inject
    lateinit var iStepCounterSensor: IStepCountSensor
    @Inject
    lateinit var iNotificationBuilder: INotificationBuilder
    private var sensorManager: SensorManager? = null
    @Inject
    lateinit var iSharedPrefService: ISharedPrefService
    private var isOnForeground: Boolean?= false
    private var isUserLoggedIn: Boolean?= false

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
                val result: Double = intent.getDoubleExtra(Constants.CALORIES_BURNED_RESULT, 0.0)
                if (isNotificationOn && !this.isOnForeground!!) {
                    sensorManager = context!!.getSystemService(Context.SENSOR_SERVICE) as SensorManager
                    iStepCounterSensor.initiateStepCounterSensor(sensorManager!!)
                    this.iNotificationBuilder.createNotification(NotificationType.NOTIFICATION_REGULAR, StepCounterService.STEP_COUNT_NOTIFICATION_REQ_CODE, "STEP_COUNT",
                        com.project.neardoc.R.drawable.ic_walk_2x,
                        com.project.neardoc.R.drawable.heart, "Hi $name!",
                        getMessage(),
                        result.toInt()
                     )
                    if (BuildConfig.DEBUG) {
                        Log.i(TAG, "Burned ${result.toInt()} kcl")
                    }
                } else {
                    if (this.isUserLoggedIn!!) {
                        EventBus.getDefault().postSticky(NotifySilentEvent(true, result.toInt()))
                    }
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
    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
        EventBus.getDefault().register(this)
    }
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        EventBus.getDefault().unregister(this)
    }
    @Subscribe(sticky = true, threadMode = ThreadMode.ASYNC)
    fun onUserStateEvent(userStateEvent: UserStateEvent) {
       this.isUserLoggedIn = userStateEvent.isLoggedIn
    }
    private fun getMessage(): String {
        val calorieMessageGenerator =
            CalorieMessageGenerator()
        return calorieMessageGenerator.getStringBasedOnWeekDays()
    }
}