package com.project.neardoc.worker

import android.content.Context
import android.util.Log
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.project.neardoc.data.local.remote.INearDocRemoteRepo
import com.project.neardoc.di.workermanager.NearDocWorkerInjection
import com.project.neardoc.utils.Constants
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.lang.Exception
import javax.inject.Inject

class UpdateEmailWorker @Inject constructor(context: Context, workerParameters: WorkerParameters): Worker(context, workerParameters) {

    @Inject
    lateinit var iNearDocRemoteRepo: INearDocRemoteRepo
    private val compositeDisposable = CompositeDisposable()

    override fun doWork(): Result {
        NearDocWorkerInjection.inject(this)
        var outputData: Data?= null
        return try {
            val key: String = inputData.getString(Constants.WORKER_WEB_KEY)!!
            val newEmail: String = inputData.getString(Constants.WORKER_NEW_EMAIL)!!
            val currentEmail: String = inputData.getString(Constants.WORKER_EMAIL)!!
            val password: String = inputData.getString(Constants.WORKER_PASSWORD)!!

            val authCredential: AuthCredential =
                EmailAuthProvider.getCredential(currentEmail, password)
            val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
            val user: FirebaseUser = firebaseAuth.currentUser!!
            user.reauthenticate(authCredential).addOnSuccessListener { onSuccess ->
                user.getIdToken(true).addOnSuccessListener {
                    this.compositeDisposable.add(this.iNearDocRemoteRepo.updateEmail(
                        Constants.FIREBASE_AUTH_UPDATE_LOGIN_INFO_END_POINT,
                        key, it.token!!, newEmail, true
                    )
                        .subscribeOn(Schedulers.io())
                        .subscribe({ response ->
                            this.compositeDisposable.add(this.iNearDocRemoteRepo.sendEmailVerification(
                                Constants.FIREBASE_AUTH_EMAIL_VERIFICATION_ENDPOINT,
                                Constants.FIREBASE_EMAIL_VERFICATION_REQUEST_TYPE,
                                response.idToken,
                                key
                            )
                                .subscribeOn(Schedulers.io())
                                .subscribe({ onEmailSent ->
                                    Log.i("Sent: ", onEmailSent.email)
                                }, { onSentError ->
                                    Log.i(
                                        "FailedToSendEmail: ",
                                        onSentError.localizedMessage!!)
                                })
                            )
                        }, { onError ->
                            Log.i("EmailUpdateError: ", onError.localizedMessage!!)
                        })
                    )
                }.addOnFailureListener {
                    Log.i("FailedToGetToken: ", it.localizedMessage!!)
                }
            }.addOnFailureListener { onFailed ->
                Log.i("ReAuthenticationFailed:", onFailed.localizedMessage!!)
            }
            Result.success(outputData!!)
        } catch (ex: Exception) {
            Log.i("Ex: ", ex.localizedMessage!!)
            return Result.failure(outputData!!)
        }
    }
    override fun onStopped() {
        super.onStopped()
        this.compositeDisposable.clear()
    }
}