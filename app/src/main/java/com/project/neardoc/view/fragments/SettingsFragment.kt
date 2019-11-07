package com.project.neardoc.view.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceFragmentCompat
import com.project.neardoc.R
import com.project.neardoc.data.local.ISharedPrefService
import com.project.neardoc.di.Injectable
import com.project.neardoc.events.LandInSettingPageEvent
import com.project.neardoc.utils.Constants
import com.project.neardoc.utils.PageType
import com.project.neardoc.view.widgets.CustomPreference
import com.project.neardoc.view.widgets.CustomSwitchPreference
import dagger.android.support.AndroidSupportInjection
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

class SettingsFragment: PreferenceFragmentCompat(), Injectable {
    @Inject
    lateinit var iSharedPrefService: ISharedPrefService
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
        this.list = listOf("prefSignInSecKey" , "prefDistanceKey", "prefSetLimitKey",
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
        val locationEnablePref: CustomSwitchPreference = findPreference<CustomSwitchPreference>("prefEnableCurrentLocationKey") as CustomSwitchPreference
        if (Constants.ENABLE_LOCATION_SWITCH) {
            locationEnablePref.setOnPreferenceClickListener {
                val sharedPref: SharedPreferences = it.sharedPreferences
                val isChecked: Boolean = sharedPref.getBoolean("prefEnableCurrentLocationKey", false)
                this.iSharedPrefService.isLocationEnabled(isChecked)
                true }
        } else {
            val searchPrefCategory: PreferenceCategory = findPreference<PreferenceCategory>("searchPrefCategory") as PreferenceCategory
            searchPrefCategory.removePreference(locationEnablePref)
        }

        for (keys in this.getPrefKeys()!!) {
            val prefSignAndSec: CustomPreference = findPreference<CustomPreference>(keys) as CustomPreference
            prefSignAndSec.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                when {
                    it.key == "prefSignInSecKey" -> {
                        val signInSecFragment = findNavController()
                        signInSecFragment.navigate(R.id.signInSecurity)
                    }

                    it.key == "prefDistanceKey" -> {
                        Toast.makeText(context, "SetDistance", Toast.LENGTH_SHORT).show()
                    }
                    it.key == "prefSetLimitKey" -> {
                        Toast.makeText(context, "SetLimit", Toast.LENGTH_SHORT).show()
                    }
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