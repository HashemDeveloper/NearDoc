package com.project.neardoc.view.widgets

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.project.neardoc.R
import com.project.neardoc.utils.BottomSheetType
import com.project.neardoc.utils.NavigationType
import com.project.neardoc.view.adapters.ContactListAdapter
import com.project.neardoc.view.adapters.models.ContactEmail
import com.project.neardoc.view.adapters.models.ContactPhone
import kotlinx.android.synthetic.main.bottom_sheet_navigation_type_layout.*
import kotlinx.android.synthetic.main.bottomsheet_contacts_main_layout.*

class NavTypeBottomSheetDialog constructor(private val bottomSheetType: BottomSheetType, private val dataList: MutableList<Any>?):
    BottomSheetDialogFragment(), ContactListAdapter.ItemClickListener {
    private val clickLiveDataObserver: MutableLiveData<Any> = MutableLiveData()
    private var contactListAdapter: ContactListAdapter?= null
    override fun getTheme(): Int {
        return R.style.BottomSheetDialogTheme
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(), theme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return when (this.bottomSheetType) {
            BottomSheetType.Navigation -> {
                inflater.inflate(R.layout.bottom_sheet_navigation_type_layout, container, false)
            }
            BottomSheetType.Contacts -> {
                inflater.inflate(R.layout.bottomsheet_contacts_main_layout, container, false)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        when (this.bottomSheetType) {
            BottomSheetType.Navigation -> {
                initNavigationView()
            }
            BottomSheetType.Contacts -> {
                bottom_sheet_contacts_layout_recyclerview_id.layoutManager = LinearLayoutManager(this.context)
                this.contactListAdapter = ContactListAdapter(this)
                bottom_sheet_contacts_layout_recyclerview_id.adapter = this.contactListAdapter
                this.contactListAdapter?.setData(this.dataList!!)
            }
        }
    }
    private fun initNavigationView() {
        bottom_sheet_use_waze_opt_text_view_id.setOnClickListener {
            this.clickLiveDataObserver.value = NavigationType.WAZE
        }
        bottom_sheet_use_google_opt_text_view_id.setOnClickListener {
            this.clickLiveDataObserver.value = NavigationType.GOOGLE
        }
    }
    fun getClickObserver(): MutableLiveData<Any> {
        return this.clickLiveDataObserver
    }

    override fun <T> onClick(items: T) {
        this.clickLiveDataObserver.value = items
//        when (items) {
//            is ContactPhone -> {
//                val phone: ContactPhone = items
//                val phoneNumber: String = phone.phone
//                val callIntent = Intent(Intent.ACTION_CALL)
//                callIntent.data = Uri.parse("tel:$phoneNumber")
////                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
////                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CALL_PHONE},REQUEST_PHONE_CALL);
////                }
////                else
////                {
////                    startActivity(intent);
////                }
//                if (ContextCompat.checkSelfPermission(this.context!!, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
//
//                }
//                context!!.startActivity(callIntent)
//            }
//            is ContactEmail -> {
//
//            }
//        }
    }
}