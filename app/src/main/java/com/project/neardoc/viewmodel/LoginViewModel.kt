package com.project.neardoc.viewmodel

import android.util.Base64
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
import com.project.neardoc.data.local.ISharedPrefService
import com.project.neardoc.data.local.remote.INearDocRemoteRepo
import com.project.neardoc.rxauth.IRxAuthentication
import com.project.neardoc.utils.Constants
import com.project.neardoc.utils.DeCryptor
import com.project.neardoc.utils.EnCryptor
import com.project.neardoc.viewmodel.listeners.ILoginViewModel
import com.project.neardoc.worker.LoginWorker
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.nio.charset.Charset
import javax.inject.Inject

class LoginViewModel @Inject constructor() : ViewModel() {
    private val loadingLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private val errorLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private val loginSuccessLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private val emailVerificationRequireLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private var iLoginViewModel: ILoginViewModel? = null
    @Inject
    lateinit var iRxAuthentication: IRxAuthentication
    @Inject
    lateinit var iSharedPrefService: ISharedPrefService
    @Inject
    lateinit var iNearDocRemoteRepo: INearDocRemoteRepo
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
        val fullName = accountIfo.givenName + " " + accountIfo.familyName
       this.compositeDisposable.add(this.iRxAuthentication.googleSignIn(activity!!, this.firebaseAuth, authCredential)
           .subscribeOn(Schedulers.io())
           .observeOn(AndroidSchedulers.mainThread())
           .subscribe({firebaseUser ->
               this.loadingLiveData.value = false
               this.errorLiveData.value = false
               this.loginSuccessLiveData.value = true
               processGoogleLoginData(activity, firebaseUser, fullName)
           }, {onError ->
               this.loadingLiveData.value = false
               this.errorLiveData.value = true
           }))
    }
    private fun processGoogleLoginData(activity: FragmentActivity?, firebaseUser: FirebaseUser, fullName: String) {
        val imagePath: String = firebaseUser.photoUrl.toString()
        val displayName: String = firebaseUser.displayName!!
        val email: String = firebaseUser.email!!
        val dbAuthKey: String = activity?.resources!!.getString(R.string.firebase_db_secret)

        val credentialData = Data.Builder()
            .putString(Constants.WORKER_DB_AUTH_KEY, dbAuthKey)
            .putString(Constants.WORKER_FULL_NAME, fullName)
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
    fun getIsEmailVerificationRequireLiveData(): LiveData<Boolean> {
        return this.emailVerificationRequireLiveData
    }

    fun processLoginWithApp(activity: FragmentActivity?, email: String, password: String) {
        this.iLoginViewModel?.onLoginActionPerformed()
        this.loadingLiveData.value = true
        this.compositeDisposable.add(this.iRxAuthentication.appSignIn(activity!!, this.firebaseAuth, email, password)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({firebaseUser ->
                if (firebaseUser.isEmailVerified) {
                    this.loginSuccessLiveData.value = true
                    this.emailVerificationRequireLiveData.value = false
                    this.errorLiveData.value = false
                    this.loadingLiveData.value = false
                } else {
                    this.emailVerificationRequireLiveData.value = true
                    this.loadingLiveData.value = false
                    this.loginSuccessLiveData.value = false
                    this.firebaseAuth.signOut()
                }
            }, {onError ->
                this.loadingLiveData.value = false
                this.errorLiveData.value = false
                Log.i("AppLoginError: ", onError.localizedMessage!!)
            }))
    }
    fun resendEmailEmailVerification(activity: FragmentActivity?) {
        val deCryptor = DeCryptor()
        val encryptedIdToken: String = this.iSharedPrefService.getIdToken()
        val encryptIv: String = this.iSharedPrefService.getEncryptIv()
        val byteArrayIdToken = Base64.decode(encryptedIdToken, Base64.DEFAULT)
        val byteArrayEncryptIv = Base64.decode(encryptIv, Base64.DEFAULT)
        val idToken = deCryptor.decryptData(Constants.FIREBASE_ID_TOKEN, byteArrayIdToken, byteArrayEncryptIv)
        this.compositeDisposable.add(this.iNearDocRemoteRepo.sendEmailVerification(Constants.FIREBASE_AUTH_EMAIL_VERIFICATION_ENDPOINT,
            Constants.FIREBASE_EMAIL_VERFICATION_REQUEST_TYPE, idToken, activity?.resources!!.getString(R.string.firebase_web_key))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({response ->
                Log.i("EmailSentTo: ", response.email)
                this.iLoginViewModel?.onEmailVerificationSent(response.email)
            }, {onError ->
                Log.i("ErrorSendingEmail:", onError.localizedMessage!!)
            }))
    }
}