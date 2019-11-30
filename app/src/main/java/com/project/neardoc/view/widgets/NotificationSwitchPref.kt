package com.project.neardoc.view.widgets

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.RippleDrawable
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.preference.PreferenceViewHolder
import androidx.preference.SwitchPreference
import com.project.neardoc.R


class NotificationSwitchPref constructor(context: Context, attributeSet: AttributeSet): SwitchPreference(context, attributeSet) {
    private var switch: Switch?= null

    override fun onBindViewHolder(holder: PreferenceViewHolder?) {
        super.onBindViewHolder(holder)
        val view: View = holder!!.itemView
        val textView: TextView = view.findViewById(android.R.id.title)
        textView.textSize = 18.0f
        setDefaultValue(true)
        this.switch = findSwitchInChildViews(view as ViewGroup)
        if (switch!!.isChecked) {
            changeColor(switch!!, this.switch!!.isChecked)
        } else {
            changeColor(this.switch!!, this.switch!!.isChecked)
        }
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

    private fun findSwitchInChildViews(view: ViewGroup): Switch? {
        var requiredView: Switch?= null
        for (i in 0 until view.childCount) {
            val chilDview: View = view.getChildAt(i)
            if (chilDview is Switch) {
                requiredView = chilDview
                break
            } else if (chilDview is ViewGroup) {
                val theSwitch =
                    findSwitchInChildViews(chilDview)
                if (theSwitch != null) {
                    requiredView = theSwitch
                    break
                }
            }
        }
        return requiredView
    }

    private fun changeColor(switch: Switch, checked: Boolean) {
        val states = arrayOf(
            intArrayOf(-android.R.attr.state_checked),
            intArrayOf(android.R.attr.state_checked)
        )
        val thumbColors = intArrayOf(
            Color.RED,
            Color.GREEN
        )

        val trackColors = intArrayOf(
            Color.LTGRAY,
            Color.LTGRAY
        )
        try {
            if (checked) {
                DrawableCompat.setTintList(
                    DrawableCompat.wrap(switch.thumbDrawable),
                    ColorStateList(states, thumbColors)
                )
            } else {
                DrawableCompat.setTintList(
                    DrawableCompat.wrap(switch.trackDrawable),
                    ColorStateList(states, trackColors)
                )
            }
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }
    }

}