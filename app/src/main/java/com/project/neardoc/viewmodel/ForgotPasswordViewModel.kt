package com.project.neardoc.viewmodel

import android.util.Log
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.project.neardoc.R
import com.project.neardoc.data.local.remote.INearDocRemoteRepo
import com.project.neardoc.utils.Constants
import com.project.neardoc.viewmodel.listeners.IForgotPasswordViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.json.JSONException
import org.json.JSONObject
import javax.inject.Inject

class ForgotPasswordViewModel @Inject constructor() : ViewModel() {
    @Inject
    lateinit var iNearDocRemoteRepo: INearDocRemoteRepo
    private val loadingLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private val errorLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private val sentSuccessLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private val compositeDisposable = CompositeDisposable()
    private var iPasswordViewModel: IForgotPasswordViewModel? = null

    override fun onCleared() {
        super.onCleared()
        this.compositeDisposable.clear()
    }

    fun sendPasswordResetLink(activity: FragmentActivity, email: String) {
        this.iPasswordViewModel?.onPasswordResetLinkProcessed()
        this.loadingLiveData.value = true
        this.compositeDisposable.add(this.iNearDocRemoteRepo.sendPasswordResetLink(
            Constants.FIREBASE_AUTH_PASSWORD_RESET_ENDPOINT,
            activity.resources.getString(R.string.firebase_web_key),
            Constants.FIREBASE_AUTH_PASSWORD_RESET_REQUEST_TYPE,
            email
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ res ->
                Log.i("Res: ", res.email)
                this.loadingLiveData.value = false
                this.sentSuccessLiveData.value = true
            }, { onError ->
                this.sentSuccessLiveData.value = false
                this.loadingLiveData.value = false
                this.errorLiveData.value = true
                Log.i("Error: ", onError.localizedMessage!!)
            })
        )
    }

    fun getIsLoadingLiveData(): LiveData<Boolean> {
        return this.loadingLiveData
    }

    fun getIsErrorLiveData(): LiveData<Boolean> {
        return this.errorLiveData
    }

    fun getIsSuccessLiveData(): LiveData<Boolean> {
        return this.sentSuccessLiveData
    }

    fun registerListener(listener: IForgotPasswordViewModel) {
        this.iPasswordViewModel = listener
    }
}