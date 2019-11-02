package com.project.neardoc.utils

import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.project.neardoc.R
import javax.inject.Inject

class NearDockMessageViewer @Inject constructor(private val context: Context): INearDockMessageViewer{

    private var mSnackBar: Snackbar?= null
    private var iSnackBarListeners: ISnackBarListeners?= null
    private var connectionSettings: ConnectionSettings?= null
    override fun registerSnackBarListener(iSnackBarListeners: ISnackBarListeners) {
       this.iSnackBarListeners = iSnackBarListeners
    }

    override fun unRegisterSnackBarListener(iSnackBarListeners: ISnackBarListeners) {
       if (this.iSnackBarListeners != null) {
           this.iSnackBarListeners = null
       }
    }

    override fun <T> snackbarOnTop(item: T, type: SnackbarType, show: Boolean, message: String, isOnTop: Boolean) {
        when(type) {
            SnackbarType.CONNECTION_SETTING -> {
                if (item is ConnectionSettings) {
                    this.connectionSettings = item
                    val view: View = item.getSnackBar().view
                    if (isOnTop) {
                        val params: FrameLayout.LayoutParams = view.layoutParams as FrameLayout.LayoutParams
                        params.gravity = Gravity.TOP
                        view.layoutParams = params
                    }
                }
            }
            SnackbarType.RESEND_EMAIL -> {
                if (item is Snackbar) {
                    this.mSnackBar = item
                    val view: View = item.view
                    if (isOnTop) {
                        val params: FrameLayout.LayoutParams =
                            view.layoutParams as FrameLayout.LayoutParams
                        params.gravity = Gravity.TOP
                        view.layoutParams = params
                    }
                    view.setBackgroundColor(
                        ContextCompat.getColor(
                            this.context,
                            R.color.blue_gray_800
                        )
                    )
                    this.mSnackBar?.show()
                    this.mSnackBar?.setAction(message) {
                        run {
                            this.iSnackBarListeners?.onResendEmailVerification()
                            this.mSnackBar?.dismiss()
                        }
                    }
                }
            }
            SnackbarType.OPEN_INBOX -> {
                if (item is Snackbar) {
                    this.mSnackBar = item
                    if (show) {
                        val view: View = item.view
                        if (isOnTop) {
                            val params: FrameLayout.LayoutParams = view.layoutParams as FrameLayout.LayoutParams
                            params.gravity = Gravity.TOP
                            view.layoutParams = params
                        }
                        view.setBackgroundColor(ContextCompat.getColor(this.context, R.color.blue_gray_800))
                        this.mSnackBar?.show()
                        this.mSnackBar?.setAction(message) {
                            run {
                                this.iSnackBarListeners?.onOpenEmailInbox(item)
                            }
                        }
                    } else {
                        this.mSnackBar?.dismiss()
                    }
                }
            }
        }
    }
    override fun <T> dismiss(item: T, type: SnackbarType) {
       when(type) {
           SnackbarType.CONNECTION_SETTING -> {
               if (item is ConnectionSettings) {
                   this.connectionSettings = item
                   if (this.connectionSettings != null && this.connectionSettings?.getSnackBar() != null) {
                       this.connectionSettings?.getSnackBar()!!.dismiss()
                   }
               }
           }
           SnackbarType.RESEND_EMAIL -> {
               if (item is Snackbar) {
                   this.mSnackBar = item
                   if (this.mSnackBar != null) {
                       this.mSnackBar?.dismiss()
                   }
               }
           }
           SnackbarType.OPEN_INBOX -> {
               if (item is Snackbar) {
                   this.mSnackBar = item
                   if (this.mSnackBar != null) {
                       this.mSnackBar?.dismiss()
                   }
               }
           }
       }
    }
}