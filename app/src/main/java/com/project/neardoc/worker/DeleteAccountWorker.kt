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
import com.project.neardoc.utils.Constants
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

    override fun doWork(): Result {
        NearDocWorkerInjection.inject(this)
        var isSuccess: Boolean? = false
        var message: String? = ""
        val outPutData: Data?
        try {
            val authKey: String = inputData.getString(Constants.WORKER_WEB_KEY)!!
            val email: String = inputData.getString(Constants.WORKER_EMAIL)!!
            val password: String = inputData.getString(Constants.WORKER_PASSWORD)!!

            val authCredential: AuthCredential = EmailAuthProvider.getCredential(email, password)
            val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
            val user: FirebaseUser = firebaseAuth.currentUser!!
            user.reauthenticateAndRetrieveData(authCredential)
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