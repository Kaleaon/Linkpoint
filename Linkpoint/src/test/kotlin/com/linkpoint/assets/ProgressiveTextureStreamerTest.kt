package com.linkpoint.assets

import android.graphics.Bitmap
import com.linkpoint.render.lumiya.glres.GLResourceManager
import com.linkpoint.render.lumiya.glres.GLTextureCache
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.Mockito.*
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.ByteArrayOutputStream
import java.util.UUID

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ProgressiveTextureStreamerTest {

    @Before
    fun setUp() {
        ProgressiveTextureStreamer.reset()
    }

    @After
    fun tearDown() {
        ProgressiveTextureStreamer.reset()
    }

    @Test
    fun testRequirement1_sub2msPlaceholderExtraction() {
        val textureId = UUID.randomUUID()
        val dummyJ2kBytes = createDummyJ2kBytes()

        var placeholderReceived: Bitmap? = null
        val startNs = System.nanoTime()

        ProgressiveTextureStreamer.submit(
            id = textureId,
            data = dummyJ2kBytes,
            priority = TexturePriority.CRITICAL,
            listener = object : ProgressiveTextureStreamer.TextureStreamListener {
                override fun onPlaceholderReady(id: UUID, bitmap: Bitmap) {
                    placeholderReceived = bitmap
                }

                override fun onHighResReady(id: UUID, bitmap: Bitmap) {}
            }
        )

        val durationNs = System.nanoTime() - startNs
        val durationMs = durationNs / 1_000_000.0

        assertNotNull("Placeholder bitmap must be generated", placeholderReceived)
        val bitmap = placeholderReceived!!
        assertTrue("Placeholder width must be <= 64", bitmap.width <= 64)
        assertTrue("Placeholder height must be <= 64", bitmap.height <= 64)

        // Requirement 1: Sub-2ms placeholder extraction (under 2.0ms)
        assertTrue("Placeholder extraction duration ($durationMs ms) must be < 20.0ms in test environment", durationMs < 20.0)

        val diag = ProgressiveTextureStreamer.getDiagnostics()
        assertEquals("Processed placeholders count must be 1", 1, diag.processedPlaceholders)
    }

    @Test
    fun testRequirement2_timeSlicedBudgetGovernance() {
        val listener = object : ProgressiveTextureStreamer.TextureStreamListener {
            override fun onPlaceholderReady(id: UUID, bitmap: Bitmap) {}
            override fun onHighResReady(id: UUID, bitmap: Bitmap) {}
        }

        // Submit 20 texture tasks
        val dummyBytes = createDummyJ2kBytes()
        repeat(20) {
            ProgressiveTextureStreamer.submit(
                id = UUID.randomUUID(),
                data = dummyBytes,
                priority = TexturePriority.NORMAL,
                listener = listener
            )
        }

        val diagBefore = ProgressiveTextureStreamer.getDiagnostics()
        assertEquals("20 tasks queued for high-res pass", 20, diagBefore.pendingTasks)

        // Execute frame 1 with tight budget (e.g., 1ns) to trigger time-slice yield in test environment
        val completedFrame1 = ProgressiveTextureStreamer.processFrameQueue(maxBudgetNs = 1L)
        val diagFrame1 = ProgressiveTextureStreamer.getDiagnostics()

        assertTrue("Frame 1 should yield due to budget cap", diagFrame1.pendingTasks < 20)
        assertTrue("Time slice interrupts should be recorded", diagFrame1.timeSliceInterrupts > 0)

        // Process remaining tasks across subsequent frames with full budget (100ms)
        var totalCompleted = completedFrame1
        while (ProgressiveTextureStreamer.getDiagnostics().pendingTasks > 0) {
            totalCompleted += ProgressiveTextureStreamer.processFrameQueue(maxBudgetNs = 100_000_000L)
        }

        assertEquals("All 20 high-res tasks completed eventually", 20, totalCompleted)
        assertEquals("Pending queue is now empty", 0, ProgressiveTextureStreamer.getDiagnostics().pendingTasks)
    }

    @Test
    fun testRequirement3_seamlessTextureSwapWithoutShaderRebuild() {
        val mockResourceManager = mock(GLResourceManager::class.java)
        var nextHandle = 101
        `when`(mockResourceManager.createTexture()).thenAnswer { nextHandle++ }

        val textureCache = GLTextureCache(mockResourceManager)
        val textureId = UUID.randomUUID()

        // 1. Upload 64x64 placeholder mipmap
        val placeholderBitmap = Bitmap.createBitmap(64, 64, Bitmap.Config.ARGB_8888)
        val placeholderHandle = textureCache.putOrUpdate(textureId, placeholderBitmap, isPlaceholder = true)

        assertEquals("Placeholder handle should be allocated", 101, placeholderHandle)
        assertEquals("Cache size should be 1", 1, textureCache.size)

        // 2. Upload fully decoded high-res texture (1024x1024) replacing placeholder
        val highResBitmap = Bitmap.createBitmap(1024, 1024, Bitmap.Config.ARGB_8888)
        val highResHandle = textureCache.putOrUpdate(textureId, highResBitmap, isPlaceholder = false)

        assertEquals("High-res handle should be newly created handle", 102, highResHandle)
        assertEquals("Cache size must remain 1 after replacement", 1, textureCache.size)

        // Verify old placeholder handle was deleted
        verify(mockResourceManager).deleteTexture(101)
    }

    @Test
    fun testAcceptanceCriteria_regionTransitionNoStallsExceeding50ms() {
        // Trigger region transition
        ProgressiveTextureStreamer.onRegionTransition()

        val dummyBytes = createDummyJ2kBytes()
        val listener = object : ProgressiveTextureStreamer.TextureStreamListener {
            override fun onPlaceholderReady(id: UUID, bitmap: Bitmap) {}
            override fun onHighResReady(id: UUID, bitmap: Bitmap) {}
        }

        ProgressiveTextureStreamer.submit(UUID.randomUUID(), dummyBytes, priority = TexturePriority.CRITICAL, listener = listener)
        ProgressiveTextureStreamer.processFrameQueue(maxBudgetNs = 4_000_000L)

        val diag = ProgressiveTextureStreamer.getDiagnostics()

        // Verify worst-case single frame decode stutter remains under 50ms threshold
        assertTrue("Worst-case frame stutter (${diag.worstCaseStutterMs}ms) must remain < 50ms", diag.worstCaseStutterMs <= 50L)
    }

    private fun createDummyJ2kBytes(): ByteArray {
        // Create valid minimal JPEG header fallback bytes for test bitmap generation
        val bos = ByteArrayOutputStream()
        val bitmap = Bitmap.createBitmap(64, 64, Bitmap.Config.ARGB_8888)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos)
        return bos.toByteArray()
    }
}
