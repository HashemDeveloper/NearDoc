package com.project.neardoc.rxauth

import androidx.fragment.app.FragmentActivity
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import io.reactivex.Observable

interface IRxAuthentication {
    fun googleSignIn(activity: FragmentActivity, firebaseAuth: FirebaseAuth, authCredential: AuthCredential) : Observable<FirebaseUser>
}