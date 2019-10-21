package com.project.neardoc.view.settings


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.project.neardoc.R
import com.project.neardoc.di.Injectable
import dagger.android.support.AndroidSupportInjection

class SignInSecurity : Fragment(), Injectable {
    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sing_in_and_security, container, false)
    }
}
