package com.project.neardoc.utils.validators

import com.google.android.material.textfield.TextInputLayout
import com.project.neardoc.R
import com.project.neardoc.utils.Constants

class UsernameValidator(mErrorContainer: TextInputLayout) : BaseInputValidator(mErrorContainer) {
    init {
        mEmptyMessage = mErrorContainer.resources.getString(R.string.username_is_required)
        mErrorMessage = mErrorContainer.resources.getString(R.string.invalid_username)
    }

    override fun isValid(charSequence: CharSequence): Boolean {
        val username = charSequence.toString()
        return Constants.usernameValidator(username)
    }
}