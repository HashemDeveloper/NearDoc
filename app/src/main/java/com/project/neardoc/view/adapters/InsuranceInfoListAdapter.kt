package com.project.neardoc.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import com.project.neardoc.R
import com.project.neardoc.view.adapters.models.InsurancePlanAndProvider

class InsuranceInfoListAdapter: RecyclerView.Adapter<BaseViewHolder<*>>() {
    private val data: MutableList<InsurancePlanAndProvider> = arrayListOf()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.fragment_insurance_list_items, parent, false)
        return InsuranceInfoListViewHolder(view)
    }

    override fun getItemCount(): Int {
        return  this.data.size
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
       val insurancePlanProvider: InsurancePlanAndProvider = this.data[position]
       insurancePlanProvider.let {
           (holder as InsuranceInfoListViewHolder).bindView(it)
       }
    }
    fun setData(list: MutableList<InsurancePlanAndProvider>) {
        this.data.clear()
        this.data.addAll(list)
        notifyDataSetChanged()
    }

    inner class InsuranceInfoListViewHolder(val view: View): BaseViewHolder<InsurancePlanAndProvider>(view) {
        private var insurancePlanView: MaterialTextView?= null
        private var insuranceProviderView: MaterialTextView?= null

        init {
            this.insurancePlanView = this.view.findViewById(R.id.fragment_insurance_plan_view_id)
            this.insuranceProviderView = this.view.findViewById(R.id.fragment_insurance_provider_view_id)
        }
        override fun bindView(item: InsurancePlanAndProvider) {
            this.insurancePlanView?.let {
                it.text = item.insurancePlan.name
            }
            this.insuranceProviderView?.let {
                it.text = item.insuranceProvider.name
            }
        }
    }
}