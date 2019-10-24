package com.project.neardoc.data.local

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import androidx.preference.PreferenceManager
import javax.inject.Inject
import androidx.core.content.edit
import com.project.neardoc.utils.Constants
import java.util.*

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
    override fun storeIdToken(encryptedIdToken: ByteArray) {
        pref?.edit(commit = true) {
            putString(Constants.SHARED_PREF_ID_TOKEN, Base64.encodeToString(encryptedIdToken, Base64.DEFAULT))
        }
    }

    override fun storeEncryptIV(iv: ByteArray) {
        pref?.edit(commit = true) {
            putString(Constants.SHARED_PREF_ENCRYPT_IV, Base64.encodeToString(iv, Base64.DEFAULT))
        }
    }
    override fun getIdToken(): String {
        return pref?.getString(Constants.SHARED_PREF_ID_TOKEN, "")!!
    }

    override fun getEncryptIv(): String {
        return pref?.getString(Constants.SHARED_PREF_ENCRYPT_IV, "")!!
    }
    override fun storeUserName(name: String) {
       pref?.edit(commit = true) {
           putString(Constants.SHARED_PREF_USER_NAME, name)
       }
        listener?.onSharedPreferenceChanged(pref, Constants.SHARED_PREF_USER_NAME)
    }

    override fun storeUserEmail(email: String) {
       pref?.edit(commit = true) {
           putString(Constants.SHARED_PREF_USER_EMAIL, email)
       }
        listener?.onSharedPreferenceChanged(pref, Constants.SHARED_PREF_USER_EMAIL)
    }
    override fun getUserName(): String {
        return pref?.getString(Constants.SHARED_PREF_USER_NAME, "")!!
    }

    override fun getUserEmail(): String {
        return pref?.getString(Constants.SHARED_PREF_USER_EMAIL, "")!!
    }
    override fun storeUserImage(image: String) {
        pref?.edit(commit = true) {
            putString(Constants.SHARED_PREF_USER_IMAGE, image)
        }
        listener?.onSharedPreferenceChanged(pref, Constants.SHARED_PREF_USER_IMAGE)
    }

    override fun getUserImage(): String {
        return pref?.getString(Constants.SHARED_PREF_USER_IMAGE, "")!!
    }

    override fun storeUserUsername(username: String) {
       pref?.edit(commit = true) {
           putString(Constants.SHARED_PREF_USER_USERNAME, username)
       }
        listener?.onSharedPreferenceChanged(pref, Constants.SHARED_PREF_USER_USERNAME)
    }

    override fun getUserUsername(): String {
        return pref?.getString(Constants.SHARED_PREF_USER_USERNAME, "")!!
    }
}