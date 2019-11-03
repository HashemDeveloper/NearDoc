package com.project.neardoc.view.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.google.android.gms.auth.api.signin.GoogleSignInClient

import com.project.neardoc.R
import com.project.neardoc.data.local.ISharedPrefService
import com.project.neardoc.di.Injectable
import com.project.neardoc.di.viewmodel.ViewModelFactory
import com.project.neardoc.events.NetworkStateEvent
import com.project.neardoc.rxeventbus.IRxEventBus
import com.project.neardoc.utils.ConnectionSettings
import com.project.neardoc.viewmodel.LoginViewModel
import dagger.android.support.AndroidSupportInjection
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_login.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject
import android.app.Activity.RESULT_OK
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.util.Log
import android.view.Gravity
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.project.neardoc.events.EmailVerificationEvent
import com.project.neardoc.utils.Constants.Companion.mobileData
import com.project.neardoc.utils.Constants.Companion.wifiData
import com.project.neardoc.utils.INearDockMessageViewer
import com.project.neardoc.utils.ISnackBarListeners
import com.project.neardoc.utils.SnackbarType
import com.project.neardoc.utils.validators.EmailValidator
import com.project.neardoc.utils.validators.PasswordValidator
import com.project.neardoc.view.widgets.GlobalLoadingBar
import com.project.neardoc.viewmodel.listeners.ILoginViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class Login : Fragment(), Injectable, ILoginViewModel, ISnackBarListeners{

    companion object {
        @JvmStatic private val GOOGLE_SIGN_IN_CODE: Int = 0
        @JvmStatic private val MESSAGE_TYPE_LIST: MutableList<SnackbarType> = arrayListOf()
    }
    private var connectionSettings: ConnectionSettings?= null
    private var emailValidator: EmailValidator?= null
    private var passwordValidator: PasswordValidator?= null
    private var email: String = ""
    private val compositeDisposable = CompositeDisposable()
    @Inject
    lateinit var iNearDockMessageViewer: INearDockMessageViewer
    @Inject
    lateinit var googleSignInClient: GoogleSignInClient
    @Inject
    lateinit var iRxEventBus: IRxEventBus
    @Inject
    lateinit var iSharedPrefService: ISharedPrefService
    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val loginViewModel: LoginViewModel by viewModels {
        this.viewModelFactory
    }
    private var isInternetAvailable = false
    private var mSnackBar: Snackbar?= null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        AndroidSupportInjection.inject(this)
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val animation: Animation = AnimationUtils.loadAnimation(context, android.R.anim.fade_in)
        fragment_login_logo_text_view.startAnimation(animation)
        fragment_login_container_id.startAnimation(animation)
        fragment_login_create_new_account_bt_id.startAnimation(animation)
        performNavigateActions()
        onBackPressed()
        this.loginViewModel.setLoginViewModelListener(this)
        this.iNearDockMessageViewer.registerSnackBarListener(this)
        observerEmailVerificationEvent()
        registerInputValidators()
    }
    private fun registerInputValidators() {
        this.emailValidator = EmailValidator(fragment_login_enter_email_input_layout_id)
        this.passwordValidator = PasswordValidator(fragment_login_enter_password_input_layout_id)
    }
    private fun observerEmailVerificationEvent() {
        this.compositeDisposable.add(this.iRxEventBus.observable(EmailVerificationEvent::class.java)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {email ->
                val message: String =
                    activity?.resources!!.getString(R.string.verify_email) + " " + email.getEmail()
               emailInboxSnackBar(message)
            })
    }
    private fun emailInboxSnackBar(message: String) {
        this.mSnackBar = Snackbar.make(view!!, message, Snackbar.LENGTH_INDEFINITE)
        MESSAGE_TYPE_LIST.add(SnackbarType.OPEN_INBOX)
        this.iNearDockMessageViewer.displayMessage(this.mSnackBar, SnackbarType.OPEN_INBOX, true, message, true)
    }

    private fun onBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().finish()
            }
        })
    }
    private fun performNavigateActions() {
        fragment_login_forgot_pass_bt_id.setOnClickListener{
            val navigateToForgoPasswordPage = LoginDirections.actionForgotPassword()
            Navigation.findNavController(it).navigate(navigateToForgoPasswordPage)
        }
        fragment_login_create_new_account_bt_id.setOnClickListener{
            val navigateToRegistrationPage = LoginDirections.actionRegistration()
            Navigation.findNavController(it).navigate(navigateToRegistrationPage)
        }
        fragment_login_bt_id.setOnClickListener{
            if (this.isInternetAvailable) {
                processLogin()
            } else {
                displayConnectionSetting()
            }
        }
        fragment_login_google_loggin_bt_id.setOnClickListener{
            if (this.isInternetAvailable) {
                val intent: Intent = this.googleSignInClient.signInIntent
                startActivityForResult(intent, GOOGLE_SIGN_IN_CODE)
            } else {
               displayConnectionSetting()
            }
        }
    }
    private fun processLogin() {
        this.email = fragment_login_enter_email_edit_text_id.text.toString()
        val password: String = fragment_login_enter_password_edit_text_id.text.toString()
        val isValidEmail = this.emailValidator?.getIsValidated(email)
        val isValidPass = this.passwordValidator?.getIsValidated(password)
        if (isValidEmail!! && isValidPass!!) {
            this.loginViewModel.processLoginWithApp(activity, email, password)
        }
    }
    private fun displayConnectionSetting() {
        this.connectionSettings = ConnectionSettings(activity!!, view!!)
        connectionSettings?.initWifiSetting(false)
        this.iNearDockMessageViewer.displayMessage(connectionSettings, SnackbarType.CONNECTION_SETTING, true, "", true)
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
        if (this.isInternetAvailable) {
            this.iNearDockMessageViewer.dismiss(this.connectionSettings, SnackbarType.CONNECTION_SETTING)
        }
    }

    private fun openEmailInbox(snackbar: Snackbar) {
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_APP_EMAIL)
        val title: String = activity?.resources!!.getString(R.string.choose_email_provider)
        val chooser = Intent.createChooser(intent, title)
        val activities: List<ResolveInfo> = activity?.packageManager!!.queryIntentActivities(
            intent,
            PackageManager.MATCH_DEFAULT_ONLY
        )
        val isIntentSafe: Boolean = activities.isNotEmpty()
        if (isIntentSafe) {
            startActivity(chooser)
        } else {
            Toast.makeText(activity, R.string.no_email_app_found, Toast.LENGTH_LONG).show()
        }
        snackbar.dismiss()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                if (task.isSuccessful) {
                    val accountIfo: GoogleSignInAccount = task.result!!
                    this.loginViewModel.processLoginWithGoogle(activity, accountIfo)
                }
            } catch (apiEx: ApiException) {
                Log.i("ApiException:", apiEx.localizedMessage!!)
            }
        }
    }

    override fun onLoginActionPerformed() {
        this.loginViewModel.getLoadingLiveData().observe(this, Observer {isLoading ->
            if (isLoading) {
                displayLoading(true)
            } else {
               displayLoading(false)
            }
        })
        this.loginViewModel.getErrorLiveData().observe(this, Observer { isError ->
            if (isError) {
                //show error
            }
        })
        this.loginViewModel.getLoginSuccessLiveData().observe(this, Observer {isLoginSuccess ->
            if (isLoginSuccess) {
                val openHomePage = findNavController()
                openHomePage.navigate(R.id.homePage)
                this.iNearDockMessageViewer.batchDismiss(this.mSnackBar, MESSAGE_TYPE_LIST)
            }
        })
        this.loginViewModel.getIsEmailVerificationRequireLiveData().observe(this, Observer { isRequried ->
            val message: String = resources.getString(R.string.email_not_verified)
            displayEmailResendMessage(message, isRequried)
        })
        this.loginViewModel.getErrorMessageLiveData().observe(this, Observer {errorMessage ->
            if (errorMessage == "The password is invalid or the user does not have a password.") {
                val snackbar: Snackbar = Snackbar.make(view!!, R.string.login_invalid_password, Snackbar.LENGTH_LONG)
                this.iNearDockMessageViewer.displayMessage(snackbar, SnackbarType.INVALID_PASSWORD, true, "", false)
            } else if (errorMessage == "There is no user record corresponding to this identifier. The user may have been deleted.") {
                val snackbar: Snackbar = Snackbar.make(view!!, R.string.email_not_found, Snackbar.LENGTH_LONG)
                this.iNearDockMessageViewer.displayMessage(snackbar, SnackbarType.EMAIL_NOT_FOUND, true, "", false)
            }
        })
    }
    private fun displayEmailResendMessage(message: String, isRequired: Boolean) {
        this.mSnackBar = Snackbar.make(view!!, message, Snackbar.LENGTH_INDEFINITE)
        if (isRequired) {
            MESSAGE_TYPE_LIST.add(SnackbarType.RESEND_EMAIL)
            this.iNearDockMessageViewer.displayMessage(this.mSnackBar, SnackbarType.RESEND_EMAIL, true, message, true)
        }
    }

    override fun onEmailVerificationSent() {
        val message: String =
            activity?.resources!!.getString(R.string.verify_email) + " " + email
        emailInboxSnackBar(message)
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
        this.iNearDockMessageViewer.batchDismiss(this.mSnackBar, MESSAGE_TYPE_LIST)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        this.compositeDisposable.clear()
        this.loginViewModel.removeLoginViewModelListener(this)
        this.iNearDockMessageViewer.unRegisterSnackBarListener(this)
        MESSAGE_TYPE_LIST.clear()
    }
    private fun displayLoading(isLoading: Boolean) {
        val globalLoadingBar = GlobalLoadingBar(activity!!, fragment_login_progress_bar_id)
        if (isLoading) {
            globalLoadingBar.setVisibility(true)
        } else {
            globalLoadingBar.setVisibility(false)
        }
    }
    override fun onResendEmailVerification() {
       this.loginViewModel.resendEmailEmailVerification(activity)
    }

    override fun onOpenEmailInbox(snackbar: Snackbar) {
        this.mSnackBar = snackbar
        openEmailInbox(this.mSnackBar!!)
    }
}
