package com.project.neardoc.utils.validators

import com.google.android.material.textfield.TextInputLayout
import com.project.neardoc.R

class EmptyFieldValidator(mContainer: TextInputLayout): BaseInputValidator(mContainer) {
    init {
        mEmptyMessage = mContainer.resources.getString(R.string.name_required)
    }

    override fun isValid(charSequence: CharSequence): Boolean {
        val value = charSequence.toString()
        return value.isNotEmpty()
    }
}