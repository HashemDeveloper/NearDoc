package com.project.neardoc.view.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.*
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.project.neardoc.R
import com.project.neardoc.di.Injectable
import com.project.neardoc.di.viewmodel.ViewModelFactory
import com.project.neardoc.events.LocationUpdateEvent
import com.project.neardoc.events.NetworkStateEvent
import com.project.neardoc.utils.networkconnections.ConnectionSettings
import com.project.neardoc.utils.Constants
import com.project.neardoc.utils.ILocationService
import com.project.neardoc.utils.IPermissionListener
import com.project.neardoc.view.widgets.GlobalLoadingBar
import com.project.neardoc.viewmodel.HomePageViewModel
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_home_page.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

class HomePage: Fragment(), Injectable, IPermissionListener, LifecycleObserver {
    companion object {
        private const val LOCATION_UPDATE_REQUEST_CODE = 34
        private const val ACCESS_COARSE_AND_FINE_LOCATION_CODE = 1
    }
    private var connectionSettings: ConnectionSettings?= null
    private var isInternetAvailable = false
    @Inject
    lateinit var iLocationService: ILocationService
    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val homePageViewModel: HomePageViewModel by viewModels {
        this.viewModelFactory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onBackPressed()
        fragment_home_page_search_layout_id.setOnClickListener{
            val navToSearchPage = findNavController()
            navToSearchPage.navigate(R.id.searchPage)
        }
        fragment_home_page_account_layout_id.setOnClickListener{
            val navToAccountPage = findNavController()
            navToAccountPage.navigate(R.id.accountPage)
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
        if (!this.isInternetAvailable) {
            displayConnectionSetting()
        } else {
            if (this.connectionSettings != null && this.connectionSettings?.getSnackBar() != null) {
                this.connectionSettings?.getSnackBar()!!.dismiss()
            }
        }
    }
    private fun displayConnectionSetting() {
        this.connectionSettings =
            ConnectionSettings(
                activity!!,
                fragment_home_page_snackbar_id
            )
        connectionSettings?.initWifiSetting(false)
    }
    private fun displayLoading(isLoading: Boolean) {
        val globalLoadingBar = GlobalLoadingBar(activity!!, fragment_home_page_loading_bar_id)
        if (isLoading) {
            globalLoadingBar.setVisibility(true)
        } else {
            globalLoadingBar.setVisibility(false)
        }
    }
    override fun onStart() {
        super.onStart()
        this.iLocationService.setPermissionListener(this)
        observeLocationUpdates()
        EventBus.getDefault().register(this)
    }
    private fun observeLocationUpdates() {
        this.iLocationService.getObserver().observe(this, Observer {location ->
            if (location != null) {
                displayLoading(false)
                val lat: String = location.latitude.toString()
                val lon: String = location.longitude.toString()
                this.homePageViewModel.storeUserCurrentState(location.latitude, location.longitude)
                EventBus.getDefault().postSticky(LocationUpdateEvent(lat, lon))
            }
        })
    }


    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    override fun onDestroy() {
        super.onDestroy()
    }
    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onActivityCreate() {
        this.homePageViewModel.startStepCountService()
    }
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onActivityStart() {
        this.homePageViewModel.unBindOnActivityStart()
    }
    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onActivityStop() {
        this.homePageViewModel.bindOnActivityStop()
    }

    override fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ActivityCompat.checkSelfPermission(activity!!, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(activity!!, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                displayLoading(true)
                requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                    LOCATION_UPDATE_REQUEST_CODE
                )
            } else {
                this.iLocationService.registerBroadcastListener(true)
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(activity!!, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                displayLoading(true)
                requestPermissions( arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                    LOCATION_UPDATE_REQUEST_CODE
                )
            } else {
                this.iLocationService.registerBroadcastListener(true)
            }
        } else {
            if (ContextCompat.checkSelfPermission(activity!!, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(activity!!, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestLowApiPermission()
            } else {
                this.iLocationService.registerBroadcastListener(true)
            }
        }
    }
    private fun requestLowApiPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity!!, Manifest.permission.ACCESS_FINE_LOCATION) ||
            ActivityCompat.shouldShowRequestPermissionRationale(activity!!, Manifest.permission.ACCESS_COARSE_LOCATION)) {
            promptToEnableLocationPermission(resources.getString(R.string.location_permission_denied))
        } else {
            ActivityCompat.requestPermissions(activity!!, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                ACCESS_COARSE_AND_FINE_LOCATION_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == LOCATION_UPDATE_REQUEST_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                if (grantResults.isNotEmpty()) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        this.iLocationService.registerBroadcastListener(true)
                    } else {
                        promptToEnableLocationPermission(resources.getString(R.string.location_permission_denied))
                    }
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (grantResults.isNotEmpty()) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        this.iLocationService.registerBroadcastListener(true)
                    } else {
                        promptToEnableLocationPermission(resources.getString(R.string.location_permission_denied))
                    }
                }
            } else {
                if (grantResults.isNotEmpty()) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        this.iLocationService.registerBroadcastListener(true)
                    } else {
                        promptToEnableLocationPermission(resources.getString(R.string.location_permission_denied))
                    }
                }
            }
        }
    }
    private fun promptToEnableLocationPermission(message: String) {
        displayLoading(false)
        val snackbar: Snackbar = Snackbar.make(fragment_home_page_snackbar_id, message, Snackbar.LENGTH_INDEFINITE)
        snackbar.view.setBackgroundColor(ContextCompat.getColor(activity!!, R.color.blue_gray_800))
        snackbar.show()
        snackbar.setAction(R.string.enable_location_permission) {
            run {
                requestPermission()
            }
        }
    }
    private fun onBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().finish()
            }
        })
    }
}