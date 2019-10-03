package com.project.neardoc.view.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation

import com.project.neardoc.R
import com.project.neardoc.di.Injectable
import com.project.neardoc.events.NetworkStateEvent
import com.project.neardoc.utils.ConnectionSettings
import com.project.neardoc.utils.validators.EmailValidator
import com.project.neardoc.utils.validators.EmptyFieldValidator
import com.project.neardoc.utils.validators.PasswordValidator
import com.project.neardoc.utils.validators.UsernameValidator
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_registration.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class Registration : Fragment(), Injectable{
    private var isInternetAvailable: Boolean = false
    private val mobileData = "MOBILE_DATA"
    private val wifiData = "WIFI_DATA"
    private var emptyFieldValidator: EmptyFieldValidator? = null
    private var usernameValidator: UsernameValidator? = null
    private var emailValidator: EmailValidator? = null
    private var passwordValidator: PasswordValidator?= null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        AndroidSupportInjection.inject(this)
        return inflater.inflate(R.layout.fragment_registration, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        performNavigationActions()
        registerInputValidators()
    }
    private fun registerInputValidators() {
        this.emptyFieldValidator = EmptyFieldValidator(fragment_register_full_name_input_layout_id)
        this.usernameValidator = UsernameValidator(fragment_register_username_input_layout_id)
        this.emailValidator = EmailValidator(fragment_register_email_input_layout_id)
        this.passwordValidator = PasswordValidator(fragment_register_password_input_layout_id)

    }
    private fun performNavigationActions() {
        fragment_registration_create_account_bt_id.setOnClickListener {
            if (this.isInternetAvailable) {
               processRegistration()
            } else {
                displayConnectionSetting()
            }
        }
        fragment_register_login_bt_id.setOnClickListener {
            val navigateToLoginPage = RegistrationDirections.actionLoginPage()
            Navigation.findNavController(it).navigate(navigateToLoginPage)
        }
    }
    private fun processRegistration() {
        val fullName: String = fragment_register_full_name_edit_text_id.text.toString()
        val username: String = fragment_register_username_edit_text_id.text.toString()
        val email: String = fragment_register_email_edit_text_id.text.toString()
        val password: String = fragment_register_password_edit_text_id.text.toString()

        val isNameValidated: Boolean = this.emptyFieldValidator!!.getIsValidated(fullName)
        val isUsernameValidated: Boolean = this.usernameValidator!!.getIsValidated(username)
        val isEmailValidated: Boolean = this.emailValidator!!.getIsValidated(email)
        val isPasswordValidated: Boolean = this.passwordValidator!!.getIsValidated(password)

        if (isNameValidated && isUsernameValidated && isEmailValidated && isPasswordValidated) {
            Toast.makeText(activity, "Register", Toast.LENGTH_SHORT).show()
        }
    }

    private fun displayConnectionSetting() {
        val connectionSettings = ConnectionSettings(activity!!, view!!)
        connectionSettings.initWifiSetting(false)
    }
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onNetworkStateChangedEvent(networkStateEvent: NetworkStateEvent) {
        if (networkStateEvent.getIsNetworkAvailable()) {
            if (networkStateEvent.getNetworkType()!!.name == wifiData) {
                this.isInternetAvailable = true
            } else if (networkStateEvent.getNetworkType()!!.name == mobileData) {
                this.isInternetAvailable = true
                Toast.makeText(activity, R.string.using_mobile_data, Toast.LENGTH_SHORT).show()
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
