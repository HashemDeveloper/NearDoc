package com.project.neardoc.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.project.neardoc.BuildConfig
import com.project.neardoc.data.local.ISharedPrefService
import kotlinx.coroutines.*
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class HeartBeatViewModel @Inject constructor(): ViewModel(), CoroutineScope {
    companion object {
        @JvmStatic private val TAG: String = HeartBeatViewModel::class.java.canonicalName!!
    }
    private val userImageLiveData: MutableLiveData<String>?= MutableLiveData()

    @Inject
    lateinit var iSharedPrefService: ISharedPrefService
    private var job = Job()

    fun init() {
        var userImagePath = ""
        launch {
            withContext(Dispatchers.IO) {
                userImagePath = iSharedPrefService.getUserImage()
            }
        }.invokeOnCompletion {
            if (it != null && it.localizedMessage != null) {
                if (BuildConfig.DEBUG) {
                    Log.i(TAG, it.localizedMessage!!)
                }
            } else {
                this.userImageLiveData!!.value = userImagePath
            }
        }
    }
    fun getUserImageLiveData(): LiveData<String> {
        return this.userImageLiveData!!
    }

    override val coroutineContext: CoroutineContext
        get() = this.job + Dispatchers.Main
}