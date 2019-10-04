package com.project.neardoc.viewmodel

import android.util.Log
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.project.neardoc.R
import com.project.neardoc.data.local.remote.INearDocRemoteRepo
import com.project.neardoc.events.EmailVerificationEvent
import com.project.neardoc.rxeventbus.IRxEventBus
import com.project.neardoc.utils.Constants
import com.project.neardoc.viewmodel.listeners.IRegistrationViewModel
import com.project.neardoc.worker.RegistrationWorker
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

class RegistrationViewModel @Inject constructor(): ViewModel() {
    private val compositeDisposable = CompositeDisposable()
    private val loadingLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private val errorLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private val errorMessageLiveData: MutableLiveData<String> = MutableLiveData()
    private val successLiveData: MutableLiveData<String> = MutableLiveData()
    private val usernameExistsLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private val usernameLiveData: MutableLiveData<String> = MutableLiveData()
    @Inject
    lateinit var iNearDocRemoteRepo: INearDocRemoteRepo
    private var iRegistrationViewModel: IRegistrationViewModel?= null
    @Inject
    lateinit var iRxEventBus: IRxEventBus
    private var verificationEmail: String?= null
    override fun onCleared() {
        super.onCleared()
        this.compositeDisposable.clear()
    }

    fun sendEmailVerificationLink(activity: FragmentActivity?, idToken: String?) {
        this.compositeDisposable.add(this.iNearDocRemoteRepo.sendEmailVerification(Constants.FIREBASE_AUTH_EMAIL_VERIFICATION_ENDPOINT, Constants.FIREBASE_EMAIL_VERFICATION_REQUEST_TYPE,
            idToken!!, activity?.resources!!.getString(R.string.firebase_web_key))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({response ->
                Log.i("Email: ", response.email)
                this.verificationEmail = response.email
                this.iRegistrationViewModel?.onEmailVerificationSent(true)
                this.loadingLiveData.value = false
            }, {onError ->
                this.loadingLiveData.value = false
                this.errorLiveData.value = true
                Log.i("Error: ", onError.localizedMessage!!)
            }))
    }

    fun processRegistration(activity: FragmentActivity?, fullName: String, username: String, email: String, password: String) {
        this.compositeDisposable.add(this.iNearDocRemoteRepo.signUpWithEmailAndPassword(Constants.FIREBASE_AUTH_SIGN_UP_ENDPOINT, activity?.resources!!.getString(R.string.firebase_web_key),
            email, password, true)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({response ->
                this.errorLiveData.value = false
                this.successLiveData.value = response.idToken
                val data = Data.Builder()
                    .putString(Constants.WORKER_FULL_NAME, fullName)
                    .putString(Constants.WORKER_DISPLAY_NAME, username)
                    .putString(Constants.WORKER_EMAIL, email)
                    .putString(Constants.WORKER_DB_AUTH_KEY, activity.resources.getString(R.string.firebase_db_secret))
                    .build()
                val oneTimeWorkRequest = OneTimeWorkRequest.Builder(RegistrationWorker::class.java)
                    .setInputData(data)
                    .build()
                val workerManager = WorkManager.getInstance(activity)
                workerManager.beginWith(oneTimeWorkRequest).enqueue()
            }, {onError ->
                Log.i("RegistrationError: ", onError.localizedMessage!!)
                this.loadingLiveData.value = false
                this.errorLiveData.value = true
                this.errorMessageLiveData.value = onError.localizedMessage!!
            }))
    }

    fun checkIfUsernameExists(activity: FragmentActivity?, username: String) {
        this.loadingLiveData.value = true
        this.compositeDisposable.add(this.iNearDocRemoteRepo.getUsernames(username, activity?.resources!!.getString(R.string.firebase_db_secret))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({response ->
                if (response.username == username) {
                    this.loadingLiveData.value = false
                    this.usernameExistsLiveData.value = true
                    this.usernameLiveData.value = response.username
                }
            }, {onError ->
                Log.i("GetUsernameErr: ", onError.localizedMessage!!)
                if (onError.localizedMessage!! == "Null is not a valid element") {
                    this.usernameExistsLiveData.value = false
                }
            }))
    }
    fun setListener(iRegistrationViewModel: IRegistrationViewModel) {
        this.iRegistrationViewModel = iRegistrationViewModel
    }
    fun getLoadingLiveData(): LiveData<Boolean> {
        return this.loadingLiveData
    }
    fun getErrorLiveData(): LiveData<Boolean> {
        return this.errorLiveData
    }
    fun getErrorMessageLiveData(): LiveData<String> {
        return this.errorMessageLiveData
    }
    fun getSuccessLiveData(): LiveData<String> {
        return this.successLiveData
    }
    fun getUsernameExistsLiveData(): LiveData<Boolean> {
        return this.usernameExistsLiveData
    }
    fun getUsernameLiveData(): LiveData<String> {
        return usernameLiveData
    }

    fun notifyEmailSent() {
        if (this.verificationEmail != null && !this.verificationEmail.isNullOrEmpty()) {
            this.iRxEventBus.post(EmailVerificationEvent(this.verificationEmail!!))
        }
    }
}