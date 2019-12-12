package com.project.neardoc.utils.calories

interface ICalorieBurnedCalculator {
    fun calculateCalorieBurned(height: Double, weight: Double, stepCount: Int): Double
    fun getDistance(height: Double, stepCount: Int): Double
}