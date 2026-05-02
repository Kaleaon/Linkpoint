package com.linkpoint.users

import android.util.Log
import com.linkpoint.protocol.messages.ids.MessageIdRegistry
import com.linkpoint.protocol.messages.UDPConnectionFixed
import com.linkpoint.protocol.transfer.XferManager
import com.linkpoint.protocol.transfer.XferResult
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.zip.CRC32

/**
 * Mute List Manager - Handles blocking/muting of avatars, objects, and groups.
 * 
 * Based on the reference viewer's SLMuteList.java
 * 
 * Mute types:
 * - AGENT: Block an avatar
 * - OBJECT: Block an object by name
 * - GROUP: Block a group
 * - BY_NAME: Block by name only (no UUID)
 * 
 * Mute flags:
 * - MUTE_CHAT: Block text chat
 * - MUTE_VOICE: Block voice
 * - MUTE_PARTICLES: Block particles
 * - MUTE_SOUNDS: Block sounds
 */
class MuteManager(
    private val udpConnection: UDPConnectionFixed,
    private val xferManager: XferManager,
    private val agentId: UUID
) {
    companion object {
        private const val TAG = "MuteManager"

        // Mute flags
        const val MUTE_CHAT = 0x00000001
        const val MUTE_VOICE = 0x00000002
        const val MUTE_PARTICLES = 0x00000004
        const val MUTE_SOUNDS = 0x00000008
        const val MUTE_ALL = MUTE_CHAT or MUTE_VOICE or MUTE_PARTICLES or MUTE_SOUNDS
    }
    
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    // Mute list data
    private val muteList = ConcurrentHashMap<MuteListKey, MuteListEntry>()
    
    // State flow for UI updates
    private val _muteListState = MutableStateFlow<List<MuteListEntry>>(emptyList())
    val muteListState: StateFlow<List<MuteListEntry>> = _muteListState
    
    // Cached CRC for server communication
    private var cachedCRC: Int = 0
    
    init {
        // Register xfer handler for mute list files
        xferManager.registerHandler("mute") { filename, result ->
            handleMuteListXfer(filename, result)
        }
    }
    
    /**
     * Request the mute list from the server.
     */
    suspend fun requestMuteList() {
        try {
            val payload = ByteBuffer.allocate(36).order(ByteOrder.LITTLE_ENDIAN)
            
            // AgentData block
            writeUUID(payload, agentId)
            writeUUID(payload, udpConnection.getSessionId())
            
            // MuteData block
            payload.putInt(cachedCRC)
            
            udpConnection.sendPacket(MessageIdRegistry.MUTE_LIST_REQUEST, payload.array(), reliable = true)
            Log.d(TAG, "Requested mute list (CRC: ${cachedCRC.toString(16)})")
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to request mute list", e)
        }
    }
    
    /**
     * Block/mute an avatar.
     */
    suspend fun muteAgent(
        avatarId: UUID,
        name: String,
        flags: Int = MUTE_ALL
    ) {
        val entry = MuteListEntry(
            id = avatarId,
            name = name,
            type = MuteType.AGENT,
            flags = flags
        )
        
        addMuteEntry(entry)
    }
    
    /**
     * Block/mute an object by name.
     */
    suspend fun muteObject(
        objectId: UUID,
        name: String,
        flags: Int = MUTE_ALL
    ) {
        val entry = MuteListEntry(
            id = objectId,
            name = name,
            type = MuteType.OBJECT,
            flags = flags
        )
        
        addMuteEntry(entry)
    }
    
    /**
     * Block/mute a group.
     */
    suspend fun muteGroup(
        groupId: UUID,
        name: String,
        flags: Int = MUTE_ALL
    ) {
        val entry = MuteListEntry(
            id = groupId,
            name = name,
            type = MuteType.GROUP,
            flags = flags
        )
        
        addMuteEntry(entry)
    }
    
    /**
     * Block by name only (no UUID).
     */
    suspend fun muteByName(
        name: String,
        flags: Int = MUTE_ALL
    ) {
        val entry = MuteListEntry(
            id = UUID(0, 0),
            name = name,
            type = MuteType.BY_NAME,
            flags = flags
        )
        
        addMuteEntry(entry)
    }
    
    /**
     * Add a mute entry and send to server.
     */
    private suspend fun addMuteEntry(entry: MuteListEntry) {
        val key = MuteListKey(entry.id, entry.name, entry.type)
        muteList[key] = entry
        updateState()
        
        try {
            val nameBytes = entry.name.toByteArray(Charsets.UTF_8)
            val payload = ByteBuffer.allocate(56 + nameBytes.size).order(ByteOrder.LITTLE_ENDIAN)
            
            // AgentData block
            writeUUID(payload, agentId)
            writeUUID(payload, udpConnection.getSessionId())
            
            // MuteData block
            writeUUID(payload, entry.id)
            payload.put(nameBytes.size.toByte())
            payload.put(nameBytes)
            payload.putInt(entry.type.ordinal)
            payload.putInt(entry.flags)
            
            udpConnection.sendPacket(MessageIdRegistry.UPDATE_MUTE_LIST_ENTRY, payload.array().copyOf(payload.position()), reliable = true)
            Log.i(TAG, "Added mute entry: ${entry.name} (${entry.type})")
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to add mute entry", e)
        }
    }
    
    /**
     * Unmute/unblock an entry.
     */
    suspend fun unmute(entry: MuteListEntry) {
        val key = MuteListKey(entry.id, entry.name, entry.type)
        muteList.remove(key)
        updateState()
        
        try {
            val nameBytes = entry.name.toByteArray(Charsets.UTF_8)
            val payload = ByteBuffer.allocate(36 + nameBytes.size).order(ByteOrder.LITTLE_ENDIAN)
            
            // AgentData block
            writeUUID(payload, agentId)
            writeUUID(payload, udpConnection.getSessionId())
            
            // MuteData block
            writeUUID(payload, entry.id)
            payload.put(nameBytes.size.toByte())
            payload.put(nameBytes)
            
            udpConnection.sendPacket(MessageIdRegistry.REMOVE_MUTE_LIST_ENTRY, payload.array().copyOf(payload.position()), reliable = true)
            Log.i(TAG, "Removed mute entry: ${entry.name} (${entry.type})")
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to remove mute entry", e)
        }
    }
    
    /**
     * Check if an avatar is muted.
     */
    fun isAgentMuted(avatarId: UUID): Boolean {
        val key = MuteListKey(avatarId, "", MuteType.AGENT)
        return muteList.keys.any { it.id == avatarId && it.type == MuteType.AGENT }
    }
    
    /**
     * Check if muted by name.
     */
    fun isMutedByName(name: String): Boolean {
        return muteList.values.any { 
            it.name.equals(name, ignoreCase = true) || 
            (it.type == MuteType.BY_NAME && it.name.equals(name, ignoreCase = true))
        }
    }
    
    /**
     * Check if a specific mute flag is set for an avatar.
     */
    fun isMuted(avatarId: UUID, flag: Int): Boolean {
        val entry = muteList.values.find { it.id == avatarId && it.type == MuteType.AGENT }
        return entry != null && (entry.flags and flag) != 0
    }
    
    /**
     * Get all muted entries.
     */
    fun getMuteList(): List<MuteListEntry> = muteList.values.toList()
    
    /**
     * Handle MuteListUpdate message (server sends filename to xfer).
     */
    fun handleMuteListUpdate(payload: ByteArray) {
        try {
            val buffer = ByteBuffer.wrap(payload).order(ByteOrder.LITTLE_ENDIAN)
            
            // Read filename
            val filenameLen = buffer.get().toInt() and 0xFF
            val filenameBytes = ByteArray(filenameLen)
            buffer.get(filenameBytes)
            val filename = String(filenameBytes, Charsets.UTF_8).trim('\u0000')
            
            if (filename.isNotEmpty()) {
                Log.d(TAG, "Mute list update: $filename")
                // The xfer manager will handle the file transfer
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing MuteListUpdate", e)
        }
    }
    
    /**
     * Handle UseCachedMuteList message.
     */
    fun handleUseCachedMuteList(payload: ByteArray) {
        Log.d(TAG, "Using cached mute list")
    }
    
    /**
     * Handle mute list xfer completion.
     */
    private fun handleMuteListXfer(filename: String, result: XferResult) {
        when (result) {
            is XferResult.Success -> {
                parseMuteListData(result.data)
            }
            is XferResult.Error -> {
                Log.e(TAG, "Failed to receive mute list: ${result.message}")
            }
        }
    }
    
    /**
     * Parse mute list data from xfer.
     */
    private fun parseMuteListData(data: ByteArray) {
        try {
            // Calculate CRC for caching
            val crc = CRC32()
            crc.update(data)
            cachedCRC = crc.value.toInt()
            
            // Clear existing list
            muteList.clear()
            
            // Parse the text data
            val content = String(data, Charsets.UTF_8)
            val lines = content.lines()
            
            for (line in lines) {
                if (line.isBlank()) continue
                
                val parts = line.split(" ", limit = 4)
                if (parts.size >= 3) {
                    try {
                        val type = MuteType.values()[parts[0].toInt()]
                        val id = UUID.fromString(parts[1])
                        val name = if (parts.size >= 4) parts[3] else parts[2]
                        val flags = if (parts.size >= 4) parts[2].toIntOrNull() ?: MUTE_ALL else MUTE_ALL
                        
                        val entry = MuteListEntry(id, name.trim(), type, flags)
                        val key = MuteListKey(id, name.trim(), type)
                        muteList[key] = entry
                    } catch (e: Exception) {
                        Log.w(TAG, "Failed to parse mute entry: $line")
                    }
                }
            }
            
            updateState()
            Log.i(TAG, "Loaded ${muteList.size} mute entries")
            
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing mute list data", e)
        }
    }
    
    private fun updateState() {
        _muteListState.value = muteList.values.toList()
    }
    
    private fun writeUUID(buffer: ByteBuffer, uuid: UUID) {
        buffer.putLong(uuid.mostSignificantBits)
        buffer.putLong(uuid.leastSignificantBits)
    }
    
    /**
     * Shutdown the manager.
     */
    fun shutdown() {
        scope.cancel()
        muteList.clear()
    }
}

/**
 * Mute entry types.
 */
enum class MuteType {
    AGENT,      // Avatar
    OBJECT,     // Object
    BY_NAME,    // Name only (no UUID)
    GROUP,      // Group
    EXTERNAL    // External (e.g., viewer-based)
}

/**
 * Mute list entry.
 */
data class MuteListEntry(
    val id: UUID,
    val name: String,
    val type: MuteType,
    val flags: Int = MuteManager.MUTE_ALL
) {
    val muteChat: Boolean get() = (flags and MuteManager.MUTE_CHAT) != 0
    val muteVoice: Boolean get() = (flags and MuteManager.MUTE_VOICE) != 0
    val muteParticles: Boolean get() = (flags and MuteManager.MUTE_PARTICLES) != 0
    val muteSounds: Boolean get() = (flags and MuteManager.MUTE_SOUNDS) != 0
}

/**
 * Key for mute list map.
 */
data class MuteListKey(
    val id: UUID,
    val name: String,
    val type: MuteType
)
