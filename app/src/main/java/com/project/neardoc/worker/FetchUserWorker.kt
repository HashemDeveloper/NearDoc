package com.project.neardoc.worker

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.project.neardoc.data.local.ISharedPrefService
import com.project.neardoc.data.local.remote.INearDocRemoteRepo
import com.project.neardoc.di.workermanager.NearDocWorkerInjection
import com.project.neardoc.model.Users
import com.project.neardoc.utils.Constants
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class FetchUserWorker @Inject constructor(context: Context, workerParams: WorkerParameters): Worker(context, workerParams) {
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    @Inject
    lateinit var iNearDocRemoteRepo: INearDocRemoteRepo
    @Inject
    lateinit var iSharedPreferences: ISharedPrefService

    override fun doWork(): Result {
        NearDocWorkerInjection.inject(this)
        val dbKey: String = inputData.getString(Constants.WORKER_DB_AUTH_KEY)!!
        val email: String = inputData.getString(Constants.WORKER_EMAIL)!!
        this.compositeDisposable.add(this.iNearDocRemoteRepo.getUsers(Constants.encodeUserEmail(email), dbKey)
            .subscribeOn(Schedulers.io())
            .subscribe({users ->
                if (users != null) {
                    val fullName: String = users.fullName
                    val userEmail: String = users.email
                    val image: String = users.image
                    val username: String = users.username
                    this.iSharedPreferences.storeUserName(fullName)
                    this.iSharedPreferences.storeUserEmail(userEmail)
                    this.iSharedPreferences.storeUserImage(image)
                    this.iSharedPreferences.storeUserUsername(username)
                } else {
                    //TODO: Prompt user to create profile
                }
            }, {onError ->
                Log.i("GetUserError: ", onError.localizedMessage!!)
            }))
        return Result.success()
    }

    override fun onStopped() {
        super.onStopped()
        this.compositeDisposable.clear()
    }
}