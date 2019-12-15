package com.project.neardoc.worker

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.project.neardoc.BuildConfig
import com.project.neardoc.broadcast.NearDocBroadcastReceiver
import com.project.neardoc.di.workermanager.NearDocWorkerInjection
import com.project.neardoc.utils.Constants
import javax.inject.Inject

class StepCountNotificationWorker @Inject constructor(
    val context: Context,
    workerParameters: WorkerParameters
) : Worker(context, workerParameters) {

    companion object {
        @JvmStatic val TAG: String = StepCountNotificationWorker::class.java.canonicalName!!
    }

    override fun doWork(): Result {
        NearDocWorkerInjection.inject(this)
        val data: Int = inputData.getInt(Constants.STEP_COUNT_VALUE, 0)
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "CaloriesBurned $data")
        }
        val broadCastIntent: Intent = Intent(this.context, NearDocBroadcastReceiver::class.java)
            .putExtra(Constants.STEP_COUNT_VALUE, data)
            .setAction(Constants.STEP_COUNTER_SERVICE_ACTION)
        this.context.sendBroadcast(broadCastIntent)
        return Result.success()
    }
}