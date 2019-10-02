package com.project.neardoc.viewmodel

import android.util.Log
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.project.neardoc.R
import com.project.neardoc.data.local.remote.INearDocRemoteApi
import com.project.neardoc.model.Username
import com.project.neardoc.model.Users
import com.project.neardoc.rxauth.IRxAuthentication
import com.project.neardoc.utils.Constants
import com.project.neardoc.viewmodel.listeners.ILoginViewModel
import com.project.neardoc.worker.LoginWorker
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class LoginViewModel @Inject constructor() : ViewModel() {
    private val loadingLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private val errorLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private val loginSuccessLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private var iLoginViewModel: ILoginViewModel? = null
    @Inject
    lateinit var iRxAuthentication: IRxAuthentication

    private val compositeDisposable = CompositeDisposable()
    private val firebaseAuth = FirebaseAuth.getInstance()

    override fun onCleared() {
        super.onCleared()
        this.compositeDisposable.clear()
    }

    fun processLoginWithGoogle(activity: FragmentActivity?, accountIfo: GoogleSignInAccount) {
        this.iLoginViewModel?.onLoginActionPerformed()
        this.loadingLiveData.value = true
        val authCredential: AuthCredential =
            GoogleAuthProvider.getCredential(accountIfo.idToken, null)
       this.compositeDisposable.add(this.iRxAuthentication.googleSignIn(activity!!, this.firebaseAuth, authCredential)
           .subscribeOn(Schedulers.io())
           .observeOn(AndroidSchedulers.mainThread())
           .subscribe({firebaseUser ->
               this.loadingLiveData.value = false
               this.errorLiveData.value = false
               this.loginSuccessLiveData.value = true
               processGoogleLoginData(activity, firebaseUser)
           }, {onError ->
               this.loadingLiveData.value = false
               this.errorLiveData.value = true
           }))
    }
    private fun processGoogleLoginData(activity: FragmentActivity?, firebaseUser: FirebaseUser) {
        val imagePath: String = firebaseUser.photoUrl.toString()
        val displayName: String = firebaseUser.displayName!!
        val email: String = firebaseUser.email!!
        val dbAuthKey: String = activity?.resources!!.getString(R.string.firebase_db_secret)

        val credentialData = Data.Builder()
            .putString(Constants.WORKER_DB_AUTH_KEY, dbAuthKey)
            .putString(Constants.WORKER_IMAGE_PATH, imagePath)
            .putString(Constants.WORKER_EMAIL, email)
            .putString(Constants.WORKER_DISPLAY_NAME, displayName)
            .build()
        val saveUserDataOnLoginRequest: OneTimeWorkRequest = OneTimeWorkRequest.Builder(LoginWorker::class.java)
            .setInputData(credentialData)
            .build()
        val workerManager: WorkManager = WorkManager.getInstance(activity)
            workerManager.beginWith(saveUserDataOnLoginRequest).enqueue()
    }
    fun setLoginViewModelListener(iLoginViewModel: ILoginViewModel) {
        this.iLoginViewModel = iLoginViewModel
    }
    fun removeLoginViewModelListener(listener: ILoginViewModel) {
        this.iLoginViewModel = listener
        if (iLoginViewModel != null) {
            this.iLoginViewModel = null
        }
    }
    fun getLoadingLiveData(): LiveData<Boolean> {
        return this.loadingLiveData
    }

    fun getErrorLiveData(): LiveData<Boolean> {
        return errorLiveData
    }

    fun getLoginSuccessLiveData(): LiveData<Boolean> {
        return loginSuccessLiveData
    }
}