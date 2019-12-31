package com.project.neardoc.view.fragments


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.project.neardoc.BuildConfig
import com.project.neardoc.view.fragments.DoctorsDetailsArgs.fromBundle
import com.project.neardoc.R
import com.project.neardoc.di.Injectable
import com.project.neardoc.di.viewmodel.ViewModelFactory
import com.project.neardoc.model.localstoragemodels.DocAndRelations
import com.project.neardoc.model.localstoragemodels.DocPractice
import com.project.neardoc.model.localstoragemodels.DocRatings
import com.project.neardoc.utils.GlideApp
import com.project.neardoc.utils.NavigationType
import com.project.neardoc.view.widgets.NavTypeBottomSheetDialog
import com.project.neardoc.viewmodel.DoctorDetailsViewModel
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_doctors_details.*
import java.lang.Exception
import java.net.URLEncoder
import javax.inject.Inject

class DoctorsDetails : Fragment(), Injectable {

    private val doctorsDetails by lazy {
        fromBundle(arguments!!).doctorsDetails
    }
    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val doctorDetailsViewModel: DoctorDetailsViewModel by viewModels {
        this.viewModelFactory
    }
    private var displayNavigationTypeDialog: NavTypeBottomSheetDialog?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_doctors_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupDoctorsInformation()
        setupClickListeners()
    }
    private fun setupDoctorsInformation() {
        val docData: DocAndRelations = this.doctorsDetails
        var docName = ""
        var docImagePath = ""
        var docBiography = ""
        var rating = 0.0
        var speciality = ""
        for (docProfile in docData.docProfile) {
            docName = docProfile.firstName + " ${docProfile.lastName}, ${docProfile.title}"
            docImagePath = docProfile.imageUrl
            docBiography = docProfile.bio
        }
        for (ratings: DocRatings in docData.docRating) {
            rating = ratings.rating
        }
        for (specialities in docData.docSpeciality) {
            speciality = specialities.actors
        }

        fragment_doctors_details_page_name_view_id?.let {
            it.text = docName
        }
        fragment_doctor_details_speciality_view_id?.let {
            it.text = speciality
        }
        GlideApp.with(this)
            .load(docImagePath)
            .placeholder(R.drawable.ic_user_black_24dp)
            .into(fragment_doctors_profile_image_view_id)
        fragment_doctors_bio_description_view_id?.let {
            it.text = docBiography
            it.movementMethod = ScrollingMovementMethod()
        }
        fragment_doctors_details_ratingbarId?.let {
            it.rating = rating.toFloat()
        }
    }

    private fun setupClickListeners() {
        val docData: DocAndRelations = this.doctorsDetails
        var docEmail = ""
        var lat = 0.0
        var lon = 0.0
        var website = ""

        for (docPractice: DocPractice in docData.docPractice) {
            docEmail = docPractice.email
            lat = docPractice.lat
            lon = docPractice.lon
            website = docPractice.website
        }
        fragment_doctors_details_insurance_list_bt_id?.let {
            it.setOnClickListener {

            }
        }
        fragment_doctors_details_contact_bt_id?.let {
            it.setOnClickListener {

            }
        }
        if (website.isEmpty()) {
            fragment_doctors_details_affiliation_bt_id?.let {
                it.visibility = View.GONE
            }
            fragment_doctors_details_website_title_view_id?.let {
                it.text = getString(R.string.business_affiliation)
            }
            fragment_doctors_details_website_bt_id?.let {
                it.setImageResource(R.drawable.business_affiliate_network)
                it.setOnClickListener {
                    Toast.makeText(this.context!!, "Business Affiliate", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            fragment_doctors_details_affiliation_bt_id?.let {
                it.visibility = View.VISIBLE
                it.setOnClickListener {
                    Toast.makeText(this.context!!, "Business Affiliate", Toast.LENGTH_SHORT).show()
                }
            }
            fragment_doctors_details_website_title_view_id?.let {
                it.text = getString(R.string.website)
            }
            fragment_doctors_details_website_bt_id?.let {
                it.setOnClickListener {
                    val browseIntent = Intent(Intent.ACTION_VIEW, Uri.parse(website))
                    startActivity(browseIntent)
                }
            }
        }

        fragment_doctors_details_navigate_bt_id?.let {
            it.setOnClickListener {
                val destination = "$lat,$lon"
                val navigationType: String = this.doctorDetailsViewModel.getNavigationType()
                val wazeNavUrl: String = NavigationType.WAZE.uriString + URLEncoder.encode(destination, "UTF-8")
                val googleNavUrl: String = NavigationType.GOOGLE.uriString + URLEncoder.encode(destination, "UTF-8")
                if (navigationType.isNotEmpty()) {
                    if (navigationType == NavigationType.WAZE.name) {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(wazeNavUrl))
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        activity?.startActivity(intent)
                    } else if (navigationType == NavigationType.GOOGLE.name) {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(googleNavUrl))
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        activity?.startActivity(intent)
                    }
                } else {
                    this.displayNavigationTypeDialog = NavTypeBottomSheetDialog()
                    displayNavigationTypeDialog?.show(activity!!.supportFragmentManager, displayNavigationTypeDialog?.tag)
                    displayNavigationTypeDialog?.getOnClickLiveDataObserver()!!.observe(activity!!, navBottomSheetClickObserver(destination))
                }
            }
        }
    }

    private fun navBottomSheetClickObserver(destination: String): Observer<NavigationType> {
        val wazeNavUrl: String = NavigationType.WAZE.uriString + URLEncoder.encode(destination, "UTF-8")
        val googleNavUrl: String = NavigationType.GOOGLE.uriString + URLEncoder.encode(destination, "UTF-8")
        return Observer {
            when (it) {
                NavigationType.WAZE -> {
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(wazeNavUrl))
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        activity?.startActivity(intent)
                        this.doctorDetailsViewModel.saveNavigationType(NavigationType.WAZE)
                        this.displayNavigationTypeDialog!!.dismiss()

                    } catch (ex: Exception) {
                        if (BuildConfig.DEBUG) {
                            if (ex.localizedMessage != null) {
                                Log.d(TAG, "Failed to open Waze: ${ex.localizedMessage!!}")
                            }
                        }
                        val installIntent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.waze"))
                        installIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        activity?.startActivity(installIntent)
                    }

                }
                NavigationType.GOOGLE -> {
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(googleNavUrl))
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        activity?.startActivity(intent)
                        this.doctorDetailsViewModel.saveNavigationType(NavigationType.GOOGLE)
                        this.displayNavigationTypeDialog!!.dismiss()
                    } catch (ex: Exception) {
                        if (BuildConfig.DEBUG) {
                            if (ex.localizedMessage != null) {
                                Log.d(TAG, "Failed to open Google: ${ex.localizedMessage!!}")
                            }
                        }
                        val installIntent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.google.android.apps.maps"))
                        installIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        activity?.startActivity(installIntent)
                    }
                }
                else -> {

                }
            }
        }
    }

    companion object {
        @JvmStatic
        private val TAG: String = DoctorsDetails::class.java.canonicalName!!
    }
}
