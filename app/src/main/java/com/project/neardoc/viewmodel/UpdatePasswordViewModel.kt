package com.project.neardoc.viewmodel

import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class UpdatePasswordViewModel @Inject constructor(): ViewModel() {
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    override fun onCleared() {
        super.onCleared()
        this.compositeDisposable.clear()
    }
}