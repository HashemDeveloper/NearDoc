package com.project.neardoc.view.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceFragmentCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.project.neardoc.R
import com.project.neardoc.data.local.ISharedPrefService
import com.project.neardoc.di.Injectable
import com.project.neardoc.events.LandInSettingPageEvent
import com.project.neardoc.utils.Constants
import com.project.neardoc.utils.PageType
import com.project.neardoc.view.widgets.CustomPreference
import com.project.neardoc.view.widgets.CustomSwitchPreference
import com.project.neardoc.view.widgets.NotificationSwitchPref
import com.ramotion.fluidslider.FluidSlider
import dagger.android.support.AndroidSupportInjection
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

class SettingsFragment: PreferenceFragmentCompat(), Injectable {
    @Inject
    lateinit var iSharedPrefService: ISharedPrefService
    private var seekBarMinValue: Int?= null
    private var seekBarMaxValue: Int?= null
    private var seekBarHalveValue: Int?= null
    private var seekBarTotalValue: Int?= null
    private var list: List<String>? = null
    private var isNotificationEnabled: Boolean?= null
    private var notificationPref: NotificationSwitchPref?= null

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
            "prefContactUsKey", "prefRateKey", "prefTermsAndConditionKey", "prefPrivacyPolicyKey")
    }
    private fun getPrefKeys(): List<String>? {
        return if (this.list != null) {
            this.list!!
        } else {
            null
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    private fun setupPreferenceListeners() {
        val locationEnablePref: CustomSwitchPreference = findPreference<CustomSwitchPreference>("prefEnableCurrentLocationKey") as CustomSwitchPreference
        val searchPrefCategory: PreferenceCategory = findPreference<PreferenceCategory>("searchPrefCategory") as PreferenceCategory
        val helpPrefCategory: PreferenceCategory = findPreference<PreferenceCategory>("helpAndSupport") as PreferenceCategory
        notificationPref = findPreference<NotificationSwitchPref>("notificationSwitchPref") as NotificationSwitchPref
        notificationPref!!.setDefaultValue(notificationPref!!.isChecked)
        notificationPref!!.setOnPreferenceClickListener {
            val sharedPref: SharedPreferences = it.sharedPreferences
            val isChecked: Boolean = sharedPref.getBoolean("notificationSwitchPref", false)
            Toast.makeText(this.context, "Checked: $isChecked", Toast.LENGTH_SHORT).show()
            this.iSharedPrefService.setNotificationEnabled(isChecked)
            true
        }
        if (Constants.ENABLE_LOCATION_SWITCH) {
            locationEnablePref.setOnPreferenceClickListener {
                val sharedPref: SharedPreferences = it.sharedPreferences
                val isChecked: Boolean = sharedPref.getBoolean("prefEnableCurrentLocationKey", false)
                this.iSharedPrefService.isLocationEnabled(isChecked)
                true }
        } else {

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
                        activity!!.runOnUiThread {
                            initSeekBar(SeekBarType.SET_DISTANCE)
                        }
                    }
                    it.key == "prefSetLimitKey" -> {
                        activity!!.runOnUiThread {
                            initSeekBar(SeekBarType.SET_LIMIT)
                        }
                    }
                    it.key == "prefContactUsKey" -> {
                        val contactUsFragment = findNavController()
                        contactUsFragment.navigate(R.id.contactUs)
                    }
                    it.key == "prefRateKey" -> Toast.makeText(context, "RateUs", Toast.LENGTH_SHORT).show()

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
    private fun initSeekBar(seekBarType: SeekBarType) {
        when (seekBarType) {
            SeekBarType.SET_DISTANCE -> {
                this.seekBarMinValue = 10
                this.seekBarMaxValue = 50
                this.seekBarHalveValue = this.seekBarMaxValue!! / 2
                this.seekBarTotalValue = this.seekBarMaxValue!! - this.seekBarMinValue!!

                val viewGroup: ViewGroup = activity!!.window.decorView.rootView as ViewGroup
                val sView: View = layoutInflater.inflate(R.layout.seekbar_layout, viewGroup, false)
                val seekBar: FluidSlider = sView.run {
                    this.findViewById(R.id.seek_bar_layout_id)
                }
                val okBt: MaterialButton = sView.run {
                    this.findViewById(R.id.seekbar_ok_bt_id)
                }
                val cancelBt: MaterialButton = sView.run {
                    this.findViewById(R.id.seekbar_cancel_bt_id)
                }
                val seekBarDialog = MaterialAlertDialogBuilder(this.context)
                seekBarDialog.setTitle(this.context!!.getString(R.string.set_distance))
                seekBarDialog.setMessage(this.context!!.getString(R.string.search_radius))
                seekBarDialog.setView(sView)

                val originDialog: AlertDialog = seekBarDialog.create()
                originDialog.show()

                seekBar.bubbleText = "${this.seekBarHalveValue}"
                seekBar.startText = "${this.seekBarMinValue}"
                seekBar.endText = "${this.seekBarMaxValue}"
                seekBar.positionListener = {
                    seekBar.bubbleText = "${this.seekBarMinValue!! + (this.seekBarTotalValue!! * it).toInt()}"
                }
                cancelBt.setOnClickListener {
                    originDialog.dismiss()
                }
                okBt.setOnClickListener {
                    Log.i("Final Result: ", seekBar.bubbleText!!)
                    this.iSharedPrefService.setDistanceRadius(seekBar.bubbleText!!)
                    originDialog.dismiss()
                }
            }
            SeekBarType.SET_LIMIT -> {
                this.seekBarMinValue = 5
                this.seekBarMaxValue = 100
                this.seekBarHalveValue = this.seekBarMaxValue!! / 2
                this.seekBarTotalValue = this.seekBarMaxValue!! - this.seekBarMinValue!!

                val viewGroup: ViewGroup = activity!!.window.decorView.rootView as ViewGroup
                val sView: View = layoutInflater.inflate(R.layout.seekbar_layout, viewGroup, false)
                val seekBar: FluidSlider = sView.run {
                    this.findViewById(R.id.seek_bar_layout_id)
                }
                val okBt: MaterialButton = sView.run {
                    this.findViewById(R.id.seekbar_ok_bt_id)
                }
                val cancelBt: MaterialButton = sView.run {
                    this.findViewById(R.id.seekbar_cancel_bt_id)
                }
                val seekBarDialog = MaterialAlertDialogBuilder(this.context)
                seekBarDialog.setTitle(this.context!!.getString(R.string.set_limit))
                seekBarDialog.setMessage(this.context!!.getString(R.string.search_limit))
                seekBarDialog.setView(sView)

                val originDialog: AlertDialog = seekBarDialog.create()
                originDialog.show()

                seekBar.bubbleText = "${this.seekBarHalveValue}"
                seekBar.startText = "${this.seekBarMinValue}"
                seekBar.endText = "${this.seekBarMaxValue}"
                seekBar.positionListener = {
                    seekBar.bubbleText = "${this.seekBarMinValue!! + (this.seekBarTotalValue!! * it).toInt()}"
                }
                cancelBt.setOnClickListener {
                    originDialog.dismiss()
                }
                okBt.setOnClickListener {
                    Log.i("Final Result: ", seekBar.bubbleText!!)
                    this.iSharedPrefService.setSearchLimit(seekBar.bubbleText!!)
                    originDialog.dismiss()
                }
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().postSticky(LandInSettingPageEvent(false, PageType.MAIN_PAGE))
    }
    private enum class SeekBarType {
        SET_DISTANCE,
        SET_LIMIT
    }
}