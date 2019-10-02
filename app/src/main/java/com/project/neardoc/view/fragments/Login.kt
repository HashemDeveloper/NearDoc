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
import kotlinx.coroutines.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext
import android.app.Activity.RESULT_OK
import android.util.Log
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.project.neardoc.viewmodel.listeners.ILoginViewModel

class Login : Fragment(), Injectable, CoroutineScope, ILoginViewModel{

    companion object {
        @JvmStatic private val GOOGLE_SIGN_IN_CODE: Int = 0
    }
    private val mobileData = "MOBILE_DATA"
    private val wifiData = "WIFI_DATA"
    private val compositeDisposable = CompositeDisposable()
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

    private val job = Job()

    override val coroutineContext: CoroutineContext
        get() = this.job + Dispatchers.Main

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
                // perform login
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
                Toast.makeText(context, "Loading", Toast.LENGTH_SHORT).show()
            }
        })
        this.loginViewModel.getErrorLiveData().observe(this, Observer { isError ->
            if (isError) {
                //
            }
        })
        this.loginViewModel.getLoginSuccessLiveData().observe(this, Observer {isLoginSuccess ->
            if (isLoginSuccess) {
                val openHomePage = findNavController()
                openHomePage.navigate(R.id.homePage)
            }
        })
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        this.compositeDisposable.clear()
        this.loginViewModel.removeLoginViewModelListener(this)
    }
}
