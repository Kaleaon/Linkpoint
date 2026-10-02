package com.linkpoint.inventory

import android.content.Context
import android.util.Log
import com.linkpoint.network.CronetHttpClient
import com.linkpoint.network.CronetResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.util.UUID

/**
 * Callback interface for asynchronous inventory asset fetching.
 */
interface AssetFetchCallback {
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
 * InventoryAssetFetcher manages non-blocking asynchronous asset queries and download requests.
 *
 * Dispatches asset queries through Cronet asynchronous [org.chromium.net.UrlRequest] callback interfaces
 * without blocking execution threads on lossy mobile networks.
 *
 * Dynamically falls back to standard HTTP connection transport functions when Google Play Services
 * Cronet engine or embedded Cronet is uninitialized or unavailable.
 */
class InventoryAssetFetcher(
    private val cronetClient: CronetHttpClient,
    private val fallbackClient: OkHttpClient = OkHttpClient()
) {

    constructor(context: Context) : this(CronetHttpClient.getOrCreate(context))

    /**
     * Dispatches an inventory asset query asynchronously through Cronet [org.chromium.net.UrlRequest] callbacks.
     *
     * Non-blocking: notifies [callback] on completion without stalling threads.
     */
    fun fetchInventoryAssetAsync(
        assetId: UUID,
        url: String,
        headers: Map<String, String> = emptyMap(),
        callback: AssetFetchCallback
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val result = fetchAsset(assetId, url, headers)
            when (result) {
                is AssetFetchResult.Success -> callback.onSuccess(result.data, result.httpStatusCode, result.protocol)
                is AssetFetchResult.Failure -> callback.onFailure(result.statusCode, result.errorMessage)
            }
        }
    }

    /**
     * Suspending fetch function for coroutine consumers.
     * Dispatches asset queries through Cronet asynchronous UrlRequest interfaces with dynamic fallback.
     */
    suspend fun fetchAsset(
        assetId: UUID,
        url: String,
        headers: Map<String, String> = emptyMap(),
        timeoutMs: Long = 15_000L
    ): AssetFetchResult {
        if (cronetClient.isAvailable) {
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

        // Dynamic fallback to standard HTTP transport (OkHttp / HttpURLConnection)
        return fetchViaStandardHttpFallback(assetId, url, headers)
    }

    private fun fetchViaStandardHttpFallback(
        assetId: UUID,
        url: String,
        headers: Map<String, String>
    ): AssetFetchResult {
        return try {
            val requestBuilder = Request.Builder().url(url)
            headers.forEach { (k, v) -> requestBuilder.addHeader(k, v) }

            fallbackClient.newCall(requestBuilder.build()).execute().use { response ->
                if (response.isSuccessful) {
                    val bodyBytes = response.body?.bytes() ?: ByteArray(0)
                    Log.d(TAG, "✓ Inventory asset $assetId fetched via standard HTTP fallback (${bodyBytes.size} bytes)")
                    AssetFetchResult.Success(
                        assetId = assetId,
                        data = bodyBytes,
                        httpStatusCode = response.code,
                        protocol = response.protocol.toString()
                    )
                } else {
                    Log.e(TAG, "Standard HTTP fallback failed for asset $assetId with HTTP ${response.code}")
                    AssetFetchResult.Failure(
                        assetId = assetId,
                        statusCode = response.code,
                        errorMessage = "HTTP ${response.code} error from fallback transport"
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

    companion object {
        private const val TAG = "InventoryAssetFetcher"
    }
}
