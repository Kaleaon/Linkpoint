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
 * 
 * IMPORTANT: Raw packet data from UDPConnectionFixed includes the 6-byte header and
 * message ID encoding. Use [extractPayload] to get just the message payload before
 * calling parse functions.
 */
object MessageParser {
    
    private const val TAG = "MessageParser"
    
    /** Packet header size: flags (1) + sequence (4) + extra (1) = 6 bytes */
    private const val PACKET_HEADER_SIZE = 6
    
    /**
     * Extract the payload portion from a raw UDP packet.
     * 
     * The packet format is:
     * - Bytes 0-5: Header (flags, sequence number, extra byte)
     * - Bytes 6+: Message ID encoding (variable length based on frequency)
     * - Remaining: Payload data
     * 
     * Message ID encoding:
     * - High frequency: 1 byte (values 0-254)
     * - Medium frequency: 2 bytes (0xFF, then value)
     * - Low frequency: 4 bytes (0xFF, 0xFF, then 2-byte short)
     * 
     * @param rawPacket The complete raw packet data including header
     * @return The payload data without header or message ID, or null if packet is malformed
     */
    fun extractPayload(rawPacket: ByteArray): ByteArray? {
        if (rawPacket.size < PACKET_HEADER_SIZE + 1) {
            Log.w(TAG, "Packet too small to contain header: ${rawPacket.size} bytes")
            return null
        }
        
        var offset = PACKET_HEADER_SIZE
        
        // Decode message ID to determine its length
        // 
        // SL Protocol Message ID Encoding (matching Lumiya implementation):
        // - Byte.toInt() in Kotlin preserves sign: 0xFF becomes -1, 0xFB becomes -5, etc.
        // - This is INTENTIONAL because SL message IDs use signed interpretation:
        //   * High frequency: single byte, values 0-254 (or -128 to 127 signed where 0xFF/-1 means "continue")
        //   * Medium frequency: 0xFF + byte, decoded with 65280 base
        //   * Low frequency: 0xFF 0xFF + short, decoded with -65536 base
        // - The -1 check specifically detects the 0xFF sentinel byte
        val b1 = rawPacket[offset].toInt()
        offset++
        
        if (b1 != -1) {
            // High frequency: 1 byte message ID, payload starts at offset
            return rawPacket.copyOfRange(offset, rawPacket.size)
        }
        
        // Check for medium/low frequency
        if (rawPacket.size < offset + 1) {
            Log.w(TAG, "Packet truncated at medium frequency check")
            return null
        }
        
        val b2 = rawPacket[offset].toInt()
        offset++
        
        if (b2 != -1) {
            // Medium frequency: 2 byte message ID (0xFF, byte)
            return rawPacket.copyOfRange(offset, rawPacket.size)
        }
        
        // Low frequency: 4 byte message ID (0xFF, 0xFF, 2 bytes)
        if (rawPacket.size < offset + 2) {
            Log.w(TAG, "Packet truncated at low frequency check")
            return null
        }
        
        offset += 2  // Skip the 2-byte short
        return rawPacket.copyOfRange(offset, rawPacket.size)
    }
    
    /**
     * Get the message ID from a raw packet.
     * 
     * Message IDs can be negative due to signed byte interpretation:
     * - High frequency: -128 to 126 (byte values 0x80-0xFE, excluding 0xFF)
     * - Medium frequency: positive values around 65280+
     * - Low frequency: negative values around -65536+
     * 
     * @param rawPacket The complete raw packet data including header
     * @return The message ID (may be negative), or Int.MIN_VALUE if packet is malformed
     */
    fun extractMessageId(rawPacket: ByteArray): Int {
        if (rawPacket.size < PACKET_HEADER_SIZE + 1) return Int.MIN_VALUE
        
        var offset = PACKET_HEADER_SIZE
        
        // Signed byte interpretation - intentional for SL protocol compatibility
        val b1 = rawPacket[offset].toInt()
        offset++
        
        if (b1 != -1) {
            // High frequency message - return signed byte value directly
            // Examples: 4 = AgentUpdate, 12 = ObjectUpdate, -5 (0xFB) = PacketAck
            return b1
        }
        
        if (rawPacket.size < offset + 1) return Int.MIN_VALUE
        val b2 = rawPacket[offset].toInt()
        offset++
        
        if (b2 != -1) {
            // Medium frequency: byte | 65280
            return b2 or 65280
        }
        
        // Low frequency: next two bytes as short | -65536
        if (rawPacket.size < offset + 2) return Int.MIN_VALUE
        
        val byte3 = rawPacket[offset].toInt() and 0xFF
        val byte4 = rawPacket[offset + 1].toInt() and 0xFF
        val shortValue = ((byte3 shl 8) or byte4).toShort().toInt()
        
        return shortValue or -65536
    }
    
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
                regionHandle = regionHandle,
                extraParams = extraParams
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
    val regionHandle: Long = 0L,  // For computing global position
    val extraParams: ByteArray = ByteArray(0)  // Extra params containing mesh/sculpt data
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
    
    /**
     * Extract mesh/sculpt asset ID from extra params.
     * Sculpt/mesh data is stored in extra param type 0x30.
     * Format: [type:2bytes][size:4bytes][data:size bytes]
     * Sculpt data format: [sculptUUID:16bytes][sculptType:1byte]
     * Mesh has sculptType = 5
     */
    fun getMeshAssetId(): UUID? {
        if (extraParams.isEmpty()) return null
        
        try {
            val buffer = java.nio.ByteBuffer.wrap(extraParams).order(java.nio.ByteOrder.LITTLE_ENDIAN)
            
            // Parse extra params - format: count:1byte, then [type:2bytes][size:4bytes][data]...
            val paramCount = buffer.get().toInt() and 0xFF
            
            for (i in 0 until paramCount) {
                if (buffer.remaining() < 6) break
                
                val paramType = buffer.short.toInt() and 0xFFFF
                val paramSize = buffer.int
                
                if (buffer.remaining() < paramSize) break
                
                // Type 0x30 (48) = Sculpt/Mesh data
                if (paramType == 0x30 && paramSize >= 17) {
                    val uuidBytes = ByteArray(16)
                    buffer.get(uuidBytes)
                    val sculptType = buffer.get().toInt() and 0xFF
                    
                    // Sculpt type 5 = mesh
                    if (sculptType == 5) {
                        // Parse UUID (big-endian)
                        val uuidBuffer = java.nio.ByteBuffer.wrap(uuidBytes).order(java.nio.ByteOrder.BIG_ENDIAN)
                        return UUID(uuidBuffer.long, uuidBuffer.long)
                    }
                    
                    // Skip remaining bytes of this param
                    val remaining = paramSize - 17
                    if (remaining > 0 && buffer.remaining() >= remaining) {
                        buffer.position(buffer.position() + remaining)
                    }
                } else {
                    // Skip this param
                    if (buffer.remaining() >= paramSize) {
                        buffer.position(buffer.position() + paramSize)
                    }
                }
            }
        } catch (e: Exception) {
            // Silently fail - extraParams may be malformed
        }
        
        return null
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
 * 
 * Message format per SL protocol (message_template.msg):
 * RegionInfo block:
 *   - RegionFlags (U32)
 *   - SimAccess (U8)
 *   - SimName (Variable 1)
 *   - SimOwner (LLUUID)
 *   - IsEstateManager (BOOL)
 *   - WaterHeight (F32)
 *   - BillableFactor (F32)
 *   - CacheID (LLUUID)
 *   - TerrainBase0-3 (4 LLUUIDs)
 *   - TerrainDetail0-3 (4 LLUUIDs)
 *   - TerrainStartHeight00-11 (4 F32)
 *   - TerrainHeightRange00-11 (4 F32)
 * RegionInfo2 block:
 *   - RegionID (LLUUID)
 * RegionInfo3 block:
 *   - CPUClassID (S32)
 *   - CPURatio (S32)
 *   - ColoName (Variable 1)
 *   - ProductSKU (Variable 1)
 *   - ProductName (Variable 1)
 * RegionInfo4 block (Variable):
 *   - RegionFlagsExtended (U64)
 *   - RegionProtocols (U64)
 */
fun MessageParser.parseRegionHandshake(data: ByteArray): RegionHandshakeData? {
    val buffer = ByteBuffer.wrap(data).order(MESSAGE_BYTE_ORDER)
    
    try {
        // RegionInfo block
        val regionFlags = buffer.int
        val simAccess = buffer.get().toInt() and 0xFF
        
        // SimName - variable length string (1-byte length prefix)
        val simNameLen = buffer.get().toInt() and 0xFF
        val simNameBytes = ByteArray(simNameLen)
        buffer.get(simNameBytes)
        val simName = String(simNameBytes, Charsets.UTF_8).trimEnd('\u0000')
        
        // SimOwner UUID
        val simOwnerBytes = ByteArray(16)
        buffer.get(simOwnerBytes)
        val simOwner = bytesToUUID(simOwnerBytes)
        
        // IsEstateManager (BOOL = 1 byte)
        val isEstateManager = buffer.get() != 0.toByte()
        
        // Water height
        val waterHeight = buffer.float
        
        // Billable factor
        val billableFactor = buffer.float
        
        // Cache ID
        val cacheIdBytes = ByteArray(16)
        buffer.get(cacheIdBytes)
        val cacheId = bytesToUUID(cacheIdBytes)
        
        // TerrainBase0-3 (4 UUIDs for base terrain textures)
        val terrainBaseTextures = (0 until 4).map {
            val texBytes = ByteArray(16)
            buffer.get(texBytes)
            bytesToUUID(texBytes)
        }
        
        // TerrainDetail0-3 (4 UUIDs for detail terrain textures)
        val terrainDetailTextures = (0 until 4).map {
            val texBytes = ByteArray(16)
            buffer.get(texBytes)
            bytesToUUID(texBytes)
        }
        
        // Combine into single list (8 textures total)
        val terrainTextures = terrainBaseTextures + terrainDetailTextures
        
        // Terrain start heights (4 floats: 00, 01, 10, 11)
        val terrainStartHeight = (0 until 4).map { buffer.float }
        
        // Terrain height ranges (4 floats: 00, 01, 10, 11)
        val terrainHeightRange = (0 until 4).map { buffer.float }
        
        // RegionInfo2 block - RegionID
        var regionId: UUID? = null
        if (buffer.remaining() >= 16) {
            val regionIdBytes = ByteArray(16)
            buffer.get(regionIdBytes)
            regionId = bytesToUUID(regionIdBytes)
        }
        
        // RegionInfo3 block (optional - may not be present in all messages)
        var cpuClassId: Int? = null
        var cpuRatio: Int? = null
        var coloName: String? = null
        var productSKU: String? = null
        var productName: String? = null
        
        if (buffer.remaining() >= 8) {
            cpuClassId = buffer.int
            cpuRatio = buffer.int
            
            // ColoName - variable string
            if (buffer.remaining() >= 1) {
                val coloNameLen = buffer.get().toInt() and 0xFF
                if (buffer.remaining() >= coloNameLen) {
                    val coloNameBytes = ByteArray(coloNameLen)
                    buffer.get(coloNameBytes)
                    coloName = String(coloNameBytes, Charsets.UTF_8).trimEnd('\u0000')
                }
            }
            
            // ProductSKU - variable string
            if (buffer.remaining() >= 1) {
                val productSKULen = buffer.get().toInt() and 0xFF
                if (buffer.remaining() >= productSKULen) {
                    val productSKUBytes = ByteArray(productSKULen)
                    buffer.get(productSKUBytes)
                    productSKU = String(productSKUBytes, Charsets.UTF_8).trimEnd('\u0000')
                }
            }
            
            // ProductName - variable string
            if (buffer.remaining() >= 1) {
                val productNameLen = buffer.get().toInt() and 0xFF
                if (buffer.remaining() >= productNameLen) {
                    val productNameBytes = ByteArray(productNameLen)
                    buffer.get(productNameBytes)
                    productName = String(productNameBytes, Charsets.UTF_8).trimEnd('\u0000')
                }
            }
        }
        
        // RegionInfo4 block (Variable block - may have 0 or 1 entries)
        var regionFlagsExtended: Long? = null
        var regionProtocols: Long? = null
        
        // Check for variable block count byte
        if (buffer.remaining() >= 1) {
            val numRegionInfo4Blocks = buffer.get().toInt() and 0xFF
            if (numRegionInfo4Blocks > 0 && buffer.remaining() >= 16) {
                regionFlagsExtended = buffer.long
                regionProtocols = buffer.long
            }
        }
        
        return RegionHandshakeData(
            regionFlags = regionFlags,
            simAccess = simAccess,
            simName = simName,
            simOwner = simOwner,
            isEstateManager = isEstateManager,
            waterHeight = waterHeight,
            billableFactor = billableFactor,
            cacheId = cacheId,
            terrainTextures = terrainTextures,
            regionId = regionId,
            cpuClassId = cpuClassId,
            cpuRatio = cpuRatio,
            coloName = coloName,
            productSKU = productSKU,
            productName = productName,
            regionFlagsExtended = regionFlagsExtended,
            regionProtocols = regionProtocols
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
 * 
 * Message format per SL protocol:
 * AgentData block:
 *   - AgentID (LLUUID)
 *   - SessionID (LLUUID)
 * Data block:
 *   - Position (LLVector3)
 *   - LookAt (LLVector3)
 *   - RegionHandle (U64)
 *   - Timestamp (U32)
 * SimData block:
 *   - ChannelVersion (Variable 2) - 2-byte length prefixed string
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
        
        // SimData block - ChannelVersion (Variable 2: 2-byte length prefix)
        var channelVersion: String? = null
        if (buffer.remaining() >= 2) {
            val channelVersionLen = buffer.short.toInt() and 0xFFFF
            if (buffer.remaining() >= channelVersionLen) {
                val channelVersionBytes = ByteArray(channelVersionLen)
                buffer.get(channelVersionBytes)
                channelVersion = String(channelVersionBytes, Charsets.UTF_8).trimEnd('\u0000')
            }
        }
        
        return AgentMovementCompleteData(
            agentId = agentId,
            sessionId = sessionId,
            position = position,
            lookAt = lookAt,
            regionHandle = regionHandle,
            timestamp = timestamp,
            channelVersion = channelVersion
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
    val terrainTextures: List<UUID>,  // 8 textures: 4 base + 4 detail
    // RegionInfo2 block
    val regionId: UUID? = null,
    // RegionInfo3 block (optional)
    val cpuClassId: Int? = null,
    val cpuRatio: Int? = null,
    val coloName: String? = null,
    val productSKU: String? = null,
    val productName: String? = null,
    // RegionInfo4 block (optional)
    val regionFlagsExtended: Long? = null,
    val regionProtocols: Long? = null
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
    val timestamp: Int,
    val channelVersion: String? = null  // SimData block
)
