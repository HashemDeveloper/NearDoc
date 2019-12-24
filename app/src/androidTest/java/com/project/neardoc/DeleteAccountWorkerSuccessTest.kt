package com.project.neardoc

import android.content.Context
import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.work.*
import androidx.work.testing.SynchronousExecutor
import androidx.work.testing.WorkManagerTestInitHelper
import com.project.neardoc.utils.Constants
import com.project.neardoc.workers.DeleteAccountWorker
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class DeleteAccountWorkerSuccessTest {

    private lateinit var context: Context

    @Before
    fun setup() {
        this.context = ApplicationProvider.getApplicationContext<Context>()!!
        val config: Configuration = Configuration.Builder()
            .setMinimumLoggingLevel(Log.DEBUG)
            .setExecutor(SynchronousExecutor())
            .build()
        WorkManagerTestInitHelper.initializeTestWorkManager(this.context, config)
    }

    @Test
    fun deleteAccount_appProvider_success() {
        val authKey: String = this.context.resources.getString(R.string.firebase_web_key)
        val email = "juniayulia@outlook.com"
        val password = "Junia.yulia1"

        val inputData: Data = Data.Builder()
            .putString(Constants.WORKER_WEB_KEY, authKey)
            .putString(Constants.WORKER_EMAIL, email)
            .putString(Constants.WORKER_PASSWORD, password)
            .build()
        val request: OneTimeWorkRequest = OneTimeWorkRequest.Builder(DeleteAccountWorker::class.java)
            .setInputData(inputData)
            .build()
        val workManager: WorkManager = WorkManager.getInstance(this.context)
        workManager.enqueue(request).result.get()

        val workInfo: WorkInfo = workManager.getWorkInfoById(request.id).get()
        MatcherAssert.assertThat(workInfo.state, Matchers.`is`(WorkInfo.State.SUCCEEDED))
    }
}