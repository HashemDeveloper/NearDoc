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
import com.project.neardoc.data.local.ISharedPrefService
import com.project.neardoc.data.local.remote.INearDocRemoteRepo
import com.project.neardoc.di.workermanager.NearDocWorkerInjection
import com.project.neardoc.model.Users
import com.project.neardoc.utils.Constants
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.lang.Exception
import java.util.concurrent.CountDownLatch
import javax.inject.Inject

class UpdateEmailWorker @Inject constructor(context: Context, workerParameters: WorkerParameters) :
    Worker(context, workerParameters) {
    @Inject
    lateinit var iNearDocRemoteRepo: INearDocRemoteRepo
    private val compositeDisposable = CompositeDisposable()
    private val countDownLatch: CountDownLatch = CountDownLatch(1)

    override fun doWork(): Result {
        NearDocWorkerInjection.inject(this)
        var isSuccess: Boolean? = null
        var message: String? = ""
        val outputData: Data?
        try {
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
                    this.compositeDisposable.add(
                        this.iNearDocRemoteRepo.updateEmail(
                            Constants.FIREBASE_AUTH_UPDATE_LOGIN_INFO_END_POINT,
                            key, it.token!!, newEmail, true
                        )
                            .subscribeOn(Schedulers.io())
                            .subscribe({ updateEmailRes ->
                                this.compositeDisposable.add(
                                    this.iNearDocRemoteRepo.sendEmailVerification(
                                        Constants.FIREBASE_AUTH_EMAIL_VERIFICATION_ENDPOINT,
                                        Constants.FIREBASE_EMAIL_VERFICATION_REQUEST_TYPE,
                                        updateEmailRes.idToken,
                                        key
                                    )
                                        .subscribeOn(Schedulers.io())
                                        .subscribe({ emailSentRes ->
                                            isSuccess = true
                                            this.countDownLatch.countDown()
                                            Log.i("Sent: ", emailSentRes.email)
                                        }, { onSentError ->
                                            isSuccess = false
                                            message = onSentError.localizedMessage!!
                                            this.countDownLatch.countDown()
                                            Log.i(
                                                "FailedToSendEmail: ",
                                                onSentError.localizedMessage!!
                                            )
                                        })
                                )
                            }, { onError ->
                                isSuccess = false
                                message = onError.localizedMessage!!
                                this.countDownLatch.countDown()
                                Log.i("EmailUpdateError: ", onError.localizedMessage!!)
                            })
                    )
                }.addOnFailureListener {
                    isSuccess = false
                    message = it.localizedMessage!!
                    this.countDownLatch.countDown()
                    Log.i("FailedToGetToken: ", it.localizedMessage!!)
                }
            }.addOnFailureListener { onFailed ->
                isSuccess = false
                message = onFailed.localizedMessage!!
                this.countDownLatch.countDown()
                Log.i("ReAuthenticationFailed:", onFailed.localizedMessage!!)
            }

            try {
                this.countDownLatch.await()
            } catch (interruptEx: InterruptedException) {
                Log.i("InterruptedEx: ", interruptEx.localizedMessage!!)
            }
            outputData = createErrorData(message!!)
            return if (isSuccess!!) {
                Result.success()
            } else {
                Result.failure(outputData)
            }

        } catch (ex: Exception) {
            return Result.failure()
        }
    }

    override fun onStopped() {
        super.onStopped()
        this.compositeDisposable.clear()
    }

    private fun createErrorData(errorMessage: String): Data {
        return Data.Builder()
            .putString(Constants.WORKER_ERROR_DATA, errorMessage)
            .build()
    }
}