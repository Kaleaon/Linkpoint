package com.linkpoint.inventory

import com.linkpoint.network.CronetHttpClient
import com.linkpoint.network.CronetResult
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
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

    private val testAssetId: UUID = UUID.randomUUID()
    private val testUrl = "https://asset-cdn.glb.agni.lindenlab.com/asset/$testAssetId"

    @Test
    fun testFetchAssetViaCronetSuccess() = runTest {
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
        val result = fetcher.fetchAsset(testAssetId, testUrl)

        assertTrue(result is AssetFetchResult.Success)
        val success = result as AssetFetchResult.Success
        assertEquals(testAssetId, success.assetId)
        assertArrayEquals(sampleData, success.data)
        assertEquals(200, success.httpStatusCode)
        assertEquals("h3", success.protocol)
    }

    @Test
    fun testAsyncFetchCallbackDispatched() {
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
            callback = object : AssetFetchCallback {
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

        assertTrue(latch.await(3, TimeUnit.SECONDS))
        assertArrayEquals(sampleData, receivedData)
        assertEquals(200, receivedCode)
        assertEquals("h3", receivedProtocol)
    }

    @Test
    fun testFallbackWhenCronetUninitialized() = runTest {
        val cronetClient = mock<CronetHttpClient> {
            on { isAvailable } doReturn false
        }

        val fetcher = InventoryAssetFetcher(cronetClient)
        // Calling fetchAsset with invalid URL will attempt fallback and return Failure (since server isn't running),
        // proving that it gracefully tried the fallback path rather than crashing or hanging on Cronet.
        val result = fetcher.fetchAsset(testAssetId, "https://invalid.local/asset")

        assertTrue(result is AssetFetchResult.Failure)
        val failure = result as AssetFetchResult.Failure
        assertEquals(testAssetId, failure.assetId)
    }
}
