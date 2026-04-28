package com.linkpoint.linden.llprimitive

import com.linkpoint.linden.llmath.Vector3
import kotlin.math.*

data class VolumeFace(
    val vertices: FloatArray,   // x,y,z triples
    val normals: FloatArray,    // x,y,z triples
    val texcoords: FloatArray,  // u,v pairs
    val indices: ShortArray,
    val center: Vector3 = Vector3.ZERO,
) {
    val numVertices: Int get() = vertices.size / 3
    val numIndices: Int get() = indices.size
}

enum class LodLevel(val detail: Int) { HIGH(4), MEDIUM(3), LOW(2), LOWEST(1) }

class LLVolume(val params: VolumeParams, val lod: LodLevel = LodLevel.HIGH) {

    val faces: List<VolumeFace> by lazy { generateFaces() }

    private fun generateFaces(): List<VolumeFace> {
        val profile = buildProfile()
        val path = buildPath()
        if (profile.isEmpty() || path.isEmpty()) return emptyList()
        return when {
            isSculpt() -> generateSculptFaces(profile, path)
            else       -> generateExtrusionFaces(profile, path)
        }
    }

    private fun isSculpt() = params.sculptType != SculptType.NONE && params.sculptId.notNull()

    // ---- profile (S-axis cross-section) ----

    private fun buildProfile(): List<FloatArray> {
        val steps = profileSteps()
        val pts = mutableListOf<FloatArray>()
        val curve = (params.profileCurve and SculptType.TYPE_MASK).toInt()
        val beginS = params.beginS
        val endS = params.endS
        val hollow = params.hollow.coerceIn(0f, 0.95f)

        when (curve) {
            0x00 -> { // circle
                for (i in 0..steps) {
                    val t = beginS + (endS - beginS) * i.toFloat() / steps
                    val a = t * 2f * PI.toFloat()
                    pts.add(floatArrayOf(cos(a) * 0.5f, sin(a) * 0.5f))
                }
            }
            0x01 -> { // square
                squareProfile(pts, steps, beginS, endS)
            }
            0x02, 0x03, 0x04 -> { // triangle variants
                triangleProfile(pts, steps, beginS, endS)
            }
            0x05 -> { // half-circle
                for (i in 0..steps) {
                    val t = beginS + (endS - beginS) * i.toFloat() / steps
                    val a = t * PI.toFloat()
                    pts.add(floatArrayOf(cos(a) * 0.5f, sin(a) * 0.5f))
                }
            }
            else -> squareProfile(pts, steps, beginS, endS)
        }

        if (hollow > 0f) {
            val inner = pts.map { floatArrayOf(it[0] * (1f - hollow), it[1] * (1f - hollow)) }
            return pts + inner.reversed()
        }
        return pts
    }

    private fun squareProfile(pts: MutableList<FloatArray>, steps: Int, beginS: Float, endS: Float) {
        for (i in 0..steps) {
            val t = beginS + (endS - beginS) * i.toFloat() / steps
            val s = t * 4f
            val x: Float; val y: Float
            when {
                s < 1f -> { x = s - 0.5f;  y = -0.5f }
                s < 2f -> { x = 0.5f;       y = s - 1.5f }
                s < 3f -> { x = 2.5f - s;   y = 0.5f }
                else   -> { x = -0.5f;       y = 3.5f - s }
            }
            pts.add(floatArrayOf(x, y))
        }
    }

    private fun triangleProfile(pts: MutableList<FloatArray>, steps: Int, beginS: Float, endS: Float) {
        for (i in 0..steps) {
            val t = beginS + (endS - beginS) * i.toFloat() / steps
            val s = t * 3f
            val x: Float; val y: Float
            when {
                s < 1f -> { x = s - 0.5f;         y = -0.5f }
                s < 2f -> { x = 1f - s;            y = (s - 1f) - 0.5f }
                else   -> { x = -(s - 2f) * 0.5f; y = 0.5f - (s - 2f) * 0.5f }
            }
            pts.add(floatArrayOf(x, y))
        }
    }

    // ---- path (T-axis extrusion spine) ----

    private fun buildPath(): List<FloatArray> {
        val steps = pathSteps()
        val pts = mutableListOf<FloatArray>()
        val curve = params.pathCurve.toInt()
        val beginT = params.beginT
        val endT = params.endT

        when (curve) {
            0x10 -> { // line
                for (i in 0..steps) {
                    val t = beginT + (endT - beginT) * i.toFloat() / steps
                    pts.add(floatArrayOf(0f, 0f, t - 0.5f, 1f, 0f))
                }
            }
            0x20, 0x30 -> { // circle / circle2
                val revs = params.revolutions.coerceAtLeast(1f)
                for (i in 0..steps) {
                    val t = beginT + (endT - beginT) * i.toFloat() / steps
                    val a = t * 2f * PI.toFloat() * revs
                    val r = 1f + params.radiusOffset
                    val skew = params.skew * (1f - t)
                    pts.add(floatArrayOf(cos(a) * r + skew, sin(a) * r, t - 0.5f, 1f, a))
                }
            }
            0x80 -> { // flexible — fallback to line
                for (i in 0..steps) {
                    val t = beginT + (endT - beginT) * i.toFloat() / steps
                    pts.add(floatArrayOf(0f, 0f, t - 0.5f, 1f, 0f))
                }
            }
            else -> {
                for (i in 0..steps) {
                    val t = beginT + (endT - beginT) * i.toFloat() / steps
                    pts.add(floatArrayOf(0f, 0f, t - 0.5f, 1f, 0f))
                }
            }
        }
        return pts
    }

    // ---- extrusion mesh generation ----

    private fun generateExtrusionFaces(profile: List<FloatArray>, path: List<FloatArray>): List<VolumeFace> {
        val pCount = profile.size
        val tCount = path.size
        val verts = mutableListOf<Float>()
        val norms = mutableListOf<Float>()
        val uvs = mutableListOf<Float>()

        for (ti in 0 until tCount) {
            val p = path[ti]
            val px = p[0]; val py = p[1]; val pz = p[2]
            val scale = p[3]
            val twist = params.twist * (ti.toFloat() / (tCount - 1).toFloat().coerceAtLeast(1f))
            val cosT = cos(twist); val sinT = sin(twist)
            val taper = params.taperX * (ti.toFloat() / (tCount - 1).toFloat().coerceAtLeast(1f))

            for (si in 0 until pCount) {
                val sp = profile[si]
                var sx = sp[0] * scale + taper; val sy = sp[1] * scale
                val rx = sx * cosT - sy * sinT
                val ry = sx * sinT + sy * cosT
                verts.add(rx + px); verts.add(ry + py); verts.add(pz)
                norms.add(rx); norms.add(ry); norms.add(0f)
                uvs.add(si.toFloat() / (pCount - 1).toFloat().coerceAtLeast(1f))
                uvs.add(ti.toFloat() / (tCount - 1).toFloat().coerceAtLeast(1f))
            }
        }

        val indices = mutableListOf<Short>()
        for (ti in 0 until tCount - 1) {
            for (si in 0 until pCount - 1) {
                val a = (ti * pCount + si).toShort()
                val b = (ti * pCount + si + 1).toShort()
                val c = ((ti + 1) * pCount + si + 1).toShort()
                val d = ((ti + 1) * pCount + si).toShort()
                indices.add(a); indices.add(b); indices.add(c)
                indices.add(a); indices.add(c); indices.add(d)
            }
        }

        val face = VolumeFace(
            vertices = verts.toFloatArray(),
            normals = normalizeNormals(norms.toFloatArray()),
            texcoords = uvs.toFloatArray(),
            indices = indices.toShortArray(),
        )
        return listOf(face)
    }

    private fun generateSculptFaces(profile: List<FloatArray>, path: List<FloatArray>): List<VolumeFace> =
        generateExtrusionFaces(profile, path)

    // ---- helpers ----

    private fun profileSteps(): Int = when (lod) {
        LodLevel.HIGH   -> 24
        LodLevel.MEDIUM -> 12
        LodLevel.LOW    -> 6
        LodLevel.LOWEST -> 3
    }

    private fun pathSteps(): Int = when (lod) {
        LodLevel.HIGH   -> 24
        LodLevel.MEDIUM -> 12
        LodLevel.LOW    -> 6
        LodLevel.LOWEST -> 3
    }

    private fun normalizeNormals(norms: FloatArray): FloatArray {
        val out = norms.copyOf()
        var i = 0
        while (i + 2 < out.size) {
            val x = out[i]; val y = out[i + 1]; val z = out[i + 2]
            val len = sqrt(x * x + y * y + z * z)
            if (len > 1e-6f) { out[i] /= len; out[i + 1] /= len; out[i + 2] /= len }
            else { out[i] = 0f; out[i + 1] = 0f; out[i + 2] = 1f }
            i += 3
        }
        return out
    }

    companion object {
        fun cube(): LLVolume = LLVolume(VolumeParams.cube())
        fun sphere(): LLVolume = LLVolume(VolumeParams(
            profileCurve = ProfileCurve.CIRCLE,
            pathCurve = PathCurve.CIRCLE,
            beginS = 0f, endS = 1f,
            beginT = 0f, endT = 1f,
        ))
        fun cylinder(): LLVolume = LLVolume(VolumeParams(
            profileCurve = ProfileCurve.CIRCLE,
            pathCurve = PathCurve.LINE,
            beginS = 0f, endS = 1f,
            beginT = 0f, endT = 1f,
        ))
    }
}
