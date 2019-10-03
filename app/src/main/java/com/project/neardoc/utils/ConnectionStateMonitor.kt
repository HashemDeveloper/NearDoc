package com.project.neardoc.utils

import android.content.Context
import android.content.IntentFilter
import android.net.*
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.project.neardoc.broadcast.ConnectionBroadcastReceiver
import javax.inject.Inject

class ConnectionStateMonitor @Inject constructor(private val context: Context) : LiveData<Boolean>(), IConnectionStateMonitor {

    @Inject
    lateinit var connectionBroadcastReceiver: ConnectionBroadcastReceiver
    private var networkCallback: ConnectivityManager.NetworkCallback?= null
    private var connectivityManager: ConnectivityManager?= null
    private val wifiConnectedLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private val usingMobileDataLiveData: MutableLiveData<Boolean> = MutableLiveData()


    init {
        this.connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.networkCallback = NearDocNetworkCallback(this)
        }
    }

    override fun onActive() {
        super.onActive()
        updateConnection()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            this.connectivityManager!!.registerDefaultNetworkCallback(this.networkCallback!!)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val networkRequest = NetworkRequest.Builder()
                .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .build()
            this.connectivityManager?.registerNetworkCallback(networkRequest, this.networkCallback!!)
        } else {
            context.registerReceiver(this.connectionBroadcastReceiver, IntentFilter(Constants.CONNECTIVITY_ACTION))
        }
    }

    override fun onInactive() {
        super.onInactive()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.connectivityManager?.unregisterNetworkCallback(this.networkCallback!!)
        } else {
            context.unregisterReceiver(this.connectionBroadcastReceiver)
        }
    }

    override fun isUsingWifiLiveData(): LiveData<Boolean> {
       return this.wifiConnectedLiveData
    }

    override fun isUsingMobileData(): LiveData<Boolean> {
        return this.usingMobileDataLiveData
    }

    override fun updateConnection() {
        if (connectivityManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                for (networks in this.connectivityManager!!.allNetworks) {
                    if (this.connectivityManager!!.getNetworkCapabilities(networks)!!.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                        this.wifiConnectedLiveData.postValue(true)
                        break
                    } else {
                        if (this.connectivityManager!!.getNetworkCapabilities(networks)!!.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                            this.usingMobileDataLiveData.postValue(true)
                        }
                    }
                }
            } else {
                //
            }
        }
    }
    override fun getObserver(): ConnectionStateMonitor {
        return this
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    class NearDocNetworkCallback(private var connectionStateMonitor: ConnectionStateMonitor) :
        ConnectivityManager.NetworkCallback() {

        override fun onAvailable(network: Network) {
            connectionStateMonitor.postValue(true)
        }

        override fun onLost(network: Network) {
            connectionStateMonitor.postValue(false)
        }

        override fun onUnavailable() {
            connectionStateMonitor.postValue(false)
        }

    }
}