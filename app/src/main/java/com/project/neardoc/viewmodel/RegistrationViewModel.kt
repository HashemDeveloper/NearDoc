package com.project.neardoc.viewmodel

import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class RegistrationViewModel @Inject constructor(): ViewModel() {
    private val compositeDisposable = CompositeDisposable()

    override fun onCleared() {
        super.onCleared()
        this.compositeDisposable.clear()
    }

    fun processRegistration(fullName: String, username: String, email: String, password: String) {

    }
}