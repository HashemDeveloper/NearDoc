package com.project.neardoc.view.settings

import androidx.lifecycle.ViewModel
import androidx.work.Data
import com.project.neardoc.utils.Constants
import javax.inject.Inject

class SignInSecViewModel @Inject constructor(): ViewModel() {

    override fun onCleared() {
        super.onCleared()
    }

    fun deleteAccount(email: String, password: String) {
        val data: Data = Data.Builder()
            .putString(Constants.WORKER_EMAIL, email)
            .putString(Constants.WORKER_PASSWORD, password)
            .build()

    }
}