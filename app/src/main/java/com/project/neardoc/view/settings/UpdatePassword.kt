package com.project.neardoc.view.settings


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

import com.project.neardoc.R
import com.project.neardoc.di.Injectable
import com.project.neardoc.di.viewmodel.ViewModelFactory
import com.project.neardoc.events.BottomBarEvent
import com.project.neardoc.events.LandInSettingPageEvent
import com.project.neardoc.events.NetworkStateEvent
import com.project.neardoc.events.UserStateEvent
import com.project.neardoc.utils.*
import com.project.neardoc.utils.validators.PasswordValidator
import com.project.neardoc.view.widgets.GlobalLoadingBar
import com.project.neardoc.viewmodel.UpdatePasswordViewModel
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_update_password.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

class UpdatePassword : Fragment(), Injectable, IUpdatePassSnackBarListener {

    @Inject
    lateinit var iNearDockMessageViewer: INearDockMessageViewer
    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val updatePasswordViewModel: UpdatePasswordViewModel by viewModels {
        this.viewModelFactory
    }
    private var currentPassValidator: PasswordValidator? = null
    private var newPassValidator: PasswordValidator? = null
    private var isInternetAvailable = false
    private var connectionSettings: ConnectionSettings? = null

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
        this.iNearDockMessageViewer.registerUpdatePassSnackBarListener(this)
        setupClickListeners()
        this.currentPassValidator =
            PasswordValidator(fragment_update_password_current_pass_input_layout_id)
        this.newPassValidator = PasswordValidator(fragment_update_password_new_pass_input_layout_id)
    }

    private fun setupClickListeners() {
        fragment_update_password_update_bt_id.setOnClickListener {
            if (this.isInternetAvailable) {
                processPasswordUpdate()
            } else {
                displayConnectionSetting()
            }
        }
    }

    private fun processPasswordUpdate() {
        val currentPassword: String =
            fragment_update_password_current_pass_edit_text_id.text.toString()
        val newPassword: String = fragment_update_password_new_pass_edit_text_id.text.toString()
        val isValidCurrentPass: Boolean =
            this.currentPassValidator?.getIsValidated(currentPassword)!!
        val isValidNewPass: Boolean = this.newPassValidator?.getIsValidated(newPassword)!!
        if (isValidCurrentPass && isValidNewPass) {
            this.updatePasswordViewModel.updatePassword(activity!!, currentPassword, newPassword)
            observeUpdateStatus()
        }
    }

    private fun observeUpdateStatus() {
        this.updatePasswordViewModel.getLoadingLiveData().observe(this, loadingObserver())
        this.updatePasswordViewModel.getStatusMessageLiveData().observe(this, statusObserver())
    }

    private fun statusObserver(): Observer<String> {
        return Observer { status ->
            if (status.isNotEmpty()) {
                if (status == "The password is invalid or the user does not have a password.") {
                    val snackbar: Snackbar = Snackbar.make(view!!, R.string.login_invalid_password, Snackbar.LENGTH_LONG)
                    this.iNearDockMessageViewer.displayMessage(snackbar, SnackbarType.INVALID_PASSWORD, true, "", true)
                } else if (status == this.context?.resources!!.getString(R.string.password_updated)) {
                    Toast.makeText(this.context, this.context?.resources!!.getString(R.string.password_updated), Toast.LENGTH_SHORT).show()
                } else if (status == "There is no user record corresponding to this identifier. The user may have been deleted.") {
                    val snackbar: Snackbar = Snackbar.make(view!!, R.string.sign_in_again, Snackbar.LENGTH_INDEFINITE)
                    this.iNearDockMessageViewer.displayMessage(snackbar, SnackbarType.SIGN_IN_AGAIN, true, "", true)
                }
            }
        }
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

    private fun displayLoading(isLoading: Boolean) {
        val globalLoadingBar = GlobalLoadingBar(activity!!, fragment_update_password_loading_bar_id)
        if (isLoading) {
            globalLoadingBar.setVisibility(true)
        } else {
            globalLoadingBar.setVisibility(false)
        }
    }

    private fun displayConnectionSetting() {
        this.connectionSettings = ConnectionSettings(activity!!, view!!)
        connectionSettings?.initWifiSetting(false)
        this.iNearDockMessageViewer.displayMessage(
            connectionSettings,
            SnackbarType.CONNECTION_SETTING,
            true,
            "",
            true
        )
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
        this.iNearDockMessageViewer.dismiss(
            this.connectionSettings,
            SnackbarType.CONNECTION_SETTING
        )
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

    override fun onDestroyView() {
        super.onDestroyView()
        this.updatePasswordViewModel.getLoadingLiveData().removeObserver(loadingObserver())
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        this.updatePasswordViewModel.getLoadingLiveData().reObserve(this, loadingObserver())
    }

    fun <T> LiveData<T>.reObserve(owner: LifecycleOwner, observer: Observer<T>) {
        removeObserver(observer)
        observe(owner, observer)
    }

    override fun signIn() {
        this.updatePasswordViewModel.signOut()
        val navigateToWelcome = findNavController()
        navigateToWelcome.navigate(R.id.welcome)
    }
}
