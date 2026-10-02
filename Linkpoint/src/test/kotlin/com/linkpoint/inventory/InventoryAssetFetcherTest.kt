package com.linkpoint.inventory

import com.linkpoint.assets.AssetType
import com.linkpoint.network.CronetHttpClient
import com.linkpoint.network.CronetResult
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
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
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.stub
import org.robolectric.RobolectricTestRunner
import java.util.UUID
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@RunWith(RobolectricTestRunner::class)
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

    @Test
    fun testFetchAssetViaCronetSuccess() = runTest {
        val testAssetId = UUID.randomUUID()
        val testUrl = "https://asset-cdn.glb.agni.lindenlab.com/asset/$testAssetId"
        val sampleData = byteArrayOf(1, 2, 3, 4, 5)
        val cronetClient = mock<CronetHttpClient>()
        cronetClient.stub {
            on { isAvailable } doReturn true
            onBlocking { get(any(), any(), any()) } doReturn CronetResult.Success(
                code = 200,
                body = sampleData,
                protocol = "h3",
                proxy = null
            )
        }

        val fetcher = InventoryAssetFetcher(cronetClient)
        val result = fetcher.fetchAssetWithResult(testAssetId, testUrl)

        assertTrue(result is AssetFetchResult.Success)
        val success = result as AssetFetchResult.Success
        assertEquals(testAssetId, success.assetId)
        assertArrayEquals(sampleData, success.data)
        assertEquals(200, success.httpStatusCode)
        assertEquals("h3", success.protocol)
    }

    @Test
    fun testAsyncFetchCallbackDispatched() {
        val testAssetId = UUID.randomUUID()
        val testUrl = "https://asset-cdn.glb.agni.lindenlab.com/asset/$testAssetId"
        val sampleData = byteArrayOf(10, 20, 30)
        val cronetClient = mock<CronetHttpClient>()
        cronetClient.stub {
            on { isAvailable } doReturn true
            onBlocking { get(any(), any(), any()) } doReturn CronetResult.Success(
                code = 200,
                body = sampleData,
                protocol = "h3",
                proxy = null
            )
        }

        val fetcher = InventoryAssetFetcher(cronetClient)
        val latch = CountDownLatch(1)
        var receivedData: ByteArray? = null
        var receivedCode = 0
        var receivedProtocol = ""

        fetcher.fetchInventoryAssetAsync(
            assetId = testAssetId,
            url = testUrl,
            callback = object : CronetAssetFetchCallback {
                override fun onSuccess(assetData: ByteArray, httpStatusCode: Int, protocol: String) {
                    receivedData = assetData
                    receivedCode = httpStatusCode
                    receivedProtocol = protocol
                    latch.countDown()
                }

                override fun onFailure(statusCode: Int?, errorMessage: String) {
                    latch.countDown()
                }
            }
        )

        assertTrue(latch.await(5, TimeUnit.SECONDS))
        assertArrayEquals(sampleData, receivedData)
        assertEquals(200, receivedCode)
        assertEquals("h3", receivedProtocol)
    }

    @Test
    fun testFallbackWhenCronetUninitialized() = runTest {
        val testAssetId = UUID.randomUUID()
        val cronetClient = mock<CronetHttpClient> {
            on { isAvailable } doReturn false
        }

        val fetcher = InventoryAssetFetcher(cronetClient)
        val result = fetcher.fetchAssetWithResult(testAssetId, "https://invalid.local/asset")

        assertTrue(result is AssetFetchResult.Failure)
        val failure = result as AssetFetchResult.Failure
        assertEquals(testAssetId, failure.assetId)
    }
}
