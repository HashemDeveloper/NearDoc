package com.project.neardoc.data.local.remote

import com.project.neardoc.model.*
import io.reactivex.Observable
import retrofit2.http.*

interface INearDocRemoteApi {
    @Headers("Content-Type: application/json")
    @POST
    fun signInWithCustomToken(@Query("key") webKey: String, @Query("token") token: String, @Query("returnSecureToken") secureToken: Boolean): Observable<ResponseTokenModel>

    @Headers("Content-Type: application/json")
    @PUT("/usernames/{username}/.json")
    fun saveUsernameInFirebaseDb(@Path("username") username: String, @Query("auth") dbKey: String, @Body usernameModel: Username): Observable<Username>

    @Headers("Content-Type: application/json")
    @PUT("/users/{email}/.json")
    fun saveUserInfoInFirebaseDb(@Path("email") email: String, @Query("auth") dbKey: String, @Body users: Users): Observable<Users>

    @Headers("Content-Type: application/json")
    @POST
    fun signUpWithEmailAndPassword(
        @Url url: String, @Query("key") webKey: String, @Query("email") email: String, @Query(
            "password"
        ) password: String, @Query("returnSecureToken") returnSecureToken: Boolean
    ): Observable<RegistrationRes>

    @Headers("Content-Type: application/json")
    @POST
    fun sendEmailVerificationLink(@Url url: String, @Query("requestType") requestType: String, @Query("idToken") idToken: String): Observable<EmailVerificationRes>
}