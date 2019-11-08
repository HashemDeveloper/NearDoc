package com.project.neardoc

import android.content.Context
import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.work.*
import androidx.work.testing.SynchronousExecutor
import androidx.work.testing.WorkManagerTestInitHelper
import com.project.neardoc.utils.Constants
import com.project.neardoc.worker.UpdateUserInfoWorker
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class UpdateUserInfoWorkerTest {

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
    fun updateUserInfoTest() {
        val dbKey: String = this.context.resources.getString(R.string.firebase_db_secret)
        val oldEmail = "junia.montana@yahoo,com"
        val username = "juniayulia"
        val userImage = ""
        val fullName = "Hashem"
        val newEmail = "juniayulia@outlook,com"

        val updateUserInfoData: Data = Data.Builder()
            .putString(Constants.WORKER_DB_AUTH_KEY, dbKey)
            .putString(Constants.WORKER_EMAIL, newEmail)
            .putString(Constants.WORKER_IMAGE_PATH, userImage)
            .putString(Constants.WORKER_FULL_NAME, fullName)
            .putString(Constants.WORKER_DISPLAY_NAME, username)
            .putString(Constants.WORKER_OLD_EMAIL, oldEmail)
            .build()

        val updateUserInfoReq: OneTimeWorkRequest = OneTimeWorkRequest.Builder(UpdateUserInfoWorker::class.java)
            .setInputData(updateUserInfoData)
            .build()
        val workManager: WorkManager = WorkManager.getInstance(this.context)
        workManager.enqueue(updateUserInfoReq).result.get()
        val workInfo: WorkInfo = workManager.getWorkInfoById(updateUserInfoReq.id).get()
        assertThat(workInfo.state, `is`(WorkInfo.State.SUCCEEDED))
    }
}