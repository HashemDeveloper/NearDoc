package com.project.neardoc.data.local.remote

import com.project.neardoc.model.*
import com.project.neardoc.view.settings.UpdateEmail
import io.reactivex.Observable
import retrofit2.http.*

interface INearDocRemoteApi {
    @Headers("Content-Type: application/json")
    @POST
    fun signInWithCustomToken(@Query("auth") apiKey: String, @Query("token") token: String, @Query("returnSecureToken") secureToken: Boolean): Observable<ResponseTokenModel>

    @Headers("Content-Type: application/json")
    @PUT("/usernames/{email}/.json")
    fun saveUsernameInFirebaseDb(@Path("email") username: String, @Query("auth") dbKey: String, @Body usernameModel: Username): Observable<Username>

    @Headers("Content-Type: application/json")
    @PUT("/users/{email}/.json")
    fun saveUserInfoInFirebaseDb(@Path("email") email: String, @Query("auth") dbKey: String, @Body users: Users): Observable<Users>

    @Headers("Content-Type: application/json")
    @POST
    fun signUpWithEmailAndPassword(
        @Url url: String, @Query("key") apiKey: String, @Query("email") email: String, @Query(
            "password"
        ) password: String, @Query("returnSecureToken") returnSecureToken: Boolean
    ): Observable<RegistrationRes>

    @Headers("Content-Type: application/json")
    @POST
    fun sendEmailVerificationLink(
        @Url url: String, @Query("requestType") requestType: String, @Query(
            "idToken"
        ) idToken: String, @Query("key") apiKey: String
    ): Observable<EmailVerificationRes>

    @Headers("Content-Type: application/json")
    @GET("/usernames/{node}/.json")
    fun getUsernames(@Path("node") username: String, @Query("key") dbKey: String): Observable<UsernameRes>

    @Headers("Content-Type: application/json")
    @GET("/users/{node}/.json")
    fun getUsers(@Path("node") email: String, @Query("auth") dbKey: String): Observable<UsersRes>

    @Headers("Content-Type: application/json")
    @POST
    fun sendPasswordResetLink(
        @Url url: String, @Query("key") apiKey: String, @Query("requestType") requestType: String, @Query(
            "email"
        ) email: String
    ): Observable<PasswordResetRes>

    @Headers("Content-Type: application/json")
    @GET
    fun getBetterDocApiHealth(@Url url: String, @Query("user_key") userKey: String): Observable<BetterDocApiHealthRes>

    @Headers("Content-Type: application/json")
    @GET
    fun searchDocByDisease(
        @Url url: String, @Query("user_key") userKey: String, @Query("limit") limit: Int, @Query(
            "location"
        ) location: String, @Query("query") disease: String, @Query("sort") sort: String
    ): Observable<BetterDocSearchByDiseaseRes>

    @Headers("Content-Type: application/json")
    @GET
    fun retrieveKnownConditions(@Url url: String, @Query("user_key") userKey: String, @Query("limit") limit: Int): Observable<KnownConditionRes>

    @Headers("Content-Type: application/json")
    @POST
    fun updateUserEmail(
        @Url url: String, @Query("key") key: String, @Query("idToken") idToken: String, @Query("email") email: String, @Query(
            "returnSecureToken"
        ) returnSecureToken: Boolean
    ): Observable<UpdateLoginInfoRes>

    @Headers("Content-Type: application/json")
    @POST
    fun updateUserPassword(
        @Url url: String, @Query("key") key: String, @Query("idToken") idToken: String, @Query("password") password: String, @Query(
            "returnSecureToken"
        ) returnSecureToken: Boolean
    ): Observable<UpdateLoginInfoRes>
}