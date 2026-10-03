package com.linkpoint.world

import android.util.Log
import com.linkpoint.protocol.core.AgentIdentity
import com.linkpoint.protocol.messages.ids.MessageIdRegistry
import com.linkpoint.protocol.messages.UDPConnectionFixed
import com.linkpoint.protocol.types.LLVector3
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * Manages parcel/land information
 */
class ParcelManager(
    private val udpConnection: UDPConnectionFixed
) {
    companion object {
        private const val TAG = "ParcelManager"
        
        // Parcel flags
        const val FLAG_ALLOW_FLY = 0x00000001L
        const val FLAG_ALLOW_SCRIPTS = 0x00000002L
        const val FLAG_ALLOW_LANDMARK = 0x00000008L
        const val FLAG_ALLOW_TERRAFORM = 0x00000010L
        const val FLAG_ALLOW_DAMAGE = 0x00000020L
        const val FLAG_CREATE_OBJECTS = 0x00000040L
        const val FLAG_FOR_SALE = 0x00000080L
        const val FLAG_USE_ACCESS_GROUP = 0x00000100L
        const val FLAG_USE_ACCESS_LIST = 0x00000200L
        const val FLAG_USE_BAN_LIST = 0x00000400L
        const val FLAG_USE_LAND_PASS_LIST = 0x00000800L
        const val FLAG_SHOW_DIRECTORY = 0x00001000L
        const val FLAG_ALLOW_DEED_TO_GROUP = 0x00002000L
        const val FLAG_CONTRIBUTE_WITH_DEED = 0x00004000L
        const val FLAG_SOUND_LOCAL = 0x00008000L
        const val FLAG_SELL_PARCEL_OBJECTS = 0x00010000L
        const val FLAG_ALLOW_PUBLISH = 0x00020000L
        const val FLAG_MATURE_PUBLISH = 0x00040000L
        const val FLAG_URL_WEB_PAGE = 0x00080000L
        const val FLAG_URL_RAW_HTML = 0x00100000L
        const val FLAG_RESTRICT_PUSHOBJECT = 0x00200000L
        const val FLAG_DENY_ANONYMOUS = 0x00400000L
        const val FLAG_ALLOW_GROUP_SCRIPTS = 0x02000000L
        const val FLAG_CREATE_GROUP_OBJECTS = 0x04000000L
        const val FLAG_ALLOW_ALL_OBJECT_ENTRY = 0x08000000L
        const val FLAG_ALLOW_GROUP_OBJECT_ENTRY = 0x10000000L
        const val FLAG_ALLOW_VOICE_CHAT = 0x20000000L
        const val FLAG_USE_ESTATE_VOICE_CHAN = 0x40000000L
        const val FLAG_DENY_AGEUNVERIFIED = 0x80000000L
        
        // Landing types
        const val LANDING_ANYWHERE = 0
        const val LANDING_POINT = 1
        const val LANDING_NONE = 2
    }
    
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    
    // Cached parcels
    private val parcels = ConcurrentHashMap<Int, ParcelInfo>()
    
    // Current parcel
    private val _currentParcel = MutableStateFlow<ParcelInfo?>(null)
    val currentParcel: StateFlow<ParcelInfo?> = _currentParcel
    
    /**
     * Write AgentData block (AgentID + SessionID) with UUIDs in big-endian bytes.
     * Message block fields remain little-endian per SL templates.
     */
    private fun writeAgentData(buffer: ByteBuffer) {
        val identity = AgentIdentity(
            agentId = udpConnection.getAgentId(),
            sessionId = udpConnection.getSessionId(),
            circuitCode = udpConnection.getCircuitCode()
        ).requireValid("ParcelManager outbound packet")
        writeUUID(buffer, identity.agentId)
        writeUUID(buffer, identity.sessionId)
    }
    
    /**
     * Write UUID to buffer using proper serialization (big-endian)
     */
    private fun writeUUID(buffer: ByteBuffer, uuid: UUID) {
        val order = buffer.order()
        buffer.order(ByteOrder.BIG_ENDIAN)
        buffer.putLong(uuid.mostSignificantBits)
        buffer.putLong(uuid.leastSignificantBits)
        buffer.order(order)
    }

    private fun putVariable1String(buffer: ByteBuffer, value: String?) {
        val bytes = (value ?: "").toByteArray(Charsets.UTF_8)
        val safeBytes = if (bytes.size > 255) bytes.copyOf(255) else bytes
        buffer.put(safeBytes.size.toByte())
        buffer.put(safeBytes)
    }
    
    /**
     * Handle ParcelProperties message
     */
    fun handleParcelProperties(
        localId: Int,
        ownerId: UUID,
        groupId: UUID?,
        name: String,
        description: String,
        claimDate: Long,
        claimPrice: Int,
        rentPrice: Int,
        aabbMin: LLVector3,
        aabbMax: LLVector3,
        area: Int,
        actualArea: Int,
        simWideMaxPrims: Int,
        simWideTotal: Int,
        maxPrims: Int,
        totalPrims: Int,
        ownerPrims: Int,
        groupPrims: Int,
        otherPrims: Int,
        selectedPrims: Int,
        parcelPrimBonus: Float,
        cleanTime: Int,
        flags: Long,
        landingType: Int,
        musicUrl: String?,
        mediaUrl: String?,
        mediaId: UUID?,
        mediaAutoScale: Boolean,
        groupPrimeOverride: Boolean,
        category: Int,
        snapshotId: UUID?,
        userLocation: LLVector3,
        userLookAt: LLVector3,
        status: Int,
        passPrice: Int,
        passHours: Float
    ) {
        val parcel = ParcelInfo(
            localId = localId,
            ownerId = ownerId,
            groupId = groupId,
            name = name,
            description = description,
            claimDate = claimDate,
            claimPrice = claimPrice,
            rentPrice = rentPrice,
            aabbMin = aabbMin,
            aabbMax = aabbMax,
            area = area,
            actualArea = actualArea,
            simWideMaxPrims = simWideMaxPrims,
            simWideTotal = simWideTotal,
            maxPrims = maxPrims,
            totalPrims = totalPrims,
            ownerPrims = ownerPrims,
            groupPrims = groupPrims,
            otherPrims = otherPrims,
            selectedPrims = selectedPrims,
            parcelPrimBonus = parcelPrimBonus,
            cleanTime = cleanTime,
            flags = flags,
            landingType = landingType,
            musicUrl = musicUrl,
            mediaUrl = mediaUrl,
            mediaId = mediaId,
            mediaAutoScale = mediaAutoScale,
            category = category,
            snapshotId = snapshotId,
            userLocation = userLocation,
            userLookAt = userLookAt,
            status = status,
            passPrice = passPrice,
            passHours = passHours
        )
        
        parcels[localId] = parcel
    }
    
    /**
     * Request parcel info at position.
     * Sends ParcelInfoRequest message to get parcel data at specific coordinates.
     */
    fun requestParcelInfo(position: LLVector3) {
        scope.launch {
            try {
                // ParcelInfoRequest message format:
                // AgentData block + InfoData block with position
                val payload = ByteBuffer.allocate(44).order(ByteOrder.LITTLE_ENDIAN)
                
                // AgentData
                writeAgentData(payload)
                
                // Position (as integers, local coordinates)
                payload.putInt(position.x.toInt())
                payload.putInt(position.y.toInt())
                payload.putInt(position.z.toInt())
                
                udpConnection.sendPacket(MessageIdRegistry.PARCEL_INFO_REQUEST, payload.array(), reliable = true)
                Log.d(TAG, "Requested parcel info at position: $position")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to request parcel info", e)
            }
        }
    }
    
    /**
     * Handle ParcelInfoReply message from server.
     */
    fun handleParcelInfoReply(data: com.linkpoint.protocol.messages.AdditionalMessageParsers.ParcelInfoReplyData) {
        Log.d(TAG, "Received parcel info: ${data.name} (${data.parcelID})")
        // Cache parcel info - could be used for map display or teleport preview
    }
    
    /**
     * Set current parcel
     */
    fun setCurrentParcel(localId: Int) {
        _currentParcel.value = parcels[localId]
    }
    
    /**
     * Get parcel at position
     */
    fun getParcelAtPosition(x: Float, y: Float): ParcelInfo? {
        val localX = x.toInt().coerceIn(0, 255)
        val localY = y.toInt().coerceIn(0, 255)
        
        val index = localY * 64 + localX / 4
        if (index < parcelOverlay.size) {
            val byte = parcelOverlay[index].toInt() and 0xFF
            val shift = (localX % 4) * 2
            val parcelId = (byte shr shift) and 0x03
            return parcels[parcelId]
        }
        
        return null
    }
    
    /**
     * Buy land
     */
    fun buyLand(localId: Int, forGroup: Boolean = false) {
        scope.launch {
            try {
                // ParcelBuy message (Lumiya parity):
                // AgentData(32) + Data(16+1+1+4+1) + ParcelData(4+4) = 67 bytes
                val payload = ByteBuffer.allocate(67).order(ByteOrder.LITTLE_ENDIAN)
                val groupId = if (forGroup) {
                    parcels[localId]?.groupId ?: UUID(0L, 0L)
                } else {
                    UUID(0L, 0L)
                }
                
                // AgentData with proper IDs
                writeAgentData(payload)
                
                // Data block
                writeUUID(payload, groupId)
                payload.put(if (forGroup) 1 else 0)  // IsGroupOwned
                payload.put(0)  // RemoveContribution
                payload.putInt(localId)
                payload.put(1)  // Final=true

                // ParcelData block
                payload.putInt(0)  // Price (0 = simulator authoritative price)
                payload.putInt(0)  // Area
                
                udpConnection.sendPacket(MessageIdRegistry.PARCEL_BUY, payload.array(), reliable = true)
                Log.i(TAG, "Sent ParcelBuy for localId=$localId")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to buy land", e)
            }
        }
    }
    
    /**
     * Deed land to group
     */
    fun deedToGroup(localId: Int, groupId: UUID) {
        scope.launch {
            try {
                // ParcelDeedToGroup message
                val payload = ByteBuffer.allocate(56).order(ByteOrder.LITTLE_ENDIAN)
                
                // AgentData with proper IDs
                writeAgentData(payload)
                
                // Data block
                writeUUID(payload, groupId)
                payload.putInt(localId)  // LocalID
                
                udpConnection.sendPacket(MessageIdRegistry.PARCEL_DEED_TO_GROUP, payload.array(), reliable = true)
                Log.i(TAG, "Sent ParcelDeedToGroup for localId=$localId to group $groupId")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to deed land to group", e)
            }
        }
    }
    
    /**
     * Release/abandon land
     */
    fun releaseLand(localId: Int) {
        scope.launch {
            try {
                // ParcelRelease message
                val payload = ByteBuffer.allocate(40).order(ByteOrder.LITTLE_ENDIAN)
                
                // AgentData with proper IDs
                writeAgentData(payload)
                
                // Data block
                payload.putInt(localId)
                
                udpConnection.sendPacket(MessageIdRegistry.PARCEL_RELEASE, payload.array(), reliable = true)
                Log.i(TAG, "Released land localId=$localId")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to release land", e)
            }
        }
    }
    
    /**
     * Set parcel for sale
     */
    fun setForSale(localId: Int, price: Int, forAll: Boolean = true) {
        scope.launch {
            try {
                val parcel = parcels[localId]
                val existingFlags = parcel?.flags ?: 0L
                val updatedFlags = if (forAll) {
                    existingFlags or FLAG_FOR_SALE
                } else {
                    existingFlags and FLAG_FOR_SALE.inv()
                }

                if (parcel != null) {
                    parcels[localId] = parcel.copy(flags = updatedFlags)
                }

                sendParcelPropertiesUpdate(
                    localId = localId,
                    salePriceOverride = price,
                    parcelFlagsOverride = updatedFlags
                )
                Log.i(TAG, "Set parcel $localId for sale at price $price")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to set parcel for sale", e)
            }
        }
    }
    
    /**
     * Return objects from parcel
     */
    fun returnObjects(localId: Int, returnType: ReturnType) {
        scope.launch {
            try {
                // ParcelReturnObjects / ParcelDisableObjects message
                val payload = ByteBuffer.allocate(50).order(ByteOrder.LITTLE_ENDIAN)
                
                // AgentData with proper IDs
                writeAgentData(payload)
                
                // ParcelData
                payload.putInt(localId)
                payload.putInt(returnType.ordinal)  // ReturnType
                
                // Empty task/owner lists
                payload.put(0)  // TaskIDs count
                payload.put(0)  // OwnerIDs count
                
                udpConnection.sendPacket(MessageIdRegistry.PARCEL_RETURN_OBJECTS, payload.array(), reliable = true)
                Log.i(TAG, "Returned objects from parcel $localId, type=${returnType.name}")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to return objects", e)
            }
        }
    }
    
    /**
     * Set parcel name
     */
    fun setParcelName(localId: Int, name: String) {
        parcels[localId]?.let { parcel ->
            parcels[localId] = parcel.copy(name = name)
        }
        scope.launch {
            try {
                sendParcelPropertiesUpdate(localId)
                Log.d(TAG, "Set parcel name: localId=$localId, name=$name")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to set parcel name", e)
            }
        }
    }
    
    /**
     * Set parcel description
     */
    fun setParcelDescription(localId: Int, description: String) {
        parcels[localId]?.let { parcel ->
            parcels[localId] = parcel.copy(description = description)
        }
        scope.launch {
            try {
                sendParcelPropertiesUpdate(localId)
                Log.d(TAG, "Set parcel description: localId=$localId")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to set parcel description", e)
            }
        }
    }
    
    /**
     * Set parcel flags
     */
    fun setParcelFlags(localId: Int, flags: Long) {
        parcels[localId]?.let { parcel ->
            parcels[localId] = parcel.copy(flags = flags)
        }
        scope.launch {
            try {
                sendParcelPropertiesUpdate(localId)
                Log.d(TAG, "Set parcel flags: localId=$localId, flags=$flags")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to set parcel flags", e)
            }
        }
    }
    
    /**
     * Set music URL
     */
    fun setMusicUrl(localId: Int, url: String?) {
        parcels[localId]?.let { parcel ->
            parcels[localId] = parcel.copy(musicUrl = url)
        }
        scope.launch {
            try {
                sendParcelPropertiesUpdate(localId)
                Log.d(TAG, "Set parcel music URL: localId=$localId")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to set music URL", e)
            }
        }
    }
    
    /**
     * Set media URL
     */
    fun setMediaUrl(localId: Int, url: String?) {
        parcels[localId]?.let { parcel ->
            parcels[localId] = parcel.copy(mediaUrl = url)
        }
        scope.launch {
            try {
                sendParcelPropertiesUpdate(localId)
                Log.d(TAG, "Set parcel media URL: localId=$localId")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to set media URL", e)
            }
        }
    }
    
    /**
     * Add to access list
     */
    fun addToAccessList(localId: Int, agentId: UUID, hours: Float = 0f) {
        scope.launch {
            try {
                // ParcelAccessListUpdate message
                val payload = ByteBuffer.allocate(93).order(ByteOrder.LITTLE_ENDIAN)
                val transactionId = UUID.randomUUID()
                
                // AgentData with proper IDs
                writeAgentData(payload)
                
                // Data block
                payload.putInt(0)  // Flags - add to access
                payload.putInt(localId)
                writeUUID(payload, transactionId)
                payload.putInt(1)  // SequenceID
                payload.putInt(1)  // Sections
                
                // List block
                payload.put(1)  // Entry count
                writeUUID(payload, agentId)
                payload.putInt((hours * 3600).toInt())  // Time in seconds
                payload.putInt(1)  // Flags - access allowed
                
                udpConnection.sendPacket(MessageIdRegistry.PARCEL_ACCESS_LIST_UPDATE, payload.array(), reliable = true)
                Log.i(TAG, "Added $agentId to access list for parcel $localId")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to add to access list", e)
            }
        }
    }
    
    /**
     * Remove from access list
     */
    fun removeFromAccessList(localId: Int, agentId: UUID) {
        scope.launch {
            try {
                val payload = ByteBuffer.allocate(93).order(ByteOrder.LITTLE_ENDIAN)
                val transactionId = UUID.randomUUID()
                
                // AgentData with proper IDs
                writeAgentData(payload)
                
                // Data block
                payload.putInt(1)  // Flags - remove from access
                payload.putInt(localId)
                writeUUID(payload, transactionId)
                payload.putInt(1)
                payload.putInt(1)
                
                // List block
                payload.put(1)
                writeUUID(payload, agentId)
                payload.putInt(0)
                payload.putInt(0)
                
                udpConnection.sendPacket(MessageIdRegistry.PARCEL_ACCESS_LIST_UPDATE, payload.array(), reliable = true)
                Log.i(TAG, "Removed $agentId from access list for parcel $localId")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to remove from access list", e)
            }
        }
    }
    
    /**
     * Add to ban list
     */
    fun addToBanList(localId: Int, agentId: UUID) {
        scope.launch {
            try {
                val payload = ByteBuffer.allocate(93).order(ByteOrder.LITTLE_ENDIAN)
                val transactionId = UUID.randomUUID()
                
                // AgentData with proper IDs
                writeAgentData(payload)
                
                // Data block  
                payload.putInt(2)  // Flags - ban list
                payload.putInt(localId)
                writeUUID(payload, transactionId)
                payload.putInt(1)
                payload.putInt(1)
                
                // List block
                payload.put(1)
                writeUUID(payload, agentId)
                payload.putInt(0)  // Permanent
                payload.putInt(1)  // Flags - banned
                
                udpConnection.sendPacket(MessageIdRegistry.PARCEL_ACCESS_LIST_UPDATE, payload.array(), reliable = true)
                Log.i(TAG, "Added $agentId to ban list for parcel $localId")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to add to ban list", e)
            }
        }
    }
    
    /**
     * Remove from ban list
     */
    fun removeFromBanList(localId: Int, agentId: UUID) {
        scope.launch {
            try {
                val payload = ByteBuffer.allocate(93).order(ByteOrder.LITTLE_ENDIAN)
                val transactionId = UUID.randomUUID()
                
                // AgentData with proper IDs
                writeAgentData(payload)
                
                // Data block
                payload.putInt(3)  // Flags - remove from ban
                payload.putInt(localId)
                writeUUID(payload, transactionId)
                payload.putInt(1)
                payload.putInt(1)
                
                // List block
                payload.put(1)
                writeUUID(payload, agentId)
                payload.putInt(0)
                payload.putInt(0)
                
                udpConnection.sendPacket(MessageIdRegistry.PARCEL_ACCESS_LIST_UPDATE, payload.array(), reliable = true)
                Log.i(TAG, "Removed $agentId from ban list for parcel $localId")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to remove from ban list", e)
            }
        }
    }
    
    /**
     * Send parcel properties update
     */
    private suspend fun sendParcelPropertiesUpdate(
        localId: Int,
        salePriceOverride: Int? = null,
        parcelFlagsOverride: Long? = null
    ) {
        val parcel = parcels[localId] ?: return
        
        val nameSize = minOf(parcel.name.toByteArray(Charsets.UTF_8).size, 255)
        val descSize = minOf(parcel.description.toByteArray(Charsets.UTF_8).size, 255)
        val musicSize = minOf((parcel.musicUrl ?: "").toByteArray(Charsets.UTF_8).size, 255)
        val mediaSize = minOf((parcel.mediaUrl ?: "").toByteArray(Charsets.UTF_8).size, 255)
        val payloadSize = 126 + nameSize + descSize + musicSize + mediaSize

        val payload = ByteBuffer.allocate(payloadSize)
            .order(ByteOrder.LITTLE_ENDIAN)
        
        // AgentData
        writeAgentData(payload)
        
        // ParcelData
        payload.putInt(localId)
        payload.putInt(parcelFlagsOverride?.toInt() ?: parcel.flags.toInt()) // Flags
        payload.putInt(parcel.flags.toInt()) // ParcelFlags
        payload.putInt(salePriceOverride ?: parcel.claimPrice.coerceAtLeast(0)) // SalePrice
        
        // Variable-length 1-byte strings
        putVariable1String(payload, parcel.name)
        putVariable1String(payload, parcel.description)
        putVariable1String(payload, parcel.musicUrl)
        putVariable1String(payload, parcel.mediaUrl)

        // Media info
        writeUUID(payload, parcel.mediaId ?: UUID(0L, 0L))
        payload.put(if (parcel.mediaAutoScale) 1 else 0)
        
        // Group
        writeUUID(payload, parcel.groupId ?: UUID(0L, 0L))

        // Pass settings
        payload.putInt(parcel.passPrice)
        payload.putFloat(parcel.passHours)

        // Category
        payload.put(parcel.category.toByte())

        // AuthBuyerID (none / any buyer), SnapshotID, landing vectors
        writeUUID(payload, UUID(0L, 0L))
        writeUUID(payload, parcel.snapshotId ?: UUID(0L, 0L))
        payload.putFloat(parcel.userLocation.x)
        payload.putFloat(parcel.userLocation.y)
        payload.putFloat(parcel.userLocation.z)
        payload.putFloat(parcel.userLookAt.x)
        payload.putFloat(parcel.userLookAt.y)
        payload.putFloat(parcel.userLookAt.z)
        payload.put(parcel.landingType.toByte())
        
        udpConnection.sendPacket(MessageIdRegistry.PARCEL_PROPERTIES_UPDATE, payload.array(), reliable = true)
    }
    
    // ==================== PARCEL OVERLAY ====================
    
    // Parcel overlay bitmap data (64x64 grid, 4 bits per parcel)
    private val parcelOverlay = ByteArray(2048) // 64x64 / 2 = 2048 bytes
    private var parcelOverlaySequence = 0
    
    /**
     * Handle parcel overlay data from UDP message
     * The overlay is a bitmap showing parcel boundaries for the minimap
     */
    fun handleParcelOverlay(sequenceId: Int, data: ByteArray) {
        Log.d(TAG, "Received parcel overlay: sequence=$sequenceId, size=${data.size} bytes")
        
        // Each sequence represents 1/4 of the total overlay
        // The region is divided into 4 horizontal strips, each 16 blocks tall
        val stripSize = 512  // 64 * 8 = 512 bytes per strip (64 wide, 16 tall, 4 bits per cell / 2 = 512)
        val offset = sequenceId * stripSize
        
        if (offset >= 0 && offset + data.size <= parcelOverlay.size) {
            System.arraycopy(data, 0, parcelOverlay, offset, minOf(data.size, parcelOverlay.size - offset))
            parcelOverlaySequence = maxOf(parcelOverlaySequence, sequenceId)
            Log.d(TAG, "Parcel overlay updated: sequence=$sequenceId, copied ${minOf(data.size, parcelOverlay.size - offset)} bytes to offset $offset")
        } else {
            Log.w(TAG, "Parcel overlay sequence $sequenceId out of bounds (offset=$offset, dataSize=${data.size})")
        }
    }
    
    /**
     * Get the parcel overlay bitmap for rendering
     */
    fun getParcelOverlay(): ByteArray = parcelOverlay.copyOf()
    
    /**
     * Check if parcel overlay is complete
     */
    fun isParcelOverlayComplete(): Boolean = parcelOverlaySequence >= 3
    
    fun shutdown() {
        scope.cancel()
    }
}

data class ParcelInfo(
    val localId: Int,
    val ownerId: UUID,
    val groupId: UUID?,
    val name: String,
    val description: String,
    val claimDate: Long,
    val claimPrice: Int,
    val rentPrice: Int,
    val aabbMin: LLVector3,
    val aabbMax: LLVector3,
    val area: Int,
    val actualArea: Int,
    val simWideMaxPrims: Int,
    val simWideTotal: Int,
    val maxPrims: Int,
    val totalPrims: Int,
    val ownerPrims: Int,
    val groupPrims: Int,
    val otherPrims: Int,
    val selectedPrims: Int,
    val parcelPrimBonus: Float,
    val cleanTime: Int,
    val flags: Long,
    val landingType: Int,
    val musicUrl: String?,
    val mediaUrl: String?,
    val mediaId: UUID?,
    val mediaAutoScale: Boolean,
    val category: Int,
    val snapshotId: UUID?,
    val userLocation: LLVector3,
    val userLookAt: LLVector3,
    val status: Int,
    val passPrice: Int,
    val passHours: Float
) {
    // Helper properties
    val allowFly: Boolean get() = (flags and ParcelManager.FLAG_ALLOW_FLY) != 0L
    val allowScripts: Boolean get() = (flags and ParcelManager.FLAG_ALLOW_SCRIPTS) != 0L
    val allowBuild: Boolean get() = (flags and ParcelManager.FLAG_CREATE_OBJECTS) != 0L
    val allowDamage: Boolean get() = (flags and ParcelManager.FLAG_ALLOW_DAMAGE) != 0L
    val forSale: Boolean get() = (flags and ParcelManager.FLAG_FOR_SALE) != 0L
    val allowVoice: Boolean get() = (flags and ParcelManager.FLAG_ALLOW_VOICE_CHAT) != 0L
    val restrictPush: Boolean get() = (flags and ParcelManager.FLAG_RESTRICT_PUSHOBJECT) != 0L
    
    val sizeString: String get() = "$area sqm"
    val primUsage: String get() = "$totalPrims / $maxPrims"
}

enum class ReturnType {
    OWNER, GROUP, OTHER, LIST, SELL
}
