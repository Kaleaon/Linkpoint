package com.linkpoint.render

import android.content.ComponentCallbacks2
import android.graphics.Bitmap
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import java.util.UUID
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

class GpuTexturesTest {

    private lateinit var destroyedCount: AtomicInteger
    private lateinit var gpuTextures: GpuTextures
    private lateinit var worldRenderer: WorldRenderer

    @Before
    fun setUp() {
        destroyedCount = AtomicInteger(0)
        gpuTextures = GpuTextures(
            context = null,
            engine = null,
            renderThreadExecutor = null,
            customDestroyer = { destroyedCount.incrementAndGet() }
        )
        worldRenderer = WorldRenderer(
            context = null,
            engine = null,
            gpuTextures = gpuTextures
        )
    }

    @Test
    fun testVramByteFootprintCalculationWithMipmaps() {
        // 512x512 RGBA8888, 1 level = 512 * 512 * 4 = 1,048,576 bytes
        val baseBytes = GpuTextures.calculateVramSizeBytes(512, 512, GpuTextures.TextureFormat.RGBA_8888, 1)
        assertEquals(1048576L, baseBytes)

        // 512x512 RGBA8888, 2 levels = 1,048,576 + (256 * 256 * 4) = 1,048,576 + 262,144 = 1,310,720 bytes
        val mipsBytes = GpuTextures.calculateVramSizeBytes(512, 512, GpuTextures.TextureFormat.RGBA_8888, 2)
        assertEquals(1310720L, mipsBytes)

        // 512x512 ETC2_RGB8 (8 bytes per 4x4 block) -> (512/4)*(512/4)*8 = 128*128*8 = 131,072 bytes
        val etc2Bytes = GpuTextures.calculateVramSizeBytes(512, 512, GpuTextures.TextureFormat.ETC2_RGB8, 1)
        assertEquals(131072L, etc2Bytes)

        // 512x512 ASTC_4x4 (16 bytes per 4x4 block) -> 128*128*16 = 262,144 bytes
        val astcBytes = GpuTextures.calculateVramSizeBytes(512, 512, GpuTextures.TextureFormat.ASTC_4x4, 1)
        assertEquals(262144L, astcBytes)
    }

    @Test
    fun testAdaptiveVramBudgetCap() {
        // Without context, default is 512MB
        assertEquals(GpuTextures.HIGH_RAM_BUDGET_BYTES, GpuTextures.determineDefaultVramBudget(null))

        // Set custom budget
        val customBudget = 64L * 1024L * 1024L
        gpuTextures.setMaxVramBudgetBytes(customBudget)
        assertEquals(customBudget, gpuTextures.getMaxVramBudgetBytes())
    }

    @Test
    fun testLruEvictionWhenExceedingBudget() {
        // Set small budget cap: 10 MB (10,485,760 bytes)
        val budget = 10L * 1024L * 1024L
        gpuTextures.setMaxVramBudgetBytes(budget)

        val id1 = UUID.randomUUID()
        val id2 = UUID.randomUUID()
        val id3 = UUID.randomUUID()

        // 512x512 RGBA8888 = 1,048,576 bytes each (~1MB)
        // Upload 8 textures (8MB)
        val texIds = mutableListOf<UUID>()
        for (i in 0 until 8) {
            val id = UUID.randomUUID()
            texIds.add(id)
            gpuTextures.uploadTexture(id, null, 512, 512, GpuTextures.TextureFormat.RGBA_8888)
        }
        assertEquals(8 * 1048576L, gpuTextures.getCurrentVramUsageBytes())
        assertEquals(8, gpuTextures.getActiveTextureCount())

        // Upload 4 more textures -> total would be 12MB (> 10MB budget)
        val newId1 = UUID.randomUUID()
        val newId2 = UUID.randomUUID()
        val newId3 = UUID.randomUUID()
        val newId4 = UUID.randomUUID()

        gpuTextures.uploadTexture(newId1, null, 512, 512, GpuTextures.TextureFormat.RGBA_8888)
        gpuTextures.uploadTexture(newId2, null, 512, 512, GpuTextures.TextureFormat.RGBA_8888)
        gpuTextures.uploadTexture(newId3, null, 512, 512, GpuTextures.TextureFormat.RGBA_8888)
        gpuTextures.uploadTexture(newId4, null, 512, 512, GpuTextures.TextureFormat.RGBA_8888)

        // VRAM usage must stay below or equal to 10MB budget
        assertTrue("VRAM usage must be within budget", gpuTextures.getCurrentVramUsageBytes() <= budget)

        // First uploaded textures must be LRU evicted
        assertEquals(GpuTextures.TextureState.EVICTED, gpuTextures.getTextureState(texIds[0]))
        assertEquals(GpuTextures.TextureState.EVICTED, gpuTextures.getTextureState(texIds[1]))

        // Newest uploaded textures must be ACTIVE
        assertEquals(GpuTextures.TextureState.ACTIVE, gpuTextures.getTextureState(newId4))
    }

    @Test
    fun testPinnedTexturesAreProtectedFromEviction() {
        // Set budget to 3MB
        gpuTextures.setMaxVramBudgetBytes(3L * 1048576L)

        val id1 = UUID.randomUUID() // ~1MB
        val id2 = UUID.randomUUID() // ~1MB
        val id3 = UUID.randomUUID() // ~1MB

        gpuTextures.uploadTexture(id1, null, 512, 512, isPinned = true) // PINNED
        gpuTextures.uploadTexture(id2, null, 512, 512, isPinned = false) // ACTIVE
        gpuTextures.uploadTexture(id3, null, 512, 512, isPinned = false) // ACTIVE

        assertEquals(3 * 1048576L, gpuTextures.getCurrentVramUsageBytes())

        // Upload 4th texture (~1MB) -> exceeds 3MB budget
        val id4 = UUID.randomUUID()
        gpuTextures.uploadTexture(id4, null, 512, 512, isPinned = false)

        // Texture 1 (PINNED) must NOT be evicted!
        assertEquals(GpuTextures.TextureState.PINNED, gpuTextures.getTextureState(id1))

        // Texture 2 (unpinned oldest) must be evicted instead
        assertEquals(GpuTextures.TextureState.EVICTED, gpuTextures.getTextureState(id2))

        // Texture 3 and 4 are ACTIVE
        assertEquals(GpuTextures.TextureState.ACTIVE, gpuTextures.getTextureState(id3))
        assertEquals(GpuTextures.TextureState.ACTIVE, gpuTextures.getTextureState(id4))
    }

    @Test
    fun testMemoryPressurePurge() {
        val id1 = UUID.randomUUID()
        val id2 = UUID.randomUUID()
        val id3 = UUID.randomUUID() // Pinned

        gpuTextures.uploadTexture(id1, null, 512, 512)
        gpuTextures.uploadTexture(id2, null, 512, 512)
        gpuTextures.uploadTexture(id3, null, 512, 512, isPinned = true)

        assertEquals(3 * 1048576L, gpuTextures.getCurrentVramUsageBytes())

        // System OS memory pressure event
        gpuTextures.onTrimMemory(ComponentCallbacks2.TRIM_MEMORY_RUNNING_CRITICAL)

        // Unpinned textures (id1, id2) are purged
        assertEquals(GpuTextures.TextureState.EVICTED, gpuTextures.getTextureState(id1))
        assertEquals(GpuTextures.TextureState.EVICTED, gpuTextures.getTextureState(id2))

        // Pinned texture (id3) remains
        assertEquals(GpuTextures.TextureState.PINNED, gpuTextures.getTextureState(id3))

        // VRAM usage drops to only pinned texture
        assertEquals(1048576L, gpuTextures.getCurrentVramUsageBytes())
    }

    @Test
    fun testReuploadingEvictedTexture() {
        val id1 = UUID.randomUUID()
        gpuTextures.uploadTexture(id1, null, 512, 512)

        // Force purge
        gpuTextures.purgeUnpinned()
        assertEquals(GpuTextures.TextureState.EVICTED, gpuTextures.getTextureState(id1))

        val fetched = AtomicBoolean(false)
        val mockBmp: Bitmap = org.mockito.kotlin.mock {
            on { width } doReturn 512
            on { height } doReturn 512
            on { isRecycled } doReturn false
        }
        val mockFetcher = WorldRenderer.TextureFetcher { textureId, onResolved ->
            assertEquals(id1, textureId)
            fetched.set(true)
            onResolved(mockBmp)
        }

        // WorldRenderer refreshTextures for visible frustum textures
        worldRenderer.refreshTextures(listOf(id1), mockFetcher)

        assertTrue("Evicted texture should be re-requested from fetcher", fetched.get())
        assertEquals(GpuTextures.TextureState.ACTIVE, gpuTextures.getTextureState(id1))
        assertEquals(1048576L, gpuTextures.getCurrentVramUsageBytes())
    }

    @Test
    fun testRenderThreadSafetyAndLifecycle() {
        val executorRan = AtomicBoolean(false)
        gpuTextures.setRenderThreadExecutor { runnable ->
            executorRan.set(true)
            runnable.run()
        }

        val id = UUID.randomUUID()
        gpuTextures.uploadTexture(id, null, 512, 512)
        gpuTextures.purgeUnpinned()

        assertTrue("Destruction must be dispatched to render thread executor", executorRan.get())
        assertEquals(GpuTextures.TextureState.EVICTED, gpuTextures.getTextureState(id))
    }
}
