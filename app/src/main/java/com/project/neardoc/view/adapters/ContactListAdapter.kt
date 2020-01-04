package com.project.neardoc.view.adapters

import android.os.Build
import android.telephony.PhoneNumberUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import com.project.neardoc.R
import com.project.neardoc.view.adapters.models.ContactEmail
import com.project.neardoc.view.adapters.models.ContactHeader
import com.project.neardoc.view.adapters.models.ContactPhone
import java.lang.IllegalArgumentException
import java.util.*

class ContactListAdapter constructor(private val listener: ItemClickListener): RecyclerView.Adapter<BaseViewHolder<*>>() {

    private var data: MutableList<Any> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
       return when (viewType) {
           HEADER_VIEW -> {
               val view: View = LayoutInflater.from(parent.context).inflate(R.layout.bottomsheet_contact_header_layout, parent, false)
               ContactHeaderViewHolder(view)
           }
           PHONE_VIEW -> {
               val view: View = LayoutInflater.from(parent.context).inflate(R.layout.bottomsheet_contacts_phone_items_layout, parent, false)
               val contactPhoneViewHolder = ContactPhoneViewHolder(view)
               contactPhoneViewHolder.getPhoneView().let {
                   it.setOnClickListener {
                       val contactPhone: ContactPhone = contactPhoneViewHolder.itemView.tag as ContactPhone
                       this.listener.onClick(contactPhone)
                   }
               }
               contactPhoneViewHolder
           }
           EMAIL_VIEW -> {
               val view: View = LayoutInflater.from(parent.context).inflate(R.layout.bottomsheet_contacts_email_item_layout, parent, false)
               val contactPhoneViewHolder = ContactEmailViewHolder(view)
               contactPhoneViewHolder.getEmailView().let {
                   it.setOnClickListener {
                       val contactEmail: ContactEmail = contactPhoneViewHolder.itemView.tag as ContactEmail
                       this.listener.onClick(contactEmail)
                   }
               }
               contactPhoneViewHolder
           }
           else -> throw IllegalArgumentException("Unsupported View")
       }
    }

    override fun getItemCount(): Int {
        return this.data.size
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
       val element: Any = this.data[position]
        when (holder) {
            is ContactHeaderViewHolder -> holder.bindView(element as ContactHeader)
            is ContactPhoneViewHolder -> holder.bindView(element as ContactPhone)
            is ContactEmailViewHolder -> holder.bindView(element as ContactEmail)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (this.data[position]) {
            is ContactHeader -> HEADER_VIEW
            is ContactPhone -> PHONE_VIEW
            is ContactEmail -> EMAIL_VIEW
            else -> throw IllegalArgumentException("Unsupported View")
        }
    }

    fun setData(data: MutableList<Any>) {
        this.data.clear()
        this.data.addAll(data)
        notifyDataSetChanged()
    }

    inner class ContactHeaderViewHolder constructor(val view: View): BaseViewHolder<ContactHeader>(view) {
        private var headerView: MaterialTextView?= null

        init {
            this.headerView = this.view.findViewById(R.id.bottom_sheet_contact_header_view_id)
        }

        override fun bindView(item: ContactHeader) {
            item.let {
                this.headerView?.let {
                    it.text = item.header
                }
            }
        }
    }
    inner class ContactPhoneViewHolder constructor(val view: View): BaseViewHolder<ContactPhone>(view) {
        private var phoneView: MaterialTextView?= null

        init {
            this.phoneView = this.view.findViewById(R.id.bottom_sheet_contact_phone_view_id)
        }

        override fun bindView(item: ContactPhone) {
            itemView.tag = item
            item.let {
                this.phoneView?.let {
                    val unFormatedPhoneNumber: String = item.phone
                    val formatedPhoneNumber: String = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        PhoneNumberUtils.formatNumber(unFormatedPhoneNumber, Locale.US.country)
                    } else {
                        PhoneNumberUtils.formatNumber(unFormatedPhoneNumber)
                    }
                    it.text = formatedPhoneNumber
                }
            }
        }

        fun getPhoneView(): MaterialTextView {
            return this.phoneView!!
        }
    }

    inner class ContactEmailViewHolder constructor(val view: View): BaseViewHolder<ContactEmail>(view) {
        private var emailView: MaterialTextView?= null
        init {
            this.emailView = this.view.findViewById(R.id.bottom_sheet_contact_email_view_id)
        }

        override fun bindView(item: ContactEmail) {
            itemView.tag = item
            item.let {
                this.emailView?.let {
                    it.text = item.email
                }
            }
        }
        fun getEmailView(): MaterialTextView {
            return this.emailView!!
        }
    }

    companion object {
        private const val HEADER_VIEW: Int = 0
        private const val PHONE_VIEW: Int = 1
        private const val EMAIL_VIEW: Int = 2
    }

    interface ItemClickListener {
        fun <T> onClick(items: T)
    }
}