package com.project.neardoc.data.local

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.project.neardoc.utils.Constants
import java.util.*
import javax.inject.Inject

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
        if (listener != null) {
            listener = null
        }
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

    override fun storeGoogleIdToken(encryptedIdToken: ByteArray) {
        pref?.edit(commit = true) {
            putString(Constants.SHARED_PREF_GOOGLE_TOKEN_ID, Base64.encodeToString(encryptedIdToken, Base64.DEFAULT))
        }
    }

    override fun storeGoogleEncryptIv(iv: ByteArray?) {
        pref?.edit(commit = true) {
            putString(Constants.SHARED_PREF_GOOGLE_TOKEN_ENCRYPT_IV, Base64.encodeToString(iv, Base64.DEFAULT))
        }
    }

    override fun getGoogleTokenId(): String {
        return pref?.getString(Constants.SHARED_PREF_GOOGLE_TOKEN_ID, "")!!
    }

    override fun getGoogleTokenEncryptIv(): String {
        return pref?.getString(Constants.SHARED_PREF_GOOGLE_TOKEN_ENCRYPT_IV, "")!!
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

    override fun storeLoginProvider(loginProvider: String) {
        pref?.edit(commit = true) {
            putString(Constants.SHARED_PREF_USER_LOGIN_PROVIDER, loginProvider)
        }
    }

    override fun getLoginProvider(): String {
        return pref?.getString(Constants.SHARED_PREF_USER_LOGIN_PROVIDER, "")!!
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

    override fun isLocationEnabled(checked: Boolean) {
       pref?.edit(commit = true) {
           putBoolean(Constants.SHARED_PREF_IS_LOCATION_ENABLED, checked)
       }
        listener?.onSharedPreferenceChanged(pref, Constants.SHARED_PREF_IS_LOCATION_ENABLED)
    }
    override fun setDistanceRadius(distance: String) {
       pref?.edit(commit = true) {
           putString(Constants.SHARED_PREF_DISTANCE_RADIUS, distance)
       }
    }

    override fun setSearchLimit(limit: String) {
        pref?.edit(commit = true) {
            putString(Constants.SHARED_PREF_SEARCH_LIMIT, limit)
        }
    }

    override fun getDistanceRadius(): String {
        return pref?.getString(Constants.SHARED_PREF_DISTANCE_RADIUS, "10")!!
    }

    override fun getSearchLimit(): String {
       return pref?.getString(Constants.SHARED_PREF_SEARCH_LIMIT, "10")!!
    }

    override fun setBreath(breath: Int) {
       pref?.edit(commit = true) {
           putInt(Constants.SHARED_PREF_BREATHING_NUM, breath)
       }
    }

    override fun getBreath(): Int {
        return pref?.getInt(Constants.SHARED_PREF_BREATHING_NUM, 0)!!
    }

    override fun setBreathingSession(session: Int) {
        pref?.edit(commit = true) {
            putInt(Constants.SHARED_PREF_BREATHING_SESSION, session)
        }
    }

    override fun getBreathingSession(): Int {
        return pref?.getInt(Constants.SHARED_PREF_BREATHING_SESSION, 0)!!
    }

    override fun setBreathingDate(date: Long) {
        pref?.edit(commit = true) {
            putLong(Constants.SHARED_PREF_BREATHING_DATE, date)
        }
    }

    override fun getBreathingDate(): String {
        val milliDate: Long = pref?.getLong(Constants.SHARED_PREF_BREATHING_DATE, 0L)!!
        val ampOrPm: String
        val calender: Calendar = Calendar.getInstance()
        calender.timeInMillis = milliDate
        val am: Int = calender.get(Calendar.AM_PM)
        ampOrPm = if (am == Calendar.AM) {
            "AM"
        } else {
            "PM"
        }
        return "Last " + calender.get(Calendar.HOUR_OF_DAY) + ":" + calender.get(Calendar.MINUTE) + " " + ampOrPm
    }

    override fun setRepeatCount(count: Int) {
        pref?.edit(commit = true) {
            putInt(Constants.SHARED_PREF_BREATH_REPEAT_COUNT, count)
        }
    }

    override fun getRepeatCount(): Int {
        return pref?.getInt(Constants.SHARED_PREF_BREATH_REPEAT_COUNT, 0)!!
    }

    override fun setUserCurrentState(currentState: String) {
        pref?.edit(commit = true) {
            putString(Constants.SHARED_PREF_USER_CURRENT_STATE, currentState)
        }
    }

    override fun getUserCurrentState(): String {
        return pref?.getString(Constants.SHARED_PREF_USER_CURRENT_STATE, "")!!
    }

    override fun storeLastValueOfStepTaken(values: Int?) {
        pref?.edit(commit = true) {
            putInt(Constants.STEP_COUNT_VALUE, values!!)
        }
    }

    override fun getLastStepCountValue(): Int {
        return pref?.getInt(Constants.STEP_COUNT_VALUE, 0)!!
    }

    override fun setNotificationEnabled(checked: Boolean) {
        pref?.edit(commit = true) {
            putBoolean(Constants.NOTIFICATION_ENABLED, checked)
        }
    }

    override fun getIsNotificationEnabled(): Boolean {
        return pref?.getBoolean(Constants.NOTIFICATION_ENABLED, false)!!
    }

    override fun saveTotalStepCount(lastStepCountValue: Int) {
        pref?.edit(commit = true) {
            putInt(Constants.TOTAL_STEP_COUNT, lastStepCountValue)
        }
    }

    override fun getTotalStepCount(): Int {
        return pref?.getInt(Constants.TOTAL_STEP_COUNT, 0)!!
    }

    override fun saveStepCountServiceType(serviceType: String) {
        pref?.edit(commit = true) {
            putString(Constants.SERVICE_TYPE, serviceType)
        }
        listener?.onSharedPreferenceChanged(pref, Constants.SERVICE_TYPE)
    }

    override fun getStepCountServiceType(): String {
        return pref?.getString(Constants.SERVICE_TYPE, "")!!
    }

    override fun saveCachingTime(nanoTime: Long) {
        pref?.edit(commit = true) {
            putLong(Constants.SEARCH_DOC_CACHE_TIME, nanoTime)
        }
    }

    override fun getCachingTime(): Long {
        return pref?.getLong(Constants.SEARCH_DOC_CACHE_TIME, 0L)!!
    }

    override fun saveNavigationType(name: String) {
        pref?.edit(commit = true) {
            putString(Constants.NAVIGATION_TYPE, name)
        }
    }

    override fun getNavigationType(): String {
        return pref?.getString(Constants.NAVIGATION_TYPE, "")!!
    }
}