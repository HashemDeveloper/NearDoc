package com.project.neardoc.utils.validators

import com.google.android.material.textfield.TextInputLayout
import com.project.neardoc.R
import com.project.neardoc.utils.Constants

class EmailValidator(mContainer: TextInputLayout): BaseInputValidator(mContainer) {
    init {
        mEmptyMessage = mContainer.resources.getString(R.string.email_is_required)
        mEmptyMessage = mContainer.resources.getString(R.string.invalid_email)
    }

    override fun isValid(charSequence: CharSequence): Boolean {
        return Constants.emailValidator(charSequence.toString())
    }
}