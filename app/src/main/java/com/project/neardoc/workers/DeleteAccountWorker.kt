package com.project.neardoc.workers

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
import com.project.neardoc.utils.Constants
import com.project.neardoc.utils.DeCryptor
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.lang.Exception
import java.util.concurrent.CountDownLatch
import javax.inject.Inject

class DeleteAccountWorker @Inject constructor(context: Context, workerParameters: WorkerParameters): Worker(context, workerParameters) {
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private val countDownLatch: CountDownLatch = CountDownLatch(1)
    @Inject
    lateinit var iNearDocRemoteRepo: INearDocRemoteRepo
    @Inject
    lateinit var iSharedPrefService: ISharedPrefService
    private var authCredential: AuthCredential?= null

    override fun doWork(): Result {
        NearDocWorkerInjection.inject(this)
        var isSuccess: Boolean? = false
        var message: String? = ""
        val outPutData: Data?
        try {
            val authKey: String = inputData.getString(Constants.WORKER_WEB_KEY)!!
            val email: String = inputData.getString(Constants.WORKER_EMAIL)!!
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
            } else if (loginProvider == Constants.SIGN_IN_PROVIDER_FIREBASE) {
                this.authCredential = EmailAuthProvider.getCredential(email, password)
            }
            val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
            val user: FirebaseUser = firebaseAuth.currentUser!!
            user.reauthenticateAndRetrieveData(this.authCredential!!)
                .addOnSuccessListener {
                   it.user!!.getIdToken(true)
                       .addOnSuccessListener {token ->
                           val idToken: String = token.token!!
                           this.compositeDisposable.add(this.iNearDocRemoteRepo.deleteUserAccount(Constants.FIREBASE_DELETE_ACOUNT,
                               authKey, idToken)
                               .subscribeOn(Schedulers.io())
                               .subscribe({onDelete ->
                                   isSuccess = true
                                   this.countDownLatch.countDown()
                               }, {onDeleteError ->
                                   if (onDeleteError.localizedMessage != null) {
                                       Log.i("DeleteAccountError: ", onDeleteError.localizedMessage!!)
                                       message = onDeleteError.localizedMessage!!
                                       this.countDownLatch.countDown()
                                   }
                               }))
                       }.addOnFailureListener { tokenException ->
                           if (tokenException.localizedMessage != null) {
                               Log.i("RetrieveTokenEx: ", tokenException.localizedMessage!!)
                               message = tokenException.localizedMessage!!
                               this.countDownLatch.countDown()
                           }
                       }
                }.addOnFailureListener { reAuthError ->
                    if (reAuthError.localizedMessage != null) {
                        Log.i("ReAuthError: ", reAuthError.localizedMessage!!)
                        message = reAuthError.localizedMessage!!
                        this.countDownLatch.countDown()
                    }
                }
            try {
                this.countDownLatch.await()
            } catch (ex: InterruptedException) {
                if (ex.localizedMessage != null) {
                    Log.i("InterruptedEx: ", ex.localizedMessage!!)
                }
            }
            outPutData = createWorkerData(message!!)
            return if (isSuccess!!) {
                Result.success()
            } else {
                Result.failure(outPutData)
            }
        } catch (e: Exception) {
            if (e.localizedMessage != null) {
                Log.i("DeleteAccountException", e.localizedMessage!!)
                message = e.localizedMessage!!
                this.countDownLatch.countDown()
            }
            return Result.failure()
        }
    }

    override fun onStopped() {
        super.onStopped()
        this.compositeDisposable.clear()
    }
    private fun createWorkerData(errorMessage: String): Data {
        return Data.Builder()
            .putString(Constants.WORKER_ERROR_DATA, errorMessage)
            .build()
    }
}