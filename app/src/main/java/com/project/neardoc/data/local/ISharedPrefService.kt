package com.project.neardoc.data.local

import android.content.SharedPreferences

interface ISharedPrefService {
    fun registerSharedPrefListener(onSharedPreferenceChangeListener: SharedPreferences.OnSharedPreferenceChangeListener)
    fun unregisterSharedPrefListener(onSharedPreferenceChangeListener: SharedPreferences.OnSharedPreferenceChangeListener)
    fun saveNetworkState(netAvailable: Boolean)
    fun getNetworkState(): Boolean?
    fun removeItems(key: String)
}