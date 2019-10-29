package com.project.neardoc.utils.validators

import com.google.android.material.textfield.TextInputLayout
import com.project.neardoc.R

class EmptyFieldValidator(mContainer: TextInputLayout, message: String): BaseInputValidator(mContainer) {
    init {
        mEmptyMessage = message
    }

    override fun isValid(charSequence: CharSequence): Boolean {
        val value = charSequence.toString()
        return value.isNotEmpty()
    }
}