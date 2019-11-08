package com.project.neardoc

import android.content.Context
import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.work.*
import androidx.work.impl.utils.SynchronousExecutor
import androidx.work.testing.WorkManagerTestInitHelper
import com.project.neardoc.utils.Constants
import com.project.neardoc.worker.DeleteAccountWorker
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class DeleteAccountWorkerWrongPassTest {
    private lateinit var context: Context

    @Before
    fun setup() {
        this.context = ApplicationProvider.getApplicationContext<Context>()!!
        val configuration: Configuration = Configuration.Builder()
            .setMinimumLoggingLevel(Log.DEBUG)
            .setExecutor(SynchronousExecutor())
            .build()
        WorkManagerTestInitHelper.initializeTestWorkManager(this.context, configuration)
    }

    @Test
    fun deleteAccount_appProvider_failedWrongPassword() {
        val authKey: String = this.context.resources.getString(R.string.firebase_web_key)
        val email = "juniayulia@outlook.com"
        val password = "Junia.yulia123"

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
        val outputData: Data = workInfo.outputData
        val message: String = outputData.getString(Constants.WORKER_ERROR_DATA)!!

        assertThat(workInfo.state, `is`(WorkInfo.State.FAILED))
        assertThat(message, `is`("The password is invalid or the user does not have a password."))
    }
}