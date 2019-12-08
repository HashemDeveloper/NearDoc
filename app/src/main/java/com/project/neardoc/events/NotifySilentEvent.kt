package com.project.neardoc.events

class NotifySilentEvent constructor(private var hasNotification: Boolean, private var caloriesBurnedResult: Int) {
    fun getHasNotification(): Boolean {
        return this.hasNotification
    }
    fun getCaloriesBurnedResult(): Int {
        return this.caloriesBurnedResult
    }
}