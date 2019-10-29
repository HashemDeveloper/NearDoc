package com.project.neardoc.viewmodel

import android.content.Context
import android.util.Base64
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.project.neardoc.R
import com.project.neardoc.data.local.ISharedPrefService
import com.project.neardoc.data.local.remote.INearDocRemoteRepo
import com.project.neardoc.rxauth.IRxAuthentication
import com.project.neardoc.utils.Constants
import com.project.neardoc.utils.DeCryptor
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class UpdateEmailViewModel @Inject constructor(): ViewModel() {
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private val isLoadingLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private val isErrorLiveData: MutableLiveData<Boolean> = MutableLiveData()
    @Inject
    lateinit var iNearDocRemoteRepo: INearDocRemoteRepo
    @Inject
    lateinit var context: Context
    @Inject
    lateinit var iSharedPreferences: ISharedPrefService
    @Inject
    lateinit var iRxAuthentication: IRxAuthentication


    fun processUpdateEmailRequest(
        currentEmail: String,
        newEmail: String,
        password: String
    ) {
        val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
        val key: String = this.context.resources!!.getString(R.string.firebase_web_key)
        val idToken: String = getIdToken()
        val authCredential: AuthCredential = EmailAuthProvider.getCredential(currentEmail, password)
        firebaseAuth.currentUser?.reauthenticate(authCredential)!!.addOnSuccessListener { onSuccess ->
            Log.i("Success", "Success")
        }.addOnFailureListener { onFailed ->
            Log.i("FailedToReAuthenticate", onFailed.localizedMessage!!)
        }
    }
    private fun getIdToken(): String {
        val deCryptor = DeCryptor()
        val encryptedIdToken: String = this.iSharedPreferences.getIdToken()
        val encryptIv: String = this.iSharedPreferences.getEncryptIv()
        val byteArrayIdToken = Base64.decode(encryptedIdToken, Base64.DEFAULT)
        val byteArrayEncryptIv = Base64.decode(encryptIv, Base64.DEFAULT)
        val idToken = deCryptor.decryptData(Constants.FIREBASE_ID_TOKEN, byteArrayIdToken, byteArrayEncryptIv)
        return idToken
    }
    override fun onCleared() {
        super.onCleared()
        this.compositeDisposable.clear()
    }
}