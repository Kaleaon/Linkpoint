package com.linkpoint.network.adapters

import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.Call
import okhttp3.Callback
import okhttp3.ConnectionPool
import okhttp3.Headers.Companion.toHeaders
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Modernized HTTP transport adapter replacing legacy Apache HttpAdapter and HttpClient usages.
 * Uses an asynchronous non-blocking OkHttpClient connection pool with HTTP/2 support,
 * connection pooling, and configured timeout policies.
 */
class ApacheHttpAdapter private constructor(
    val okHttpClient: OkHttpClient
) {
    companion object {
        private const val CONNECT_TIMEOUT_SECONDS = 15L
        private const val READ_TIMEOUT_SECONDS = 30L
        private const val WRITE_TIMEOUT_SECONDS = 30L

        /**
         * Creates a new instance of ApacheHttpAdapter configured with standard OkHttp settings:
         * 15s connect timeout, 30s read/write timeout, HTTP/2 connection pooling, and optional caching.
         */
        @JvmStatic
        @JvmOverloads
        fun create(
            cacheDir: File? = null,
            cacheSizeBytes: Long = 20L * 1024L * 1024L,
            customClientBuilder: (OkHttpClient.Builder.() -> Unit)? = null
        ): ApacheHttpAdapter {
            val builder = OkHttpClient.Builder()
                .connectTimeout(CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .readTimeout(READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .writeTimeout(WRITE_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .connectionPool(ConnectionPool(10, 5, TimeUnit.MINUTES))
                .protocols(listOf(Protocol.HTTP_2, Protocol.HTTP_1_1))
                .retryOnConnectionFailure(true)

            if (cacheDir != null) {
                try {
                    val cache = okhttp3.Cache(cacheDir, cacheSizeBytes)
                    builder.cache(cache)
                } catch (_: Exception) {
                    // Ignore cache creation error if directory creation fails
                }
            }

            customClientBuilder?.invoke(builder)

            return ApacheHttpAdapter(builder.build())
        }

        /**
         * Wraps an existing OkHttpClient instance in an ApacheHttpAdapter abstraction.
         */
        @JvmStatic
        fun fromClient(client: OkHttpClient): ApacheHttpAdapter {
            return ApacheHttpAdapter(client)
        }
    }

    /**
     * Response container for asynchronous transport operations.
     */
    data class HttpResponse(
        val code: Int,
        val isSuccessful: Boolean,
        val headers: Map<String, List<String>>,
        val bodyBytes: ByteArray,
        val bodyString: String
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            other as HttpResponse
            if (code != other.code) return false
            if (isSuccessful != other.isSuccessful) return false
            if (!bodyBytes.contentEquals(other.bodyBytes)) return false
            return true
        }

        override fun hashCode(): Int {
            var result = code
            result = 31 * result + isSuccessful.hashCode()
            result = 31 * result + bodyBytes.contentHashCode()
            return result
        }
    }

    /**
     * Executes an HTTP request asynchronously using non-blocking OkHttp callbacks.
     */
    suspend fun executeAsync(
        method: String,
        url: String,
        headers: Map<String, String> = emptyMap(),
        bodyBytes: ByteArray? = null,
        contentType: String? = "application/json"
    ): HttpResponse {
        val requestBuilder = Request.Builder().url(url)

        if (headers.isNotEmpty()) {
            requestBuilder.headers(headers.toHeaders())
        }

        val mediaType = contentType?.toMediaType()
        val requestBody = bodyBytes?.toRequestBody(mediaType)

        when (method.uppercase()) {
            "GET" -> requestBuilder.get()
            "POST" -> requestBuilder.post(requestBody ?: ByteArray(0).toRequestBody(mediaType))
            "PUT" -> requestBuilder.put(requestBody ?: ByteArray(0).toRequestBody(mediaType))
            "PATCH" -> requestBuilder.patch(requestBody ?: ByteArray(0).toRequestBody(mediaType))
            "DELETE" -> if (requestBody != null) requestBuilder.delete(requestBody) else requestBuilder.delete()
            "HEAD" -> requestBuilder.head()
            else -> requestBuilder.method(method.uppercase(), requestBody)
        }

        val call = okHttpClient.newCall(requestBuilder.build())
        return call.awaitAndProcess()
    }

    /**
     * Non-blocking GET request helper.
     */
    suspend fun getAsync(
        url: String,
        headers: Map<String, String> = emptyMap()
    ): HttpResponse = executeAsync("GET", url, headers)

    /**
     * Non-blocking POST request helper.
     */
    suspend fun postAsync(
        url: String,
        headers: Map<String, String> = emptyMap(),
        bodyBytes: ByteArray,
        contentType: String = "application/json"
    ): HttpResponse = executeAsync("POST", url, headers, bodyBytes, contentType)

    /**
     * Non-blocking asset download returning raw bytes.
     */
    suspend fun downloadBytesAsync(
        url: String,
        headers: Map<String, String> = emptyMap()
    ): ByteArray {
        val response = getAsync(url, headers)
        if (!response.isSuccessful) {
            throw IOException("HTTP ${response.code} error fetching $url")
        }
        return response.bodyBytes
    }

    private suspend fun Call.awaitAndProcess(): HttpResponse = suspendCancellableCoroutine { continuation ->
        enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                try {
                    response.use { resp ->
                        val bytes = resp.body?.bytes() ?: ByteArray(0)
                        val str = String(bytes, Charsets.UTF_8)
                        val headerMap = resp.headers.toMultimap()

                        val result = HttpResponse(
                            code = resp.code,
                            isSuccessful = resp.isSuccessful,
                            headers = headerMap,
                            bodyBytes = bytes,
                            bodyString = str
                        )
                        continuation.resume(result)
                    }
                } catch (e: Throwable) {
                    continuation.resumeWithException(e)
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                if (!continuation.isCancelled) {
                    continuation.resumeWithException(e)
                }
            }
        })

        continuation.invokeOnCancellation {
            try {
                cancel()
            } catch (_: Throwable) {}
        }
    }
}
