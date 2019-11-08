package com.project.neardoc

import android.content.Context
import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.work.*
import androidx.work.testing.SynchronousExecutor
import androidx.work.testing.WorkManagerTestInitHelper
import com.project.neardoc.utils.Constants
import com.project.neardoc.worker.UpdateEmailWorker
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class UpdateEmailWorkerSuccessTest {

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
    fun updateEmail_success() {
        val webKey: String = this.context.resources.getString(R.string.firebase_web_key)
        val newEmail = "juniayulia@outlook.com"
        val currentEmail = "junia.montana@yahoo.com"
        val password = "Junia.yulia1"

        val inputData: Data = Data.Builder()
            .putString(Constants.WORKER_WEB_KEY, webKey)
            .putString(Constants.WORKER_NEW_EMAIL, newEmail)
            .putString(Constants.WORKER_EMAIL, currentEmail)
            .putString(Constants.WORKER_PASSWORD, password)
            .build()
        val updateEmailRequest: OneTimeWorkRequest = OneTimeWorkRequest.Builder(UpdateEmailWorker::class.java)
            .setInputData(inputData)
            .build()
        val workManager: WorkManager = WorkManager.getInstance(this.context)
        workManager.enqueue(updateEmailRequest).result.get()
        val workInfo: WorkInfo = workManager.getWorkInfoById(updateEmailRequest.id).get()
        MatcherAssert.assertThat(workInfo.state, CoreMatchers.`is`(WorkInfo.State.SUCCEEDED))
    }
}