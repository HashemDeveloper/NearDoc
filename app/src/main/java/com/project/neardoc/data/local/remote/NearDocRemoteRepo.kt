package com.project.neardoc.data.local.remote

import com.project.neardoc.model.*
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
        idToken: String,
        webKey: String
    ): Observable<EmailVerificationRes> {
        return this.iNearDocRemoteApi.sendEmailVerificationLink(url, requestType, idToken, webKey)
    }

    override fun getUsernames(username: String, dbKey: String): Observable<UsernameRes> {
        return this.iNearDocRemoteApi.getUsernames(username, dbKey)
    }
    override fun sendPasswordResetLink(
        url: String,
        apiKey: String,
        requestType: String,
        email: String
    ): Observable<PasswordResetRes> {
        return this.iNearDocRemoteApi.sendPasswordResetLink(url, apiKey, requestType, email)
    }
    override fun checkBetterDocApiHealth(
        url: String,
        userKey: String
    ): Observable<BetterDocApiHealthRes> {
        return this.iNearDocRemoteApi.getBetterDocApiHealth(url, userKey)
    }
    override fun searchDocByDisease(
        url: String,
        userKey: String,
        limit: Int,
        location: String,
        disease: String
    ): Observable<BetterDocSearchByDiseaseRes> {
        return this.iNearDocRemoteApi.searchDocByDisease(url, userKey, limit, location, disease)
    }
}