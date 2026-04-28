package com.linkpoint.linden.llmath

import kotlin.math.abs
import kotlin.math.sqrt

data class RayHit(val t: Float, val point: Vector3, val normal: Vector3)

object LLRayTrace {

    fun rayAABBTest(
        origin: Vector3,
        dir: Vector3,
        minBound: Vector3,
        maxBound: Vector3
    ): Float? {
        var tMin = Float.NEGATIVE_INFINITY
        var tMax = Float.POSITIVE_INFINITY
        for (i in 0..2) {
            val o = origin[i]; val d = dir[i]
            val bMin = minBound[i]; val bMax = maxBound[i]
            if (abs(d) < 1e-8f) {
                if (o < bMin || o > bMax) return null
            } else {
                val invD = 1f / d
                var t1 = (bMin - o) * invD
                var t2 = (bMax - o) * invD
                if (t1 > t2) { val tmp = t1; t1 = t2; t2 = tmp }
                tMin = maxOf(tMin, t1)
                tMax = minOf(tMax, t2)
                if (tMin > tMax) return null
            }
        }
        return if (tMax < 0f) null else if (tMin < 0f) tMax else tMin
    }

    fun rayTriangleTest(
        origin: Vector3,
        dir: Vector3,
        v0: Vector3,
        v1: Vector3,
        v2: Vector3
    ): RayHit? {
        val edge1 = v1 - v0
        val edge2 = v2 - v0
        val h = dir % edge2
        val a = edge1 * h   // dot product via operator*
        if (abs(a) < 1e-8f) return null
        val f = 1f / a
        val s = origin - v0
        val u = f * (s * h)
        if (u < 0f || u > 1f) return null
        val q = s % edge1
        val v = f * (dir * q)
        if (v < 0f || u + v > 1f) return null
        val t = f * (edge2 * q)
        if (t < 1e-8f) return null
        val point = origin + dir * t
        val nVec = edge1 % edge2
        nVec.normalize()
        return RayHit(t, point, nVec)
    }

    fun raySphereTest(
        origin: Vector3,
        dir: Vector3,
        center: Vector3,
        radius: Float
    ): Float? {
        val oc = origin - center
        val a = dir * dir
        val b = 2f * (oc * dir)
        val c = (oc * oc) - radius * radius
        val discriminant = b * b - 4f * a * c
        if (discriminant < 0f) return null
        val t = (-b - sqrt(discriminant)) / (2f * a)
        return if (t > 1e-8f) t else {
            val t2 = (-b + sqrt(discriminant)) / (2f * a)
            if (t2 > 1e-8f) t2 else null
        }
    }
}
