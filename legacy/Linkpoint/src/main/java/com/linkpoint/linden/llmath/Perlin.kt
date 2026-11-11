package com.linkpoint.linden.llmath

import kotlin.math.sqrt
import kotlin.random.Random

object PerlinNoise {

    private const val B  = 0x100
    private const val BM = 0xff
    private const val NF = 4096f

    private val perm = IntArray(B + B + 2)
    private val g3   = Array(B + B + 2) { FloatArray(3) }
    private val g2   = Array(B + B + 2) { FloatArray(2) }
    private val g1   = FloatArray(B + B + 2)

    init { init() }

    private fun normalize2(v: FloatArray) {
        val s = 1f / sqrt(v[0] * v[0] + v[1] * v[1])
        v[0] *= s; v[1] *= s
    }

    private fun normalize3(v: FloatArray) {
        val s = 1f / sqrt(v[0] * v[0] + v[1] * v[1] + v[2] * v[2])
        v[0] *= s; v[1] *= s; v[2] *= s
    }

    private fun init() {
        val rng = Random(0)
        for (i in 0 until B) {
            perm[i] = i
            g1[i]   = ((rng.nextInt(B + B)) - B).toFloat() / B
            for (j in 0 until 2) g2[i][j] = ((rng.nextInt(B + B)) - B).toFloat() / B
            normalize2(g2[i])
            for (j in 0 until 3) g3[i][j] = ((rng.nextInt(B + B)) - B).toFloat() / B
            normalize3(g3[i])
        }

        var i = B - 1
        while (i > 0) {
            val k = perm[i]
            val j = rng.nextInt(B)
            perm[i] = perm[j]
            perm[j] = k
            i--
        }

        for (i in 0 until B + 2) {
            perm[B + i] = perm[i]
            g1[B + i]   = g1[i]
            for (j in 0 until 2) g2[B + i][j] = g2[i][j]
            for (j in 0 until 3) g3[B + i][j] = g3[i][j]
        }
    }

    private fun sCurve(t: Float): Float = t * t * (3f - 2f * t)
    private fun lerp(t: Float, a: Float, b: Float): Float = a + t * (b - a)

    fun noise1(x: Float): Float {
        val r1  = x + NF
        val t   = r1.toInt()
        val bx0 = t and BM
        val bx1 = (bx0 + 1) and BM
        val rx0 = r1 - t
        val rx1 = rx0 - 1f
        val sx  = sCurve(rx0)
        return lerp(sx, rx0 * g1[perm[bx0]], rx1 * g1[perm[bx1]])
    }

    fun noise2(x: Float, y: Float): Float {
        val r1x = x + NF; val tx = r1x.toInt()
        val bx0 = tx and BM; val bx1 = (bx0 + 1) and BM
        val rx0 = r1x - tx; val rx1 = rx0 - 1f

        val r1y = y + NF; val ty = r1y.toInt()
        val by0 = ty and BM; val by1 = (by0 + 1) and BM
        val ry0 = r1y - ty; val ry1 = ry0 - 1f

        val i = perm[bx0]; val j = perm[bx1]
        val b00 = perm[i + by0]; val b10 = perm[j + by0]
        val b01 = perm[i + by1]; val b11 = perm[j + by1]

        val sx = sCurve(rx0); val sy = sCurve(ry0)

        val a = lerp(sx,
            rx0 * g2[b00][0] + ry0 * g2[b00][1],
            rx1 * g2[b10][0] + ry0 * g2[b10][1])
        val b = lerp(sx,
            rx0 * g2[b01][0] + ry1 * g2[b01][1],
            rx1 * g2[b11][0] + ry1 * g2[b11][1])

        return lerp(sy, a, b)
    }

    fun noise3(x: Float, y: Float, z: Float): Float {
        val r1x = x + NF; val tx = r1x.toInt()
        val bx0 = tx and BM; val bx1 = (bx0 + 1) and BM
        val rx0 = r1x - tx; val rx1 = rx0 - 1f

        val r1y = y + NF; val ty = r1y.toInt()
        val by0 = ty and BM; val by1 = (by0 + 1) and BM
        val ry0 = r1y - ty; val ry1 = ry0 - 1f

        val r1z = z + NF; val tz = r1z.toInt()
        val bz0 = tz and BM; val bz1 = (bz0 + 1) and BM
        val rz0 = r1z - tz; val rz1 = rz0 - 1f

        val i = perm[bx0]; val j = perm[bx1]
        val b00 = perm[i + by0]; val b10 = perm[j + by0]
        val b01 = perm[i + by1]; val b11 = perm[j + by1]

        val sx = sCurve(rx0); val sy = sCurve(ry0); val sz = sCurve(rz0)

        fun at3(idx: Int, rx: Float, ry: Float, rz: Float): Float =
            rx * g3[idx][0] + ry * g3[idx][1] + rz * g3[idx][2]

        val a0 = lerp(sx, at3(b00 + bz0, rx0, ry0, rz0), at3(b10 + bz0, rx1, ry0, rz0))
        val b0 = lerp(sx, at3(b01 + bz0, rx0, ry1, rz0), at3(b11 + bz0, rx1, ry1, rz0))
        val c  = lerp(sy, a0, b0)

        val a1 = lerp(sx, at3(b00 + bz1, rx0, ry0, rz1), at3(b10 + bz1, rx1, ry0, rz1))
        val b1 = lerp(sx, at3(b01 + bz1, rx0, ry1, rz1), at3(b11 + bz1, rx1, ry1, rz1))
        val d  = lerp(sy, a1, b1)

        return lerp(sz, c, d)
    }

    fun turbulence2(x: Float, y: Float, freq: Float): Float {
        var t = 0f; var f = freq
        while (f >= 1f) { t += noise2(f * x, f * y) / f; f *= 0.5f }
        return t
    }

    fun turbulence3(x: Float, y: Float, z: Float, freq: Float): Float {
        var t = 0f; var f = freq
        while (f >= 1f) { t += noise3(f * x, f * y, f * z) / f; f *= 0.5f }
        return t
    }

    fun cloud3(x: Float, y: Float, z: Float, freq: Float): Float {
        var t = 0f; var f = freq
        while (f >= 1f) { val n = noise3(f * x, f * y, f * z); t += (n * n) / f; f *= 0.5f }
        return t
    }
}
