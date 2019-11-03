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

    private var mTypeList: MutableList<SnackbarType>?= null
    private var mSnackBar: Snackbar?= null
    private var iLoginSnackBarListeners: ILoginSnackBarListeners?= null
    private var iUpdatePassSnackBarListener: IUpdatePassSnackBarListener?= null
    private var connectionSettings: ConnectionSettings?= null


    override fun registerLoginSnackBarListener(iLoginSnackBarListeners: ILoginSnackBarListeners) {
       this.iLoginSnackBarListeners = iLoginSnackBarListeners
    }

    override fun unRegisterLoginSnackBarListener(iLoginSnackBarListeners: ILoginSnackBarListeners) {
       if (this.iLoginSnackBarListeners != null) {
           this.iLoginSnackBarListeners = null
       }
    }

    override fun registerUpdatePassSnackBarListener(iUpdatePassSnackBarListener: IUpdatePassSnackBarListener) {
        this.iUpdatePassSnackBarListener = iUpdatePassSnackBarListener
    }

    override fun unRegisterUpdatePassSnackBarListener(iUpdatePassSnackBarListener: IUpdatePassSnackBarListener) {
        if (this.iUpdatePassSnackBarListener != null) {
            this.iUpdatePassSnackBarListener = null
        }
    }

    override fun <T> displayMessage(item: T, type: SnackbarType, show: Boolean, message: String, isOnTop: Boolean) {
        when(type) {
            SnackbarType.CONNECTION_SETTING -> {
                if (item is ConnectionSettings) {
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
                    if (show) {
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
                        item.show()
                        val actionMessage: String = this.context.resources.getString(R.string.email_resend)
                        item.setAction(actionMessage) {
                            run {
                                this.iLoginSnackBarListeners?.onResendEmailVerification()
                                item.dismiss()
                            }
                        }
                    } else {
                        item.dismiss()
                    }
                }
            }
            SnackbarType.OPEN_INBOX -> {
                if (item is Snackbar) {
                    if (show) {
                        val view: View = item.view
                        if (isOnTop) {
                            val params: FrameLayout.LayoutParams = view.layoutParams as FrameLayout.LayoutParams
                            params.gravity = Gravity.TOP
                            view.layoutParams = params
                        }
                        view.setBackgroundColor(ContextCompat.getColor(this.context, R.color.blue_gray_800))
                        item.show()
                        val actionMessage: String = this.context.resources.getString(R.string.email_inbox)
                        item.setAction(actionMessage) {
                            run {
                                this.iLoginSnackBarListeners?.onOpenEmailInbox(item)
                            }
                        }
                    } else {
                        item.dismiss()
                    }
                }
            }
            SnackbarType.INVALID_PASSWORD -> {
                if (item is Snackbar) {
                    val view: View = item.view
                    item.view.setBackgroundColor(ContextCompat.getColor(this.context, R.color.blue_gray_800))
                    if (isOnTop) {
                        val params: FrameLayout.LayoutParams = view.layoutParams as FrameLayout.LayoutParams
                        params.gravity = Gravity.TOP
                        view.layoutParams = params
                    }
                    item.show()
                }
            }
            SnackbarType.EMAIL_NOT_FOUND -> {
                if (item is Snackbar) {
                    val view: View = item.view
                    item.view.setBackgroundColor(ContextCompat.getColor(this.context, R.color.blue_gray_800))
                    if (isOnTop) {
                        val params: FrameLayout.LayoutParams = view.layoutParams as FrameLayout.LayoutParams
                        params.gravity = Gravity.TOP
                        view.layoutParams = params
                    }
                    item.show()
                }
            }
            SnackbarType.SIGN_IN_AGAIN -> {
                if (item is Snackbar) {
                    if (show) {
                        val view: View = item.view
                        if (isOnTop) {
                            val params: FrameLayout.LayoutParams = view.layoutParams as FrameLayout.LayoutParams
                            params.gravity = Gravity.TOP
                            view.layoutParams = params
                        }
                        view.setBackgroundColor(ContextCompat.getColor(this.context, R.color.blue_gray_800))
                        item.show()
                        val actionTitle = this.context.resources.getString(R.string.log_in)
                        item.setAction(actionTitle) {
                            run {
                                this.iUpdatePassSnackBarListener?.signIn()
                            }
                        }
                    }
                }
            }
        }
    }
    override fun <T> dismiss(item: T, type: SnackbarType) {
       when(type) {
           SnackbarType.CONNECTION_SETTING -> {
               dismiss(item)
           }
           SnackbarType.RESEND_EMAIL -> {
               dismiss(item)
           }
           SnackbarType.OPEN_INBOX -> {
               dismiss(item)
           }
           SnackbarType.INVALID_PASSWORD -> {
               dismiss(item)
           }
           SnackbarType.EMAIL_NOT_FOUND -> {
               dismiss(item)
           }
           SnackbarType.SIGN_IN_AGAIN -> {
               dismiss(item)
           }
       }
    }
    private fun <T> dismiss(item: T) {
        if (item is Snackbar) {
            this.mSnackBar = item
            if (this.mSnackBar != null) {
                this.mSnackBar?.dismiss()
            }
        } else if (item is ConnectionSettings) {
            this.connectionSettings = item
            if (this.connectionSettings != null && this.connectionSettings?.getSnackBar() != null) {
                this.connectionSettings?.getSnackBar()!!.dismiss()
            }
        }
    }

    override fun <T> batchDismiss(item: T, typeList: MutableList<SnackbarType>) {
      this.mTypeList = typeList
        if (this.mTypeList != null && this.mTypeList?.isNotEmpty()!!) {
            loop@ for (type in this.mTypeList!!) {
                when(type) {
                    SnackbarType.CONNECTION_SETTING -> {
                        if (item is ConnectionSettings) {
                            this.connectionSettings = item
                            if (this.connectionSettings != null && this.connectionSettings?.getSnackBar() != null) {
                                this.connectionSettings?.getSnackBar()!!.dismiss()
                            }
                        }
                        break@loop
                    }
                    SnackbarType.RESEND_EMAIL -> {
                        if (item is Snackbar) {
                            this.mSnackBar = item
                            if (this.mSnackBar != null) {
                                this.mSnackBar?.dismiss()
                            }
                        }
                        break@loop
                    }
                    SnackbarType.OPEN_INBOX -> {
                        if (item is Snackbar) {
                            this.mSnackBar = item
                            if (this.mSnackBar != null) {
                                this.mSnackBar?.dismiss()
                            }
                        }
                        break@loop
                    }
                    SnackbarType.EMAIL_NOT_FOUND -> {
                        break@loop
                    }
                    SnackbarType.INVALID_PASSWORD -> {
                        break@loop
                    }
                    SnackbarType.SIGN_IN_AGAIN -> {
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
    }

    override fun clearBatchTypeList() {
       if (this.mTypeList != null) {
           this.mTypeList?.clear()
       }
    }
}