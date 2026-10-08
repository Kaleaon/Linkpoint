package com.linkpoint.linden.llcommon

import kotlin.math.sqrt

class LLStatsAccumulator {
    private var count: Long = 0L
    private var sum: Double = 0.0
    private var sumSq: Double = 0.0
    private var min: Double = Double.MAX_VALUE
    private var max: Double = -Double.MAX_VALUE

    fun record(value: Double) {
        count++
        sum += value
        sumSq += value * value
        if (value < min) min = value
        if (value > max) max = value
    }

    fun record(value: Float) = record(value.toDouble())
    fun record(value: Int) = record(value.toDouble())

    fun getCount(): Long = count
    fun getSum(): Double = sum
    fun getMean(): Double = if (count == 0L) 0.0 else sum / count
    fun getMin(): Double = if (count == 0L) 0.0 else min
    fun getMax(): Double = if (count == 0L) 0.0 else max

    fun getVariance(): Double {
        if (count < 2L) return 0.0
        val mean = getMean()
        return (sumSq / count) - (mean * mean)
    }

    fun getStdDev(): Double = sqrt(getVariance().coerceAtLeast(0.0))

    fun reset() {
        count = 0L
        sum = 0.0
        sumSq = 0.0
        min = Double.MAX_VALUE
        max = -Double.MAX_VALUE
    }
}
