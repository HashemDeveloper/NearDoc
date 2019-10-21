package com.project.neardoc.view.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import androidx.preference.PreferenceViewHolder
import androidx.preference.SwitchPreference

class CustomSwitchPreference(context: Context?, attrs: AttributeSet?) : SwitchPreference(context, attrs) {

    override fun onBindViewHolder(holder: PreferenceViewHolder?) {
        super.onBindViewHolder(holder)
        val view: View = holder!!.itemView
        val textView: TextView = view.findViewById(android.R.id.title)
        textView.textSize = 18.0f
    }
}