package com.linkpoint.inventory

import com.linkpoint.assets.AssetType
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.UUID
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class InventoryAssetFetcherTest {

    private lateinit var server: MockWebServer

    @Before
    fun setUp() {
        server = MockWebServer()
        server.start()
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun `fetchAsset returns bytes on 200 OK`() = runBlocking {
        val sampleData = byteArrayOf(0xAA.toByte(), 0xBB.toByte(), 0xCC.toByte())
        server.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(okio.Buffer().write(sampleData))
        )

        val fetcher = InventoryAssetFetcher(
            httpClient = InventoryAssetFetcher.defaultOkHttpClient()
        )

        val assetId = UUID.randomUUID()
        val url = server.url("/asset/$assetId").toString()
        val bytes = fetcher.fetchAsset(assetId, url, AssetType.TEXTURE.value)

        assertNotNull(bytes)
        assertArrayEquals(sampleData, bytes)
    }

    @Test
    fun `fetchAsset retries on 503 and succeeds on 200 OK`() = runBlocking {
        val sampleData = byteArrayOf(0x10, 0x20, 0x30)
        val delays = mutableListOf<Long>()

        server.enqueue(MockResponse().setResponseCode(503))
        server.enqueue(MockResponse().setResponseCode(500))
        server.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(okio.Buffer().write(sampleData))
        )

        val fetcher = InventoryAssetFetcher(
            httpClient = InventoryAssetFetcher.defaultOkHttpClient(),
            maxRetries = 3,
            initialBackoffMs = 50,
            maxBackoffMs = 200,
            sleeper = { delays.add(it) }
        )

        val assetId = UUID.randomUUID()
        val url = server.url("/asset/$assetId").toString()
        val bytes = fetcher.fetchAsset(assetId, url)

        assertNotNull(bytes)
        assertArrayEquals(sampleData, bytes)
        assertEquals(listOf(50L, 100L), delays)
    }

    @Test
    fun `fetchAsset returns null on 404 without retrying`() = runBlocking {
        val delays = mutableListOf<Long>()
        server.enqueue(MockResponse().setResponseCode(404))

        val fetcher = InventoryAssetFetcher(
            httpClient = InventoryAssetFetcher.defaultOkHttpClient(),
            maxRetries = 3,
            initialBackoffMs = 50,
            sleeper = { delays.add(it) }
        )

        val assetId = UUID.randomUUID()
        val url = server.url("/asset/$assetId").toString()
        val bytes = fetcher.fetchAsset(assetId, url)

        assertNull(bytes)
        assertTrue(delays.isEmpty())
    }

    @Test
    fun `fetchAssetAsync invokes onSuccess callback non-blockingly`() {
        val sampleData = byteArrayOf(0x01, 0x02, 0x03)
        server.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(okio.Buffer().write(sampleData))
        )

        val fetcher = InventoryAssetFetcher(
            httpClient = InventoryAssetFetcher.defaultOkHttpClient()
        )

        val assetId = UUID.randomUUID()
        val url = server.url("/asset/$assetId").toString()
        val latch = CountDownLatch(1)
        var receivedBytes: ByteArray? = null
        var receivedType: Int = -1

        fetcher.fetchAssetAsync(
            assetId = assetId,
            assetUrl = url,
            assetType = AssetType.TEXTURE.value,
            callback = object : AssetFetchCallback {
                override fun onSuccess(assetId: UUID, assetType: Int, data: ByteArray, fromCache: Boolean) {
                    receivedBytes = data
                    receivedType = assetType
                    latch.countDown()
                }

                override fun onFailure(assetId: UUID, assetType: Int, error: Throwable) {
                    latch.countDown()
                }
            }
        )

        val completed = latch.await(5, TimeUnit.SECONDS)
        assertTrue("Callback completed within 5s", completed)
        assertNotNull(receivedBytes)
        assertArrayEquals(sampleData, receivedBytes)
        assertEquals(AssetType.TEXTURE.value, receivedType)
    }
}
