package com.project.neardoc.view.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController

import com.project.neardoc.R
import com.project.neardoc.di.Injectable
import com.project.neardoc.di.viewmodel.ViewModelFactory
import com.project.neardoc.events.NetworkStateEvent
import com.project.neardoc.utils.ConnectionSettings
import com.project.neardoc.utils.INearDockMessageViewer
import com.project.neardoc.utils.SnackbarType
import com.project.neardoc.utils.validators.EmailValidator
import com.project.neardoc.utils.validators.EmptyFieldValidator
import com.project.neardoc.utils.validators.PasswordValidator
import com.project.neardoc.utils.validators.UsernameValidator
import com.project.neardoc.view.widgets.GlobalLoadingBar
import com.project.neardoc.viewmodel.RegistrationViewModel
import com.project.neardoc.viewmodel.listeners.IRegistrationViewModel
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_registration.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

class Registration : Fragment(), Injectable, IRegistrationViewModel{
    @Inject
    lateinit var iNearDockMessageViewer: INearDockMessageViewer
    private var connectionSettings: ConnectionSettings?= null
    private var isInternetAvailable: Boolean = false
    private val mobileData = "MOBILE_DATA"
    private val wifiData = "WIFI_DATA"
    private var emptyFieldValidator: EmptyFieldValidator? = null
    private var usernameValidator: UsernameValidator? = null
    private var emailValidator: EmailValidator? = null
    private var passwordValidator: PasswordValidator?= null
    private var isSent: Boolean = false
    private var webKey: String = ""
    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val registrationViewModel: RegistrationViewModel by viewModels {
        this.viewModelFactory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidSupportInjection.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        this.webKey = resources.getString(R.string.firebase_web_key)
        return inflater.inflate(R.layout.fragment_registration, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        performNavigationActions()
        this.registrationViewModel.setListener(this)
        registerInputValidators()
    }
    private fun registerInputValidators() {
        this.emptyFieldValidator = EmptyFieldValidator(fragment_register_full_name_input_layout_id, "")
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

        val isNameValidated: Boolean = this.emptyFieldValidator?.setEmptyMessage(resources.getString(R.string.name_required))!!.getIsValidated(fullName)
        val isUsernameValidated: Boolean = this.usernameValidator!!.getIsValidated(username)
        val isEmailValidated: Boolean = this.emailValidator!!.getIsValidated(email)
        val isPasswordValidated: Boolean = this.passwordValidator!!.getIsValidated(password)

        if (isNameValidated && isUsernameValidated && isEmailValidated && isPasswordValidated) {
            this.registrationViewModel.checkIfUsernameExists(username, webKey)
            this.registrationViewModel.getUsernameExistsLiveData().observe(this, Observer {isExists ->
                if (isExists) {
                    fragment_register_username_input_layout_id.error = activity?.resources!!.getString(R.string.username_exists)
                } else {
                    this.registrationViewModel.processRegistration(fullName, username, email, password, this.webKey)
                }
            })
            this.registrationViewModel.getLoadingLiveData().observe(this, Observer { isLoading ->
                if (isLoading) {
                    displayLoading(true)
                } else {
                    displayLoading(false)
                }
            })
            this.registrationViewModel.getSuccessLiveData().observe(this, Observer { idToken ->
                if (idToken != null) {
                    this.registrationViewModel.sendEmailVerificationLink(idToken)
                }
            })
            this.registrationViewModel.getErrorMessageLiveData().observe(this, Observer { errorMessage ->
                if (errorMessage != null) {
                    fragment_register_email_input_layout_id.error = activity?.resources!!.getString(R.string.email_exists)
                }
            })
        }
    }
    private fun displayLoading(isLoading: Boolean) {
        val globalLoadingBar = GlobalLoadingBar(activity!!, fragment_registration_progress_bar_id)
        if (isLoading) {
            globalLoadingBar.setVisibility(true)
        } else {
            globalLoadingBar.setVisibility(false)
        }
    }

    private fun displayConnectionSetting() {
        this.connectionSettings = ConnectionSettings(activity!!, view!!)
        connectionSettings?.initWifiSetting(false)
        this.iNearDockMessageViewer.displayMessage(connectionSettings, SnackbarType.CONNECTION_SETTING, true, "", true)
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
        if (this.isInternetAvailable) {
            this.iNearDockMessageViewer.dismiss(this.connectionSettings, SnackbarType.CONNECTION_SETTING)
        }
    }

    override fun onEmailVerificationSent(isSent: Boolean) {
        this.isSent = isSent
        val navigateToLogin = findNavController()
        navigateToLogin.navigate(R.id.login)
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    override fun onPause() {
        super.onPause()
        if (this.isSent) {
            this.registrationViewModel.notifyEmailSent()
        }
    }
}
