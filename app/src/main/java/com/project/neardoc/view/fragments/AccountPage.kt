package com.project.neardoc.view.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.project.neardoc.R
import com.project.neardoc.di.Injectable
import com.project.neardoc.events.BottomBarEvent
import com.project.neardoc.events.LandInSettingPageEvent
import com.project.neardoc.utils.PageType
import dagger.android.support.AndroidSupportInjection
import org.greenrobot.eventbus.EventBus

class AccountPage : Fragment(), Injectable {

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

    override fun onDestroy() {
        EventBus.getDefault().postSticky(BottomBarEvent(true))
        super.onDestroy()
    }
}
