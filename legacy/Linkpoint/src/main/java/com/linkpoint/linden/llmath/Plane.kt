package com.linkpoint.linden.llmath

import kotlin.math.*

class Plane {
    var a: Float = 0f
    var b: Float = 0f
    var c: Float = 0f
    var d: Float = 1f

    constructor()

    constructor(point: Vector3, dVal: Float) {
        a = point.x; b = point.y; c = point.z; d = dVal
    }

    constructor(point: Vector3, n: Vector3) {
        val dVal = -(point * n)
        a = n.x; b = n.y; c = n.z; d = dVal
    }

    constructor(p0: Vector3, p1: Vector3, p2: Vector3) {
        val u = p1 - p0
        val v = p2 - p0
        val w = u % v
        w.normalize()
        val dVal = -(w * p0)
        a = w.x; b = w.y; c = w.z; d = dVal
    }

    fun set(other: Plane) { a = other.a; b = other.b; c = other.c; d = other.d }

    fun set(n: Vector3, dVal: Float) { a = n.x; b = n.y; c = n.z; d = dVal }

    fun normal(): Vector3 = Vector3(a, b, c)

    fun dist(v: Vector3): Float = a * v.x + b * v.y + c * v.z + d

    fun clear() { a = 0f; b = 0f; c = 0f; d = 1f }

    fun calcPlaneMask(): UByte {
        var mask = 0u
        if (a >= 0f) mask = mask or 0x1u
        if (b >= 0f) mask = mask or 0x2u
        if (c >= 0f) mask = mask or 0x4u
        return mask.toUByte()
    }

    operator fun get(idx: Int): Float = when (idx) { 0 -> a; 1 -> b; 2 -> c; else -> d }

    fun equal(other: Plane): Boolean =
        a == other.a && b == other.b && c == other.c && d == other.d

    override fun toString(): String = "Plane($a, $b, $c, $d)"
}
