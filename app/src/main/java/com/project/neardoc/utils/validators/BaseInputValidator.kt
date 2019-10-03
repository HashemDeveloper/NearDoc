package com.project.neardoc.utils.validators

import com.google.android.material.textfield.TextInputLayout

open class BaseInputValidator constructor(var mContainer: TextInputLayout) {
    protected var mEmptyMessage: String? = ""
    protected var mErrorMessage: String? = ""

    protected open fun isValid(charSequence: CharSequence): Boolean {
        return true
    }
    fun getIsValidated(charSequence: CharSequence): Boolean {
        return if (this.mEmptyMessage != null && (charSequence.isEmpty())) {
            this.mContainer.error = this.mEmptyMessage
            false
        } else if (isValid(charSequence)) {
            this.mContainer.error = ""
            true
        } else {
            this.mContainer.error = this.mErrorMessage
            false
        }
    }
}