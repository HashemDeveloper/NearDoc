package com.project.neardoc.data.local

import android.content.SharedPreferences

interface ISharedPrefService {
    fun registerSharedPrefListener(onSharedPreferenceChangeListener: SharedPreferences.OnSharedPreferenceChangeListener)
    fun unregisterSharedPrefListener(onSharedPreferenceChangeListener: SharedPreferences.OnSharedPreferenceChangeListener)
    fun removeItems(key: String)
    fun storeIdToken(encryptedIdToken: ByteArray)
    fun storeEncryptIV(iv: ByteArray)
    fun getIdToken(): String
    fun getEncryptIv(): String
    fun storeUserImage(image: String)
    fun storeUserName(name: String)
    fun storeUserUsername(username: String)
    fun storeUserEmail(email: String)
    fun getUserName(): String
    fun getUserEmail(): String
    fun getUserImage(): String
    fun getUserUsername(): String
    fun storeLoginProvider(loginProvider: String)
    fun getLoginProvider(): String
    fun storeGoogleIdToken(encryptedIdToken: ByteArray)
    fun storeGoogleEncryptIv(iv: ByteArray?)
    fun getGoogleTokenId(): String
    fun getGoogleTokenEncryptIv(): String
    fun isLocationEnabled(checked: Boolean)
    fun setDistanceRadius(distance: String)
    fun setSearchLimit(limit: String)
    fun getDistanceRadius(): String
    fun getSearchLimit(): String
    fun setBreath(breath: Int)
    fun getBreath(): Int
    fun setBreathingSession(session: Int)
    fun getBreathingSession(): Int
    fun setBreathingDate(date: Long)
    fun getBreathingDate(): String
    fun setRepeatCount(count: Int)
    fun getRepeatCount(): Int
}