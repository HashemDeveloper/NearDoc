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
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class LoginWorker @Inject constructor(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    @Inject
    lateinit var iNearDocRemoteRepo: INearDocRemoteRepo
    private val compositeDisposable = CompositeDisposable()

    override fun doWork(): Result {
        NearDocWorkerInjection.inject(this)
        val imagePath: String = inputData.getString(Constants.WORKER_IMAGE_PATH)!!
        val displayName: String = inputData.getString(Constants.WORKER_DISPLAY_NAME)!!
        val email: String = inputData.getString(Constants.WORKER_EMAIL)!!
        val dbKey: String = inputData.getString(Constants.WORKER_DB_AUTH_KEY)!!
        val usernameModel = Username(displayName)
        var isSaveSuccess = false
        val encodedEmail = Constants.encodeUserEmail(email)
        this.compositeDisposable.add(this.iNearDocRemoteRepo.storeUsername(displayName, dbKey, usernameModel)
            .subscribeOn(Schedulers.io())
            .subscribeWith(object : DisposableObserver<Username>() {
                override fun onComplete() {
                    val users = Users(displayName, encodedEmail, imagePath, true, Constants.SIGN_IN_PROVIDER_GOOGLE)
                   compositeDisposable.add(iNearDocRemoteRepo.storeUsersInfo(encodedEmail, dbKey, users)
                       .subscribeOn(Schedulers.io())
                       .subscribeWith(object : DisposableObserver<Users>() {
                           override fun onComplete() {
                               isSaveSuccess = true
                           }

                           override fun onNext(t: Users) {
                           }

                           override fun onError(e: Throwable) {
                               Log.i("onError: ", e.localizedMessage!!)
                           }
                       }))
                }

                override fun onNext(t: Username) {

                }

                override fun onError(e: Throwable) {
                    Log.i("onError: ", e.localizedMessage!!)
                }
            }))
        if (isSaveSuccess) {
            return Result.success()
        } else {
            return Result.retry()
        }
    }
}