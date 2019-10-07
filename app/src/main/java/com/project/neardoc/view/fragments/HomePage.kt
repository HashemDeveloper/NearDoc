package com.project.neardoc.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.project.neardoc.R
import com.project.neardoc.di.Injectable
import com.project.neardoc.di.viewmodel.ViewModelFactory
import com.project.neardoc.events.NetworkStateEvent
import com.project.neardoc.utils.Constants
import com.project.neardoc.viewmodel.HomepageViewModel
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_home_page.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

class HomePage: Fragment(), Injectable {
    private var isInternetAvailable = false
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
        return inflater.inflate(R.layout.fragment_home_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragment_home_page_sign_out_id.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val navigateToWelcome = findNavController()
            navigateToWelcome.navigate(R.id.welcome)
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
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }
}