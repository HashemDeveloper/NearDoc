package com.project.neardoc.view.widgets

import android.view.View
import android.view.WindowManager
import androidx.fragment.app.FragmentActivity

class GlobalLoadingBar constructor(var activity: FragmentActivity, var view: View) {

    fun setVisibility(isVisible: Boolean) {
        if (isVisible) {
            this.activity.window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            this.view.visibility = View.VISIBLE
        } else {
            this.activity.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            this.view.visibility = View.GONE
        }
    }
}