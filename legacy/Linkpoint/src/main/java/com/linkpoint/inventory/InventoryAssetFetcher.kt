package com.linkpoint.inventory

import android.content.Context
import android.util.Log
import com.linkpoint.assets.AssetType
import com.linkpoint.network.adapters.ApacheHttpAdapter
import com.linkpoint.network.CronetHttpClient
import com.linkpoint.network.CronetResult
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.File
import java.io.IOException
import java.util.UUID
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.math.min

/**
 * Callback for non-blocking asynchronous asset fetch operations.
 */
interface AssetFetchCallback {
    fun onSuccess(assetId: UUID, assetType: Int, data: ByteArray, fromCache: Boolean)
    fun onFailure(assetId: UUID, assetType: Int, error: Throwable)
}

/**
 * Callback interface for Cronet-style asynchronous inventory asset fetching.
 */
interface CronetAssetFetchCallback {
    fun onSuccess(assetData: ByteArray, httpStatusCode: Int, protocol: String)
    fun onFailure(statusCode: Int?, errorMessage: String)
}

/**
 * Result data class for inventory asset requests.
 */
sealed class AssetFetchResult {
    data class Success(
        val assetId: UUID,
        val data: ByteArray,
        val httpStatusCode: Int,
        val protocol: String
    ) : AssetFetchResult() {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            other as Success
            return assetId == other.assetId && data.contentEquals(other.data) && httpStatusCode == other.httpStatusCode && protocol == other.protocol
        }

        override fun hashCode(): Int {
            var result = assetId.hashCode()
            result = 31 * result + data.contentHashCode()
            result = 31 * result + httpStatusCode
            result = 31 * result + protocol.hashCode()
            return result
        }
    }

    data class Failure(
        val assetId: UUID,
        val statusCode: Int?,
        val errorMessage: String
    ) : AssetFetchResult()
}

/**
 * Asynchronous, non-blocking Inventory Asset Fetcher.
 * Dispatches asset queries through Cronet asynchronous UrlRequest callback interfaces when available,
 * and dynamically falls back to standard HTTP connection transport with exponential backoff retry policies.
 */
class InventoryAssetFetcher @JvmOverloads constructor(
    private val httpClient: OkHttpClient = defaultOkHttpClient(),
    private val maxRetries: Int = 3,
    private val initialBackoffMs: Long = 200L,
    private val maxBackoffMs: Long = 3000L,
    private val sleeper: suspend (Long) -> Unit = { delay(it) },
    private val cronetClient: CronetHttpClient? = null
) {
    constructor(cronetClient: CronetHttpClient, fallbackClient: OkHttpClient = defaultOkHttpClient()) : this(
        httpClient = fallbackClient,
        maxRetries = 3,
        initialBackoffMs = 200L,
        maxBackoffMs = 3000L,
        sleeper = { delay(it) },
        cronetClient = cronetClient
    )

    constructor(context: Context) : this(
        cronetClient = CronetHttpClient.getOrCreate(context)
    )

    companion object {
        private const val TAG = "InventoryAssetFetcher"

        @JvmStatic
        fun defaultOkHttpClient(cacheDir: File? = null): OkHttpClient {
            return ApacheHttpAdapter.create(cacheDir).okHttpClient
        }
    }

    private val fetchScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    /**
     * Executes an HTTP GET asset request asynchronously using non-blocking call callbacks.
     * Never stalls worker threads or blocks background thread pools.
     */
    fun fetchAssetAsync(
        assetId: UUID,
        assetUrl: String,
        assetType: Int = AssetType.UNKNOWN.value,
        headers: Map<String, String> = emptyMap(),
        callback: AssetFetchCallback
    ) {
        fetchScope.launch {
            try {
                val data = fetchAsset(assetId, assetUrl, assetType, headers)
                if (data != null) {
                    callback.onSuccess(assetId, assetType, data, fromCache = false)
                } else {
                    callback.onFailure(
                        assetId,
                        assetType,
                        IOException("Failed to fetch asset $assetId after $maxRetries retries")
                    )
                }
            } catch (e: Throwable) {
                if (e !is CancellationException) {
                    callback.onFailure(assetId, assetType, e)
                }
            }
        }
    }

    /**
     * Dispatches an inventory asset query asynchronously through Cronet / fallback callback interfaces.
     */
    fun fetchInventoryAssetAsync(
        assetId: UUID,
        url: String,
        headers: Map<String, String> = emptyMap(),
        callback: CronetAssetFetchCallback
    ) {
        fetchScope.launch {
            val result = fetchAssetWithResult(assetId, url, headers)
            when (result) {
                is AssetFetchResult.Success -> callback.onSuccess(result.data, result.httpStatusCode, result.protocol)
                is AssetFetchResult.Failure -> callback.onFailure(result.statusCode, result.errorMessage)
            }
        }
    }

    /**
     * Suspending non-blocking asset fetcher with Cronet transport and bounded exponential backoff retries.
     */
    suspend fun fetchAsset(
        assetId: UUID,
        assetUrl: String,
        assetType: Int = AssetType.UNKNOWN.value,
        headers: Map<String, String> = emptyMap()
    ): ByteArray? {
        if (cronetClient != null && cronetClient.isAvailable) {
            try {
                Log.d(TAG, "Dispatching async Cronet UrlRequest for inventory asset $assetId ($assetUrl)")
                val cronetResult = cronetClient.get(assetUrl, headers)
                when (cronetResult) {
                    is CronetResult.Success -> {
                        if (cronetResult.code in 200..299) {
                            Log.d(TAG, "✓ Inventory asset $assetId fetched via Cronet/${cronetResult.protocol} (${cronetResult.body.size} bytes)")
                            return cronetResult.body
                        } else {
                            Log.w(TAG, "Cronet returned HTTP ${cronetResult.code} for inventory asset $assetId. Falling back to standard HTTP transport.")
                        }
                    }
                    is CronetResult.Failure -> {
                        Log.w(TAG, "Cronet UrlRequest failed for inventory asset $assetId (${cronetResult.message}). Falling back to standard HTTP transport.")
                    }
                    is CronetResult.Cancelled -> {
                        Log.w(TAG, "Cronet UrlRequest cancelled for inventory asset $assetId. Falling back to standard HTTP transport.")
                    }
                    is CronetResult.EngineUnavailable -> {
                        Log.i(TAG, "Cronet engine unavailable for asset $assetId. Falling back to standard HTTP transport.")
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Cronet execution error for asset $assetId: ${e.message}. Falling back to standard HTTP transport.", e)
            }
        }

        return fetchAssetWithRetry(assetId, assetUrl, headers)
    }

    /**
     * Suspending fetch function returning detailed [AssetFetchResult].
     */
    suspend fun fetchAssetWithResult(
        assetId: UUID,
        url: String,
        headers: Map<String, String> = emptyMap(),
        timeoutMs: Long = 15_000L
    ): AssetFetchResult {
        if (cronetClient != null && cronetClient.isAvailable) {
            try {
                Log.d(TAG, "Dispatching async Cronet UrlRequest for inventory asset $assetId ($url)")
                val cronetResult = cronetClient.get(url, headers, timeoutMs)
                when (cronetResult) {
                    is CronetResult.Success -> {
                        if (cronetResult.code in 200..299) {
                            Log.d(TAG, "✓ Inventory asset $assetId fetched via Cronet/${cronetResult.protocol} (${cronetResult.body.size} bytes)")
                            return AssetFetchResult.Success(
                                assetId = assetId,
                                data = cronetResult.body,
                                httpStatusCode = cronetResult.code,
                                protocol = cronetResult.protocol
                            )
                        } else {
                            Log.w(TAG, "Cronet returned HTTP ${cronetResult.code} for inventory asset $assetId. Falling back to standard HTTP transport.")
                        }
                    }
                    is CronetResult.Failure -> {
                        Log.w(TAG, "Cronet UrlRequest failed for inventory asset $assetId (${cronetResult.message}). Falling back to standard HTTP transport.")
                    }
                    is CronetResult.Cancelled -> {
                        Log.w(TAG, "Cronet UrlRequest cancelled for inventory asset $assetId. Falling back to standard HTTP transport.")
                    }
                    is CronetResult.EngineUnavailable -> {
                        Log.i(TAG, "Cronet engine unavailable for asset $assetId. Falling back to standard HTTP transport.")
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Cronet execution error for asset $assetId: ${e.message}. Falling back to standard HTTP transport.", e)
            }
        } else {
            Log.d(TAG, "Cronet engine uninitialized/unavailable. Fetching inventory asset $assetId via standard HTTP fallback.")
        }

        return fetchViaStandardHttpFallback(assetId, url, headers)
    }

    private suspend fun fetchViaStandardHttpFallback(
        assetId: UUID,
        url: String,
        headers: Map<String, String>
    ): AssetFetchResult {
        return try {
            val requestBuilder = Request.Builder().url(url)
            headers.forEach { (k, v) -> requestBuilder.addHeader(k, v) }

            val response = httpClient.newCall(requestBuilder.build()).awaitCall()
            response.use { resp ->
                if (resp.isSuccessful) {
                    val bodyBytes = resp.body?.bytes() ?: ByteArray(0)
                    Log.d(TAG, "✓ Inventory asset $assetId fetched via standard HTTP fallback (${bodyBytes.size} bytes)")
                    AssetFetchResult.Success(
                        assetId = assetId,
                        data = bodyBytes,
                        httpStatusCode = resp.code,
                        protocol = resp.protocol.toString()
                    )
                } else {
                    Log.e(TAG, "Standard HTTP fallback failed for asset $assetId with HTTP ${resp.code}")
                    AssetFetchResult.Failure(
                        assetId = assetId,
                        statusCode = resp.code,
                        errorMessage = "HTTP ${resp.code} error from fallback transport"
                    )
                }
            }
        } catch (e: IOException) {
            Log.e(TAG, "Standard HTTP fallback IO error for asset $assetId: ${e.message}", e)
            AssetFetchResult.Failure(
                assetId = assetId,
                statusCode = null,
                errorMessage = e.message ?: "Network IO exception"
            )
        }
    }

    private suspend fun fetchAssetWithRetry(
        assetId: UUID,
        assetUrl: String,
        headers: Map<String, String>
    ): ByteArray? {
        var currentDelay = initialBackoffMs
        var attempt = 0

        while (attempt <= maxRetries) {
            attempt++
            try {
                val requestBuilder = Request.Builder().url(assetUrl)
                headers.forEach { (k, v) -> requestBuilder.addHeader(k, v) }

                val call = httpClient.newCall(requestBuilder.build())
                val response = call.awaitCall()

                response.use { resp ->
                    if (resp.isSuccessful) {
                        return resp.body?.bytes()
                    }

                    // Retry on 5xx server errors or 429 Too Many Requests
                    val code = resp.code
                    if (code in 500..599 || code == 429) {
                        logWarning(TAG, "Fetch attempt $attempt for asset $assetId returned HTTP $code, retrying in ${currentDelay}ms")
                    } else {
                        // Non-retryable HTTP error (e.g., 404, 401, 403)
                        logError(TAG, "Non-retryable HTTP $code fetching asset $assetId from $assetUrl")
                        return null
                    }
                }
            } catch (e: IOException) {
                logWarning(TAG, "Fetch attempt $attempt failed for asset $assetId: ${e.message}")
                if (attempt > maxRetries) {
                    throw e
                }
            }

            if (attempt <= maxRetries) {
                sleeper(currentDelay)
                currentDelay = min(currentDelay * 2, maxBackoffMs)
            }
        }

        return null
    }

    private suspend fun Call.awaitCall(): Response = suspendCancellableCoroutine { continuation ->
        enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                continuation.resume(response)
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

    private fun logWarning(tag: String, msg: String) {
        try {
            Log.w(tag, msg)
        } catch (_: Throwable) {
            println("[$tag WARN] $msg")
        }
    }

    private fun logError(tag: String, msg: String) {
        try {
            Log.e(tag, msg)
        } catch (_: Throwable) {
            println("[$tag ERROR] $msg")
        }
    }
}
