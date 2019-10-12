package com.project.neardoc.utils

import android.content.Context
import android.content.IntentFilter
import androidx.lifecycle.LiveData
import com.google.firebase.auth.FirebaseAuth
import com.project.neardoc.broadcast.ConnectionBroadcastReceiver
import javax.inject.Inject

class UserStateService @Inject constructor(val context: Context): LiveData<FirebaseAuth>(), IUserStateService, FirebaseAuth.AuthStateListener {

    @Inject
    lateinit var connectionBroadcastReceiver: ConnectionBroadcastReceiver
    private var firebaseAuth: FirebaseAuth?= null

    init {
        this.firebaseAuth = FirebaseAuth.getInstance()
    }
    override fun onActive() {
        super.onActive()
        this.firebaseAuth?.addAuthStateListener(this)
        this.context.registerReceiver(this.connectionBroadcastReceiver, IntentFilter(Constants.USER_STATE_ACTION))
    }

    override fun onInactive() {
        super.onInactive()
        this.context.unregisterReceiver(this.connectionBroadcastReceiver)
    }

    override fun getObserver(): UserStateService{
        return this
    }
    override fun onAuthStateChanged(userAuth: FirebaseAuth) {
        postValue(userAuth)
    }
}