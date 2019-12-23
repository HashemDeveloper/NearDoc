package com.project.neardoc

import android.content.Context
import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.work.*
import androidx.work.testing.SynchronousExecutor
import androidx.work.testing.WorkManagerTestInitHelper
import com.project.neardoc.utils.Constants
import com.project.neardoc.workers.DeleteUserInfoWorker
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class DeleteUserInfoWorkerTest {

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
    fun deleteUserInfoFromDb_success() {
        val dbKey: String = this.context.resources.getString(R.string.firebase_db_secret)
        val email = "juniayuli@outlook,com"

        val inputData: Data = Data.Builder()
            .putString(Constants.WORKER_DB_AUTH_KEY, dbKey)
            .putString(Constants.WORKER_EMAIL, email)
            .build()
        val request: OneTimeWorkRequest = OneTimeWorkRequest.Builder(DeleteUserInfoWorker::class.java)
            .setInputData(inputData)
            .build()
        val workManager: WorkManager = WorkManager.getInstance(this.context)
        workManager.enqueue(request).result.get()
        val workInfo: WorkInfo = workManager.getWorkInfoById(request.id).get()
        assertThat(workInfo.state, `is`(WorkInfo.State.SUCCEEDED))
    }

    @Test
    fun deleteUserInfo_Failed() {
        val request: OneTimeWorkRequest = OneTimeWorkRequest.Builder(DeleteUserInfoWorker::class.java)
            .build()
        val workManager: WorkManager = WorkManager.getInstance(this.context)
        workManager.enqueue(request).result.get()
        val workInfo: WorkInfo = workManager.getWorkInfoById(request.id).get()
        assertThat(workInfo.state, `is`(WorkInfo.State.FAILED))
    }
}