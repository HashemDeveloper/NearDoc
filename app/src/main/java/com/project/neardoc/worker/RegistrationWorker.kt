package com.project.neardoc.worker

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.project.neardoc.data.local.remote.INearDocRemoteRepo
import com.project.neardoc.di.workermanager.NearDocWorkerInjection
import com.project.neardoc.model.Username
import com.project.neardoc.model.Users
import com.project.neardoc.utils.Constants
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class RegistrationWorker @Inject constructor(context: Context, workerParameters: WorkerParameters): Worker(context, workerParameters){

    @Inject
    lateinit var iNearDocRemoteRepo: INearDocRemoteRepo
    private val compositeDisposable = CompositeDisposable()

    override fun doWork(): Result {
        NearDocWorkerInjection.inject(this)
        var isSaveSuccess = false
        val displayName: String = inputData.getString(Constants.WORKER_DISPLAY_NAME)!!
        val fullName: String = inputData.getString(Constants.WORKER_FULL_NAME)!!
        val email: String = inputData.getString(Constants.WORKER_EMAIL)!!
        val dbKey: String = inputData.getString(Constants.WORKER_DB_AUTH_KEY)!!
        val encodedEmail = Constants.encodeUserEmail(email)
        val usernameModel = Username(displayName)

        this.compositeDisposable.add(this.iNearDocRemoteRepo.storeUsername(displayName, dbKey, usernameModel)
            .subscribeOn(Schedulers.io())
            .subscribe({username ->
                val users = Users(fullName, displayName, email, "", false, Constants.SIGN_IN_PROVIDER_FIREBASE)
                this.compositeDisposable.add(this.iNearDocRemoteRepo.storeUsersInfo(encodedEmail, dbKey, users)
                    .subscribeOn(Schedulers.io())
                    .subscribe({userRes ->
                        isSaveSuccess = true
                    }, {onUserErr ->
                        Log.i("OnUserErr: ", onUserErr.localizedMessage!!)
                        isSaveSuccess = false
                    }))
            }, {onUsernameErr ->
                Log.i("OnUsernameErr: ", onUsernameErr.localizedMessage!!)
                isSaveSuccess = false
            }))

        return if (isSaveSuccess) {
            Result.success()
        } else {
            Result.retry()
        }
    }

}