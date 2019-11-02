package com.project.neardoc.view.settings


import android.content.Context
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController

import com.project.neardoc.R
import com.project.neardoc.di.Injectable
import com.project.neardoc.di.viewmodel.ViewModelFactory
import com.project.neardoc.events.LandInSettingPageEvent
import com.project.neardoc.events.LoginInfoUpdatedEvent
import com.project.neardoc.events.NetworkStateEvent
import com.project.neardoc.utils.ConnectionSettings
import com.project.neardoc.utils.Constants
import com.project.neardoc.utils.PageType
import com.project.neardoc.utils.validators.EmailValidator
import com.project.neardoc.utils.validators.EmptyFieldValidator
import com.project.neardoc.view.widgets.GlobalLoadingBar
import com.project.neardoc.viewmodel.UpdateEmailViewModel
import com.project.neardoc.viewmodel.listeners.UpdateEmailListener
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_update_email.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

class UpdateEmail : Fragment(), Injectable, UpdateEmailListener{

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val updateEmailViewModel: UpdateEmailViewModel by viewModels {
        this.viewModelFactory
    }
    private var emailValidator: EmailValidator?= null
    private var emptyFieldValidator: EmptyFieldValidator?= null
    private var currentEmail: String = ""
    private var isInternetAvailable = false
    private var connectionSettings: ConnectionSettings?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)
        EventBus.getDefault().postSticky(LandInSettingPageEvent(true, PageType.CHANGE_EMAIL))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_update_email, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val email: String = arguments!!.getString(Constants.WORKER_EMAIL, "")
        this.updateEmailViewModel.setUpdateEmailListener(this)
        this.currentEmail = email
        this.emailValidator = EmailValidator(fragment_update_email_input_layout_id)
        this.emptyFieldValidator = EmptyFieldValidator(fragment_update_email_current_password_input_layout, "")
        processClickListeners()
    }
    private fun processClickListeners() {
        fragment_update_email_update_bt_id.setOnClickListener {
            if (this.isInternetAvailable) {
                processUpdateEmail()
                hideKeyboard()
            } else {
                displayConnectionSetting()
                hideKeyboard()
            }
        }
    }
    private fun processUpdateEmail() {
        val newEmail: String = fragment_update_email_edit_text_id.text.toString()
        val password: String = fragment_update_email_current_password_edit_text_id.text.toString()
        val isPasswordValid: Boolean = this.emptyFieldValidator?.setEmptyMessage(resources.getString(R.string.passowrd_required))!!.getIsValidated(password)
        val isValidEmail: Boolean = this.emailValidator?.getIsValidated(newEmail)!!
        if (isValidEmail && isPasswordValid) {
            this.updateEmailViewModel.processUpdateEmailRequest(activity!!, this.currentEmail, newEmail, password)
        }
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

    private fun displayConnectionSetting() {
        this.connectionSettings = ConnectionSettings(activity!!, view!!)
        connectionSettings?.initWifiSetting(false)
        viewSnackbarOnTop(connectionSettings!!)
    }
    private fun viewSnackbarOnTop(connectionSettings: ConnectionSettings) {
        val view: View = connectionSettings.getSnackBar().view
        val params: FrameLayout.LayoutParams = view.layoutParams as FrameLayout.LayoutParams
        params.gravity = Gravity.TOP
        view.layoutParams = params
    }

    override fun onDestroy() {
        super.onDestroy()
        closeSnackbar()
        EventBus.getDefault().postSticky(LandInSettingPageEvent(false, PageType.SIGN_IN_SECURITY))
    }
    private fun closeSnackbar() {
        if (this.connectionSettings != null && this.connectionSettings?.getSnackBar() != null) {
            this.connectionSettings?.getSnackBar()!!.dismiss()
        }
    }
    override fun onProcess() {
        this.updateEmailViewModel.getLoadingLiveData().observe(this, Observer { isLoading ->
            if (isLoading) {
                displayLoading(true)
            } else {
                displayLoading(false)
            }
        })
    }

    override fun onSuccess() {
       this.updateEmailViewModel.getLoadingLiveData().observe(this, Observer { isLoading ->
           if (isLoading) {
               displayLoading(true)
           } else {
               displayLoading(false)
               Toast.makeText(context, getString(R.string.email_update_success), Toast.LENGTH_SHORT).show()
               EventBus.getDefault().post(LoginInfoUpdatedEvent(true))
           }
       })
    }

    override fun onFailed() {
        this.updateEmailViewModel.getLoadingLiveData().observe(this, Observer { isLoading ->
            if (!isLoading) {
                displayLoading(false)
            }
        })
    }
    fun hideKeyboard() {
        val imputMethodService: InputMethodManager = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imputMethodService.hideSoftInputFromWindow(view!!.windowToken, 0)
    }

    private fun displayLoading(isLoading: Boolean) {
        val globalLoadingBar = GlobalLoadingBar(activity!!, fragment_update_email_progress_bar_id)
        if (isLoading) {
            globalLoadingBar.setVisibility(true)
        } else {
            globalLoadingBar.setVisibility(false)
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
