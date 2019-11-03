package com.project.neardoc.utils

interface INearDockMessageViewer {
    fun registerSnackBarListener(iSnackBarListeners: ISnackBarListeners)
    fun unRegisterSnackBarListener(iSnackBarListeners: ISnackBarListeners)
    fun <T> displayMessage(item: T, type: SnackbarType, show: Boolean, message: String, isOnTop: Boolean)
    fun <T> dismiss(item: T, type: SnackbarType)
    fun <T> batchDismiss(item: T, typeList: MutableList<SnackbarType>)
    fun clearBatchTypeList()
}