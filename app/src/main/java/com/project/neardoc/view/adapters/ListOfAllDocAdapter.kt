package com.project.neardoc.view.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import com.project.neardoc.R
import com.project.neardoc.model.localstoragemodels.DocAndRelations
import com.project.neardoc.model.localstoragemodels.DocPractice
import com.project.neardoc.model.localstoragemodels.DocProfile
import com.project.neardoc.model.localstoragemodels.DocRatings
import com.project.neardoc.utils.GlideApp
import com.project.neardoc.utils.roundOff1DecimalPoint
import com.project.neardoc.utils.roundOff2DecimalPoint
import me.zhanghai.android.materialratingbar.MaterialRatingBar

class ListOfAllDocAdapter constructor(private val context: Context, private val listener: DocListClickListener): PagedListAdapter<DocAndRelations, RecyclerView.ViewHolder>(
    DOC_AND_RELATION_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View = LayoutInflater.from(this.context).inflate(R.layout.search_page_list_of_doc_layout, parent, false)
        val viewHolder = ListOfDocViewHolder(view)
        viewHolder.getDistanceContainerView().setOnClickListener {
            val data: DocAndRelations = viewHolder.itemView.tag as DocAndRelations
            this.listener.onDistanceContainerClicked(data)
        }
        viewHolder.getViewMoreDetailsBt().setOnClickListener {
            val data: DocAndRelations = viewHolder.itemView.tag as DocAndRelations
            this.listener.onViewMoreBtClicked(data)
        }
        return ListOfDocViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
       val docAndRelations: DocAndRelations = getItem(position)!!
        docAndRelations.let {
            (holder as ListOfDocViewHolder).bindView(it)
        }
    }
    inner class ListOfDocViewHolder constructor(view: View): RecyclerView.ViewHolder(view) {
        private var doctorImageView: AppCompatImageView?= null
        private var doctorRatingBar: MaterialRatingBar?= null
        private var doctorsNameTextView: MaterialTextView?= null
        private var doctorsDescriptionTextView: MaterialTextView?= null
        private var distanceContainerView: RelativeLayout?= null
        private var distanceInMeterTextView: MaterialTextView?= null
        private var viewMoreDetailsBt: AppCompatImageView?= null

        init {
            this.doctorImageView = view.findViewById(R.id.search_page_list_of_doc_image_view_id)
            this.doctorRatingBar = view.findViewById(R.id.search_page_list_of_doc_rating_bar_id)
            this.doctorsNameTextView = view.findViewById(R.id.search_page_list_doc_name_view_id)
            this.doctorsDescriptionTextView = view.findViewById(R.id.search_page_list_doc_description_view_id)
            this.distanceContainerView = view.findViewById(R.id.search_page_list_doc_distance_container_id)
            this.distanceInMeterTextView = view.findViewById(R.id.search_page_list_distance_in_meter_text_view_id)
            this.viewMoreDetailsBt = view.findViewById(R.id.page_search_list_details_bt_id)
        }

        fun getDistanceContainerView(): RelativeLayout {
            return this.distanceContainerView!!
        }
        fun getViewMoreDetailsBt(): AppCompatImageView {
            return this.viewMoreDetailsBt!!
        }
        fun bindView(data: DocAndRelations) {
            this.itemView.tag = data
            val docProfileList: List<DocProfile> = data.docProfile
            val docRatingList: List<DocRatings> = data.docRating
            val docPracticeList: List<DocPractice> = data.docPractice

            for (docPractice: DocPractice in docPracticeList) {
                docPractice.let {practice ->
                    this.distanceInMeterTextView?.let {
                        val distance: String = if (practice.distance >= 10.0) {
                            roundOff1DecimalPoint(practice.distance).toString() + "mi"
                        } else {
                            roundOff2DecimalPoint(practice.distance).toString() + "mi"
                        }
                        it.text = distance
                    }
                }
            }
            for (docProfile in docProfileList) {
                docProfile.let {profile ->
                    this.doctorImageView?.let {imageView ->
                        GlideApp.with(context).load(profile.imageUrl).into(imageView)
                    }
                    this.doctorsNameTextView?.let {nameView ->
                        val docName: String = profile.firstName + " " + profile.lastName
                        nameView.text = docName
                    }
                    this.doctorsDescriptionTextView?.let {
                        val description: String = if (profile.bio.length > 100) {
                            profile.bio.substring(0, 100) + "..."
                        } else {
                            profile.bio
                        }
                        it.text = description
                    }
                }
            }
            for (docRating: DocRatings in docRatingList) {
                docRating.let {rating ->
                    this.doctorRatingBar?.let {ratingView ->
                        ratingView.rating = rating.rating.toFloat()
                    }
                }
            }
        }
    }

    companion object {
        private val DOC_AND_RELATION_COMPARATOR = object : DiffUtil.ItemCallback<DocAndRelations>() {
            override fun areItemsTheSame(
                oldItem: DocAndRelations,
                newItem: DocAndRelations
            ): Boolean = oldItem.doc == newItem.doc && oldItem.docProfile == newItem.docProfile && oldItem.docRating == newItem.docRating

            override fun areContentsTheSame(
                oldItem: DocAndRelations,
                newItem: DocAndRelations
            ): Boolean = oldItem == newItem
        }
    }
    interface DocListClickListener {
        fun onDistanceContainerClicked(data: DocAndRelations)
        fun onViewMoreBtClicked(data: DocAndRelations)
    }
}