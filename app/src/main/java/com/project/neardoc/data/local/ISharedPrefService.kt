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
    fun storeUserEmail(email: String)
    fun getUserName(): String
    fun getUserEmail(): String
    fun getUserImage(): String
}