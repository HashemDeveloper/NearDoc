package com.project.neardoc.viewmodel

import android.content.Context
import androidx.lifecycle.*
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.project.neardoc.R
import com.project.neardoc.data.local.ISharedPrefService
import com.project.neardoc.data.local.remote.INearDocRemoteRepo
import com.project.neardoc.data.local.searchdocdaos.*
import com.project.neardoc.model.*
import com.project.neardoc.model.localstoragemodels.*
import com.project.neardoc.utils.Constants
import com.project.neardoc.utils.LocalDbInsertionOption
import com.project.neardoc.utils.NavigationType
import com.project.neardoc.utils.livedata.ResultHandler
import kotlinx.coroutines.*
import retrofit2.Response
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class SearchPageViewModel @Inject constructor(): ViewModel(), CoroutineScope {
    @Inject
    lateinit var iDocDao: IDocDao
    @Inject
    lateinit var iDocProfileDao: IDocProfileDao
    @Inject
    lateinit var iDocRatingDao: IDocRatingDao
    @Inject
    lateinit var iDocProfileLanguageDao: IDocProfileLanguageDao
    @Inject
    lateinit var iDocPractice: IDocPracticeDao
    @Inject
    lateinit var iNearDocRemoteRepo: INearDocRemoteRepo
    @Inject
    lateinit var context: Context
    var fetchDocByDiseaseLiveData: LiveData<ResultHandler<Any>>?= null
    var checkBetterDocApiHealth: LiveData<ResultHandler<Any>>? = null
    @Inject
    lateinit var iSharedPrefService: ISharedPrefService
    private val job = Job()
    val list: MutableList<DocProfile> = arrayListOf()
    private var docParentId: String?= null
    private var docProfileId: String?= null
    private var ratingId: String?= null
    private var profileLanguageId: String?= null
    private var docPracticeId: String?= null
    private var pagedListConfig: PagedList.Config?= null

    fun init() {
        val limit: String = this.iSharedPrefService.getSearchLimit()
        this.pagedListConfig = PagedList.Config.Builder()
            .setEnablePlaceholders(true)
            .setInitialLoadSizeHint(INITIAL_PAGE_LOAD_HINT)
            .setPageSize(limit.toInt())
            .build()
    }

    fun getDoctorsData() {
        val expireTime: Long = TimeUnit.MINUTES.toMillis(1)
        val createdTime: Long = this.iSharedPrefService.getCachingTime()
        val currentTime: Long = System.currentTimeMillis()
        if (createdTime < (currentTime - expireTime)) {
            fetchDataFromServer()
        } else {
            fetchDataFromLocalDb()
        }
    }
    private fun fetchDataFromServer() {
        this.checkBetterDocApiHealth = liveData {
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
    }

    private fun fetchDataFromLocalDb() {
        this.fetchDocByDiseaseLiveData = liveData {
            val docList = LivePagedListBuilder<Int, DocAndRelations>(iDocDao.getAllDoctorsInformation(), pagedListConfig!!).build()
            if (docList != null) {
                emit(ResultHandler.success(docList))
            } else {
                emit(ResultHandler.onError("", "Failed"))
            }
        }
    }

   fun initNearByDocList(
        apiKey: String,
        latitude: String,
        longitude: String,
        s: String,
        insertionType: LocalDbInsertionOption
    ) {
        val userEmail: String = this.iSharedPrefService.getUserEmail()
        val radius: String = this.iSharedPrefService.getDistanceRadius()
        val limit: String = this.iSharedPrefService.getSearchLimit()
        val distance = "$latitude,$longitude,$radius"
        this.fetchDocByDiseaseLiveData = liveData {
            when (insertionType) {
                LocalDbInsertionOption.INSERT -> {

                }
                LocalDbInsertionOption.UPDATE -> {
                    emit(ResultHandler.onLoading(true))
                }
            }
            try {
                val result: Response<BetterDocSearchByDiseaseRes> = iNearDocRemoteRepo.searchDocByDiseaseKtx(Constants.SEARCH_DOC_BY_DISEASE_ENDPOINT,
                    apiKey, limit.toInt(), distance, s, "distance-asc")
                if (result.isSuccessful) {
                    val body: BetterDocSearchByDiseaseRes = result.body()!!
                    var doc: Doc
                    if (body.searchByDiseaseData.isNotEmpty()) {
                        iDocDao.clearDocList()
                        iDocProfileDao.clearDocProfile()
                        iDocRatingDao.clearDocRatings()
                        iDocProfileLanguageDao.clearLanguage()
                        for (doctor: Doctor in body.searchByDiseaseData) {
                            docParentId = UUID.randomUUID().toString()
                            docProfileId = UUID.randomUUID().toString()
                            ratingId = UUID.randomUUID().toString()
                            profileLanguageId = UUID.randomUUID().toString()
                            docPracticeId = UUID.randomUUID().toString()
                            doc = Doc(docParentId!!, userEmail, doctor.uid)
                            iDocDao.insertDoctors(doc)

                            val doctorProfile: Profile = doctor.profile
                            doctor.let {
                                if (it.ratingList.isNotEmpty()) {
                                    for (rating: Rating in it.ratingList) {
                                        val docRatings = DocRatings(ratingId!!, doc.docParentId, rating.active, rating.provider ?: "", rating.providerUid ?: "", rating.providerUrl ?: "",
                                            rating.rating ?: 0.0, rating.reviewCount ?: 0, rating.imageUrlSmall ?: "", rating.imageUrlSmall2x ?: "", rating.imageUrlLarge ?: "", rating.imageUrlLarge2x ?: "")
                                        iDocRatingDao.insertDoctorRatings(docRatings)
                                    }
                                }
                                if (it.practiceList.isNotEmpty()) {
                                    for (practice: Practice in it.practiceList) {
                                        val docPractice = DocPractice(docPracticeId!!, doc.docParentId, practice.locationSlug ?: "", practice.withinSearchArea.toString() ?: "",
                                            practice.distance, practice.lat, practice.lon, practice.uid, practice.name, practice.acceptsNewPatients.toString() ?: "", practice.website ?: "",
                                            practice.email ?: "", practice.npi ?: "", practice.slug ?: "", practice.description ?: "", practice.totalDocInPractice, practice.paginationUrlForDoc ?: "")
                                        iDocPractice.insertDocPractice(docPractice)
                                    }
                                }
                            }
                            val docProfile = DocProfile(docProfileId!!, doc.docParentId, userEmail, doctorProfile.firstName, doctorProfile.lastName, if (doctorProfile.slug.isNotEmpty()) doctorProfile.slug else "",
                                doctorProfile.title ?: "", doctorProfile.imageUrl ?: "", doctorProfile.gender ?: "", doctorProfile.bio ?: "", doc.uid)
                            list.add(docProfile)
                            iDocProfileDao.insertDocProfile(list)
                            doctorProfile.let {
                                if (it.listOfLanguage.isNotEmpty()) {
                                    for (languages in it.listOfLanguage) {
                                        val language: Language = languages
                                        val docProfileLanguage = DocProfileLanguage(profileLanguageId!!, docProfile.docProfileId, language.name, language.code)
                                        iDocProfileLanguageDao.insertDocProfileLanguage(docProfileLanguage)
                                    }
                                }
                            }
                        }
                        iSharedPrefService.saveCachingTime(System.currentTimeMillis())
                        val docList = LivePagedListBuilder<Int, DocAndRelations>(iDocDao.getAllDoctorsInformation(), pagedListConfig!!).build()
                        emit(ResultHandler.success(docList))
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

    override fun onCleared() {
        super.onCleared()
    }

    fun fetchDataForOfflineState() {
        fetchDataFromLocalDb()
    }

    fun saveNavigationType(waze: NavigationType) {

    }

    override val coroutineContext: CoroutineContext
        get() = this.job + Dispatchers.Main

    companion object {
        @JvmStatic private val TAG: String = SearchPageViewModel::class.java.canonicalName!!
        private const val INITIAL_PAGE_LOAD_HINT = 20
    }
}