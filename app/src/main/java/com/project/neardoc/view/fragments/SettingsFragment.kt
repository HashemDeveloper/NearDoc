package com.project.neardoc.view.fragments

import android.os.Bundle
import android.widget.Toast
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.project.neardoc.R
import com.project.neardoc.di.Injectable
import com.project.neardoc.view.widgets.CustomPreference
import dagger.android.support.AndroidSupportInjection

class SettingsFragment: PreferenceFragmentCompat(), Injectable {
    private var list: List<String>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidSupportInjection.inject(this)
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
                    it.key == "prefSignInSecKey" -> Toast.makeText(context, "SignInKey", Toast.LENGTH_SHORT).show()
                    it.key == "prefDistanceKey" -> Toast.makeText(context, "SetDistance", Toast.LENGTH_SHORT).show()
                    it.key == "prefSetLimitKey" -> Toast.makeText(context, "SetLimit", Toast.LENGTH_SHORT).show()
                    it.key == "prefContactUsKey" -> Toast.makeText(context, "ContactUs", Toast.LENGTH_SHORT).show()
                    it.key == "prefRateKey" -> Toast.makeText(context, "RateUs", Toast.LENGTH_SHORT).show()
                    it.key == "prefSendFeedbackKey" -> Toast.makeText(context, "SendFeedback", Toast.LENGTH_SHORT).show()
                    it.key == "prefTermsAndConditionKey" -> Toast.makeText(context, "TermsAndCond", Toast.LENGTH_SHORT).show()
                    it.key == "prefPrivacyPolicyKey" -> Toast.makeText(context, "PrivacyPol", Toast.LENGTH_SHORT).show()
                }
                true
            }
        }
    }
}