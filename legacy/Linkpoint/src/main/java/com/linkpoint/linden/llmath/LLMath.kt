package com.linkpoint.linden.llmath

import kotlin.math.*

const val GRAVITY: Float = -9.8f

const val F_PI: Float = 3.1415926535897932384626433832795f
const val F_TWO_PI: Float = 6.283185307179586476925286766559f
const val F_PI_BY_TWO: Float = 1.5707963267948966192313216916398f
const val F_SQRT_TWO_PI: Float = 2.506628274631000502415765284811f
const val F_E: Float = 2.71828182845904523536f
const val F_SQRT2: Float = 1.4142135623730950488016887242097f
const val F_SQRT3: Float = 1.73205080756888288657986402541f
const val OO_SQRT2: Float = 0.7071067811865475244008443621049f
const val OO_SQRT3: Float = 0.577350269189625764509f
const val DEG_TO_RAD: Float = 0.017453292519943295769236907684886f
const val RAD_TO_DEG: Float = 57.295779513082320876798154814105f
const val F_APPROXIMATELY_ZERO: Float = 0.00001f
const val F_LN10: Float = 2.3025850929940456840179914546844f
const val OO_LN10: Float = 0.43429448190325182765112891891661f
const val F_LN2: Float = 0.69314718056f
const val OO_LN2: Float = 1.4426950408889634073599246810019f

const val F_ALMOST_ZERO: Float = 0.0001f
const val F_ALMOST_ONE: Float = 1.0f - F_ALMOST_ZERO

const val GIMBAL_THRESHOLD: Float = 0.000436f

const val FP_MAG_THRESHOLD: Float = 0.0000001f

const val FAST_MAG_ALPHA: Float = 0.960433870103f
const val FAST_MAG_BETA: Float = 0.397824734759f

fun isApproxZero(f: Float): Boolean = (-F_APPROXIMATELY_ZERO < f) && (f < F_APPROXIMATELY_ZERO)

fun isZero(x: Float): Boolean = (x.toBits() and 0x7fffffff) == 0

fun isApproxEqual(x: Float, y: Float): Boolean {
    val COMPARE_MANTISSA_UP_TO_BIT = 0x02
    return abs(x.toBits() - y.toBits()) < COMPARE_MANTISSA_UP_TO_BIT
}

fun isApproxEqual(x: Double, y: Double): Boolean {
    val COMPARE_MANTISSA_UP_TO_BIT = 0x02L
    return abs(x.toBits() - y.toBits()) < COMPARE_MANTISSA_UP_TO_BIT
}

fun llFloor(f: Float): Int = floor(f).toInt()

fun llCeil(f: Float): Int = ceil(f).toInt()

fun llRound(v: Float): Int = llFloor(v + 0.5f)

fun llRound(v: Double): Double = floor(v + 0.5)

fun llRound(v: Float, nearest: Float): Float = floor(v * (1.0f / nearest) + 0.5f) * nearest

fun llRound(v: Double, nearest: Double): Double = floor(v * (1.0 / nearest) + 0.5) * nearest

inline fun <T : Comparable<T>> llClamp(value: T, min: T, max: T): T =
    if (value < min) min else if (value > max) max else value

inline fun <T : Comparable<T>> llMin(a: T, b: T): T = if (a < b) a else b

inline fun <T : Comparable<T>> llMax(a: T, b: T): T = if (a > b) a else b

fun fastMagnitude(a: Float, b: Float): Float {
    val absA = abs(a)
    val absB = abs(b)
    return FAST_MAG_ALPHA * llMax(absA, absB) + FAST_MAG_BETA * llMin(absA, absB)
}

fun fastPow(x: Float, y: Float): Float = exp(y * ln(x))

fun snapToSigFigs(foo: Float, sigFigs: Int): Float {
    var bar = 1f
    for (i in 0 until sigFigs) bar *= 10f
    val sign = if (foo > 0f) 1f else -1f
    var result = (foo * bar + sign * 0.5f).toLong().toFloat()
    result /= bar
    return result
}

fun lerpScalar(a: Float, b: Float, t: Float): Float = a + (b - a) * t

fun lerp2d(x00: Float, x01: Float, x10: Float, x11: Float, u: Float, v: Float): Float {
    val a = x00 + (x01 - x00) * u
    val b = x10 + (x11 - x10) * u
    return a + (b - a) * v
}

fun ramp(x: Float, a: Float, b: Float): Float =
    if (a == b) 0.0f else (a - x) / (a - b)

fun rescale(x: Float, x1: Float, x2: Float, y1: Float, y2: Float): Float =
    lerpScalar(y1, y2, ramp(x, x1, x2))

fun clampRescale(x: Float, x1: Float, x2: Float, y1: Float, y2: Float): Float {
    val r = rescale(x, x1, x2, y1, y2)
    return if (y1 < y2) llClamp(r, y1, y2) else llClamp(r, y2, y1)
}

fun cubicStep(x: Float, x0: Float, x1: Float, s0: Float, s1: Float): Float {
    if (x <= x0) return s0
    if (x >= x1) return s1
    val f = (x - x0) / (x1 - x0)
    return s0 + (s1 - s0) * (f * f) * (3.0f - 2.0f * f)
}

fun cubicStep(x: Float): Float {
    val cx = llClamp(x, 0f, 1f)
    return (cx * cx) * (3.0f - 2.0f * cx)
}

fun quadraticStep(x: Float, x0: Float, x1: Float, s0: Float, s1: Float): Float {
    if (x <= x0) return s0
    if (x >= x1) return s1
    val f = (x - x0) / (x1 - x0)
    val fSq = f * f
    return s0 * (1f - fSq) + (s1 - s0) * fSq
}

fun simpleAngle(angleIn: Float): Float {
    var angle = angleIn
    while (angle <= -F_PI) angle += F_TWO_PI
    while (angle > F_PI) angle -= F_TWO_PI
    return angle
}

fun getLowerPowerTwo(valIn: UInt, maxPowerTwoIn: UInt): UInt {
    var maxPowerTwo = if (maxPowerTwoIn == 0u) (1u shl 31) else maxPowerTwoIn
    if (maxPowerTwo and (maxPowerTwo - 1u) != 0u) return 0u
    var v = valIn
    while (v < maxPowerTwo) maxPowerTwo = maxPowerTwo shr 1
    return maxPowerTwo
}

fun getNextPowerTwo(valIn: UInt, maxPowerTwoIn: UInt): UInt {
    var maxPowerTwo = if (maxPowerTwoIn == 0u) (1u shl 31) else maxPowerTwoIn
    if (valIn >= maxPowerTwo) return maxPowerTwo
    var v = valIn - 1u
    v = v or (v shr 1)
    v = v or (v shr 2)
    v = v or (v shr 4)
    v = v or (v shr 8)
    v = v or (v shr 16)
    return v + 1u
}

fun gaussian(x: Float, o: Float): Float =
    1f / (F_SQRT_TWO_PI * o) * F_E.pow(-(x * x) / (2f * o * o))

fun linearToSRGB(v: Float): Float =
    if (v < 0.0031308f) v * 12.92f
    else 1.055f * v.pow(1.0f / 2.4f) - 0.055f

fun sRGBToLinear(v: Float): Float =
    if (v < 0.04045f) v / 12.92f
    else ((v + 0.055f) / 1.055f).pow(2.4f)
