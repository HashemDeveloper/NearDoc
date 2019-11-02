package com.project.neardoc.utils

import androidx.lifecycle.LiveData
import com.google.firebase.auth.FirebaseAuth


interface IUserStateService {
    fun getAuthObserver(): UserStateService
}