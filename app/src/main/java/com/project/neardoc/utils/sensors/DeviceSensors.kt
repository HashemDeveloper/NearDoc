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
import javax.inject.Inject

class DeviceSensors @Inject constructor(private val context: Context):
    IDeviceSensors {
    @Inject
    lateinit var iTempSensor: ITempSensor
    @Inject
    lateinit var iLightSensor: ILightSensor
    @Inject
    lateinit var iStepCountSensor: IStepCountSensor

    private val mSensorList: MutableList<Any> = arrayListOf()
    private var mSensorManager: SensorManager?= null

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
                   this.mSensorList.add(this.iTempSensor)
                   roomTempTextView.visibility = View.VISIBLE
                   this.iTempSensor.initiateTempSensor(this.mSensorManager!!)
                       .registerListener(this.mSensorManager!!)
                   this.iTempSensor.getSensorEvent().observe(activity, tempSensorEventObserver(roomTempTextView))
               }
               Sensor.TYPE_LIGHT -> {
                   this.mSensorList.add(this.iLightSensor)
                   this.iLightSensor.initiateLightSensor(this.mSensorManager!!)
                       .registerListener(this.mSensorManager!!)
                   this.iLightSensor.getSensorEventLiveData().observe(activity, lightSensorEventObserver())
               }
               Sensor.TYPE_STEP_COUNTER -> {
                   stepCountParentLayout.visibility = View.VISIBLE
//                   val stepCountServiceIntent = Intent(this.context, StepCounterService::class.java)
//                   activity.startService(stepCountServiceIntent)
               }
           }
        }
    }

    private fun stepCountSensorEventObserver(view: MaterialTextView): Observer<Int> {
        return Observer {
            view.text = it.toString()
        }
    }

    private fun tempSensorEventObserver(view: MaterialTextView): Observer<SensorEvent> {
        return Observer {
            view.text = it.values[0].toString()
        }
    }
    private fun lightSensorEventObserver(): Observer<SensorEvent> {
        return Observer {
            if (BuildConfig.DEBUG) {
                Log.i("LigtSensor: ", it.values[0].toString())
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
                    this.iLightSensor.getSensorEventLiveData().removeObserver(lightSensorEventObserver())
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
}