package com.project.neardoc

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.project.neardoc.data.local.ISharedPrefService
import com.project.neardoc.events.NetworkStateEvent
import com.project.neardoc.rxeventbus.IRxEventBus
import com.project.neardoc.utils.IConnectionStateMonitor
import com.project.neardoc.utils.ILocationService
import com.project.neardoc.utils.IPermissionListener
import com.project.neardoc.utils.NearDocNetworkType
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import io.reactivex.disposables.CompositeDisposable
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

class NearDocMainActivity : AppCompatActivity(), HasSupportFragmentInjector, IPermissionListener{
    companion object {
        private val LOCATION_UPDATE_REQUEST_CODE = 34
        private val ACCESS_COARSE_AND_FINE_LOCATION_CODE = 1
    }
    private val compositeDisposable = CompositeDisposable()
    @Inject
    lateinit var iRxEventBus: IRxEventBus
    @Inject
    lateinit var iConnectionStateMonitor: IConnectionStateMonitor
    @Inject
    lateinit var iSharedPrefService: ISharedPrefService
    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>
    @Inject
    lateinit var iLocationService: ILocationService
    private lateinit var navController: NavController
    private var isWifiConnected = false
    private var view: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.near_by_main_layout)
        this.view = findViewById(R.id.container)
        this.navController = Navigation.findNavController(this, R.id.container)
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return this.dispatchingAndroidInjector
    }

    override fun onStart() {
        super.onStart()
        this.iLocationService.setPermissionListener(this)
        monitorConnectionSetting()
        observeNetworkUpdates()
    }

    override fun onStop() {
        super.onStop()
    }
    private fun observeNetworkUpdates() {
        this.iLocationService.getObserver().observe(this, Observer {location ->
            if (location != null) {
                val lat: String = location.latitude.toString()
                val lon: String = location.longitude.toString()
                Log.i("Latitude: ", lat)
                Log.i("Longitude: ", lon)
            }
        })
    }
    private fun monitorConnectionSetting() {
        this.iConnectionStateMonitor.getObserver().observe(this, Observer {isNetAvailable ->
           if (isNetAvailable) {
               this.iConnectionStateMonitor.isConnectedNoInternetLiveData().observe(this, Observer { noInternet ->

               })
               this.iConnectionStateMonitor.isUsingWifiLiveData().observe(this, Observer {isWifi ->
                   if (isWifi) {
                       this.isWifiConnected = true
                   }
               })
               this.iConnectionStateMonitor.isUsingMobileData().observe(this, Observer {isMobileData ->
                   if (isMobileData) {
                       this.isWifiConnected = false
                   }
               })
               if (isWifiConnected) {
                   EventBus.getDefault().postSticky(NetworkStateEvent(true, NearDocNetworkType.WIFI_DATA))
               } else {
                   EventBus.getDefault().postSticky(NetworkStateEvent(true, NearDocNetworkType.MOBILE_DATA))
               }
           } else {
               EventBus.getDefault().postSticky(NetworkStateEvent(false, NearDocNetworkType.NO_NETWORK))
               Toast.makeText(this, "Connection lost", Toast.LENGTH_SHORT).show()
           }
        })
    }

    override fun requestPermission() {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
          if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
              && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
              ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION), LOCATION_UPDATE_REQUEST_CODE)
          } else {
              this.iLocationService.registerBroadcastListener(true)
          }
      } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
          if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
              ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), LOCATION_UPDATE_REQUEST_CODE)
          } else {
              this.iLocationService.registerBroadcastListener(true)
          }
      } else {
          if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
              && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
              requestLowApiPermission()
          } else {
              this.iLocationService.registerBroadcastListener(true)
          }
      }
    }
    private fun requestLowApiPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION) ||
                ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
            emailInboxSnackBar(resources.getString(R.string.location_permission_denied))
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), ACCESS_COARSE_AND_FINE_LOCATION_CODE)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        this.compositeDisposable.clear()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
       if (requestCode == LOCATION_UPDATE_REQUEST_CODE) {
           if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
               if (grantResults.isNotEmpty()) {
                   if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                       this.iLocationService.registerBroadcastListener(true)
                   } else {
                      emailInboxSnackBar(resources.getString(R.string.location_permission_denied))
                   }
               }
           } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
               if (grantResults.isNotEmpty()) {
                   if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                       this.iLocationService.registerBroadcastListener(true)
                   } else {
                       emailInboxSnackBar(resources.getString(R.string.location_permission_denied))
                   }
               }
           } else {
               if (grantResults.isNotEmpty()) {
                   if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                       this.iLocationService.registerBroadcastListener(true)
                   } else {
                       emailInboxSnackBar(resources.getString(R.string.location_permission_denied))
                   }
               }
           }
       }
    }
    private fun emailInboxSnackBar(message: String) {
        val snackbar: Snackbar = Snackbar.make(view!!, message, Snackbar.LENGTH_INDEFINITE)
        snackbar.view.setBackgroundColor(ContextCompat.getColor(this, R.color.blue_gray_800))
        snackbar.show()
        snackbar.setAction(R.string.enable_location_permission) {
            run {
                requestPermission()
            }
        }
    }
}
