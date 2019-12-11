package com.project.neardoc.view.fragments


import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textview.MaterialTextView
import com.linroid.filtermenu.library.FilterMenu
import com.linroid.filtermenu.library.FilterMenuLayout
import com.project.neardoc.BuildConfig
import com.project.neardoc.R
import com.project.neardoc.di.Injectable
import com.project.neardoc.di.viewmodel.ViewModelFactory
import com.project.neardoc.events.BottomBarEvent
import com.project.neardoc.events.NotifySilentEvent
import com.project.neardoc.events.StepCounterEvent
import com.project.neardoc.utils.Constants
import com.project.neardoc.utils.GenderType
import com.project.neardoc.utils.keyboardstatechecker.KeyboardEventListener
import com.project.neardoc.utils.validators.EmptyFieldValidator
import com.project.neardoc.viewmodel.AccountPageViewModel
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_account_page.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


class AccountPage : Fragment(), Injectable, FilterMenu.OnMenuChangeListener {
    companion object {
        const val ACTIVITY_RECOGNITION_REQ_CODE: Int = 2
        @JvmStatic
        private val TAG: String = AccountPage::class.java.canonicalName!!
    }
    private var emptyWeightValidator: EmptyFieldValidator?= null
    private var emptyHeightValidator: EmptyFieldValidator?= null
    private var emptyAgeValidator: EmptyFieldValidator?= null
    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val accountPageViewModel: AccountPageViewModel by viewModels {
        this.viewModelFactory
    }
    private var isNotificationWhileInApp: Boolean = false
    private var dialogUserInfoMainContainerView: ScrollView?= null
    private var rootDialog: AlertDialog?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
        EventBus.getDefault().postSticky(BottomBarEvent(false))
        if (arguments != null) {
            val bundle: Bundle = arguments!!
            if (bundle.containsKey(Constants.STEP_COUNT_NOTIFICATION)) {
                this.isNotificationWhileInApp = true
                val caloriresBurned: Int = bundle.getInt(Constants.CALORIES_BURNED_RESULT)
                this.accountPageViewModel.flashStepCounter()
                val totalStepCount: Int = this.accountPageViewModel.getTotalStepCounted()
                displayCaloriesBurnedDialog(caloriresBurned, totalStepCount, GenderType.MALE)
            }
        }
        this.accountPageViewModel.getIsAnyEventTriggered().observe(activity!!, observeEvents())
    }
    private fun observeEvents(): Observer<Boolean> {
        return Observer { event ->
            if (event) {

            } else {
                if (BuildConfig.DEBUG) {
                    Log.i(TAG, "Failed to save user data")
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_account_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.accountPageViewModel.setupUserProfile(context!!, fragment_account_user_image_view_id, fragment_account_user_name_id,
            fragment_account_user_email_view_id, fragment_account_user_location_view_id)
        this.accountPageViewModel.setupDeviceSensor(activity!!, fragment_account_room_temp_view_id, fragment_step_count_parent_layout)
        val lastStepCountValue: Int = this.accountPageViewModel.getLastStepCountValue()
        if (!fragment_account_page_start_step_count_bt_id.isVisible) {
            fragment_account_step_counter_view_id.text = lastStepCountValue.toString()
        }
        this.accountPageViewModel.animateBreathingTitleView(fragment_account_breathing_ex_title_view_id)
        fragment_account_start_breathing_bt_id.setOnClickListener {
            this.accountPageViewModel.startAnimation(context!!, activity!!, fragment_account_breathing_image_view_id,
                fragment_account_breath_guide_text_view_id)
        }
        fragment_account_go_back_bt_id.setOnClickListener {
            EventBus.getDefault().postSticky(BottomBarEvent(true))
            Navigation.findNavController(it).navigate(AccountPageDirections.actionHomePage())
        }
        fragment_account_page_start_step_count_bt_id.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                if (!checkActivityRecognitionPermission()) {
                    requestActivityRecogPermission()
                } else {
                    this.accountPageViewModel.startStepCountService(activity!!)
                }
            } else {
                this.accountPageViewModel.startStepCountService(activity!!)
            }
        }
        attachMenu(fragment_account_menu_bt_id)
    }

    private fun displayCaloriesBurnedDialog(caloriesBurned: Int, totalStepTaken: Int, gender: GenderType) {
        var calorieBurnGoalPerDay: Int? = null
        calorieBurnGoalPerDay = when (gender) {
            GenderType.MALE -> {
                2000
            }
            GenderType.FEMALE -> {
                1600
            }
        }
        val caloriesParentView: View = getParentViewForDialog(R.layout.calories_burned_layout, DialogViewType.VIEW_DISPLAY_CALORIES)
        val stepsTakenView: MaterialTextView = caloriesParentView.run {
            this.findViewById(R.id.calories_burned_steps_taken_view_id)
        }
        val caloriesDateView: MaterialTextView = caloriesParentView.run {
            this.findViewById(R.id.calories_burned_date_view_id)
        }
        val caloriesStatProgressView: ProgressBar = caloriesParentView.run {
            this.findViewById(R.id.calories_fg_progress_bar_id)
        }
        val caloriesBurnedTextView: MaterialTextView = caloriesParentView.run {
            this.findViewById(R.id.calories_burned_result_view_id)
        }
        val goalView: MaterialTextView = caloriesParentView.run {
            this.findViewById(R.id.calories_burned_goal_view_id)
        }
        val okBt: MaterialButton = caloriesParentView.run {
            this.findViewById(R.id.calories_burned_ok_bt_id)
        }
        val displayCaloriesDialog = MaterialAlertDialogBuilder(this.context)
        displayCaloriesDialog.setView(caloriesParentView)
        this.rootDialog = displayCaloriesDialog.create()
        rootDialog?.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        rootDialog?.show()

        val dateFormat: DateFormat = SimpleDateFormat("EEEE MMM d yyyy", Locale.getDefault())
        val dateString: String = dateFormat.format(Date())
        caloriesDateView.text = dateString

        val stepsTakenMessage = "$totalStepTaken Steps Taken"
        val caloriesBurnedMessage = "$caloriesBurned Calories Burned"
        val goalMessage = "Your Goal $calorieBurnGoalPerDay"
        caloriesStatProgressView.progress =  totalStepTaken

        goalView.text = goalMessage
        caloriesBurnedTextView.text = caloriesBurnedMessage
        stepsTakenView.text = stepsTakenMessage

        okBt.setOnClickListener {
            rootDialog?.dismiss()
        }
    }
    private fun getParentViewForDialog(resource: Int, viewType: DialogViewType): View {
        val view: View?
        val viewGroup: ViewGroup = activity!!.window.decorView.rootView as ViewGroup
        view = when (viewType) {
            DialogViewType.VIEW_DISPLAY_CALORIES -> {
                layoutInflater.inflate(resource, viewGroup, false)
            }
            DialogViewType.VIEW_DISPLAY_USER_FORM -> {
                layoutInflater.inflate(resource, viewGroup, false)
            }
        }
        return view
    }

    @SuppressLint("InlinedApi")
    private fun requestActivityRecogPermission() {
        if (ActivityCompat.checkSelfPermission(activity!!, Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.ACTIVITY_RECOGNITION), ACTIVITY_RECOGNITION_REQ_CODE)
        }
    }
    @SuppressLint("InlinedApi")
    private fun checkActivityRecognitionPermission(): Boolean {
        val isActivityRecogGranted: Int = ActivityCompat.checkSelfPermission(activity!!, Manifest.permission.ACTIVITY_RECOGNITION)
        return isActivityRecogGranted == PackageManager.PERMISSION_GRANTED
    }

    private fun attachMenu(layout: FilterMenuLayout?): FilterMenu? {
        val signOutBt = AppCompatImageView(this.context)
        signOutBt.setBackgroundResource(R.drawable.ic_exit_to_app_white_24dp)
        signOutBt.id = R.id.account_signout_bt
        val heartRate = AppCompatImageView(this.context)
        heartRate.setBackgroundResource(R.drawable.heart_cardiogram_24_white)
        heartRate.id = R.id.account_heart_rate_bt
        val editProfileBt = AppCompatImageView(this.context)
        editProfileBt.setBackgroundResource(R.drawable.ic_edit_white_24dp)
        editProfileBt.id = R.id.account_edit_personal_info_bt
        return FilterMenu.Builder(this.context)
            .addItem(signOutBt)
            .addItem(heartRate)
            .addItem(editProfileBt)
            .attach(layout)
            .withListener(this)
            .build()
    }

    override fun onMenuCollapse() {

    }

    override fun onMenuItemClick(view: View?, position: Int) {
        if (view!!.id == R.id.account_signout_bt) {
            signOut()
        } else if (view.id == R.id.account_heart_rate_bt) {
            Toast.makeText(this.context, "Heart rate", Toast.LENGTH_SHORT).show()
        } else if (view.id == R.id.account_edit_personal_info_bt) {
            displayUserInfoForm()
        }
    }
    private fun signOut() {
        this.accountPageViewModel.signOut()
        val navigateToWelcome = findNavController()
        navigateToWelcome.navigate(R.id.welcome)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
       if (requestCode == ACTIVITY_RECOGNITION_REQ_CODE) {
           if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
              if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                  fragment_account_page_start_step_count_bt_id.visibility = View.GONE
                  fragment_account_step_counter_view_id.text = "0"
                  displayUserInfoForm()
              } else {
                  fragment_account_page_start_step_count_bt_id.visibility = View.VISIBLE
              }
           }
       }
    }
    private fun displayUserInfoForm() {
        val parentView: View = getParentViewForDialog(R.layout.dialog_user_info_form_layout, DialogViewType.VIEW_DISPLAY_USER_FORM)
        this.dialogUserInfoMainContainerView = parentView.findViewById(R.id.dialog_user_info_main_container_id)
        val saveBt: MaterialButton = parentView.run {
            this.findViewById(R.id.dialog_user_info_save_bt_id)
        }
        val closeDialogBt: FloatingActionButton = parentView.run {
            this.findViewById(R.id.dialog_user_info_close_form_bt_id)
        }
        val weightInputLayout: TextInputLayout = parentView.run {
            this.findViewById(R.id.dialog_user_info_weight_input_layout_id)
        }
        val weightEditText: TextInputEditText = parentView.run {
            this.findViewById(R.id.dialog_user_info_weight_edit_text_id)
        }
        val heightInputLayout: TextInputLayout = parentView.run {
            this.findViewById(R.id.dialog_user_info_height_input_layout_id)
        }
        val heightEditText: TextInputEditText = parentView.run {
            this.findViewById(R.id.dialog_user_info_height_edit_text_id)
        }
        val ageInputLayout: TextInputLayout = parentView.run {
            this.findViewById(R.id.dialog_user_info_age_input_layout_id)
        }
        val ageEditText: TextInputEditText = parentView.run {
            this.findViewById(R.id.dialog_user_info_age_edit_text_id)
        }
        val ageRadioGroup: RadioGroup = parentView.run {
            this.findViewById(R.id.dialog_user_info_radio_group_id)
        }
        var gender = "Male"
        ageRadioGroup.setOnCheckedChangeListener { radioGroup, checkedId ->
            when (checkedId) {
                R.id.dialog_user_info_male_radio_bt_id -> {
                    gender = "Male"
                }
                R.id.dialog_user_info_female_radio_bt_id -> {
                    gender = "Female"
                }
            }
        }
        this.emptyWeightValidator = EmptyFieldValidator(weightInputLayout, "")
        this.emptyHeightValidator = EmptyFieldValidator(heightInputLayout, "")
        this.emptyAgeValidator = EmptyFieldValidator(ageInputLayout, "")

        val displayCaloriesDialog = MaterialAlertDialogBuilder(this.context)
        displayCaloriesDialog.setView(parentView)
        this.rootDialog = displayCaloriesDialog.create()
        rootDialog?.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        this.rootDialog!!.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        rootDialog!!.show()
        rootDialog!!.setCanceledOnTouchOutside(false)

        saveBt.setOnClickListener {
            val userWeight: String = weightEditText.text.toString()
            val userHeight: String = heightEditText.text.toString()
            val userAge: String = ageEditText.text.toString()
            val isValidWeight: Boolean = this.emptyWeightValidator!!.getIsValidated(userWeight)
            val isValidHeight: Boolean = this.emptyHeightValidator!!.getIsValidated(userHeight)
            val isValidAge: Boolean = this.emptyAgeValidator!!.getIsValidated(userAge)
            if (isValidWeight && isValidHeight && isValidAge && gender.isNotEmpty()) {
                displayValidationError(false, weightInputLayout, heightInputLayout, ageInputLayout)
                this.accountPageViewModel.saveUserPersonalInfo(userAge, userWeight, userHeight, gender)
                this.rootDialog!!.dismiss()

            } else {
                displayValidationError(true, weightInputLayout, heightInputLayout, ageInputLayout)
            }
        }
        closeDialogBt.setOnClickListener {
            this.rootDialog!!.dismiss()
        }
    }
    private fun displayValidationError(isError: Boolean, weightInputLayout: TextInputLayout, heightInputLayout: TextInputLayout, ageInputLayout: TextInputLayout) {
        if (isError) {
            weightInputLayout.isErrorEnabled = true
            heightInputLayout.isErrorEnabled = true
            ageInputLayout.isErrorEnabled = true
            weightInputLayout.error = "Please enter weight"
            heightInputLayout.error = "Please enter height"
            ageInputLayout.error = "Please enter age"
        } else {
            weightInputLayout.isErrorEnabled = false
            heightInputLayout.isErrorEnabled = false
            ageInputLayout.isErrorEnabled = false
            weightInputLayout.error = ""
            heightInputLayout.error = ""
            ageInputLayout.error = ""
        }
    }

    override fun onMenuExpand() {

    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onStepCountEvent(event: StepCounterEvent) {
        if (event.getStepCount() != 0) {
            fragment_account_step_counter_view_id?.let {
                it.text = event.getStepCount().toString()
            }
        }
    }
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onSilentNotificationEvent(event: NotifySilentEvent) {
        if (event.getHasNotification()) {
            val totalStepCount: Int = this.accountPageViewModel.getTotalStepCounted()
            val caloriesBurnedResult: Int = event.getCaloriesBurnedResult()
            displayCaloriesBurnedDialog(caloriesBurnedResult, totalStepCount, GenderType.MALE)
        }
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        EventBus.getDefault().postSticky(BottomBarEvent(true))
        this.accountPageViewModel.getIsAnyEventTriggered().removeObserver(observeEvents())
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        EventBus.getDefault().postSticky(BottomBarEvent(false))
        KeyboardEventListener(this.activity!!) {
            if (it) {
                if (this.dialogUserInfoMainContainerView != null) {
                    this.dialogUserInfoMainContainerView!!.scrollTo(0, this.dialogUserInfoMainContainerView!!.bottom)
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onPause() {
        super.onPause()
        this.accountPageViewModel.clearObservers(fragment_account_room_temp_view_id, fragment_account_step_counter_view_id)
    }

    override fun onStop() {
        super.onStop()
        this.accountPageViewModel.clearObservers(fragment_account_room_temp_view_id, fragment_account_step_counter_view_id)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        this.accountPageViewModel.clearObservers(fragment_account_room_temp_view_id, fragment_account_step_counter_view_id)
        if (this.rootDialog != null) {
            this.rootDialog!!.dismiss()
        }
    }
    enum class DialogViewType {
        VIEW_DISPLAY_CALORIES,
        VIEW_DISPLAY_USER_FORM
    }
}
