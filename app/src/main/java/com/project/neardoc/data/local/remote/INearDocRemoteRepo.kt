package com.project.neardoc.data.local.remote

import com.project.neardoc.model.EmailVerificationRes
import com.project.neardoc.model.RegistrationRes
import com.project.neardoc.model.Username
import com.project.neardoc.model.Users
import io.reactivex.Observable

interface INearDocRemoteRepo {
    fun storeUsername(username: String, dbKey: String, usernameModel: Username) : Observable<Username>
    fun storeUsersInfo(email: String, dbKey: String, users: Users): Observable<Users>
    fun signUpWithEmailAndPassword(url: String, webKey: String, email: String, password: String, returnSecureToken: Boolean): Observable<RegistrationRes>
    fun sendEmailVerification(url: String, requestType: String, idToken: String): Observable<EmailVerificationRes>
}