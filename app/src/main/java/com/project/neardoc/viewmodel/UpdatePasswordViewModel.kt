package com.project.neardoc.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.project.neardoc.data.local.ISharedPrefService
import javax.inject.Inject

class UpdatePasswordViewModel @Inject constructor(): ViewModel() {
    private val isLoadingData: MutableLiveData<Boolean> = MutableLiveData()
    @Inject
    lateinit var iSharedPrefService: ISharedPrefService


    fun updatePassword(currentPassword: String, newPassword: String) {
        this.isLoadingData.value = true
        val email: String = this.iSharedPrefService.getUserEmail()
    }

    fun getLoadingLiveData(): LiveData<Boolean> {
        return this.isLoadingData
    }
}