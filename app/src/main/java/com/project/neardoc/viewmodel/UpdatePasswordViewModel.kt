package com.project.neardoc.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class UpdatePasswordViewModel @Inject constructor(): ViewModel() {
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private val isLoadingData: MutableLiveData<Boolean> = MutableLiveData()

    override fun onCleared() {
        super.onCleared()
        this.compositeDisposable.clear()
    }

    fun updatePassword(currentPassword: String, newPassword: String) {
        this.isLoadingData.value = true
    }
    fun getLoadingLiveData(): LiveData<Boolean> {
        return this.isLoadingData
    }
}