package io.github.aaalest.lanstopwatch.core.utils


/**
 * Maps a value from one range to another.
 */
fun Double.mapRange(
    inMin: Double,
    inMax: Double,
    outMin: Double,
    outMax: Double
): Double {
    if (inMax == inMin) return outMin
    val rangeSize = (inMax - inMin)
    val valueOffset = (this - inMin)
    return outMin + (valueOffset * (outMax - outMin) / rangeSize)
}
