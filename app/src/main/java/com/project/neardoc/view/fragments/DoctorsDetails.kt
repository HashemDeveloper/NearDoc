package com.project.neardoc.view.fragments


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.project.neardoc.view.fragments.DoctorsDetailsArgs.fromBundle
import com.project.neardoc.R
import com.project.neardoc.di.Injectable
import com.project.neardoc.model.localstoragemodels.DocAndRelations

class DoctorsDetails : Fragment(), Injectable {

    private val doctorsDetails by lazy {
        fromBundle(arguments!!).doctorsDetails
    }
    override fun onCreate(savedInstanceState: Bundle?) {
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
        val docData: DocAndRelations = this.doctorsDetails
        for (docProfile in docData.docProfile) {
            Log.d("Details", "Name: ${docProfile.firstName}")
        }
    }
}
