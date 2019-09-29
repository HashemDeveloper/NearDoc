package com.project.neardoc.utils

import android.content.Intent
import android.provider.Settings
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.google.android.material.snackbar.Snackbar
import com.project.neardoc.R

class ConnectionSettings(private var fragmentActivity: FragmentActivity, private var view: View) {
    fun initWifiSetting() {
       val snackbar = Snackbar.make(this.view, this.fragmentActivity.resources.getText(R.string.not_connected_internet), Snackbar.LENGTH_INDEFINITE)
        snackbar.view.setBackgroundColor(ContextCompat.getColor(this.fragmentActivity, R.color.black))
        snackbar.setActionTextColor(ContextCompat.getColor(this.fragmentActivity, R.color.red_900))
        snackbar.setAction(this.fragmentActivity.resources.getText(R.string.settings)) { v ->
            val intent = Intent(Settings.ACTION_WIFI_SETTINGS)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
            this.fragmentActivity.startActivity(intent)
        }
        snackbar.show()
    }
}