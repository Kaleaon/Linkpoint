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
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.stub
import org.mockito.kotlin.verify
import org.robolectric.RobolectricTestRunner

import org.mockito.kotlin.anyOrNull

@RunWith(RobolectricTestRunner::class)
class CronetTransportAdapterTest {

    @Test
    fun testCronetAvailableWhenEngineNonNull() {
        val cronetClient = mock<CronetHttpClient> {
            on { isAvailable } doReturn true
        }
        val fallback = mock<AisTransport>()
        val adapter = CronetTransportAdapter(cronetClient, fallback)

        assertTrue(adapter.isCronetAvailable)
    }

    @Test
    fun testCronetUnavailableWhenEngineNull() {
        val cronetClient = mock<CronetHttpClient> {
            on { isAvailable } doReturn false
        }
        val fallback = mock<AisTransport>()
        val adapter = CronetTransportAdapter(cronetClient, fallback)

        assertFalse(adapter.isCronetAvailable)
    }

    @Test
    fun testExecutesViaCronetWhenAvailableAndSuccessful() = runTest {
        val cronetClient = mock<CronetHttpClient>()
        cronetClient.stub {
            on { isAvailable } doReturn true
            onBlocking { execute(any(), any(), any(), anyOrNull(), anyOrNull(), any()) } doReturn CronetResult.Success(
                code = 200,
                body = "{\"folder_id\":\"1234\"}".toByteArray(Charsets.UTF_8),
                protocol = "h3",
                proxy = null
            )
        }

        val fallback = mock<AisTransport>()
        val adapter = CronetTransportAdapter(cronetClient, fallback)

        val request = AisHttpRequest(
            method = "GET",
            url = "https://example.com/category/1234"
        )

        val response = adapter.execute(request)

        assertEquals(200, response.code)
        assertEquals("{\"folder_id\":\"1234\"}", response.body)
    }

    @Test
    fun testFallbackToStandardHttpWhenCronetEngineUninitialized() = runTest {
        val cronetClient = mock<CronetHttpClient> {
            on { isAvailable } doReturn false
        }

        val fallback = mock<AisTransport>()
        fallback.stub {
            onBlocking { execute(any()) } doReturn AisHttpResponse(200, "{\"fallback\":true}")
        }

        val adapter = CronetTransportAdapter(cronetClient, fallback)

        val request = AisHttpRequest(
            method = "GET",
            url = "https://example.com/category/1234"
        )

        val response = adapter.execute(request)

        assertEquals(200, response.code)
        assertEquals("{\"fallback\":true}", response.body)
        verify(fallback).execute(request)
    }

    @Test
    fun testFallbackToStandardHttpWhenCronetFails() = runTest {
        val cronetClient = mock<CronetHttpClient>()
        cronetClient.stub {
            on { isAvailable } doReturn true
            onBlocking { execute(any(), any(), any(), anyOrNull(), anyOrNull(), any()) } doReturn CronetResult.Failure(
                message = "Connection reset by peer",
                httpCode = null
            )
        }

        val fallback = mock<AisTransport>()
        fallback.stub {
            onBlocking { execute(any()) } doReturn AisHttpResponse(200, "{\"recovered\":true}")
        }

        val adapter = CronetTransportAdapter(cronetClient, fallback)

        val request = AisHttpRequest(
            method = "GET",
            url = "https://example.com/category/1234"
        )

        val response = adapter.execute(request)

        assertEquals(200, response.code)
        assertEquals("{\"recovered\":true}", response.body)
        verify(fallback).execute(request)
    }
}
