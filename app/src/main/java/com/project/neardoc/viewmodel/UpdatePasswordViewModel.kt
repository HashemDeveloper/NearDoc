package com.project.neardoc.viewmodel

import android.content.Context
import android.os.Handler
import android.util.Log
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.google.firebase.auth.FirebaseAuth
import com.project.neardoc.R
import com.project.neardoc.data.local.ISharedPrefService
import com.project.neardoc.events.BottomBarEvent
import com.project.neardoc.events.LandInSettingPageEvent
import com.project.neardoc.events.UserStateEvent
import com.project.neardoc.utils.Constants
import com.project.neardoc.utils.PageType
import com.project.neardoc.worker.UpdatePasswordWorker
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

class UpdatePasswordViewModel @Inject constructor(): ViewModel() {
    private val isLoadingData: MutableLiveData<Boolean> = MutableLiveData()
    private val statusMessageLiveData: MutableLiveData<String> = MutableLiveData()
    private var workerLiveData: LiveData<WorkInfo>?= null
    @Inject
    lateinit var context: Context
    @Inject
    lateinit var iSharedPrefService: ISharedPrefService


    fun updatePassword(activity: FragmentActivity, currentPassword: String, newPassword: String) {
        this.isLoadingData.value = true
        val email: String = this.iSharedPrefService.getUserEmail()
        val key: String = this.context.resources.getString(R.string.firebase_web_key)
        val data: Data = Data.Builder()
            .putString(Constants.WORKER_WEB_KEY, key)
            .putString(Constants.WORKER_EMAIL, email)
            .putString(Constants.WORKER_PASSWORD, currentPassword)
            .putString(Constants.WORKER_NEW_PASS, newPassword)
            .build()
        val request: OneTimeWorkRequest = OneTimeWorkRequest.Builder(UpdatePasswordWorker::class.java)
            .setInputData(data)
            .build()
        val workManager: WorkManager = WorkManager.getInstance(this.context)
        workManager.beginWith(request).enqueue()
        this.workerLiveData = workManager.getWorkInfoByIdLiveData(request.id)
        this.workerLiveData?.observe(activity, workerObserver())
    }
    private fun workerObserver(): Observer<WorkInfo> {
        return Observer {
           if (it?.state == null) {
               return@Observer
           } else {
               when (it.state) {
                   WorkInfo.State.SUCCEEDED -> {
                       this.isLoadingData.value = false
                       val message = this.context.resources.getString(R.string.password_updated)
                       this.statusMessageLiveData.value = message
                   }
                   WorkInfo.State.FAILED -> {
                       this.isLoadingData.value = false
                       val errorData: Data? = it.outputData
                       if (errorData != null) {
                           val errorMessage: String = errorData.getString(Constants.WORKER_ERROR_DATA)!!
                           if (errorMessage.isNotEmpty()) {
                               Log.i("Error: ", errorMessage)
                               this.statusMessageLiveData.value = errorMessage
                           }
                       }
                   }
                   else -> {
                       return@Observer
                   }
               }
           }
        }
    }

    fun getLoadingLiveData(): LiveData<Boolean> {
        return this.isLoadingData
    }

    fun getStatusMessageLiveData(): LiveData<String> {
        return this.statusMessageLiveData
    }

    override fun onCleared() {
        super.onCleared()
        if (this.workerLiveData != null) {
            this.workerLiveData?.removeObserver(workerObserver())
        }
    }

    fun signOut() {
        FirebaseAuth.getInstance().signOut()
        EventBus.getDefault().postSticky(UserStateEvent(false))
        EventBus.getDefault().postSticky(LandInSettingPageEvent(false, PageType.MAIN_PAGE))
        EventBus.getDefault().postSticky(BottomBarEvent(false))
    }
}