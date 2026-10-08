package com.linkpoint.network

import com.linkpoint.inventory.AisHttpRequest
import com.linkpoint.inventory.AisHttpResponse
import com.linkpoint.inventory.AisTransport
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class CronetTransportAdapterTest {

    private class FakeCronetHttpClient(
        private val available: Boolean,
        private val result: CronetResult = CronetResult.EngineUnavailable
    ) : CronetHttpClient(null) {
        override val isAvailable: Boolean get() = available

        override suspend fun execute(
            method: String,
            url: String,
            headers: Map<String, String>,
            body: ByteArray?,
            contentType: String?,
            timeoutMs: Long
        ): CronetResult = result
    }

    private class FakeAisTransport(
        private val response: AisHttpResponse = AisHttpResponse(200, "{}")
    ) : AisTransport {
        var executeCalls = 0
        var lastRequest: AisHttpRequest? = null

        override suspend fun execute(request: AisHttpRequest): AisHttpResponse {
            executeCalls++
            lastRequest = request
            return response
        }
    }

    @Test
    fun testCronetAvailableWhenEngineNonNull() {
        val cronetClient = FakeCronetHttpClient(available = true)
        val fallback = FakeAisTransport()
        val adapter = CronetTransportAdapter(cronetClient, fallback)

        assertTrue(adapter.isCronetAvailable)
    }

    @Test
    fun testCronetUnavailableWhenEngineNull() {
        val cronetClient = FakeCronetHttpClient(available = false)
        val fallback = FakeAisTransport()
        val adapter = CronetTransportAdapter(cronetClient, fallback)

        assertFalse(adapter.isCronetAvailable)
    }

    @Test
    fun testExecutesViaCronetWhenAvailableAndSuccessful() = runTest {
        val cronetClient = FakeCronetHttpClient(
            available = true,
            result = CronetResult.Success(
                code = 200,
                body = "{\"folder_id\":\"1234\"}".toByteArray(Charsets.UTF_8),
                protocol = "h3",
                proxy = null
            )
        )

        val fallback = FakeAisTransport()
        val adapter = CronetTransportAdapter(cronetClient, fallback)

        val request = AisHttpRequest(
            method = "GET",
            url = "https://example.com/category/1234"
        )

        val response = adapter.execute(request)

        assertEquals(200, response.code)
        assertEquals("{\"folder_id\":\"1234\"}", response.body)
        assertEquals(0, fallback.executeCalls)
    }

    @Test
    fun testFallbackToStandardHttpWhenCronetEngineUninitialized() = runTest {
        val cronetClient = FakeCronetHttpClient(available = false)
        val fallback = FakeAisTransport(
            response = AisHttpResponse(200, "{\"fallback\":true}")
        )

        val adapter = CronetTransportAdapter(cronetClient, fallback)

        val request = AisHttpRequest(
            method = "GET",
            url = "https://example.com/category/1234"
        )

        val response = adapter.execute(request)

        assertEquals(200, response.code)
        assertEquals("{\"fallback\":true}", response.body)
        assertEquals(1, fallback.executeCalls)
        assertEquals(request, fallback.lastRequest)
    }

    @Test
    fun testFallbackToStandardHttpWhenCronetFails() = runTest {
        val cronetClient = FakeCronetHttpClient(
            available = true,
            result = CronetResult.Failure(
                message = "Connection reset by peer",
                httpCode = null
            )
        )

        val fallback = FakeAisTransport(
            response = AisHttpResponse(200, "{\"recovered\":true}")
        )

        val adapter = CronetTransportAdapter(cronetClient, fallback)

        val request = AisHttpRequest(
            method = "GET",
            url = "https://example.com/category/1234"
        )

        val response = adapter.execute(request)

        assertEquals(200, response.code)
        assertEquals("{\"recovered\":true}", response.body)
        assertEquals(1, fallback.executeCalls)
        assertEquals(request, fallback.lastRequest)
    }
}
