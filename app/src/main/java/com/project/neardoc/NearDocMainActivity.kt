package com.project.neardoc

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.project.neardoc.data.local.ISharedPrefService
import com.project.neardoc.events.NetworkStateEvent
import com.project.neardoc.rxeventbus.IRxEventBus
import com.project.neardoc.utils.IConnectionStateMonitor
import com.project.neardoc.utils.NearDocNetworkType
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import io.reactivex.disposables.CompositeDisposable
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

class NearDocMainActivity : AppCompatActivity(), HasSupportFragmentInjector{
    private val compositeDisposable = CompositeDisposable()
    @Inject
    lateinit var iRxEventBus: IRxEventBus
    @Inject
    lateinit var iConnectionStateMonitor: IConnectionStateMonitor
    @Inject
    lateinit var iSharedPrefService: ISharedPrefService
    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>
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
        monitorConnectionSetting()
    }

    override fun onStop() {
        super.onStop()
    }

    private fun monitorConnectionSetting() {
        this.iConnectionStateMonitor.getObserver().observe(this, Observer {isNetAvailable ->
           if (isNetAvailable) {
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
                   EventBus.getDefault().post(NetworkStateEvent(true, NearDocNetworkType.WIFI_DATA))
               } else {
                   EventBus.getDefault().post(NetworkStateEvent(true, NearDocNetworkType.MOBILE_DATA))
               }
           } else {
               EventBus.getDefault().post(NetworkStateEvent(false, NearDocNetworkType.NO_NETWORK))
               Toast.makeText(this, "Connection lost", Toast.LENGTH_SHORT).show()
           }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        this.compositeDisposable.clear()
    }
}
