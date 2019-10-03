package com.project.neardoc.data.local.remote

import com.project.neardoc.model.EmailVerificationRes
import com.project.neardoc.model.RegistrationRes
import com.project.neardoc.model.Username
import com.project.neardoc.model.Users
import io.reactivex.Observable
import javax.inject.Inject

class NearDocRemoteRepo @Inject constructor(): INearDocRemoteRepo {

    @Inject
    lateinit var iNearDocRemoteApi: INearDocRemoteApi

    override fun storeUsername(
        username: String,
        dbKey: String,
        usernameModel: Username
    ): Observable<Username> {
        return this.iNearDocRemoteApi.saveUsernameInFirebaseDb(username, dbKey, usernameModel)
    }

    override fun storeUsersInfo(email: String, dbKey: String, users: Users): Observable<Users> {
        return this.iNearDocRemoteApi.saveUserInfoInFirebaseDb(email, dbKey, users)
    }

    override fun signUpWithEmailAndPassword(
        url: String,
        webKey: String,
        email: String,
        password: String,
        returnSecureToken: Boolean
    ): Observable<RegistrationRes> {
       return this.iNearDocRemoteApi.signUpWithEmailAndPassword(url, webKey, email, password, returnSecureToken)
    }

    override fun sendEmailVerification(
        url: String,
        requestType: String,
        idToken: String
    ): Observable<EmailVerificationRes> {
        return this.iNearDocRemoteApi.sendEmailVerificationLink(url, requestType, idToken)
    }
}