package com.project.neardoc.viewmodel

import android.util.Log
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.project.neardoc.R
import com.project.neardoc.data.local.remote.INearDocRemoteRepo
import com.project.neardoc.utils.Constants
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class RegistrationViewModel @Inject constructor(): ViewModel() {
    private val compositeDisposable = CompositeDisposable()
    private val loadingLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private val errorLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private val errorMessageLiveData: MutableLiveData<String> = MutableLiveData()
    private val successLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private val usernameExistsLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private val usernameLiveData: MutableLiveData<String> = MutableLiveData()
    @Inject
    lateinit var iNearDocRemoteRepo: INearDocRemoteRepo

    override fun onCleared() {
        super.onCleared()
        this.compositeDisposable.clear()
    }

    fun processRegistration(activity: FragmentActivity?, fullName: String, username: String, email: String, password: String) {
        this.compositeDisposable.add(this.iNearDocRemoteRepo.signUpWithEmailAndPassword(Constants.FIREBASE_AUTH_SIGN_UP_ENDPOINT, activity?.resources!!.getString(R.string.firebase_web_key),
            email, password, true)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({response ->
               Log.i("IdToken: ", response.idToken)
                this.loadingLiveData.value = false
                this.errorLiveData.value = false
                this.successLiveData.value = true
                //TODO: save user info into firebase using worker

            }, {onError ->
                Log.i("RegistrationError: ", onError.localizedMessage!!)
                this.loadingLiveData.value = false
                this.errorLiveData.value = true
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

    fun getLoadingLiveData(): LiveData<Boolean> {
        return this.loadingLiveData
    }
    fun getErrorLiveData(): LiveData<Boolean> {
        return this.errorLiveData
    }
    fun getErrorMessageLiveData(): LiveData<String> {
        return this.errorMessageLiveData
    }
    fun getSuccessLiveData(): LiveData<Boolean> {
        return this.successLiveData
    }
    fun getUsernameExistsLiveData(): LiveData<Boolean> {
        return this.usernameExistsLiveData
    }
    fun getUsernameLiveData(): LiveData<String> {
        return usernameLiveData
    }
}