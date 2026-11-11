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
     * Tri-state classification of an AABB against the frustum.
     *
     * Lineage: LL viewer `LLVolumeOctree::cull` returns three states so an
     * octree walk can short-circuit into "draw whole subtree, no further
     * culling" once a node is fully inside the frustum. Lumiya kept the
     * binary path because its draw lists were flat and the per-prim test
     * was cheap; with our octree-backed [SpatialIndex], the savings come
     * back as soon as the camera looks into a dense region.
     *
     * Returns:
     *  - [FrustumResult.OUTSIDE]   – box is entirely on the negative side
     *                                of at least one plane; whole subtree
     *                                can be skipped
     *  - [FrustumResult.INSIDE]    – box is entirely on the positive side
     *                                of every plane; subtree can be drawn
     *                                without further plane tests
     *  - [FrustumResult.INTERSECTS] – box straddles at least one plane;
     *                                child boxes must be tested
     *
     * Contract: when this returns INSIDE for a node, callers may treat
     * every contained AABB as visible without re-testing — useful for
     * tight inner loops over an octree leaf.
     */
    fun classifyAABB(
        minX: Float, minY: Float, minZ: Float,
        maxX: Float, maxY: Float, maxZ: Float
    ): FrustumResult {
        var allInside = true
        for (p in planes) {
            // Positive corner: maximises the plane's inside-ness.
            val px = if (p[0] >= 0) maxX else minX
            val py = if (p[1] >= 0) maxY else minY
            val pz = if (p[2] >= 0) maxZ else minZ
            if (p[0] * px + p[1] * py + p[2] * pz + p[3] < 0f) {
                return FrustumResult.OUTSIDE
            }
            // Negative corner: minimises inside-ness. If it's on or
            // crosses the plane (≤ 0), the whole box is not strictly on
            // the positive side. The `≤` (not `<`) catches the tangent
            // case where the corner sits exactly on the plane — a box
            // touching a plane must report INTERSECTS, not INSIDE, so
            // octree subtree-skipping callers don't draw a child on the
            // outside as if it were guaranteed visible.
            val nx = if (p[0] >= 0) minX else maxX
            val ny = if (p[1] >= 0) minY else maxY
            val nz = if (p[2] >= 0) minZ else maxZ
            if (p[0] * nx + p[1] * ny + p[2] * nz + p[3] <= 0f) {
                allInside = false
            }
        }
        return if (allInside) FrustumResult.INSIDE else FrustumResult.INTERSECTS
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

    /**
     * Test seam — install a single plane equation directly so unit
     * tests don't have to mock the GLES matrix multiply chain in
     * [extractPlanes]. Production callers should always go through
     * [extractPlanes].
     */
    @androidx.annotation.VisibleForTesting
    internal fun setPlaneForTest(index: Int, a: Float, b: Float, c: Float, d: Float) {
        require(index in 0..5) { "plane index out of range: $index" }
        planes[index][0] = a; planes[index][1] = b
        planes[index][2] = c; planes[index][3] = d
    }
}
