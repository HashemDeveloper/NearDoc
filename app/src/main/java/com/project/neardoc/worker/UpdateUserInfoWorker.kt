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

class UpdateUserInfoWorker @Inject constructor(context: Context, workerParameters: WorkerParameters): Worker(context, workerParameters) {
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    @Inject
    lateinit var iSharedPrefService: ISharedPrefService
    @Inject
    lateinit var iNearDocRemoteRepo: INearDocRemoteRepo

    override fun doWork(): Result {
        NearDocWorkerInjection.inject(this)
        val dbKey: String = inputData.getString(Constants.WORKER_DB_AUTH_KEY)!!
        val username: String = inputData.getString(Constants.WORKER_DISPLAY_NAME)!!
        val userImage: String = inputData.getString(Constants.WORKER_IMAGE_PATH)!!
        val newEmail: String = inputData.getString(Constants.WORKER_EMAIL)!!
        val oldEmail: String = inputData.getString(Constants.WORKER_OLD_EMAIL)!!
        val fullName: String = inputData.getString(Constants.WORKER_FULL_NAME)!!
        val user = Users(fullName, username, newEmail, userImage, true, Constants.SIGN_IN_PROVIDER_FIREBASE)

        this.compositeDisposable.add(this.iNearDocRemoteRepo.deleteUser(Constants.encodeUserEmail(oldEmail), dbKey)
            .subscribeOn(Schedulers.io())
            .subscribe({void ->
                Log.i("OldInfo: ", "Deleted")
            }, {onDeleteError ->
                Log.i("OnDeleteError: ", onDeleteError.localizedMessage!!)
                Result.retry()
            }))
        this.compositeDisposable.add(this.iNearDocRemoteRepo.storeUsersInfo(Constants.encodeUserEmail(newEmail), dbKey, user)
            .subscribeOn(Schedulers.io())
            .subscribe({userResponse ->
                if (userResponse != null) {
                    this.iSharedPrefService.storeUserName(userResponse.fullName)
                    this.iSharedPrefService.storeUserUsername(userResponse.username)
                    this.iSharedPrefService.storeUserEmail(userResponse.email)
                    this.iSharedPrefService.storeUserImage(userResponse.image)
                }
            }, {saveUserError ->
                Log.i("SaveUserError: ", saveUserError.localizedMessage!!)
            }))
        return Result.success()
    }

    override fun onStopped() {
        super.onStopped()
        this.compositeDisposable.clear()
    }
}