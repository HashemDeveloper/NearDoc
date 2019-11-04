package com.project.neardoc.data.local.remote

import com.project.neardoc.model.*
import io.reactivex.Observable

interface INearDocRemoteRepo {
    fun storeUsername(username: String, dbKey: String, usernameModel: Username) : Observable<Username>
    fun storeUsersInfo(email: String, dbKey: String, users: Users): Observable<Users>
    fun signUpWithEmailAndPassword(url: String, webKey: String, email: String, password: String, returnSecureToken: Boolean): Observable<RegistrationRes>
    fun sendEmailVerification(url: String, requestType: String, idToken: String, webKey: String): Observable<EmailVerificationRes>
    fun getUsernames(username: String, dbKey: String): Observable<UsernameRes>
    fun sendPasswordResetLink(url: String, apiKey: String, requestType: String, email: String): Observable<PasswordResetRes>
    fun checkBetterDocApiHealth(url: String, userKey: String): Observable<BetterDocApiHealthRes>
    fun searchDocByDisease(url: String, userKey: String, limit: Int, location: String, disease: String, sort: String): Observable<BetterDocSearchByDiseaseRes>
    fun getKnownConditions(url: String, userKey: String, limit: Int): Observable<KnownConditionRes>
    fun getUsers(email: String, dbKey: String): Observable<UsersRes>
    fun updateEmail(url: String, key: String, idToken: String, email: String, returnSecureToken: Boolean): Observable<UpdateLoginInfoRes>
    fun updatePassword(url: String, key: String, idToken: String, password: String, returnSecureToken: Boolean): Observable<UpdateLoginInfoRes>
    fun getFirebaseUserData(url: String, key: String, idToken: String): Observable<CurrentUserRes>
    fun deleteUserInfoFromFirebaseDb(email: String, authKey: String): Observable<Void>
    fun deleteUserAccount(url: String, webKey: String, idToken: String): Observable<Void>
}