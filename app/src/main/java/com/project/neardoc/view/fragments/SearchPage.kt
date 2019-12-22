package com.project.neardoc.view.fragments


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.project.neardoc.BuildConfig

import com.project.neardoc.R
import com.project.neardoc.di.Injectable
import com.project.neardoc.di.viewmodel.ViewModelFactory
import com.project.neardoc.events.LocationUpdateEvent
import com.project.neardoc.events.NetworkStateEvent
import com.project.neardoc.model.BetterDocApiHealthRes
import com.project.neardoc.model.BetterDocSearchByDiseaseRes
import com.project.neardoc.utils.networkconnections.ConnectionSettings
import com.project.neardoc.utils.Constants
import com.project.neardoc.utils.livedata.ResultHandler
import com.project.neardoc.viewmodel.SearchPageViewModel
import com.project.neardoc.viewmodel.listeners.ISearchPageViewModel
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_search_page.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

class SearchPage : Fragment(), Injectable{
    companion object {
        @JvmStatic private val TAG: String = SearchPage::class.java.canonicalName!!
    }
    private var connectionSettings: ConnectionSettings?= null
    private var isInternetAvailable = false
    private var latitude: String = ""
    private var longitude: String = ""
    private var betterDocApiKey = ""
    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val homePageViewModel: SearchPageViewModel by viewModels {
        this.viewModelFactory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.betterDocApiKey = resources.getString(R.string.better_doc_api_key)
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
            activity!!.runOnUiThread {
                this.homePageViewModel.checkBetterDocApiHealth.observe(viewLifecycleOwner, checkBetterDocApiHealthObserver())
            }
            if (this.connectionSettings != null && this.connectionSettings?.getSnackBar() != null) {
                this.connectionSettings?.getSnackBar()!!.dismiss()
            }
        } else {
            displayConnectionSetting()
        }
    }
    private fun checkBetterDocApiHealthObserver(): Observer<ResultHandler<Any>> {
        return Observer {
            it?.let {
                when (it.status) {
                    ResultHandler.ResultStatus.LOADING -> {
                        if (it.data is Boolean) {
                            val isLoading: Boolean = it.data
                            if (isLoading) {
                                Log.i("Loading", "Yes")
                            }
                        }
                    }
                    ResultHandler.ResultStatus.SUCCESS -> {
                        if (it.data is BetterDocApiHealthRes) {
                            val data: BetterDocApiHealthRes = it.data
                            this.homePageViewModel.fetchDocByDisease(this.betterDocApiKey, this.latitude, this.longitude, "arthritis")
                            if (BuildConfig.DEBUG) {
                                Log.i(TAG, "Logging BetterDocApiHealth Information---> Status: ${data.status}, Api Version: ${data.apiVersion}")
                            }
                            this.homePageViewModel.fetchDocByDiseaseLiveData?.let {
                                it.observe(viewLifecycleOwner, Observer {
                                    it?.let {
                                        when (it.status) {
                                            ResultHandler.ResultStatus.LOADING -> {

                                            }
                                            ResultHandler.ResultStatus.SUCCESS -> {
                                                if (it.data is BetterDocSearchByDiseaseRes) {
                                                    val searchData: BetterDocSearchByDiseaseRes = it.data
                                                    Log.d(TAG, "Result: ${searchData.metaData}")
                                                }
                                            }
                                            ResultHandler.ResultStatus.ERROR -> {
                                                if (it.message != null && it.message.isNotEmpty()) {
                                                    if (BuildConfig.DEBUG) {
                                                        Log.i(TAG, "BetterDocApiHealth Exception: ${it.message}")
                                                    }
                                                }
                                            }
                                        }
                                    }
                                })
                            }
                        }
                    }
                    ResultHandler.ResultStatus.ERROR -> {
                        if (it.message != null && it.message.isNotEmpty()) {
                            if (BuildConfig.DEBUG) {
                                Log.i(TAG, "BetterDocApiHealth Exception: ${it.message}")
                            }
                        }
                    }
                }
            }
        }
    }
    private fun displayConnectionSetting() {
        this.connectionSettings =
            ConnectionSettings(
                activity!!,
                fragment_search_page_snackbar_id
            )
        connectionSettings?.initWifiSetting(false)
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
        this.homePageViewModel.checkBetterDocApiHealth.removeObserver(checkBetterDocApiHealthObserver())
    }
}
