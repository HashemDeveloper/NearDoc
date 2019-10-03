package com.project.neardoc.utils.validators

import com.google.android.material.textfield.TextInputLayout
import com.project.neardoc.R
import com.project.neardoc.utils.Constants

class PasswordValidator(mContainer: TextInputLayout): BaseInputValidator(mContainer) {
    init {
        mEmptyMessage = mContainer.resources.getString(R.string.passowrd_required)
        mErrorMessage = mContainer.resources.getString(R.string.invalid_password)
    }

    override fun isValid(charSequence: CharSequence): Boolean {
        return Constants.passwordValidator(charSequence.toString())
    }
}