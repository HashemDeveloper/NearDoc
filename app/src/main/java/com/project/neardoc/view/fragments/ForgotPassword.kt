package com.project.neardoc.view.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController

import com.project.neardoc.R
import com.project.neardoc.di.Injectable
import com.project.neardoc.di.viewmodel.ViewModelFactory
import com.project.neardoc.events.NetworkStateEvent
import com.project.neardoc.utils.ConnectionSettings
import com.project.neardoc.utils.Constants.Companion.mobileData
import com.project.neardoc.utils.Constants.Companion.wifiData
import com.project.neardoc.utils.validators.EmailValidator
import com.project.neardoc.viewmodel.ForgotPasswordViewModel
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_forgot_password.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

class ForgotPassword : Fragment(), Injectable {
    private var isInternetAvailable = false
    private var emailValidator: EmailValidator?= null
    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val forgotPassViewModel: ForgotPasswordViewModel by viewModels {
        this.viewModelFactory
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        AndroidSupportInjection.inject(this)
        return inflater.inflate(R.layout.fragment_forgot_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        attachValidator()
        performActions()
    }
    private fun attachValidator() {
        this.emailValidator = EmailValidator(fragment_forgot_password_email_input_layout_id)
    }
    private fun performActions() {
        fragment_forgot_password_go_back_bt_id.setOnClickListener{
            val navigateToLoginScreen = findNavController()
            navigateToLoginScreen.navigate(R.id.login)
        }
        fragment_forgot_password_reset_bt_id.setOnClickListener {
            if (this.isInternetAvailable) {
                processPasswordReset()
            } else {
                displayConnectionSetting()
            }
        }
    }
    private fun processPasswordReset() {
        val email: String = fragment_forg_password_email_edit_text_id.text.toString()
        val isValidEmail = this.emailValidator?.getIsValidated(email)
        if (isValidEmail!!) {
            Toast.makeText(activity, "Valid", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(activity, "Not", Toast.LENGTH_SHORT).show()
        }
    }
    private fun displayConnectionSetting() {
        val connectionSettings = ConnectionSettings(activity!!, view!!)
        connectionSettings.initWifiSetting(false)
    }
    @Subscribe(sticky = true, threadMode = ThreadMode.ASYNC)
    fun onNetworkStateChangedEvent(networkStateEvent: NetworkStateEvent) {
        if (networkStateEvent.getIsNetworkAvailable()) {
            if (networkStateEvent.getNetworkType()!!.name == wifiData) {
                this.isInternetAvailable = true
            } else if (networkStateEvent.getNetworkType()!!.name == mobileData) {
                this.isInternetAvailable = true
            }
        } else {
            this.isInternetAvailable = false
        }
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }
}
