package com.project.neardoc.view.settings


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

import com.project.neardoc.R
import com.project.neardoc.data.local.ISharedPrefService
import com.project.neardoc.di.Injectable
import com.project.neardoc.di.viewmodel.ViewModelFactory
import com.project.neardoc.events.LandInSettingPageEvent
import com.project.neardoc.events.NetworkStateEvent
import com.project.neardoc.model.ManageAccountHeader
import com.project.neardoc.model.ManageAccountModel
import com.project.neardoc.model.SignInSecurityHeaderModel
import com.project.neardoc.model.SignInSecurityModel
import com.project.neardoc.utils.*
import com.project.neardoc.utils.validators.PasswordValidator
import com.project.neardoc.view.adapters.SignInSecClickListener
import com.project.neardoc.view.adapters.SignInSecurityAdapter
import com.project.neardoc.viewmodel.SignInSecViewModel
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_sign_in_and_security.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

class SignInSecurity : Fragment(), Injectable, SignInSecClickListener {
    private var passwordValidator: PasswordValidator? = null
    private var signInSecurityAdapter: SignInSecurityAdapter? = null
    private var isInternetAvailable = false
    private var connectionSettings: ConnectionSettings?= null
    @Inject
    lateinit var iNearDockMessageViewer: INearDockMessageViewer
    @Inject
    lateinit var iSharedPrefService: ISharedPrefService
    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val signInSecViewModel: SignInSecViewModel by viewModels {
        this.viewModelFactory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)
        EventBus.getDefault().postSticky(LandInSettingPageEvent(true, PageType.SIGN_IN_SECURITY))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sign_in_and_security, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.signInSecurityAdapter = SignInSecurityAdapter(context!!, this)
        fragment_sign_in_security_recycler_view_id.layoutManager = LinearLayoutManager(context)
        fragment_sign_in_security_recycler_view_id.adapter = this.signInSecurityAdapter
        val fullName: String = this.iSharedPrefService.getUserName()
        val email: String = this.iSharedPrefService.getUserEmail()
        val headerModel = SignInSecurityHeaderModel("Login Information")
        val infoModel = SignInSecurityModel(fullName, email, "******")
        val manageHeader = ManageAccountHeader("Manage Account")
        val deleteAccount = ManageAccountModel(email)

        val dataList: List<Any> = arrayListOf(headerModel, infoModel, manageHeader, deleteAccount)
        this.signInSecurityAdapter?.setData(dataList)
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
        EventBus.getDefault().postSticky(LandInSettingPageEvent(false, PageType.SETTINGS_FRAGMENT))
    }

    override fun <T> onClick(items: T, model: String) {
        when (items) {
            is SignInSecurityModel -> {
                if (items.email == model) {
                    val updateEmailAction = findNavController()
                    val bundle = Bundle()
                    bundle.putString(Constants.WORKER_EMAIL, items.email)
                    updateEmailAction.navigate(R.id.updateEmail, bundle)
                } else {
                    val updatePasswordAction = findNavController()
                    updatePasswordAction.navigate(R.id.updatePassword)
                }
            }
            is ManageAccountModel -> {
                if (this.isInternetAvailable) {
                    val email: String = items.deleteEmail
                    deleteAccountDialog(email)
                } else {
                    displayConnectionSetting()
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
    private fun closeSnackbar() {
        this.iNearDockMessageViewer.dismiss(this.connectionSettings, SnackbarType.CONNECTION_SETTING)
    }
    private fun displayConnectionSetting() {
        this.connectionSettings = ConnectionSettings(activity!!, view!!)
        connectionSettings?.initWifiSetting(false)
        this.iNearDockMessageViewer.displayMessage(connectionSettings, SnackbarType.CONNECTION_SETTING, true, "", true)
    }

    private fun deleteAccountDialog(email: String) {
        val viewGroup: ViewGroup = activity!!.window.decorView.rootView as ViewGroup
        val dView: View =
            layoutInflater.inflate(R.layout.layout_delete_account_dialog, viewGroup, false)
        val passwordInputLayout: TextInputLayout =
            dView.findViewById(R.id.layout_delete_account_pass_input_layout_id)
        this.passwordValidator = PasswordValidator(passwordInputLayout)
        val passwordTextView: TextInputEditText =
            dView.run { findViewById(R.id.layout_delete_account_pass_edit_text_id) }
        val cancelBt: MaterialButton =
            dView.run { findViewById(R.id.layout_delete_account_cancel_bt_id) }
        val deleteBt: MaterialButton =
            dView.run { findViewById(R.id.layout_delete_account_delete_bt_id) }

        val accountInfoDialog = MaterialAlertDialogBuilder(this.context)
        val confirmDeleteDialog = MaterialAlertDialogBuilder(this.context)
        confirmDeleteDialog.setTitle(getString(R.string.delete_account))
        confirmDeleteDialog.setMessage(getString(R.string.confirm_delete_account))
        confirmDeleteDialog.setNegativeButton(android.R.string.cancel) { _, _ ->

        }
        confirmDeleteDialog.setPositiveButton(android.R.string.ok) { _, _ ->
            if (this.isInternetAvailable) {
                accountInfoDialog.setTitle(getString(R.string.enter_password))
                accountInfoDialog.setMessage(email)
                accountInfoDialog.setView(dView)
                val alertDialog = accountInfoDialog.create()
                alertDialog.show()
                cancelBt.setOnClickListener {
                    alertDialog.dismiss()
                }
                deleteBt.setOnClickListener {
                    if (this.isInternetAvailable) {
                        val password: String = passwordTextView.text.toString()
                        val isValidPass: Boolean = this.passwordValidator?.getIsValidated(password)!!
                        if (isValidPass) {
                            processDeleteAccount(email, password)
                            alertDialog.dismiss()
                        }
                    } else {
                        displayConnectionSetting()
                    }
                }
            } else {
                displayConnectionSetting()
            }
        }
        confirmDeleteDialog.show()
    }

    private fun processDeleteAccount(email: String, password: String) {
        this.signInSecViewModel.deleteAccount(email, password, activity!!)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (this.connectionSettings != null) {
            this.iNearDockMessageViewer.dismiss(this.connectionSettings, SnackbarType.CONNECTION_SETTING)
        }
    }
}
