package com.linkpoint.protocol.terrain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TerrainPatchTest {

    @Test
    fun `TerrainPatch class initializes without ArrayIndexOutOfBoundsException`() {
        // This test verifies that the static initialization of TerrainPatch
        // completes successfully without throwing ArrayIndexOutOfBoundsException.
        // The crash was caused by buildCopyMatrix16() having incorrect zigzag
        // traversal logic that could produce negative indices.
        
        // Simply accessing PATCH_SIZE forces the companion object initialization
        val patchSize = TerrainPatch.PATCH_SIZE
        assertEquals(16, patchSize)
    }

    @Test
    fun `TerrainPatch companion object constants are correct`() {
        // Verify expected constants
        assertEquals(97, TerrainPatch.END_OF_PATCHES)
        assertEquals(16, TerrainPatch.PATCH_SIZE)
        assertEquals(16, TerrainPatch.PATCHES_PER_SIDE)
    }

    @Test
    fun `TerrainPatch can be instantiated with valid data`() {
        val heightMap = FloatArray(256) { it.toFloat() }
        val patch = TerrainPatch(5, 10, heightMap)
        
        assertEquals(5, patch.x)
        assertEquals(10, patch.y)
        assertEquals(256, patch.heightMap.size)
    }
}
