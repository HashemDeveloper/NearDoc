package com.project.neardoc.view.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.project.neardoc.R
import com.project.neardoc.di.Injectable
import com.project.neardoc.events.BottomBarEvent
import com.project.neardoc.utils.IDeviceSensors
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_account_page.*
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

class AccountPage : Fragment(), Injectable {
    @Inject
    lateinit var iSensors: IDeviceSensors

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
        this.iSensors.setupDeviceSensor(activity!!, fragment_account_room_temp_view_id)
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
        this.iSensors.clearObservers(fragment_account_room_temp_view_id)
    }
}
