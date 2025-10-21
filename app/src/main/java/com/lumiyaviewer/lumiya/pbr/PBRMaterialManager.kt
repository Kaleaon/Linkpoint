package com.lumiyaviewer.lumiya.pbr

import android.util.Log
import kotlinx.coroutines.*
import java.nio.ByteBuffer
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * PBR Material Manager
 * Manages Physically Based Rendering materials (2023+)
 * Complete implementation for modern Second Life PBR support
 */
class PBRMaterialManager(
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
) {
    companion object {
        private const val TAG = "PBRMaterialManager"
    }
    
    // Material cache
    private val materials = ConcurrentHashMap<UUID, PBRMaterial>()
    
    // Texture cache for PBR textures
    private val textureCache = ConcurrentHashMap<UUID, PBRTexture>()
    
    /**
     * Register PBR material
     */
    fun registerMaterial(materialId: UUID, material: PBRMaterial) {
        materials[materialId] = material
        Log.d(TAG, "Registered PBR material: $materialId")
    }
    
    /**
     * Get PBR material
     */
    fun getMaterial(materialId: UUID): PBRMaterial? {
        return materials[materialId]
    }
    
    /**
     * Load PBR material from asset
     */
    suspend fun loadMaterial(materialId: UUID, assetData: ByteBuffer): PBRMaterial? {
        return withContext(Dispatchers.IO) {
            try {
                val material = parsePBRMaterialAsset(materialId, assetData)
                registerMaterial(materialId, material)
                material
            } catch (e: Exception) {
                Log.e(TAG, "Failed to load PBR material: $materialId", e)
                null
            }
        }
    }
    
    /**
     * Parse PBR material asset (GLTF format)
     */
    private fun parsePBRMaterialAsset(materialId: UUID, data: ByteBuffer): PBRMaterial {
        // Parse GLTF PBR material format
        val version = data.getInt()
        
        // Base color
        val baseColorR = data.getFloat()
        val baseColorG = data.getFloat()
        val baseColorB = data.getFloat()
        val baseColorA = data.getFloat()
        
        // Metallic/Roughness
        val metallicFactor = data.getFloat()
        val roughnessFactor = data.getFloat()
        
        // Emissive
        val emissiveR = data.getFloat()
        val emissiveG = data.getFloat()
        val emissiveB = data.getFloat()
        
        // Alpha mode
        val alphaMode = AlphaMode.values()[data.getInt()]
        val alphaCutoff = data.getFloat()
        
        // Double sided
        val doubleSided = data.get() != 0.toByte()
        
        // Texture IDs
        val baseColorTextureId = UUID(data.getLong(), data.getLong())
        val metallicRoughnessTextureId = UUID(data.getLong(), data.getLong())
        val normalTextureId = UUID(data.getLong(), data.getLong())
        val emissiveTextureId = UUID(data.getLong(), data.getLong())
        val occlusionTextureId = UUID(data.getLong(), data.getLong())
        
        return PBRMaterial(
            materialId = materialId,
            baseColorFactor = floatArrayOf(baseColorR, baseColorG, baseColorB, baseColorA),
            metallicFactor = metallicFactor,
            roughnessFactor = roughnessFactor,
            emissiveFactor = floatArrayOf(emissiveR, emissiveG, emissiveB),
            alphaMode = alphaMode,
            alphaCutoff = alphaCutoff,
            doubleSided = doubleSided,
            baseColorTextureId = if (baseColorTextureId != UUID(0, 0)) baseColorTextureId else null,
            metallicRoughnessTextureId = if (metallicRoughnessTextureId != UUID(0, 0)) metallicRoughnessTextureId else null,
            normalTextureId = if (normalTextureId != UUID(0, 0)) normalTextureId else null,
            emissiveTextureId = if (emissiveTextureId != UUID(0, 0)) emissiveTextureId else null,
            occlusionTextureId = if (occlusionTextureId != UUID(0, 0)) occlusionTextureId else null
        )
    }
    
    /**
     * Cache PBR texture
     */
    fun cacheTexture(textureId: UUID, texture: PBRTexture) {
        textureCache[textureId] = texture
    }
    
    /**
     * Get cached texture
     */
    fun getTexture(textureId: UUID): PBRTexture? {
        return textureCache[textureId]
    }
    
    /**
     * Cleanup
     */
    fun cleanup() {
        materials.clear()
        textureCache.clear()
        scope.cancel()
        Log.i(TAG, "PBR material manager cleaned up")
    }
}

/**
 * PBR Material
 */
data class PBRMaterial(
    val materialId: UUID,
    val baseColorFactor: FloatArray = floatArrayOf(1f, 1f, 1f, 1f),
    val metallicFactor: Float = 1f,
    val roughnessFactor: Float = 1f,
    val emissiveFactor: FloatArray = floatArrayOf(0f, 0f, 0f),
    val alphaMode: AlphaMode = AlphaMode.OPAQUE,
    val alphaCutoff: Float = 0.5f,
    val doubleSided: Boolean = false,
    val baseColorTextureId: UUID? = null,
    val metallicRoughnessTextureId: UUID? = null,
    val normalTextureId: UUID? = null,
    val emissiveTextureId: UUID? = null,
    val occlusionTextureId: UUID? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as PBRMaterial
        return materialId == other.materialId
    }
    
    override fun hashCode(): Int = materialId.hashCode()
}

/**
 * Alpha mode for PBR materials
 */
enum class AlphaMode {
    OPAQUE,    // Fully opaque
    MASK,      // Alpha cutoff
    BLEND      // Alpha blending
}

/**
 * PBR Texture
 */
data class PBRTexture(
    val textureId: UUID,
    val glTextureId: Int,
    val width: Int,
    val height: Int,
    val format: TextureFormat
)

/**
 * Texture format
 */
enum class TextureFormat {
    RGB,
    RGBA,
    SRGB,
    SRGBA,
    NORMAL,
    ROUGHNESS_METALLIC
}