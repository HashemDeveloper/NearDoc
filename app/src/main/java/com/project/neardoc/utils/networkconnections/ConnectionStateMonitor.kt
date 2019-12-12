package com.project.neardoc.utils.networkconnections

import android.content.Context
import android.content.IntentFilter
import android.net.*
import android.os.Build
import android.os.Handler
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.project.neardoc.R
import com.project.neardoc.broadcast.NearDocBroadcastReceiver
import com.project.neardoc.utils.Constants
import javax.inject.Inject

class ConnectionStateMonitor @Inject constructor(private val context: Context) : LiveData<Boolean>(),
    IConnectionStateMonitor {

    @Inject
    lateinit var nearDocBroadcastReceiver: NearDocBroadcastReceiver
    private var networkCallback: ConnectivityManager.NetworkCallback?= null
    private var connectivityManager: ConnectivityManager?= null
    private val wifiConnectedLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private val usingMobileDataLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private val connectedNoInternetLiveData: MutableLiveData<Boolean> = MutableLiveData()


    init {
        this.connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.networkCallback =
                NearDocNetworkCallback(
                    this
                )
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
            context.registerReceiver(this.nearDocBroadcastReceiver, IntentFilter(Constants.CONNECTIVITY_ACTION))
        }
    }

    override fun onInactive() {
        super.onInactive()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.connectivityManager?.unregisterNetworkCallback(this.networkCallback!!)
        } else {
            context.unregisterReceiver(this.nearDocBroadcastReceiver)
        }
    }

    override fun isUsingWifiLiveData(): LiveData<Boolean> {
       return this.wifiConnectedLiveData
    }
    override fun isUsingMobileData(): LiveData<Boolean> {
        return this.usingMobileDataLiveData
    }

    override fun isConnectedNoInternetLiveData(): LiveData<Boolean> {
        return this.connectedNoInternetLiveData
    }
    override fun updateConnection() {
        if (connectivityManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                var network: Network?= null
                for (networks in this.connectivityManager!!.allNetworks) {
                    network = networks
                }
                if (this.connectivityManager!!.getNetworkCapabilities(network) != null) {
                    if (this.connectivityManager!!.getNetworkCapabilities(network)!!.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                        this.wifiConnectedLiveData.postValue(true)
                        this.usingMobileDataLiveData.postValue(false)
                        Toast.makeText(context, R.string.using_wifi, Toast.LENGTH_SHORT).show()
                    } else if (this.connectivityManager!!.getNetworkCapabilities(network)!!.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)){
                        this.usingMobileDataLiveData.postValue(true)
                        this.wifiConnectedLiveData.postValue(false)
                        Toast.makeText(context, R.string.using_mobile_data, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    postValue(false)
                }

            } else {
                // handle for lower api
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
            val handler = Handler()
            handler.post {
                this.connectionStateMonitor.updateConnection()
            }
        }

        override fun onLost(network: Network) {
            connectionStateMonitor.postValue(false)
        }

        override fun onUnavailable() {
            connectionStateMonitor.postValue(false)
            val handler = Handler()
            handler.post {
                this.connectionStateMonitor.updateConnection()
            }
        }

        override fun onCapabilitiesChanged(
            network: Network,
            networkCapabilities: NetworkCapabilities
        ) {
            if (networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)) {
                this.connectionStateMonitor.postValue(true)
            } else {
                this.connectionStateMonitor.postValue(false)
            }
        }
    }
}