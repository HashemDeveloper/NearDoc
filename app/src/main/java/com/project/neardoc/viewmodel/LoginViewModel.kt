package com.project.neardoc.viewmodel

import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.project.neardoc.rxauth.IRxAuthentication
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class LoginViewModel @Inject constructor(): ViewModel() {
    @Inject
    lateinit var googleSignInClient: GoogleSignInClient
    @Inject
    lateinit var iRxAuthentication: IRxAuthentication

    private val compositeDisposable = CompositeDisposable()

    override fun onCleared() {
        super.onCleared()
        this.compositeDisposable.clear()
    }
}