package com.project.neardoc.view.fragments

import android.os.Bundle
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.project.neardoc.R
import com.project.neardoc.di.Injectable
import com.project.neardoc.events.LandInSettingPageEvent
import com.project.neardoc.utils.PageType
import com.project.neardoc.view.widgets.CustomPreference
import dagger.android.support.AndroidSupportInjection
import org.greenrobot.eventbus.EventBus

class SettingsFragment: PreferenceFragmentCompat(), Injectable {
    private var list: List<String>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidSupportInjection.inject(this)
        EventBus.getDefault().postSticky(LandInSettingPageEvent(true, PageType.SETTINGS_FRAGMENT))
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings_pref_layout, rootKey)
        addPrefKeys()
        setupPreferenceListeners()
    }
    private fun addPrefKeys() {
        this.list = listOf("prefSignInSecKey", "prefDistanceKey", "prefSetLimitKey",
            "prefContactUsKey", "prefRateKey", "prefSendFeedbackKey", "prefTermsAndConditionKey", "prefPrivacyPolicyKey")
    }
    private fun getPrefKeys(): List<String>? {
        return if (this.list != null) {
            this.list!!
        } else {
            null
        }
    }
    private fun setupPreferenceListeners() {
        for (keys in this.getPrefKeys()!!) {
            val prefSignAndSec: CustomPreference = findPreference<CustomPreference>(keys) as CustomPreference
            prefSignAndSec.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                when {
                    it.key == "prefSignInSecKey" -> {
                        val signInSecFragment = findNavController()
                        signInSecFragment.navigate(R.id.signInSecurity)
                    }
                    it.key == "prefDistanceKey" -> Toast.makeText(context, "SetDistance", Toast.LENGTH_SHORT).show()
                    it.key == "prefSetLimitKey" -> Toast.makeText(context, "SetLimit", Toast.LENGTH_SHORT).show()
                    it.key == "prefContactUsKey" -> {
                        val contactUsFragment = findNavController()
                        contactUsFragment.navigate(R.id.contactUs)
                    }
                    it.key == "prefRateKey" -> Toast.makeText(context, "RateUs", Toast.LENGTH_SHORT).show()
                    it.key == "prefSendFeedbackKey" -> {
                        val sendFeedbackPage = findNavController()
                        sendFeedbackPage.navigate(R.id.sendFeedback)
                    }
                    it.key == "prefTermsAndConditionKey" -> {
                        val termsAndConditionPage = findNavController()
                        termsAndConditionPage.navigate(R.id.termsAndCondition)
                    }
                    it.key == "prefPrivacyPolicyKey" -> {
                        val privacyPolicyPage = findNavController()
                        privacyPolicyPage.navigate(R.id.privacyPolicy)
                    }
                }
                true
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().postSticky(LandInSettingPageEvent(false, PageType.MAIN_PAGE))
    }
}