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
import kotlinx.coroutines.*
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class ConnectionStateMonitor @Inject constructor(private val context: Context) : LiveData<Boolean>(),
    IConnectionStateMonitor, CoroutineScope {

    @Inject
    lateinit var nearDocBroadcastReceiver: NearDocBroadcastReceiver
    private var networkCallback: ConnectivityManager.NetworkCallback?= null
    private var connectivityManager: ConnectivityManager?= null
    private val wifiConnectedLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private val usingMobileDataLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private val connectedNoInternetLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private val job = Job()
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
        launch {
            withContext(Dispatchers.IO) {
                when {
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> {
                        connectivityManager!!.registerDefaultNetworkCallback(networkCallback!!)
                    }
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP -> {
                        val networkRequest = NetworkRequest.Builder()
                            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                            .build()
                        connectivityManager?.registerNetworkCallback(networkRequest, networkCallback!!)
                    }
                    else -> {
                        context.registerReceiver(nearDocBroadcastReceiver, IntentFilter(Constants.CONNECTIVITY_ACTION))
                    }
                }
            }
        }
    }

    override fun onInactive() {
        super.onInactive()
        launch {
            withContext(Dispatchers.IO) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    connectivityManager?.unregisterNetworkCallback(networkCallback!!)
                } else {
                    context.unregisterReceiver(nearDocBroadcastReceiver)
                }
            }
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
        launch {
            withContext(Dispatchers.IO) {
                if (connectivityManager != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        var network: Network?= null
                        for (networks in connectivityManager!!.allNetworks) {
                            network = networks
                        }
                        if (connectivityManager!!.getNetworkCapabilities(network) != null) {
                            if (connectivityManager!!.getNetworkCapabilities(network)!!.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                                withContext(Dispatchers.Main) {
                                    wifiConnectedLiveData.postValue(true)
                                    usingMobileDataLiveData.postValue(false)
                                    Toast.makeText(context, R.string.using_wifi, Toast.LENGTH_SHORT).show()
                                }
                            } else if (connectivityManager!!.getNetworkCapabilities(network)!!.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)){
                                withContext(Dispatchers.Main) {
                                    usingMobileDataLiveData.postValue(true)
                                    wifiConnectedLiveData.postValue(false)
                                    Toast.makeText(context, R.string.using_mobile_data, Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            withContext(Dispatchers.Main) {
                                postValue(false)
                            }
                        }

                    } else {
                        // handle for lower api
                    }
                }
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
//            val handler = Handler()
////            handler.post {
////                this.connectionStateMonitor.updateConnection()
////            }
            this.connectionStateMonitor.updateConnection()
        }

        override fun onLost(network: Network) {
            connectionStateMonitor.postValue(false)
        }

        override fun onUnavailable() {
            connectionStateMonitor.postValue(false)
//            val handler = Handler()
//            handler.post {
//                this.connectionStateMonitor.updateConnection()
//            }
            this.connectionStateMonitor.updateConnection()
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

    override val coroutineContext: CoroutineContext
        get() = this.job + Dispatchers.Main
}