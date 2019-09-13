package com.project.neardoc.view.fragments


import android.content.ComponentCallbacks
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcher
import androidx.navigation.Navigation

import com.project.neardoc.R
import com.project.neardoc.di.Injectable
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class Login : Fragment(), Injectable, CoroutineScope {

    private val job = Job()

    override val coroutineContext: CoroutineContext
        get() = this.job + Dispatchers.Main

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        AndroidSupportInjection.inject(this)
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val animation: Animation = AnimationUtils.loadAnimation(context, android.R.anim.fade_in)
        fragment_login_logo_text_view.startAnimation(animation)
        fragment_login_container_id.startAnimation(animation)
        fragment_login_create_new_account_bt_id.startAnimation(animation)
        performNavigateActions()
        onBackPressed()
    }
    private fun onBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().finish()
            }
        })
    }
    private fun performNavigateActions() {
        fragment_login_forgot_pass_bt_id.setOnClickListener{
            val navigateToForgoPasswordPage = LoginDirections.actionForgotPassword()
            Navigation.findNavController(it).navigate(navigateToForgoPasswordPage)
        }
        fragment_login_create_new_account_bt_id.setOnClickListener{
            val navigateToRegistrationPage = LoginDirections.actionRegistration()
            Navigation.findNavController(it).navigate(navigateToRegistrationPage)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}
