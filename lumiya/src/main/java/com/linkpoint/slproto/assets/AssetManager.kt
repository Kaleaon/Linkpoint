package com.linkpoint.slproto.assets

import com.linkpoint.slproto.caps.CapsManager
import com.linkpoint.slproto.llsd.*
import kotlinx.coroutines.*
import okhttp3.*
import java.io.File
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * Asset management system for textures, sounds, animations, etc.
 */
class AssetManager(
    private val capsManager: CapsManager,
    private val cacheDir: File,
) {
    private val assetCache = ConcurrentHashMap<UUID, Asset>()
    private val downloadQueue = ConcurrentHashMap<UUID, Job>()
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val httpClient =
        OkHttpClient.Builder()
            .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
            .build()

    companion object {
        // Asset types
        const val ASSET_TYPE_TEXTURE = 0
        const val ASSET_TYPE_SOUND = 1
        const val ASSET_TYPE_CALLINGCARD = 2
        const val ASSET_TYPE_LANDMARK = 3
        const val ASSET_TYPE_SCRIPT = 4
        const val ASSET_TYPE_CLOTHING = 5
        const val ASSET_TYPE_OBJECT = 6
        const val ASSET_TYPE_NOTECARD = 7
        const val ASSET_TYPE_LSL_TEXT = 10
        const val ASSET_TYPE_LSL_BYTECODE = 11
        const val ASSET_TYPE_TEXTURE_TGA = 12
        const val ASSET_TYPE_BODYPART = 13
        const val ASSET_TYPE_ANIMATION = 20
        const val ASSET_TYPE_GESTURE = 21
        const val ASSET_TYPE_MESH = 49
        const val ASSET_TYPE_SETTINGS = 56
        const val ASSET_TYPE_MATERIAL = 57
    }

    init {
        // Create cache directory if it doesn't exist
        if (!cacheDir.exists()) {
            cacheDir.mkdirs()
        }
    }

    /**
     * Request an asset
     */
    suspend fun requestAsset(
        assetId: UUID,
        assetType: Int,
        priority: Int = 0,
    ): Asset? =
        withContext(Dispatchers.IO) {
            // Check cache first
            assetCache[assetId]?.let { return@withContext it }

            // Check disk cache
            val cacheFile = File(cacheDir, "$assetId.asset")
            if (cacheFile.exists()) {
                val data = cacheFile.readBytes()
                val asset = Asset(assetId, assetType, data)
                assetCache[assetId] = asset
                return@withContext asset
            }

            // Download from server
            downloadAsset(assetId, assetType, priority)
        }

    /**
     * Download asset from server
     */
    private suspend fun downloadAsset(
        assetId: UUID,
        assetType: Int,
        priority: Int,
    ): Asset? =
        withContext(Dispatchers.IO) {
            // Check if already downloading
            if (downloadQueue.containsKey(assetId)) {
                downloadQueue[assetId]?.join()
                return@withContext assetCache[assetId]
            }

            val job =
                async {
                    try {
                        val data =
                            when (assetType) {
                                ASSET_TYPE_TEXTURE, ASSET_TYPE_TEXTURE_TGA -> downloadTexture(assetId)
                                ASSET_TYPE_MESH -> downloadMesh(assetId)
                                else -> downloadGenericAsset(assetId, assetType)
                            }

                        if (data != null) {
                            val asset = Asset(assetId, assetType, data)
                            assetCache[assetId] = asset

                            // Save to disk cache
                            val cacheFile = File(cacheDir, "$assetId.asset")
                            cacheFile.writeBytes(data)

                            asset
                        } else {
                            null
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        null
                    } finally {
                        downloadQueue.remove(assetId)
                    }
                }

            downloadQueue[assetId] = job
            job.await()
        }

    /**
     * Download texture via GetTexture capability
     */
    private suspend fun downloadTexture(textureId: UUID): ByteArray? =
        withContext(Dispatchers.IO) {
            val getTextureUrl =
                capsManager.getCapability(CapsManager.CAP_GET_TEXTURE)
                    ?: return@withContext null

            val url = "$getTextureUrl?texture_id=$textureId"

            val request =
                Request.Builder()
                    .url(url)
                    .get()
                    .build()

            val response = httpClient.newCall(request).execute()

            if (response.isSuccessful) {
                response.body?.bytes()
            } else {
                null
            }
        }

    /**
     * Download mesh via GetMesh capability
     */
    private suspend fun downloadMesh(meshId: UUID): ByteArray? =
        withContext(Dispatchers.IO) {
            val getMeshUrl =
                capsManager.getCapability(CapsManager.CAP_GET_MESH2)
                    ?: capsManager.getCapability(CapsManager.CAP_GET_MESH)
                    ?: return@withContext null

            val url = "$getMeshUrl?mesh_id=$meshId"

            val request =
                Request.Builder()
                    .url(url)
                    .get()
                    .build()

            val response = httpClient.newCall(request).execute()

            if (response.isSuccessful) {
                response.body?.bytes()
            } else {
                null
            }
        }

    /**
     * Download generic asset
     */
    private suspend fun downloadGenericAsset(
        assetId: UUID,
        assetType: Int,
    ): ByteArray? =
        withContext(Dispatchers.IO) {
            // For generic assets, we might need to use UDP transfer or other methods
            // This is a placeholder for now
            null
        }

    /**
     * Upload an asset
     */
    suspend fun uploadAsset(
        data: ByteArray,
        assetType: Int,
        name: String,
        description: String,
    ): UUID? =
        withContext(Dispatchers.IO) {
            try {
                val assetTypeString = getAssetTypeString(assetType)
                val response = capsManager.uploadAsset(assetTypeString, data, name, description)

                if (response is LLSDMap) {
                    val assetIdStr = (response["new_asset"] as? LLSDString)?.value
                    if (assetIdStr != null) {
                        UUID.fromString(assetIdStr)
                    } else {
                        null
                    }
                } else {
                    null
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

    /**
     * Get asset type string for CAPS
     */
    private fun getAssetTypeString(assetType: Int): String {
        return when (assetType) {
            ASSET_TYPE_TEXTURE -> "texture"
            ASSET_TYPE_SOUND -> "sound"
            ASSET_TYPE_ANIMATION -> "animation"
            ASSET_TYPE_MESH -> "mesh"
            ASSET_TYPE_MATERIAL -> "material"
            else -> "unknown"
        }
    }

    /**
     * Clear cache
     */
    fun clearCache() {
        assetCache.clear()
        cacheDir.listFiles()?.forEach { it.delete() }
    }

    /**
     * Cleanup
     */
    fun cleanup() {
        scope.cancel()
    }
}

/**
 * Asset data class
 */
data class Asset(
    val assetId: UUID,
    val assetType: Int,
    val data: ByteArray,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Asset) return false
        if (assetId != other.assetId) return false
        if (assetType != other.assetType) return false
        if (!data.contentEquals(other.data)) return false
        return true
    }

    override fun hashCode(): Int {
        var result = assetId.hashCode()
        result = 31 * result + assetType
        result = 31 * result + data.contentHashCode()
        return result
    }
}
