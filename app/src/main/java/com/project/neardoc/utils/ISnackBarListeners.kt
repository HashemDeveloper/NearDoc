package com.project.neardoc.utils

import com.google.android.material.snackbar.Snackbar

interface ISnackBarListeners {
    fun onResendEmailVerification()
    fun onOpenEmailInbox(snackbar: Snackbar)
}