package com.project.neardoc.worker

import android.content.Context
import android.util.Base64
import android.util.Log
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.firebase.auth.*
import com.project.neardoc.data.local.ISharedPrefService
import com.project.neardoc.data.local.remote.INearDocRemoteRepo
import com.project.neardoc.di.workermanager.NearDocWorkerInjection
import com.project.neardoc.model.Users
import com.project.neardoc.utils.Constants
import com.project.neardoc.utils.DeCryptor
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.lang.Exception
import java.util.concurrent.CountDownLatch
import javax.inject.Inject

class UpdateEmailWorker @Inject constructor(context: Context, workerParameters: WorkerParameters) :
    Worker(context, workerParameters) {
    @Inject
    lateinit var iSharedPrefService: ISharedPrefService
    @Inject
    lateinit var iNearDocRemoteRepo: INearDocRemoteRepo
    private val compositeDisposable = CompositeDisposable()
    private val countDownLatch: CountDownLatch = CountDownLatch(1)
    private var authCredential: AuthCredential?= null

    override fun doWork(): Result {
        NearDocWorkerInjection.inject(this)
        var isSuccess: Boolean? = null
        var message: String? = ""
        val outputData: Data?
        var isGoogleProvider: Boolean? = false
        try {
            val key: String = inputData.getString(Constants.WORKER_WEB_KEY)!!
            val newEmail: String = inputData.getString(Constants.WORKER_NEW_EMAIL)!!
            val currentEmail: String = inputData.getString(Constants.WORKER_EMAIL)!!
            val password: String = inputData.getString(Constants.WORKER_PASSWORD)!!
            val loginProvider: String = this.iSharedPrefService.getLoginProvider()
            if (loginProvider == Constants.SIGN_IN_PROVIDER_GOOGLE) {
                val deCryptor = DeCryptor()
                val encryptedIdToken: String = this.iSharedPrefService.getGoogleTokenId()
                val encryptIv: String = this.iSharedPrefService.getGoogleTokenEncryptIv()
                val byteArrayIdToken = Base64.decode(encryptedIdToken, Base64.DEFAULT)
                val byteArrayEncryptIv = Base64.decode(encryptIv, Base64.DEFAULT)
                val idToken = deCryptor.decryptData(Constants.GOOGLE_ID_TOKEN, byteArrayIdToken, byteArrayEncryptIv)
                this.authCredential = GoogleAuthProvider.getCredential(idToken, null)
                isGoogleProvider = true
            } else {
                this.authCredential = EmailAuthProvider.getCredential(currentEmail, password)
            }

            val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
            val user: FirebaseUser = firebaseAuth.currentUser!!
            user.reauthenticate(this.authCredential!!).addOnSuccessListener { onSuccess ->
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