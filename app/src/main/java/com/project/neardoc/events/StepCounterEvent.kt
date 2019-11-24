package com.project.neardoc.events

class StepCounterEvent(private val stepCount: Int) {
    fun getStepCount(): Int {
        return this.stepCount
    }
}