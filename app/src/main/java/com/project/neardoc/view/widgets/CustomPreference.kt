package com.project.neardoc.view.widgets

import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.RippleDrawable
import android.os.Build
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder
import com.project.neardoc.R

class CustomPreference constructor(context: Context, attr: AttributeSet): Preference(context, attr) {
    override fun onBindViewHolder(holder: PreferenceViewHolder?) {
        super.onBindViewHolder(holder)
        val view: View = holder!!.itemView
        rippleEffect(view)
    }
    private fun rippleEffect(view: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val rippleDrawable: RippleDrawable = this.context.getDrawable(R.drawable.dr_pref_ripple_effect) as RippleDrawable
            view.background = rippleDrawable
        } else {
            val drawable: Drawable = ContextCompat.getDrawable(this.context, R.drawable.dr_pref_ripple_effect_below_21_api) as Drawable
            view.background = drawable
        }
    }
}