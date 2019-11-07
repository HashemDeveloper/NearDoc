package com.project.neardoc.worker

import android.content.Context
import android.location.Location
import android.os.Looper
import android.util.Log
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.android.gms.location.*
import com.project.neardoc.di.workermanager.NearDocWorkerInjection
import com.project.neardoc.utils.Constants
import java.lang.Exception
import java.util.concurrent.CountDownLatch
import javax.inject.Inject

class LocationUpdateWorker @Inject constructor(private val context: Context, workerParameters: WorkerParameters): Worker(context, workerParameters) {

    companion object {
        @JvmStatic private val UPDATE_INTERVAL = 2000L
        @JvmStatic private val FASTEST_UPDATE_INTERVAL = UPDATE_INTERVAL / 2
    }

    private var fusedLocationProviderClient: FusedLocationProviderClient?= null
    private val countDownLatch: CountDownLatch = CountDownLatch(1)
    private var locationCallback: LocationCallback?= null

    override fun doWork(): Result {
        NearDocWorkerInjection.inject(this)
        this.fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this.context)
        var lat: Double?= 0.0
        var lon: Double?= 0.0
        var outputData: Data?= null

        val locationRequest = LocationRequest()
        locationRequest.interval = UPDATE_INTERVAL
        locationRequest.fastestInterval = FASTEST_UPDATE_INTERVAL
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        try {
            this.locationCallback = object : LocationCallback() {
                override fun onLocationResult(result: LocationResult?) {
                    if (result == null) {
                        return
                    } else {
                        for (location in result.locations) {
                            if (location != null) {
                                lat = location.latitude
                                lon = location.longitude
                                countDownLatch.countDown()
                            }
                            if (fusedLocationProviderClient != null) {
                                fusedLocationProviderClient!!.removeLocationUpdates(locationCallback)
                            }
                        }
                    }
                }
            }

            this.fusedLocationProviderClient?.lastLocation!!.addOnSuccessListener {
                if (it != null) {
                    val location: Location = it
                    lat = location.latitude
                    lon = location.longitude
                    this.countDownLatch.countDown()
                }
            }
            try {
                this.fusedLocationProviderClient?.requestLocationUpdates(locationRequest, null)
            } catch (securityEx: SecurityException) {
                if (securityEx.localizedMessage != null) {
                    Log.i("SecurityEx: ", securityEx.localizedMessage!!)
                }
            }

            try {
                this.countDownLatch.await()
            } catch (interruptedEx: InterruptedException) {
                Log.i("InterruptedEx: ", interruptedEx.localizedMessage!!)
            }
            outputData = createOutPutData(lat!!, lon!!)
        } catch (ex: Exception) {
            if (ex.localizedMessage != null) {
                Log.i("LocationUpdateEx: ", ex.localizedMessage!!)
            }
        }
        return Result.success(outputData!!)
    }

    private fun createOutPutData(lat: Double, lon: Double): Data {
        val latitude: String = lat.toString()
        val longitude: String = lon.toString()
        return Data.Builder()
            .putString(Constants.WORKER_LOCATION_LAT, latitude)
            .putString(Constants.WORKER_LOCATION_LON, longitude)
            .build()
    }
}