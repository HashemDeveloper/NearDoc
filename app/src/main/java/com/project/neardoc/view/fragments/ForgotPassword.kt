package com.project.neardoc.view.fragments


import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar

import com.project.neardoc.R
import com.project.neardoc.di.Injectable
import com.project.neardoc.di.viewmodel.ViewModelFactory
import com.project.neardoc.events.NetworkStateEvent
import com.project.neardoc.utils.ConnectionSettings
import com.project.neardoc.utils.Constants.Companion.mobileData
import com.project.neardoc.utils.Constants.Companion.wifiData
import com.project.neardoc.utils.validators.EmailValidator
import com.project.neardoc.view.widgets.GlobalLoadingBar
import com.project.neardoc.viewmodel.ForgotPasswordViewModel
import com.project.neardoc.viewmodel.listeners.IForgotPasswordViewModel
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_forgot_password.*
import kotlinx.android.synthetic.main.fragment_login.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

class ForgotPassword : Fragment(), Injectable, IForgotPasswordViewModel {
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
        this.forgotPassViewModel.registerListener(this)
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
            this.forgotPassViewModel.sendPasswordResetLink(activity!!, email)
        }
    }
    private fun displayConnectionSetting() {
        val connectionSettings = ConnectionSettings(activity!!, view!!)
        connectionSettings.initWifiSetting(false)
    }
    override fun onPasswordResetLinkProcessed() {
        this.forgotPassViewModel.getIsLoadingLiveData().observe(this, Observer {isLoading ->
            if (isLoading) {
                displayLoading(true)
            } else {
                displayLoading(false)
            }
        })
        this.forgotPassViewModel.getIsErrorLiveData().observe(this, Observer { isError ->
            if (isError) {
                val message = activity?.resources!!.getString(R.string.error_sending_password_reset_link) + ": " + activity?.resources!!.getString(R.string.email_not_found)
                val snackbar: Snackbar = Snackbar.make(view!!, message, Snackbar.LENGTH_LONG)
                snackbar.view.setBackgroundColor(ContextCompat.getColor(activity!!, R.color.blue_gray_800))
                snackbar.show()
            }
        })
        this.forgotPassViewModel.getIsSuccessLiveData().observe(this, Observer { isSuccess ->
            val snackbar: Snackbar = Snackbar.make(view!!, R.string.password_link_sent, Snackbar.LENGTH_INDEFINITE)
            snackbar.view.setBackgroundColor(ContextCompat.getColor(activity!!, R.color.blue_gray_800))
            snackbar.show()
            snackbar.setAction(R.string.email_inbox) {
                run {
                    openEmailInbox()
                }
            }
        })
    }
    private fun openEmailInbox() {
        val sendIntent = Intent(Intent.ACTION_SEND)
        sendIntent.addCategory(Intent.CATEGORY_APP_EMAIL)
        val title: String = activity?.resources!!.getString(R.string.choose_email_provider)
        val chooser = Intent.createChooser(sendIntent, title)
        val activities: List<ResolveInfo> = activity?.packageManager!!.queryIntentActivities(
            sendIntent,
            PackageManager.MATCH_DEFAULT_ONLY
        )
        val isIntentSafe: Boolean = activities.isNotEmpty()
        if (isIntentSafe) {
            startActivity(chooser)
        } else {
            Toast.makeText(activity, R.string.no_email_app_found, Toast.LENGTH_LONG).show()
        }
    }
    private fun displayLoading(isLoading: Boolean) {
        val globalLoadingBar = GlobalLoadingBar(activity!!, fragment_forgot_password_progress_bar_id)
        if (isLoading) {
            globalLoadingBar.setVisibility(true)
        } else {
            globalLoadingBar.setVisibility(false)
        }
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
