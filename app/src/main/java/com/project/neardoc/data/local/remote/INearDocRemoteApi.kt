package com.project.neardoc.data.local.remote

import com.project.neardoc.model.*
import com.project.neardoc.model.insuranceplanproviders.InsuranceAndPlans
import com.project.neardoc.view.settings.UpdateEmail
import io.reactivex.Observable
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.*

interface INearDocRemoteApi {
    @Headers("Content-Type: application/json")
    @POST
    fun signInWithCustomToken(@Query("auth") apiKey: String, @Query("token") token: String, @Query("returnSecureToken") secureToken: Boolean): Observable<ResponseTokenModel>

    @Headers("Content-Type: application/json")
    @PUT("/usernames/{username}/.json")
    fun saveUsernameInFirebaseDb(@Path("username") username: String, @Query("auth") dbKey: String, @Body usernameModel: Username): Observable<Username>
    @Headers("Content-Type: application/json")
    @DELETE("/usernames/{node}/.json")
    fun deleteUsername(@Path("node") username: String, @Query("auth") dbKey: String): Observable<Void>
    @Headers("Content-Type: application/json")
    @PUT("/users/{email}/.json")
    fun saveUserInfoInFirebaseDb(@Path("email") email: String, @Query("auth") dbKey: String, @Body users: Users): Observable<Users>
    @Headers("Content-Type: application/json")
    @DELETE("/users/{email}/.json")
    fun deleteUserInfoFromFirebaseDb(@Path("email") email: String, @Query("auth") dbKey: String): Observable<Void>
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
    fun retrieveKnownConditions(@Url url: String, @Query("user_key") userKey: String, @Query("limit") limit: Int): Observable<KnownConditionRes>

    @Headers("Content-Type: application/json")
    @GET
    fun getInsuranceProviderAndPlans(@Url url: String, @Query("user_key") userKey: String): Observable<InsuranceAndPlans>

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

    @Headers("Content-Type: application/json")
    @POST
    fun getFirebaseUserData(@Url url: String, @Query("key") key: String, @Query("idToken") idToken: String): Observable<CurrentUserRes>
    @Headers("Content-Type: application/json")
    @POST
    fun deleteUserAccount(@Url url: String, @Query("key") webApiKey: String, @Query("idToken") idToken: String): Observable<Void>

    @Headers("Content-Type: application/json")
    @GET
    suspend fun getBetterDocApiHealthKtx(@Url url: String, @Query("user_key") userKey: String): Response<BetterDocApiHealthRes>
    @Headers("Content-Type: application/json")
    @GET
    suspend fun searchDocByDiseaseKtx(@Url url: String, @Query("user_key") userKey: String, @Query("limit")
    limit: Int, @Query("location") location: String, @Query("query") disease: String, @Query("sort") sort: String): Response<BetterDocSearchByDiseaseRes>

}