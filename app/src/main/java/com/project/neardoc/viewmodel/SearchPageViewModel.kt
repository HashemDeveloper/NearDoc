package com.project.neardoc.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import com.project.neardoc.R
import com.project.neardoc.data.local.ISharedPrefService
import com.project.neardoc.data.local.remote.INearDocRemoteRepo
import com.project.neardoc.data.local.searchdocdaos.IDocDao
import com.project.neardoc.data.local.searchdocdaos.IDocProfileDao
import com.project.neardoc.data.local.searchdocdaos.IDocProfileLanguageDao
import com.project.neardoc.data.local.searchdocdaos.IDocRatingDao
import com.project.neardoc.model.*
import com.project.neardoc.model.localstoragemodels.*
import com.project.neardoc.utils.Constants
import com.project.neardoc.utils.livedata.ResultHandler
import kotlinx.coroutines.*
import retrofit2.Response
import java.util.*
import java.util.concurrent.CountDownLatch
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class SearchPageViewModel @Inject constructor(): ViewModel(), CoroutineScope {
    companion object {
        @JvmStatic private val TAG: String = SearchPageViewModel::class.java.canonicalName!!
    }
    @Inject
    lateinit var iDocDao: IDocDao
    @Inject
    lateinit var iDocProfileDao: IDocProfileDao
    @Inject
    lateinit var iDocRatingDao: IDocRatingDao
    @Inject
    lateinit var iDocProfileLanguageDao: IDocProfileLanguageDao
    @Inject
    lateinit var iNearDocRemoteRepo: INearDocRemoteRepo
    @Inject
    lateinit var context: Context
    var fetchDocByDiseaseLiveData: LiveData<ResultHandler<Any>>?= null
    @Inject
    lateinit var iSharedPrefService: ISharedPrefService
    private val job = Job()
    val list: MutableList<DocProfile> = arrayListOf()

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

    fun initNearByDocList(apiKey: String, latitude: String, longitude: String, s: String) {
        val userEmail: String = this.iSharedPrefService.getUserEmail()
        val radius: String = this.iSharedPrefService.getDistanceRadius()
        val limit: String = this.iSharedPrefService.getSearchLimit()
        val distance = "$latitude,$longitude,$radius"
        this.fetchDocByDiseaseLiveData = liveData {

            try {
                iDocDao.clearDocList()
                val result: Response<BetterDocSearchByDiseaseRes> = iNearDocRemoteRepo.searchDocByDiseaseKtx(Constants.SEARCH_DOC_BY_DISEASE_ENDPOINT,
                    apiKey, limit.toInt(), distance, s, "distance-asc")
                if (result.isSuccessful) {
                    val body: BetterDocSearchByDiseaseRes = result.body()!!
                    var doc: Doc
                    if (body.searchByDiseaseData.isNotEmpty()) {
                        for (doctor: Doctor in body.searchByDiseaseData) {
                            val docParentId: String = UUID.randomUUID().toString()
                            val docProfileId: String = UUID.randomUUID().toString()
                            val ratingId: String = UUID.randomUUID().toString()
                            val profileLanguageId = UUID.randomUUID().toString()
                            doc = Doc(docParentId, userEmail, doctor.uid)
                            iDocDao.insertDoctors(doc)
                            val doctorProfile: Profile = doctor.profile
                            doctor.let {
                                if (it.ratingList.isNotEmpty()) {
                                    for (rating: Rating in it.ratingList) {
                                        val docRatings = DocRatings(ratingId, doc.docParentId, rating.active, rating.provider ?: "", rating.providerUid ?: "", rating.providerUrl ?: "",
                                            rating.rating ?: 0.0, rating.reviewCount ?: 0, rating.imageUrlSmall ?: "", rating.imageUrlSmall2x ?: "", rating.imageUrlLarge ?: "", rating.imageUrlLarge2x ?: "")
                                        iDocRatingDao.insertDoctorRatings(docRatings)
                                    }
                                }
                            }
                            val docProfile = DocProfile(docProfileId, doc.docParentId, userEmail, doctorProfile.firstName, doctorProfile.lastName, if (doctorProfile.slug.isNotEmpty()) doctorProfile.slug else "",
                                doctorProfile.title ?: "", doctorProfile.imageUrl ?: "", doctorProfile.gender ?: "", doctorProfile.bio ?: "", doc.uid)
                            list.add(docProfile)
                            iDocProfileDao.insertDocProfile(list)
                            doctorProfile.let {
                                if (it.listOfLanguage.isNotEmpty()) {
                                    for (languages in it.listOfLanguage) {
                                        val language: Language = languages
                                        val docProfileLanguage = DocProfileLanguage(profileLanguageId, docProfile.docProfileId, language.name, language.code)
                                        iDocProfileLanguageDao.insertDocProfileLanguage(docProfileLanguage)
                                    }
                                }
                            }
                        }
                        emit(ResultHandler.success(iDocProfileDao.getDoctorsProfile()))
                    }
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

    override val coroutineContext: CoroutineContext
        get() = this.job + Dispatchers.Main
}