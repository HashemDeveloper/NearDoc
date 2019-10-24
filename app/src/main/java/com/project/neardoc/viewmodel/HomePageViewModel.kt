package com.project.neardoc.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.project.neardoc.data.local.ISharedPrefService
import com.project.neardoc.data.local.remote.INearDocRemoteRepo
import javax.inject.Inject

class HomePageViewModel @Inject constructor(): ViewModel() {
    @Inject
    lateinit var iNearDocRemoteRepo: INearDocRemoteRepo
    @Inject
    lateinit var iSharedPrefService: ISharedPrefService
    @Inject
    lateinit var context: Context

    override fun onCleared() {
        super.onCleared()
    }
}