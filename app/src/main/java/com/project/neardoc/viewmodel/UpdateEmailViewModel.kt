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
import com.google.firebase.auth.FirebaseAuth
import com.project.neardoc.R
import com.project.neardoc.data.local.ISharedPrefService
import com.project.neardoc.data.local.remote.INearDocRemoteRepo
import com.project.neardoc.events.BottomBarEvent
import com.project.neardoc.events.LandInSettingPageEvent
import com.project.neardoc.events.UserStateEvent
import com.project.neardoc.rxauth.IRxAuthentication
import com.project.neardoc.utils.Constants
import com.project.neardoc.utils.widgets.PageType
import com.project.neardoc.workers.UpdateEmailWorker
import com.project.neardoc.workers.UpdateUserInfoWorker
import io.reactivex.disposables.CompositeDisposable
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

class UpdateEmailViewModel @Inject constructor(): ViewModel() {

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private val isLoadingLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private val statusMessageLiveData: MutableLiveData<String> = MutableLiveData()
    @Inject
    lateinit var iNearDocRemoteRepo: INearDocRemoteRepo
    @Inject
    lateinit var context: Context
    @Inject
    lateinit var iSharedPreferences: ISharedPrefService
    @Inject
    lateinit var iRxAuthentication: IRxAuthentication
    private var workerLiveData: LiveData<WorkInfo>?= null

    fun processUpdateEmailRequest(
        activity: FragmentActivity,
        currentEmail: String,
        newEmail: String,
        password: String
    ) {
        this.isLoadingLiveData.value = true
        val key: String = this.context.resources!!.getString(R.string.firebase_web_key)
        val dbKey: String = this.context.resources!!.getString(R.string.firebase_db_secret)

        // old information to update
        val oldEmail: String = this.iSharedPreferences.getUserEmail()
        val userImage: String = this.iSharedPreferences.getUserImage()
        val username: String = this.iSharedPreferences.getUserUsername()
        val fullName: String = this.iSharedPreferences.getUserName()

        val updateEmailData: Data = Data.Builder()
            .putString(Constants.WORKER_WEB_KEY, key)
            .putString(Constants.WORKER_NEW_EMAIL, newEmail)
            .putString(Constants.WORKER_EMAIL, currentEmail)
            .putString(Constants.WORKER_PASSWORD, password)
            .build()

        val updateUserInfoData: Data = Data.Builder()
            .putString(Constants.WORKER_DB_AUTH_KEY, dbKey)
            .putString(Constants.WORKER_EMAIL, newEmail)
            .putString(Constants.WORKER_IMAGE_PATH, userImage)
            .putString(Constants.WORKER_FULL_NAME, fullName)
            .putString(Constants.WORKER_DISPLAY_NAME, username)
            .putString(Constants.WORKER_OLD_EMAIL, oldEmail)
            .build()
        val updateUserInfoRequest: OneTimeWorkRequest = OneTimeWorkRequest.Builder(UpdateUserInfoWorker::class.java)
            .setInputData(updateUserInfoData)
            .build()
        val updateEmailRequest: OneTimeWorkRequest = OneTimeWorkRequest.Builder(UpdateEmailWorker::class.java)
            .setInputData(updateEmailData)
            .build()
        val workerManager: WorkManager = WorkManager.getInstance(this.context)
        workerManager
            .beginWith(updateUserInfoRequest)
            .then(updateEmailRequest)
            .enqueue()

        this.workerLiveData = workerManager.getWorkInfoByIdLiveData(updateEmailRequest.id)
        this.workerLiveData?.observe(activity, workerObserver())
    }
    private fun workerObserver(): Observer<WorkInfo> {
        return Observer {
            if (it?.state == null) {
                return@Observer
            } else {
                when (it.state) {
                    WorkInfo.State.SUCCEEDED -> {
                        this.isLoadingLiveData.value = false
                        val googleProvider: String = this.iSharedPreferences.getLoginProvider()
                        if (googleProvider == Constants.SIGN_IN_PROVIDER_GOOGLE) {
                            val message: String = Constants.SIGN_IN_PROVIDER_GOOGLE
                            this.statusMessageLiveData.value = message
                        } else {
                            val message: String = this.context.resources.getString(R.string.email_update_success)
                            this.statusMessageLiveData.value = message
                        }
                    }
                    WorkInfo.State.FAILED -> {
                        this.isLoadingLiveData.value = false
                        val errorData: Data? = it.outputData
                        if (errorData != null) {
                            val errorMessage: String = errorData.getString(Constants.WORKER_ERROR_DATA)!!
                            if (errorMessage.isNotEmpty()) {
                                this.statusMessageLiveData.value = errorMessage
                            }
                        }
                    }
                    else ->
                        return@Observer
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        this.compositeDisposable.clear()
        if (this.workerLiveData != null) {
            this.workerLiveData?.removeObserver(workerObserver())
        }
    }

    fun getLoadingLiveData(): LiveData<Boolean> {
        return this.isLoadingLiveData
    }

    fun getStatusMessageLiveData(): LiveData<String> {
        return this.statusMessageLiveData
    }

    fun signOut() {
        FirebaseAuth.getInstance().signOut()
        EventBus.getDefault().postSticky(UserStateEvent(false))
        EventBus.getDefault().postSticky(LandInSettingPageEvent(false, PageType.MAIN_PAGE))
        EventBus.getDefault().postSticky(BottomBarEvent(false))
    }
}