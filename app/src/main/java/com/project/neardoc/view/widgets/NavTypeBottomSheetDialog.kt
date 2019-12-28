package com.project.neardoc.view.widgets

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.project.neardoc.R
import com.project.neardoc.utils.NavigationType
import kotlinx.android.synthetic.main.bottom_sheet_navigation_type_layout.*

class NavTypeBottomSheetDialog: BottomSheetDialogFragment() {
    private val clickLiveDataObserver: MutableLiveData<NavigationType> = MutableLiveData()

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
        return inflater.inflate(R.layout.bottom_sheet_navigation_type_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }
    private fun initView() {
        bottom_sheet_use_waze_opt_text_view_id.setOnClickListener {
            this.clickLiveDataObserver.value = NavigationType.WAZE
        }
        bottom_sheet_use_google_opt_text_view_id.setOnClickListener {
            this.clickLiveDataObserver.value = NavigationType.GOOGLE
        }
    }
    fun getOnClickLiveDataObserver(): MutableLiveData<NavigationType> {
        return this.clickLiveDataObserver
    }
}