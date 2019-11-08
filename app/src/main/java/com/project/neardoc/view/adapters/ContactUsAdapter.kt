package com.project.neardoc.view.adapters

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class ContactUsAdapter constructor(private val context: Context): RecyclerView.Adapter<BaseViewHolder<*>>() {

    companion object {
        private const val TYPE_HEADER = 0
    }
    private var data: List<Any> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getItemCount(): Int {
       return this.data.size
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}