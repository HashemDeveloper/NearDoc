package com.project.neardoc.utils

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import com.project.neardoc.broadcast.ConnectionBroadcastReceiver
import javax.inject.Inject

class LocationService @Inject constructor(private val context: Context): LiveData<Location>(), ILocationService, LocationListener {

    @Inject
    lateinit var connectionBroadcastReceiver: ConnectionBroadcastReceiver
    private var locationManager: LocationManager?= null
    private var iPermissionListener: IPermissionListener?= null
    private var isRegister = false

    init {
       this.locationManager = this.context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    override fun onActive() {
        super.onActive()
        if (!checkPermission()) {
            requestPermission()
        } else {
            this.locationManager?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0f, this)
            this.context.registerReceiver(this.connectionBroadcastReceiver, IntentFilter(Constants.LOCATION_SERVICE_ACTION))
            this.isRegister = true
        }
    }

    override fun onInactive() {
        super.onInactive()
        if (this.isRegister) {
            this.context.unregisterReceiver(this.connectionBroadcastReceiver)
        }
    }

    override fun getObserver(): LocationService {
       return this
    }
    override fun onLocationChanged(location: Location?) {
        postValue(location)
    }

    override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {

    }

    override fun onProviderEnabled(p0: String?) {

    }

    override fun onProviderDisabled(p0: String?) {

    }
    private fun checkPermission() : Boolean {
        val foreGroundFineLocationState: Int = ContextCompat.checkSelfPermission(this.context, Manifest.permission.ACCESS_FINE_LOCATION)
        val foreGroundCoarseLocationState: Int = ContextCompat.checkSelfPermission(this.context, Manifest.permission.ACCESS_COARSE_LOCATION)
        var backgroundLocationState = 0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            backgroundLocationState = ContextCompat.checkSelfPermission(this.context, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        }
        return (foreGroundCoarseLocationState == PackageManager.PERMISSION_GRANTED) && (foreGroundFineLocationState == PackageManager.PERMISSION_GRANTED)
                && (backgroundLocationState == PackageManager.PERMISSION_GRANTED)
    }
    private fun requestPermission() {
       this.iPermissionListener?.requestPermission()
    }
    override fun setPermissionListener(iPermissionListener: IPermissionListener) {
       this.iPermissionListener = iPermissionListener
    }
    @SuppressLint("MissingPermission")
    override fun registerBroadcastListener(b: Boolean) {
        if (b) {
            this.context.registerReceiver(this.connectionBroadcastReceiver, IntentFilter(Constants.LOCATION_SERVICE_ACTION))
            this.locationManager?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0f, this)
        }
    }
}