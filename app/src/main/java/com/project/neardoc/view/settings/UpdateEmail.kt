package com.project.neardoc.view.settings


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels

import com.project.neardoc.R
import com.project.neardoc.di.Injectable
import com.project.neardoc.di.viewmodel.ViewModelFactory
import com.project.neardoc.events.LandInSettingPageEvent
import com.project.neardoc.utils.PageType
import com.project.neardoc.utils.validators.EmailValidator
import com.project.neardoc.viewmodel.UpdateEmailViewModel
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_update_email.*
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

class UpdateEmail : Fragment(), Injectable {
    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val updateEmailViewModel: UpdateEmailViewModel by viewModels {
        this.viewModelFactory
    }

    private var emailValidator: EmailValidator?= null
    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)
        EventBus.getDefault().postSticky(LandInSettingPageEvent(true, PageType.CHANGE_EMAIL))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_update_email, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.emailValidator = EmailValidator(fragment_update_email_input_layout_id)
        processClickListeners()
    }
    private fun processClickListeners() {
        fragment_update_email_update_bt_id.setOnClickListener {
            val email: String = fragment_update_email_edit_text_id.text.toString()
            val isValidEmail: Boolean = this.emailValidator?.getIsValidated(email)!!
            if (isValidEmail) {
                this.updateEmailViewModel.processUpdateEmailRequest(activity!!, email)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().postSticky(LandInSettingPageEvent(false, PageType.SIGN_IN_SECURITY))
    }
}
