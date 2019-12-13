package com.project.neardoc.viewmodel

import android.app.ActivityManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.ViewModel
import com.project.neardoc.BuildConfig
import com.project.neardoc.data.local.ISharedPrefService
import com.project.neardoc.data.local.remote.INearDocRemoteRepo
import com.project.neardoc.services.StepCountForegroundService
import com.project.neardoc.services.StepCounterService
import com.project.neardoc.utils.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
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
    private val job = Job()
    private var geoCoder: Geocoder?= null
    private var stepCountForegroundService: StepCountForegroundService?= null
    private var mBound: Boolean = false
    private var isActivityStopped: Boolean = false

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

    fun startStepCountService() {
        when (this.iSharedPrefService.getStepCountServiceType()) {
            Constants.SERVICE_FOREGROUND -> {
                if (BuildConfig.DEBUG) {
                    Log.i(TAG, "Running step count foreground service")
                }
                startForegroundService()
            }
            Constants.SERVICE_BACKGROUND -> {
                if (BuildConfig.DEBUG) {
                    Log.i(TAG, "Running step count background service")
                }
                this.context.startService(Intent(this.context, StepCounterService::class.java))
            }
        }
    }
    private fun startForegroundService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.context.startForegroundService(Intent(this.context, StepCountForegroundService::class.java))
            bindService()
        } else {
            this.context.startService(Intent(this.context, StepCountForegroundService::class.java))
            bindService()
        }
    }
    private fun bindService() {
        try {
            if (!this.mBound) {
                bindOperator()
            }
        } catch (ex: Exception) {
            if (BuildConfig.DEBUG) {
                if (ex.localizedMessage != null) {
                    Log.i(TAG, ex.localizedMessage!!)
                }
            }
        }
    }
    private fun bindOperator() {
        this.context.bindService(Intent(this.context, StepCountForegroundService::class.java), this.serviceConnection, Context.BIND_AUTO_CREATE)
    }
    private fun unBindOperator() {
        this.context.unbindService(this.serviceConnection)
    }

    fun unBindService() {
        if (!this.mBound) {
            try {
                unBindOperator()
                this.mBound = false
                this.isActivityStopped = true
            } catch (ex: Exception) {
                if (BuildConfig.DEBUG) {
                    if (ex.localizedMessage != null) {
                        Log.i(TAG, ex.localizedMessage!!)
                    }
                }
                startForegroundService()
            }
        }
    }

    fun reBindService() {
        if (this.isActivityStopped && !this.mBound) {
            try {
                bindOperator()
            } catch (ex: Exception) {
                if (BuildConfig.DEBUG) {
                    if (ex.localizedMessage != null) {
                        Log.i(TAG, ex.localizedMessage!!)
                    }
                }
            }
        }
    }

    fun stopBindService() {
        if (isServiceRunning()) {
            try {
                unBindOperator()
            } catch (ex: Exception) {
                if (BuildConfig.DEBUG) {
                    if (ex.localizedMessage != null) {
                        Log.i(TAG, ex.localizedMessage!!)
                    }
                }
            }
        }
    }

    private fun isServiceRunning(): Boolean {
        val manager: ActivityManager =
            this.context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service: ActivityManager.RunningServiceInfo in manager.getRunningServices(
            Int.MAX_VALUE
        )) {
            if (StepCountForegroundService::class.java.getName() == service.service.className) {
                return true
            }
        }
        return false
    }

    private val serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(componentName: ComponentName, iBinder: IBinder) {
            val localBinder: StepCountForegroundService.LocalBinder = iBinder as StepCountForegroundService.LocalBinder
            stepCountForegroundService = localBinder.service
            mBound = true
        }

        override fun onServiceDisconnected(componentName: ComponentName) {
            stepCountForegroundService = null
            mBound = false
        }
    }
}