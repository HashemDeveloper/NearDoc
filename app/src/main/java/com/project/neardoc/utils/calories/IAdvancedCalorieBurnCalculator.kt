package com.project.neardoc.utils.calories

import com.project.neardoc.utils.GenderType
import java.util.*

interface IAdvancedCalorieBurnCalculator {
    fun calculateEnergyExpenditure(
        height: Float,
        age: Float,
        weight: Float,
        gender: GenderType,
        durationInSeconds: Long,
        stepsTaken: Int,
        strideLengthInMetres: Float
    ): Float
}