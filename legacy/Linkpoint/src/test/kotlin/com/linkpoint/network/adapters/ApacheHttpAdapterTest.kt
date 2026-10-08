package com.linkpoint.network.adapters

import kotlinx.coroutines.runBlocking
import okhttp3.Protocol
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException

class ApacheHttpAdapterTest {

    private lateinit var server: MockWebServer
    private lateinit var adapter: ApacheHttpAdapter

    @Before
    fun setUp() {
        server = MockWebServer()
        server.start()
        adapter = ApacheHttpAdapter.create()
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun `adapter configures 15s connect and 30s read timeouts with HTTP2 support`() {
        val client = adapter.okHttpClient
        assertEquals(15_000, client.connectTimeoutMillis)
        assertEquals(30_000, client.readTimeoutMillis)
        assertEquals(30_000, client.writeTimeoutMillis)
        assertTrue(client.protocols.contains(Protocol.HTTP_2))
    }

    @Test
    fun `executeAsync GET returns successful response`() = runBlocking {
        server.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody("""{"status":"ok"}""")
        )

        val baseUrl = server.url("/test/get").toString()
        val response = adapter.getAsync(baseUrl)

        assertTrue(response.isSuccessful)
        assertEquals(200, response.code)
        assertEquals("""{"status":"ok"}""", response.bodyString)

        val recordedRequest = server.takeRequest()
        assertEquals("GET", recordedRequest.method)
        assertEquals("/test/get", recordedRequest.path)
    }

    @Test
    fun `postAsync sends body and receives response`() = runBlocking {
        server.enqueue(
            MockResponse()
                .setResponseCode(201)
                .setBody("""{"created":true}""")
        )

        val baseUrl = server.url("/test/post").toString()
        val payload = """{"name":"item"}""".toByteArray(Charsets.UTF_8)
        val response = adapter.postAsync(baseUrl, bodyBytes = payload)

        assertTrue(response.isSuccessful)
        assertEquals(201, response.code)
        assertEquals("""{"created":true}""", response.bodyString)

        val recordedRequest = server.takeRequest()
        assertEquals("POST", recordedRequest.method)
        assertEquals("""{"name":"item"}""", recordedRequest.body.readUtf8())
    }

    @Test
    fun `downloadBytesAsync returns raw byte array`() = runBlocking {
        val sampleData = byteArrayOf(0x01, 0x02, 0x03, 0x04, 0x05)
        server.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(okio.Buffer().write(sampleData))
        )

        val baseUrl = server.url("/asset/download").toString()
        val bytes = adapter.downloadBytesAsync(baseUrl)

        assertArrayEquals(sampleData, bytes)
    }

    @Test(expected = IOException::class)
    fun `downloadBytesAsync throws IOException on HTTP failure`(): Unit = runBlocking {
        server.enqueue(MockResponse().setResponseCode(404))

        val baseUrl = server.url("/asset/missing").toString()
        adapter.downloadBytesAsync(baseUrl)
    }

    @Test
    fun `executeAsync non-2xx status code is captured in HttpResponse`() = runBlocking {
        server.enqueue(
            MockResponse()
                .setResponseCode(500)
                .setBody("Internal Server Error")
        )

        val baseUrl = server.url("/error").toString()
        val response = adapter.getAsync(baseUrl)

        assertFalse(response.isSuccessful)
        assertEquals(500, response.code)
        assertEquals("Internal Server Error", response.bodyString)
    }
}
