package com.project.neardoc.utils

import androidx.lifecycle.LiveData

interface IUserStateService {
    fun getObserver(): UserStateService
}