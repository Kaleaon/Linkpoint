package com.linkpoint.protocol.messages

import android.util.Log
import com.linkpoint.protocol.types.LLColor4
import com.linkpoint.protocol.types.LLQuaternion
import com.linkpoint.protocol.types.LLVector3
import com.linkpoint.protocol.types.LLVector3d
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.UUID


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
    
    internal const val TAG = "MessageParser"
    
    /** Packet header size: flags (1) + sequence (4) + extra (1) = 6 bytes */
    private const val PACKET_HEADER_SIZE = 6
    internal const val REGION_HANDSHAKE_MIN_BYTES = 189
    
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

        // LLUDP packets carry an "extra header" of 0..255 bytes after the
        // 6-byte fixed header (flags + 4-byte seq + 1-byte extra-header
        // length). The message-ID prefix follows those bytes, not byte 6.
        // Skipping the extra-header is what `getMessageStartOffset` /
        // `extractMessageId` in UDPConnectionFixed already do (commit
        // 3cf9fad4 hardened those paths); applying the same skip here so
        // `registerParsedHandler` receives the right body for packets
        // where the simulator chose to include extra-header bytes.
        val extraHeaderLen = rawPacket[5].toInt() and 0xFF
        var offset = PACKET_HEADER_SIZE + extraHeaderLen
        if (offset + 1 > rawPacket.size) {
            Log.w(TAG, "Packet truncated past extra header (size=${rawPacket.size}, extra=$extraHeaderLen)")
            return null
        }

        // Decode message ID to determine its length
        //
        // SL Protocol Message ID Encoding (matching SL protocol):
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
        if (rawPacket.size < PACKET_HEADER_SIZE) return Int.MIN_VALUE

        val extraHeaderLen = rawPacket[5].toInt() and 0xFF
        var offset = PACKET_HEADER_SIZE + extraHeaderLen
        if (offset >= rawPacket.size) return Int.MIN_VALUE

        // Signed byte interpretation - intentional for SL protocol compatibility
        val b1 = rawPacket[offset].toInt()
        offset++

        if (b1 != -1) {
            // High frequency message - return signed byte value directly
            // Examples: 4 = AgentUpdate, 12 = ObjectUpdate, -5 (0xFB) = PacketAck
            return b1
        }

        if (offset >= rawPacket.size) return Int.MIN_VALUE
        val b2 = rawPacket[offset].toInt()
        offset++

        if (b2 != -1) {
            // Medium frequency: byte | 65280
            return b2 or 65280
        }

        // Low frequency: next two bytes as short | -65536
        if (offset + 1 >= rawPacket.size) return Int.MIN_VALUE

        val byte3 = rawPacket[offset].toInt() and 0xFF
        val byte4 = rawPacket[offset + 1].toInt() and 0xFF
        val shortValue = ((byte3 shl 8) or byte4).toShort().toInt()

        return shortValue or -65536
    }

    fun extractSequenceNumber(rawPacket: ByteArray): Int {
        if (rawPacket.size < 5) return -1
        return ((rawPacket[1].toInt() and 0xFF) shl 24) or
            ((rawPacket[2].toInt() and 0xFF) shl 16) or
            ((rawPacket[3].toInt() and 0xFF) shl 8) or
            (rawPacket[4].toInt() and 0xFF)
    }
    

    fun parseByMessageId(messageId: Int, payload: ByteArray): Any? =
        MessageParserRegistry.parse(messageId, payload)

    /**
     * Parse ObjectUpdate message
     */
    fun parseObjectUpdate(data: ByteArray): List<ObjectUpdateData> = ObjectMessageParsers.parseObjectUpdate(data)
    
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

            // Sound UUID (per SL ObjectUpdate.java - this comes BEFORE OwnerID)
            val soundIdBytes = ByteArray(16)
            buffer.get(soundIdBytes)
            val soundId = bytesToUUID(soundIdBytes)

            // Owner ID
            val ownerIdBytes = ByteArray(16)
            buffer.get(ownerIdBytes)
            val ownerId = bytesToUUID(ownerIdBytes)

            // Gain (float) - sound volume
            val gain = buffer.float

            // Flags (byte) - sound flags
            val soundFlags = buffer.get().toInt() and 0xFF

            // Radius (float) - sound radius
            val soundRadius = buffer.float

            // JointType (byte)
            val jointType = buffer.get().toInt() and 0xFF

            // JointPivot (LLVector3)
            val jointPivotBytes = ByteArray(12)
            buffer.get(jointPivotBytes)
            val jointPivot = LLVector3.fromBytes(jointPivotBytes)

            // JointAxisOrAnchor (LLVector3)
            val jointAxisBytes = ByteArray(12)
            buffer.get(jointAxisBytes)
            val jointAxisOrAnchor = LLVector3.fromBytes(jointAxisBytes)

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
                soundId = soundId,
                ownerId = ownerId,
                soundGain = gain,
                soundFlags = soundFlags,
                soundRadius = soundRadius,
                jointType = jointType,
                jointPivot = jointPivot,
                jointAxisOrAnchor = jointAxisOrAnchor,
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
    fun parseObjectUpdateCompressed(data: ByteArray): List<ObjectUpdateData> = ObjectMessageParsers.parseObjectUpdateCompressed(data)
    
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
    fun parseTerseObjectUpdate(data: ByteArray): List<TerseUpdateData> = ObjectMessageParsers.parseTerseObjectUpdate(data)
    
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
    fun parseAvatarAnimation(data: ByteArray): AvatarAnimationData? = AvatarMessageParsers.parseAvatarAnimation(data)
    
    /**
     * Parse ChatFromSimulator message
     */
    fun parseChatFromSimulator(data: ByteArray): ChatData? = ChatMessageParsers.parseChatFromSimulator(data)

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
    val soundId: UUID? = null,           // Sound attached to object 
    val ownerId: UUID?,
    val soundGain: Float = 0f,           // Sound volume 
    val soundFlags: Int = 0,             // Sound flags 
    val soundRadius: Float = 0f,         // Sound radius 
    val jointType: Int = 0,              // Joint type 
    val jointPivot: LLVector3 = LLVector3.zero(),  // Joint pivot point
    val jointAxisOrAnchor: LLVector3 = LLVector3.zero(),  // Joint axis or anchor
    val nameValue: String,
    val regionHandle: Long = 0L,  // For computing global position
    val extraParams: ByteArray = ByteArray(0),  // Extra params containing mesh/sculpt data
    /**
     * Path/profile shape parameters from the ObjectUpdate. Drives real prim
     * geometry instead of the previous hardcoded box. Defaulted so the
     * compressed/cached/terse paths (which don't carry these bytes inline)
     * keep working unchanged.
     */
    val shapeParams: PrimShapeParams = PrimShapeParams.DEFAULT
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
                        val mostSigBits = uuidBuffer.long
                        val leastSigBits = uuidBuffer.long
                        return UUID(mostSigBits, leastSigBits)
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
    if (data.size < REGION_HANDSHAKE_MIN_BYTES) {
        Log.w(TAG, "RegionHandshake payload too short: ${data.size} bytes (min=$REGION_HANDSHAKE_MIN_BYTES)")
        return null
    }
    val buffer = ByteBuffer.wrap(data).order(MESSAGE_BYTE_ORDER)
    
    try {
        // RegionInfo block
        val regionFlags = buffer.int
        val simAccess = buffer.get().toInt() and 0xFF
        
        // SimName - variable length string (1-byte length prefix)
        val simNameLen = buffer.get().toInt() and 0xFF
        val simNameBytes = ByteArray(simNameLen)
        buffer.get(simNameBytes)
        val simName = String(simNameBytes, Charsets.UTF_8).trimEnd('\u0000').trim()
        if (simName.isEmpty()) {
            Log.w(TAG, "RegionHandshake payload rejected: empty SimName")
            return null
        }
        
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
            terrainStartHeights = terrainStartHeight,
            terrainHeightRanges = terrainHeightRange,
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
    /** Per-corner elevation blend start heights (0,0 / 1,0 / 0,1 / 1,1). */
    val terrainStartHeights: List<Float> = emptyList(),
    /** Per-corner elevation blend ranges (matches startHeights ordering). */
    val terrainHeightRanges: List<Float> = emptyList(),
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

/**
 * Data from ObjectUpdateCached message (message ID 14)
 * 
 * This message indicates the server has cached object data.
 * The client should respond with RequestMultipleObjects to get full data.
 */
data class ObjectUpdateCachedData(
    val regionHandle: Long,
    val timeDilation: Int,
    val objects: List<CachedObjectData>
)

/**
 * Individual cached object entry
 */
data class CachedObjectData(
    val localId: Int,
    val crc: Int,
    val updateFlags: Int
)

/**
 * Parse ObjectUpdateCached message (ID 14 / 0xE)
 * 
 * Format:
 * - RegionData: RegionHandle (U64), TimeDilation (U16)
 * - ObjectData (variable): ID (U32), CRC (U32), UpdateFlags (U32)
 */
fun MessageParser.parseObjectUpdateCached(data: ByteArray): ObjectUpdateCachedData? = ObjectMessageParsers.parseObjectUpdateCached(data)



/**
 * Data from ScriptControlChange message (message ID -65347 / 0xFFFF00BD)
 * 
 * This message indicates that script control permissions have changed.
 * Scripts can take/release keyboard/mouse controls from the agent.
 */
data class ScriptControlChangeData(
    val controls: List<ControlData>
)

/**
 * Individual control permission entry
 */
data class ControlData(
    val takeControls: Boolean,
    val controls: Int,  // Bitmask of control flags
    val passToAgent: Boolean
)

/**
 * Parse ScriptControlChange message (ID -65347 / 0xFFFF00BD)
 * 
 * Format:
 * - Data (variable): TakeControls (BOOL), Controls (U32), PassToAgent (BOOL)
 */
fun MessageParser.parseScriptControlChange(data: ByteArray): ScriptControlChangeData? {
    try {
        val buffer = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN)
        
        // Count of data blocks
        val numData = buffer.get().toInt() and 0xFF
        
        val controls = mutableListOf<ControlData>()
        
        for (i in 0 until numData) {
            val takeControls = buffer.get() != 0.toByte()
            val controlFlags = buffer.int
            val passToAgent = buffer.get() != 0.toByte()
            
            controls.add(ControlData(
                takeControls = takeControls,
                controls = controlFlags,
                passToAgent = passToAgent
            ))
        }
        
        return ScriptControlChangeData(controls = controls)
    } catch (e: Exception) {
        Log.e("MessageParser", "Failed to parse ScriptControlChange", e)
        return null
    }
}

/**
 * Data from ObjectProperties message (message ID 65289 / 0xFF09 - medium frequency)
 * 
 * Contains detailed object metadata sent from the server when object properties
 * are requested via ObjectSelect or when the server pushes updated properties.
 */
data class ObjectPropertiesData(
    val objects: List<ObjectPropertyEntry>
)

/**
 * Individual object property entry from ObjectProperties message.
 * 
 * Note: equals() and hashCode() use only objectId for comparison because
 * the objectId uniquely identifies an object in the Second Life protocol.
 * Multiple ObjectProperties messages for the same object should update
 * the existing entry rather than create duplicates.
 */
data class ObjectPropertyEntry(
    val objectId: UUID,
    val creatorId: UUID,
    val ownerId: UUID,
    val groupId: UUID,
    val creationDate: Long,
    val baseMask: Int,
    val ownerMask: Int,
    val groupMask: Int,
    val everyoneMask: Int,
    val nextOwnerMask: Int,
    val ownershipCost: Int,
    val saleType: Int,
    val salePrice: Int,
    val aggregatePerms: Int,
    val aggregatePermTextures: Int,
    val aggregatePermTexturesOwner: Int,
    val category: Int,
    val inventorySerial: Int,
    val itemId: UUID,
    val folderId: UUID,
    val fromTaskId: UUID,
    val lastOwnerId: UUID,
    val name: String,
    val description: String,
    val touchName: String,
    val sitName: String,
    val textureIds: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as ObjectPropertyEntry
        return objectId == other.objectId
    }
    
    override fun hashCode(): Int = objectId.hashCode()
}

/**
 * Parse ObjectProperties message (ID 65289 / 0xFF09 - medium frequency)
 * 
 * This message contains detailed object properties like name, description,
 * owner, permissions, and other metadata. It's typically sent in response
 * to ObjectSelect or when properties change.
 * 
 * Format per ObjectData block:
 * - ObjectID (UUID, 16 bytes)
 * - CreatorID (UUID, 16 bytes)
 * - OwnerID (UUID, 16 bytes)
 * - GroupID (UUID, 16 bytes)
 * - CreationDate (S64, 8 bytes)
 * - BaseMask (U32, 4 bytes)
 * - OwnerMask (U32, 4 bytes)
 * - GroupMask (U32, 4 bytes)
 * - EveryoneMask (U32, 4 bytes)
 * - NextOwnerMask (U32, 4 bytes)
 * - OwnershipCost (S32, 4 bytes)
 * - SaleType (U8, 1 byte)
 * - SalePrice (S32, 4 bytes)
 * - AggregatePerms (U8, 1 byte)
 * - AggregatePermTextures (U8, 1 byte)
 * - AggregatePermTexturesOwner (U8, 1 byte)
 * - Category (U32, 4 bytes)
 * - InventorySerial (U16, 2 bytes) - treated as unsigned for protocol compatibility
 * - ItemID (UUID, 16 bytes)
 * - FolderID (UUID, 16 bytes)
 * - FromTaskID (UUID, 16 bytes)
 * - LastOwnerID (UUID, 16 bytes)
 * - Name (Variable, 1-byte length prefix)
 * - Description (Variable, 1-byte length prefix)
 * - TouchName (Variable, 1-byte length prefix)
 * - SitName (Variable, 1-byte length prefix)
 * - TextureID (Variable, 1-byte length prefix)
 */
fun MessageParser.parseObjectProperties(data: ByteArray): ObjectPropertiesData? {
    try {
        val buffer = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN)
        
        // Number of ObjectData blocks
        val numObjects = buffer.get().toInt() and 0xFF
        
        val objects = mutableListOf<ObjectPropertyEntry>()
        
        for (i in 0 until numObjects) {
            if (buffer.remaining() < 175) {
                // Minimum size check (UUIDs + fixed fields, excluding variable strings)
                Log.w("MessageParser", "ObjectProperties: insufficient data for object $i")
                break
            }
            
            // ObjectID
            val objectIdBytes = ByteArray(16)
            buffer.get(objectIdBytes)
            val objectId = bytesToUUID(objectIdBytes)
            
            // CreatorID
            val creatorIdBytes = ByteArray(16)
            buffer.get(creatorIdBytes)
            val creatorId = bytesToUUID(creatorIdBytes)
            
            // OwnerID
            val ownerIdBytes = ByteArray(16)
            buffer.get(ownerIdBytes)
            val ownerId = bytesToUUID(ownerIdBytes)
            
            // GroupID
            val groupIdBytes = ByteArray(16)
            buffer.get(groupIdBytes)
            val groupId = bytesToUUID(groupIdBytes)
            
            // CreationDate (S64)
            val creationDate = buffer.long
            
            // Permission masks
            val baseMask = buffer.int
            val ownerMask = buffer.int
            val groupMask = buffer.int
            val everyoneMask = buffer.int
            val nextOwnerMask = buffer.int
            
            // Sale info
            val ownershipCost = buffer.int
            val saleType = buffer.get().toInt() and 0xFF
            val salePrice = buffer.int
            
            // Aggregate permissions
            val aggregatePerms = buffer.get().toInt() and 0xFF
            val aggregatePermTextures = buffer.get().toInt() and 0xFF
            val aggregatePermTexturesOwner = buffer.get().toInt() and 0xFF
            
            // Category and inventory
            val category = buffer.int
            val inventorySerial = buffer.short.toInt() and 0xFFFF
            
            // ItemID
            val itemIdBytes = ByteArray(16)
            buffer.get(itemIdBytes)
            val itemId = bytesToUUID(itemIdBytes)
            
            // FolderID
            val folderIdBytes = ByteArray(16)
            buffer.get(folderIdBytes)
            val folderId = bytesToUUID(folderIdBytes)
            
            // FromTaskID
            val fromTaskIdBytes = ByteArray(16)
            buffer.get(fromTaskIdBytes)
            val fromTaskId = bytesToUUID(fromTaskIdBytes)
            
            // LastOwnerID
            val lastOwnerIdBytes = ByteArray(16)
            buffer.get(lastOwnerIdBytes)
            val lastOwnerId = bytesToUUID(lastOwnerIdBytes)
            
            // Name (variable, 1-byte length prefix)
            val nameLen = buffer.get().toInt() and 0xFF
            val name = if (nameLen > 0 && buffer.remaining() >= nameLen) {
                val nameBytes = ByteArray(nameLen)
                buffer.get(nameBytes)
                String(nameBytes, Charsets.UTF_8).trimEnd('\u0000')
            } else ""
            
            // Description (variable, 1-byte length prefix)
            val descLen = buffer.get().toInt() and 0xFF
            val description = if (descLen > 0 && buffer.remaining() >= descLen) {
                val descBytes = ByteArray(descLen)
                buffer.get(descBytes)
                String(descBytes, Charsets.UTF_8).trimEnd('\u0000')
            } else ""
            
            // TouchName (variable, 1-byte length prefix)
            val touchNameLen = buffer.get().toInt() and 0xFF
            val touchName = if (touchNameLen > 0 && buffer.remaining() >= touchNameLen) {
                val touchNameBytes = ByteArray(touchNameLen)
                buffer.get(touchNameBytes)
                String(touchNameBytes, Charsets.UTF_8).trimEnd('\u0000')
            } else ""
            
            // SitName (variable, 1-byte length prefix)
            val sitNameLen = buffer.get().toInt() and 0xFF
            val sitName = if (sitNameLen > 0 && buffer.remaining() >= sitNameLen) {
                val sitNameBytes = ByteArray(sitNameLen)
                buffer.get(sitNameBytes)
                String(sitNameBytes, Charsets.UTF_8).trimEnd('\u0000')
            } else ""
            
            // TextureID (variable, 1-byte length prefix - contains texture UUIDs)
            val textureIdLen = buffer.get().toInt() and 0xFF
            val textureIds = if (textureIdLen > 0 && buffer.remaining() >= textureIdLen) {
                val textureIdBytes = ByteArray(textureIdLen)
                buffer.get(textureIdBytes)
                textureIdBytes
            } else ByteArray(0)
            
            objects.add(ObjectPropertyEntry(
                objectId = objectId,
                creatorId = creatorId,
                ownerId = ownerId,
                groupId = groupId,
                creationDate = creationDate,
                baseMask = baseMask,
                ownerMask = ownerMask,
                groupMask = groupMask,
                everyoneMask = everyoneMask,
                nextOwnerMask = nextOwnerMask,
                ownershipCost = ownershipCost,
                saleType = saleType,
                salePrice = salePrice,
                aggregatePerms = aggregatePerms,
                aggregatePermTextures = aggregatePermTextures,
                aggregatePermTexturesOwner = aggregatePermTexturesOwner,
                category = category,
                inventorySerial = inventorySerial,
                itemId = itemId,
                folderId = folderId,
                fromTaskId = fromTaskId,
                lastOwnerId = lastOwnerId,
                name = name,
                description = description,
                touchName = touchName,
                sitName = sitName,
                textureIds = textureIds
            ))
        }
        
        Log.d("MessageParser", "Parsed ObjectProperties: ${objects.size} objects")
        return ObjectPropertiesData(objects = objects)
    } catch (e: Exception) {
        Log.e("MessageParser", "Failed to parse ObjectProperties", e)
        return null
    }
}

// =====================================
// Phase 1 Critical Message Data Classes
// =====================================

/**
 * TeleportFinish - Teleport completed, connect to new region
 */
data class TeleportFinishData(
    val agentId: UUID,
    val locationId: Int,
    val simIP: String,
    val simPort: Int,
    val regionHandle: Long,
    val seedCapability: String,
    val simAccess: Int,
    val teleportFlags: Int
)

/**
 * TeleportFailed - Teleport failed with reason
 */
data class TeleportFailedData(
    val agentId: UUID,
    val reason: String
)

/**
 * TeleportProgress - Teleport status update
 */
data class TeleportProgressData(
    val agentId: UUID,
    val teleportFlags: Int,
    val message: String
)

/**
 * AlertMessage - System alert
 */
data class AlertMessageData(
    val message: String,
    val alertInfos: List<AlertInfo> = emptyList()
)

data class AlertInfo(
    val message: String,
    val extraParams: String
)

/**
 * AgentAlertMessage - Agent-specific alert
 */
data class AgentAlertMessageData(
    val agentId: UUID,
    val modal: Boolean,
    val message: String
)

/**
 * EnableSimulator - Neighbor sim connection info
 */
data class EnableSimulatorData(
    val handle: Long,
    val ip: String,
    val port: Int
)

/**
 * CrossedRegion - Region crossing info (medium frequency)
 */
data class CrossedRegionData(
    val agentId: UUID,
    val sessionId: UUID,
    val simIP: String,
    val simPort: Int,
    val regionHandle: Long,
    val seedCapability: String,
    val position: LLVector3,
    val lookAt: LLVector3
)

// =====================================
// Phase 1 Message Parsers
// =====================================

/**
 * Parse TeleportFinish message
 */
fun MessageParser.parseTeleportFinish(data: ByteArray): TeleportFinishData? = TeleportMessageParsers.parseTeleportFinish(data)



/**
 * Parse TeleportFailed message
 */
fun MessageParser.parseTeleportFailed(data: ByteArray): TeleportFailedData? = TeleportMessageParsers.parseTeleportFailed(data)



/**
 * Parse TeleportProgress message
 */
fun MessageParser.parseTeleportProgress(data: ByteArray): TeleportProgressData? = TeleportMessageParsers.parseTeleportProgress(data)



/**
 * Parse AlertMessage
 */
fun MessageParser.parseAlertMessage(data: ByteArray): AlertMessageData? {
    try {
        val buffer = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN)
        
        // Message (variable, 1-byte length prefix)
        val msgLen = buffer.get().toInt() and 0xFF
        val message = if (msgLen > 0 && buffer.remaining() >= msgLen) {
            val msgBytes = ByteArray(msgLen)
            buffer.get(msgBytes)
            String(msgBytes, Charsets.UTF_8).trimEnd('\u0000')
        } else ""
        
        // AlertInfo blocks (variable count)
        val alertInfos = mutableListOf<AlertInfo>()
        if (buffer.remaining() >= 1) {
            val numAlerts = buffer.get().toInt() and 0xFF
            for (i in 0 until numAlerts) {
                if (buffer.remaining() < 2) break
                
                val alertMsgLen = buffer.get().toInt() and 0xFF
                val alertMsg = if (alertMsgLen > 0 && buffer.remaining() >= alertMsgLen) {
                    val alertMsgBytes = ByteArray(alertMsgLen)
                    buffer.get(alertMsgBytes)
                    String(alertMsgBytes, Charsets.UTF_8).trimEnd('\u0000')
                } else ""
                
                val extraLen = buffer.get().toInt() and 0xFF
                val extra = if (extraLen > 0 && buffer.remaining() >= extraLen) {
                    val extraBytes = ByteArray(extraLen)
                    buffer.get(extraBytes)
                    String(extraBytes, Charsets.UTF_8).trimEnd('\u0000')
                } else ""
                
                alertInfos.add(AlertInfo(alertMsg, extra))
            }
        }
        
        Log.d("MessageParser", "Parsed AlertMessage: $message")
        return AlertMessageData(message = message, alertInfos = alertInfos)
    } catch (e: Exception) {
        Log.e("MessageParser", "Failed to parse AlertMessage", e)
        return null
    }
}

/**
 * Parse AgentAlertMessage
 */
fun MessageParser.parseAgentAlertMessage(data: ByteArray): AgentAlertMessageData? {
    try {
        val buffer = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN)
        
        // AgentID (16 bytes)
        val agentIdBytes = ByteArray(16)
        buffer.get(agentIdBytes)
        val agentId = bytesToUUID(agentIdBytes)
        
        // Modal (1 byte boolean)
        val modal = buffer.get() != 0.toByte()
        
        // Message (variable, 1-byte length prefix)
        val msgLen = buffer.get().toInt() and 0xFF
        val message = if (msgLen > 0 && buffer.remaining() >= msgLen) {
            val msgBytes = ByteArray(msgLen)
            buffer.get(msgBytes)
            String(msgBytes, Charsets.UTF_8).trimEnd('\u0000')
        } else ""
        
        Log.d("MessageParser", "Parsed AgentAlertMessage: modal=$modal, message=$message")
        return AgentAlertMessageData(agentId = agentId, modal = modal, message = message)
    } catch (e: Exception) {
        Log.e("MessageParser", "Failed to parse AgentAlertMessage", e)
        return null
    }
}

/**
 * Parse EnableSimulator
 */
fun MessageParser.parseEnableSimulator(data: ByteArray): EnableSimulatorData? = TeleportMessageParsers.parseEnableSimulator(data)



/**
 * Parse CrossedRegion (medium frequency)
 */
fun MessageParser.parseCrossedRegion(data: ByteArray): CrossedRegionData? = TeleportMessageParsers.parseCrossedRegion(data)



/**
 * Parse ImprovedInstantMessage message.
 * Handles instant messages, group notices, typing indicators, etc.
 *
 * Wire format (`message_template.msg:5613-5644`, verified against Lumiya
 * `messages/ImprovedInstantMessage.java:51-70` AND a live capture from
 * the production simulator):
 *
 * ```
 * AgentData    { AgentID(16 BE)  SessionID(16 BE) }
 * MessageBlock { FromGroup(1)  ToAgentID(16)  ParentEstateID(4 LE)
 *                RegionID(16)  Position(12)  Offline(1)  Dialog(1)
 *                ID(16)  Timestamp(4 LE)  FromAgentName(var-1+NUL)
 *                Message(var-2+NUL)  BinaryBucket(var-2) }
 * EstateBlock  { EstateID(4 LE) }       // INBOUND ONLY — sim emits, viewer ignores
 * MetaData     { count(1) [...] }       // INBOUND ONLY — sim emits, viewer ignores
 * ```
 *
 * **Inbound packets from the simulator carry the trailing `EstateBlock`
 * (4 bytes) and `MetaData` (1+ bytes) blocks** — confirmed via a live
 * capture where every inbound IIM had exactly 5 trailing bytes after
 * BinaryBucket. The viewer's outbound IIMs do NOT include those blocks
 * (Lumiya / LL viewer / Firestorm / LibreMetaverse all stop at
 * BinaryBucket); the sim stamps them on its way back. We tolerate them
 * by simply not reading past `BinaryBucket`.
 *
 * Previously this parser skipped both `AgentData.SessionID` (16 bytes)
 * and `MessageBlock.FromGroup` (1 byte), so every field after offset 16
 * was off by 17 bytes — every inbound IM, group chat line, and typing
 * indicator decoded to garbage. That's why the user observed "I never
 * see IMs come in" in addition to the outbound bugs already fixed in
 * commit 83184418.
 */
fun MessageParser.parseImprovedInstantMessage(data: ByteArray): ImprovedInstantMessageData? {
    val buffer = ByteBuffer.wrap(data).order(MESSAGE_BYTE_ORDER)

    try {
        // AgentData.AgentID (16 bytes BE) — the sender.
        val fromAgentIdBytes = ByteArray(16)
        buffer.get(fromAgentIdBytes)
        val fromAgentId = bytesToUUID(fromAgentIdBytes)

        // AgentData.SessionID (16 bytes BE) — server reflects our session
        // ID back; not useful to inbound dispatch but it's on the wire.
        // Skip without consuming as a UUID we surface upward.
        buffer.position(buffer.position() + 16)

        // MessageBlock.FromGroup (BOOL — 1 byte). Captured for future use;
        // current data class doesn't expose it.
        @Suppress("UNUSED_VARIABLE")
        val fromGroup = buffer.get().toInt() != 0

        // MessageBlock.ToAgentID (16 bytes BE)
        val toAgentIdBytes = ByteArray(16)
        buffer.get(toAgentIdBytes)
        val toAgentId = bytesToUUID(toAgentIdBytes)

        // MessageBlock.ParentEstateID (U32 LE)
        val parentEstateId = buffer.int

        // MessageBlock.RegionID (16 bytes BE)
        val regionIdBytes = ByteArray(16)
        buffer.get(regionIdBytes)
        val regionId = bytesToUUID(regionIdBytes)

        // MessageBlock.Position (Vector3 = 3 LE floats = 12 bytes)
        val posBytes = ByteArray(12)
        buffer.get(posBytes)
        val position = LLVector3.fromBytes(posBytes)

        // MessageBlock.Offline (U8)
        val offline = buffer.get().toInt() and 0xFF

        // MessageBlock.Dialog (U8) — IM type (0=normal, 17=group send,
        // 41/42=typing, etc.).
        val dialog = buffer.get().toInt() and 0xFF

        // MessageBlock.ID (16 bytes BE) — IM session UUID
        val sessionIdBytes = ByteArray(16)
        buffer.get(sessionIdBytes)
        val sessionId = bytesToUUID(sessionIdBytes)
        
        // Timestamp (U32 - 4 bytes)
        val timestamp = buffer.int.toLong() and 0xFFFFFFFFL
        
        // FromAgentName (Variable 1 - length prefix 1 byte)
        val fromNameLen = buffer.get().toInt() and 0xFF
        val fromNameBytes = ByteArray(fromNameLen)
        buffer.get(fromNameBytes)
        val fromAgentName = String(fromNameBytes, Charsets.UTF_8).trimEnd('\u0000')
        
        // Message (Variable 2 - length prefix 2 bytes)
        val messageLen = buffer.short.toInt() and 0xFFFF
        val messageBytes = ByteArray(messageLen)
        buffer.get(messageBytes)
        val message = String(messageBytes, Charsets.UTF_8).trimEnd('\u0000')
        
        // BinaryBucket (Variable 2 - length prefix 2 bytes)
        val binaryBucketLen = buffer.short.toInt() and 0xFFFF
        val binaryBucket = ByteArray(binaryBucketLen)
        if (binaryBucketLen > 0) {
            buffer.get(binaryBucket)
        }
        // Trailing EstateBlock + MetaData blocks (sim → client only) are
        // intentionally not read; the parser stops at BinaryBucket.

        return ImprovedInstantMessageData(
            fromAgentId = fromAgentId,
            toAgentId = toAgentId,
            parentEstateId = parentEstateId,
            regionId = regionId,
            position = position,
            offline = offline,
            dialog = dialog,
            sessionId = sessionId,
            timestamp = timestamp,
            fromAgentName = fromAgentName,
            message = message,
            binaryBucket = binaryBucket
        )
    } catch (e: Exception) {
        Log.e("MessageParser", "Failed to parse ImprovedInstantMessage", e)
        return null
    }
}

/**
 * Data class for ImprovedInstantMessage
 */
data class ImprovedInstantMessageData(
    val fromAgentId: UUID,
    val toAgentId: UUID,
    val parentEstateId: Int,
    val regionId: UUID,
    val position: LLVector3,
    val offline: Int,
    val dialog: Int,
    val sessionId: UUID,
    val timestamp: Long,
    val fromAgentName: String,
    val message: String,
    val binaryBucket: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ImprovedInstantMessageData

        if (fromAgentId != other.fromAgentId) return false
        if (toAgentId != other.toAgentId) return false
        if (parentEstateId != other.parentEstateId) return false
        if (regionId != other.regionId) return false
        if (position != other.position) return false
        if (offline != other.offline) return false
        if (dialog != other.dialog) return false
        if (sessionId != other.sessionId) return false
        if (timestamp != other.timestamp) return false
        if (fromAgentName != other.fromAgentName) return false
        if (message != other.message) return false
        if (!binaryBucket.contentEquals(other.binaryBucket)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = fromAgentId.hashCode()
        result = 31 * result + toAgentId.hashCode()
        result = 31 * result + parentEstateId
        result = 31 * result + regionId.hashCode()
        result = 31 * result + position.hashCode()
        result = 31 * result + offline
        result = 31 * result + dialog
        result = 31 * result + sessionId.hashCode()
        result = 31 * result + timestamp.hashCode()
        result = 31 * result + fromAgentName.hashCode()
        result = 31 * result + message.hashCode()
        result = 31 * result + binaryBucket.contentHashCode()
        return result
    }
}

/**
 * Data from ParcelProperties message (ID 23 - high frequency)
 */
data class ParcelPropertiesData(
    val requestResult: Int,
    val sequenceId: Int,
    val snapSelection: Boolean,
    val selfCount: Int,
    val otherCount: Int,
    val publicCount: Int,
    val localId: Int,
    val ownerId: UUID,
    val isGroupOwned: Boolean,
    val auctionId: Int,
    val claimDate: Int,
    val claimPrice: Int,
    val rentPrice: Int,
    val aabbMin: LLVector3,
    val aabbMax: LLVector3,
    val bitmap: ByteArray,
    val area: Int,
    val status: Int,
    val simWideMaxPrims: Int,
    val simWideTotalPrims: Int,
    val maxPrims: Int,
    val totalPrims: Int,
    val ownerPrims: Int,
    val groupPrims: Int,
    val otherPrims: Int,
    val selectedPrims: Int,
    val parcelPrimBonus: Float,
    val otherCleanTime: Int,
    val parcelFlags: Long, // U32
    val salePrice: Int,
    val name: String,
    val description: String,
    val musicUrl: String,
    val mediaUrl: String,
    val mediaId: UUID,
    val mediaAutoScale: Int, // U8
    val groupId: UUID,
    val passPrice: Int,
    val passHours: Float,
    val category: Int, // U8
    val authBuyerId: UUID,
    val snapshotId: UUID,
    val userLocation: LLVector3,
    val userLookAt: LLVector3,
    val landingType: Int, // U8
    val regionPushOverride: Boolean,
    val regionDenyAnonymous: Boolean,
    val regionDenyIdentified: Boolean,
    val regionDenyTransacted: Boolean,
    val regionDenyAgeUnverified: Boolean,
    val seeAVs: Boolean,
    val anyAVSounds: Boolean,
    val groupPrimsAllowed: Boolean
)

/**
 * Parse ParcelProperties message (ID 23)
 */
fun MessageParser.parseParcelProperties(data: ByteArray): ParcelPropertiesData? {
    try {
        val buffer = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN)

        // Helper for variable string reading
        fun ByteBuffer.readString1(): String {
            if (this.remaining() < 1) return ""
            val len = this.get().toInt() and 0xFF
            if (len == 0) return ""
            if (this.remaining() < len) return ""
            val bytes = ByteArray(len)
            this.get(bytes)
            return String(bytes, Charsets.UTF_8).trimEnd('\u0000')
        }

        // 1. RequestResult (S32)
        val requestResult = buffer.int

        // 2. SequenceID (S32)
        val sequenceId = buffer.int

        // 3. SnapSelection (BOOL)
        val snapSelection = buffer.get() != 0.toByte()

        // 4. SelfCount (S32)
        val selfCount = buffer.int

        // 5. OtherCount (S32)
        val otherCount = buffer.int

        // 6. PublicCount (S32)
        val publicCount = buffer.int

        // 7. LocalID (S32)
        val localId = buffer.int

        // 8. OwnerID (UUID)
        val ownerIdBytes = ByteArray(16)
        buffer.get(ownerIdBytes)
        val ownerId = bytesToUUID(ownerIdBytes)

        // 9. IsGroupOwned (BOOL)
        val isGroupOwned = buffer.get() != 0.toByte()

        // 10. AuctionID (U32)
        val auctionId = buffer.int

        // 11. ClaimDate (S32)
        val claimDate = buffer.int

        // 12. ClaimPrice (S32)
        val claimPrice = buffer.int

        // 13. RentPrice (S32)
        val rentPrice = buffer.int

        // 14. AABBMin (Vector3)
        val aabbMinBytes = ByteArray(12)
        buffer.get(aabbMinBytes)
        val aabbMin = LLVector3.fromBytes(aabbMinBytes)

        // 15. AABBMax (Vector3)
        val aabbMaxBytes = ByteArray(12)
        buffer.get(aabbMaxBytes)
        val aabbMax = LLVector3.fromBytes(aabbMaxBytes)

        // 16. Bitmap (Variable 1)
        val bitmapLen = buffer.get().toInt() and 0xFF
        val bitmap = ByteArray(bitmapLen)
        if (bitmapLen > 0) {
            buffer.get(bitmap)
        }

        // 17. Area (S32)
        val area = buffer.int

        // 18. Status (U8)
        val status = buffer.get().toInt() and 0xFF

        // 19. SimWideMaxPrims (S32)
        val simWideMaxPrims = buffer.int

        // 20. SimWideTotalPrims (S32)
        val simWideTotalPrims = buffer.int

        // 21. MaxPrims (S32)
        val maxPrims = buffer.int

        // 22. TotalPrims (S32)
        val totalPrims = buffer.int

        // 23. OwnerPrims (S32)
        val ownerPrims = buffer.int

        // 24. GroupPrims (S32)
        val groupPrims = buffer.int

        // 25. OtherPrims (S32)
        val otherPrims = buffer.int

        // 26. SelectedPrims (S32)
        val selectedPrims = buffer.int

        // 27. ParcelPrimBonus (F32)
        val parcelPrimBonus = buffer.float

        // 28. OtherCleanTime (S32)
        val otherCleanTime = buffer.int

        // 29. ParcelFlags (U32)
        val parcelFlags = buffer.int.toLong() and 0xFFFFFFFFL

        // 30. SalePrice (S32)
        val salePrice = buffer.int

        // 31. Name (Variable 1)
        val name = buffer.readString1()

        // 32. Description (Variable 1)
        val description = buffer.readString1()

        // 33. MusicURL (Variable 1)
        val musicUrl = buffer.readString1()

        // 34. MediaURL (Variable 1)
        val mediaUrl = buffer.readString1()

        // 35. MediaID (UUID)
        val mediaIdBytes = ByteArray(16)
        buffer.get(mediaIdBytes)
        val mediaId = bytesToUUID(mediaIdBytes)

        // 36. MediaAutoScale (U8)
        val mediaAutoScale = buffer.get().toInt() and 0xFF

        // 37. GroupID (UUID)
        val groupIdBytes = ByteArray(16)
        buffer.get(groupIdBytes)
        val groupId = bytesToUUID(groupIdBytes)

        // 38. PassPrice (S32)
        val passPrice = buffer.int

        // 39. PassHours (F32)
        val passHours = buffer.float

        // 40. Category (U8)
        val category = buffer.get().toInt() and 0xFF

        // 41. AuthBuyerID (UUID)
        val authBuyerIdBytes = ByteArray(16)
        buffer.get(authBuyerIdBytes)
        val authBuyerId = bytesToUUID(authBuyerIdBytes)

        // 42. SnapshotID (UUID)
        val snapshotIdBytes = ByteArray(16)
        buffer.get(snapshotIdBytes)
        val snapshotId = bytesToUUID(snapshotIdBytes)

        // 43. UserLocation (Vector3)
        val userLocationBytes = ByteArray(12)
        buffer.get(userLocationBytes)
        val userLocation = LLVector3.fromBytes(userLocationBytes)

        // 44. UserLookAt (Vector3)
        val userLookAtBytes = ByteArray(12)
        buffer.get(userLookAtBytes)
        val userLookAt = LLVector3.fromBytes(userLookAtBytes)

        // 45. LandingType (U8)
        val landingType = buffer.get().toInt() and 0xFF

        // 46. RegionPushOverride (BOOL)
        val regionPushOverride = buffer.get() != 0.toByte()

        // 47. RegionDenyAnonymous (BOOL)
        val regionDenyAnonymous = buffer.get() != 0.toByte()

        // 48. RegionDenyIdentified (BOOL)
        val regionDenyIdentified = buffer.get() != 0.toByte()

        // 49. RegionDenyTransacted (BOOL)
        val regionDenyTransacted = buffer.get() != 0.toByte()

        // 50. RegionDenyAgeUnverified (BOOL)
        val regionDenyAgeUnverified = buffer.get() != 0.toByte()

        // Optional fields (check buffer remaining)
        var seeAVs = true
        var anyAVSounds = true
        var groupPrimsAllowed = true

        if (buffer.remaining() > 0) {
            seeAVs = buffer.get() != 0.toByte()
        }
        if (buffer.remaining() > 0) {
            anyAVSounds = buffer.get() != 0.toByte()
        }
        if (buffer.remaining() > 0) {
            groupPrimsAllowed = buffer.get() != 0.toByte()
        }

        return ParcelPropertiesData(
            requestResult, sequenceId, snapSelection, selfCount, otherCount, publicCount,
            localId, ownerId, isGroupOwned, auctionId, claimDate, claimPrice, rentPrice,
            aabbMin, aabbMax, bitmap, area, status, simWideMaxPrims, simWideTotalPrims,
            maxPrims, totalPrims, ownerPrims, groupPrims, otherPrims, selectedPrims,
            parcelPrimBonus, otherCleanTime, parcelFlags, salePrice, name, description,
            musicUrl, mediaUrl, mediaId, mediaAutoScale, groupId, passPrice, passHours,
            category, authBuyerId, snapshotId, userLocation, userLookAt, landingType,
            regionPushOverride, regionDenyAnonymous, regionDenyIdentified, regionDenyTransacted,
            regionDenyAgeUnverified, seeAVs, anyAVSounds, groupPrimsAllowed
        )

    } catch (e: Exception) {
        Log.e("MessageParser", "Failed to parse ParcelProperties", e)
        return null
    }
}
