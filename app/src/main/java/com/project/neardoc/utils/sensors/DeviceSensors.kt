package com.project.neardoc.utils.sensors

import android.content.Context
import android.hardware.*
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import com.google.android.material.textview.MaterialTextView
import com.project.neardoc.BuildConfig
import kotlinx.coroutines.*
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class DeviceSensors @Inject constructor(private val context: Context) :
    IDeviceSensors, CoroutineScope {
    companion object {
        private val TAG: String = DeviceSensors::class.java.canonicalName!!
    }

    @Inject
    lateinit var iTempSensor: ITempSensor
    @Inject
    lateinit var iLightSensor: ILightSensor
    @Inject
    lateinit var iStepCountSensor: IStepCountSensor

    private val mSensorList: MutableList<Any> = arrayListOf()
    private var mSensorManager: SensorManager? = null
    private val job = Job()
    private var isStepCountAvailAble: Boolean? = false

    override fun setupDeviceSensor(
        activity: FragmentActivity,
        roomTempTextView: MaterialTextView,
        stepCountParentLayout: FrameLayout
    ) {
        for (sensorList in getListOfSensorsInDevice()) {
            if (BuildConfig.DEBUG) {
                Log.i("Sensors: ", sensorList.name)
            }
            when (sensorList.type) {
                Sensor.TYPE_AMBIENT_TEMPERATURE -> {
                    launch {
                        withContext(Dispatchers.IO) {
                            mSensorList.add(iTempSensor)
                            roomTempTextView.visibility = View.VISIBLE
                            iTempSensor.initiateTempSensor(mSensorManager!!)
                                .registerListener(mSensorManager!!)
                        }
                    }.invokeOnCompletion {
                        if (it != null && it.localizedMessage != null) {
                            if (BuildConfig.DEBUG) {
                                Log.i(TAG + "TEMP Sensor", it.localizedMessage!!)
                            }
                        } else {
                            this.iTempSensor.getSensorEvent()
                                .observe(activity, tempSensorEventObserver(roomTempTextView))
                        }
                    }
                }
                Sensor.TYPE_LIGHT -> {
                    launch {
                        withContext(Dispatchers.IO) {
                            mSensorList.add(iLightSensor)
                            iLightSensor.initiateLightSensor(mSensorManager!!)
                                .registerListener(mSensorManager!!)
                        }
                    }.invokeOnCompletion {
                        if (it != null && it.localizedMessage != null) {
                            if (BuildConfig.DEBUG) {
                                Log.i(TAG + "LIGHT SENSOR", it.localizedMessage!!)
                            }
                        } else {
                            this.iLightSensor.getSensorEventLiveData()
                                .observe(activity, lightSensorEventObserver())
                        }
                    }
                }
                Sensor.TYPE_STEP_COUNTER -> {
                    this.isStepCountAvailAble = true
                    stepCountParentLayout.visibility = View.VISIBLE
                }
                Sensor.TYPE_STEP_DETECTOR -> {
                    if (!this.isStepCountAvailAble!!) {
                        this.isStepCountAvailAble = true
                        stepCountParentLayout.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun tempSensorEventObserver(view: MaterialTextView): Observer<SensorEvent> {
        return Observer {
            var sensorValue: String? = null
            launch {
                withContext(Dispatchers.IO) {
                    sensorValue = it.values[0].toString()
                }
            }.invokeOnCompletion {
                if (it != null && it.localizedMessage != null) {
                    if (BuildConfig.DEBUG) {
                        Log.i(TAG + "TEMP SENSOR: ", "Exception: ${it.localizedMessage!!}")
                    }
                } else {
                    sensorValue?.let {
                        view.text = it
                    }
                }
            }
        }
    }

    private fun lightSensorEventObserver(): Observer<SensorEvent> {
        return Observer {
            var values: String? = null
            if (BuildConfig.DEBUG) {
                launch {
                    withContext(Dispatchers.IO) {
                        values = it.values[0].toString()
                    }
                }.invokeOnCompletion {
                    if (it != null && it.localizedMessage != null) {
                        if (BuildConfig.DEBUG) {
                            Log.i(TAG, "Exception: ${it.localizedMessage!!}")
                        }
                    } else {
                        Log.i(TAG + "LigtSensor: ", values)
                    }
                }
            }
        }
    }

    private fun getListOfSensorsInDevice(): List<Sensor> {
        this.mSensorManager = this.context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        return mSensorManager?.getSensorList(Sensor.TYPE_ALL)!!
    }

    override fun clearObservers(view: MaterialTextView, stepCountView: MaterialTextView) {
        if (this.mSensorList.isNotEmpty()) {
            for (list in this.mSensorList) {
                if (list is ITempSensor) {
                    this.iTempSensor.getSensorEvent().removeObserver(tempSensorEventObserver(view))
                } else if (list is ILightSensor) {
                    this.iLightSensor.getSensorEventLiveData()
                        .removeObserver(lightSensorEventObserver())
                }
            }
            this.iStepCountSensor.clearDisposable()
        }
    }

    override fun unRegisterSensorListener() {
        this.iStepCountSensor.unRegisterCounterSensor(this.mSensorManager!!)
    }

    override fun flashStepCounter() {
        this.iStepCountSensor.flashStepCounter()
    }

    override val coroutineContext: CoroutineContext
        get() = this.job + Dispatchers.Main
}