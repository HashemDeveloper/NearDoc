package com.project.neardoc.viewmodel

import android.content.Context
import android.os.SystemClock
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import com.github.florent37.viewanimator.ViewAnimator
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textview.MaterialTextView
import com.project.neardoc.R
import com.project.neardoc.data.local.ISharedPrefService
import com.project.neardoc.utils.IDeviceSensors
import javax.inject.Inject

class AccountPageViewModel @Inject constructor(): ViewModel() {
    @Inject
    lateinit var iSensors: IDeviceSensors
    @Inject
    lateinit var iSharedPrefService: ISharedPrefService

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

    fun startAnimation(context: Context, activity: FragmentActivity,
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
            .repeatCount(5)
            .accelerate()
            .duration(5000)
            .onStop { 
                kotlin.run {
                    breathingImageView!!.scaleX = 1.0f
                    breathingImageView.scaleY = 1.0f
                    this.iSharedPrefService.setBreathingSession(this.iSharedPrefService.getBreathingSession() + 1)
                    this.iSharedPrefService.setBreath(this.iSharedPrefService.getBreath() + 1)
                    this.iSharedPrefService.setBreathingDate(System.currentTimeMillis())
                    displayResultDialog(context, activity)
                    breathingGuideTextView!!.visibility = View.GONE
                }
            }
            .start()
    }
    private fun displayResultDialog(context: Context, activity: FragmentActivity) {
        val viewGroup: ViewGroup = activity.window.decorView.rootView as ViewGroup
        val resultView: View = activity.layoutInflater.inflate(R.layout.breathing_result_layout, viewGroup, false)
        val resultDialog = MaterialAlertDialogBuilder(context)
        resultDialog.setView(resultView)
        resultDialog.show()
    }
}