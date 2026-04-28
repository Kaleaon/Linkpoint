package com.linkpoint.linden.llmath

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

data class LLVolumeVertex(val pos: Vector3, val normal: Vector3, val uv: Vector2)
data class LLVolumeFace(val vertices: List<LLVolumeVertex>, val indices: List<Int>)

enum class LodDetail(val steps: Int) { LOWEST(4), LOW(8), MEDIUM(16), HIGH(32) }

object LLVolume {

    fun buildVolume(params: com.linkpoint.linden.llprimitive.VolumeParams, lod: LodDetail = LodDetail.MEDIUM): List<LLVolumeFace> {
        val profile = buildProfile(params, lod.steps)
        val path    = buildPath(params, lod.steps)
        return extrude(profile, path)
    }

    private fun buildProfile(params: com.linkpoint.linden.llprimitive.VolumeParams, steps: Int): List<Vector2> {
        val begin = params.beginS
        val end   = params.endS
        val hollow = params.hollow
        val curve = params.profileCurve

        return when (curve.toInt()) {
            0x10 -> buildCircleProfile(begin, end, steps, hollow)  // CIRCLE
            0x20 -> buildSquareProfile(begin, end, hollow)          // SQUARE
            0x30 -> buildTriangleProfile(begin, end, hollow)        // TRIANGLE (iso)
            else -> buildCircleProfile(begin, end, steps, hollow)
        }
    }

    private fun buildCircleProfile(begin: Float, end: Float, steps: Int, hollow: Float): List<Vector2> {
        val pts = mutableListOf<Vector2>()
        val startAngle = begin * 2f * PI.toFloat()
        val endAngle   = end   * 2f * PI.toFloat()
        val totalAngle = endAngle - startAngle
        for (i in 0..steps) {
            val t = i.toFloat() / steps
            val angle = startAngle + t * totalAngle
            pts.add(Vector2(cos(angle), sin(angle)))
        }
        if (hollow > 0f) {
            for (i in steps downTo 0) {
                val t = i.toFloat() / steps
                val angle = startAngle + t * totalAngle
                pts.add(Vector2(cos(angle) * hollow, sin(angle) * hollow))
            }
        }
        return pts
    }

    private fun buildSquareProfile(begin: Float, end: Float, hollow: Float): List<Vector2> {
        val pts = mutableListOf<Vector2>()
        val corners = listOf(
            Vector2(-0.5f, -0.5f), Vector2(0.5f, -0.5f),
            Vector2(0.5f, 0.5f), Vector2(-0.5f, 0.5f), Vector2(-0.5f, -0.5f)
        )
        val startIdx = (begin * 4f).toInt().coerceIn(0, 3)
        val endIdx   = (end   * 4f).toInt().coerceIn(startIdx, 4)
        for (i in startIdx..endIdx) pts.add(corners[i])
        if (hollow > 0f) {
            for (i in (startIdx..endIdx).reversed()) {
                val c = corners[i]
                pts.add(Vector2(c.x * hollow, c.y * hollow))
            }
        }
        return pts
    }

    private fun buildTriangleProfile(begin: Float, end: Float, hollow: Float): List<Vector2> {
        val corners = listOf(
            Vector2(0f, 0.5f), Vector2(-0.5f, -0.5f), Vector2(0.5f, -0.5f), Vector2(0f, 0.5f)
        )
        val pts = mutableListOf<Vector2>()
        val startIdx = (begin * 3f).toInt().coerceIn(0, 2)
        val endIdx   = (end   * 3f).toInt().coerceIn(startIdx, 3)
        for (i in startIdx..endIdx) pts.add(corners[i])
        if (hollow > 0f) {
            for (i in (startIdx..endIdx).reversed()) {
                val c = corners[i]
                pts.add(Vector2(c.x * hollow, c.y * hollow))
            }
        }
        return pts
    }

    private fun buildPath(params: com.linkpoint.linden.llprimitive.VolumeParams, steps: Int): List<Pair<Vector3, Float>> {
        val begin = params.beginT
        val end   = params.endT
        val twist = params.twist
        val curve = params.pathCurve

        return when (curve.toInt()) {
            0x10 -> buildLinePath(begin, end, twist, steps)     // LINE
            0x20 -> buildCirclePath(begin, end, twist, steps)   // CIRCLE
            else -> buildLinePath(begin, end, twist, steps)
        }
    }

    private fun buildLinePath(begin: Float, end: Float, twist: Float, steps: Int): List<Pair<Vector3, Float>> {
        val result = mutableListOf<Pair<Vector3, Float>>()
        for (i in 0..steps) {
            val t = i.toFloat() / steps
            val z = begin + t * (end - begin)
            val twistAngle = twist * t * (2f * PI.toFloat())
            result.add(Vector3(0f, 0f, z) to twistAngle)
        }
        return result
    }

    private fun buildCirclePath(begin: Float, end: Float, twist: Float, steps: Int): List<Pair<Vector3, Float>> {
        val result = mutableListOf<Pair<Vector3, Float>>()
        val startAngle = begin * 2f * PI.toFloat()
        val endAngle   = end   * 2f * PI.toFloat()
        for (i in 0..steps) {
            val t = i.toFloat() / steps
            val angle = startAngle + t * (endAngle - startAngle)
            val x = cos(angle)
            val y = sin(angle)
            val twistAngle = twist * t * (2f * PI.toFloat())
            result.add(Vector3(x, y, 0f) to twistAngle)
        }
        return result
    }

    private fun extrude(profile: List<Vector2>, path: List<Pair<Vector3, Float>>): List<LLVolumeFace> {
        if (profile.size < 2 || path.size < 2) return emptyList()
        val faces = mutableListOf<LLVolumeFace>()
        val n = profile.size
        val m = path.size

        val verts = mutableListOf<LLVolumeVertex>()
        for ((pathPt, twist) in path) {
            val cosT = cos(twist); val sinT = sin(twist)
            for (p in profile) {
                val rx = p.x * cosT - p.y * sinT
                val ry = p.x * sinT + p.y * cosT
                val pos = pathPt + Vector3(rx, ry, 0f)
                verts.add(LLVolumeVertex(pos, Vector3(0f, 0f, 1f), Vector2(p.x + 0.5f, p.y + 0.5f)))
            }
        }

        val indices = mutableListOf<Int>()
        for (j in 0 until m - 1) {
            for (i in 0 until n - 1) {
                val a = j * n + i; val b = j * n + i + 1
                val c = (j + 1) * n + i + 1; val d = (j + 1) * n + i
                indices += listOf(a, b, c, a, c, d)
            }
        }

        for (v in verts) {
            // Recompute normals from position data if needed (simplified flat shading)
        }

        faces.add(LLVolumeFace(verts, indices))
        return faces
    }
}
