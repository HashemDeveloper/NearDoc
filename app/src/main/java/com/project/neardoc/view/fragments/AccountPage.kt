package com.project.neardoc.view.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.project.neardoc.R
import com.project.neardoc.di.Injectable
import com.project.neardoc.di.viewmodel.ViewModelFactory
import com.project.neardoc.events.BottomBarEvent
import com.project.neardoc.events.LocationUpdateEvent
import com.project.neardoc.model.CurrentLocation
import com.project.neardoc.utils.IDeviceSensors
import com.project.neardoc.viewmodel.AccountPageViewModel
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_account_page.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

class AccountPage : Fragment(), Injectable {
    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val accountPageViewModel: AccountPageViewModel by viewModels {
        this.viewModelFactory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)
        EventBus.getDefault().postSticky(BottomBarEvent(false))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_account_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.accountPageViewModel.setupUserProfile(context!!, fragment_account_user_image_view_id, fragment_account_user_name_id,
            fragment_account_user_email_view_id, fragment_account_user_location_view_id)
        this.accountPageViewModel.setupDeviceSensor(activity!!, fragment_account_room_temp_view_id)
        this.accountPageViewModel.animateBreathingTitleView(fragment_account_breathing_ex_title_view_id)
        fragment_account_start_breathing_bt_id.setOnClickListener {
            this.accountPageViewModel.startAnimation(context!!, activity!!, fragment_account_breathing_image_view_id,
                fragment_account_breath_guide_text_view_id)
        }
        fragment_account_go_back_bt_id.setOnClickListener {
            EventBus.getDefault().postSticky(BottomBarEvent(true))
            Navigation.findNavController(it).navigate(AccountPageDirections.actionHomePage())
        }
    }

    override fun onDestroy() {
        EventBus.getDefault().postSticky(BottomBarEvent(true))
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        EventBus.getDefault().postSticky(BottomBarEvent(false))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        this.accountPageViewModel.clearObservers(fragment_account_room_temp_view_id)
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onStop() {
        super.onStop()
    }
}
