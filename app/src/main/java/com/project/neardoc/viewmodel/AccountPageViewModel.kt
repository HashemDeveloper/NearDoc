package com.project.neardoc.viewmodel

import android.content.Context
import android.content.DialogInterface
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bumptech.glide.Glide
import com.github.florent37.viewanimator.ViewAnimator
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.auth.FirebaseAuth
import com.project.neardoc.BuildConfig
import com.project.neardoc.R
import com.project.neardoc.data.local.ISharedPrefService
import com.project.neardoc.data.local.IUserInfoDao
import com.project.neardoc.events.BottomBarEvent
import com.project.neardoc.events.LandInSettingPageEvent
import com.project.neardoc.events.UserStateEvent
import com.project.neardoc.model.localstoragemodels.UserPersonalInfo
import com.project.neardoc.utils.Constants
import com.project.neardoc.utils.sensors.IDeviceSensors
import com.project.neardoc.utils.widgets.PageType
import com.ramotion.fluidslider.FluidSlider
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.*
import org.greenrobot.eventbus.EventBus
import java.text.MessageFormat
import java.util.*
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class AccountPageViewModel @Inject constructor(): ViewModel(), CoroutineScope {

    companion object {
        @JvmStatic private val BREATH_COUNT_MAX: Int = 50
        const val DEFAULT_REPEAT_COUNT = 5
        @JvmStatic
        private val TAG: String = AccountPageViewModel::class.java.canonicalName!!
    }
    @Inject
    lateinit var iSensors: IDeviceSensors
    @Inject
    lateinit var iSharedPrefService: ISharedPrefService
    private var seekBarMinValue: Int?= null
    private var seekBarMaxValue: Int?= null
    private var seekBarHalveValue: Int?= null
    private var seekBarTotalValue: Int?= null
    @Inject
    lateinit var context: Context
    @Inject
    lateinit var iUserInfoDao: IUserInfoDao
    private val job = Job()
    private val eventObserver: MutableLiveData<Boolean> = MutableLiveData()
    private val userPersonalInfoLiveData: MutableLiveData<UserPersonalInfo> = MutableLiveData()

    fun init() {
        val email: String = this.iSharedPrefService.getUserEmail()
        val breathCount: Int = this.iSharedPrefService.getBreath()
//
        var userPersonalInfo: UserPersonalInfo?= null
        launch {
            userPersonalInfo = iUserInfoDao.getUserByEmail(email)
        }.invokeOnCompletion {
            if (it != null && it.localizedMessage != null) {
                if (BuildConfig.DEBUG) {
                    Log.i(TAG, it.localizedMessage!!)
                }
            } else {
                this.userPersonalInfoLiveData.value = userPersonalInfo
            }
        }
    }

    fun setupDeviceSensor(activity: FragmentActivity, fragmentAccountRoomTempViewId: MaterialTextView?, stepCountParentLayout: FrameLayout) {
        this.iSensors.setupDeviceSensor(activity, fragmentAccountRoomTempViewId!!, stepCountParentLayout)
    }

    fun clearObservers(tempView: MaterialTextView?, stepCountView: MaterialTextView?) {
        this.iSensors.clearObservers(tempView!!, stepCountView!!)
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
        val repeatCount: Int?
        val count: Int = this.iSharedPrefService.getRepeatCount()
        repeatCount = if (count >= DEFAULT_REPEAT_COUNT) {
            count
        } else {
            DEFAULT_REPEAT_COUNT
        }

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
            .repeatCount(repeatCount)
            .accelerate()
            .duration(5000)
            .onStop { 
                kotlin.run {
                    breathingImageView!!.scaleX = 1.0f
                    breathingImageView.scaleY = 1.0f
                    this.iSharedPrefService.setBreathingSession(this.iSharedPrefService.getBreathingSession() + 1)
                    this.iSharedPrefService.setBreath(this.iSharedPrefService.getBreath() + 1)
                    this.iSharedPrefService.setBreathingDate(System.currentTimeMillis())
                    displayBreathingResultDialog(context, activity)
                    breathingGuideTextView!!.visibility = View.GONE
                }
            }
            .start()
    }
    private fun displayBreathingResultDialog(context: Context, activity: FragmentActivity) {
        val viewGroup: ViewGroup = activity.window.decorView.rootView as ViewGroup
        val resultView: View = activity.layoutInflater.inflate(R.layout.breathing_result_layout, viewGroup, false)
        val breathCountedView: MaterialTextView = resultView.run {
            this.findViewById(R.id.breathing_result_total_breath_view_id)
        }
        val lastBreathDateView: MaterialTextView = resultView.run {
            this.findViewById(R.id.breathing_result_last_breath_date_view_id)
        }

        breathCountedView.text = MessageFormat.format("{0} Sets", this.iSharedPrefService.getBreath().toString())
        lastBreathDateView.text = this.iSharedPrefService.getBreathingDate()
        val okBt: MaterialButton = resultView.run {
            this.findViewById(R.id.breathing_result_ok_bt_id)
        }

        val seekBar: FluidSlider = resultView.run {
            this.findViewById(R.id.breathing_result_set_repeat_count_seek_bar_id)
        }
        this.seekBarMinValue = 5
        this.seekBarMaxValue = 100
        this.seekBarHalveValue = this.seekBarMinValue!! + 4
        this.seekBarTotalValue = this.seekBarMaxValue!! - this.seekBarMinValue!!

        val resultDialog = MaterialAlertDialogBuilder(context)
        resultDialog.setView(resultView)

        val parentDialog: AlertDialog = resultDialog.create()
        parentDialog.show()

        seekBar.startText = "${this.seekBarMinValue}"
        seekBar.endText = "${this.seekBarMaxValue}"
        seekBar.bubbleText = "${this.seekBarHalveValue}"
        seekBar.positionListener = {
            seekBar.bubbleText = "${this.seekBarMinValue!! + (this.seekBarTotalValue!! * it).toInt()}"
        }
        okBt.setOnClickListener {
            val count: Int = seekBar.bubbleText!!.toInt()
            this.iSharedPrefService.setRepeatCount(count)
            parentDialog.dismiss()
        }
    }

    fun setupUserProfile(context: Context,
        userImageView: CircleImageView?,
        userNameView: MaterialTextView?,
        userEmailView: MaterialTextView?,
        userLocationView: MaterialTextView?
    ) {
        val userImageUri: String = this.iSharedPrefService.getUserImage()
        val userName: String = this.iSharedPrefService.getUserName()
        val userEmail: String = this.iSharedPrefService.getUserEmail()
        val userCurrentStat: String = this.iSharedPrefService.getUserCurrentState()

        if (userImageUri.isNotEmpty()) {
            Glide.with(context).load(userImageUri).into(userImageView!!)
        } else {
            val accountImageResId = getDrawableImage(context, "ic_account_circle_white_24dp")
            Glide.with(context).load(accountImageResId).into(userImageView!!)
        }
        userNameView!!.text = userName
        userEmailView!!.text = userEmail
        userLocationView!!.text = userCurrentStat
    }

    private fun getDrawableImage(context: Context, image: String): Int {
        return context.resources.getIdentifier(image, "drawable", context.packageName)
    }

    fun signOut() {
        FirebaseAuth.getInstance().signOut()
        EventBus.getDefault().postSticky(UserStateEvent(false))
        EventBus.getDefault().postSticky(LandInSettingPageEvent(false, PageType.MAIN_PAGE))
        EventBus.getDefault().postSticky(BottomBarEvent(false))
        clearLocalStorage()
    }
    private fun clearLocalStorage() {
        this.iSharedPrefService.removeItems(Constants.SHARED_PREF_USER_NAME)
        this.iSharedPrefService.removeItems(Constants.SHARED_PREF_USER_EMAIL)
        this.iSharedPrefService.removeItems(Constants.SHARED_PREF_USER_USERNAME)
        this.iSharedPrefService.removeItems(Constants.SHARED_PREF_USER_CURRENT_STATE)
        this.iSharedPrefService.removeItems(Constants.SHARED_PREF_USER_LOGIN_PROVIDER)
        this.iSharedPrefService.removeItems(Constants.SHARED_PREF_USER_IMAGE)
        this.iSharedPrefService.removeItems(Constants.NOTIFICATION_ENABLED)
        this.iSharedPrefService.removeItems(Constants.NAVIGATION_TYPE)
    }

    fun getLastStepCountValue(): Int {
        return this.iSharedPrefService.getLastStepCountValue()
    }

    fun flashStepCounter() {
        this.iSensors.flashStepCounter()
    }

    fun getTotalStepCounted(): Int {
        return this.iSharedPrefService.getTotalStepCount()
    }

    fun saveUserPersonalInfo(
        userAge: String,
        userWeight: String,
        userHeight: String,
        gender: String
    ) {
        val email: String = this.iSharedPrefService.getUserEmail()
        val id: String = UUID.randomUUID().toString()
        val userPersonalInfo = UserPersonalInfo(id, email, userWeight.toDouble(), userHeight.toDouble(), userAge.toInt(), gender)
        launch {
            iUserInfoDao.insertUserInfo(userPersonalInfo)
        }.invokeOnCompletion {
            if (it != null && it.localizedMessage != null) {
                if (BuildConfig.DEBUG) {
                    Log.i(TAG, it.localizedMessage!!)
                }
                this.eventObserver.value = false

            } else {
                this.eventObserver.value = true
                this.userPersonalInfoLiveData.value = userPersonalInfo
            }
        }
    }
    fun getIsAnyEventTriggered(): LiveData<Boolean> {
        return this.eventObserver
    }
    fun getUserInfoLiveData(): LiveData<UserPersonalInfo> {
        return this.userPersonalInfoLiveData
    }

    fun stepCountServiceShouldRunOnFgOrBg(
        service: String,
        d: DialogInterface
    ) {
        launch {
            saveStepCountServiceTypeOnSeparateThread(service)
        }.invokeOnCompletion {
            if (it != null && it.localizedMessage != null) {
                if (BuildConfig.DEBUG) {
                    Log.i(TAG, it.localizedMessage!!)
                }
            } else {
                d.dismiss()
            }
        }
    }
    private suspend fun saveStepCountServiceTypeOnSeparateThread(service: String) {
        withContext(Dispatchers.IO) {
            iSharedPrefService.saveStepCountServiceType(service)
        }
    }

    fun checkIfAnyServiceRunning(): Boolean {
        return this.iSharedPrefService.getStepCountServiceType() == Constants.SERVICE_FOREGROUND || this.iSharedPrefService.getStepCountServiceType() == Constants.SERVICE_BACKGROUND
    }

    fun checkIfHeartRateUnlocked(): Boolean {
        return this.iSharedPrefService.getBreathingSession() >= 50
    }

    override val coroutineContext: CoroutineContext
        get() = this.job + Dispatchers.Main
}