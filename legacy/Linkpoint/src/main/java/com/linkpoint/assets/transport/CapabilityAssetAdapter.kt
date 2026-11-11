package com.linkpoint.assets.transport

import android.util.Log
import com.linkpoint.assets.AssetType
import com.linkpoint.network.SSLHelper
import com.linkpoint.protocol.capabilities.CapabilityManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Request
import java.util.UUID
import java.util.concurrent.TimeUnit

/**
 * Transport adapter for fetching assets over HTTP Capabilities (GetTexture, GetMesh, ViewerAsset).
 */
class CapabilityAssetAdapter(
    private val capabilityManager: CapabilityManager?,
    private val httpClient: OkHttpClient = createDefaultHttpClient()
) : AssetFetcherAdapter {

    override val name: String = "CapabilityAssetAdapter"
    override val priority: Int = 10

    companion object {
        private const val TAG = "CapabilityAssetAdapter"

        private fun createDefaultHttpClient(): OkHttpClient {
            return SSLHelper.configureForCdn(
                OkHttpClient.Builder()
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .protocols(listOf(Protocol.HTTP_2, Protocol.HTTP_1_1))
            ).build()
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
        if (capabilityManager == null) return false
        return when (assetType) {
            AssetType.TEXTURE, AssetType.SNAPSHOT -> resolveTextureCapabilityUrl() != null
            AssetType.MESH -> resolveMeshCapabilityUrl() != null
            else -> capabilityManager.getCapability(CapabilityManager.CAP_VIEWER_ASSET) != null ||
                    resolveTextureCapabilityUrl() != null
        }
    }

    private fun resolveTextureCapabilityUrl(): String? {
        return capabilityManager?.getTextureFetchURL()
            ?: capabilityManager?.getCapability(CapabilityManager.CAP_GET_TEXTURE)
    }

    private fun resolveMeshCapabilityUrl(): String? {
        return capabilityManager?.getMeshFetchURL()
            ?: capabilityManager?.getCapability(CapabilityManager.CAP_GET_MESH2)
            ?: capabilityManager?.getCapability(CapabilityManager.CAP_GET_MESH)
    }

    override suspend fun fetchAsset(assetId: UUID, assetType: AssetType): ByteArray? = withContext(Dispatchers.IO) {
        val baseUrl = when (assetType) {
            AssetType.TEXTURE, AssetType.SNAPSHOT -> resolveTextureCapabilityUrl()
            AssetType.MESH -> resolveMeshCapabilityUrl()
            else -> capabilityManager?.getCapability(CapabilityManager.CAP_VIEWER_ASSET)
                ?: resolveTextureCapabilityUrl()
        } ?: return@withContext null

        val url = buildUrl(baseUrl, assetId, assetType)
        logD("Fetching $assetType $assetId via capability URL: $url")

        val request = Request.Builder()
            .url(url)
            .header("Accept", "*/*")
            .build()

        try {
            httpClient.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val bytes = response.body?.bytes()
                    if (bytes != null && bytes.isNotEmpty()) {
                        logD("Successfully fetched $assetType $assetId (${bytes.size} bytes)")
                        return@withContext bytes
                    }
                } else if (response.code == 404) {
                    // Fail fast on HTTP 404 per Constraint 3
                    logW("HTTP 404 Not Found for $assetType $assetId at capability URL")
                    return@withContext null
                } else {
                    logW("HTTP ${response.code} error fetching $assetType $assetId at capability URL")
                    return@withContext null
                }
            }
        } catch (e: Throwable) {
            logE("Error fetching $assetType $assetId via capability", e)
            return@withContext null
        }
        return@withContext null
    }

    private fun buildUrl(baseUrl: String, assetId: UUID, assetType: AssetType): String {
        val secureUrl = if (baseUrl.startsWith("http://") &&
            (baseUrl.contains(".lindenlab.com") || baseUrl.contains(".secondlife.com"))
        ) {
            baseUrl.replaceFirst("http://", "https://")
        } else {
            baseUrl
        }

        return when {
            secureUrl.contains("?") -> "$secureUrl&asset_id=$assetId"
            assetType == AssetType.TEXTURE || assetType == AssetType.SNAPSHOT -> "$secureUrl?texture_id=$assetId"
            assetType == AssetType.MESH -> "$secureUrl?mesh_id=$assetId"
            else -> "$secureUrl?asset_id=$assetId"
        }
    }
}
