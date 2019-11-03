package com.project.neardoc.utils

import com.google.android.material.snackbar.Snackbar

interface ILoginSnackBarListeners {
    fun onResendEmailVerification()
    fun onOpenEmailInbox(snackbar: Snackbar)
}