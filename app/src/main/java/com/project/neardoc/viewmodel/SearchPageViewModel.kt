package com.project.neardoc.viewmodel

import android.content.Context
import android.util.Log
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.*
import com.project.neardoc.BuildConfig
import com.project.neardoc.R
import com.project.neardoc.data.local.remote.INearDocRemoteRepo
import com.project.neardoc.model.*
import com.project.neardoc.utils.Constants
import com.project.neardoc.utils.livedata.ResultHandler
import com.project.neardoc.viewmodel.listeners.ISearchPageViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.*
import retrofit2.Response
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class SearchPageViewModel @Inject constructor(): ViewModel() {
    companion object {
        @JvmStatic private val TAG: String = SearchPageViewModel::class.java.canonicalName!!
    }
    @Inject
    lateinit var iNearDocRemoteRepo: INearDocRemoteRepo
    @Inject
    lateinit var context: Context
    var fetchDocByDiseaseLiveData: LiveData<ResultHandler<Any>>?= null

    override fun onCleared() {
        super.onCleared()
    }

    val checkBetterDocApiHealth = liveData {
        emit(ResultHandler.onLoading(true))
        val apiKey: String = context.resources.getString(R.string.better_doc_api_key)
        try {
            val result: Response<BetterDocApiHealthRes> = iNearDocRemoteRepo.checkBetterDocApiHealthKtxAsync(Constants.BETTER_DOC_API_HEALTH_ENDPOINT, apiKey)
            if (result.isSuccessful) {
                val betterDocHealthRes: BetterDocApiHealthRes = result.body()!!
                emit(ResultHandler.success(betterDocHealthRes))
            } else {
                emit(ResultHandler.onError(null, "Failed"))
            }
        } catch (ex: Exception) {
            if (ex.localizedMessage != null) {
                emit(ResultHandler.onError(ex.localizedMessage!!, ex.localizedMessage!!))
            }
        }
    }

    fun fetchDocByDisease(apiKey: String, latitude: String, longitude: String,  s: String) {
        val distance = "$latitude,$longitude,10"
        this.fetchDocByDiseaseLiveData = liveData {
            try {
                val result: Response<BetterDocSearchByDiseaseRes> = iNearDocRemoteRepo.searchDocByDiseaseKtx(Constants.SEARCH_DOC_BY_DISEASE_ENDPOINT,
                    apiKey, 10, distance, s, "distance-asc")
                if (result.isSuccessful) {
                    val body: BetterDocSearchByDiseaseRes = result.body()!!
                    emit(ResultHandler.success(body))
                } else {
                    emit(ResultHandler.onError(null, "Failed"))
                }
            } catch (ex: Exception) {
                if (ex.localizedMessage != null) {
                    emit(ResultHandler.onError(ex.localizedMessage!!, ex.localizedMessage!!))
                }
            }
        }
    }
}