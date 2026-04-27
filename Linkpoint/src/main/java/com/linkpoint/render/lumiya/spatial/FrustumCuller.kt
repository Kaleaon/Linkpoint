package com.linkpoint.render.lumiya.spatial

import android.opengl.Matrix

/**
 * View-frustum culling using six clip planes extracted from the VP matrix.
 *
 * Design lineage: Lumiya `FrustrumPlanes.java` / `FrustrumInfo.java`,
 * modernised with proper normalisation and AABB / sphere tests.
 */
class FrustumCuller {

    /** Six plane equations: (A, B, C, D) where Ax + By + Cz + D ≥ 0 is inside. */
    private val planes = Array(6) { FloatArray(4) }

    /**
     * Extract the six frustum planes from the combined VP matrix.
     * Call once per frame after camera update.
     */
    fun extractPlanes(projection: FloatArray, view: FloatArray) {
        val vp = FloatArray(16)
        Matrix.multiplyMM(vp, 0, projection, 0, view, 0)

        // Left
        planes[0][0] = vp[3] + vp[0]
        planes[0][1] = vp[7] + vp[4]
        planes[0][2] = vp[11] + vp[8]
        planes[0][3] = vp[15] + vp[12]

        // Right
        planes[1][0] = vp[3] - vp[0]
        planes[1][1] = vp[7] - vp[4]
        planes[1][2] = vp[11] - vp[8]
        planes[1][3] = vp[15] - vp[12]

        // Bottom
        planes[2][0] = vp[3] + vp[1]
        planes[2][1] = vp[7] + vp[5]
        planes[2][2] = vp[11] + vp[9]
        planes[2][3] = vp[15] + vp[13]

        // Top
        planes[3][0] = vp[3] - vp[1]
        planes[3][1] = vp[7] - vp[5]
        planes[3][2] = vp[11] - vp[9]
        planes[3][3] = vp[15] - vp[13]

        // Near
        planes[4][0] = vp[3] + vp[2]
        planes[4][1] = vp[7] + vp[6]
        planes[4][2] = vp[11] + vp[10]
        planes[4][3] = vp[15] + vp[14]

        // Far
        planes[5][0] = vp[3] - vp[2]
        planes[5][1] = vp[7] - vp[6]
        planes[5][2] = vp[11] - vp[10]
        planes[5][3] = vp[15] - vp[14]

        // Normalise
        for (p in planes) {
            val len = Math.sqrt((p[0] * p[0] + p[1] * p[1] + p[2] * p[2]).toDouble()).toFloat()
            if (len > 0f) { p[0] /= len; p[1] /= len; p[2] /= len; p[3] /= len }
        }
    }

    /**
     * Test whether an AABB is at least partially inside the frustum.
     *
     * @return `true` if visible (intersects or is inside the frustum).
     */
    fun isAABBVisible(
        minX: Float, minY: Float, minZ: Float,
        maxX: Float, maxY: Float, maxZ: Float
    ): Boolean {
        for (p in planes) {
            // Pick the corner most *aligned* with the plane normal
            val px = if (p[0] >= 0) maxX else minX
            val py = if (p[1] >= 0) maxY else minY
            val pz = if (p[2] >= 0) maxZ else minZ
            if (p[0] * px + p[1] * py + p[2] * pz + p[3] < 0f) {
                return false   // entirely outside this plane
            }
        }
        return true
    }

    /**
     * Test whether a sphere is at least partially inside the frustum.
     */
    fun isSphereVisible(cx: Float, cy: Float, cz: Float, radius: Float): Boolean {
        for (p in planes) {
            val dist = p[0] * cx + p[1] * cy + p[2] * cz + p[3]
            if (dist < -radius) return false
        }
        return true
    }

    /**
     * Test a single point.
     */
    fun isPointVisible(x: Float, y: Float, z: Float): Boolean {
        for (p in planes) {
            if (p[0] * x + p[1] * y + p[2] * z + p[3] < 0f) return false
        }
        return true
    }
}
