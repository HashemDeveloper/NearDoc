package com.project.neardoc.utils.sensors

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build.VERSION_CODES
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.project.neardoc.BuildConfig
import com.project.neardoc.data.local.ISharedPrefService
import com.project.neardoc.events.StepCounterEvent
import com.project.neardoc.utils.Constants
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject
import kotlin.math.min

class StepCountSensor @Inject constructor(): IStepCountSensor, SensorEventListener {

    companion object {
        const val DEFAULT_LATENCY: Int = 0
        @JvmStatic val EVENT_QUEUE_LENGTH: Int = 10
        val TAG: String? = this::class.java.canonicalName
    }

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private var mStepSensor: Sensor?= null
    private val sensorEventLiveData: MutableLiveData<Int>?= MutableLiveData()
    private var mSteps: Int?= null
    private var mStepCounter: Int = 0
    private var mEventDelays: FloatArray = FloatArray(EVENT_QUEUE_LENGTH)
    private var mEventLength: Int = 0
    private var mEventData: Int = 0
    private var mDelayStringBuffer: StringBuffer = StringBuffer()
    @Inject
    lateinit var iSharedPrefService: ISharedPrefService

    @RequiresApi(VERSION_CODES.KITKAT)
    override fun initiateStepCounterSensor(
        sensorManager: SensorManager
    ) {
        this.mStepCounter = 0
        this.mStepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        val isRegistered: Boolean = sensorManager.registerListener(this, this.mStepSensor, SensorManager.SENSOR_DELAY_NORMAL,
            DEFAULT_LATENCY
        )
        if (isRegistered) {
            if (BuildConfig.DEBUG) {
                Log.i("StepCountRegisteredAt: $TAG", sensorManager.toString())
            }
        }
    }
    private fun register(sensorManager: SensorManager) {

    }

    override fun onAccuracyChanged(sensor: Sensor?, p1: Int) {

    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (BuildConfig.DEBUG) {
            Log.i("Event: ", "${event!!.values}")
        }
        this.compositeDisposable.add(calculateSteps(event)!!
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess { values ->
                EventBus.getDefault().postSticky(StepCounterEvent(values))
                this.sensorEventLiveData!!.value = values
            }
            .subscribeOn(Schedulers.newThread())
            .subscribe({values ->
                run {
                    this.iSharedPrefService.storeLastValueOfStepTaken(values)
                    this.iSharedPrefService.saveTotalStepCount(values)
                }
            }, { error ->
                run {
                    if (error.localizedMessage != null) {
                        Log.i("Error: ", error.localizedMessage!!)
                    }
                }
            }))
    }

    override fun getSensorEvent(): LiveData<Int> {
        return this.sensorEventLiveData!!
    }

    private fun calculateSteps(event: SensorEvent?): Single<Int>? {
        return Single.fromCallable {
            val sensor: Sensor = event?.sensor!!
            recordEvent(event)
            if (sensor.type == Sensor.TYPE_STEP_COUNTER) {
                if (this.mStepCounter < 1) {
                    this.mStepCounter = event.values[0].toInt()
                }
                this.mSteps = event.values[0].toInt() - this.mStepCounter // calculate steps taken
                this.mSteps = this.mSteps!! + this.iSharedPrefService.getLastStepCountValue() // add previous step taken
            }
            this.mSteps
        }
    }
    private fun recordEvent(event: SensorEvent) {
        // calculate the delay event was recorded until it was received
        this.mEventDelays[this.mEventData] = System.currentTimeMillis() - (event.timestamp / 1000000L).toFloat()
        // increment the event length
        this.mEventLength = min(EVENT_QUEUE_LENGTH, this.mEventLength)
        // move pointer to the next oldest location
        this.mEventData = (this.mEventData + 1) % EVENT_QUEUE_LENGTH
    }

    override fun clearDisposable() {
       this.compositeDisposable.clear()
    }

    override fun getDelayString(): String {
        this.mDelayStringBuffer.setLength(0)
        // Loop over all recorded delays and append them to the buffer as a decimal
        for (i: Int in 0 until mEventLength) {
            if (i > 0) {
                mDelayStringBuffer.append(", ")
            }
            val index: Int = (mEventData + i) % EVENT_QUEUE_LENGTH
            val delay: Float = mEventDelays[index] / 1000f // convert delay from ms into s
            mDelayStringBuffer.append(String.format("%1.1f", delay))
        }
        return mDelayStringBuffer.toString()
    }

    override fun flashStepCounter() {
        this.mSteps = 0
        this.mStepCounter = 0
        this.mEventLength = 0
        this.mEventData = 0
        this.mEventDelays = FloatArray(EVENT_QUEUE_LENGTH)
        this.iSharedPrefService.removeItems(Constants.STEP_COUNT_VALUE)
    }

    override fun unRegisterCounterSensor(sensorManager: SensorManager) {
        sensorManager.unregisterListener(this)
    }
}