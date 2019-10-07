package com.project.neardoc.viewmodel

import android.util.Log
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import com.project.neardoc.R
import com.project.neardoc.data.local.remote.INearDocRemoteRepo
import com.project.neardoc.utils.Constants
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class HomepageViewModel @Inject constructor(): ViewModel() {
    private val compositeDisposable = CompositeDisposable()
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
                Log.i("Result: ", res.status)
            }, {onError ->
                Log.i("Error: ", onError.localizedMessage!!)
            }))
    }
}