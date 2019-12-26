package com.project.neardoc.view.fragments


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.iammert.library.ui.multisearchviewlib.MultiSearchView
import com.project.neardoc.BuildConfig

import com.project.neardoc.R
import com.project.neardoc.di.Injectable
import com.project.neardoc.di.viewmodel.ViewModelFactory
import com.project.neardoc.events.LocationUpdateEvent
import com.project.neardoc.events.NetworkStateEvent
import com.project.neardoc.model.BetterDocApiHealthRes
import com.project.neardoc.model.localstoragemodels.DocAndRelations
import com.project.neardoc.utils.networkconnections.ConnectionSettings
import com.project.neardoc.utils.Constants
import com.project.neardoc.utils.LocalDbInsertionOption
import com.project.neardoc.utils.livedata.ResultHandler
import com.project.neardoc.view.widgets.GlobalLoadingBar
import com.project.neardoc.viewmodel.SearchPageViewModel
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_search_page.*
import kotlinx.coroutines.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class SearchPage : Fragment(), Injectable, CoroutineScope, MultiSearchView.MultiSearchViewListener{
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
    private val globalLoadingBar: GlobalLoadingBar by lazy {
        GlobalLoadingBar(activity!!, fragment_search_progress_bar_id)
    }
    private val homePageViewModel: SearchPageViewModel by viewModels {
        this.viewModelFactory
    }
    private val job = Job()

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
        fragment_search_page_search_container_id.setSearchViewListener(this)
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
                this.homePageViewModel.getDoctorsData()
                this.homePageViewModel.checkBetterDocApiHealth?.observe(viewLifecycleOwner, checkBetterDocApiHealthObserver())
                doctorResultHandler()
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
                        var isLoading: Boolean?= null
                        launch {
                            withContext(Dispatchers.IO) {
                                if (it.data is Boolean) {
                                    isLoading = it.data
                                }
                            }
                        }.invokeOnCompletion {throwable ->
                            if (throwable != null && throwable.localizedMessage != null) {
                                if (BuildConfig.DEBUG) {
                                    Log.i(TAG, "Check Api Health Coroutines Exception: ${throwable.localizedMessage!!}")
                                }
                            } else {
                                activity!!.runOnUiThread {
                                    isLoading?.let {loading ->
                                        if (loading) {
                                            displayLoading(loading)
                                        }
                                    }
                                }
                            }
                        }

                    }
                    ResultHandler.ResultStatus.SUCCESS -> {
                        launch {
                            if (it.data is BetterDocApiHealthRes) {
                                val data: BetterDocApiHealthRes = it.data
                                if (BuildConfig.DEBUG) {
                                    Log.i(TAG, "Logging BetterDocApiHealth Information---> Status: ${data.status}, Api Version: ${data.apiVersion}")
                                }
                                homePageViewModel.initNearByDocList(betterDocApiKey, latitude, longitude, "", LocalDbInsertionOption.INSERT)
                                doctorResultHandler()
                            }
                        }
                    }
                    ResultHandler.ResultStatus.ERROR -> {
                        if (it.message != null && it.message.isNotEmpty()) {
                            if (BuildConfig.DEBUG) {
                                Log.i(TAG, "BetterDocApiHealth Exception: ${it.message}")
                            }
                        }
                        displayLoading(false)
                    }
                }
            }
        }
    }
    private fun doctorResultHandler() {
        this.homePageViewModel.fetchDocByDiseaseLiveData?.let { result ->
            result.observe(viewLifecycleOwner, Observer { resultHandler ->
                resultHandler?.let {
                    when (it.status) {
                        ResultHandler.ResultStatus.LOADING -> {
                            var isLoading: Boolean?= null
                            launch {
                                withContext(Dispatchers.IO) {
                                    if (it.data is Boolean) {
                                        isLoading = it.data
                                    }
                                }
                            }.invokeOnCompletion {throwable ->
                                if (throwable != null && throwable.localizedMessage != null) {
                                    if (BuildConfig.DEBUG) {
                                        Log.i(TAG, "Check Api Health Coroutines Exception: ${throwable.localizedMessage!!}")
                                    }
                                } else {
                                    activity!!.runOnUiThread {
                                        isLoading?.let {loading ->
                                            if (loading) {
                                                displayLoading(loading)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        ResultHandler.ResultStatus.SUCCESS -> {
                            launch { withContext(Dispatchers.IO) {
                                val docAndRelations: List<DocAndRelations> = it.data as List<DocAndRelations>
                                for (list in docAndRelations) {
                                    for (rating in list.docRating) {
                                        Log.d(TAG, "Rating: ${rating.rating}")
                                    }
                                }
                            }}.invokeOnCompletion { throwable ->
                                if (throwable != null && throwable.localizedMessage != null) {
                                    if (BuildConfig.DEBUG) {
                                        Log.i(TAG, "Search Result Handler Coroutines Exception: ${throwable.localizedMessage!!}")
                                    }
                                } else {
                                    activity!!.runOnUiThread {
                                        displayLoading(false)
                                    }
                                }
                            }
                        }
                        ResultHandler.ResultStatus.ERROR -> {
                            if (it.message != null && it.message.isNotEmpty()) {
                                if (BuildConfig.DEBUG) {
                                    Log.i(TAG, "BetterDocApiHealth Exception: ${it.message}")
                                }
                            }
                            displayLoading(false)
                        }
                    }
                }
            })
        }
    }
    private fun displayLoading(isLoading: Boolean) {
        this.globalLoadingBar.setVisibility(isLoading)
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
        this.homePageViewModel.checkBetterDocApiHealth?.removeObserver(checkBetterDocApiHealthObserver())
    }

    override val coroutineContext: CoroutineContext
        get() = this.job + Dispatchers.Main

    override fun onItemSelected(index: Int, s: CharSequence) {

    }

    override fun onSearchComplete(index: Int, s: CharSequence) {
        homePageViewModel.initNearByDocList(
            betterDocApiKey,
            latitude,
            longitude,
            s.toString(),
            LocalDbInsertionOption.UPDATE
        )
        doctorResultHandler()
    }

    override fun onSearchItemRemoved(index: Int) {
    }

    override fun onTextChanged(index: Int, s: CharSequence) {

    }
}
