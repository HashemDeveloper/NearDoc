package com.project.neardoc.view.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation

import com.project.neardoc.R
import com.project.neardoc.di.Injectable
import kotlinx.android.synthetic.main.fragment_welcome.*

class Welcome : Fragment(), Injectable{

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_welcome, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        welcome_page_create_account_bt_id.setOnClickListener{
            val navToRegistration = WelcomeDirections.actionRegistrationFragment()
            Navigation.findNavController(it).navigate(navToRegistration)
        }
    }
}
