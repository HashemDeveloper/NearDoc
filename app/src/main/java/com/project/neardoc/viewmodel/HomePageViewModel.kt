package com.project.neardoc.viewmodel

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.work.*
import com.project.neardoc.BuildConfig
import com.project.neardoc.data.local.ISharedPrefService
import com.project.neardoc.data.local.remote.INearDocRemoteRepo
import com.project.neardoc.services.StepCounterService
import com.project.neardoc.utils.Constants
import com.project.neardoc.worker.LocationUpdateWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class HomePageViewModel @Inject constructor(): ViewModel(), CoroutineScope {
    companion object {
        private val MINIMUM_INTERVAL = 15 * 60 * 1000L
        private val FLEX_INTERVAL = 5 * 60 * 1000L
        @JvmStatic private val TAG: String = HomePageViewModel::class.java.canonicalName!!
    }
    @Inject
    lateinit var iNearDocRemoteRepo: INearDocRemoteRepo
    @Inject
    lateinit var iSharedPrefService: ISharedPrefService
    @Inject
    lateinit var context: Context
    private var workerLiveData: LiveData<WorkInfo>?= null
    private val job = Job()
    private var geoCoder: Geocoder?= null

    fun requestPermission(): Boolean {
        val foreGroundFineLocationState: Int = ContextCompat.checkSelfPermission(this.context, Manifest.permission.ACCESS_FINE_LOCATION)
        val foreGroundCoarseLocationState: Int = ContextCompat.checkSelfPermission(this.context, Manifest.permission.ACCESS_COARSE_LOCATION)
        var backgroundLocationState = 0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            backgroundLocationState = ContextCompat.checkSelfPermission(this.context, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        }
        return (foreGroundCoarseLocationState == PackageManager.PERMISSION_GRANTED) && (foreGroundFineLocationState == PackageManager.PERMISSION_GRANTED)
                && (backgroundLocationState == PackageManager.PERMISSION_GRANTED)
    }

    fun startLocationUpdate(activity: FragmentActivity, isStart: Boolean) {
        if (isStart) {
            val locationUpdateReq: PeriodicWorkRequest = PeriodicWorkRequest.Builder(LocationUpdateWorker::class.java, MINIMUM_INTERVAL, TimeUnit.MILLISECONDS,
                FLEX_INTERVAL, TimeUnit.MILLISECONDS)
                .build()
            val workManager: WorkManager = WorkManager.getInstance(this.context)
            workManager.enqueueUniquePeriodicWork("PeriodicLocationUpdate", ExistingPeriodicWorkPolicy.REPLACE, locationUpdateReq)
            this.workerLiveData = workManager.getWorkInfoByIdLiveData(locationUpdateReq.id)
            this.workerLiveData!!.observe(activity, locationUpdateObserver())
        }
    }
    private fun locationUpdateObserver(): Observer<WorkInfo> {
        return Observer {
            if (it?.state == null) {
                return@Observer
            } else {
                when (it.state) {
                    WorkInfo.State.SUCCEEDED -> {
                        val data: Data = it.outputData
                        val lat: String = data.getString(Constants.WORKER_LOCATION_LAT)!!
                        val lon: String = data.getString(Constants.WORKER_LOCATION_LON)!!
                        if (BuildConfig.DEBUG) {
                            Log.i("$TAG, LatLon:", "$lat, $lon")
                        }
                    }
                    else -> {
                        return@Observer
                    }
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        if (this.workerLiveData != null) {
            this.workerLiveData!!.removeObserver(locationUpdateObserver())
        }
    }

    override val coroutineContext: CoroutineContext
        get() = this.job + Dispatchers.Main

    fun storeUserCurrentState(latitude: Double, longitude: Double) {
        this.geoCoder = Geocoder(this.context, Locale.US)
        try {
            val addressList: MutableList<Address> = geoCoder?.getFromLocation(latitude, longitude, 10)!!
            for (address in addressList) {
                if (address.locality != null && address.locality.isNotEmpty()) {
                    val currentState: String = address.locality
                    iSharedPrefService.setUserCurrentState(currentState)
                }
            }
        } catch (ioException: IOException) {
            if (ioException.localizedMessage != null) {
                if (BuildConfig.DEBUG) {
                    Log.i("$TAG: IoException:", ioException.localizedMessage!!)
                }
            }
        }
    }

    fun startStepCountService(activity: FragmentActivity) {
        when (this.iSharedPrefService.getStepCountServiceType()) {
            Constants.SERVICE_FOREGROUND -> {

            }
            Constants.SERVICE_BACKGROUND -> {
                if (BuildConfig.DEBUG) {
                    Log.i(TAG, "Running step count foreground service")
                }
                activity.startService(Intent(this.context, StepCounterService::class.java))
            }
        }
    }
}