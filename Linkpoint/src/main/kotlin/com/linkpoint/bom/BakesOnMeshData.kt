package com.linkpoint.bom

import java.util.UUID

/**
 * Bakes on Mesh (BoM) Data Structures
 * Implements Second Life Bakes on Mesh system (2018+)
 * 
 * BoM allows server-baked textures to be applied to mesh bodies/heads,
 * enabling clothing layers, tattoos, and makeup on mesh avatars.
 */

/**
 * BoM Bake Types
 * Different texture layers that can be baked onto mesh
 */
enum class BomBakeType(val index: Int, val layerName: String) {
    // Classic avatar bakes
    HEAD(0, "head"),
    UPPER_BODY(1, "upper"),
    LOWER_BODY(2, "lower"),
    EYES(3, "eyes"),
    SKIRT(4, "skirt"),
    HAIR(5, "hair"),
    
    // BoM-specific bakes for mesh bodies
    LEFT_ARM(6, "leftarm"),
    LEFT_LEG(7, "leftleg"),
    AUX1(8, "aux1"),
    AUX2(9, "aux2"),
    AUX3(10, "aux3")
    
    companion object {
        fun fromIndex(index: Int): BomBakeType? {
            return values().find { it.index == index }
        }
        
        fun fromLayerName(name: String): BomBakeType? {
            return values().find { it.layerName == name }
        }
    }
}

/**
 * BoM Texture Layer
 * Individual clothing/tattoo layer in the baking system
 */
data class BomTextureLayer(
    val layerId: UUID,
    val bakeType: BomBakeType,
    val textureId: UUID,
    val alpha: Float = 1.0f,
    val blendMode: BlendMode = BlendMode.NORMAL,
    val order: Int = 0
) {
    enum class BlendMode {
        NORMAL,
        MULTIPLY,
        SCREEN,
        OVERLAY,
        ADD
    }
}

/**
 * BoM Bake Request
 * Request to bake textures on server
 */
data class BomBakeRequest(
    val agentId: UUID,
    val bakeType: BomBakeType,
    val layers: List<BomTextureLayer>,
    val hash: String
) {
    /**
     * Calculate hash of layers for caching
     */
    companion object {
        fun calculateHash(layers: List<BomTextureLayer>): String {
            val combined = layers.sortedBy { it.order }
                .joinToString("|") { "${it.textureId}:${it.alpha}:${it.blendMode}" }
            return combined.hashCode().toString(16)
        }
    }
}

/**
 * BoM Bake Result
 * Result of server-side baking
 */
data class BomBakeResult(
    val bakeType: BomBakeType,
    val bakedTextureId: UUID,
    val hash: String,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * BoM Avatar Configuration
 * Complete BoM setup for an avatar
 */
data class BomAvatarConfig(
    val avatarId: UUID,
    val bakes: Map<BomBakeType, BomBakeResult> = emptyMap(),
    val layers: Map<BomBakeType, List<BomTextureLayer>> = emptyMap()
) {
    /**
     * Get baked texture for a bake type
     */
    fun getBakedTexture(bakeType: BomBakeType): UUID? {
        return bakes[bakeType]?.bakedTextureId
    }
    
    /**
     * Check if bake is up to date
     */
    fun isBakeValid(bakeType: BomBakeType): Boolean {
        val bake = bakes[bakeType] ?: return false
        val currentLayers = layers[bakeType] ?: emptyList()
        val currentHash = BomBakeRequest.calculateHash(currentLayers)
        return bake.hash == currentHash
    }
    
    /**
     * Get layers that need rebaking
     */
    fun getStaleB

akes(): List<BomBakeType> {
        return BomBakeType.values().filter { !isBakeValid(it) }
    }
}

/**
 * BoM Mesh Attachment Point
 * Specifies which BoM bake to apply to which mesh
 */
data class BomMeshAttachment(
    val meshId: UUID,
    val attachmentPoint: String,
    val bakeType: BomBakeType,
    val uvScale: Pair<Float, Float> = Pair(1.0f, 1.0f),
    val uvOffset: Pair<Float, Float> = Pair(0.0f, 0.0f)
)

/**
 * BoM Wearable Item
 * Clothing or attachment that uses BoM
 */
data class BomWearableItem(
    val itemId: UUID,
    val name: String,
    val wearableType: WearableType,
    val layers: List<BomTextureLayer>,
    val meshAttachments: List<BomMeshAttachment> = emptyList()
) {
    enum class WearableType {
        SKIN,
        SHIRT,
        PANTS,
        SHOES,
        SOCKS,
        JACKET,
        GLOVES,
        UNDERSHIRT,
        UNDERPANTS,
        SKIRT,
        TATTOO,
        ALPHA,
        PHYSICS,
        UNIVERSAL
    }
}

/**
 * BoM Cache Entry
 * Cached baked texture
 */
data class BomCacheEntry(
    val hash: String,
    val bakeType: BomBakeType,
    val textureId: UUID,
    val textureData: ByteArray?,
    val timestamp: Long = System.currentTimeMillis(),
    var lastAccess: Long = System.currentTimeMillis()
) {
    /**
     * Check if cache entry is expired
     */
    fun isExpired(maxAge: Long = 3600000): Boolean { // 1 hour default
        return System.currentTimeMillis() - timestamp > maxAge
    }
    
    /**
     * Update last access time
     */
    fun access() {
        lastAccess = System.currentTimeMillis()
    }
    
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as BomCacheEntry
        return hash == other.hash && bakeType == other.bakeType
    }
    
    override fun hashCode(): Int {
        var result = hash.hashCode()
        result = 31 * result + bakeType.hashCode()
        return result
    }
}

/**
 * BoM Statistics
 */
data class BomStatistics(
    val totalBakes: Int = 0,
    val cachedBakes: Int = 0,
    val activeBakes: Int = 0,
    val pendingBakes: Int = 0,
    val failedBakes: Int = 0,
    val cacheHitRate: Float = 0f,
    val averageBakeTime: Long = 0
)