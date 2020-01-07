package com.project.neardoc.view.settings


import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast

import com.project.neardoc.R
import com.project.neardoc.di.Injectable
import com.project.neardoc.events.LandInSettingPageEvent
import com.project.neardoc.utils.Constants
import com.project.neardoc.utils.widgets.PageType
import com.project.neardoc.view.widgets.GlobalLoadingBar
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_privacy_policy.*
import kotlinx.android.synthetic.main.fragment_terms_and_condition.*
import org.greenrobot.eventbus.EventBus

@SuppressLint("SetJavaScriptEnabled")
class PrivacyPolicy : Fragment(), Injectable {
    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)
        EventBus.getDefault().postSticky(LandInSettingPageEvent(true, PageType.PRIVACY_POLICY))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_privacy_policy, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragment_privacy_policy_webview_id?.settings!!.javaScriptEnabled = true
        displayLoading(true)
        fragment_privacy_policy_webview_id?.webViewClient = object : WebViewClient() {
            override fun onReceivedError(
                view: WebView?,
                errorCode: Int,
                description: String?,
                failingUrl: String?
            ) {
                Toast.makeText(context!!, description, Toast.LENGTH_SHORT).show()
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                displayLoading(false)
            }
        }
        fragment_privacy_policy_webview_id?.loadUrl(Constants.TERMS_AND_CONDITION_URL)
    }

    private fun displayLoading(isLoading: Boolean) {
        val globalLoadingBar = GlobalLoadingBar(activity!!, fragment_privacy_policy_loading_bar_id)
        if (isLoading) {
            globalLoadingBar.setVisibility(true)
        } else {
            globalLoadingBar.setVisibility(false)
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().postSticky(LandInSettingPageEvent(false, PageType.SETTINGS_FRAGMENT))
    }
}
