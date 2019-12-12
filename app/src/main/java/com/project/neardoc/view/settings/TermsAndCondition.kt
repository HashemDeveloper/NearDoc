package com.project.neardoc.view.settings


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.project.neardoc.R
import com.project.neardoc.di.Injectable
import com.project.neardoc.events.LandInSettingPageEvent
import com.project.neardoc.utils.widgets.PageType
import dagger.android.support.AndroidSupportInjection
import org.greenrobot.eventbus.EventBus

class TermsAndCondition : Fragment(), Injectable {
    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)
        EventBus.getDefault().postSticky(LandInSettingPageEvent(true, PageType.TERMS_AND_CONDITION))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_terms_and_condition, container, false)
    }
    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().postSticky(LandInSettingPageEvent(false, PageType.SETTINGS_FRAGMENT))
    }
}
