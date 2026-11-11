package com.linkpoint.assets.transport

import com.linkpoint.assets.AssetType
import com.linkpoint.network.grid.GridInfoResolver
import com.linkpoint.protocol.auth.LoginResponseParser
import com.linkpoint.protocol.capabilities.CapabilityManager
import com.linkpoint.protocol.types.putUUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.UUID

class AssetFetchStrategyTest {

    private lateinit var mockWebServer: MockWebServer

    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `CapabilityAssetAdapter canFetch and fetchAsset HTTP 200 and fail fast on 404`() = runBlocking {
        val assetId = UUID.randomUUID()
        val mockCapUrl = mockWebServer.url("/cap/GetTexture").toString()

        val mockCapManager = object : CapabilityManager() {
            override fun getCapability(name: String): String? {
                return if (name == CAP_GET_TEXTURE || name == CAP_VIEWER_ASSET) mockCapUrl else null
            }
        }

        val adapter = CapabilityAssetAdapter(mockCapManager, okhttp3.OkHttpClient())

        assertTrue(adapter.canFetch(AssetType.TEXTURE))

        // Case 1: Successful fetch (200 OK)
        val expectedData = "TEXTURE_BYTES_CAPABILITY".toByteArray()
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(String(expectedData)))

        val fetchedBytes = adapter.fetchAsset(assetId, AssetType.TEXTURE)
        assertNotNull(fetchedBytes)
        assertArrayEquals(expectedData, fetchedBytes)

        // Case 2: 404 Not Found (fail fast)
        mockWebServer.enqueue(MockResponse().setResponseCode(404).setBody("Not Found"))

        val failedBytes = adapter.fetchAsset(assetId, AssetType.TEXTURE)
        assertNull(failedBytes)
    }

    @Test
    fun `RestAssetServerAdapter fetches asset and fails fast on 404`() = runBlocking {
        val assetId = UUID.randomUUID()
        val serverUrl = mockWebServer.url("").toString()

        val adapter = RestAssetServerAdapter(assetServerUrl = serverUrl)

        assertTrue(adapter.canFetch(AssetType.TEXTURE))
        assertTrue(adapter.canFetch(AssetType.MESH))

        // 200 OK Response
        val expectedData = "REST_ASSET_BYTES".toByteArray()
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(String(expectedData)))

        val fetched = adapter.fetchAsset(assetId, AssetType.TEXTURE)
        assertNotNull(fetched)
        assertArrayEquals(expectedData, fetched)

        // 404 Response
        mockWebServer.enqueue(MockResponse().setResponseCode(404).setBody("Asset Not Found"))

        val notFound = adapter.fetchAsset(assetId, AssetType.TEXTURE)
        assertNull(notFound)
    }

    @Test
    fun `UdpTextureAdapter functions without Android context or TextureManager setup`() = runBlocking {
        val assetId = UUID.randomUUID()
        var requestSent = false

        lateinit var adapter: UdpTextureAdapter
        adapter = UdpTextureAdapter(
            packetSender = { id, discard ->
                if (id == assetId) {
                    requestSent = true
                    val dataBytes = "UDP_TEXTURE_DATA".toByteArray()
                    val payloadBuffer = ByteBuffer.allocate(16 + 1 + 4 + 2 + dataBytes.size).order(ByteOrder.LITTLE_ENDIAN)
                    payloadBuffer.putUUID(assetId)
                    payloadBuffer.put(0.toByte()) // codec
                    payloadBuffer.putInt(dataBytes.size) // size
                    payloadBuffer.putShort(1.toShort()) // packets = 1
                    payloadBuffer.put(dataBytes)

                    adapter.onImageData(payloadBuffer.array())
                }
            },
            timeoutMs = 2000L
        )

        assertTrue(adapter.canFetch(AssetType.TEXTURE))
        assertFalse(adapter.canFetch(AssetType.MESH))

        val fetchResult = adapter.fetchAsset(assetId, AssetType.TEXTURE)

        assertNotNull(fetchResult)
        assertEquals("UDP_TEXTURE_DATA", String(fetchResult!!))
        assertTrue(requestSent)
    }

    @Test
    fun `AssetFetchStrategyManager evaluates adapters in priority order`() = runBlocking {
        val assetId = UUID.randomUUID()
        val manager = AssetFetchStrategyManager()

        var capCalled = false
        var restCalled = false
        var udpCalled = false

        val mockCapAdapter = object : AssetFetcherAdapter {
            override val name: String = "CapabilityAssetAdapter"
            override val priority: Int = 10
            override fun canFetch(assetType: AssetType): Boolean = true
            override suspend fun fetchAsset(assetId: UUID, assetType: AssetType): ByteArray? {
                capCalled = true
                return null // Fail capability
            }
        }

        val mockRestAdapter = object : AssetFetcherAdapter {
            override val name: String = "RestAssetServerAdapter"
            override val priority: Int = 20
            override fun canFetch(assetType: AssetType): Boolean = true
            override suspend fun fetchAsset(assetId: UUID, assetType: AssetType): ByteArray? {
                restCalled = true
                return "REST_SUCCESS".toByteArray() // REST succeeds
            }
        }

        val mockUdpAdapter = object : AssetFetcherAdapter {
            override val name: String = "UdpTextureAdapter"
            override val priority: Int = 30
            override fun canFetch(assetType: AssetType): Boolean = true
            override suspend fun fetchAsset(assetId: UUID, assetType: AssetType): ByteArray? {
                udpCalled = true
                return "UDP_SUCCESS".toByteArray()
            }
        }

        manager.registerAdapter(mockUdpAdapter)
        manager.registerAdapter(mockCapAdapter)
        manager.registerAdapter(mockRestAdapter)

        val result = manager.fetchAsset(assetId, AssetType.TEXTURE)

        assertNotNull(result)
        assertEquals("REST_SUCCESS", String(result!!))
        assertTrue(capCalled)
        assertTrue(restCalled)
        assertFalse(udpCalled) // UDP was not evaluated because REST succeeded
    }

    @Test
    fun `GridInfoResolver extracts asset_server_url into GridInfo`() = runBlocking {
        val xmlPayload = """
            <?xml version="1.0"?>
            <gridinfo>
                <gridname>Test OpenSim Grid</gridname>
                <loginuri>http://login.testgrid.org:8002/</loginuri>
                <asset_server_url>http://assets.testgrid.org:8003</asset_server_url>
            </gridinfo>
        """.trimIndent()

        val parsedMap = GridInfoResolver.parseGridInfoPayload(xmlPayload)
        assertEquals("http://assets.testgrid.org:8003", parsedMap["asset_server_url"])

        val gridInfo = GridInfoResolver.resolveGridInfo("http://login.testgrid.org:8002/")
        val mergedGrid = GridInfoResolver.parseGridInfoPayload(xmlPayload)
        assertTrue(mergedGrid.containsKey("asset_server_url"))
    }

    @Test
    fun `LoginResponseParser extracts asset_server_url from XML`() {
        val loginXml = """
            <?xml version="1.0"?>
            <methodResponse>
                <params><param><value><struct>
                    <member><name>agent_access_max</name><value><string>M</string></value></member>
                    <member><name>asset_server_url</name><value><string>http://assets.osgrid.org:8003</string></value></member>
                </struct></value></param></params>
            </methodResponse>
        """.trimIndent()

        val parsed = LoginResponseParser.parse(loginXml)
        assertEquals("http://assets.osgrid.org:8003", parsed.assetServerUrl)
    }
}
