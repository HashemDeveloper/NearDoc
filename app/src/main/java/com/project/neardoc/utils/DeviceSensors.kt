package com.project.neardoc.utils

import android.content.Context
import android.hardware.*
import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import com.google.android.material.textview.MaterialTextView
import javax.inject.Inject

class DeviceSensors @Inject constructor(private val context: Context): IDeviceSensors {
    @Inject
    lateinit var iTempSensor: ITempSensor
    @Inject
    lateinit var iLightSensor: ILightSensor

    private val mSensorList: MutableList<Any> = arrayListOf()
    private var mSensorManager: SensorManager?= null

    override fun setupDeviceSensor(
        activity: FragmentActivity,
        fragmentAccountRoomTempViewId: MaterialTextView
    ) {
        for (sensorList in getListOfSensorsInDevice()) {
           when (sensorList.type) {
               Sensor.TYPE_AMBIENT_TEMPERATURE -> {
                   this.mSensorList.add(this.iTempSensor)
                   fragmentAccountRoomTempViewId.visibility = View.VISIBLE
                   this.iTempSensor.initiateTempSensor(this.mSensorManager!!)
                       .registerListener(this.mSensorManager!!)
                   this.iTempSensor.getSensorEvent().observe(activity, tempSensorEventObserver(fragmentAccountRoomTempViewId))
               }
               Sensor.TYPE_LIGHT -> {
                   this.mSensorList.add(this.iLightSensor)
                   this.iLightSensor.initiateLightSensor(this.mSensorManager!!)
                       .registerListener(this.mSensorManager!!)
                   this.iLightSensor.getSensorEventLiveData().observe(activity, lightSensorEventObserver())
               }
               Sensor.TYPE_STEP_COUNTER -> {

               }
           }
        }
    }

    private fun tempSensorEventObserver(view: MaterialTextView): Observer<SensorEvent> {
        return Observer {
            view.text = it.values[0].toString()
        }
    }
    private fun lightSensorEventObserver(): Observer<SensorEvent> {
        return Observer {

        }
    }
    private fun getListOfSensorsInDevice(): List<Sensor> {
        this.mSensorManager = this.context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        return mSensorManager?.getSensorList(Sensor.TYPE_ALL)!!
    }

    override fun clearObservers(view: MaterialTextView) {
        if (this.mSensorList.isNotEmpty()) {
            for (list in this.mSensorList) {
                if (list is ITempSensor) {
                    this.iTempSensor.getSensorEvent().removeObserver(tempSensorEventObserver(view))
                } else if (list is ILightSensor) {
                    this.iLightSensor.getSensorEventLiveData().removeObserver(lightSensorEventObserver())
                }
            }
        }
    }
}