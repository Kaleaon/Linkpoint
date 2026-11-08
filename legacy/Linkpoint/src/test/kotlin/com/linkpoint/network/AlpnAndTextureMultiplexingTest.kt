package com.linkpoint.network

import com.linkpoint.assets.AssetCache
import com.linkpoint.assets.TextureManager
import okhttp3.OkHttpClient
import okhttp3.Protocol
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.robolectric.RobolectricTestRunner
import java.security.Security

@RunWith(RobolectricTestRunner::class)
class AlpnAndTextureMultiplexingTest {

    @Before
    fun setUp() {
        NetworkLogger.clearLogs()
    }

    @Test
    fun testEnsureConscryptInstalled() {
        val installed = SSLHelper.ensureConscryptInstalled()
        assertTrue("SSLHelper.ensureConscryptInstalled should return true", installed)
        val conscryptProvider = Security.getProvider("Conscrypt")
        assertNotNull("Conscrypt provider should be registered in JCA", conscryptProvider)
    }

    @Test
    fun testConfigureSSLAndCdnProtocolList() {
        val builder = OkHttpClient.Builder()
        SSLHelper.configureForCdn(builder)
        val client = builder.build()

        assertTrue("Client protocols must contain HTTP_2", client.protocols.contains(Protocol.HTTP_2))
        assertTrue("Client protocols must contain HTTP_1_1", client.protocols.contains(Protocol.HTTP_1_1))
        assertEquals("First priority protocol must be HTTP_2", Protocol.HTTP_2, client.protocols[0])
    }

    @Test
    fun testMeteredAssetGateConcurrencyCaps() {
        val gate = MeteredAssetGate(initialMetered = false)
        assertEquals(64, gate.concurrencyCap)
        assertFalse(gate.isMetered)

        gate.updateMetered(true)
        assertEquals(2, gate.concurrencyCap)
        assertTrue(gate.isMetered)

        gate.updateMetered(false)
        assertEquals(64, gate.concurrencyCap)
        assertFalse(gate.isMetered)
    }

    @Test
    fun testTextureManagerDiagnosticsInitialState() {
        val mockContext = mock<android.content.Context>()
        val mockCache = mock<AssetCache>()

        val textureManager = TextureManager(mockContext, mockCache)
        val diag = textureManager.getDiagnostics()

        assertNull(diag.lastNegotiatedProtocol)
        assertEquals(0, diag.http2DownloadCount)
        assertEquals(0, diag.http11DownloadCount)
        assertEquals(0, diag.alpnWarningCount)
        assertEquals("UNTESTED", diag.alpnState)

        textureManager.shutdown()
    }

    @Test
    fun testNetworkLoggerAlpnWarning() {
        NetworkLogger.logAlpnWarning("https://asset-cdn.glb.agni.lindenlab.com/test", "http/1.1")
        val stats = NetworkLogger.getProtocolStatistics()

        assertEquals(1, stats.alpnWarnings)
        val logs = NetworkLogger.getRecentLogs()
        assertTrue(logs.contains("ALPN Protocol Fallback Warning"))
    }
}
