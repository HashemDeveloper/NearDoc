package com.project.neardoc.workers

import android.content.Context
import android.util.Log
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.project.neardoc.data.local.ISharedPrefService
import com.project.neardoc.data.local.remote.INearDocRemoteRepo
import com.project.neardoc.di.workermanager.NearDocWorkerInjection
import com.project.neardoc.utils.Constants
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.lang.Exception
import java.util.concurrent.CountDownLatch
import javax.inject.Inject

class DeleteUserInfoWorker @Inject constructor(context: Context, workerParameters: WorkerParameters): Worker(context, workerParameters) {
    @Inject
    lateinit var iSharedPrefService: ISharedPrefService
    @Inject
    lateinit var iNearDocRemoteRepo: INearDocRemoteRepo
    private var compositeDisposable: CompositeDisposable = CompositeDisposable()
    private val countDownLatch: CountDownLatch = CountDownLatch(1)


    override fun doWork(): Result {
        NearDocWorkerInjection.inject(this)
        var isSuccess: Boolean? = false
        var message: String? = ""
        val outPutData: Data?
        try {
            val dbKey: String = inputData.getString(Constants.WORKER_DB_AUTH_KEY)!!
            val email: String = inputData.getString(Constants.WORKER_EMAIL)!!

            this.compositeDisposable.add(this.iNearDocRemoteRepo.deleteUserInfoFromFirebaseDb(Constants.encodeUserEmail(email),
                dbKey)
                .subscribeOn(Schedulers.io())
                .subscribe({r ->
                    isSuccess = true
                    this.iSharedPrefService.removeItems(Constants.SHARED_PREF_USER_IMAGE)
                    this.iSharedPrefService.removeItems(Constants.SHARED_PREF_USER_USERNAME)
                    this.iSharedPrefService.removeItems(Constants.SHARED_PREF_USER_EMAIL)
                    this.iSharedPrefService.removeItems(Constants.SHARED_PREF_USER_NAME)
                    this.countDownLatch.countDown()
                }, {onError ->
                    if (onError.localizedMessage != null) {
                        isSuccess = false
                        Log.i("DeleteFromDbErr: ", onError.localizedMessage!!)
                        message = onError.localizedMessage!!
                    }
                    this.countDownLatch.countDown()
                }))

            try {
                this.countDownLatch.await()
            } catch (interrupEx: InterruptedException) {
                if (interrupEx.localizedMessage != null) {
                    Log.i("InterruptedException: ", interrupEx.localizedMessage!!)
                }
            }
            outPutData = createWorkerData(message!!)
            return if (isSuccess!!) {
                Result.success()
            } else {
                Result.failure(outPutData)
            }
        } catch (ex: Exception) {
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