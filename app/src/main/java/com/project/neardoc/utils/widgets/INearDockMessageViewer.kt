package com.project.neardoc.utils.widgets

interface INearDockMessageViewer {
    fun registerLoginSnackBarListener(iLoginSnackBarListeners: ILoginSnackBarListeners)
    fun unRegisterLoginSnackBarListener(iLoginSnackBarListeners: ILoginSnackBarListeners)
    fun registerUpdatePassSnackBarListener(iUpdatePassSnackBarListener: IUpdatePassSnackBarListener)
    fun unRegisterUpdatePassSnackBarListener(iUpdatePassSnackBarListener: IUpdatePassSnackBarListener)
    fun <T> displayMessage(item: T, type: SnackbarType, show: Boolean, message: String, isOnTop: Boolean)
    fun <T> dismiss(item: T, type: SnackbarType)
    fun <T> batchDismiss(item: T, typeList: MutableList<SnackbarType>)
    fun clearBatchTypeList()
}