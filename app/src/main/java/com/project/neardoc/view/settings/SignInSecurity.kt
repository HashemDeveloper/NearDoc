package com.project.neardoc.view.settings


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager

import com.project.neardoc.R
import com.project.neardoc.di.Injectable
import com.project.neardoc.events.LandInSettingPageEvent
import com.project.neardoc.model.ManageAccountHeader
import com.project.neardoc.model.ManageAccountModel
import com.project.neardoc.model.SignInSecurityHeaderModel
import com.project.neardoc.model.SignInSecurityModel
import com.project.neardoc.utils.PageType
import com.project.neardoc.view.adapters.SignInSecClickListener
import com.project.neardoc.view.adapters.SignInSecurityAdapter
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_sign_in_and_security.*
import kotlinx.android.synthetic.main.near_by_main_layout.*
import org.greenrobot.eventbus.EventBus

class SignInSecurity : Fragment(), Injectable, SignInSecClickListener {
    private var signInSecurityAdapter: SignInSecurityAdapter?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)
        EventBus.getDefault().postSticky(LandInSettingPageEvent(true, PageType.SIGN_IN_SECURITY))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sign_in_and_security, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.signInSecurityAdapter = SignInSecurityAdapter(context!!, this)
        fragment_sign_in_security_recycler_view_id.layoutManager = LinearLayoutManager(context)
        fragment_sign_in_security_recycler_view_id.adapter = this.signInSecurityAdapter

        val headerModel = SignInSecurityHeaderModel("Login Information")
        val infoModel = SignInSecurityModel("Hashem", "JuniaYulia", "******")
        val manageHeader = ManageAccountHeader("Manage Account")
        val deleteAccount = ManageAccountModel("juniayulia@outlook.com")

        val dataList: List<Any> = arrayListOf(headerModel, infoModel, manageHeader, deleteAccount)
        this.signInSecurityAdapter?.setData(dataList)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().postSticky(LandInSettingPageEvent(false, PageType.SETTINGS_FRAGMENT))
    }

    override fun <T> onClick(items: T, model: String) {
        when (items) {
            is SignInSecurityModel -> {
                if (items.username == model) {
                    Toast.makeText(context, "Username: " + items.username, Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "PASSWORD", Toast.LENGTH_SHORT).show()
                }
            }
            is ManageAccountModel -> {
                Toast.makeText(context, "Email: " + items.deleteEmail, Toast.LENGTH_SHORT).show()
            }
        }
    }
}
