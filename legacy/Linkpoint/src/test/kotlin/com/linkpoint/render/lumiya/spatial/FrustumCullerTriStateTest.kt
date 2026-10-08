package com.linkpoint.render.lumiya.spatial

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Tri-state classification of AABBs against a hand-built unit frustum.
 *
 * The default [FrustumCuller.extractPlanes] runs a chain of GLES
 * matrix multiplies that don't return useful values under the JVM
 * unit test runner; using [FrustumCuller.setPlaneForTest] lets us
 * verify the new tri-state logic directly.
 *
 * Frustum (axis-aligned cube from [-1, 1]^3 in world space):
 *   plane 0: +x ≥ -1     (left)         (1,0,0,1)
 *   plane 1: -x ≥ -1     (right)        (-1,0,0,1)
 *   plane 2: +y ≥ -1     (bottom)       (0,1,0,1)
 *   plane 3: -y ≥ -1     (top)          (0,-1,0,1)
 *   plane 4: +z ≥ -1     (near)         (0,0,1,1)
 *   plane 5: -z ≥ -1     (far)          (0,0,-1,1)
 */
class FrustumCullerTriStateTest {

    private fun unitFrustum(): FrustumCuller {
        val c = FrustumCuller()
        c.setPlaneForTest(0,  1f,  0f,  0f, 1f)
        c.setPlaneForTest(1, -1f,  0f,  0f, 1f)
        c.setPlaneForTest(2,  0f,  1f,  0f, 1f)
        c.setPlaneForTest(3,  0f, -1f,  0f, 1f)
        c.setPlaneForTest(4,  0f,  0f,  1f, 1f)
        c.setPlaneForTest(5,  0f,  0f, -1f, 1f)
        return c
    }

    @Test
    fun `box fully inside reports INSIDE`() {
        val c = unitFrustum()
        assertEquals(
            FrustumResult.INSIDE,
            c.classifyAABB(-0.5f, -0.5f, -0.5f, 0.5f, 0.5f, 0.5f)
        )
    }

    @Test
    fun `box straddling a plane reports INTERSECTS`() {
        val c = unitFrustum()
        assertEquals(
            FrustumResult.INTERSECTS,
            c.classifyAABB(-2f, -0.5f, -0.5f, 0.5f, 0.5f, 0.5f)
        )
    }

    @Test
    fun `box completely outside reports OUTSIDE`() {
        val c = unitFrustum()
        assertEquals(
            FrustumResult.OUTSIDE,
            c.classifyAABB(2f, 2f, 2f, 3f, 3f, 3f)
        )
    }

    @Test
    fun `box exactly tangent to a plane is INTERSECTS not OUTSIDE`() {
        val c = unitFrustum()
        // maxX == 1 (right plane), positive corner gives 0, negative
        // corner gives 1 → straddles.
        assertEquals(
            FrustumResult.INTERSECTS,
            c.classifyAABB(0.5f, -0.5f, -0.5f, 1f, 0.5f, 0.5f)
        )
    }

    @Test
    fun `binary helper agrees with non-OUTSIDE classification`() {
        val c = unitFrustum()
        val inside = c.classifyAABB(-0.5f, -0.5f, -0.5f, 0.5f, 0.5f, 0.5f)
        val intersects = c.classifyAABB(-2f, -0.5f, -0.5f, 0.5f, 0.5f, 0.5f)
        val outside = c.classifyAABB(2f, 2f, 2f, 3f, 3f, 3f)
        assertEquals(true, c.isAABBVisible(-0.5f, -0.5f, -0.5f, 0.5f, 0.5f, 0.5f))
        assertEquals(true, c.isAABBVisible(-2f, -0.5f, -0.5f, 0.5f, 0.5f, 0.5f))
        assertEquals(false, c.isAABBVisible(2f, 2f, 2f, 3f, 3f, 3f))
        // Sanity: the tri-state result should never disagree with the
        // binary visible test in either direction.
        assertEquals(inside != FrustumResult.OUTSIDE, true)
        assertEquals(intersects != FrustumResult.OUTSIDE, true)
        assertEquals(outside == FrustumResult.OUTSIDE, true)
    }
}
