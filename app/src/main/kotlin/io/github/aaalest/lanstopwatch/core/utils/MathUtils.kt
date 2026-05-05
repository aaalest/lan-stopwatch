package io.github.aaalest.lanstopwatch.core.utils


/**
 * Maps a value from one range to another.
 */
fun Long.mapRange(
    inMin: Long,
    inMax: Long,
    outMin: Long,
    outMax: Long
): Long {
    if (inMax == inMin) return outMin
    val rangeSize = (inMax - inMin)
    val valueOffset = (this - inMin)
    return outMin + (valueOffset * (outMax - outMin) / rangeSize)
}
