package com.linkpoint.render.lumiya.spatial

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Conservative OBB → AABB widening for [SpatialEntry.fromOrientedBox].
 * Pure math; no GLES dependency.
 */
class SpatialEntryRotatedAabbTest {

    private fun identity4(): FloatArray = floatArrayOf(
        1f, 0f, 0f, 0f,
        0f, 1f, 0f, 0f,
        0f, 0f, 1f, 0f,
        0f, 0f, 0f, 1f
    )

    /** Column-major rotation about the world Z axis. */
    private fun rotZ(rad: Float): FloatArray {
        val c = cos(rad)
        val s = sin(rad)
        return floatArrayOf(
            c,  s, 0f, 0f,
           -s,  c, 0f, 0f,
            0f, 0f, 1f, 0f,
            0f, 0f, 0f, 1f
        )
    }

    @Test
    fun `identity rotation leaves half extents unchanged`() {
        val (wx, wy, wz) = SpatialEntry.conservativeWorldHalfExtents(
            halfX = 2f, halfY = 1f, halfZ = 0.5f,
            rotation = identity4()
        )
        assertEquals(2f, wx, 1e-5f)
        assertEquals(1f, wy, 1e-5f)
        assertEquals(0.5f, wz, 1e-5f)
    }

    @Test
    fun `45 degree rotation about Z over-estimates conservatively`() {
        // 2x1x0.5 prim rotated 45 degrees about Z. The XY footprint
        // becomes the diagonal of the local 2x1 rectangle projected
        // onto each axis: |cos|*hx + |sin|*hy = (|cos|*2 + |sin|*1).
        val rad = (PI / 4).toFloat()
        val (wx, wy, wz) = SpatialEntry.conservativeWorldHalfExtents(
            halfX = 2f, halfY = 1f, halfZ = 0.5f,
            rotation = rotZ(rad)
        )
        val expectedXY = (sqrt(2.0) / 2.0).toFloat() * 2f + (sqrt(2.0) / 2.0).toFloat() * 1f
        assertEquals(expectedXY, wx, 1e-5f)
        assertEquals(expectedXY, wy, 1e-5f)
        // Z is unaffected by Z-axis rotation.
        assertEquals(0.5f, wz, 1e-5f)
        // The over-estimate must always cover the local extents.
        assertTrue("rotated XY width must be ≥ original max half extent",
            wx >= 2f - 1e-5f)
    }

    @Test
    fun `90 degree rotation swaps X and Y extents`() {
        val rad = (PI / 2).toFloat()
        val (wx, wy, wz) = SpatialEntry.conservativeWorldHalfExtents(
            halfX = 3f, halfY = 1f, halfZ = 0.5f,
            rotation = rotZ(rad)
        )
        assertEquals(1f, wx, 1e-5f)
        assertEquals(3f, wy, 1e-5f)
        assertEquals(0.5f, wz, 1e-5f)
    }

    @Test
    fun `fromOrientedBox factory preserves position and applies widening`() {
        val rad = (PI / 4).toFloat()
        val entry = SpatialEntry.fromOrientedBox(
            id = 7L,
            posX = 100f, posY = 50f, posZ = 25f,
            halfX = 2f, halfY = 1f, halfZ = 0.5f,
            rotation = rotZ(rad)
        )
        assertEquals(100f, entry.posX, 0f)
        assertEquals(50f, entry.posY, 0f)
        assertEquals(25f, entry.posZ, 0f)
        // World-space half extent must exceed the original max
        // half-extent along the rotated axis.
        assertTrue(entry.halfExtentX > 2f - 1e-5f)
        assertTrue(entry.halfExtentY > 1f - 1e-5f)
    }
}
