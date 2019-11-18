package com.project.neardoc.viewmodel

import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import com.github.florent37.viewanimator.ViewAnimator
import com.google.android.material.textview.MaterialTextView
import com.project.neardoc.R
import com.project.neardoc.utils.IDeviceSensors
import javax.inject.Inject

class AccountPageViewModel @Inject constructor(): ViewModel() {
    @Inject
    lateinit var iSensors: IDeviceSensors

    fun setupDeviceSensor(activity: FragmentActivity, fragmentAccountRoomTempViewId: MaterialTextView?) {
        this.iSensors.setupDeviceSensor(activity, fragmentAccountRoomTempViewId!!)
    }

    fun clearObservers(tempView: MaterialTextView?) {
        this.iSensors.clearObservers(tempView!!)
    }

    fun animateBreathingTitleView(breathingTitleView: MaterialTextView?) {
        ViewAnimator
            .animate(breathingTitleView)
            .scale(0f, 1f)
            .duration(500)
            .onStart {
                kotlin.run {
                    breathingTitleView!!.setText(R.string.let_s_do_breathing_exercises)
                }
            }
            .start()
    }

    fun startAnimation(
        breathingImageView: AppCompatImageView?,
        breathingGuideTextView: MaterialTextView?
    ) {

        ViewAnimator
            .animate(breathingImageView)
            .alpha(0f, 1f)
            .onStart {
                kotlin.run {
                    breathingGuideTextView!!.visibility = View.VISIBLE
                    breathingGuideTextView.setText(R.string.inhale_exhale)
                }
            }
            .decelerate()
            .duration(1000)
            .thenAnimate(breathingImageView)
            .scale(0.02f, 1.5f, 0.02f)
            .rotation(360f)
            .repeatCount(6)
            .accelerate()
            .duration(5000)
            .onStop { 
                kotlin.run { 
                    breathingGuideTextView!!.setText(R.string.good_job)
                    breathingImageView!!.scaleX = 1.0f
                    breathingImageView.scaleY = 1.0f
                }
            }
            .start()
    }
}