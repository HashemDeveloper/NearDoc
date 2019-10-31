package com.project.neardoc.viewmodel

import android.content.Context
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.project.neardoc.R
import com.project.neardoc.data.local.ISharedPrefService
import com.project.neardoc.data.local.remote.INearDocRemoteRepo
import com.project.neardoc.rxauth.IRxAuthentication
import com.project.neardoc.utils.Constants
import com.project.neardoc.viewmodel.listeners.UpdateEmailListener
import com.project.neardoc.worker.UpdateEmailWorker
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class UpdateEmailViewModel @Inject constructor(): ViewModel() {

    private var updateEmailListener: UpdateEmailListener?= null
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private val isLoadingLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private val isErrorLiveData: MutableLiveData<Boolean> = MutableLiveData()
    @Inject
    lateinit var iNearDocRemoteRepo: INearDocRemoteRepo
    @Inject
    lateinit var context: Context
    @Inject
    lateinit var iSharedPreferences: ISharedPrefService
    @Inject
    lateinit var iRxAuthentication: IRxAuthentication


    fun processUpdateEmailRequest(
        activity: FragmentActivity,
        currentEmail: String,
        newEmail: String,
        password: String
    ) {
        this.isLoadingLiveData.value = true
        val key: String = this.context.resources!!.getString(R.string.firebase_web_key)
        val keyData: Data = Data.Builder()
            .putString(Constants.WORKER_WEB_KEY, key)
            .putString(Constants.WORKER_NEW_EMAIL, newEmail)
            .putString(Constants.WORKER_EMAIL, currentEmail)
            .putString(Constants.WORKER_PASSWORD, password)
            .build()
        val request: OneTimeWorkRequest = OneTimeWorkRequest.Builder(UpdateEmailWorker::class.java)
            .setInputData(keyData)
            .build()
        val workerManager: WorkManager = WorkManager.getInstance(this.context)
        workerManager.beginWith(request).enqueue()

        workerManager.getWorkInfoByIdLiveData(request.id)
            .observe(activity, Observer {
                if (it?.state == null) {
                    return@Observer
                } else {
                    when (it.state) {
                        WorkInfo.State.SUCCEEDED -> {
                            this.isLoadingLiveData.value = false
                            this.isErrorLiveData.value = false
                            this.updateEmailListener?.onSuccess()
                        }
                        WorkInfo.State.FAILED -> {
                            this.isLoadingLiveData.value = false
                            this.isErrorLiveData.value = true
                            this.updateEmailListener?.onFailed()
                        }
                        else ->
                            return@Observer
                    }
                }
            })
    }
    fun setUpdateEmailListener(emailListener: UpdateEmailListener) {
        this.updateEmailListener = emailListener;
    }
    override fun onCleared() {
        super.onCleared()
        this.compositeDisposable.clear()
    }

    fun getLoadingLiveData(): LiveData<Boolean> {
        return this.isLoadingLiveData
    }

    fun getErrorLiveData(): LiveData<Boolean> {
        return this.isErrorLiveData
    }
}