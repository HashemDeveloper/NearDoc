package com.project.neardoc.events

class BottomBarEvent constructor(private val isBottomEnabled: Boolean){
    fun getIsBottomBarEnabled(): Boolean {
        return this.isBottomEnabled
    }
}