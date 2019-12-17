package com.project.neardoc.utils.calories
import com.project.neardoc.utils.GenderType
import java.util.*
import java.util.concurrent.TimeUnit


class AdvancedCalorieBurnedCalculator {

    fun calculateEnergyExpenditure(
        height: Float,
        age: Date,
        weight: Float,
        gender: GenderType,
        durationInSeconds: Long,
        stepsTaken: Int,
        strideLengthInMetres: Float
    ): Float {
        val ageCalculated = getAgeFromDateOfBirth(age)
        val harrisBenedictRmR = convertKilocaloriesToMlKmin(
            harrisBenedictRmr(
                gender,
                weight,
                ageCalculated,
                convertMetresToCentimetre(height)
            ), weight
        )
        val kmTravelled =
            calculateDistanceTravelledInKM(stepsTaken, strideLengthInMetres)
        val hours: Float = TimeUnit.SECONDS.toHours(durationInSeconds).toFloat()
        val speedInMph: Float = (0.621371 * kmTravelled).toFloat()
        val metValue = getMetForActivity(speedInMph)
        val constant = 3.5f
        val correctedMets = metValue * (constant / harrisBenedictRmR)
        return correctedMets * hours * weight
    }

    /**
     * Gets a users age from a date. Only takes into account years.
     *
     * @param age The date of birth.
     * @return The age in years.
     */
    private fun getAgeFromDateOfBirth(age: Date): Float {
        val currentDate: Calendar = Calendar.getInstance()
        val dateOfBirth: Calendar = Calendar.getInstance()
        dateOfBirth.setTime(age)
        require(!dateOfBirth.after(currentDate)) { "Can't be born in the future" }
        val currentYear: Int = currentDate.get(Calendar.YEAR)
        val dateOfBirthYear: Int = dateOfBirth.get(Calendar.YEAR)
        var age2 = currentYear - dateOfBirthYear
        val currentMonth: Int = currentDate.get(Calendar.MONTH)
        val dateOfBirthMonth: Int = dateOfBirth.get(Calendar.MONTH)
        if (dateOfBirthMonth > currentMonth) {
            age2--
        } else if (currentMonth == dateOfBirthMonth) {
            val currentDay: Int = currentDate.get(Calendar.DAY_OF_MONTH)
            val dateOfBirthDay: Int = dateOfBirth.get(Calendar.DAY_OF_MONTH)
            if (dateOfBirthDay > currentDay) {
                age2--
            }
        }
        return age2.toFloat()
    }

    private fun convertKilocaloriesToMlKmin(
        kilocalories: Float,
        weightKgs: Float
    ): Float {
        var kcalMin = kilocalories / 1440
        kcalMin /= 5f
        return kcalMin / weightKgs * 1000
    }

    private fun convertMetresToCentimetre(metres: Float): Float {
        return metres * 100
    }

    fun calculateDistanceTravelledInKM(
        stepsTaken: Int,
        entityStrideLength: Float
    ): Float {
        return stepsTaken.toFloat() * entityStrideLength / 1000
    }

    /**
     * Gets the MET value for an activity. Based on https://sites.google.com/site/compendiumofphysicalactivities/Activity-Categories/walking .
     *
     * @param speedInMph The speed in miles per hour
     * @return The met value.
     */
    private fun getMetForActivity(speedInMph: Float): Float {
        if (speedInMph < 2.0) {
            return 2.0f
        } else if (java.lang.Float.compare(speedInMph, 2.0f) == 0) {
            return 2.8f
        } else if (java.lang.Float.compare(speedInMph, 2.0f) > 0 && java.lang.Float.compare(
                speedInMph,
                2.7f
            ) <= 0
        ) {
            return 3.0f
        } else if (java.lang.Float.compare(speedInMph, 2.8f) > 0 && java.lang.Float.compare(
                speedInMph,
                3.3f
            ) <= 0
        ) {
            return 3.5f
        } else if (java.lang.Float.compare(speedInMph, 3.4f) > 0 && java.lang.Float.compare(
                speedInMph,
                3.5f
            ) <= 0
        ) {
            return 4.3f
        } else if (java.lang.Float.compare(speedInMph, 3.5f) > 0 && java.lang.Float.compare(
                speedInMph,
                4.0f
            ) <= 0
        ) {
            return 5.0f
        } else if (java.lang.Float.compare(speedInMph, 4.0f) > 0 && java.lang.Float.compare(
                speedInMph,
                4.5f
            ) <= 0
        ) {
            return 7.0f
        } else if (java.lang.Float.compare(speedInMph, 4.5f) > 0 && java.lang.Float.compare(
                speedInMph,
                5.0f
            ) <= 0
        ) {
            return 8.3f
        } else if (java.lang.Float.compare(speedInMph, 5.0f) > 0) {
            return 9.8f
        }
        return 0f
    }

    /**
     * Calculates the Harris Benedict RMR value for an entity. Based on above calculation for Com
     *
     * @param gender   Users gender.
     * @param weightKg Weight in Kg.
     * @param age      Age in years.
     * @param heightCm Height in CM.
     * @return Harris benedictRMR value.
     */
    private fun harrisBenedictRmr(
        gender: GenderType,
        weightKg: Float,
        age: Float,
        heightCm: Float
    ): Float {
        return if (gender == GenderType.MALE) {
            655.0955f + 1.8496f * heightCm + 9.5634f * weightKg - 4.6756f * age
        } else {
            66.4730f + 5.0033f * heightCm + 13.7516f * weightKg - 6.7550f * age
        }
    }
}