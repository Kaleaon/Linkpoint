package com.linkpoint.bom

import java.nio.ByteBuffer
import java.util.UUID

/**
 * Bakes on Mesh Protocol Handler
 * Handles SL protocol messages for BoM
 */
object BakesOnMeshProtocol {
    
    // BoM message types
    const val MSG_AGENT_SET_APPEARANCE = 0xFFEB
    const val MSG_AVATAR_APPEARANCE = 0xFFEC
    const val MSG_AGENT_CACHED_TEXTURE = 0xFFED
    const val MSG_AGENT_CACHED_TEXTURE_RESPONSE = 0xFFEE
    
    // BoM texture indices (extended from classic)
    const val TEX_HEAD_BAKED = 0
    const val TEX_UPPER_BAKED = 1
    const val TEX_LOWER_BAKED = 2
    const val TEX_EYES_BAKED = 3
    const val TEX_SKIRT_BAKED = 4
    const val TEX_HAIR_BAKED = 5
    const val TEX_LEFT_ARM_BAKED = 6
    const val TEX_LEFT_LEG_BAKED = 7
    const val TEX_AUX1_BAKED = 8
    const val TEX_AUX2_BAKED = 9
    const val TEX_AUX3_BAKED = 10
    
    /**
     * Create AgentSetAppearance message with BoM data
     */
    fun createAgentSetAppearance(
        agentId: UUID,
        sessionId: UUID,
        config: BomAvatarConfig,
        serialNum: Int
    ): ByteBuffer {
        val buffer = ByteBuffer.allocate(4096)
        
        // Message header
        buffer.putShort(MSG_AGENT_SET_APPEARANCE.toShort())
        
        // Agent data
        buffer.putLong(agentId.mostSignificantBits)
        buffer.putLong(agentId.leastSignificantBits)
        buffer.putLong(sessionId.mostSignificantBits)
        buffer.putLong(sessionId.leastSignificantBits)
        buffer.putInt(serialNum)
        
        // Size placeholder
        val sizePos = buffer.position()
        buffer.putInt(0)
        
        // Texture entries
        val textureCount = 11 // Number of BoM bake types
        buffer.put(textureCount.toByte())
        
        // Write each baked texture
        for (bakeType in BomBakeType.values()) {
            val textureId = config.getBakedTexture(bakeType) ?: UUID(0, 0)
            buffer.putLong(textureId.mostSignificantBits)
            buffer.putLong(textureId.leastSignificantBits)
        }
        
        // Visual params (simplified)
        val visualParamCount = 0
        buffer.put(visualParamCount.toByte())
        
        // Update size
        val currentPos = buffer.position()
        buffer.putInt(sizePos, currentPos - sizePos - 4)
        
        buffer.flip()
        return buffer
    }
    
    /**
     * Parse AvatarAppearance message
     */
    fun parseAvatarAppearance(data: ByteBuffer): BomAvatarAppearance? {
        try {
            // Read sender ID
            val senderId = UUID(data.getLong(), data.getLong())
            
            // Read avatar ID
            val avatarId = UUID(data.getLong(), data.getLong())
            
            // Read is local flag
            val isLocal = data.get() != 0.toByte()
            
            // Read baked textures
            val textureCount = data.get().toInt()
            val bakedTextures = mutableMapOf<BomBakeType, UUID>()
            
            for (i in 0 until textureCount) {
                val bakeType = BomBakeType.fromIndex(i)
                val textureId = UUID(data.getLong(), data.getLong())
                
                if (bakeType != null && textureId != UUID(0, 0)) {
                    bakedTextures[bakeType] = textureId
                }
            }
            
            // Read hover offset
            val hoverX = data.getFloat()
            val hoverY = data.getFloat()
            val hoverZ = data.getFloat()
            
            // Read visual params
            val visualParamCount = data.get().toInt()
            val visualParams = ByteArray(visualParamCount)
            data.get(visualParams)
            
            return BomAvatarAppearance(
                senderId = senderId,
                avatarId = avatarId,
                isLocal = isLocal,
                bakedTextures = bakedTextures,
                hoverOffset = Triple(hoverX, hoverY, hoverZ),
                visualParams = visualParams
            )
            
        } catch (e: Exception) {
            return null
        }
    }
    
    /**
     * Create AgentCachedTexture request
     */
    fun createAgentCachedTextureRequest(
        agentId: UUID,
        sessionId: UUID,
        textureHashes: Map<BomBakeType, String>
    ): ByteBuffer {
        val buffer = ByteBuffer.allocate(2048)
        
        // Message header
        buffer.putShort(MSG_AGENT_CACHED_TEXTURE.toShort())
        
        // Agent data
        buffer.putLong(agentId.mostSignificantBits)
        buffer.putLong(agentId.leastSignificantBits)
        buffer.putLong(sessionId.mostSignificantBits)
        buffer.putLong(sessionId.leastSignificantBits)
        buffer.putInt(0) // Serial number
        
        // Texture hashes
        buffer.put(textureHashes.size.toByte())
        
        for ((bakeType, hash) in textureHashes) {
            buffer.put(bakeType.index.toByte())
            
            // Write hash (MD5, 16 bytes)
            val hashBytes = hash.toByteArray().take(16).toByteArray()
            buffer.put(hashBytes)
            if (hashBytes.size < 16) {
                // Pad if needed
                repeat(16 - hashBytes.size) { buffer.put(0) }
            }
        }
        
        buffer.flip()
        return buffer
    }
    
    /**
     * Parse AgentCachedTextureResponse
     */
    fun parseAgentCachedTextureResponse(data: ByteBuffer): BomCachedTextureResponse? {
        try {
            // Read agent ID
            val agentId = UUID(data.getLong(), data.getLong())
            
            // Read serial number
            val serialNum = data.getInt()
            
            // Read cached texture data
            val count = data.get().toInt()
            val cachedTextures = mutableMapOf<BomBakeType, UUID?>()
            
            for (i in 0 until count) {
                val bakeType = BomBakeType.fromIndex(data.get().toInt())
                val textureId = UUID(data.getLong(), data.getLong())
                
                if (bakeType != null) {
                    // If textureId is null UUID, server doesn't have cached version
                    cachedTextures[bakeType] = if (textureId == UUID(0, 0)) null else textureId
                }
            }
            
            return BomCachedTextureResponse(
                agentId = agentId,
                serialNum = serialNum,
                cachedTextures = cachedTextures
            )
            
        } catch (e: Exception) {
            return null
        }
    }
    
    /**
     * Create rebake request for specific bake type
     */
    fun createRebakeAvatarTexturesMessage(
        agentId: UUID,
        sessionId: UUID,
        bakeType: BomBakeType
    ): ByteBuffer {
        val buffer = ByteBuffer.allocate(256)
        
        // Message header (uses AgentSetAppearance with rebake flag)
        buffer.putShort(MSG_AGENT_SET_APPEARANCE.toShort())
        
        // Agent data
        buffer.putLong(agentId.mostSignificantBits)
        buffer.putLong(agentId.leastSignificantBits)
        buffer.putLong(sessionId.mostSignificantBits)
        buffer.putLong(sessionId.leastSignificantBits)
        
        // Serial number with rebake flag
        buffer.putInt(0x80000000.toInt() or bakeType.index)
        
        buffer.flip()
        return buffer
    }
}

/**
 * Avatar appearance data from server
 */
data class BomAvatarAppearance(
    val senderId: UUID,
    val avatarId: UUID,
    val isLocal: Boolean,
    val bakedTextures: Map<BomBakeType, UUID>,
    val hoverOffset: Triple<Float, Float, Float>,
    val visualParams: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as BomAvatarAppearance
        return avatarId == other.avatarId
    }
    
    override fun hashCode(): Int {
        return avatarId.hashCode()
    }
}

/**
 * Cached texture response from server
 */
data class BomCachedTextureResponse(
    val agentId: UUID,
    val serialNum: Int,
    val cachedTextures: Map<BomBakeType, UUID?>
)