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
        disease: String,
        sort: String
    ): Observable<BetterDocSearchByDiseaseRes> {
        return this.iNearDocRemoteApi.searchDocByDisease(url, userKey, limit, location, disease, sort)
    }
    override fun getKnownConditions(
        url: String,
        userKey: String,
        limit: Int
    ): Observable<KnownConditionRes> {
       return this.iNearDocRemoteApi.retrieveKnownConditions(url, userKey, limit)
    }
    override fun getUsers(email: String, dbKey: String): Observable<UsersRes> {
        return this.iNearDocRemoteApi.getUsers(email, dbKey)
    }
    override fun updateEmail(
        url: String,
        key: String,
        idToken: String,
        email: String,
        returnSecureToken: Boolean
    ): Observable<UpdateLoginInfoRes> {
        return this.iNearDocRemoteApi.updateUserEmail(url, key, idToken, email, returnSecureToken)
    }

    override fun updatePassword(
        url: String,
        key: String,
        idToken: String,
        password: String,
        returnSecureToken: Boolean
    ): Observable<UpdateLoginInfoRes> {
       return this.iNearDocRemoteApi.updateUserPassword(url, key, idToken, password, returnSecureToken)
    }
}