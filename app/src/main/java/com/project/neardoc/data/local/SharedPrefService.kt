package com.project.neardoc.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import javax.inject.Inject

class SharedPrefService @Inject constructor(): ISharedPrefService {
    companion object {
        private const val GLOBAL_SHARED_PREF = "global_shared_pref"
        private var pref: SharedPreferences? = null
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
}