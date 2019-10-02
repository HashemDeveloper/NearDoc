package com.project.neardoc.data.local.remote

import com.project.neardoc.model.ResponseTokenModel
import com.project.neardoc.model.Username
import com.project.neardoc.model.Users
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
}