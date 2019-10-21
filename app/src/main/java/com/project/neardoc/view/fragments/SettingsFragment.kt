package com.project.neardoc.view.fragments

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.project.neardoc.R
import com.project.neardoc.di.Injectable
import dagger.android.support.AndroidSupportInjection

class SettingsFragment: PreferenceFragmentCompat(), Injectable {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidSupportInjection.inject(this)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings_pref_layout, rootKey)
    }
}