package com.linkpoint.render.lumiya.picking

import android.opengl.GLU
import android.opengl.Matrix

/**
 * Screen-to-world ray casting and triangle intersection testing.
 *
 * Design lineage: Lumiya `GLRayTrace.java`, modernised.
 *
 * Used for:
 *  - Object picking (tap → which prim?)
 *  - Avatar collision detection
 *  - HUD object selection
 */
object GLRayTrace {

    /**
     * Unproject a screen coordinate into a world-space ray.
     *
     * @return Pair of (rayOrigin[3], rayDirection[3]).
     */
    fun screenToWorldRay(
        screenX: Float, screenY: Float,
        viewportWidth: Int, viewportHeight: Int,
        viewMatrix: FloatArray,
        projectionMatrix: FloatArray
    ): Pair<FloatArray, FloatArray> {
        val invertedVP = FloatArray(16)
        val vp = FloatArray(16)
        Matrix.multiplyMM(vp, 0, projectionMatrix, 0, viewMatrix, 0)
        Matrix.invertM(invertedVP, 0, vp, 0)

        // Normalised device coordinates
        val ndcX = (2.0f * screenX / viewportWidth) - 1.0f
        val ndcY = 1.0f - (2.0f * screenY / viewportHeight)

        val nearPoint = unproject(invertedVP, ndcX, ndcY, -1.0f)
        val farPoint = unproject(invertedVP, ndcX, ndcY, 1.0f)

        val direction = floatArrayOf(
            farPoint[0] - nearPoint[0],
            farPoint[1] - nearPoint[1],
            farPoint[2] - nearPoint[2]
        )
        normalise3(direction)

        return Pair(nearPoint, direction)
    }

    /**
     * Möller–Trumbore ray–triangle intersection.
     *
     * @return Intersection distance along the ray, or [Float.MAX_VALUE] if no hit.
     */
    fun rayTriangleIntersect(
        rayOrigin: FloatArray, rayDir: FloatArray,
        v0: FloatArray, v1: FloatArray, v2: FloatArray
    ): Float {
        val EPSILON = 0.000001f

        val edge1 = floatArrayOf(v1[0] - v0[0], v1[1] - v0[1], v1[2] - v0[2])
        val edge2 = floatArrayOf(v2[0] - v0[0], v2[1] - v0[1], v2[2] - v0[2])

        val h = cross(rayDir, edge2)
        val a = dot(edge1, h)
        if (a > -EPSILON && a < EPSILON) return Float.MAX_VALUE

        val f = 1.0f / a
        val s = floatArrayOf(rayOrigin[0] - v0[0], rayOrigin[1] - v0[1], rayOrigin[2] - v0[2])
        val u = f * dot(s, h)
        if (u < 0.0f || u > 1.0f) return Float.MAX_VALUE

        val q = cross(s, edge1)
        val v = f * dot(rayDir, q)
        if (v < 0.0f || u + v > 1.0f) return Float.MAX_VALUE

        val t = f * dot(edge2, q)
        return if (t > EPSILON) t else Float.MAX_VALUE
    }

    // ── Helpers ──────────────────────────────────────────────────────────

    private fun unproject(invertedVP: FloatArray, ndcX: Float, ndcY: Float, ndcZ: Float): FloatArray {
        val clipCoords = floatArrayOf(ndcX, ndcY, ndcZ, 1.0f)
        val worldCoords = FloatArray(4)
        Matrix.multiplyMV(worldCoords, 0, invertedVP, 0, clipCoords, 0)
        val w = worldCoords[3]
        return floatArrayOf(worldCoords[0] / w, worldCoords[1] / w, worldCoords[2] / w)
    }

    private fun cross(a: FloatArray, b: FloatArray): FloatArray {
        return floatArrayOf(
            a[1] * b[2] - a[2] * b[1],
            a[2] * b[0] - a[0] * b[2],
            a[0] * b[1] - a[1] * b[0]
        )
    }

    private fun dot(a: FloatArray, b: FloatArray): Float {
        return a[0] * b[0] + a[1] * b[1] + a[2] * b[2]
    }

    private fun normalise3(v: FloatArray) {
        val len = Math.sqrt((v[0] * v[0] + v[1] * v[1] + v[2] * v[2]).toDouble()).toFloat()
        if (len > 0f) { v[0] /= len; v[1] /= len; v[2] /= len }
    }
}
