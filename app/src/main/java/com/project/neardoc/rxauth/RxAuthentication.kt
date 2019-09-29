package com.project.neardoc.rxauth

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import io.reactivex.Observable
import javax.inject.Inject

class RxAuthentication @Inject constructor(): IRxAuthentication {
    private val isLoading: MutableLiveData<Boolean> = MutableLiveData()
    private val isError: MutableLiveData<Boolean> = MutableLiveData()
    private val isLoginSuccess: MutableLiveData<Boolean> = MutableLiveData()

    override fun googleSignIn(
        activity: FragmentActivity,
        firebaseAuth: FirebaseAuth,
        authCredential: AuthCredential
    ): Observable<FirebaseUser> {
        this.isLoading.value = true
        return Observable.create { emitter ->
            firebaseAuth.signInWithCredential(authCredential).addOnCompleteListener(activity) {
                if (it.isSuccessful) {
                    val firebaseUser = firebaseAuth.currentUser
                    this.isError.value = false
                    this.isLoading.value = false
                    this.isLoginSuccess.value = true
                    emitter.onNext(firebaseUser!!)
                }
            }.addOnFailureListener{
                this.isError.value = true
                this.isLoading.value = false
                this.isLoginSuccess.value = false
                emitter.onError(it)
            }
        }
    }
}