package com.project.neardoc.viewmodel

import android.content.Context
import android.util.Log
import androidx.annotation.VisibleForTesting
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.project.neardoc.R
import com.project.neardoc.data.local.remote.INearDocRemoteRepo
import com.project.neardoc.model.*
import com.project.neardoc.utils.Constants
import com.project.neardoc.viewmodel.listeners.IHomepageViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class SearchPageViewModel @Inject constructor(): ViewModel() {
    private val compositeDisposable = CompositeDisposable()
    private var iHomepageViewModel: IHomepageViewModel?= null
    @VisibleForTesting
    private val statusOkLiveData: MutableLiveData<Boolean> = MutableLiveData()
    @Inject
    lateinit var iNearDocRemoteRepo: INearDocRemoteRepo
    @Inject
    lateinit var context: Context

    override fun onCleared() {
        super.onCleared()
        this.compositeDisposable.clear()
    }

    fun checkBetterDocApiHealth(apiKey: String) {
        this.compositeDisposable.add(this.iNearDocRemoteRepo.checkBetterDocApiHealth(Constants.BETTER_DOC_API_HEALTH_ENDPOINT, apiKey)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({res ->
                if (res.status == "OK") {
                    this.statusOkLiveData.value = true
                    this.iHomepageViewModel?.fetchData()
                }
            }, {onError ->
                this.statusOkLiveData.value = false
                this.iHomepageViewModel?.onServerError()
            }))
    }
    fun setListener(iHomepageViewModel: IHomepageViewModel) {
        this.iHomepageViewModel = iHomepageViewModel
    }

    fun fetchDocByDisease(apiKey: String, latitude: String, longitude: String,  s: String) {
        val distance = "$latitude,$longitude,10"
       this.compositeDisposable.add(this.iNearDocRemoteRepo.searchDocByDisease(Constants.SEARCH_DOC_BY_DISEASE_ENDPOINT, apiKey,
           10, distance, s, "distance-asc")
           .subscribeOn(Schedulers.io())
           .observeOn(AndroidSchedulers.mainThread())
           .subscribe({response ->
               val dataList: List<Doctor> = response.searchByDiseaseData
               for (data in dataList) {
                   val profile = data.profile
                   val insuranceList: List<Insurance> = data.insuranceList
                   val practiceList: List<Practice> = data.practiceList
                   val specialityList: List<Specialty> = data.specialityList
                   for (insurance in insuranceList) {
                       val insurancePlan: InsurancePlan = insurance.insurancePlan
                   }
               }
           }, {onError ->
               Log.i("Error: ", onError.localizedMessage!!)
           }))
    }
    fun getStatusOkLiveData(): LiveData<Boolean> {
        return this.statusOkLiveData
    }
}