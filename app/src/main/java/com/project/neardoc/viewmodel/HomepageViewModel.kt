package com.project.neardoc.viewmodel

import android.util.Log
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import com.project.neardoc.R
import com.project.neardoc.data.local.remote.INearDocRemoteRepo
import com.project.neardoc.model.Doctor
import com.project.neardoc.utils.Constants
import com.project.neardoc.viewmodel.listeners.IHomepageViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class HomepageViewModel @Inject constructor(): ViewModel() {
    private val compositeDisposable = CompositeDisposable()
    private var iHomepageViewModel: IHomepageViewModel?= null
    @Inject
    lateinit var iNearDocRemoteRepo: INearDocRemoteRepo

    override fun onCleared() {
        super.onCleared()
        this.compositeDisposable.clear()
    }

    fun checkBetterDocApiHealth(activity: FragmentActivity?) {
        this.compositeDisposable.add(this.iNearDocRemoteRepo.checkBetterDocApiHealth(Constants.BETTER_DOC_API_HEALTH_ENDPOINT, activity?.resources!!.getString(
            R.string.better_doc_api_key))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({res ->
                if (res.status == "OK") {
                    this.iHomepageViewModel?.fetchData()
                }
            }, {onError ->
                this.iHomepageViewModel?.onServerError()
            }))
    }
    fun setListener(iHomepageViewModel: IHomepageViewModel) {
        this.iHomepageViewModel = iHomepageViewModel
    }

    fun fetchDocByDisease(activity: FragmentActivity?, s: String) {
       this.compositeDisposable.add(this.iNearDocRemoteRepo.searchDocByDisease(Constants.SEARCH_DOC_BY_DISEASE_ENDPOINT, activity?.resources!!.getString(R.string.better_doc_api_key),
           10, "40.7128,-74.0060,10", s, "distance-asc")
           .subscribeOn(Schedulers.io())
           .observeOn(AndroidSchedulers.mainThread())
           .subscribe({response ->
               val dataList: List<Doctor> = response.searchByDiseaseData
               for (data in dataList) {
                   Log.i("UID: ", data.uid)
               }
           }, {onError ->
               Log.i("Error: ", onError.localizedMessage!!)
           }))
    }
}