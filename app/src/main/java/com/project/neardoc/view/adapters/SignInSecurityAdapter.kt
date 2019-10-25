package com.project.neardoc.view.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.project.neardoc.R
import com.project.neardoc.model.ManageAccountHeader
import com.project.neardoc.model.ManageAccountModel
import com.project.neardoc.model.SignInSecurityHeaderModel
import com.project.neardoc.model.SignInSecurityModel
import java.lang.IllegalArgumentException

class SignInSecurityAdapter constructor(private val context: Context, private val clickListener: SignInSecClickListener) :
    RecyclerView.Adapter<BaseViewHolder<*>>() {
    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_SIGN_SEC_CONTENT = 1
        private const val TYPE_MANAGE_ACC_HEADER = 2
        private const val TYPE_MANAGE_ACC_CONTENT = 3
    }

    private var data: List<Any> = emptyList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        return when(viewType) {
            TYPE_HEADER -> {
                val view: View = LayoutInflater.from(this.context).inflate(R.layout.fragment_sign_in_sec_info_header_holder, parent, false)
                HeaderViewHolder(view)
            }
            TYPE_SIGN_SEC_CONTENT -> {
                val view: View = LayoutInflater.from(this.context).inflate(R.layout.fragment_sign_in_sec_info_item_holder, parent, false)
                val infoViewHolder = InfoViewHolder(view)
                infoViewHolder.getUsernameHolder()?.setOnClickListener {
                    val signInSecurityModel = infoViewHolder.itemView.tag as SignInSecurityModel
                    this.clickListener.onClick(signInSecurityModel, signInSecurityModel.email)
                }
                infoViewHolder.getPasswordHolder()?.setOnClickListener {
                    val signInSecurityModel = infoViewHolder.itemView.tag as SignInSecurityModel
                    this.clickListener.onClick(signInSecurityModel, signInSecurityModel.password)
                }
                infoViewHolder
            }
            TYPE_MANAGE_ACC_HEADER -> {
                val view: View = LayoutInflater.from(this.context).inflate(R.layout.fragment_sign_in_sec_info_header_holder, parent, false)
                ManageAccHeaderHolder(view)
            }
            TYPE_MANAGE_ACC_CONTENT -> {
                val view: View = LayoutInflater.from(this.context).inflate(R.layout.fragment_sign_in_sec_manage_account_holder, parent, false)
                val deleteHolder = ManageAccDeleteHolder(view)
                deleteHolder.getDeleteBt()?.setOnClickListener {
                    val manageAccountModel: ManageAccountModel = deleteHolder.itemView.tag as ManageAccountModel
                    this.clickListener.onClick(manageAccountModel, manageAccountModel.deleteEmail)
                }
                deleteHolder
            }
            else -> throw IllegalArgumentException("Unsupported view")
        }
    }

    override fun getItemCount(): Int {
        return this.data.size
    }
    fun setData(data: List<Any>) {
        this.data = data
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        val element = this.data[position]
        when (holder) {
            is HeaderViewHolder -> holder.bindView(element as SignInSecurityHeaderModel)
            is InfoViewHolder -> holder.bindView(element as SignInSecurityModel)
            is ManageAccHeaderHolder -> holder.bindView(element as ManageAccountHeader)
            is ManageAccDeleteHolder -> holder.bindView(element as ManageAccountModel)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (this.data[position]) {
            is SignInSecurityHeaderModel -> TYPE_HEADER
            is SignInSecurityModel -> TYPE_SIGN_SEC_CONTENT
            is ManageAccountHeader -> TYPE_MANAGE_ACC_HEADER
            is ManageAccountModel -> TYPE_MANAGE_ACC_CONTENT
            else -> throw IllegalArgumentException("Invalid Type Of Data")
        }
    }

    inner class HeaderViewHolder constructor(itemView: View) :
        BaseViewHolder<SignInSecurityHeaderModel>(itemView) {
        private var textView: AppCompatTextView? = null

        init {
            this.textView =
                this.itemView.findViewById(R.id.fragment_sign_in_security_holder_header_view_id)
        }

        override fun bindView(item: SignInSecurityHeaderModel) {
            this.textView?.text = item.header
        }
    }

    inner class InfoViewHolder constructor(itemView: View) :
        BaseViewHolder<SignInSecurityModel>(itemView) {
        private var nameView: AppCompatTextView? = null
        private var usernameView: AppCompatTextView? = null
        private var passwordView: AppCompatTextView? = null
        private var usernameHolder: RelativeLayout?= null
        private var passwordHolder: RelativeLayout?= null

        init {
            this.nameView = this.itemView.findViewById(R.id.fragment_sign_in_sec_name_view_id)
            this.usernameView =
                this.itemView.findViewById(R.id.fragment_sign_in_sec_item_username_view_id)
            this.passwordView =
                this.itemView.findViewById(R.id.fragment_sign_in_sec_item_password_view_id)
            this.usernameHolder = this.itemView.findViewById(R.id.fragment_sign_in_sec_username_holder_id)
            this.passwordHolder = this.itemView.findViewById(R.id.fragment_sign_in_sec_password_holder_id)
        }

        override fun bindView(item: SignInSecurityModel) {
            this.itemView.tag = item
            if (this.nameView != null) {
                this.nameView?.text = item.fullName
            }
            if (this.usernameView != null) {
                this.usernameView?.text = item.email
            }
            if (this.passwordView != null) {
                this.passwordView?.text = item.password
            }
        }
        fun getUsernameHolder() : RelativeLayout? {
            return if (this.usernameHolder != null) {
                this.usernameHolder!!
            } else {
                null
            }
        }
        fun getPasswordHolder(): RelativeLayout? {
            return if (this.passwordHolder != null) {
                this.passwordHolder!!
            } else {
                null
            }
        }
    }

    inner class ManageAccHeaderHolder constructor(itemView: View) :
            BaseViewHolder<ManageAccountHeader>(itemView) {
        private var headerView: AppCompatTextView?= null

        init {
            this.headerView = this.itemView.findViewById(R.id.fragment_sign_in_security_holder_header_view_id)
        }
        override fun bindView(item: ManageAccountHeader) {
            if (this.headerView != null) {
                this.headerView?.text = item.header
            }
        }
    }

    inner class ManageAccDeleteHolder constructor(itemView: View) :
            BaseViewHolder<ManageAccountModel>(itemView) {
        private var emailView: AppCompatTextView?= null
        private var deleteBt: AppCompatImageView?= null

        init {
            this.emailView = this.itemView.findViewById(R.id.fragment_sign_in_sec_manage_account_email_view_id)
            this.deleteBt = this.itemView.findViewById(R.id.fragment_sign_in_sec_manage_account_delete_icon_id)
        }
        override fun bindView(item: ManageAccountModel) {
            this.itemView.tag = item
            if (this.emailView != null) {
                this.emailView?.text = item.deleteEmail
            }
        }
        fun getDeleteBt(): AppCompatImageView? {
            return if (this.deleteBt != null) {
                this.deleteBt!!
            } else {
                null
            }
        }
    }
}

interface SignInSecClickListener {
    fun <T> onClick(items: T, model: String)
}