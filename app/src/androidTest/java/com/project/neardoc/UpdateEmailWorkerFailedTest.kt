package com.project.neardoc

import android.content.Context
import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.work.*
import androidx.work.testing.SynchronousExecutor
import androidx.work.testing.WorkManagerTestInitHelper
import com.project.neardoc.utils.Constants
import com.project.neardoc.workers.UpdateEmailWorker
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class UpdateEmailWorkerFailedTest {

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
    fun updateEmail_failed_wrongPassword() {
        val webKey: String = this.context.resources.getString(R.string.firebase_web_key)
        val newEmail = "juniayulia@outlook.com"
        val currentEmail = "junia.montana@yahoo.com"
        val password = "Junia.yulia132"

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
        val outputData: Data = workInfo.outputData
        val message: String = outputData.getString(Constants.WORKER_ERROR_DATA)!!
        assertThat(workInfo.state, `is`(WorkInfo.State.FAILED))
        assertThat(message, `is`("The password is invalid or the user does not have a password."))
    }

    @Test
    fun updateEmail_Failed_emptyInputData() {
        val updateEmailRequest: OneTimeWorkRequest = OneTimeWorkRequest.Builder(UpdateEmailWorker::class.java)
            .build()
        val workManager: WorkManager = WorkManager.getInstance(this.context)
        workManager.enqueue(updateEmailRequest).result.get()
        val workInfo: WorkInfo = workManager.getWorkInfoById(updateEmailRequest.id).get()
        assertThat(workInfo.state, `is`(WorkInfo.State.FAILED))
    }
}