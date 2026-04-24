package com.linkpoint.inventory

import android.util.Log
import com.linkpoint.avatar.AvatarBaker
import com.linkpoint.avatar.WearableData
import com.linkpoint.avatar.WearableType
import com.linkpoint.objects.ObjectManager
import com.linkpoint.protocol.messages.MessageIds
import com.linkpoint.protocol.messages.UDPConnectionFixed
import com.linkpoint.protocol.types.putUUID
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.roundToInt

/**
 * Manages outfits and wearables
 * Handles wearing, removing, and outfit management
 */
class OutfitManager(
    private val inventoryManager: InventoryManager,
    private val baker: AvatarBaker,
    private val gestureManager: GestureManager,
    private val udpConnection: UDPConnectionFixed? = null,
    private val agentId: UUID? = null,
    private val sessionId: UUID? = null,
    private val objectManager: ObjectManager? = null,
    private val wearableAssetFetcher: (suspend (InventoryItem) -> ByteArray?)? = null
) {
    companion object {
        private const val TAG = "OutfitManager"
        
        // Attachment points
        const val ATTACH_CHEST = 1
        const val ATTACH_SKULL = 2
        const val ATTACH_LEFT_SHOULDER = 3
        const val ATTACH_RIGHT_SHOULDER = 4
        const val ATTACH_LEFT_HAND = 5
        const val ATTACH_RIGHT_HAND = 6
        const val ATTACH_LEFT_FOOT = 7
        const val ATTACH_RIGHT_FOOT = 8
        const val ATTACH_SPINE = 9
        const val ATTACH_PELVIS = 10
        const val ATTACH_MOUTH = 11
        const val ATTACH_CHIN = 12
        const val ATTACH_LEFT_EAR = 13
        const val ATTACH_RIGHT_EAR = 14
        const val ATTACH_LEFT_EYE = 15
        const val ATTACH_RIGHT_EYE = 16
        const val ATTACH_NOSE = 17
        const val ATTACH_RIGHT_UPPER_ARM = 18
        const val ATTACH_RIGHT_FOREARM = 19
        const val ATTACH_LEFT_UPPER_ARM = 20
        const val ATTACH_LEFT_FOREARM = 21
        const val ATTACH_RIGHT_HIP = 22
        const val ATTACH_RIGHT_UPPER_LEG = 23
        const val ATTACH_RIGHT_LOWER_LEG = 24
        const val ATTACH_LEFT_HIP = 25
        const val ATTACH_LEFT_UPPER_LEG = 26
        const val ATTACH_LEFT_LOWER_LEG = 27
        const val ATTACH_STOMACH = 28
        const val ATTACH_LEFT_PEC = 29
        const val ATTACH_RIGHT_PEC = 30
        const val ATTACH_CENTER_2 = 31
        const val ATTACH_TOP_RIGHT = 32
        const val ATTACH_TOP_CENTER = 33
        const val ATTACH_TOP_LEFT = 34
        const val ATTACH_CENTER = 35
        const val ATTACH_BOTTOM_LEFT = 36
        const val ATTACH_BOTTOM = 37
        const val ATTACH_BOTTOM_RIGHT = 38
        const val ATTACH_NECK = 39
        const val ATTACH_AVATAR_CENTER = 40
        const val ATTACH_LEFT_HAND_RING = 41
        const val ATTACH_RIGHT_HAND_RING = 42
        const val ATTACH_TAIL_BASE = 43
        const val ATTACH_TAIL_TIP = 44
        const val ATTACH_LEFT_WING = 45
        const val ATTACH_RIGHT_WING = 46
        const val ATTACH_JAW = 47
        const val ATTACH_ALT_LEFT_EAR = 48
        const val ATTACH_ALT_RIGHT_EAR = 49
        const val ATTACH_ALT_LEFT_EYE = 50
        const val ATTACH_ALT_RIGHT_EYE = 51
        const val ATTACH_TONGUE = 52
        const val ATTACH_GROIN = 53
        const val ATTACH_HIND_LEFT_FOOT = 54
        const val ATTACH_HIND_RIGHT_FOOT = 55

        // Protocol Message IDs
        private const val OBJECT_DETACH = (0xFFFF0118).toInt()
        
        // Attachment flags
        private const val ATTACHMENT_APPEND_FLAG = 0x80
    }
    
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    
    // Currently worn items
    private val wornWearables = ConcurrentHashMap<WearableType, UUID>()
    private val wornAttachments = ConcurrentHashMap<Int, UUID>()
    
    private val _isChangingOutfit = MutableStateFlow(false)
    val isChangingOutfit: StateFlow<Boolean> = _isChangingOutfit
    
    private val _currentOutfit = MutableStateFlow<List<UUID>>(emptyList())
    val currentOutfit: StateFlow<List<UUID>> = _currentOutfit
    
    /**
     * Wear an item
     */
    suspend fun wearItem(
        itemId: UUID,
        replace: Boolean = true,
        attachPoint: Int? = null
    ): Boolean = withContext(Dispatchers.Default) {
        val item = inventoryManager.getItem(itemId) ?: return@withContext false
        
        when (item.inventoryType) {
            InventoryType.WEARABLE -> wearWearable(item, replace)
            InventoryType.OBJECT -> {
                if (attachPoint != null) {
                    attachObject(item, attachPoint, replace)
                } else {
                    // Use default attach point from item flags
                    val defaultPoint = (item.flags and 0xFF)
                    attachObject(item, defaultPoint, replace)
                }
            }
            InventoryType.GESTURE -> activateGesture(item)
            else -> false
        }
    }
    
    private suspend fun wearWearable(item: InventoryItem, replace: Boolean): Boolean {
        val wearableType = WearableType.fromValue(item.flags and 0xFF)
        applyWearableSelection(wornWearables, wearableType, item.itemId, replace)
        
        // Load wearable data
        val wearableData = loadWearableData(item)
        if (wearableData != null) {
            baker.setWearable(wearableType, wearableData)
            rebakeWearableLayers(wearableType)
        } else {
            // Keep previous wearable state if parse/fetch failed to avoid dropping appearance.
            Log.w(TAG, "Skipping wearable apply for ${item.itemId} (${item.name}) due to missing/corrupt asset data")
        }

        updateCurrentOutfit()
        return true
    }

    private suspend fun loadWearableData(item: InventoryItem): WearableData? {
        val wearableType = WearableType.fromValue(item.flags and 0xFF)
        val fallback = wearableFallback(wearableType, item.assetId)
        val fetcher = wearableAssetFetcher

        if (fetcher == null) {
            Log.w(TAG, "No wearable asset fetcher configured; using fallback for ${item.itemId}")
            return fallback
        }

        return try {
            val bytes = fetcher(item)
            if (bytes == null) {
                Log.w(TAG, "Wearable asset fetch failed for item=${item.itemId} asset=${item.assetId}; using fallback")
                fallback
            } else {
                val parseResult = WearableAssetParser.parseWithDiagnostics(
                    raw = bytes,
                    defaultType = wearableType,
                    assetId = item.assetId
                )
                val parsed = parseResult.data
                if (parsed == null) {
                    Log.e(
                        TAG,
                        "Wearable parse failed for item=${item.itemId} asset=${item.assetId} " +
                            "(${parseResult.error ?: "unknown parse error"}, ${bytes.size} bytes); using fallback"
                    )
                    fallback
                } else {
                    parsed
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load wearable asset for item=${item.itemId}, falling back", e)
            fallback
        }
    }

    private fun wearableFallback(type: WearableType, assetId: UUID): WearableData {
        return WearableData(
            type = type,
            assetId = assetId,
            textures = emptyMap(),
            params = emptyMap()
        )
    }

    private suspend fun rebakeWearableLayers(wearableType: WearableType) {
        val channels = affectedBakeChannels(wearableType)
        if (channels.isEmpty()) {
            baker.bakeAll()
            return
        }

        channels.forEach { channel ->
            baker.bakeChannel(channel)
        }
    }

    internal fun affectedBakeChannels(wearableType: WearableType): Set<Int> = affectedBakeChannelsForType(wearableType)
    
    private suspend fun attachObject(item: InventoryItem, point: Int, replace: Boolean): Boolean {
        if (replace) {
            wornAttachments[point] = item.itemId
        } else {
            // Add to existing attachments at point
            wornAttachments[point] = item.itemId
        }
        
        updateCurrentOutfit()
        
        // RezSingleAttachmentFromInv packet
        if (udpConnection != null && agentId != null && sessionId != null) {
            try {
                sendRezSingleAttachmentFromInv(item, point, replace)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to send RezSingleAttachmentFromInv", e)
            }
        }

        return true
    }
    
    private suspend fun sendRezSingleAttachmentFromInv(item: InventoryItem, point: Int, replace: Boolean) {
        val nameBytes = item.name.toByteArray(Charsets.UTF_8)
        val descBytes = item.description.toByteArray(Charsets.UTF_8)

        // Ensure name/desc are within limits (1 byte length max 255)
        val safeNameBytes = if (nameBytes.size > 255) nameBytes.copyOf(255) else nameBytes
        val safeDescBytes = if (descBytes.size > 255) descBytes.copyOf(255) else descBytes

        // Calculate size:
        // AgentData: 16 (AgentID) + 16 (SessionID) = 32
        // ObjectData:
        //   ItemID (16)
        //   OwnerID (16)
        //   AttachmentPt (1)
        //   ItemFlags (4)
        //   GroupMask (4)
        //   EveryoneMask (4)
        //   NextOwnerMask (4)
        //   Name (1 + len)
        //   Description (1 + len)
        //   CreationDate (4)
        //   CRC (4)

        val payloadSize = 32 + 16 + 16 + 1 + 4 + 4 + 4 + 4 +
                          1 + safeNameBytes.size + 1 + safeDescBytes.size + 4 + 4

        val payload = ByteBuffer.allocate(payloadSize).order(ByteOrder.LITTLE_ENDIAN)

        // AgentData - Validate required fields
        val agentIdValue = agentId 
            ?: throw IllegalStateException("Agent ID not initialized in OutfitManager")
        val sessionIdValue = sessionId 
            ?: throw IllegalStateException("Session ID not initialized in OutfitManager")
        
        payload.putUUID(agentIdValue)
        payload.putUUID(sessionIdValue)

        // ObjectData
        payload.putUUID(item.itemId)
        payload.putUUID(item.permissions.ownerId)

        // AttachmentPt
        // ATTACHMENT_APPEND_FLAG means APPEND. If replace is false, we append.
        val attachPtByte = if (!replace) point or ATTACHMENT_APPEND_FLAG else point
        payload.put(attachPtByte.toByte())

        payload.putInt(item.flags)
        payload.putInt(item.permissions.groupMask)
        payload.putInt(item.permissions.everyoneMask)
        payload.putInt(item.permissions.nextOwnerMask)

        // Name (Variable 1)
        payload.put(safeNameBytes.size.toByte())
        payload.put(safeNameBytes)

        // Description (Variable 1)
        payload.put(safeDescBytes.size.toByte())
        payload.put(safeDescBytes)

        payload.putInt(item.creationDate)
        payload.putInt(0) // CRC

        // Validate UDP connection before sending
        val connection = udpConnection 
            ?: throw IllegalStateException("UDP connection not initialized in OutfitManager")
        connection.sendPacket(MessageIds.REZ_SINGLE_ATTACHMENT_FROM_INV, payload.array(), reliable = true)
        Log.d(TAG, "Sent RezSingleAttachmentFromInv for item ${item.itemId} at point $point (replace=$replace)")
    }
    
    private suspend fun activateGesture(item: InventoryItem): Boolean {
        return gestureManager.activateGesture(item.assetId, item.itemId)
    }
    
    /**
     * Remove a wearable
     */
    suspend fun removeWearable(type: WearableType): Boolean {
        wornWearables.remove(type)
        baker.removeWearable(type)
        rebakeWearableLayers(type)
        updateCurrentOutfit()
        return true
    }
    
    /**
     * Detach an object
     */
    suspend fun detachFromPoint(point: Int): Boolean {
        val itemId = wornAttachments[point]
        wornAttachments.remove(point)
        updateCurrentOutfit()

        // Send ObjectDetach to server
        if (udpConnection != null && agentId != null && sessionId != null) {
            try {
                sendObjectDetach(point, itemId)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to send ObjectDetach", e)
            }
        }
        return true
    }
    
    /**
     * Send ObjectDetach packet
     */
    private suspend fun sendObjectDetach(point: Int, itemId: UUID?) {
        if (udpConnection == null || agentId == null || sessionId == null) return

        if (itemId == null) {
             Log.w(TAG, "Cannot send ObjectDetach: Missing ItemID for point $point")
             return
        }

        // Try to get LocalID from ObjectManager if available
        var localId: Int = 0
        if (objectManager != null) {
            val obj = objectManager.getObjectByUUID(itemId)
            if (obj != null) {
                localId = obj.localId
            }
        }

        if (localId == 0) {
            Log.w(TAG, "Cannot send ObjectDetach: Missing LocalID for attachment at point $point (item $itemId)")
            // Some viewers might try to guess or use 0? No, 0 is invalid.
            return
        }

        // ObjectDetach packet format:
        // AgentData Block
        // - AgentID (16 bytes)
        // - SessionID (16 bytes)
        // ObjectData Block (Variable Array)
        // - ObjectLocalID (U32)

        val payload = ByteBuffer.allocate(16 + 16 + 1 + 4)
            .order(ByteOrder.LITTLE_ENDIAN) // Message body is little endian

        // AgentData - UUIDs are big-endian in payload if we use putUUID helper which might handle it?
        // Let's use putUUID from existing code or check how it does it.
        // ChatManager uses buffer.putUUID(agentId).
        // Let's see putUUID implementation in ChatManager context or copy it.
        // It seems `com.linkpoint.protocol.types.putUUID` is an extension method.
        // It likely handles endianness.

        // AgentData
        putUUID(payload, agentId)
        putUUID(payload, sessionId)

        // ObjectData (Array)
        payload.put(1.toByte()) // Count = 1
        payload.putInt(localId)

        udpConnection.sendPacket(OBJECT_DETACH, payload.array(), reliable = true)
        Log.i(TAG, "Sent ObjectDetach for localId $localId (point $point)")
    }

    private fun putUUID(buffer: ByteBuffer, uuid: UUID) {
        val originalOrder = buffer.order()
        buffer.order(ByteOrder.BIG_ENDIAN)
        buffer.putLong(uuid.mostSignificantBits)
        buffer.putLong(uuid.leastSignificantBits)
        buffer.order(originalOrder)
    }

    /**
     * Detach by item ID
     */
    suspend fun detachItem(itemId: UUID): Boolean {
        val point = wornAttachments.entries.find { it.value == itemId }?.key ?: return false
        return detachFromPoint(point)
    }
    
    /**
     * Wear an outfit folder
     */
    suspend fun wearOutfit(folderId: UUID, replace: Boolean = true): Boolean {
        _isChangingOutfit.value = true
        
        return withContext(Dispatchers.Default) {
            try {
                // Fetch folder contents
                inventoryManager.fetchFolderContents(folderId)
                val contents = inventoryManager.getFolderContents(folderId)
                
                if (replace) {
                    // Remove all current items
                    wornWearables.clear()
                    wornAttachments.clear()
                }
                
                // Wear each item
                for (node in contents) {
                    if (node is InventoryNode.Item) {
                        wearItem(node.item.itemId, replace = false)
                    }
                }
                
                true
            } catch (e: Exception) {
                Log.e(TAG, "Failed to wear outfit", e)
                false
            } finally {
                _isChangingOutfit.value = false
            }
        }
    }
    
    /**
     * Save current outfit as a new outfit folder
     */
    suspend fun saveOutfit(name: String): UUID? {
        val outfitsFolder = inventoryManager.getSystemFolder(
            InventoryManager.FOLDER_TYPE_MYOUTFITS
        ) ?: return null
        
        // Create new outfit folder
        val outfitFolderId = inventoryManager.createFolder(
            outfitsFolder,
            name,
            InventoryManager.FOLDER_TYPE_OUTFIT
        ) ?: return null
        
        // Copy current outfit items to folder
        for (itemId in wornWearables.values) {
            inventoryManager.copyItem(itemId, outfitFolderId)
        }
        
        for (itemId in wornAttachments.values) {
            inventoryManager.copyItem(itemId, outfitFolderId)
        }
        
        return outfitFolderId
    }
    
    /**
     * Check if item is worn
     */
    fun isWorn(itemId: UUID): Boolean {
        return wornWearables.containsValue(itemId) || wornAttachments.containsValue(itemId)
    }
    
    /**
     * Get worn wearable by type
     */
    fun getWornWearable(type: WearableType): UUID? = wornWearables[type]

    /**
     * Get inventory item details for a worn item.
     */
    fun getInventoryItem(itemId: UUID): InventoryItem? = inventoryManager.getItem(itemId)
    
    /**
     * Get attachment at point
     */
    fun getAttachmentAt(point: Int): UUID? = wornAttachments[point]
    
    /**
     * Get all worn items
     */
    fun getWornItems(): List<UUID> {
        return wornWearables.values.toList() + wornAttachments.values.toList()
    }
    
    private fun updateCurrentOutfit() {
        _currentOutfit.value = getWornItems()
    }
    
    fun shutdown() {
        scope.cancel()
    }
}

internal object WearableAssetParser {
    private const val NULL_UUID = "00000000-0000-0000-0000-000000000000"
    internal data class ParseResult(val data: WearableData?, val error: String? = null)

    fun parse(raw: ByteArray, defaultType: WearableType, assetId: UUID): WearableData? {
        return parseWithDiagnostics(raw, defaultType, assetId).data
    }

    fun parseWithDiagnostics(raw: ByteArray, defaultType: WearableType, assetId: UUID): ParseResult {
        val text = raw.toString(Charsets.UTF_8)
        val lines = text.lineSequence().map { it.trim() }.filter { it.isNotEmpty() }.toList()
        if (lines.isEmpty()) return ParseResult(null, "wearable asset is empty")

        var declaredType: WearableType? = null
        val params = mutableMapOf<Int, Float>()
        val textures = mutableMapOf<Int, UUID>()

        var index = 0
        while (index < lines.size) {
            val line = lines[index]
            when {
                line.startsWith("type ", ignoreCase = true) -> {
                    val value = line.substringAfter("type").trim().toIntOrNull()
                    if (value != null) {
                        declaredType = WearableType.fromValue(value)
                    }
                    index++
                }
                line.startsWith("parameters ", ignoreCase = true) -> {
                    val count = line.substringAfter("parameters").trim().toIntOrNull()
                        ?: return ParseResult(null, "invalid parameters count line: '$line'")
                    index++
                    repeat(count) {
                        if (index >= lines.size) {
                            return ParseResult(
                                null,
                                "parameters section truncated at entry ${it + 1} of $count"
                            )
                        }
                        val tokens = lines[index].split(Regex("\\s+"))
                        if (tokens.size < 2) {
                            return ParseResult(null, "invalid parameter row: '${lines[index]}'")
                        }
                        val paramId = tokens[0].toIntOrNull()
                            ?: return ParseResult(null, "invalid parameter id: '${tokens[0]}'")
                        val value = tokens[1].toFloatOrNull()
                            ?: return ParseResult(null, "invalid parameter value: '${tokens[1]}'")
                        params[paramId] = value
                        index++
                    }
                }
                line.startsWith("textures ", ignoreCase = true) -> {
                    val count = line.substringAfter("textures").trim().toIntOrNull()
                        ?: return ParseResult(null, "invalid textures count line: '$line'")
                    index++
                    repeat(count) {
                        if (index >= lines.size) {
                            return ParseResult(
                                null,
                                "textures section truncated at entry ${it + 1} of $count"
                            )
                        }
                        val tokens = lines[index].split(Regex("\\s+"))
                        if (tokens.size < 2) {
                            return ParseResult(null, "invalid texture row: '${lines[index]}'")
                        }
                        val textureIndex = tokens[0].toIntOrNull()
                            ?: return ParseResult(null, "invalid texture index: '${tokens[0]}'")
                        val uuid = runCatching { UUID.fromString(tokens[1]) }.getOrNull()
                            ?: return ParseResult(null, "invalid texture uuid: '${tokens[1]}'")
                        if (uuid.toString() != NULL_UUID) {
                            textures[textureIndex] = uuid
                        }
                        index++
                    }
                }
                else -> index++
            }
        }

        val type = declaredType ?: defaultType
        val tintColor = extractTintColor(params)
        return ParseResult(
            data = WearableData(type, assetId, textures.toMap(), params.toMap(), tintColor)
        )
    }

    private fun extractTintColor(params: Map<Int, Float>): IntArray? {
        // Common wearable color visual params (R/G/B) in normalized float range [0..1].
        val r = params[80] ?: return null
        val g = params[81] ?: return null
        val b = params[82] ?: return null
        return intArrayOf(
            (r.coerceIn(0f, 1f) * 255f).roundToInt(),
            (g.coerceIn(0f, 1f) * 255f).roundToInt(),
            (b.coerceIn(0f, 1f) * 255f).roundToInt()
        )
    }
}

object InventoryType {
    const val TEXTURE = 0
    const val SOUND = 1
    const val CALLINGCARD = 2
    const val LANDMARK = 3
    const val SCRIPT = 4
    const val CLOTHING = 5
    const val OBJECT = 6
    const val NOTECARD = 7
    const val CATEGORY = 8
    const val ROOT_CATEGORY = 9
    const val LSL2 = 10
    const val SNAPSHOT = 15
    const val ATTACHMENT = 17
    const val WEARABLE = 18
    const val ANIMATION = 19
    const val GESTURE = 20
    const val MESH = 22
    const val SETTINGS = 25
    const val MATERIAL = 26
}

internal fun applyWearableSelection(
    wearableMap: MutableMap<WearableType, UUID>,
    type: WearableType,
    itemId: UUID,
    _replace: Boolean
) {
    // Current behavior is consistent for replace and multi-wear:
    // one active wearable per type, newest choice wins.
    wearableMap[type] = itemId
}

internal fun affectedBakeChannelsForType(wearableType: WearableType): Set<Int> {
    return when (wearableType) {
        WearableType.SKIN -> setOf(
            AvatarBaker.BAKE_HEAD,
            AvatarBaker.BAKE_UPPER,
            AvatarBaker.BAKE_LOWER,
            AvatarBaker.BAKE_LEFTARM,
            AvatarBaker.BAKE_LEFTLEG,
            AvatarBaker.BAKE_AUX1,
            AvatarBaker.BAKE_AUX2,
            AvatarBaker.BAKE_AUX3
        )
        WearableType.TATTOO -> setOf(
            AvatarBaker.BAKE_HEAD,
            AvatarBaker.BAKE_LEFTARM,
            AvatarBaker.BAKE_LEFTLEG,
            AvatarBaker.BAKE_AUX1
        )
        WearableType.HAIR -> setOf(AvatarBaker.BAKE_HAIR)
        WearableType.EYES -> setOf(AvatarBaker.BAKE_EYES)
        WearableType.SHIRT, WearableType.UNDERSHIRT, WearableType.JACKET -> setOf(
            AvatarBaker.BAKE_UPPER,
            AvatarBaker.BAKE_AUX2
        )
        WearableType.PANTS, WearableType.UNDERPANTS -> setOf(
            AvatarBaker.BAKE_LOWER,
            AvatarBaker.BAKE_AUX3
        )
        else -> setOf(
            AvatarBaker.BAKE_HEAD,
            AvatarBaker.BAKE_UPPER,
            AvatarBaker.BAKE_LOWER
        )
    }
}
