package com.project.neardoc.worker

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
import java.util.concurrent.CountDownLatch
import javax.inject.Inject

class DeleteUsernameWorker @Inject constructor(context: Context, workerParameters: WorkerParameters): Worker(context, workerParameters) {

    @Inject
    lateinit var iSharedPreferences: ISharedPrefService
    @Inject
    lateinit var iNearDocRemoteRepo: INearDocRemoteRepo
    private val countDownLatch: CountDownLatch = CountDownLatch(1)
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    override fun doWork(): Result {
        NearDocWorkerInjection.inject(this)
        val username: String = this.iSharedPreferences.getUserUsername()
        val dbKey: String = inputData.getString(Constants.WORKER_DB_AUTH_KEY)!!
        var isSuccess: Boolean? = false
        var message: String? = ""
        val outPutData: Data?
        try {
            this.compositeDisposable.add(this.iNearDocRemoteRepo.deleteUsername(username, dbKey)
                .subscribeOn(Schedulers.io())
                .subscribe({deleted ->
                    isSuccess = true
                    this.countDownLatch.countDown()
                }, {onError ->
                    isSuccess = false
                    this.countDownLatch.countDown()
                    if (onError.localizedMessage != null) {
                        Log.i("DeleteUsernameErr: ", onError.localizedMessage!!)
                        message = onError.localizedMessage!!
                    }
                }))
            try {
                this.countDownLatch.await()
            } catch (interruptEx: InterruptedException) {
                if (interruptEx.localizedMessage != null) {
                    Log.i("InterruptEx: ", interruptEx.localizedMessage!!)
                }
            }
            outPutData = createWorkerData(message!!)
            return if (isSuccess!!) {
                Result.success()
            } else {
                Result.failure(outPutData)
            }
        } catch (ex: Exception) {
            if (ex.localizedMessage != null) {
                Log.i("Exception: ", ex.localizedMessage!!)
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