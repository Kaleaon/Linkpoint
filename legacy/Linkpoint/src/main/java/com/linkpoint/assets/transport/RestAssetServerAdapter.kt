package com.linkpoint.assets.transport

import android.util.Log
import com.linkpoint.assets.AssetType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.UUID
import java.util.concurrent.TimeUnit

/**
 * Transport adapter for downloading assets via OpenSim REST Asset Server endpoints.
 */
class RestAssetServerAdapter(
    @Volatile var assetServerUrl: String? = null,
    private val httpClient: OkHttpClient = createDefaultHttpClient()
) : AssetFetcherAdapter {

    override val name: String = "RestAssetServerAdapter"
    override val priority: Int = 20

    companion object {
        private const val TAG = "RestAssetServerAdapter"

        private fun createDefaultHttpClient(): OkHttpClient {
            return OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .followRedirects(true)
                .build()
        }

        private fun logD(msg: String) {
            try { Log.d(TAG, msg) } catch (_: Throwable) {}
        }

        private fun logW(msg: String) {
            try { Log.w(TAG, msg) } catch (_: Throwable) {}
        }

        private fun logE(msg: String, e: Throwable? = null) {
            try { Log.e(TAG, msg, e) } catch (_: Throwable) {}
        }
    }

    override fun canFetch(assetType: AssetType): Boolean {
        return !assetServerUrl.isNullOrBlank()
    }

    override suspend fun fetchAsset(assetId: UUID, assetType: AssetType): ByteArray? = withContext(Dispatchers.IO) {
        val baseUrl = assetServerUrl?.trim()?.trimEnd('/') ?: return@withContext null
        if (baseUrl.isBlank()) return@withContext null

        val candidateUrls = listOf(
            if (baseUrl.endsWith("/assets")) "$baseUrl/$assetId" else "$baseUrl/assets/$assetId",
            "$baseUrl/?asset_id=$assetId",
            "$baseUrl/$assetId"
        ).distinct()

        for (url in candidateUrls) {
            logD("Attempting REST asset fetch for $assetType $assetId at $url")
            val request = Request.Builder()
                .url(url)
                .header("Accept", "*/*")
                .build()

            try {
                httpClient.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        val bytes = response.body?.bytes()
                        if (bytes != null && bytes.isNotEmpty()) {
                            logD("Successfully fetched $assetType $assetId via REST (${bytes.size} bytes)")
                            return@withContext bytes
                        }
                    } else if (response.code == 404) {
                        // Fail fast on HTTP 404 per Constraint 3
                        logW("HTTP 404 Not Found for $assetType $assetId at $url")
                        return@withContext null
                    } else {
                        logW("HTTP ${response.code} fetching $assetType $assetId at $url")
                    }
                }
            } catch (e: Throwable) {
                logE("Error executing REST asset request to $url", e)
            }
        }

        return@withContext null
    }
}
