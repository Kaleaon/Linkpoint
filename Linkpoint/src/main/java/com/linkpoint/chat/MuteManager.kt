package com.linkpoint.chat

import android.content.Context
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * Manages the mute list for blocking users and objects.
 * 
 * The mute list (also called "block list") prevents:
 * - Receiving chat from muted agents/objects
 * - Receiving IMs from muted agents
 * - Seeing objects owned by muted agents
 * - Hearing sounds from muted sources
 * 
 * This matches the behavior in Firestorm, Alchemy, and other official viewers.
 * 
 * @see <a href="https://wiki.secondlife.com/wiki/Mute_List">SL Mute List</a>
 */
class MuteManager(context: Context) {
    
    companion object {
        private const val TAG = "MuteManager"
        private const val PREFS_NAME = "linkpoint_mute_list"
        private const val KEY_MUTED_AGENTS = "muted_agents"
        private const val KEY_MUTED_OBJECTS = "muted_objects"
        private const val KEY_MUTED_GROUPS = "muted_groups"
        private const val KEY_MUTED_NAMES = "muted_names"
        
        // Serialization format: id|name|type|flags|timestamp
        private const val MUTE_ENTRY_FIELD_COUNT = 5
    }
    
    /**
     * Mute entry types matching the Second Life protocol.
     */
    enum class MuteType(val value: Int) {
        BY_NAME(0),    // Mute by avatar name (legacy)
        AGENT(1),      // Mute by agent UUID
        OBJECT(2),     // Mute by object UUID
        GROUP(3),      // Mute by group UUID
        EXTERNAL(4)    // External source (script-added)
    }
    
    /**
     * Mute flags for fine-grained control.
     */
    object MuteFlags {
        const val NONE = 0
        const val TEXT_CHAT = 1       // Block text chat
        const val VOICE_CHAT = 2      // Block voice
        const val PARTICLES = 4        // Block particles
        const val SOUNDS = 8           // Block sounds
        const val ALL = TEXT_CHAT or VOICE_CHAT or PARTICLES or SOUNDS
    }
    
    /**
     * Represents a single mute entry.
     */
    data class MuteEntry(
        val id: UUID,
        val name: String,
        val type: MuteType,
        val flags: Int = MuteFlags.ALL,
        val timestamp: Long = System.currentTimeMillis()
    )
    
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    
    // In-memory caches for fast lookup
    private val mutedAgents = ConcurrentHashMap<UUID, MuteEntry>()
    private val mutedObjects = ConcurrentHashMap<UUID, MuteEntry>()
    private val mutedGroups = ConcurrentHashMap<UUID, MuteEntry>()
    private val mutedByName = ConcurrentHashMap<String, MuteEntry>()
    
    // Observable state for UI
    private val _muteListChanged = MutableStateFlow(0L)
    val muteListChanged: StateFlow<Long> = _muteListChanged
    
    init {
        loadMuteList()
    }
    
    /**
     * Check if a source is muted.
     * 
     * @param sourceId UUID of the source (agent, object, or group)
     * @param sourceName Optional name for legacy name-based mutes
     * @return true if the source is muted
     */
    fun isMuted(sourceId: UUID, sourceName: String? = null): Boolean {
        // Check UUID-based mutes
        if (mutedAgents.containsKey(sourceId)) return true
        if (mutedObjects.containsKey(sourceId)) return true
        if (mutedGroups.containsKey(sourceId)) return true
        
        // Check name-based mutes (legacy)
        if (sourceName != null && mutedByName.containsKey(sourceName.lowercase())) {
            return true
        }
        
        return false
    }
    
    /**
     * Check if a specific mute flag applies.
     */
    fun isMutedForFlag(sourceId: UUID, flag: Int): Boolean {
        val entry = mutedAgents[sourceId] 
            ?: mutedObjects[sourceId] 
            ?: mutedGroups[sourceId]
            ?: return false
        
        return (entry.flags and flag) != 0
    }
    
    /**
     * Check if text chat from a source should be blocked.
     */
    fun isTextMuted(sourceId: UUID): Boolean {
        return isMutedForFlag(sourceId, MuteFlags.TEXT_CHAT)
    }
    
    /**
     * Check if voice from a source should be blocked.
     */
    fun isVoiceMuted(sourceId: UUID): Boolean {
        return isMutedForFlag(sourceId, MuteFlags.VOICE_CHAT)
    }
    
    /**
     * Check if sounds from a source should be blocked.
     */
    fun isSoundMuted(sourceId: UUID): Boolean {
        return isMutedForFlag(sourceId, MuteFlags.SOUNDS)
    }
    
    /**
     * Mute an agent.
     */
    fun muteAgent(agentId: UUID, name: String, flags: Int = MuteFlags.ALL) {
        val entry = MuteEntry(agentId, name, MuteType.AGENT, flags)
        mutedAgents[agentId] = entry
        saveMuteList()
        notifyChange()
        Log.i(TAG, "Muted agent: $name ($agentId)")
    }
    
    /**
     * Mute an object.
     */
    fun muteObject(objectId: UUID, name: String, flags: Int = MuteFlags.ALL) {
        val entry = MuteEntry(objectId, name, MuteType.OBJECT, flags)
        mutedObjects[objectId] = entry
        saveMuteList()
        notifyChange()
        Log.i(TAG, "Muted object: $name ($objectId)")
    }
    
    /**
     * Mute a group.
     */
    fun muteGroup(groupId: UUID, name: String, flags: Int = MuteFlags.ALL) {
        val entry = MuteEntry(groupId, name, MuteType.GROUP, flags)
        mutedGroups[groupId] = entry
        saveMuteList()
        notifyChange()
        Log.i(TAG, "Muted group: $name ($groupId)")
    }
    
    /**
     * Mute by name (legacy).
     */
    fun muteByName(name: String, flags: Int = MuteFlags.ALL) {
        val entry = MuteEntry(UUID(0, 0), name, MuteType.BY_NAME, flags)
        mutedByName[name.lowercase()] = entry
        saveMuteList()
        notifyChange()
        Log.i(TAG, "Muted by name: $name")
    }
    
    /**
     * Unmute an agent.
     */
    fun unmuteAgent(agentId: UUID) {
        val removed = mutedAgents.remove(agentId)
        if (removed != null) {
            saveMuteList()
            notifyChange()
            Log.i(TAG, "Unmuted agent: ${removed.name} ($agentId)")
        }
    }
    
    /**
     * Unmute an object.
     */
    fun unmuteObject(objectId: UUID) {
        val removed = mutedObjects.remove(objectId)
        if (removed != null) {
            saveMuteList()
            notifyChange()
            Log.i(TAG, "Unmuted object: ${removed.name} ($objectId)")
        }
    }
    
    /**
     * Unmute a group.
     */
    fun unmuteGroup(groupId: UUID) {
        val removed = mutedGroups.remove(groupId)
        if (removed != null) {
            saveMuteList()
            notifyChange()
            Log.i(TAG, "Unmuted group: ${removed.name} ($groupId)")
        }
    }
    
    /**
     * Unmute by name.
     */
    fun unmuteByName(name: String) {
        val removed = mutedByName.remove(name.lowercase())
        if (removed != null) {
            saveMuteList()
            notifyChange()
            Log.i(TAG, "Unmuted by name: $name")
        }
    }
    
    /**
     * Get all muted agents.
     */
    fun getMutedAgents(): List<MuteEntry> = mutedAgents.values.toList()
    
    /**
     * Get all muted objects.
     */
    fun getMutedObjects(): List<MuteEntry> = mutedObjects.values.toList()
    
    /**
     * Get all muted groups.
     */
    fun getMutedGroups(): List<MuteEntry> = mutedGroups.values.toList()
    
    /**
     * Get all mutes (combined list).
     */
    fun getAllMutes(): List<MuteEntry> {
        return mutedAgents.values.toList() +
               mutedObjects.values.toList() +
               mutedGroups.values.toList() +
               mutedByName.values.toList()
    }
    
    /**
     * Get total mute count.
     */
    fun getMuteCount(): Int {
        return mutedAgents.size + mutedObjects.size + mutedGroups.size + mutedByName.size
    }
    
    /**
     * Clear all mutes.
     */
    fun clearAll() {
        mutedAgents.clear()
        mutedObjects.clear()
        mutedGroups.clear()
        mutedByName.clear()
        saveMuteList()
        notifyChange()
        Log.i(TAG, "Cleared all mutes")
    }
    
    private fun notifyChange() {
        _muteListChanged.value = System.currentTimeMillis()
    }
    
    private fun loadMuteList() {
        try {
            // Load agents
            prefs.getStringSet(KEY_MUTED_AGENTS, emptySet())?.forEach { entry ->
                parseMuteEntry(entry)?.let { mutedAgents[it.id] = it }
            }
            
            // Load objects
            prefs.getStringSet(KEY_MUTED_OBJECTS, emptySet())?.forEach { entry ->
                parseMuteEntry(entry)?.let { mutedObjects[it.id] = it }
            }
            
            // Load groups
            prefs.getStringSet(KEY_MUTED_GROUPS, emptySet())?.forEach { entry ->
                parseMuteEntry(entry)?.let { mutedGroups[it.id] = it }
            }
            
            // Load names
            prefs.getStringSet(KEY_MUTED_NAMES, emptySet())?.forEach { entry ->
                parseMuteEntry(entry)?.let { mutedByName[it.name.lowercase()] = it }
            }
            
            Log.d(TAG, "Loaded mute list: ${getMuteCount()} entries")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load mute list", e)
        }
    }
    
    private fun saveMuteList() {
        try {
            prefs.edit()
                .putStringSet(KEY_MUTED_AGENTS, mutedAgents.values.map { serializeMuteEntry(it) }.toSet())
                .putStringSet(KEY_MUTED_OBJECTS, mutedObjects.values.map { serializeMuteEntry(it) }.toSet())
                .putStringSet(KEY_MUTED_GROUPS, mutedGroups.values.map { serializeMuteEntry(it) }.toSet())
                .putStringSet(KEY_MUTED_NAMES, mutedByName.values.map { serializeMuteEntry(it) }.toSet())
                .apply()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save mute list", e)
        }
    }
    
    private fun serializeMuteEntry(entry: MuteEntry): String {
        return "${entry.id}|${entry.name}|${entry.type.value}|${entry.flags}|${entry.timestamp}"
    }
    
    private fun parseMuteEntry(serialized: String): MuteEntry? {
        return try {
            val parts = serialized.split("|")
            if (parts.size >= MUTE_ENTRY_FIELD_COUNT) {
                MuteEntry(
                    id = UUID.fromString(parts[0]),
                    name = parts[1],
                    type = MuteType.values().find { it.value == parts[2].toInt() } ?: MuteType.AGENT,
                    flags = parts[3].toInt(),
                    timestamp = parts[4].toLong()
                )
            } else null
        } catch (e: Exception) {
            Log.w(TAG, "Failed to parse mute entry: $serialized")
            null
        }
    }
}
