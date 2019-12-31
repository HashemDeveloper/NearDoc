package com.project.neardoc.viewmodel

import androidx.lifecycle.ViewModel
import com.project.neardoc.data.local.ISharedPrefService
import com.project.neardoc.utils.NavigationType
import javax.inject.Inject

class DoctorDetailsViewModel @Inject constructor(): ViewModel() {

    @Inject
    lateinit var iSharedPrefService: ISharedPrefService


    fun getNavigationType(): String {
        return this.iSharedPrefService.getNavigationType()
    }

    fun saveNavigationType(navType: NavigationType) {
        this.iSharedPrefService.saveNavigationType(navType.name)
    }
}