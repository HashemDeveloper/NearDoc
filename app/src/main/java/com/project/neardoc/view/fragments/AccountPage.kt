package com.project.neardoc.view.fragments


import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.linroid.filtermenu.library.FilterMenu
import com.linroid.filtermenu.library.FilterMenuLayout
import com.project.neardoc.R
import com.project.neardoc.di.Injectable
import com.project.neardoc.di.viewmodel.ViewModelFactory
import com.project.neardoc.events.BottomBarEvent
import com.project.neardoc.viewmodel.AccountPageViewModel
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_account_page.*
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject


class AccountPage : Fragment(), Injectable, FilterMenu.OnMenuChangeListener {
    companion object {
        const val ACTIVITY_RECOGNITION_REQ_CODE: Int = 2
    }
    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val accountPageViewModel: AccountPageViewModel by viewModels {
        this.viewModelFactory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)
        EventBus.getDefault().postSticky(BottomBarEvent(false))
        requestActivityRecogPermission()
    }

    @SuppressLint("InlinedApi")
    private fun requestActivityRecogPermission() {
        if (ActivityCompat.checkSelfPermission(activity!!, Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.ACTIVITY_RECOGNITION), ACTIVITY_RECOGNITION_REQ_CODE)
        }
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
        this.accountPageViewModel.setupDeviceSensor(activity!!, fragment_account_room_temp_view_id, fragment_account_step_counter_view_id)
        this.accountPageViewModel.animateBreathingTitleView(fragment_account_breathing_ex_title_view_id)
        fragment_account_start_breathing_bt_id.setOnClickListener {
            this.accountPageViewModel.startAnimation(context!!, activity!!, fragment_account_breathing_image_view_id,
                fragment_account_breath_guide_text_view_id)
        }
        fragment_account_go_back_bt_id.setOnClickListener {
            EventBus.getDefault().postSticky(BottomBarEvent(true))
            Navigation.findNavController(it).navigate(AccountPageDirections.actionHomePage())
        }
        attachMenu(fragment_account_menu_bt_id)
    }

    private fun attachMenu(layout: FilterMenuLayout?): FilterMenu? {
        val signOutBt = AppCompatImageView(this.context)
        signOutBt.setBackgroundResource(R.drawable.ic_exit_to_app_white_24dp)
        signOutBt.id = R.id.account_signout_bt
        val heartRate = AppCompatImageView(this.context)
        heartRate.setBackgroundResource(R.drawable.heart_cardiogram_24_white)
        heartRate.id = R.id.account_heart_rate_bt
        return FilterMenu.Builder(this.context)
            .addItem(signOutBt)
            .addItem(heartRate)
            .attach(layout)
            .withListener(this)
            .build()
    }

    override fun onMenuCollapse() {

    }

    override fun onMenuItemClick(view: View?, position: Int) {
        if (view!!.id == R.id.account_signout_bt) {
            signOut()
        } else if (view.id == R.id.account_heart_rate_bt) {
            Toast.makeText(this.context, "Heart rate", Toast.LENGTH_SHORT).show()
        }
    }
    private fun signOut() {
        this.accountPageViewModel.signOut()
        val navigateToWelcome = findNavController()
        navigateToWelcome.navigate(R.id.welcome)
    }

    override fun onMenuExpand() {

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
        this.accountPageViewModel.clearObservers(fragment_account_room_temp_view_id, fragment_account_step_counter_view_id)
    }
}
