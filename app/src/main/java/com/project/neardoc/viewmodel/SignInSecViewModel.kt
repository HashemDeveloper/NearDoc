package com.project.neardoc.viewmodel

import android.content.Context
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
import com.project.neardoc.events.BottomBarEvent
import com.project.neardoc.events.LandInSettingPageEvent
import com.project.neardoc.events.UserStateEvent
import com.project.neardoc.utils.Constants
import com.project.neardoc.utils.widgets.PageType
import com.project.neardoc.worker.DeleteAccountWorker
import com.project.neardoc.worker.DeleteUserInfoWorker
import com.project.neardoc.worker.DeleteUsernameWorker
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

class SignInSecViewModel @Inject constructor(): ViewModel() {
    @Inject
    lateinit var context: Context
    private var deleteAccountLiveData: LiveData<WorkInfo>?= null
    private val isLoadingLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private val deleteStatusLiveData: MutableLiveData<String> = MutableLiveData()


    fun deleteAccount(email: String, password: String, activity: FragmentActivity) {
        this.isLoadingLiveData.value = true
        val authKey: String = this.context.resources.getString(R.string.firebase_web_key)
        val dbKey: String = this.context.resources.getString(R.string.firebase_db_secret)

        val usernameDeleteData: Data = Data.Builder()
            .putString(Constants.WORKER_DB_AUTH_KEY, dbKey)
            .build()
        val deleteUserInfoData: Data = Data.Builder()
            .putString(Constants.WORKER_DB_AUTH_KEY, dbKey)
            .putString(Constants.WORKER_EMAIL, email)
            .build()
        val accountDeleteData: Data = Data.Builder()
            .putString(Constants.WORKER_WEB_KEY, authKey)
            .putString(Constants.WORKER_EMAIL, email)
            .putString(Constants.WORKER_PASSWORD, password)
            .build()

        val deleteUsernameRequest: OneTimeWorkRequest = OneTimeWorkRequest.Builder(DeleteUsernameWorker::class.java)
            .setInputData(usernameDeleteData)
            .build()
        val deleteUserInfoRequest: OneTimeWorkRequest = OneTimeWorkRequest.Builder(DeleteUserInfoWorker::class.java)
            .setInputData(deleteUserInfoData)
            .build()
        val deleteAccountRequest: OneTimeWorkRequest = OneTimeWorkRequest.Builder(DeleteAccountWorker::class.java)
            .setInputData(accountDeleteData)
            .build()

        val workManager: WorkManager = WorkManager.getInstance(this.context)
        workManager
            .beginWith(deleteAccountRequest)
            .then(deleteUsernameRequest)
            .then(deleteUserInfoRequest)
            .enqueue()

        this.deleteAccountLiveData = workManager.getWorkInfoByIdLiveData(deleteAccountRequest.id)
        this.deleteAccountLiveData?.observe(activity, getDeleteAccountObserver())
    }

    private fun getDeleteAccountObserver(): Observer<WorkInfo> {
        return Observer {
            if (it?.state == null) {
                return@Observer
            } else {
                when(it.state) {
                    WorkInfo.State.SUCCEEDED -> {
                        this.isLoadingLiveData.value = false
                        this.deleteStatusLiveData.value = this.context.resources.getString(R.string.delete_success)
                    }
                    WorkInfo.State.FAILED -> {
                        this.isLoadingLiveData.value = false
                        val errorData: Data? = it.outputData
                        if (errorData != null) {
                            val errorMessage: String = it.outputData.getString(Constants.WORKER_ERROR_DATA)!!
                            if (errorMessage.isNotEmpty()) {
                                if (errorMessage == "The password is invalid or the user does not have a password.") {
                                    Log.i("Error: ", errorMessage)
                                    this.deleteStatusLiveData.value = errorMessage
                                }
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

    override fun onCleared() {
        super.onCleared()
        if (this.deleteAccountLiveData != null) {
            this.deleteAccountLiveData?.removeObserver(getDeleteAccountObserver())
        }
    }

    fun getLoadingLiveData(): LiveData<Boolean> {
        return this.isLoadingLiveData
    }

    fun getDeleteStatusData(): LiveData<String> {
        return this.deleteStatusLiveData
    }

    fun signOut() {
        FirebaseAuth.getInstance().signOut()
        EventBus.getDefault().postSticky(UserStateEvent(false))
        EventBus.getDefault().postSticky(LandInSettingPageEvent(false, PageType.MAIN_PAGE))
        EventBus.getDefault().postSticky(BottomBarEvent(false))
    }
}