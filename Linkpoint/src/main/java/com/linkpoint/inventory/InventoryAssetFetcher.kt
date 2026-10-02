package com.linkpoint.inventory

import android.util.Log
import com.linkpoint.assets.AssetType
import com.linkpoint.network.adapters.ApacheHttpAdapter
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
 * Asynchronous, non-blocking Inventory Asset Fetcher.
 * Uses an OkHttpClient non-blocking connection pool (with HTTP/2 support and standard caching)
 * and exponential backoff retry policies without stalling worker threads in asset fetcher loops.
 */
class InventoryAssetFetcher @JvmOverloads constructor(
    private val httpClient: OkHttpClient = defaultOkHttpClient(),
    private val maxRetries: Int = 3,
    private val initialBackoffMs: Long = 200L,
    private val maxBackoffMs: Long = 3000L,
    private val sleeper: suspend (Long) -> Unit = { delay(it) }
) {
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
                val data = fetchAssetWithRetry(assetId, assetUrl, headers)
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
     * Suspending non-blocking asset fetcher with bounded exponential backoff retries.
     */
    suspend fun fetchAsset(
        assetId: UUID,
        assetUrl: String,
        assetType: Int = AssetType.UNKNOWN.value,
        headers: Map<String, String> = emptyMap()
    ): ByteArray? {
        return fetchAssetWithRetry(assetId, assetUrl, headers)
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
