package com.project.neardoc.view.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.project.neardoc.R
import com.project.neardoc.model.SignInSecurityHeaderModel
import kotlinx.android.synthetic.main.fragment_contact_us_view_email_layout.view.*
import java.lang.IllegalArgumentException

class ContactUsAdapter constructor(private val context: Context, private val listener: ContactClickListener) :
    RecyclerView.Adapter<BaseViewHolder<*>>() {

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_CONTENT = 1
    }

    private var data: List<Any> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        return when (viewType) {
            TYPE_HEADER -> {
                val headerView: View = LayoutInflater.from(this.context).inflate(R.layout.fragment_sign_in_sec_info_header_holder, parent, false)
                HeaderViewModel(headerView)
            }
            TYPE_CONTENT -> {
                val contentView: View = LayoutInflater.from(this.context).inflate(R.layout.fragment_contact_us_view_email_layout, parent, false)
                val contentViewHolder = ContentViewHolder(contentView)
                contentViewHolder.getEmailCardView()?.setOnClickListener {
                    val email: String = contentViewHolder.itemView.tag as String
                    this.listener.onEmailClicked(email)
                }
                return contentViewHolder
            }
            else -> throw IllegalArgumentException("Unsupported view!")
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        val element: Any = this.data[position]
        when (holder) {
            is HeaderViewModel -> holder.bindView(element as SignInSecurityHeaderModel)
            is ContentViewHolder -> holder.bindView(element as String)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (this.data[position]) {
            is SignInSecurityHeaderModel -> TYPE_HEADER
            is String -> TYPE_CONTENT
            else -> throw IllegalArgumentException("Invalid Data Type")
        }
    }

    fun setData(data: List<Any>) {
        this.data = data
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return this.data.size
    }

    inner class HeaderViewModel constructor(itemView: View) :
        BaseViewHolder<SignInSecurityHeaderModel>(itemView) {
        private var headerTextView: AppCompatTextView? = null

        init {
            this.headerTextView =
                this.itemView.findViewById(R.id.fragment_sign_in_security_holder_header_view_id)
        }

        override fun bindView(item: SignInSecurityHeaderModel) {
            this.headerTextView?.text = item.header
        }
    }

    inner class ContentViewHolder constructor(itemView: View) : BaseViewHolder<String>(itemView) {
        private var contentTextView: AppCompatTextView? = null
        private var emailCardView: CardView?= null

        init {
            this.contentTextView =
                this.itemView.findViewById(R.id.fragment_contact_us_view_email_email_id)
            this.emailCardView = this.itemView.findViewById(R.id.fragment_contact_us_email_holder_id)
        }

        override fun bindView(item: String) {
            itemView.tag = item
            this.contentTextView?.text = item
        }
        fun getEmailCardView(): CardView? {
            return if (this.emailCardView != null) {
                this.emailCardView
            } else {
                return null
            }
        }
    }
    interface ContactClickListener {
        fun onEmailClicked(email: String)
    }
}