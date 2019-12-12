package com.project.neardoc.utils.calories

import javax.inject.Inject

class CalorieBurnedCalculator @Inject constructor():
    ICalorieBurnedCalculator {
    companion object {
        @JvmStatic val WALING_FACTOR: Double = 0.57
    }
    private var caloriesBurned: Double?= null
    private var distance: Double?= null

    override fun calculateCalorieBurned(height: Double, weight: Double, stepCount: Int): Double {
        val strip: Double = getCalculatedStrip(height)
        this.caloriesBurned = stepCount * getCalculatedConversionFactor(strip, weight)
        return this.caloriesBurned!!
    }

    override fun getDistance(height: Double, stepCount: Int): Double {
        val s: Double = getCalculatedStrip(height)
        this.distance = (stepCount * s) / 100000
        return this.distance!!
    }

    private fun getCalculatedStrip(height: Double): Double {
        return height * 0.415
    }
    private fun getCalculatedConversionFactor(weight: Double, strip: Double): Double {
        val calorieBurnedPerMile: Double = WALING_FACTOR * (weight * 2.2)
        val stepCountMile: Double = 160934.4 / strip
        return calorieBurnedPerMile / stepCountMile
    }
}