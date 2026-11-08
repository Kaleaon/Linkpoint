package com.linkpoint.render.lumiya.glres

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import java.nio.ByteBuffer

@RunWith(AndroidJUnit4::class)
class PboRingBufferManagerTest {

    @Test
    fun testGles2FallbackBehavior() {
        val rm = GLResourceManager()
        val pboManager = PboRingBufferManager(rm, ringDepth = 2)

        // GLES version 20 (< 30)
        pboManager.initialize(glVersion = 20)

        assertFalse(pboManager.isPboSupported)

        val buffer = ByteBuffer.allocateDirect(256 * 256 * 4)
        val success = pboManager.stageAndUploadTexture(
            textureHandle = 1,
            width = 256,
            height = 256,
            pixelBuffer = buffer
        )

        assertFalse(success) // Falls back to direct upload
        assertEquals(1L, pboManager.totalFallbackUploads)
    }

    @Test
    fun testMemoryCeilingAutoThrottling() {
        val rm = GLResourceManager()
        val pboManager = PboRingBufferManager(rm, ringDepth = 2)

        val buffer = ByteBuffer.allocateDirect(1024 * 1024 * 4)

        // When low memory system flag is true, stageAndUploadTexture auto-throttles
        val success = pboManager.stageAndUploadTexture(
            textureHandle = 1,
            width = 1024,
            height = 1024,
            pixelBuffer = buffer,
            isLowMemorySystem = true
        )

        assertFalse(success) // Auto-throttled to protect system RAM
        assertTrue(pboManager.autoThrottled)
    }
}
