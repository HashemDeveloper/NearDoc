package com.project.neardoc.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import javax.inject.Inject
import androidx.core.content.edit
import com.project.neardoc.utils.Constants
import com.project.neardoc.utils.Constants.Companion.GLOBAL_SHARED_PREF

class SharedPrefService @Inject constructor(): ISharedPrefService {
    companion object {
        private var pref: SharedPreferences? = null
        private var listener: SharedPreferences.OnSharedPreferenceChangeListener? = null
        @Volatile private var instance: SharedPrefService?= null
        private val LOCK = Any()

        operator fun invoke(context: Context):SharedPrefService = instance ?: synchronized(LOCK) {
            instance ?: buildSharedPrefService(context).also {
                instance = it
            }
        }
        private fun buildSharedPrefService(context: Context): SharedPrefService {
            this.pref = PreferenceManager.getDefaultSharedPreferences(context)
            return SharedPrefService()
        }
    }

    override fun saveNetworkState(netAvailable: Boolean) {
        pref?.edit(commit = true) {
            putBoolean(GLOBAL_SHARED_PREF, netAvailable)
        }
        listener?.onSharedPreferenceChanged(pref, GLOBAL_SHARED_PREF)
    }

    override fun getNetworkState(): Boolean? {
        return pref?.getBoolean(GLOBAL_SHARED_PREF, false)
    }
    override fun removeItems(key: String) {
       pref?.edit(commit = true) {
           remove(key)
       }
    }

    override fun registerSharedPrefListener(onSharedPreferenceChangeListener: SharedPreferences.OnSharedPreferenceChangeListener) {
       listener = onSharedPreferenceChangeListener
    }

    override fun unregisterSharedPrefListener(onSharedPreferenceChangeListener: SharedPreferences.OnSharedPreferenceChangeListener) {
        listener = onSharedPreferenceChangeListener
    }
}