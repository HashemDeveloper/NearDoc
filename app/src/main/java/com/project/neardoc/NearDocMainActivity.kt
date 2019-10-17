package com.project.neardoc

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.PopupMenu
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.project.neardoc.data.local.ISharedPrefService
import com.project.neardoc.events.NetworkStateEvent
import com.project.neardoc.events.UserStateEvent
import com.project.neardoc.rxeventbus.IRxEventBus
import com.project.neardoc.utils.*
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.near_by_main_layout.*
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject
import android.view.WindowManager


class NearDocMainActivity : AppCompatActivity(), HasSupportFragmentInjector{
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
        this.view = findViewById(R.id.container)
        this.navController = Navigation.findNavController(this, R.id.container)
        main_layout_menu_bar_id.setOnClickListener{
            showPopupMenu(it)
        }
    }

    private fun showPopupMenu(view: View) {
        val context = ContextThemeWrapper(this, R.style.PopUpMenuStyle)
        val popup = PopupMenu(context, view)
        val inflater = popup.menuInflater
        inflater.inflate(R.menu.bottom_nav_menu, popup.menu)

        val menuHelper: Any
        val argTypes: Array<Class<*>>
        try {
            val fMenuHelper = PopupMenu::class.java.getDeclaredField("mPopup")
            fMenuHelper.isAccessible = true
            menuHelper = fMenuHelper.get(popup)!!
            argTypes = arrayOf(Boolean::class.javaPrimitiveType!!)
            menuHelper.javaClass.getDeclaredMethod("setForceShowIcon", *argTypes)
                .invoke(menuHelper, true)
        } catch (e: Exception) {
            Log.i("PopupError: ", e.localizedMessage!!)
        }
        popup.show()
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
