package com.project.neardoc.view.adapters

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.textview.MaterialTextView
import com.project.neardoc.R
import com.project.neardoc.model.localstoragemodels.DocAndRelations
import com.project.neardoc.model.localstoragemodels.DocProfile
import com.project.neardoc.model.localstoragemodels.DocRatings
import kotlinx.android.synthetic.main.search_page_list_of_doc_layout.view.*
import me.zhanghai.android.materialratingbar.MaterialRatingBar

class ListOfAllDocAdapter constructor(private val context: Context): PagedListAdapter<DocAndRelations, RecyclerView.ViewHolder>(
    DOC_AND_RELATION_COMPARATOR) {

    fun setData(docList: PagedList<DocAndRelations>) {
        this.submitList(docList)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View = LayoutInflater.from(this.context).inflate(R.layout.search_page_list_of_doc_layout, parent, false)
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

        fun bindView(data: DocAndRelations) {
            val docProfileList: List<DocProfile> = data.docProfile
            val docRatingList: List<DocRatings> = data.docRating

            for (docProfile in docProfileList) {
                docProfile.let {profile ->
                    this.doctorImageView?.let {imageView ->
                        val imageUrl: String = Uri.parse(profile.imageUrl).toString()
                        Glide.with(context).load(imageUrl).into(imageView)
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
}