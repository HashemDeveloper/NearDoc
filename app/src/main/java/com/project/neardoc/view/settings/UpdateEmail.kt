package com.project.neardoc.view.settings


import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar

import com.project.neardoc.R
import com.project.neardoc.di.Injectable
import com.project.neardoc.di.viewmodel.ViewModelFactory
import com.project.neardoc.events.LandInSettingPageEvent
import com.project.neardoc.events.NetworkStateEvent
import com.project.neardoc.utils.*
import com.project.neardoc.utils.validators.EmailValidator
import com.project.neardoc.utils.validators.EmptyFieldValidator
import com.project.neardoc.view.widgets.GlobalLoadingBar
import com.project.neardoc.viewmodel.UpdateEmailViewModel
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_update_email.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

class UpdateEmail : Fragment(), Injectable, IUpdatePassSnackBarListener{

    @Inject
    lateinit var iNearDockMessageViewer: INearDockMessageViewer
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
        this.iNearDockMessageViewer.registerUpdatePassSnackBarListener(this)
        val email: String = arguments!!.getString(Constants.WORKER_EMAIL, "")
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
            observeStatus()
        }
    }
    private fun observeStatus() {
        this.updateEmailViewModel.getLoadingLiveData().observe(activity!!, loadingObserver())
        this.updateEmailViewModel.getStatusMessageLiveData().observe(activity!!, statusObserver())
    }
    private fun loadingObserver(): Observer<Boolean> {
        return Observer { isLoading ->
            if (isLoading) {
                displayLoading(true)
            } else {
                displayLoading(false)
            }
        }
    }
    private fun statusObserver(): Observer<String> {
        return Observer { status ->
            if (status.isNotEmpty()) {
                when (status) {
                    resources.getString(R.string.email_update_success) -> Toast.makeText(activity!!, status, Toast.LENGTH_SHORT).show()
                    "There is no user record corresponding to this identifier. The user may have been deleted." -> {
                        val snackBar: Snackbar = Snackbar.make(view!!, R.string.sign_in_again, Snackbar.LENGTH_INDEFINITE)
                        this.iNearDockMessageViewer.displayMessage(snackBar, SnackbarType.SIGN_IN_AGAIN, true, "", true)
                    }
                    "The password is invalid or the user does not have a password." -> {
                        val snackbar: Snackbar = Snackbar.make(view!!, R.string.login_invalid_password, Snackbar.LENGTH_LONG)
                        this.iNearDockMessageViewer.displayMessage(snackbar, SnackbarType.INVALID_PASSWORD, true, "", true)
                    }
                    "We have blocked all requests from this device due to unusual activity. Try again later. [ Too many unsuccessful login attempts.  Please include reCaptcha verification or try again later ]" -> {
                        val snackBar: Snackbar = Snackbar.make(view!!, R.string.too_many_login_attempt, Snackbar.LENGTH_LONG)
                        this.iNearDockMessageViewer.displayMessage(snackBar, SnackbarType.INVALID_PASSWORD, true, "", true)
                    }
                }
            }
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
        this.iNearDockMessageViewer.displayMessage(connectionSettings, SnackbarType.CONNECTION_SETTING, true, "", true)
    }

    override fun onDestroy() {
        super.onDestroy()
        closeSnackbar()
        this.iNearDockMessageViewer.unRegisterUpdatePassSnackBarListener(this)
        EventBus.getDefault().postSticky(LandInSettingPageEvent(false, PageType.SIGN_IN_SECURITY))
    }
    private fun closeSnackbar() {
       this.iNearDockMessageViewer.dismiss(this.connectionSettings, SnackbarType.CONNECTION_SETTING)
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
    fun <T> LiveData<T>.reObserve(owner: LifecycleOwner, observer: Observer<T>) {
        removeObserver(observer)
        observe(owner, observer)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
//        this.updateEmailViewModel.getStatusMessageLiveData().reObserve(activity!!,statusObserver())
//        this.updateEmailViewModel.getLoadingLiveData().reObserve(activity!!, loadingObserver())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        this.updateEmailViewModel.getStatusMessageLiveData().removeObserver(statusObserver())
        this.updateEmailViewModel.getLoadingLiveData().removeObserver(loadingObserver())
    }

    override fun signIn() {
        this.updateEmailViewModel.signOut()
        val navigateToWelcome = findNavController()
        navigateToWelcome.navigate(R.id.welcome)
    }
}
