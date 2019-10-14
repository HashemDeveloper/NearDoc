package com.project.neardoc

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.project.neardoc.data.local.ISharedPrefService
import com.project.neardoc.events.LocationUpdateEvent
import com.project.neardoc.events.NetworkStateEvent
import com.project.neardoc.events.UserStateEvent
import com.project.neardoc.rxeventbus.IRxEventBus
import com.project.neardoc.utils.*
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.near_by_main_layout.*
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

class NearDocMainActivity : AppCompatActivity(), HasSupportFragmentInjector{
    private var firebaseUser: FirebaseUser?= null
    @Inject
    lateinit var iRxEventBus: IRxEventBus
    @Inject
    lateinit var iConnectionStateMonitor: IConnectionStateMonitor
    @Inject
    lateinit var iSharedPrefService: ISharedPrefService
    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>
    @Inject
    lateinit var iUserStateService: IUserStateService

    private lateinit var navController: NavController
    private var isWifiConnected = false
    private var view: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.near_by_main_layout)
        this.firebaseUser = FirebaseAuth.getInstance().currentUser
        this.view = findViewById(R.id.container)
        this.navController = Navigation.findNavController(this, R.id.container)
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return this.dispatchingAndroidInjector
    }

    override fun onStart() {
        super.onStart()
        monitorConnectionSetting()
        monitorUserState()
    }
    private fun monitorUserState() {
        this.iUserStateService.getObserver().observe(this, Observer {currentUser ->
            if (currentUser.currentUser != null) {
                enableBottomBar(true)
                EventBus.getDefault().postSticky(UserStateEvent(true))
            } else {
                enableBottomBar(false)
            }
        })
    }
    private fun enableBottomBar(isLoggedIn: Boolean) {
        if (isLoggedIn) {
            fragment_main_bottom_bar_id.visibility = View.VISIBLE
        } else {
            fragment_main_bottom_bar_id.visibility = View.GONE
        }
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
}
