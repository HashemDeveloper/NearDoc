package com.project.neardoc

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.PopupMenu
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.project.neardoc.data.local.ISharedPrefService
import com.project.neardoc.rxeventbus.IRxEventBus
import com.project.neardoc.utils.*
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.near_by_main_layout.*
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject
import android.view.WindowManager
import androidx.lifecycle.LiveData
import androidx.navigation.findNavController
import androidx.work.*
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseUser
import com.project.neardoc.data.local.IUserInfoDao
import com.project.neardoc.events.*
import com.project.neardoc.model.localstoragemodels.UserPersonalInfo
import com.project.neardoc.services.StepCounterService
import com.project.neardoc.utils.calories.ICalorieBurnedCalculator
import com.project.neardoc.utils.networkconnections.IConnectionStateMonitor
import com.project.neardoc.utils.networkconnections.NearDocNetworkType
import com.project.neardoc.utils.notifications.INotificationScheduler
import com.project.neardoc.utils.notifications.IScheduleNotificationManager
import com.project.neardoc.utils.service.IStepCountForegroundServiceManager
import com.project.neardoc.utils.widgets.PageType
import com.project.neardoc.viewmodel.HomePageViewModel
import com.project.neardoc.worker.StepCountNotificationWorker
import kotlinx.coroutines.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.concurrent.TimeUnit
import kotlin.coroutines.CoroutineContext


class NearDocMainActivity : AppCompatActivity(), HasSupportFragmentInjector, SharedPreferences.OnSharedPreferenceChangeListener, CoroutineScope{
    companion object {
        @JvmStatic
        private val TAG: String = NearDocMainActivity::class.java.canonicalName!!
    }
    @Inject
    lateinit var iScheduleNotificationManager: IScheduleNotificationManager
    @Inject
    lateinit var iLocationService: ILocationService
    @Inject
    lateinit var iRxEventBus: IRxEventBus
    @Inject
    lateinit var iConnectionStateMonitor: IConnectionStateMonitor
    @Inject
    lateinit var iSharedPrefService: ISharedPrefService
    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>
    @Inject
    lateinit var iUserStateService: IUserStateService
    @Inject
    lateinit var iStepCountForegroundServiceManager: IStepCountForegroundServiceManager
    private lateinit var navController: NavController
    private var isWifiConnected = false
    private var view: View? = null
    private var isLoginInfoUpdated = false
    private val job = Job()

    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.near_by_main_layout)
        EventBus.getDefault().register(this)
        this.view = findViewById(R.id.container)
        this.navController = Navigation.findNavController(this, R.id.container)
        this.navController.setGraph(R.navigation.nearby_doc_navigation)
        main_layout_menu_bar_id.setOnClickListener{
            val navigateToSettingPage = findNavController(R.id.container)
            navigateToSettingPage.navigate(R.id.settingsFragment)
        }
        fragment_main_bottom_bar_back_bt_id.setOnClickListener {
            onBackPressed()
        }
    }

    @Deprecated("unused")
    private fun showPopupMenu(view: View) {
        val context = ContextThemeWrapper(this, R.style.PopUpMenuStyle)
        val popup = PopupMenu(context, view)
        val inflater = popup.menuInflater
        inflater.inflate(R.menu.bottom_nav_menu, popup.menu)

        val menuHelper: Any
        val argTypes: Array<Class<*>>
        val intTypes: Array<Class<*>>
        val viewTypes: Array<Class<*>>
        try {
            val fMenuHelper = PopupMenu::class.java.getDeclaredField("mPopup")
            fMenuHelper.isAccessible = true
            menuHelper = fMenuHelper.get(popup)!!
            argTypes = arrayOf(Boolean::class.javaPrimitiveType!!)
            intTypes = arrayOf(Int::class.javaPrimitiveType!!)
            viewTypes = arrayOf(View::class.javaObjectType)
            menuHelper.javaClass.getDeclaredMethod("setForceShowIcon", *argTypes)
                .invoke(menuHelper, true)
            menuHelper.javaClass.getDeclaredMethod("setGravity", *intTypes).invoke(menuHelper, Gravity.END)
            menuHelper.javaClass.getDeclaredMethod("setAnchorView", *viewTypes).invoke(menuHelper, view)
        } catch (e: Exception) {
            Log.i("PopupError: ", e.localizedMessage!!)
        }

        popup.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.menuSettingId -> {
                    Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show()
                }
                R.id.menuFeedbackId -> {
                    Toast.makeText(this, "Feedback", Toast.LENGTH_SHORT).show()
                }
                R.id.menuHelpId -> {
                    Toast.makeText(this, "Help", Toast.LENGTH_SHORT).show()
                }
                R.id.menuInfoId -> {
                    Toast.makeText(this, "About", Toast.LENGTH_SHORT).show()
                }
            }
            true
        }
        popup.setOnDismissListener {
            this.view!!.alpha = 1.0f
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        }
        popup.show()
        this.view!!.alpha = 0.3f
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return this.dispatchingAndroidInjector
    }

    override fun onStart() {
        super.onStart()
        this.iSharedPrefService.registerSharedPrefListener(this)
        monitorConnectionSetting()
        monitorUserState()
//        monitorLocationUpdate()
    }

    override fun onResume() {
        super.onResume()
        if (this.isLoginInfoUpdated) {
            this.navController.navigate(R.id.welcome)
        }
    }

    override fun onStop() {
        super.onStop()
        removeObservers()
    }
    private fun monitorLocationUpdate() {
        this.iLocationService.getObserver().observe(this, Observer {
            if (it != null) {
                Log.i("Location: ", "Location")
            }
        })
    }
    private fun monitorUserState() {
        this.iUserStateService.getAuthObserver().observe(this, Observer { currentUser ->
            if (currentUser.currentUser != null) {
                enableBottomBar(true)
                getUserIdToken(currentUser.currentUser!!)
                EventBus.getDefault().postSticky(UserStateEvent(true))
            } else {
                if (!this.isLoginInfoUpdated) {
                    enableBottomBar(false)
                    this.iSharedPrefService.removeItems(Constants.FIREBASE_ID_TOKEN)
                }
                EventBus.getDefault().postSticky(UserStateEvent(false))
            }
        })
    }
    private fun getUserIdToken(user: FirebaseUser) {
        user.getIdToken(true).addOnSuccessListener {
            val idToken: String = it.token!!
            val enCryptor = EnCryptor()
            val encryptedToken: ByteArray = enCryptor.encryptText(Constants.FIREBASE_ID_TOKEN, idToken)
            this.iSharedPrefService.storeIdToken(encryptedToken)
            this.iSharedPrefService.storeEncryptIV(enCryptor.iv!!)
        }.addOnFailureListener {
            Log.i("FailedGettingIdToken: ", it.localizedMessage!!)
        }
    }
    private fun enableBottomBar(isLoggedIn: Boolean) {
        if (isLoggedIn) {
            try {
                fragment_main_bottom_bar_id.visibility = View.VISIBLE
                val userImage: String = this.iSharedPrefService.getUserImage()
                val username: String = this.iSharedPrefService.getUserUsername()
                if (!isFinishing) {
                    if (userImage.isNotEmpty()) {
                        Glide.with(this).load(userImage).into(fragment_main_bottom_bar_profile_image_id)
                    } else {
                        val accountImageResId = getDrawableImage("ic_account_circle_white_24dp")
                        Glide.with(this).load(accountImageResId).into(fragment_main_bottom_bar_profile_image_id)
                    }
                    fragment_main_bottom_bar_username_view_id.text = username
                }
            } catch (ex: UninitializedPropertyAccessException) {
                if (BuildConfig.DEBUG) {
                    if (ex.localizedMessage != null) {
                        Log.i("Initialized: ", ex.localizedMessage!!)
                    }
                }
            }
        } else {
            fragment_main_bottom_bar_id.visibility = View.GONE
        }
    }
    private fun monitorConnectionSetting() {
        this.iConnectionStateMonitor.getObserver().observe(this, Observer {isNetAvailable ->
           if (isNetAvailable) {
               this.iConnectionStateMonitor.isConnectedNoInternetLiveData().observe(this, observeNotInternetConnectedLiveData())
               this.iConnectionStateMonitor.isUsingWifiLiveData().observe(this, observeUsingWifiLiveData())
               this.iConnectionStateMonitor.isUsingMobileData().observe(this, observeUsingMobileDataLiveData())
               if (isWifiConnected) {
                   EventBus.getDefault().postSticky(NetworkStateEvent(true, NearDocNetworkType.WIFI_DATA))
               } else {
                   EventBus.getDefault().postSticky(NetworkStateEvent(true, NearDocNetworkType.MOBILE_DATA))
               }
           } else {
               EventBus.getDefault().postSticky(NetworkStateEvent(false, NearDocNetworkType.NO_NETWORK))
               Toast.makeText(this, "Connection lost", Toast.LENGTH_SHORT).show()
           }
        })
    }
    private fun removeObservers() {
        this.iConnectionStateMonitor.isConnectedNoInternetLiveData().removeObserver(observeNotInternetConnectedLiveData())
        this.iConnectionStateMonitor.isUsingWifiLiveData().removeObserver(observeUsingWifiLiveData())
        this.iConnectionStateMonitor.isUsingMobileData().removeObserver(observeUsingMobileDataLiveData())
    }
    private fun observeUsingWifiLiveData(): Observer<Boolean> {
        return Observer {isWifi ->
            if (isWifi) {
                this.isWifiConnected = true
            }
        }
    }
    private fun observeUsingMobileDataLiveData(): Observer<Boolean> {
        return Observer {isMobileData ->
            if (isMobileData) {
                this.isWifiConnected = false
            }
        }
    }
    private fun observeNotInternetConnectedLiveData(): Observer<Boolean> {
        return Observer {
            if (BuildConfig.DEBUG) {
                Log.i("No Internet", it.toString())
            }
        }
    }
    private fun setSettingBar(
        isSetting: Boolean,
        currentPage: PageType
    ) {
        if (isSetting) {
            toggleBottomBar(currentPage)
        } else {
            toggleBottomBar(currentPage)
        }
    }
    private fun toggleBottomBar(currentPage: PageType) {
        when (currentPage) {
            PageType.SIGN_IN_SECURITY -> {
                setupSettingFragment(resources.getString(R.string.sign_in_security))
            }
            PageType.SETTINGS_FRAGMENT -> {
                setupSettingFragment(resources.getString(R.string.settings))
            }

            PageType.CONTACT_US -> {
                setupSettingFragment(resources.getString(R.string.contact_us))
            }
            PageType.SEND_FEEDBACK -> {
                setupSettingFragment(resources.getString(R.string.send_feedback))
            }
            PageType.TERMS_AND_CONDITION -> {
                setupSettingFragment(resources.getString(R.string.terms_condition))
            }
            PageType.PRIVACY_POLICY -> {
                setupSettingFragment(resources.getString(R.string.privacy_policy))
            }
            PageType.CHANGE_EMAIL -> {
                setupSettingFragment(getString(R.string.change_email))
            }
            PageType.CHANGE_PASSWORD -> {
                setupSettingFragment(getString(R.string.change_password))
            }
            PageType.MAIN_PAGE -> {
                mainPageBottomBar()
            }
        }
    }
    private fun setupSettingFragment(title: String) {
        fragment_main_bottom_bar_profile_image_id.visibility = View.GONE
        fragment_main_bottom_bar_username_view_id.visibility = View.GONE
        fragment_main_bottom_bar_title_view_id.visibility = View.VISIBLE
        fragment_main_bottom_bar_title_view_id.text = title
        fragment_main_bottom_bar_back_bt_id.visibility = View.VISIBLE
        main_layout_menu_bar_id.visibility = View.GONE
    }
    private fun mainPageBottomBar() {
        fragment_main_bottom_bar_profile_image_id.visibility = View.VISIBLE
        fragment_main_bottom_bar_username_view_id.visibility = View.VISIBLE
        fragment_main_bottom_bar_title_view_id.visibility = View.GONE
        fragment_main_bottom_bar_back_bt_id.visibility = View.GONE
        main_layout_menu_bar_id.visibility = View.VISIBLE
    }

    override fun onBackPressed() {
        super.onBackPressed()
        onSupportNavigateUp()
    }


    override fun onSupportNavigateUp(): Boolean {
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        this.iSharedPrefService.unregisterSharedPrefListener(this)
        EventBus.getDefault().unregister(this)
        removeObservers()
        this.iScheduleNotificationManager.onCleared()
    }

    override fun onPause() {
        super.onPause()
        removeObservers()
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onBottomBarEvent(event: BottomBarEvent) {
        if (!event.getIsBottomBarEnabled()) {
            enableBottomBar(false)
        } else {
            enableBottomBar(true)
        }
    }
    @Subscribe(threadMode = ThreadMode.ASYNC)
    fun onLoginInfoUpdatedEvent(event: LoginInfoUpdatedEvent) {
        if (event.getIsLoginInfoUpdated()) {
            this.isLoginInfoUpdated = true
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onLandInSettingPageEvent(event: LandInSettingPageEvent) {
        if (event.getIsOnSettingPage()) {
            when (event.getCurrentPage()) {
                PageType.SETTINGS_FRAGMENT -> setSettingBar(event.getIsOnSettingPage(), event.getCurrentPage())
                PageType.SIGN_IN_SECURITY -> setSettingBar(event.getIsOnSettingPage(), event.getCurrentPage())
                PageType.CONTACT_US -> setSettingBar(event.getIsOnSettingPage(), event.getCurrentPage())
                PageType.SEND_FEEDBACK -> setSettingBar(event.getIsOnSettingPage(), event.getCurrentPage())
                PageType.TERMS_AND_CONDITION -> setSettingBar(event.getIsOnSettingPage(), event.getCurrentPage())
                PageType.PRIVACY_POLICY -> setSettingBar(event.getIsOnSettingPage(), event.getCurrentPage())
                PageType.CHANGE_EMAIL -> setSettingBar(event.getIsOnSettingPage(), event.getCurrentPage())
                PageType.CHANGE_PASSWORD -> setSettingBar(event.getIsOnSettingPage(), event.getCurrentPage())
                PageType.MAIN_PAGE -> mainPageBottomBar()
            }
        } else {
            setSettingBar(false, event.getCurrentPage())
        }
    }

    override fun onSharedPreferenceChanged(pref: SharedPreferences?, key: String?) {
        runOnUiThread {
            when(key) {
                Constants.SHARED_PREF_USER_IMAGE -> {
                    val userImage: String = pref?.getString(key, "")!!
                    if (userImage.isNotEmpty()) {
                        Glide.with(this).load(userImage).into(fragment_main_bottom_bar_profile_image_id)
                    } else {
                        val drawableResourceId = getDrawableImage("ic_account_circle_white_24dp")
                        Glide.with(this).load(drawableResourceId).into(fragment_main_bottom_bar_profile_image_id)
                    }
                }
                Constants.SHARED_PREF_USER_USERNAME -> {
                    val username: String = pref?.getString(key, "")!!
                    fragment_main_bottom_bar_username_view_id.text = username
                }
                Constants.SHARED_PREF_IS_LOCATION_ENABLED -> {
                    val isEnabled: Boolean = pref?.getBoolean(key, false)!!
                    if (isEnabled) {
                        EventBus.getDefault().postSticky(LocationEnabledEvent(true))
                    } else {
                        EventBus.getDefault().postSticky(LocationEnabledEvent(false))
                    }
                }
                Constants.SERVICE_TYPE -> {
                    when (pref?.getString(key, "")!!) {
                        Constants.SERVICE_FOREGROUND -> {
                            if (BuildConfig.DEBUG) {
                                Log.i(TAG, "Running step count foreground service")
                            }
                            launch {
                                withContext(Dispatchers.IO) {
                                    iStepCountForegroundServiceManager.startForegroundService()
                                }
                            }.invokeOnCompletion {
                                if (it != null && it.localizedMessage != null) {
                                    if (BuildConfig.DEBUG) {
                                        Log.i(TAG, it.localizedMessage!!)
                                    }
                                } else {
                                    setupRegularNotification()
                                }
                            }
                        }
                        Constants.SERVICE_BACKGROUND -> {
                            if (BuildConfig.DEBUG) {
                                Log.i(TAG, "Running step count background service")
                            }
                            launch {
                                withContext(Dispatchers.IO) {
                                    startService(Intent(applicationContext, StepCounterService::class.java))
                                }
                            }.invokeOnCompletion {
                                if (it != null && it.localizedMessage != null) {
                                    if (BuildConfig.DEBUG) {
                                        Log.i(TAG, it.localizedMessage!!)
                                    }
                                } else {
                                    setupRegularNotification()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setupRegularNotification() {
        launch {
            iScheduleNotificationManager.scheduleRegularNotification()
        }
    }

    private fun getDrawableImage(image: String): Int {
        return resources.getIdentifier(image, "drawable", packageName)
    }

    override val coroutineContext: CoroutineContext
        get() = this.job + Dispatchers.IO
}
