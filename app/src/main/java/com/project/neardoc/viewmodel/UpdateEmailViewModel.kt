package com.project.neardoc.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.project.neardoc.R
import com.project.neardoc.data.local.remote.INearDocRemoteRepo
import com.project.neardoc.utils.Constants
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class UpdateEmailViewModel @Inject constructor(): ViewModel() {
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    @Inject
    lateinit var iNearDocRemoteRepo: INearDocRemoteRepo
    @Inject
    lateinit var context: Context

    fun processUpdateEmailRequest(email: String) {
        val key: String = this.context.resources!!.getString(R.string.firebase_web_key)
        val currentUser: FirebaseUser = FirebaseAuth.getInstance().currentUser!!
        var idToken = ""
        currentUser.getIdToken(true).addOnSuccessListener { onSuccess ->
            idToken = onSuccess.token!!
            Log.i("Token: ", idToken)
        }.addOnFailureListener { onFailed ->
            Log.i("TokenFailed: ", onFailed.localizedMessage!!)
        }
        this.compositeDisposable.add(this.iNearDocRemoteRepo.updateEmail(Constants.FIREBASE_AUTH_UPDATE_LOGIN_INFO_END_POINT,
            key, "", email, true)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({onResponse ->

            }, {onError ->

            }))

    }
    override fun onCleared() {
        super.onCleared()
        this.compositeDisposable.clear()
    }
}