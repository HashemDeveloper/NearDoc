package com.project.neardoc.services

import com.google.firebase.messaging.FirebaseMessagingService
import com.project.neardoc.data.local.ISharedPrefService
import com.project.neardoc.di.firebaseservice.NearFirebaseServiceInjection
import com.project.neardoc.utils.Constants
import com.project.neardoc.utils.EnCryptor
import javax.inject.Inject

class FirebaseTokenIdService @Inject constructor(): FirebaseMessagingService() {
    @Inject
    lateinit var iSharedPrefService: ISharedPrefService
    override fun onCreate() {
        NearFirebaseServiceInjection.inject(this)
        super.onCreate()
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        val enCryptor = EnCryptor()
        val encryptedToken: ByteArray = enCryptor.encryptText(Constants.FIREBASE_ID_TOKEN, token)
        this.iSharedPrefService.storeIdToken(encryptedToken)
    }

}