package com.project.neardoc.utils

interface INearDockSnackBar {
    fun registerSnackBarListener(iSnackBarListeners: ISnackBarListeners)
    fun unRegisterSnackBarListener(iSnackBarListeners: ISnackBarListeners)
    fun dismis()
}