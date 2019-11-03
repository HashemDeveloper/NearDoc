package com.project.neardoc.worker

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.project.neardoc.data.local.remote.INearDocRemoteRepo
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class UpdatePasswordWorker @Inject constructor(context: Context, workerParameters: WorkerParameters): Worker(context, workerParameters) {
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    @Inject
    lateinit var iNearDocRemoteRepo: INearDocRemoteRepo

    override fun doWork(): Result {

        return Result.success()
    }

    override fun onStopped() {
        super.onStopped()
    }
}