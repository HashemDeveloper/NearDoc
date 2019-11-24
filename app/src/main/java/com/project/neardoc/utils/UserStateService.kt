package com.project.neardoc.utils

import android.content.Context
import android.content.IntentFilter
import androidx.lifecycle.LiveData
import com.google.firebase.auth.FirebaseAuth
import com.project.neardoc.broadcast.NearDocBroadcastReceiver
import javax.inject.Inject

class UserStateService @Inject constructor(val context: Context): LiveData<FirebaseAuth>(), IUserStateService, FirebaseAuth.AuthStateListener {
    private val firebaseAuth = FirebaseAuth.getInstance()
    init {
        this.firebaseAuth.addAuthStateListener(this)
    }
    @Inject
    lateinit var nearDocBroadcastReceiver: NearDocBroadcastReceiver

    override fun onActive() {
        super.onActive()
        this.context.registerReceiver(this.nearDocBroadcastReceiver, IntentFilter(Constants.USER_STATE_ACTION))
    }

    override fun onInactive() {
        super.onInactive()
        this.context.unregisterReceiver(this.nearDocBroadcastReceiver)
    }

    override fun getAuthObserver(): UserStateService{
        return this
    }
    override fun onAuthStateChanged(userAuth: FirebaseAuth) {
        postValue(userAuth)
    }
}