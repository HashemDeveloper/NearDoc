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
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executor
import javax.inject.Inject

class UpdatePasswordWorker @Inject constructor(context: Context, workerParameters: WorkerParameters): Worker(context, workerParameters) {
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    @Inject
    lateinit var iNearDocRemoteRepo: INearDocRemoteRepo
    private val countDownLatch: CountDownLatch = CountDownLatch(1)

    override fun doWork(): Result {
        NearDocWorkerInjection.inject(this)
        var isSuccess: Boolean?= false
        var errorMessage: String? = ""
        try {
            var errorData: Data?= null
            val key: String = inputData.getString(Constants.WORKER_WEB_KEY)!!
            val currentPass: String = inputData.getString(Constants.WORKER_PASSWORD)!!
            val newPass: String = inputData.getString(Constants.WORKER_NEW_PASS)!!
            val email: String = inputData.getString(Constants.WORKER_EMAIL)!!
            val authCredential: AuthCredential = EmailAuthProvider.getCredential(email, currentPass)
            val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
            val user: FirebaseUser = firebaseAuth.currentUser!!
            user.reauthenticateAndRetrieveData(authCredential)
                .addOnSuccessListener {onReAuthSuccess ->
                    onReAuthSuccess.user!!.getIdToken(true)
                        .addOnSuccessListener {
                            this.compositeDisposable.add(this.iNearDocRemoteRepo.updatePassword(Constants.FIREBASE_AUTH_UPDATE_LOGIN_INFO_END_POINT,
                                key, it.token!!, newPass, true)
                                .subscribeOn(Schedulers.io())
                                .subscribe({onResponse ->
                                    Log.i("Success: ", onResponse.email)
                                    isSuccess = true
                                    this.countDownLatch.countDown()
                                }, {onUpdatePassError ->
                                    Log.i("UpdatePassError: ", onUpdatePassError.localizedMessage!!)
                                    isSuccess = false
                                    errorMessage = onUpdatePassError.localizedMessage!!
                                    this.countDownLatch.countDown()
                                }))
                        }.addOnFailureListener { onTokenFailed ->
                            isSuccess = false
                            Log.i("TokenFailed: ", onTokenFailed.localizedMessage!!)
                            errorMessage = onTokenFailed.localizedMessage!!
                            this.countDownLatch.countDown()
                        }
                }.addOnFailureListener { onReAuthFailed ->
                    isSuccess = false
                    Log.i("ReAuthFailed: ", onReAuthFailed.localizedMessage!!)
                    errorMessage = onReAuthFailed.localizedMessage!!
                    errorData = createErrorData(errorMessage!!)
                    this.countDownLatch.countDown()
                }
            try {
                this.countDownLatch.await()
            } catch (interruptedEx: InterruptedException) {
                Log.i("Interrupted: ", interruptedEx.localizedMessage!!)
            }
            errorData = createErrorData(errorMessage!!)
            return if (isSuccess!!) {
                Result.success()
            } else {
                Result.failure(errorData!!)
            }
        } catch (ex: Exception) {
            return Result.failure()
        }
    }
    private fun createErrorData(errorMessage: String): Data {
        return Data.Builder()
            .putString(Constants.WORKER_ERROR_DATA, errorMessage)
            .build()
    }

    override fun onStopped() {
        super.onStopped()
        this.compositeDisposable.clear()
    }
}