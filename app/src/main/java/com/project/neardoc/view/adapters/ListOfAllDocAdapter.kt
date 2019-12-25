package com.project.neardoc.view.adapters

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.project.neardoc.model.localstoragemodels.DocAndRelations

class ListOfAllDocAdapter constructor(private val context: Context): PagedListAdapter<DocAndRelations, BaseViewHolder<*>>(
    DOC_AND_RELATION_COMPARATOR) {

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
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
    inner class ListOfDocViewHolder constructor(view: View) {

    }
}