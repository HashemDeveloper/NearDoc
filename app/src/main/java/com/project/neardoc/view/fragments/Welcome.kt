package com.project.neardoc.view.fragments


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

import com.project.neardoc.R
import com.project.neardoc.di.Injectable
import com.project.neardoc.events.UserStateEvent
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_welcome.*
import kotlinx.android.synthetic.main.near_by_main_layout.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class Welcome : Fragment(), Injectable{
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        AndroidSupportInjection.inject(this)
        return inflater.inflate(R.layout.fragment_welcome, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val holdAnimation: Animation = AnimationUtils.loadAnimation(context, R.anim.anim_hold)
        val translateScale = AnimationUtils.loadAnimation(context, R.anim.anim_translate_scale)
        translateScale.setAnimationListener(object : Animation.AnimationListener{
            override fun onAnimationRepeat(p0: Animation?) {

            }

            override fun onAnimationEnd(p0: Animation?) {
                fragment_welcome_splash_image_view_id.clearAnimation()
                val openLoginPage = findNavController()
                openLoginPage.navigate(R.id.login)
            }

            override fun onAnimationStart(p0: Animation?) {
            }
        })
        holdAnimation.setAnimationListener(object : Animation.AnimationListener{
            override fun onAnimationRepeat(p0: Animation?) {

            }

            override fun onAnimationEnd(p0: Animation?) {
                fragment_welcome_splash_image_view_id.clearAnimation()
                fragment_welcome_splash_image_view_id.startAnimation(translateScale)
            }

            override fun onAnimationStart(p0: Animation?) {

            }
        })
        fragment_welcome_splash_image_view_id.startAnimation(holdAnimation)
    }
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onUserStateEvent(userStateEvent: UserStateEvent) {
        if (userStateEvent.isLoggedIn) {
            val navigateToHomePage = findNavController()
            navigateToHomePage.navigate(R.id.homePage)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        EventBus.getDefault().unregister(this)
    }
}
