package com.project.neardoc.utils.validators

import com.google.android.material.textfield.TextInputLayout
import com.project.neardoc.R

class NameValidator(mContainer: TextInputLayout): BaseInputValidator(mContainer) {
    init {
        mErrorMessage = mContainer.resources.getString(R.string.name_required)
    }

    override fun isValid(charSequence: CharSequence): Boolean {
        val value = charSequence.toString()
        return value.isNotEmpty()
    }
}