package com.project.neardoc.view.settings


import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout

import com.project.neardoc.R
import com.project.neardoc.di.Injectable
import com.project.neardoc.events.LandInSettingPageEvent
import com.project.neardoc.events.NetworkStateEvent
import com.project.neardoc.utils.*
import com.project.neardoc.utils.validators.PasswordValidator
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_update_password.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

class UpdatePassword : Fragment(), Injectable {
    @Inject
    lateinit var iNearDockMessageViewer: INearDockMessageViewer
    private var passwordValidator:PasswordValidator?= null
    private var isInternetAvailable = false
    private var connectionSettings:ConnectionSettings?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)
        EventBus.getDefault().postSticky(LandInSettingPageEvent(true, PageType.CHANGE_PASSWORD))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_update_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
    }
    private fun setupClickListeners() {
        fragment_update_password_update_bt_id.setOnClickListener {
            if (this.isInternetAvailable) {

            } else {
                displayConnectionSetting()
            }
        }
    }
    private fun displayConnectionSetting() {
        this.connectionSettings = ConnectionSettings(activity!!, view!!)
        connectionSettings?.initWifiSetting(false)
//        viewSnackbarOnTop(connectionSettings!!)
        this.iNearDockMessageViewer.snackbarOnTop(connectionSettings, SnackbarType.CONNECTION_SETTING, true, "", true)
    }
    private fun viewSnackbarOnTop(connectionSettings: ConnectionSettings) {
        val view: View = connectionSettings.getSnackBar().view
        val params: FrameLayout.LayoutParams = view.layoutParams as FrameLayout.LayoutParams
        params.gravity = Gravity.TOP
        view.layoutParams = params
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.ASYNC)
    fun onNetworkStateChangedEvent(networkStateEvent: NetworkStateEvent) {
        if (networkStateEvent.getIsNetworkAvailable()) {
            if (networkStateEvent.getNetworkType()!!.name == Constants.wifiData) {
                this.isInternetAvailable = true
            } else if (networkStateEvent.getNetworkType()!!.name == Constants.mobileData) {
                this.isInternetAvailable = true
            }
        } else {
            this.isInternetAvailable = false
        }
        if (this.isInternetAvailable) {
            closeSnackbar()
        }
    }
    private fun closeSnackbar() {
        this.iNearDockMessageViewer.dismiss(this.connectionSettings, SnackbarType.CONNECTION_SETTING)
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().postSticky(LandInSettingPageEvent(false, PageType.SIGN_IN_SECURITY))
    }
}
