package com.project.neardoc.utils.service

import android.app.ActivityManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.IBinder
import android.util.Log
import com.project.neardoc.BuildConfig
import com.project.neardoc.services.StepCountForegroundService
import com.project.neardoc.utils.Constants
import javax.inject.Inject

class StepCountForegroundServiceManager @Inject constructor(): IStepCountForegroundServiceManager {
    companion object {
        private val TAG: String = StepCountForegroundServiceManager::class.java.canonicalName!!
    }
    @Inject
    lateinit var context: Context
    private var stepCountForegroundService: StepCountForegroundService?= null
    private var mBound: Boolean = false
    private var isActivityStopped: Boolean = false

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
    override fun startForegroundService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.context.startForegroundService(Intent(this.context, StepCountForegroundService::class.java))
            bindService()
        } else {
            this.context.startService(Intent(this.context, StepCountForegroundService::class.java))
            bindService()
        }
    }

    override fun stopStepCountService() {
        val intent = Intent(this.context, StepCountForegroundService::class.java)
        intent.putExtra(Constants.SIGN_OUT, true)
        this.context.stopService(intent)
    }

    override fun unBindOnActivityStart(isForegroundServiceAvailable: Boolean) {
        if (isForegroundServiceAvailable) {
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
    }

    override fun bindOnActivityStop(isForegroundServiceAvailable: Boolean) {
        if (isForegroundServiceAvailable) {
            if (this.mBound) {
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
    }

    override fun stopBindService(isForegroundServiceAvailable: Boolean) {
        if (isForegroundServiceAvailable) {
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
    }

    private fun bindOperator() {
        this.context.bindService(Intent(this.context, StepCountForegroundService::class.java), this.serviceConnection, Context.BIND_AUTO_CREATE)
    }
    private fun unBindOperator() {
        this.context.unbindService(this.serviceConnection)
    }
    private fun isServiceRunning(): Boolean {
        val manager: ActivityManager =
            this.context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service: ActivityManager.RunningServiceInfo in manager.getRunningServices(
            Int.MAX_VALUE
        )) {
            if (StepCountForegroundService::class.java.name == service.service.className) {
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