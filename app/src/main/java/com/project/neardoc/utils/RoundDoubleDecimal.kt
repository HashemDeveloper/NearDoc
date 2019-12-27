package com.project.neardoc.utils

import java.math.RoundingMode
import java.text.DecimalFormat

fun roundOff2DecimalPoint(number: Double): Double? {
    val df = DecimalFormat("#.##")
    df.roundingMode = RoundingMode.CEILING
    return df.format(number).toDouble()
}
fun roundOff1DecimalPoint(number: Double): Double? {
    val df = DecimalFormat("#.#")
    df.roundingMode = RoundingMode.CEILING
    return df.format(number).toDouble()
}