package com.project.neardoc.utils

import android.content.Context
import android.content.IntentFilter
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.google.firebase.auth.FirebaseAuth
import com.project.neardoc.broadcast.ConnectionBroadcastReceiver
import javax.inject.Inject

class UserStateService @Inject constructor(val context: Context): LiveData<FirebaseAuth>(), IUserStateService, FirebaseAuth.AuthStateListener {
    private val firebaseAuth = FirebaseAuth.getInstance()
    init {
        this.firebaseAuth.addAuthStateListener(this)
    }
    @Inject
    lateinit var connectionBroadcastReceiver: ConnectionBroadcastReceiver

    override fun onActive() {
        super.onActive()
        this.context.registerReceiver(this.connectionBroadcastReceiver, IntentFilter(Constants.USER_STATE_ACTION))
    }

    override fun onInactive() {
        super.onInactive()
        this.context.unregisterReceiver(this.connectionBroadcastReceiver)
    }

    override fun getAuthObserver(): UserStateService{
        return this
    }
    override fun onAuthStateChanged(userAuth: FirebaseAuth) {
        postValue(userAuth)
    }
}