package com.project.neardoc.worker

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.project.neardoc.data.local.ISharedPrefService
import com.project.neardoc.di.workermanager.NearDocWorkerInjection
import com.project.neardoc.utils.Constants
import javax.inject.Inject

class StepCountNotificationWorker @Inject constructor(
    context: Context,
    workerParameters: WorkerParameters
) : Worker(context, workerParameters) {
    @Inject
    lateinit var iSharedPreferences: ISharedPrefService

    override fun doWork(): Result {
        NearDocWorkerInjection.inject(this)
        val stepsCount: Int = this.iSharedPreferences.getLastStepCountValue()
        Log.i("Counts: ", stepsCount.toString())
        val data: Data = Data.Builder()
            .putInt(Constants.STEP_COUNT_VALUE, stepsCount)
            .build()
        return Result.success(data)
    }
}