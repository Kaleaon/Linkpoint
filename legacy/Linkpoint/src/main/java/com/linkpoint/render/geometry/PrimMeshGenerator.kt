package com.linkpoint.render.geometry

import android.graphics.Bitmap
import com.linkpoint.protocol.messages.PrimShapeParams
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.*

/**
 * Standard primitive shape selection.
 */
enum class PrimShape {
    BOX, SPHERE, CYLINDER, TORUS, PRISM, RING, TUBE
}

/**
 * Shape cache key for parametric primitive mesh tessellation.
 */
data class PrimShapeKey(
    val kind: PrimShape,
    val params: PrimShapeParams
) {
    companion object {
        fun from(p: PrimShapeParams): PrimShapeKey {
            val kind = when (p.pathCurve) {
                PrimShapeParams.PATH_LINE -> when (p.profileType) {
                    PrimShapeParams.PROFILE_SQUARE -> PrimShape.BOX
                    PrimShapeParams.PROFILE_CIRCLE -> PrimShape.CYLINDER
                    PrimShapeParams.PROFILE_ISO_TRI,
                    PrimShapeParams.PROFILE_EQUAL_TRI,
                    PrimShapeParams.PROFILE_RIGHT_TRI -> PrimShape.PRISM
                    PrimShapeParams.PROFILE_HALF_CIRCLE -> PrimShape.CYLINDER
                    else -> PrimShape.BOX
                }
                PrimShapeParams.PATH_CIRCLE,
                PrimShapeParams.PATH_CIRCLE2 -> when (p.profileType) {
                    PrimShapeParams.PROFILE_HALF_CIRCLE -> PrimShape.SPHERE
                    PrimShapeParams.PROFILE_CIRCLE -> PrimShape.TORUS
                    PrimShapeParams.PROFILE_SQUARE -> PrimShape.TUBE
                    PrimShapeParams.PROFILE_ISO_TRI,
                    PrimShapeParams.PROFILE_EQUAL_TRI,
                    PrimShapeParams.PROFILE_RIGHT_TRI -> PrimShape.RING
                    else -> PrimShape.TORUS
                }
                else -> PrimShape.BOX
            }
            return PrimShapeKey(kind, p)
        }

        fun defaultFor(kind: PrimShape): PrimShapeKey =
            PrimShapeKey(kind, PrimShapeParams.DEFAULT)
    }
}

/**
 * Pure geometry data structure representing vertex attributes and index buffers
 * without dependencies on Filament or OpenGL ES.
 */
data class PrimMeshData(
    val positions: FloatArray, // 3 floats per vertex (x, y, z)
    val normals: FloatArray,   // 3 floats per vertex (nx, ny, nz)
    val uvs: FloatArray,       // 2 floats per vertex (u, v)
    val indices: ShortArray,   // Triangle indices
    val faceRanges: List<IntRange> = emptyList()
) {
    val vertexCount: Int get() = positions.size / 3
    val indexCount: Int get() = indices.size

    /**
     * Produce an interleaved FloatArray of POS(3) + NORMAL(3) + UV(2) = 8 floats per vertex.
     * Stride is 32 bytes (8 floats).
     */
    fun toInterleavedBuffer(): FloatArray {
        val count = vertexCount
        val interleaved = FloatArray(count * 8)
        for (i in 0 until count) {
            interleaved[i * 8 + 0] = positions[i * 3 + 0]
            interleaved[i * 8 + 1] = positions[i * 3 + 1]
            interleaved[i * 8 + 2] = positions[i * 3 + 2]
            interleaved[i * 8 + 3] = normals[i * 3 + 0]
            interleaved[i * 8 + 4] = normals[i * 3 + 1]
            interleaved[i * 8 + 5] = normals[i * 3 + 2]
            interleaved[i * 8 + 6] = uvs[i * 2 + 0]
            interleaved[i * 8 + 7] = uvs[i * 2 + 1]
        }
        return interleaved
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PrimMeshData) return false
        return positions.contentEquals(other.positions) &&
               normals.contentEquals(other.normals) &&
               uvs.contentEquals(other.uvs) &&
               indices.contentEquals(other.indices) &&
               faceRanges == other.faceRanges
    }

    override fun hashCode(): Int {
        var result = positions.contentHashCode()
        result = 31 * result + normals.contentHashCode()
        result = 31 * result + uvs.contentHashCode()
        result = 31 * result + indices.contentHashCode()
        result = 31 * result + faceRanges.hashCode()
        return result
    }

    companion object {
        /**
         * Construct PrimMeshData from interleaved FloatArray (8 floats per vertex: POS3 + NORMAL3 + UV2).
         */
        fun fromInterleaved(
            interleaved: FloatArray,
            indices: ShortArray,
            faceRanges: List<IntRange> = emptyList()
        ): PrimMeshData {
            val count = interleaved.size / 8
            val positions = FloatArray(count * 3)
            val normals = FloatArray(count * 3)
            val uvs = FloatArray(count * 2)

            for (i in 0 until count) {
                positions[i * 3 + 0] = interleaved[i * 8 + 0]
                positions[i * 3 + 1] = interleaved[i * 8 + 1]
                positions[i * 3 + 2] = interleaved[i * 8 + 2]
                normals[i * 3 + 0] = interleaved[i * 8 + 3]
                normals[i * 3 + 1] = interleaved[i * 8 + 4]
                normals[i * 3 + 2] = interleaved[i * 8 + 5]
                uvs[i * 2 + 0] = interleaved[i * 8 + 6]
                uvs[i * 2 + 1] = interleaved[i * 8 + 7]
            }

            return PrimMeshData(positions, normals, uvs, indices, faceRanges)
        }
    }
}

/**
 * Shared primitive shape tessellator and sculpt mesh generator.
 */
object PrimMeshGenerator {

    /**
     * Generate mesh data for the given shape parameters.
     */
    fun generateMesh(shapeParams: PrimShapeParams): PrimMeshData {
        val key = PrimShapeKey.from(shapeParams)
        return generateMesh(key)
    }

    /**
     * Generate mesh data for the given shape key.
     */
    fun generateMesh(key: PrimShapeKey): PrimMeshData {
        return when (key.kind) {
            PrimShape.BOX -> if (needsExtrudeGenerator(key)) {
                generateExtrudedProfile(key)
            } else generateBoxMesh(key)
            PrimShape.CYLINDER -> if (needsExtrudeGenerator(key)) {
                generateExtrudedProfile(key)
            } else generateCylinderMesh(key)
            PrimShape.PRISM -> if (needsExtrudeGenerator(key)) {
                generateExtrudedProfile(key)
            } else generatePrismMesh(key)
            PrimShape.SPHERE -> generateSphereMesh(key)
            PrimShape.TORUS -> generateTorusMesh(key)
            PrimShape.RING -> generateRingMesh(key)
            PrimShape.TUBE -> generateTubeMesh(key)
        }
    }

    private fun needsExtrudeGenerator(key: PrimShapeKey): Boolean {
        val p = key.params
        return p.profileBegin > 0.0001f || p.profileEnd < 0.9999f ||
               p.pathBegin > 0.0001f || p.pathEnd < 0.9999f ||
               p.profileHollow > 0.0001f ||
               abs(p.pathTaperX) > 0.0001f ||
               abs(p.pathTaperY) > 0.0001f ||
               abs(p.pathTwist) > 0.0001f ||
               abs(p.pathTwistBegin) > 0.0001f
    }

    /**
     * Generic path-line profile extrusion with cuts, hollow, and taper.
     */
    private fun generateExtrudedProfile(key: PrimShapeKey): PrimMeshData {
        val p = key.params
        val profile = buildProfile2D(key.kind, p)
        if (profile.size < 6) return generateBoxMesh(key)

        val zBottom = -0.5f + p.pathBegin
        val zTop = -0.5f + p.pathEnd

        val taperX = (1f - p.pathTaperX).coerceAtLeast(0.001f)
        val taperY = (1f - p.pathTaperY).coerceAtLeast(0.001f)

        val twistBeginRad = p.pathTwistBegin * 2f * PI.toFloat()
        val twistEndRad = p.pathTwist * 2f * PI.toFloat()
        val twistDelta = abs(twistEndRad - twistBeginRad)

        val pathRings = when {
            twistDelta < 0.05f -> 2
            else -> (4 + (twistDelta / (PI.toFloat() / 6f)).toInt()).coerceAtMost(24)
        }

        val n = profile.size / 2
        val verts = mutableListOf<Float>()
        val idx = mutableListOf<Short>()
        val ringStart = IntArray(pathRings)

        fun pushVertex(x: Float, y: Float, z: Float, nx: Float, ny: Float, nz: Float, u: Float, v: Float) {
            verts.addAll(listOf(x, y, z, nx, ny, nz, u, v))
        }

        // Outer rings.
        for (r in 0 until pathRings) {
            val t = if (pathRings == 1) 0f else r.toFloat() / (pathRings - 1)
            val z = zBottom + (zTop - zBottom) * t
            val sx = 1f + (taperX - 1f) * t
            val sy = 1f + (taperY - 1f) * t
            val twist = twistBeginRad + (twistEndRad - twistBeginRad) * t
            val ct = cos(twist); val st = sin(twist)
            ringStart[r] = verts.size / 8
            for (i in 0 until n) {
                val rx0 = profile[i * 2] * sx
                val ry0 = profile[i * 2 + 1] * sy
                val px = rx0 * ct - ry0 * st
                val py = rx0 * st + ry0 * ct
                val nl = sqrt(px * px + py * py).coerceAtLeast(1e-4f)
                pushVertex(px, py, z, px / nl, py / nl, 0f, i.toFloat() / n, t)
            }
        }

        val closesProfile = p.profileBegin <= 0.0001f && p.profileEnd >= 0.9999f
        val wallSegments = if (closesProfile) n else n - 1
        for (r in 0 until pathRings - 1) {
            val a = ringStart[r]
            val b = ringStart[r + 1]
            for (i in 0 until wallSegments) {
                val i0 = (a + i).toShort()
                val i1 = (a + (i + 1) % n).toShort()
                val i2 = (b + i).toShort()
                val i3 = (b + (i + 1) % n).toShort()
                idx.addAll(listOf(i0, i2, i1, i1, i2, i3))
            }
        }

        // Hollow path: emit inner rings, walls, and ring caps
        val hollow = p.profileHollow.coerceIn(0f, 0.95f)
        if (hollow > 0.001f) {
            val innerProfile = scaleProfile(profile, 1f - hollow)
            val innerRingStart = IntArray(pathRings)
            for (r in 0 until pathRings) {
                val t = if (pathRings == 1) 0f else r.toFloat() / (pathRings - 1)
                val z = zBottom + (zTop - zBottom) * t
                val sx = 1f + (taperX - 1f) * t
                val sy = 1f + (taperY - 1f) * t
                val twist = twistBeginRad + (twistEndRad - twistBeginRad) * t
                val ct = cos(twist); val st = sin(twist)
                innerRingStart[r] = verts.size / 8
                for (i in 0 until n) {
                    val rx0 = innerProfile[i * 2] * sx
                    val ry0 = innerProfile[i * 2 + 1] * sy
                    val px = rx0 * ct - ry0 * st
                    val py = rx0 * st + ry0 * ct
                    val nl = sqrt(px * px + py * py).coerceAtLeast(1e-4f)
                    pushVertex(px, py, z, -px / nl, -py / nl, 0f, i.toFloat() / n, t)
                }
            }
            for (r in 0 until pathRings - 1) {
                val a = innerRingStart[r]
                val b = innerRingStart[r + 1]
                for (i in 0 until wallSegments) {
                    val i0 = (a + i).toShort()
                    val i1 = (a + (i + 1) % n).toShort()
                    val i2 = (b + i).toShort()
                    val i3 = (b + (i + 1) % n).toShort()
                    idx.addAll(listOf(i0, i1, i2, i1, i3, i2))
                }
            }
            // Bottom ring cap
            run {
                val o = ringStart[0]; val n0 = innerRingStart[0]
                for (i in 0 until wallSegments) {
                    val o0 = (o + i).toShort()
                    val o1 = (o + (i + 1) % n).toShort()
                    val ni0 = (n0 + i).toShort()
                    val ni1 = (n0 + (i + 1) % n).toShort()
                    idx.addAll(listOf(o0, ni0, o1, o1, ni0, ni1))
                }
            }
            // Top ring cap
            run {
                val o = ringStart[pathRings - 1]; val n0 = innerRingStart[pathRings - 1]
                for (i in 0 until wallSegments) {
                    val o0 = (o + i).toShort()
                    val o1 = (o + (i + 1) % n).toShort()
                    val ni0 = (n0 + i).toShort()
                    val ni1 = (n0 + (i + 1) % n).toShort()
                    idx.addAll(listOf(o0, o1, ni0, o1, ni1, ni0))
                }
            }
        } else {
            // Solid caps
            val centerBottom = (verts.size / 8).toShort()
            pushVertex(0f, 0f, zBottom, 0f, 0f, -1f, 0.5f, 0.5f)
            val centerTop = (verts.size / 8).toShort()
            pushVertex(0f, 0f, zTop, 0f, 0f, 1f, 0.5f, 0.5f)
            val a = ringStart[0]
            val b = ringStart[pathRings - 1]
            for (i in 0 until wallSegments) {
                val a0 = (a + i).toShort()
                val a1 = (a + (i + 1) % n).toShort()
                idx.addAll(listOf(centerBottom, a1, a0))
                val b0 = (b + i).toShort()
                val b1 = (b + (i + 1) % n).toShort()
                idx.addAll(listOf(centerTop, b0, b1))
            }
        }

        return PrimMeshData.fromInterleaved(verts.toFloatArray(), idx.toShortArray())
    }

    private fun buildProfile2D(kind: PrimShape, p: PrimShapeParams): FloatArray {
        val begin = p.profileBegin.coerceIn(0f, 0.99f)
        val end = p.profileEnd.coerceIn(begin + 0.01f, 1f)
        return when (kind) {
            PrimShape.CYLINDER -> circularArc(begin, end, segments = 24, radius = 0.5f)
            PrimShape.PRISM -> polygonArc(begin, end, sides = 3)
            PrimShape.BOX -> polygonArc(begin, end, sides = 4)
            else -> polygonArc(begin, end, sides = 4)
        }
    }

    private fun circularArc(begin: Float, end: Float, segments: Int, radius: Float): FloatArray {
        val n = (segments * (end - begin)).toInt().coerceAtLeast(3)
        val out = FloatArray(n * 2)
        for (i in 0 until n) {
            val t = begin + (end - begin) * (i.toFloat() / (n - 1))
            val theta = t * 2.0 * PI
            out[i * 2] = (cos(theta) * radius).toFloat()
            out[i * 2 + 1] = (sin(theta) * radius).toFloat()
        }
        return out
    }

    private fun polygonArc(begin: Float, end: Float, sides: Int): FloatArray {
        val samples = (sides * 8 * (end - begin)).toInt().coerceAtLeast(sides + 1)
        val out = FloatArray(samples * 2)
        for (i in 0 until samples) {
            val t = begin + (end - begin) * (i.toFloat() / (samples - 1))
            val st = (t * sides) % 1f
            val sideIdx = (t * sides).toInt() % sides
            val a0 = sideIdx * (2.0 * PI / sides) - PI / 4
            val a1 = (sideIdx + 1) * (2.0 * PI / sides) - PI / 4
            val x0 = cos(a0).toFloat() * 0.5f * sqrt(2f)
            val y0 = sin(a0).toFloat() * 0.5f * sqrt(2f)
            val x1 = cos(a1).toFloat() * 0.5f * sqrt(2f)
            val y1 = sin(a1).toFloat() * 0.5f * sqrt(2f)
            out[i * 2] = x0 + (x1 - x0) * st
            out[i * 2 + 1] = y0 + (y1 - y0) * st
        }
        return out
    }

    private fun scaleProfile(profile: FloatArray, factor: Float): FloatArray {
        val out = FloatArray(profile.size)
        for (i in profile.indices) out[i] = profile[i] * factor
        return out
    }

    private fun generateBoxMesh(key: PrimShapeKey): PrimMeshData {
        val h = 0.5f
        val vertices = floatArrayOf(
            // Front face (+Z)
            -h, -h,  h,  0f,  0f,  1f,  0f, 0f,
             h, -h,  h,  0f,  0f,  1f,  1f, 0f,
             h,  h,  h,  0f,  0f,  1f,  1f, 1f,
            -h,  h,  h,  0f,  0f,  1f,  0f, 1f,
            // Back face (-Z)
            -h, -h, -h,  0f,  0f, -1f,  1f, 0f,
            -h,  h, -h,  0f,  0f, -1f,  1f, 1f,
             h,  h, -h,  0f,  0f, -1f,  0f, 1f,
             h, -h, -h,  0f,  0f, -1f,  0f, 0f,
            // Top face (+Y)
            -h,  h, -h,  0f,  1f,  0f,  0f, 0f,
            -h,  h,  h,  0f,  1f,  0f,  0f, 1f,
             h,  h,  h,  0f,  1f,  0f,  1f, 1f,
             h,  h, -h,  0f,  1f,  0f,  1f, 0f,
            // Bottom face (-Y)
            -h, -h, -h,  0f, -1f,  0f,  0f, 1f,
             h, -h, -h,  0f, -1f,  0f,  1f, 1f,
             h, -h,  h,  0f, -1f,  0f,  1f, 0f,
            -h, -h,  h,  0f, -1f,  0f,  0f, 0f,
            // Right face (+X)
             h, -h, -h,  1f,  0f,  0f,  0f, 0f,
             h,  h, -h,  1f,  0f,  0f,  0f, 1f,
             h,  h,  h,  1f,  0f,  0f,  1f, 1f,
             h, -h,  h,  1f,  0f,  0f,  1f, 0f,
            // Left face (-X)
            -h, -h, -h, -1f,  0f,  0f,  1f, 0f,
            -h, -h,  h, -1f,  0f,  0f,  0f, 0f,
            -h,  h,  h, -1f,  0f,  0f,  0f, 1f,
            -h,  h, -h, -1f,  0f,  0f,  1f, 1f
        )

        val indices = shortArrayOf(
            0, 1, 2, 0, 2, 3,       // front
            4, 5, 6, 4, 6, 7,       // back
            8, 9, 10, 8, 10, 11,    // top
            12, 13, 14, 12, 14, 15, // bottom
            16, 17, 18, 16, 18, 19, // right
            20, 21, 22, 20, 22, 23  // left
        )

        return PrimMeshData.fromInterleaved(vertices, indices)
    }

    private fun generateSphereMesh(key: PrimShapeKey): PrimMeshData {
        val segments = 24
        val rings = 16

        val vertexCount = (rings + 1) * (segments + 1)
        val vertices = FloatArray(vertexCount * 8)
        var vIdx = 0

        for (y in 0..rings) {
            val phi = PI * y / rings

            for (x in 0..segments) {
                val theta = 2 * PI * x / segments

                val px = (sin(phi) * cos(theta) * 0.5).toFloat()
                val py = (cos(phi) * 0.5).toFloat()
                val pz = (sin(phi) * sin(theta) * 0.5).toFloat()

                val nx = (sin(phi) * cos(theta)).toFloat()
                val ny = cos(phi).toFloat()
                val nz = (sin(phi) * sin(theta)).toFloat()

                val u = x.toFloat() / segments
                val v = y.toFloat() / rings

                vertices[vIdx++] = px
                vertices[vIdx++] = py
                vertices[vIdx++] = pz
                vertices[vIdx++] = nx
                vertices[vIdx++] = ny
                vertices[vIdx++] = nz
                vertices[vIdx++] = u
                vertices[vIdx++] = v
            }
        }

        val indexCount = rings * segments * 6
        val indices = ShortArray(indexCount)
        var iIdx = 0

        for (y in 0 until rings) {
            for (x in 0 until segments) {
                val i0 = (y * (segments + 1) + x).toShort()
                val i1 = (i0 + 1).toShort()
                val i2 = (i0 + segments + 1).toShort()
                val i3 = (i2 + 1).toShort()

                indices[iIdx++] = i0
                indices[iIdx++] = i2
                indices[iIdx++] = i1

                indices[iIdx++] = i1
                indices[iIdx++] = i2
                indices[iIdx++] = i3
            }
        }

        return PrimMeshData.fromInterleaved(vertices, indices)
    }

    private fun generateCylinderMesh(key: PrimShapeKey): PrimMeshData {
        val segments = 24
        val vertexCount = 4 * (segments + 1) + 2
        val vertices = FloatArray(vertexCount * 8)
        var vIdx = 0

        val indexCount = segments * 12
        val indices = ShortArray(indexCount)
        var iIdx = 0

        for (i in 0..segments) {
            val theta = 2 * PI * i / segments
            val x = (cos(theta) * 0.5).toFloat()
            val z = (sin(theta) * 0.5).toFloat()
            val nx = cos(theta).toFloat()
            val nz = sin(theta).toFloat()
            val u = i.toFloat() / segments

            vertices[vIdx++] = x; vertices[vIdx++] = -0.5f; vertices[vIdx++] = z
            vertices[vIdx++] = nx; vertices[vIdx++] = 0f; vertices[vIdx++] = nz
            vertices[vIdx++] = u; vertices[vIdx++] = 0f

            vertices[vIdx++] = x; vertices[vIdx++] = 0.5f; vertices[vIdx++] = z
            vertices[vIdx++] = nx; vertices[vIdx++] = 0f; vertices[vIdx++] = nz
            vertices[vIdx++] = u; vertices[vIdx++] = 1f
        }

        for (i in 0 until segments) {
            val b0 = (i * 2).toShort()
            val t0 = (i * 2 + 1).toShort()
            val b1 = (i * 2 + 2).toShort()
            val t1 = (i * 2 + 3).toShort()

            indices[iIdx++] = b0; indices[iIdx++] = b1; indices[iIdx++] = t0
            indices[iIdx++] = t0; indices[iIdx++] = b1; indices[iIdx++] = t1
        }

        val topCenter = (vIdx / 8).toShort()
        vertices[vIdx++] = 0f; vertices[vIdx++] = 0.5f; vertices[vIdx++] = 0f
        vertices[vIdx++] = 0f; vertices[vIdx++] = 1f; vertices[vIdx++] = 0f
        vertices[vIdx++] = 0.5f; vertices[vIdx++] = 0.5f

        val topStart = (vIdx / 8).toShort()
        for (i in 0..segments) {
            val theta = 2 * PI * i / segments
            val x = (cos(theta) * 0.5).toFloat()
            val z = (sin(theta) * 0.5).toFloat()

            vertices[vIdx++] = x; vertices[vIdx++] = 0.5f; vertices[vIdx++] = z
            vertices[vIdx++] = 0f; vertices[vIdx++] = 1f; vertices[vIdx++] = 0f
            vertices[vIdx++] = (cos(theta) * 0.5 + 0.5).toFloat()
            vertices[vIdx++] = (sin(theta) * 0.5 + 0.5).toFloat()
        }

        for (i in 0 until segments) {
            indices[iIdx++] = topCenter
            indices[iIdx++] = (topStart + i).toShort()
            indices[iIdx++] = (topStart + i + 1).toShort()
        }

        val bottomCenter = (vIdx / 8).toShort()
        vertices[vIdx++] = 0f; vertices[vIdx++] = -0.5f; vertices[vIdx++] = 0f
        vertices[vIdx++] = 0f; vertices[vIdx++] = -1f; vertices[vIdx++] = 0f
        vertices[vIdx++] = 0.5f; vertices[vIdx++] = 0.5f

        val bottomStart = (vIdx / 8).toShort()
        for (i in 0..segments) {
            val theta = 2 * PI * i / segments
            val x = (cos(theta) * 0.5).toFloat()
            val z = (sin(theta) * 0.5).toFloat()

            vertices[vIdx++] = x; vertices[vIdx++] = -0.5f; vertices[vIdx++] = z
            vertices[vIdx++] = 0f; vertices[vIdx++] = -1f; vertices[vIdx++] = 0f
            vertices[vIdx++] = (cos(theta) * 0.5 + 0.5).toFloat()
            vertices[vIdx++] = (sin(theta) * 0.5 + 0.5).toFloat()
        }

        for (i in 0 until segments) {
            indices[iIdx++] = bottomCenter
            indices[iIdx++] = (bottomStart + i + 1).toShort()
            indices[iIdx++] = (bottomStart + i).toShort()
        }

        return PrimMeshData.fromInterleaved(vertices, indices)
    }

    private fun generateTorusMesh(key: PrimShapeKey): PrimMeshData {
        val majorSegments = 24
        val minorSegments = 12
        val majorRadius = 0.35f
        val minorRadius = 0.15f

        val vertexCount = (majorSegments + 1) * (minorSegments + 1)
        val vertices = FloatArray(vertexCount * 8)
        var vIdx = 0

        val indexCount = majorSegments * minorSegments * 6
        val indices = ShortArray(indexCount)
        var iIdx = 0

        for (i in 0..majorSegments) {
            val u = 2 * PI * i / majorSegments

            for (j in 0..minorSegments) {
                val v = 2 * PI * j / minorSegments

                val x = ((majorRadius + minorRadius * cos(v)) * cos(u)).toFloat()
                val y = (minorRadius * sin(v)).toFloat()
                val z = ((majorRadius + minorRadius * cos(v)) * sin(u)).toFloat()

                val nx = (cos(v) * cos(u)).toFloat()
                val ny = sin(v).toFloat()
                val nz = (cos(v) * sin(u)).toFloat()

                vertices[vIdx++] = x; vertices[vIdx++] = y; vertices[vIdx++] = z
                vertices[vIdx++] = nx; vertices[vIdx++] = ny; vertices[vIdx++] = nz
                vertices[vIdx++] = i.toFloat() / majorSegments
                vertices[vIdx++] = j.toFloat() / minorSegments
            }
        }

        for (i in 0 until majorSegments) {
            for (j in 0 until minorSegments) {
                val i0 = (i * (minorSegments + 1) + j).toShort()
                val i1 = (i0 + 1).toShort()
                val i2 = (i0 + minorSegments + 1).toShort()
                val i3 = (i2 + 1).toShort()

                indices[iIdx++] = i0; indices[iIdx++] = i2; indices[iIdx++] = i1
                indices[iIdx++] = i1; indices[iIdx++] = i2; indices[iIdx++] = i3
            }
        }

        return PrimMeshData.fromInterleaved(vertices, indices)
    }

    private fun generatePrismMesh(key: PrimShapeKey): PrimMeshData {
        val vertices = floatArrayOf(
            // Front triangle
            0f, 0.5f, 0.5f, 0f, 0f, 1f, 0.5f, 1f,
            -0.5f, -0.5f, 0.5f, 0f, 0f, 1f, 0f, 0f,
            0.5f, -0.5f, 0.5f, 0f, 0f, 1f, 1f, 0f,
            // Back triangle
            0f, 0.5f, -0.5f, 0f, 0f, -1f, 0.5f, 1f,
            0.5f, -0.5f, -0.5f, 0f, 0f, -1f, 1f, 0f,
            -0.5f, -0.5f, -0.5f, 0f, 0f, -1f, 0f, 0f,
            // Bottom
            -0.5f, -0.5f, 0.5f, 0f, -1f, 0f, 0f, 0f,
            -0.5f, -0.5f, -0.5f, 0f, -1f, 0f, 0f, 1f,
            0.5f, -0.5f, -0.5f, 0f, -1f, 0f, 1f, 1f,
            0.5f, -0.5f, 0.5f, 0f, -1f, 0f, 1f, 0f,
            // Left side
            0f, 0.5f, 0.5f, -0.894f, 0.447f, 0f, 1f, 0f,
            -0.5f, -0.5f, 0.5f, -0.894f, 0.447f, 0f, 0f, 0f,
            -0.5f, -0.5f, -0.5f, -0.894f, 0.447f, 0f, 0f, 1f,
            0f, 0.5f, -0.5f, -0.894f, 0.447f, 0f, 1f, 1f,
            // Right side
            0f, 0.5f, -0.5f, 0.894f, 0.447f, 0f, 0f, 1f,
            0.5f, -0.5f, -0.5f, 0.894f, 0.447f, 0f, 1f, 1f,
            0.5f, -0.5f, 0.5f, 0.894f, 0.447f, 0f, 1f, 0f,
            0f, 0.5f, 0.5f, 0.894f, 0.447f, 0f, 0f, 0f
        )

        val indices = shortArrayOf(
            0, 1, 2,       // front
            3, 4, 5,       // back
            6, 7, 8, 6, 8, 9,  // bottom
            10, 11, 12, 10, 12, 13, // left
            14, 15, 16, 14, 16, 17  // right
        )

        return PrimMeshData.fromInterleaved(vertices, indices)
    }

    private fun generateRingMesh(key: PrimShapeKey): PrimMeshData {
        val majorSegments = 32
        val minorSegments = 12
        val majorRadius = 0.45f
        val minorRadius = 0.08f

        val vertexCount = (majorSegments + 1) * (minorSegments + 1)
        val vertices = FloatArray(vertexCount * 8)
        var vIdx = 0

        for (i in 0..majorSegments) {
            val u = 2.0 * PI * i / majorSegments
            val cosU = cos(u).toFloat()
            val sinU = sin(u).toFloat()

            for (j in 0..minorSegments) {
                val v = 2.0 * PI * j / minorSegments
                val cosV = cos(v).toFloat()
                val sinV = sin(v).toFloat()

                val radial = majorRadius + minorRadius * cosV
                val px = radial * cosU
                val py = radial * sinU
                val pz = minorRadius * sinV

                val nx = cosV * cosU
                val ny = cosV * sinU
                val nz = sinV

                vertices[vIdx++] = px; vertices[vIdx++] = py; vertices[vIdx++] = pz
                vertices[vIdx++] = nx; vertices[vIdx++] = ny; vertices[vIdx++] = nz
                vertices[vIdx++] = i.toFloat() / majorSegments
                vertices[vIdx++] = j.toFloat() / minorSegments
            }
        }

        val indices = ShortArray(majorSegments * minorSegments * 6)
        var iIdx = 0
        val stride = minorSegments + 1

        for (i in 0 until majorSegments) {
            for (j in 0 until minorSegments) {
                val i0 = (i * stride + j).toShort()
                val i1 = (i0 + 1).toShort()
                val i2 = ((i + 1) * stride + j).toShort()
                val i3 = (i2 + 1).toShort()

                indices[iIdx++] = i0; indices[iIdx++] = i2; indices[iIdx++] = i1
                indices[iIdx++] = i1; indices[iIdx++] = i2; indices[iIdx++] = i3
            }
        }

        return PrimMeshData.fromInterleaved(vertices, indices)
    }

    private fun generateTubeMesh(key: PrimShapeKey): PrimMeshData {
        val majorSegments = 24
        val minorSegments = 4
        val majorRadius = 0.35f
        val minorHalf = 0.15f

        val vertexCount = (majorSegments + 1) * (minorSegments + 1)
        val vertices = FloatArray(vertexCount * 8)
        var vIdx = 0

        for (i in 0..majorSegments) {
            val u = 2 * PI * i / majorSegments
            val cu = cos(u).toFloat()
            val su = sin(u).toFloat()

            val corners = floatArrayOf(
                 minorHalf,  minorHalf,  1f,  0f,
                -minorHalf,  minorHalf,  0f,  1f,
                -minorHalf, -minorHalf, -1f,  0f,
                 minorHalf, -minorHalf,  0f, -1f
            )
            for (j in 0..minorSegments) {
                val k = (j % minorSegments) * 4
                val rx = corners[k]
                val ry = corners[k + 1]
                val nrx = corners[k + 2]
                val nry = corners[k + 3]

                val px = (majorRadius + rx) * cu
                val py = ry
                val pz = (majorRadius + rx) * su

                val nx = nrx * cu
                val ny = nry
                val nz = nrx * su

                vertices[vIdx++] = px; vertices[vIdx++] = py; vertices[vIdx++] = pz
                vertices[vIdx++] = nx; vertices[vIdx++] = ny; vertices[vIdx++] = nz
                vertices[vIdx++] = i.toFloat() / majorSegments
                vertices[vIdx++] = j.toFloat() / minorSegments
            }
        }

        val indexCount = majorSegments * minorSegments * 6
        val indices = ShortArray(indexCount)
        var iIdx = 0
        for (i in 0 until majorSegments) {
            for (j in 0 until minorSegments) {
                val i0 = (i * (minorSegments + 1) + j).toShort()
                val i1 = (i0 + 1).toShort()
                val i2 = (i0 + minorSegments + 1).toShort()
                val i3 = (i2 + 1).toShort()

                indices[iIdx++] = i0; indices[iIdx++] = i2; indices[iIdx++] = i1
                indices[iIdx++] = i1; indices[iIdx++] = i2; indices[iIdx++] = i3
            }
        }
        return PrimMeshData.fromInterleaved(vertices, indices)
    }

    // =========================================================================
    // Sculpt Mesh Generator
    // =========================================================================

    /**
     * Generate sculpt mesh from a Bitmap sculpt map texture and sculptType.
     * sculptType: 1=Sphere, 2=Torus, 3=Plane, 4=Cylinder.
     */
    fun generateSculptMesh(bitmap: Bitmap, sculptType: Int): PrimMeshData {
        val width = bitmap.width
        val height = bitmap.height
        val pixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
        return generateSculptMesh(width, height, pixels, sculptType)
    }

    /**
     * Generate sculpt mesh from raw RGB pixel data.
     * rgbPixels contain ARGB or RGB color values where Red=X, Green=Y, Blue=Z.
     */
    fun generateSculptMesh(
        width: Int,
        height: Int,
        rgbPixels: IntArray,
        sculptType: Int
    ): PrimMeshData {
        if (width <= 1 || height <= 1 || rgbPixels.isEmpty()) {
            return generateMesh(PrimShapeParams.DEFAULT)
        }

        val topology = sculptType and 0x07 // low 3 bits = sculpt topology (1=Sphere, 2=Torus, 3=Plane, 4=Cylinder)
        val numVerts = width * height
        val positions = FloatArray(numVerts * 3)
        val normals = FloatArray(numVerts * 3)
        val uvs = FloatArray(numVerts * 2)

        // Extract positions and UVs
        for (y in 0 until height) {
            val v = y.toFloat() / (height - 1).coerceAtLeast(1)
            for (x in 0 until width) {
                val idx = y * width + x
                val pixel = rgbPixels[idx]

                val r = (pixel shr 16) and 0xFF
                val g = (pixel shr 8) and 0xFF
                val b = pixel and 0xFF

                // Map [0..255] -> [-0.5..0.5]
                val px = (r / 255f) - 0.5f
                val py = (g / 255f) - 0.5f
                val pz = (b / 255f) - 0.5f

                positions[idx * 3 + 0] = px
                positions[idx * 3 + 1] = py
                positions[idx * 3 + 2] = pz

                val u = x.toFloat() / (width - 1).coerceAtLeast(1)
                uvs[idx * 2 + 0] = u
                uvs[idx * 2 + 1] = v
            }
        }

        // Generate indices
        val numQuads = (width - 1) * (height - 1)
        val indices = ShortArray(numQuads * 6)
        var iIdx = 0

        for (y in 0 until height - 1) {
            for (x in 0 until width - 1) {
                val i0 = (y * width + x).toShort()
                val i1 = (y * width + (x + 1)).toShort()
                val i2 = ((y + 1) * width + x).toShort()
                val i3 = ((y + 1) * width + (x + 1)).toShort()

                indices[iIdx++] = i0; indices[iIdx++] = i2; indices[iIdx++] = i1
                indices[iIdx++] = i1; indices[iIdx++] = i2; indices[iIdx++] = i3
            }
        }

        // Compute smooth vertex normals
        val vertNormAccum = FloatArray(numVerts * 3)
        for (i in 0 until indices.size step 3) {
            val idx0 = indices[i].toInt() and 0xFFFF
            val idx1 = indices[i + 1].toInt() and 0xFFFF
            val idx2 = indices[i + 2].toInt() and 0xFFFF

            val ax = positions[idx0 * 3 + 0]; val ay = positions[idx0 * 3 + 1]; val az = positions[idx0 * 3 + 2]
            val bx = positions[idx1 * 3 + 0]; val by = positions[idx1 * 3 + 1]; val bz = positions[idx1 * 3 + 2]
            val cx = positions[idx2 * 3 + 0]; val cy = positions[idx2 * 3 + 1]; val cz = positions[idx2 * 3 + 2]

            val ux = bx - ax; val uy = by - ay; val uz = bz - az
            val vx = cx - ax; val vy = cy - ay; val vz = cz - az

            var nx = uy * vz - uz * vy
            var ny = uz * vx - ux * vz
            var nz = ux * vy - uy * vx

            val len = sqrt(nx * nx + ny * ny + nz * nz)
            if (len > 1e-5f) {
                nx /= len; ny /= len; nz /= len
                vertNormAccum[idx0 * 3 + 0] += nx; vertNormAccum[idx0 * 3 + 1] += ny; vertNormAccum[idx0 * 3 + 2] += nz
                vertNormAccum[idx1 * 3 + 0] += nx; vertNormAccum[idx1 * 3 + 1] += ny; vertNormAccum[idx1 * 3 + 2] += nz
                vertNormAccum[idx2 * 3 + 0] += nx; vertNormAccum[idx2 * 3 + 1] += ny; vertNormAccum[idx2 * 3 + 2] += nz
            }
        }

        for (v in 0 until numVerts) {
            val nx = vertNormAccum[v * 3 + 0]
            val ny = vertNormAccum[v * 3 + 1]
            val nz = vertNormAccum[v * 3 + 2]
            val len = sqrt(nx * nx + ny * ny + nz * nz)
            if (len > 1e-5f) {
                normals[v * 3 + 0] = nx / len
                normals[v * 3 + 1] = ny / len
                normals[v * 3 + 2] = nz / len
            } else {
                normals[v * 3 + 0] = 0f
                normals[v * 3 + 1] = 1f
                normals[v * 3 + 2] = 0f
            }
        }

        return PrimMeshData(positions, normals, uvs, indices)
    }
}
