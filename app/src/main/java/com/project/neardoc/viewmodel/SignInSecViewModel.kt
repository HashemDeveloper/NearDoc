package com.project.neardoc.viewmodel

import android.content.Context
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.project.neardoc.R
import com.project.neardoc.data.local.remote.INearDocRemoteRepo
import com.project.neardoc.utils.Constants
import com.project.neardoc.worker.DeleteAccountWorker
import javax.inject.Inject

class SignInSecViewModel @Inject constructor(): ViewModel() {
    @Inject
    lateinit var context: Context
    private var workerLiveData: LiveData<WorkInfo>?= null


    fun deleteAccount(email: String, password: String, activity: FragmentActivity) {
        val authKey: String = this.context.resources.getString(R.string.firebase_web_key)
        val data: Data = Data.Builder()
            .putString(Constants.WORKER_WEB_KEY, authKey)
            .putString(Constants.WORKER_EMAIL, email)
            .putString(Constants.WORKER_PASSWORD, password)
            .build()
        val request: OneTimeWorkRequest = OneTimeWorkRequest.Builder(DeleteAccountWorker::class.java)
            .setInputData(data)
            .build()
        val workManager: WorkManager = WorkManager.getInstance(this.context)
        workManager.beginWith(request).enqueue()

        this.workerLiveData = workManager.getWorkInfoByIdLiveData(request.id)
        this.workerLiveData?.observe(activity, getWorkerObserver())
    }

    private fun getWorkerObserver(): Observer<WorkInfo> {
        return Observer {
            if (it?.state == null) {
                return@Observer
            } else {
                when(it.state) {
                    WorkInfo.State.SUCCEEDED -> {

                    }
                    WorkInfo.State.FAILED -> {

                    }
                    else -> {
                        return@Observer
                    }
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        if (this.workerLiveData != null) {
            this.workerLiveData?.removeObserver(getWorkerObserver())
        }
    }

}