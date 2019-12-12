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
        @JvmStatic private val TAG: String = HomePageViewModel::class.java.canonicalName!!
    }
    @Inject
    lateinit var iNearDocRemoteRepo: INearDocRemoteRepo
    @Inject
    lateinit var iSharedPrefService: ISharedPrefService
    @Inject
    lateinit var context: Context
    private val job = Job()
    private var geoCoder: Geocoder?= null

    override fun onCleared() {
        super.onCleared()
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
                if (BuildConfig.DEBUG) {
                    Log.i(TAG, "Running step count foreground service")
                }
            }
            Constants.SERVICE_BACKGROUND -> {
                if (BuildConfig.DEBUG) {
                    Log.i(TAG, "Running step count background service")
                }
                activity.startService(Intent(this.context, StepCounterService::class.java))
            }
        }
    }
}