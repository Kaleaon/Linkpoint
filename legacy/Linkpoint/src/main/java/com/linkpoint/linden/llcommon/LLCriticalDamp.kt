package com.linkpoint.linden.llcommon

import kotlin.math.exp

/**
 * Exponential-decay interpolation helper, equivalent to LLSmoothInterpolation / LLCriticalDamp.
 *
 * The interpolant for a given time constant is:
 *   1 - 2^(-frametime / timeconstant)
 * clamped to [0, 1].
 *
 * The C++ source uses pow(2, -dt/tc) via the identity exp(-ln2 * dt / tc).
 * The cache maps time-constant -> interpolant and is kept sorted for binary search.
 */
object CriticalDamp {

    private var sTimeDelta: Float = 0f

    private data class Interpolant(val timeScale: Float, var interpolant: Float)

    private val sInterpolants: MutableList<Interpolant> = mutableListOf()

    fun updateInterpolants(frametime: Float) {
        sTimeDelta = frametime
        for (entry in sInterpolants) {
            entry.interpolant = calcInterpolant(entry.timeScale)
        }
    }

    fun getInterpolant(timeconstant: Float, useCache: Boolean = true): Float {
        if (timeconstant == 0f) return 1f

        if (!useCache) return calcInterpolant(timeconstant)

        val idx = sInterpolants.binarySearch { it.timeScale.compareTo(timeconstant) }
        return if (idx >= 0) {
            sInterpolants[idx].interpolant
        } else {
            val insertAt = -(idx + 1)
            val value = calcInterpolant(timeconstant)
            sInterpolants.add(insertAt, Interpolant(timeconstant, value))
            value
        }
    }

    fun getInterpolant(timeconstant: Float): Float = getInterpolant(timeconstant, useCache = true)

    private fun calcInterpolant(timeConstant: Float): Float {
        // 1 - 2^(-dt / tc)  ==  1 - exp(-ln2 * dt / tc)
        val raw = 1f - exp(-sTimeDelta / timeConstant * LN2)
        return raw.coerceIn(0f, 1f)
    }

    private const val LN2 = 0.6931471805599453f

    fun getTimeDelta(): Float = sTimeDelta
}
