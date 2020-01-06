package com.project.neardoc.view.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.neardoc.view.fragments.InsuranceListArgs.fromBundle

import com.project.neardoc.R
import com.project.neardoc.di.Injectable
import com.project.neardoc.view.adapters.InsuranceInfoListAdapter
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_insurance_list.*

class InsuranceList : Fragment(), Injectable {
    private val insuranceInfoList by lazy {
        fromBundle(arguments!!).insuranceInfo
    }
    private var insuranceInfoListAdapter: InsuranceInfoListAdapter?= null


    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_insurance_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        insurance_list_view_id.layoutManager = LinearLayoutManager(this.context!!)
        this.insuranceInfoListAdapter = InsuranceInfoListAdapter()
        if (this.insuranceInfoList != null) {
            fragment_insurance_list_error_view_id.visibility = View.GONE
            this.insuranceInfoListAdapter?.setData(this.insuranceInfoList?.list!!)
            insurance_list_view_id.adapter = this.insuranceInfoListAdapter
        } else {
            fragment_insurance_list_error_view_id.visibility = View.VISIBLE
        }
    }
}
