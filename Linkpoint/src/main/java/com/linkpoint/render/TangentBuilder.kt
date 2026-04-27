package com.linkpoint.render

import kotlin.math.abs
import kotlin.math.sqrt

/**
 * Per-vertex tangent-frame computation for Filament.
 *
 * Filament's `VertexAttribute.TANGENTS` is a FLOAT4 quaternion encoding the
 * full TBN (tangent / bitangent / normal) frame, NOT just a normal vector.
 * For proper normal mapping the vertex shader needs the tangent direction
 * aligned with UV.x increase, the bitangent with UV.y increase, and the
 * normal as the surface normal — all packed into one unit quaternion.
 *
 * We compute tangents using the classic Mikkelsen-style algorithm
 * (averaging per-triangle tangent contributions, weighted by surface area
 * implicitly via the signed UV cross), then orthonormalise against the
 * supplied normal and pack into a quaternion via shortest-arc rotation
 * from (0, 0, 1) to the actual normal frame.
 *
 * The previous code stuffed a 3-float normal vector into a slot Filament
 * actually treats as TANGENTS, which works for unlit / no-normal-map
 * materials by accident but breaks the moment a material samples a
 * normalMap. This utility produces a real packed TBN that:
 *   - lights correctly with no normal map (normal is recovered as-is)
 *   - lights correctly with a normal map (T and B are valid)
 */
object TangentBuilder {

    /**
     * Build a packed TANGENTS attribute (FLOAT4 quaternion per vertex)
     * from positions / normals / UVs / triangle list.
     *
     * @return float[vertexCount * 4] in (x, y, z, w) quaternion order.
     */
    fun build(
        positions: FloatArray,
        normals: FloatArray,
        uvs: FloatArray,
        indices: ShortArray
    ): FloatArray {
        val vertexCount = positions.size / 3
        // Accumulator buffers for tangent + handedness sign.
        val tan = FloatArray(vertexCount * 3)
        val bitan = FloatArray(vertexCount * 3)
        val out = FloatArray(vertexCount * 4)

        // Per-triangle: solve for tangent that maps (UV.x increase) and
        // bitangent that maps (UV.y increase) given the triangle's edge
        // vectors. Accumulate onto each triangle vertex.
        var i = 0
        val n = indices.size
        while (i + 2 < n) {
            val i0 = indices[i].toInt() and 0xFFFF
            val i1 = indices[i + 1].toInt() and 0xFFFF
            val i2 = indices[i + 2].toInt() and 0xFFFF
            i += 3
            if (i0 >= vertexCount || i1 >= vertexCount || i2 >= vertexCount) continue

            val p0x = positions[i0 * 3]; val p0y = positions[i0 * 3 + 1]; val p0z = positions[i0 * 3 + 2]
            val p1x = positions[i1 * 3]; val p1y = positions[i1 * 3 + 1]; val p1z = positions[i1 * 3 + 2]
            val p2x = positions[i2 * 3]; val p2y = positions[i2 * 3 + 1]; val p2z = positions[i2 * 3 + 2]

            val u0 = uvs[i0 * 2]; val v0 = uvs[i0 * 2 + 1]
            val u1 = uvs[i1 * 2]; val v1 = uvs[i1 * 2 + 1]
            val u2 = uvs[i2 * 2]; val v2 = uvs[i2 * 2 + 1]

            val e1x = p1x - p0x; val e1y = p1y - p0y; val e1z = p1z - p0z
            val e2x = p2x - p0x; val e2y = p2y - p0y; val e2z = p2z - p0z
            val du1 = u1 - u0; val dv1 = v1 - v0
            val du2 = u2 - u0; val dv2 = v2 - v0
            val det = du1 * dv2 - du2 * dv1
            if (abs(det) < 1e-8f) continue
            val invDet = 1f / det

            val tx = (e1x * dv2 - e2x * dv1) * invDet
            val ty = (e1y * dv2 - e2y * dv1) * invDet
            val tz = (e1z * dv2 - e2z * dv1) * invDet
            val bx = (e2x * du1 - e1x * du2) * invDet
            val by = (e2y * du1 - e1y * du2) * invDet
            val bz = (e2z * du1 - e1z * du2) * invDet

            // Accumulate onto each vertex of this triangle.
            for (vi in intArrayOf(i0, i1, i2)) {
                tan[vi * 3]     += tx
                tan[vi * 3 + 1] += ty
                tan[vi * 3 + 2] += tz
                bitan[vi * 3]     += bx
                bitan[vi * 3 + 1] += by
                bitan[vi * 3 + 2] += bz
            }
        }

        // Per-vertex orthonormalise + pack as quaternion.
        for (v in 0 until vertexCount) {
            val nx = normals[v * 3]
            val ny = normals[v * 3 + 1]
            val nz = normals[v * 3 + 2]
            var tx = tan[v * 3]
            var ty = tan[v * 3 + 1]
            var tz = tan[v * 3 + 2]
            // Gram-Schmidt: T = normalize(T - (T·N) * N)
            val dotTN = tx * nx + ty * ny + tz * nz
            tx -= dotTN * nx
            ty -= dotTN * ny
            tz -= dotTN * nz
            val tLen = sqrt(tx * tx + ty * ty + tz * tz)
            if (tLen < 1e-6f) {
                // Degenerate — pick an arbitrary tangent perpendicular to N.
                val (px, py, pz) = perpendicular(nx, ny, nz)
                tx = px; ty = py; tz = pz
            } else {
                tx /= tLen; ty /= tLen; tz /= tLen
            }
            // Bitangent handedness: sign of (N × T) · accumulated B.
            val nxt_x = ny * tz - nz * ty
            val nxt_y = nz * tx - nx * tz
            val nxt_z = nx * ty - ny * tx
            val bx = bitan[v * 3]
            val by = bitan[v * 3 + 1]
            val bz = bitan[v * 3 + 2]
            val sign = if (nxt_x * bx + nxt_y * by + nxt_z * bz < 0f) -1f else 1f

            packTBNQuaternion(tx, ty, tz, sign * nxt_x, sign * nxt_y, sign * nxt_z, nx, ny, nz, out, v * 4)
            // Encode handedness in the quaternion's w sign (Filament
            // convention: negative w = flipped bitangent).
            if (sign < 0f) {
                if (out[v * 4 + 3] > 0f) {
                    out[v * 4]     = -out[v * 4]
                    out[v * 4 + 1] = -out[v * 4 + 1]
                    out[v * 4 + 2] = -out[v * 4 + 2]
                    out[v * 4 + 3] = -out[v * 4 + 3]
                }
            }
        }
        return out
    }

    /** Pick any unit vector perpendicular to (nx, ny, nz). */
    private fun perpendicular(nx: Float, ny: Float, nz: Float): Triple<Float, Float, Float> {
        return if (abs(nx) < 0.9f) {
            // Cross with X axis.
            val x = 0f; val y = nz; val z = -ny
            val len = sqrt(y * y + z * z).coerceAtLeast(1e-6f)
            Triple(x, y / len, z / len)
        } else {
            // Cross with Y axis.
            val x = -nz; val y = 0f; val z = nx
            val len = sqrt(x * x + z * z).coerceAtLeast(1e-6f)
            Triple(x / len, y, z / len)
        }
    }

    /**
     * Pack an orthonormal TBN frame as a unit quaternion. Standard
     * "matrix → quaternion" with the matrix column basis (T, B, N).
     */
    private fun packTBNQuaternion(
        tx: Float, ty: Float, tz: Float,
        bx: Float, by: Float, bz: Float,
        nx: Float, ny: Float, nz: Float,
        out: FloatArray, offset: Int
    ) {
        val trace = tx + by + nz
        val qx: Float; val qy: Float; val qz: Float; val qw: Float
        if (trace > 0f) {
            val s = sqrt(trace + 1f) * 2f
            qw = 0.25f * s
            qx = (by - nz) / s   // bitangent.y - normal.z (=-)
            // Standard formula uses (m[2][1] - m[1][2]) etc. for column
            // basis (T, B, N): m[0]=T column, m[1]=B column, m[2]=N column.
            // We already have m[0][0]=tx, m[1][1]=by, m[2][2]=nz; the
            // off-diagonals are the components of the other axes.
            val m21 = ny; val m12 = bz
            val m02 = nx; val m20 = tz
            val m10 = bx; val m01 = ty
            val qx2 = (m21 - m12) / s
            val qy2 = (m02 - m20) / s
            val qz2 = (m10 - m01) / s
            out[offset]     = qx2
            out[offset + 1] = qy2
            out[offset + 2] = qz2
            out[offset + 3] = qw
            return
        }
        // Use the standard branch logic when the trace is non-positive.
        val m00 = tx; val m11 = by; val m22 = nz
        val m21 = ny; val m12 = bz
        val m02 = nx; val m20 = tz
        val m10 = bx; val m01 = ty
        if (m00 > m11 && m00 > m22) {
            val s = sqrt(1f + m00 - m11 - m22) * 2f
            out[offset]     = 0.25f * s
            out[offset + 1] = (m01 + m10) / s
            out[offset + 2] = (m02 + m20) / s
            out[offset + 3] = (m21 - m12) / s
        } else if (m11 > m22) {
            val s = sqrt(1f + m11 - m00 - m22) * 2f
            out[offset]     = (m01 + m10) / s
            out[offset + 1] = 0.25f * s
            out[offset + 2] = (m12 + m21) / s
            out[offset + 3] = (m02 - m20) / s
        } else {
            val s = sqrt(1f + m22 - m00 - m11) * 2f
            out[offset]     = (m02 + m20) / s
            out[offset + 1] = (m12 + m21) / s
            out[offset + 2] = 0.25f * s
            out[offset + 3] = (m10 - m01) / s
        }
    }
}
