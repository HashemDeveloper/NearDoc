package com.project.neardoc.viewmodel

import android.content.Context
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
import com.google.firebase.auth.*
import com.project.neardoc.R
import com.project.neardoc.data.local.ISharedPrefService
import com.project.neardoc.data.local.remote.INearDocRemoteRepo
import com.project.neardoc.rxauth.IRxAuthentication
import com.project.neardoc.utils.Constants
import com.project.neardoc.utils.DeCryptor
import com.project.neardoc.utils.EnCryptor
import com.project.neardoc.viewmodel.listeners.ILoginViewModel
import com.project.neardoc.worker.FetchUserWorker
import com.project.neardoc.worker.LoginWorker
import com.project.neardoc.worker.UpdateUserInfoWorker
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class LoginViewModel @Inject constructor() : ViewModel() {
    private val loadingLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private val errorLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private val loginSuccessLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private val emailVerificationRequireLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private val errorMessageLiveData: MutableLiveData<String> = MutableLiveData()
    private var iLoginViewModel: ILoginViewModel? = null
    private val emailVerificationSentLiveData: MutableLiveData<String> = MutableLiveData()
    @Inject
    lateinit var iRxAuthentication: IRxAuthentication
    @Inject
    lateinit var iSharedPrefService: ISharedPrefService
    @Inject
    lateinit var iNearDocRemoteRepo: INearDocRemoteRepo
    @Inject
    lateinit var context: Context
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
        val tokenId: String = accountIfo.idToken!!
        val enCrypter = EnCryptor()
        val encryptedIdToken = enCrypter.encryptText(Constants.GOOGLE_ID_TOKEN, tokenId)
        this.iSharedPrefService.storeGoogleIdToken(encryptedIdToken)
        this.iSharedPrefService.storeGoogleEncryptIv(enCrypter.iv)
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
               Log.i("GoogleLoginErr: ", onError.localizedMessage!!)
           }))
    }
    private fun processGoogleLoginData(activity: FragmentActivity?, firebaseUser: FirebaseUser, fullName: String) {
        val imagePath: String = firebaseUser.photoUrl.toString()
        val displayName: String = firebaseUser.displayName!!
        val email: String = firebaseUser.email!!
        val dbAuthKey: String = activity?.resources!!.getString(R.string.firebase_db_secret)
        this.iSharedPrefService.storeUserUsername(displayName)
        this.iSharedPrefService.storeUserName(fullName)
        this.iSharedPrefService.storeUserEmail(email)
        if (imagePath.isNotEmpty()) {
            this.iSharedPrefService.storeUserImage(imagePath)
        }
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
    fun getErrorMessageLiveData(): LiveData<String> {
        return this.errorMessageLiveData
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
                    val userEmail:String = firebaseUser.email!!
                    fetchUserInfoFromFirebaseDb(userEmail)
                } else {
                    this.emailVerificationRequireLiveData.value = true
                    this.loadingLiveData.value = false
                    this.loginSuccessLiveData.value = false
                    this.firebaseAuth.signOut()
                }
            }, {onError ->
                this.loadingLiveData.value = false
                this.errorLiveData.value = false
                this.errorMessageLiveData.value = onError.localizedMessage!!
                Log.i("AppLoginError: ", onError.localizedMessage!!)
                Log.i("Error: ", onError.message!!)
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
                this.iLoginViewModel?.onEmailVerificationSent()
            }, {onError ->
                this.iLoginViewModel?.onEmailVerificationSent()
                Log.i("ErrorSendingEmail:", onError.localizedMessage!!)
            }))
    }
    private fun fetchUserInfoFromFirebaseDb(newEmail: String) {
        val fullName: String = this.iSharedPrefService.getUserName()
        val username: String = this.iSharedPrefService.getUserUsername()
        val oldEmail: String = this.iSharedPrefService.getUserEmail()
        val dbKey: String = this.context.resources!!.getString(R.string.firebase_db_secret)
        if (fullName.isEmpty() || fullName == ""
            && username.isEmpty() || username == ""
            && oldEmail.isEmpty() || oldEmail == "") {
            fetchUserInfoProcessInBg(dbKey, newEmail)
        }
    }
    private fun fetchUserInfoProcessInBg(dbKey: String, newEmail: String) {
        val keyData: Data = Data.Builder()
            .putString(Constants.WORKER_DB_AUTH_KEY, dbKey)
            .putString(Constants.WORKER_EMAIL, newEmail)
            .build()
        val request: OneTimeWorkRequest = OneTimeWorkRequest.Builder(FetchUserWorker::class.java)
            .setInputData(keyData)
            .build()
        val workManager: WorkManager = WorkManager.getInstance(this.context)
        workManager.beginWith(request).enqueue()
    }
    private fun updateUserInfoProcessInBg(dbKey: String, newEmail: String, oldEmail: String, userImage: String, fullName: String, username: String) {
        val keyData: Data = Data.Builder()
            .putString(Constants.WORKER_DB_AUTH_KEY, dbKey)
            .putString(Constants.WORKER_EMAIL, newEmail)
            .putString(Constants.WORKER_IMAGE_PATH, userImage)
            .putString(Constants.WORKER_FULL_NAME, fullName)
            .putString(Constants.WORKER_DISPLAY_NAME, username)
            .putString(Constants.WORKER_OLD_EMAIL, oldEmail)
            .build()
        val request: OneTimeWorkRequest = OneTimeWorkRequest.Builder(UpdateUserInfoWorker::class.java)
            .setInputData(keyData)
            .build()
        val workManager: WorkManager = WorkManager.getInstance(this.context)
        workManager.beginWith(request).enqueue()
    }
}