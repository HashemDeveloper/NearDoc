package com.project.neardoc.data.local.remote

import com.project.neardoc.model.Username
import com.project.neardoc.model.Users
import io.reactivex.Observable

interface INearDocRemoteRepo {
    fun storeUsername(username: String, dbKey: String, usernameModel: Username) : Observable<Username>
    fun storeUsersInfo(email: String, dbKey: String, users: Users): Observable<Users>
}