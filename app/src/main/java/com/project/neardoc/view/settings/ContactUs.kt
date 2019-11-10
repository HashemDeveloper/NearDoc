package com.project.neardoc.view.settings


import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.neardoc.R
import com.project.neardoc.di.Injectable
import com.project.neardoc.events.LandInSettingPageEvent
import com.project.neardoc.model.SignInSecurityHeaderModel
import com.project.neardoc.utils.PageType
import com.project.neardoc.view.adapters.ContactUsAdapter
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_contact_us.*
import org.greenrobot.eventbus.EventBus

class ContactUs : Fragment(), Injectable, ContactUsAdapter.ContactClickListener {

    private var contactUsAdapter: ContactUsAdapter?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)
        EventBus.getDefault().postSticky(LandInSettingPageEvent(true, PageType.CONTACT_US))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_contact_us, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.contactUsAdapter = ContactUsAdapter(context!!, this)
        fragment_contact_us_recycler_view_id.layoutManager = LinearLayoutManager(context!!)
        fragment_contact_us_recycler_view_id.adapter = this.contactUsAdapter
        val header = SignInSecurityHeaderModel("Email Support")
        val contactEmail = "neardocapp@gmail.com"
        val dataList: List<Any> = arrayListOf(header, contactEmail)
        this.contactUsAdapter?.setData(dataList)
    }

    override fun onEmailClicked(email: String) {
        sendEmail(email)
    }

    private fun sendEmail(email: String) {
        val i = Intent(Intent.ACTION_SEND)
        i.type = "message/rfc822"
        i.putExtra(
            Intent.EXTRA_EMAIL,
            arrayOf(email)
        )
        try {
            startActivity(Intent.createChooser(i, "Send mail..."))
        } catch (ex: ActivityNotFoundException) {
            Toast.makeText(
                context!!,
                "There are no email clients installed.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().postSticky(LandInSettingPageEvent(false, PageType.SETTINGS_FRAGMENT))
    }
}
