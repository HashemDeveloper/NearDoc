package com.project.neardoc.rxauth

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import io.reactivex.Observable
import javax.inject.Inject

class RxAuthentication @Inject constructor() : IRxAuthentication {

    override fun appSignIn(
        activity: FragmentActivity,
        firebaseAuth: FirebaseAuth,
        email: String,
        password: String
    ): Observable<FirebaseUser> {
        return Observable.create { emitter ->
            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity) {
                    if (it.isSuccessful) {
                        val firebaseUser = firebaseAuth.currentUser
                        emitter.onNext(firebaseUser!!)
                    }
                }.addOnFailureListener {
                emitter.onError(it)
            }
        }
    }

    override fun googleSignIn(
        activity: FragmentActivity,
        firebaseAuth: FirebaseAuth,
        authCredential: AuthCredential
    ): Observable<FirebaseUser> {
        return Observable.create { emitter ->
            firebaseAuth.signInWithCredential(authCredential).addOnCompleteListener(activity) {
                if (it.isSuccessful) {
                    val firebaseUser = firebaseAuth.currentUser
                    emitter.onNext(firebaseUser!!)
                }
            }.addOnFailureListener {
                emitter.onError(it)
            }
        }
    }
}