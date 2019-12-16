package com.project.neardoc.viewmodel

import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.util.Log
import androidx.lifecycle.ViewModel
import com.project.neardoc.BuildConfig
import com.project.neardoc.data.local.ISharedPrefService
import com.project.neardoc.data.local.remote.INearDocRemoteRepo
import com.project.neardoc.services.StepCounterService
import com.project.neardoc.utils.Constants
import com.project.neardoc.utils.service.IStepCountForegroundServiceManager
import kotlinx.coroutines.*
import java.io.IOException
import java.util.*
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
    @Inject
    lateinit var iStepCountForegroundServiceManager: IStepCountForegroundServiceManager
    private val job = Job()
    private var geoCoder: Geocoder?= null

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

    fun startStepCountService() {
        when (this.iSharedPrefService.getStepCountServiceType()) {
            Constants.SERVICE_FOREGROUND -> {
                if (BuildConfig.DEBUG) {
                    Log.i(TAG, "Running step count foreground service")
                }
                this.iStepCountForegroundServiceManager.startForegroundService()
            }
            Constants.SERVICE_BACKGROUND -> {
                if (BuildConfig.DEBUG) {
                    Log.i(TAG, "Running step count background service")
                }
                this.context.startService(Intent(this.context, StepCounterService::class.java))
            }
        }
    }

    fun unBindOnActivityStart() {
        this.iStepCountForegroundServiceManager.unBindOnActivityStart(checkIfStepCountForegroundAvailable())
    }

    fun bindOnActivityStop() {
        this.iStepCountForegroundServiceManager.bindOnActivityStop(checkIfStepCountForegroundAvailable())
    }

    fun stopBindService() {
        this.iStepCountForegroundServiceManager.stopBindService(checkIfStepCountForegroundAvailable())
    }
    private fun checkIfStepCountForegroundAvailable(): Boolean {
        return this.iSharedPrefService.getStepCountServiceType() == Constants.SERVICE_FOREGROUND
    }
    override fun onCleared() {
        super.onCleared()
    }
}