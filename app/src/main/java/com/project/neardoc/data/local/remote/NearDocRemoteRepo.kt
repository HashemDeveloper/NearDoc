package com.project.neardoc.data.local.remote

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
}