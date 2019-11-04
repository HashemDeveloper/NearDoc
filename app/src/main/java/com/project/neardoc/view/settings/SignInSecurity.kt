package com.project.neardoc.view.settings


import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder

import com.project.neardoc.R
import com.project.neardoc.data.local.ISharedPrefService
import com.project.neardoc.di.Injectable
import com.project.neardoc.events.LandInSettingPageEvent
import com.project.neardoc.model.ManageAccountHeader
import com.project.neardoc.model.ManageAccountModel
import com.project.neardoc.model.SignInSecurityHeaderModel
import com.project.neardoc.model.SignInSecurityModel
import com.project.neardoc.utils.Constants
import com.project.neardoc.utils.PageType
import com.project.neardoc.view.adapters.SignInSecClickListener
import com.project.neardoc.view.adapters.SignInSecurityAdapter
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_sign_in_and_security.*
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

class SignInSecurity : Fragment(), Injectable, SignInSecClickListener {
    private var signInSecurityAdapter: SignInSecurityAdapter?= null
    @Inject
    lateinit var iSharedPrefService: ISharedPrefService
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
        val fullName: String = this.iSharedPrefService.getUserName()
        val email: String = this.iSharedPrefService.getUserEmail()
        val headerModel = SignInSecurityHeaderModel("Login Information")
        val infoModel = SignInSecurityModel(fullName, email, "******")
        val manageHeader = ManageAccountHeader("Manage Account")
        val deleteAccount = ManageAccountModel(email)

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
                if (items.email == model) {
                    val updateEmailAction = findNavController()
                    val bundle = Bundle()
                    bundle.putString(Constants.WORKER_EMAIL, items.email)
                    updateEmailAction.navigate(R.id.updateEmail, bundle)
                } else {
                   val updatePasswordAction = findNavController()
                    updatePasswordAction.navigate(R.id.updatePassword)
                }
            }
            is ManageAccountModel -> {
                val alertDialog = MaterialAlertDialogBuilder(this.context)
                alertDialog.setTitle("Delete Account")
                alertDialog.setMessage("Are you sure? All your information will be deleted!")
                alertDialog.setPositiveButton("OK") { onClick, i ->
                    Toast.makeText(this.context, "Deleted", Toast.LENGTH_SHORT).show()
                }
                alertDialog.show()
            }
        }
    }
}
