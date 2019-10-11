package com.project.neardoc.view.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.iammert.library.ui.multisearchviewlib.MultiSearchView
import com.project.neardoc.R
import com.project.neardoc.di.Injectable
import com.project.neardoc.di.viewmodel.ViewModelFactory
import com.project.neardoc.events.LocationUpdateEvent
import com.project.neardoc.events.NetworkStateEvent
import com.project.neardoc.utils.ConnectionSettings
import com.project.neardoc.utils.Constants
import com.project.neardoc.viewmodel.HomepageViewModel
import com.project.neardoc.viewmodel.listeners.IHomepageViewModel
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_home_page.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

class HomePage: Fragment(), Injectable, IHomepageViewModel {

    private var isInternetAvailable = false
    private var latitude: String = ""
    private var longitude: String = ""
    private var betterDocApiKey = ""
    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val homePageViewModel: HomepageViewModel by viewModels {
        this.viewModelFactory
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        AndroidSupportInjection.inject(this)
        this.homePageViewModel.setListener(this)
        return inflater.inflate(R.layout.fragment_home_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.betterDocApiKey = resources.getString(R.string.better_doc_api_key)
        signoutBtId.setOnClickListener{
            FirebaseAuth.getInstance().signOut()
            val n = findNavController()
            n.navigate(R.id.welcome)
        }
        searchId.setSearchViewListener(object : MultiSearchView.MultiSearchViewListener{
            override fun onItemSelected(index: Int, s: CharSequence) {
                val message = "$s selected at index $index"
                Log.i("ItemSelected: ", message)
            }

            override fun onSearchComplete(index: Int, s: CharSequence) {
                val message = "$s completed at index $index"
                Log.i("OnSearchComplete: ", message)
            }

            override fun onSearchItemRemoved(index: Int) {
                val message = "Removed at index $index"
                Log.i("onSearchRemoved: ", message)
            }

            override fun onTextChanged(index: Int, s: CharSequence) {
                val message = "$s text changed at index $index"
                Log.i("onTextChanged: ", message)
            }
        })
    }
    @Subscribe(sticky = true, threadMode = ThreadMode.ASYNC)
    fun onLocationUpdate(locationUpdateEvent: LocationUpdateEvent) {
        this.latitude = locationUpdateEvent.getLatitude()
        this.longitude = locationUpdateEvent.getLongitude()
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
            this.homePageViewModel.checkBetterDocApiHealth(this.betterDocApiKey)
        } else {
            displayConnectionSetting()
        }
    }
    private fun displayConnectionSetting() {
        val connectionSettings = ConnectionSettings(activity!!, view!!)
        connectionSettings.initWifiSetting(false)
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }
    override fun fetchData() {
        this.homePageViewModel.getStatusOkLiveData().observe(this, Observer { isHealthy ->
            if (isHealthy) {
                this.homePageViewModel.fetchDocByDisease(this.betterDocApiKey, this.latitude, this.longitude, "arthritis")

            }
        })
    }
    override fun onServerError() {

    }
}