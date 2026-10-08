package com.linkpoint.protocol.textures

import com.linkpoint.network.MeteredAssetGate
import com.linkpoint.network.core.NetworkSessionManager
import com.linkpoint.render.RenderStateManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.UUID

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class SLTextureFetcherTest {

    private lateinit var fetcher: SLTextureFetcher

    @Before
    fun setUp() {
        fetcher = SLTextureFetcher()
        fetcher.clearQueue()
    }

    @Test
    fun `pauseFetching toggles state and halts queue polling`() {
        assertFalse(fetcher.isFetchingPaused)

        val id1 = UUID.randomUUID()
        val id2 = UUID.randomUUID()
        val id3 = UUID.randomUUID()

        fetcher.beginFetch(SLTextureFetcher.TextureFetchRequest(id1, priority = 10))
        fetcher.beginFetch(SLTextureFetcher.TextureFetchRequest(id2, priority = 20))

        assertEquals(2, fetcher.activeTransferCount)
        assertEquals(0, fetcher.pendingQueueSize)

        // Pause texture fetching
        fetcher.pauseFetching()

        assertTrue(fetcher.isFetchingPaused)
        assertEquals(0, fetcher.activeTransferCount)
        assertEquals(2, fetcher.pendingQueueSize) // Descriptors retained in queue

        // Begin fetch while paused -> request enqueued but no active transfers started
        fetcher.beginFetch(SLTextureFetcher.TextureFetchRequest(id3, priority = 30))
        assertEquals(0, fetcher.activeTransferCount)
        assertEquals(3, fetcher.pendingQueueSize)
    }

    @Test
    fun `resumeFetching restores queue execution in priority order`() {
        val idLow = UUID.randomUUID()
        val idHigh = UUID.randomUUID()
        val idCritical = UUID.randomUUID()

        fetcher.pauseFetching()

        fetcher.beginFetch(SLTextureFetcher.TextureFetchRequest(idLow, priority = 5))
        fetcher.beginFetch(SLTextureFetcher.TextureFetchRequest(idHigh, priority = 50))
        fetcher.beginFetch(SLTextureFetcher.TextureFetchRequest(idCritical, priority = 100))

        assertEquals(0, fetcher.activeTransferCount)
        assertEquals(3, fetcher.pendingQueueSize)

        // Resume fetching
        fetcher.resumeFetching()

        assertFalse(fetcher.isFetchingPaused)
        assertEquals(SLTextureFetcher.MAX_UDP_TRANSFERS, fetcher.activeTransferCount)
        assertEquals(1, fetcher.pendingQueueSize)
    }

    @Test
    fun `RenderStateManager fullscreen overlay toggles pause and resume`() {
        val renderStateManager = RenderStateManager()
        renderStateManager.textureQueueController = fetcher

        assertFalse(fetcher.isFetchingPaused)

        // Enable chat overlay -> pauses fetching
        renderStateManager.setFullScreenOverlayActive(true)
        assertTrue(fetcher.isFetchingPaused)

        // Dismiss overlay -> resumes fetching
        renderStateManager.setFullScreenOverlayActive(false)
        assertFalse(fetcher.isFetchingPaused)
    }

    @Test
    fun `NetworkSessionManager background and foreground toggle fetcher and permits`() {
        val context = org.robolectric.RuntimeEnvironment.getApplication()
        val networkSessionManager = NetworkSessionManager(context, textureQueueController = fetcher)

        assertFalse(fetcher.isFetchingPaused)

        // Transition to background
        networkSessionManager.onBackground()
        assertTrue(fetcher.isFetchingPaused)

        // Return to foreground
        networkSessionManager.onForeground()
        assertFalse(fetcher.isFetchingPaused)
    }
}
