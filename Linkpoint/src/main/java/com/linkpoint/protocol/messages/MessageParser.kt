package com.linkpoint.protocol.messages

import android.util.Log
import com.linkpoint.protocol.types.LLColor4
import com.linkpoint.protocol.types.LLQuaternion
import com.linkpoint.protocol.types.LLVector3
import com.linkpoint.protocol.types.LLVector3d
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.UUID

private val MESSAGE_BYTE_ORDER = ByteOrder.LITTLE_ENDIAN

/**
 * Parses Second Life UDP message payloads.
 *
 * Payload fields are little-endian per message templates; UUIDs are raw big-endian bytes.
 */
object MessageParser {
    
    private const val TAG = "MessageParser"
    
    /**
     * Parse ObjectUpdate message
     */
    fun parseObjectUpdate(data: ByteArray): List<ObjectUpdateData> {
        val results = mutableListOf<ObjectUpdateData>()
        val buffer = ByteBuffer.wrap(data).order(MESSAGE_BYTE_ORDER)
        
        try {
            val regionHandle = buffer.long
            val timeDilation = buffer.short.toInt() and 0xFFFF
            
            // Number of object blocks
            val numBlocks = buffer.get().toInt() and 0xFF
            
            for (i in 0 until numBlocks) {
                val update = parseObjectBlock(buffer, regionHandle)
                if (update != null) {
                    results.add(update)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse ObjectUpdate", e)
        }
        
        return results
    }
    
    private fun parseObjectBlock(buffer: ByteBuffer, regionHandle: Long): ObjectUpdateData? {
        try {
            val localId = buffer.int
            val state = buffer.get().toInt() and 0xFF
            
            // Full UUID
            val fullIdBytes = ByteArray(16)
            buffer.get(fullIdBytes)
            val fullId = bytesToUUID(fullIdBytes)
            
            val crc = buffer.int
            val pcode = buffer.get().toInt() and 0xFF
            val material = buffer.get().toInt() and 0xFF
            val clickAction = buffer.get().toInt() and 0xFF
            
            // Scale
            val scaleBytes = ByteArray(12)
            buffer.get(scaleBytes)
            val scale = LLVector3.fromBytes(scaleBytes)
            
            // Object data length
            val dataLen = buffer.get().toInt() and 0xFF
            val objectData = ByteArray(dataLen)
            buffer.get(objectData)
            
            // Parse position/rotation from objectData
            var position = LLVector3.zero()
            var rotation = LLQuaternion.identity()
            var velocity = LLVector3.zero()
            
            if (dataLen >= 60) {
                position = LLVector3.fromBytes(objectData, 0)
                velocity = LLVector3.fromBytes(objectData, 12)
                // acceleration at 24
                rotation = LLQuaternion.fromBytes(objectData, 36)
                // angular velocity at 48
            } else if (dataLen >= 32) {
                // Terse update format
                position = LLVector3.fromTerse(objectData, 0, 256f)
                velocity = LLVector3.fromTerse(objectData, 6, 256f)
                rotation = LLQuaternion.fromTerse(objectData, 24)
            }
            
            // Parent ID
            val parentId = buffer.int
            
            // Update flags
            val updateFlags = buffer.int
            
            // Path/profile data
            val pathCurve = buffer.get()
            val profileCurve = buffer.get()
            val pathBegin = buffer.short
            val pathEnd = buffer.short
            val pathScaleX = buffer.get()
            val pathScaleY = buffer.get()
            val pathShearX = buffer.get()
            val pathShearY = buffer.get()
            val pathTwist = buffer.get()
            val pathTwistBegin = buffer.get()
            val pathRadiusOffset = buffer.get()
            val pathTaperX = buffer.get()
            val pathTaperY = buffer.get()
            val pathRevolutions = buffer.get()
            val pathSkew = buffer.get()
            val profileBegin = buffer.short
            val profileEnd = buffer.short
            val profileHollow = buffer.short
            
            // Texture entry
            val textureEntryLen = buffer.short.toInt() and 0xFFFF
            val textureEntry = ByteArray(textureEntryLen)
            buffer.get(textureEntry)
            
            // Texture anim
            val textureAnimLen = buffer.get().toInt() and 0xFF
            val textureAnim = ByteArray(textureAnimLen)
            buffer.get(textureAnim)
            
            // Name value
            val nameValueLen = buffer.short.toInt() and 0xFFFF
            val nameValue = ByteArray(nameValueLen)
            buffer.get(nameValue)
            val nameValueStr = String(nameValue, Charsets.UTF_8)
            
            // Data
            val dataLength = buffer.short.toInt() and 0xFFFF
            val extraData = ByteArray(dataLength)
            buffer.get(extraData)
            
            // Text (floating text)
            val textLen = buffer.get().toInt() and 0xFF
            val text = if (textLen > 0) {
                val textBytes = ByteArray(textLen)
                buffer.get(textBytes)
                String(textBytes, Charsets.UTF_8)
            } else ""
            
            // Text color
            val textColorBytes = ByteArray(4)
            buffer.get(textColorBytes)
            val textColor = LLColor4.fromBytes(textColorBytes)
            
            // Media URL
            val mediaUrlLen = buffer.get().toInt() and 0xFF
            val mediaUrl = if (mediaUrlLen > 0) {
                val urlBytes = ByteArray(mediaUrlLen)
                buffer.get(urlBytes)
                String(urlBytes, Charsets.UTF_8)
            } else ""
            
            // PSBlock
            val psBlockLen = buffer.get().toInt() and 0xFF
            val psBlock = ByteArray(psBlockLen)
            buffer.get(psBlock)
            
            // Extra params
            val extraParamsLen = buffer.get().toInt() and 0xFF
            val extraParams = ByteArray(extraParamsLen)
            buffer.get(extraParams)
            
            // Owner ID
            val ownerIdBytes = ByteArray(16)
            buffer.get(ownerIdBytes)
            val ownerId = bytesToUUID(ownerIdBytes)
            
            return ObjectUpdateData(
                localId = localId,
                fullId = fullId,
                parentId = parentId,
                position = position,
                rotation = rotation,
                velocity = velocity,
                scale = scale,
                pcode = pcode,
                material = material,
                clickAction = clickAction,
                updateFlags = updateFlags,
                textureEntry = textureEntry,
                hoverText = text,
                hoverTextColor = textColor,
                mediaUrl = mediaUrl,
                ownerId = ownerId,
                nameValue = nameValueStr,
                regionHandle = regionHandle
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse object block", e)
            return null
        }
    }
    
    /**
     * Parse ObjectUpdateCompressed message
     */
    fun parseObjectUpdateCompressed(data: ByteArray): List<ObjectUpdateData> {
        val results = mutableListOf<ObjectUpdateData>()
        val buffer = ByteBuffer.wrap(data).order(MESSAGE_BYTE_ORDER)
        
        try {
            val regionHandle = buffer.long
            val timeDilation = buffer.short.toInt() and 0xFFFF
            val numBlocks = buffer.get().toInt() and 0xFF
            
            for (i in 0 until numBlocks) {
                val update = parseCompressedBlock(buffer, regionHandle)
                if (update != null) {
                    results.add(update)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse ObjectUpdateCompressed", e)
        }
        
        return results
    }
    
    private fun parseCompressedBlock(buffer: ByteBuffer, regionHandle: Long): ObjectUpdateData? {
        try {
            val updateFlags = buffer.int
            
            val dataLen = buffer.short.toInt() and 0xFFFF
            val compressedData = ByteArray(dataLen)
            buffer.get(compressedData)
            
            val cb = ByteBuffer.wrap(compressedData).order(MESSAGE_BYTE_ORDER)
            
            // Full ID
            val fullIdBytes = ByteArray(16)
            cb.get(fullIdBytes)
            val fullId = bytesToUUID(fullIdBytes)
            
            val localId = cb.int
            val pcode = cb.get().toInt() and 0xFF
            
            // State
            val state = cb.get().toInt() and 0xFF
            
            // CRC
            val crc = cb.int
            
            // Material
            val material = cb.get().toInt() and 0xFF
            
            // Click action
            val clickAction = cb.get().toInt() and 0xFF
            
            // Scale
            val scaleBytes = ByteArray(12)
            cb.get(scaleBytes)
            val scale = LLVector3.fromBytes(scaleBytes)
            
            // Position
            val posBytes = ByteArray(12)
            cb.get(posBytes)
            val position = LLVector3.fromBytes(posBytes)
            
            // Rotation
            val rotBytes = ByteArray(12)
            cb.get(rotBytes)
            val rotation = LLQuaternion.fromBytes(rotBytes)
            
            val compFlags = cb.int
            
            var ownerId: UUID? = null
            if ((compFlags and 0x01) != 0) {
                val ownerBytes = ByteArray(16)
                cb.get(ownerBytes)
                ownerId = bytesToUUID(ownerBytes)
            }
            
            return ObjectUpdateData(
                localId = localId,
                fullId = fullId,
                parentId = 0,
                position = position,
                rotation = rotation,
                velocity = LLVector3.zero(),
                scale = scale,
                pcode = pcode,
                material = material,
                clickAction = clickAction,
                updateFlags = updateFlags,
                textureEntry = ByteArray(0),
                hoverText = "",
                hoverTextColor = LLColor4.white(),
                mediaUrl = "",
                ownerId = ownerId,
                nameValue = "",
                regionHandle = regionHandle
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse compressed block", e)
            return null
        }
    }
    
    /**
     * Parse ImprovedTerseObjectUpdate (fast position updates)
     */
    fun parseTerseObjectUpdate(data: ByteArray): List<TerseUpdateData> {
        val results = mutableListOf<TerseUpdateData>()
        val buffer = ByteBuffer.wrap(data).order(MESSAGE_BYTE_ORDER)
        
        try {
            val regionHandle = buffer.long
            val timeDilation = buffer.short.toInt() and 0xFFFF
            val numBlocks = buffer.get().toInt() and 0xFF
            
            for (i in 0 until numBlocks) {
                val dataLen = buffer.get().toInt() and 0xFF
                if (dataLen == 0) continue
                
                val blockData = ByteArray(dataLen)
                buffer.get(blockData)
                
                val update = parseTerseBlock(blockData)
                if (update != null) {
                    results.add(update)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse TerseObjectUpdate", e)
        }
        
        return results
    }
    
    private fun parseTerseBlock(data: ByteArray): TerseUpdateData? {
        if (data.size < 30) return null
        
        val bb = ByteBuffer.wrap(data).order(MESSAGE_BYTE_ORDER)
        
        val localId = bb.int
        val state = bb.get().toInt() and 0xFF
        val isAvatar = (state and 0x01) != 0
        
        // Foot collision
        val footCollisionPlane = if (isAvatar && data.size >= 46) {
            // 4 floats
            floatArrayOf(bb.float, bb.float, bb.float, bb.float)
        } else null
        
        val position = LLVector3.fromTerse(data, bb.position(), 256f)
        bb.position(bb.position() + 6)
        
        val velocity = LLVector3.fromTerse(data, bb.position(), 256f)
        bb.position(bb.position() + 6)
        
        val acceleration = LLVector3.fromTerse(data, bb.position(), 256f)
        bb.position(bb.position() + 6)
        
        val rotation = LLQuaternion.fromTerse(data, bb.position())
        bb.position(bb.position() + 8)
        
        val angularVelocity = if (bb.remaining() >= 6) {
            LLVector3.fromTerse(data, bb.position(), 256f)
        } else LLVector3.zero()
        
        return TerseUpdateData(
            localId = localId,
            isAvatar = isAvatar,
            position = position,
            velocity = velocity,
            acceleration = acceleration,
            rotation = rotation,
            angularVelocity = angularVelocity
        )
    }
    
    /**
     * Parse AvatarAnimation message
     */
    fun parseAvatarAnimation(data: ByteArray): AvatarAnimationData? {
        val buffer = ByteBuffer.wrap(data).order(MESSAGE_BYTE_ORDER)
        
        try {
            val agentIdBytes = ByteArray(16)
            buffer.get(agentIdBytes)
            val agentId = bytesToUUID(agentIdBytes)
            
            val numAnimations = buffer.get().toInt() and 0xFF
            val animations = mutableListOf<Pair<UUID, Int>>()
            
            for (i in 0 until numAnimations) {
                val animIdBytes = ByteArray(16)
                buffer.get(animIdBytes)
                val animId = bytesToUUID(animIdBytes)
                val sequenceId = buffer.int
                animations.add(animId to sequenceId)
            }
            
            val numSources = buffer.get().toInt() and 0xFF
            val sources = mutableListOf<UUID>()
            
            for (i in 0 until numSources) {
                val sourceIdBytes = ByteArray(16)
                buffer.get(sourceIdBytes)
                sources.add(bytesToUUID(sourceIdBytes))
            }
            
            return AvatarAnimationData(agentId, animations, sources)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse AvatarAnimation", e)
            return null
        }
    }
    
    /**
     * Parse ChatFromSimulator message
     */
    fun parseChatFromSimulator(data: ByteArray): ChatData? {
        val buffer = ByteBuffer.wrap(data).order(MESSAGE_BYTE_ORDER)
        
        try {
            // From name (variable)
            val fromNameLen = buffer.get().toInt() and 0xFF
            val fromNameBytes = ByteArray(fromNameLen)
            buffer.get(fromNameBytes)
            val fromName = String(fromNameBytes, Charsets.UTF_8).trimEnd('\u0000')
            
            // Source ID
            val sourceIdBytes = ByteArray(16)
            buffer.get(sourceIdBytes)
            val sourceId = bytesToUUID(sourceIdBytes)
            
            // Owner ID
            val ownerIdBytes = ByteArray(16)
            buffer.get(ownerIdBytes)
            val ownerId = bytesToUUID(ownerIdBytes)
            
            // Source type
            val sourceType = buffer.get().toInt() and 0xFF
            
            // Chat type
            val chatType = buffer.get().toInt() and 0xFF
            
            // Audible
            val audible = buffer.get().toInt() and 0xFF
            
            // Position
            val posBytes = ByteArray(12)
            buffer.get(posBytes)
            val position = LLVector3.fromBytes(posBytes)
            
            // Message (variable)
            val messageLen = buffer.short.toInt() and 0xFFFF
            val messageBytes = ByteArray(messageLen)
            buffer.get(messageBytes)
            val message = String(messageBytes, Charsets.UTF_8).trimEnd('\u0000')
            
            return ChatData(
                fromName = fromName,
                sourceId = sourceId,
                ownerId = ownerId,
                sourceType = ChatSourceType.fromValue(sourceType),
                chatType = ChatType.fromValue(chatType),
                audible = audible,
                position = position,
                message = message
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse ChatFromSimulator", e)
            return null
        }
    }
    
    private fun bytesToUUID(bytes: ByteArray): UUID {
        val bb = ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN)
        return UUID(bb.long, bb.long)
    }
}

// Data classes for parsed messages

data class ObjectUpdateData(
    val localId: Int,
    val fullId: UUID,
    val parentId: Int,
    val position: LLVector3,
    val rotation: LLQuaternion,
    val velocity: LLVector3,
    val scale: LLVector3,
    val pcode: Int,
    val material: Int,
    val clickAction: Int,
    val updateFlags: Int,
    val textureEntry: ByteArray,
    val hoverText: String,
    val hoverTextColor: LLColor4,
    val mediaUrl: String,
    val ownerId: UUID?,
    val nameValue: String,
    val regionHandle: Long = 0L  // For computing global position
) {
    /**
     * Compute global X position from region handle and local position
     */
    fun getGlobalX(): Double {
        val regionX = ((regionHandle shr 32) and 0xFFFFFFFFL).toDouble()
        return regionX + position.x
    }
    
    /**
     * Compute global Y position from region handle and local position
     */
    fun getGlobalY(): Double {
        val regionY = (regionHandle and 0xFFFFFFFFL).toDouble()
        return regionY + position.y
    }
    
    /**
     * Get global position as LLVector3d
     */
    fun getGlobalPosition(): LLVector3d {
        return LLVector3d(getGlobalX(), getGlobalY(), position.z.toDouble())
    }
}

data class TerseUpdateData(
    val localId: Int,
    val isAvatar: Boolean,
    val position: LLVector3,
    val velocity: LLVector3,
    val acceleration: LLVector3,
    val rotation: LLQuaternion,
    val angularVelocity: LLVector3
)

data class AvatarAnimationData(
    val agentId: UUID,
    val animations: List<Pair<UUID, Int>>,
    val sources: List<UUID>
)

data class ChatData(
    val fromName: String,
    val sourceId: UUID,
    val ownerId: UUID,
    val sourceType: ChatSourceType,
    val chatType: ChatType,
    val audible: Int,
    val position: LLVector3,
    val message: String
)

enum class ChatSourceType(val value: Int) {
    SYSTEM(0),
    AGENT(1),
    OBJECT(2);
    
    companion object {
        fun fromValue(value: Int) = values().find { it.value == value } ?: SYSTEM
    }
}

enum class ChatType(val value: Int) {
    WHISPER(0),
    NORMAL(1),
    SHOUT(2),
    SAY(3),
    START_TYPING(4),
    STOP_TYPING(5),
    DEBUG(6),
    REGION(7),
    OWNER(8),
    DIRECT(9);
    
    companion object {
        fun fromValue(value: Int) = values().find { it.value == value } ?: NORMAL
    }
}

/**
 * Parse RegionHandshake message.
 * This message is sent by the simulator after CompleteAgentMovement
 * and must be acknowledged with RegionHandshakeReply.
 */
fun MessageParser.parseRegionHandshake(data: ByteArray): RegionHandshakeData? {
    val buffer = ByteBuffer.wrap(data).order(MESSAGE_BYTE_ORDER)
    
    try {
        // RegionInfo block
        val regionFlags = buffer.int
        val simAccess = buffer.get().toInt() and 0xFF
        
        // SimName - variable length string
        val simNameLen = buffer.get().toInt() and 0xFF
        val simNameBytes = ByteArray(simNameLen)
        buffer.get(simNameBytes)
        val simName = String(simNameBytes, Charsets.UTF_8).trimEnd('\u0000')
        
        // SimOwner UUID
        val simOwnerBytes = ByteArray(16)
        buffer.get(simOwnerBytes)
        val simOwner = bytesToUUID(simOwnerBytes)
        
        // IsEstateManager
        val isEstateManager = buffer.get() != 0.toByte()
        
        // Water height
        val waterHeight = buffer.float
        
        // Billboard sunset
        val billableFactor = buffer.float
        
        // Cache ID
        val cacheIdBytes = ByteArray(16)
        buffer.get(cacheIdBytes)
        val cacheId = bytesToUUID(cacheIdBytes)
        
        // Terrain textures (4 UUIDs)
        val terrainTextures = (0 until 4).map {
            val texBytes = ByteArray(16)
            buffer.get(texBytes)
            bytesToUUID(texBytes)
        }
        
        // Terrain start/end heights (8 floats)
        val terrainStartHeight = (0 until 4).map { buffer.float }
        val terrainHeightRange = (0 until 4).map { buffer.float }
        
        // Region UUID (from RegionInfo2 block if present)
        // For simplicity, we'll generate from cache ID
        
        return RegionHandshakeData(
            regionFlags = regionFlags,
            simAccess = simAccess,
            simName = simName,
            simOwner = simOwner,
            isEstateManager = isEstateManager,
            waterHeight = waterHeight,
            billableFactor = billableFactor,
            cacheId = cacheId,
            terrainTextures = terrainTextures
        )
    } catch (e: Exception) {
        Log.e("MessageParser", "Failed to parse RegionHandshake", e)
        return null
    }
}

/**
 * Helper to convert bytes to UUID - accessible for extension function
 */
private fun bytesToUUID(bytes: ByteArray): UUID {
    val bb = ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN)
    return UUID(bb.long, bb.long)
}

/**
 * Parse AgentMovementComplete message.
 * Confirms the agent is fully in the region.
 */
fun MessageParser.parseAgentMovementComplete(data: ByteArray): AgentMovementCompleteData? {
    val buffer = ByteBuffer.wrap(data).order(MESSAGE_BYTE_ORDER)
    
    try {
        // AgentData block
        val agentIdBytes = ByteArray(16)
        buffer.get(agentIdBytes)
        val agentId = bytesToUUID(agentIdBytes)
        
        val sessionIdBytes = ByteArray(16)
        buffer.get(sessionIdBytes)
        val sessionId = bytesToUUID(sessionIdBytes)
        
        // Data block
        val positionBytes = ByteArray(12)
        buffer.get(positionBytes)
        val position = LLVector3.fromBytes(positionBytes)
        
        val lookAtBytes = ByteArray(12)
        buffer.get(lookAtBytes)
        val lookAt = LLVector3.fromBytes(lookAtBytes)
        
        val regionHandle = buffer.long
        val timestamp = buffer.int
        
        return AgentMovementCompleteData(
            agentId = agentId,
            sessionId = sessionId,
            position = position,
            lookAt = lookAt,
            regionHandle = regionHandle,
            timestamp = timestamp
        )
    } catch (e: Exception) {
        Log.e("MessageParser", "Failed to parse AgentMovementComplete", e)
        return null
    }
}

/**
 * Data from RegionHandshake message
 */
data class RegionHandshakeData(
    val regionFlags: Int,
    val simAccess: Int,
    val simName: String,
    val simOwner: UUID,
    val isEstateManager: Boolean,
    val waterHeight: Float,
    val billableFactor: Float,
    val cacheId: UUID,
    val terrainTextures: List<UUID>
)

/**
 * Data from AgentMovementComplete message
 */
data class AgentMovementCompleteData(
    val agentId: UUID,
    val sessionId: UUID,
    val position: LLVector3,
    val lookAt: LLVector3,
    val regionHandle: Long,
    val timestamp: Int
)
