package com.project.neardoc.view.widgets

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import androidx.preference.PreferenceViewHolder
import androidx.preference.SwitchPreference
import com.project.neardoc.R

class CustomSwitchPreference(context: Context?, attrs: AttributeSet?) : SwitchPreference(context, attrs) {
    init {
        val typeArray: TypedArray = context!!.obtainStyledAttributes(attrs, R.styleable.SwitchPreference, R.attr.switchPreferenceStyle, R.style.SwitchPrefStyle)
        typeArray.recycle()
    }
    override fun onBindViewHolder(holder: PreferenceViewHolder?) {
        super.onBindViewHolder(holder)
        val view: View = holder!!.itemView
        val textView: TextView = view.findViewById(android.R.id.title)
        textView.textSize = 18.0f
    }
}