package com.project.neardoc.viewmodel

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.project.neardoc.rxauth.IRxAuthentication
import com.project.neardoc.viewmodel.listeners.ILoginViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
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
        this.loadingLiveData.postValue(true)
        val authCredential: AuthCredential =
            GoogleAuthProvider.getCredential(accountIfo.idToken, null)
        this.compositeDisposable.add(this.iRxAuthentication.googleSignIn(
            activity!!,
            this.firebaseAuth,
            authCredential
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableObserver<FirebaseUser>() {
                override fun onComplete() {
                    loadingLiveData.postValue(false)
                    errorLiveData.postValue(false)
                }

                override fun onNext(t: FirebaseUser) {

                }

                override fun onError(e: Throwable) {
                    loadingLiveData.postValue(false)
                    errorLiveData.postValue(true)
                }
            })
        )
    }
    fun setLoginViewModelListener(iLoginViewModel: ILoginViewModel) {
        this.iLoginViewModel = iLoginViewModel
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