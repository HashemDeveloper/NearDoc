package com.project.neardoc.utils

interface INearDockMessageViewer {
    fun registerSnackBarListener(iSnackBarListeners: ISnackBarListeners)
    fun unRegisterSnackBarListener(iSnackBarListeners: ISnackBarListeners)
    fun <T> snackbarOnTop(item: T, type: SnackbarType, show: Boolean, message: String, isOnTop: Boolean)
    fun <T> dismiss(item: T, type: SnackbarType)
}