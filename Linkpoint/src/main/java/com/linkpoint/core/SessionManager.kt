package com.linkpoint.core

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

/**
 * Manages the active session with a Second Life grid
 */
class SessionManager(private val context: Context) {
    
    companion object {
        private const val TAG = "SessionManager"
        private const val PREFS_NAME = "linkpoint_session"
        private const val KEY_TELEPORT_HISTORY = "teleport_history"
        private const val KEY_HOME_LOCATION = "home_location"
        private const val MAX_HISTORY_SIZE = 50
    }
    
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    
    // Session state
    private val _connectionState = MutableStateFlow(ConnectionState.DISCONNECTED)
    val connectionState: StateFlow<ConnectionState> = _connectionState
    
    private val _currentRegion = MutableStateFlow<RegionInfo?>(null)
    val currentRegion: StateFlow<RegionInfo?> = _currentRegion
    
    // Teleport history
    private val _teleportHistory = MutableStateFlow<List<TeleportHistoryEntry>>(emptyList())
    val teleportHistory: StateFlow<List<TeleportHistoryEntry>> = _teleportHistory
    
    // Session data
    private var sessionId: String? = null
    private var agentId: UUID? = null
    private var secureSessionId: String? = null
    
    // Avatar info
    private var avatarFirstName: String = ""
    private var avatarLastName: String = ""
    
    // Home location
    private var homeLocation: TeleportHistoryEntry? = null
    
    init {
        loadTeleportHistory()
        loadHomeLocation()
    }
    
    fun isConnected(): Boolean = _connectionState.value == ConnectionState.CONNECTED
    
    fun getAgentId(): UUID? = agentId
    fun getSessionId(): String? = sessionId
    fun getAvatarName(): String = "$avatarFirstName $avatarLastName".trim()
    
    /**
     * Called when login succeeds
     */
    fun onLoginSuccess(
        sessionId: String,
        agentId: UUID,
        secureSessionId: String,
        firstName: String,
        lastName: String,
        regionInfo: RegionInfo
    ) {
        this.sessionId = sessionId
        this.agentId = agentId
        this.secureSessionId = secureSessionId
        this.avatarFirstName = firstName
        this.avatarLastName = lastName
        
        _currentRegion.value = regionInfo
        _connectionState.value = ConnectionState.CONNECTED
        
        // Add to teleport history
        addToHistory(regionInfo)
        
        Log.i(TAG, "Session established for $firstName $lastName in ${regionInfo.name}")
    }
    
    /**
     * Called when disconnecting
     */
    fun disconnect() {
        Log.i(TAG, "Disconnecting session")
        
        sessionId = null
        agentId = null
        secureSessionId = null
        avatarFirstName = ""
        avatarLastName = ""
        
        _currentRegion.value = null
        _connectionState.value = ConnectionState.DISCONNECTED
    }
    
    /**
     * Called when teleporting to a new region
     */
    fun onRegionChanged(regionInfo: RegionInfo) {
        _currentRegion.value = regionInfo
        addToHistory(regionInfo)
        Log.i(TAG, "Teleported to ${regionInfo.name}")
    }
    
    /**
     * Update the region name after receiving RegionHandshake.
     * This updates the region info with the actual name from the simulator.
     */
    fun updateRegionName(regionName: String) {
        val current = _currentRegion.value
        if (current != null && current.name != regionName) {
            _currentRegion.value = current.copy(name = regionName)
            Log.i(TAG, "Region name updated: ${current.name} -> $regionName")
        } else if (current == null) {
            // Create minimal region info if we don't have any yet
            _currentRegion.value = RegionInfo(
                name = regionName,
                handle = 0,
                x = 128,
                y = 128,
                simIP = "",
                simPort = 0,
                seedCapability = null
            )
            Log.i(TAG, "Region info created for: $regionName")
        }
    }
    
    /**
     * Update connection state
     */
    fun setConnectionState(state: ConnectionState) {
        _connectionState.value = state
    }
    
    // ==================== TELEPORT HISTORY ====================
    
    /**
     * Add current location to teleport history.
     */
    private fun addToHistory(regionInfo: RegionInfo) {
        val entry = TeleportHistoryEntry(
            regionName = regionInfo.name,
            x = regionInfo.x,
            y = regionInfo.y,
            z = 25, // Default height if not known
            timestamp = System.currentTimeMillis()
        )
        
        val currentHistory = _teleportHistory.value.toMutableList()
        
        // Remove duplicate if exists
        currentHistory.removeAll { it.regionName == entry.regionName }
        
        // Add to front
        currentHistory.add(0, entry)
        
        // Limit size
        if (currentHistory.size > MAX_HISTORY_SIZE) {
            currentHistory.removeAt(currentHistory.size - 1)
        }
        
        _teleportHistory.value = currentHistory
        saveTeleportHistory()
    }
    
    /**
     * Get teleport history.
     */
    fun getTeleportHistory(): List<TeleportHistoryEntry> = _teleportHistory.value
    
    /**
     * Clear teleport history.
     */
    fun clearTeleportHistory() {
        _teleportHistory.value = emptyList()
        prefs.edit().remove(KEY_TELEPORT_HISTORY).apply()
    }
    
    private fun loadTeleportHistory() {
        val json = prefs.getString(KEY_TELEPORT_HISTORY, null) ?: return
        try {
            val array = JSONArray(json)
            val history = mutableListOf<TeleportHistoryEntry>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                history.add(TeleportHistoryEntry(
                    regionName = obj.getString("regionName"),
                    x = obj.getInt("x"),
                    y = obj.getInt("y"),
                    z = obj.optInt("z", 25),
                    timestamp = obj.getLong("timestamp")
                ))
            }
            _teleportHistory.value = history
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load teleport history", e)
        }
    }
    
    private fun saveTeleportHistory() {
        try {
            val array = JSONArray()
            for (entry in _teleportHistory.value) {
                val obj = JSONObject()
                obj.put("regionName", entry.regionName)
                obj.put("x", entry.x)
                obj.put("y", entry.y)
                obj.put("z", entry.z)
                obj.put("timestamp", entry.timestamp)
                array.put(obj)
            }
            prefs.edit().putString(KEY_TELEPORT_HISTORY, array.toString()).apply()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save teleport history", e)
        }
    }
    
    // ==================== HOME LOCATION ====================
    
    /**
     * Set current location as home.
     */
    fun setHomeHere() {
        val region = _currentRegion.value ?: return
        homeLocation = TeleportHistoryEntry(
            regionName = region.name,
            x = region.x,
            y = region.y,
            z = 25,
            timestamp = System.currentTimeMillis()
        )
        saveHomeLocation()
        Log.i(TAG, "Home location set to ${region.name}")
    }
    
    /**
     * Get home location.
     */
    fun getHomeLocation(): TeleportHistoryEntry? = homeLocation
    
    /**
     * Teleport home.
     */
    suspend fun teleportHome() {
        val home = homeLocation
        if (home != null) {
            // Would trigger teleport via capabilities
            Log.i(TAG, "Teleporting home to ${home.regionName}")
        } else {
            Log.w(TAG, "No home location set")
        }
    }
    
    private fun loadHomeLocation() {
        val json = prefs.getString(KEY_HOME_LOCATION, null) ?: return
        try {
            val obj = JSONObject(json)
            homeLocation = TeleportHistoryEntry(
                regionName = obj.getString("regionName"),
                x = obj.getInt("x"),
                y = obj.getInt("y"),
                z = obj.optInt("z", 25),
                timestamp = obj.getLong("timestamp")
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load home location", e)
        }
    }
    
    private fun saveHomeLocation() {
        try {
            val home = homeLocation ?: return
            val obj = JSONObject()
            obj.put("regionName", home.regionName)
            obj.put("x", home.x)
            obj.put("y", home.y)
            obj.put("z", home.z)
            obj.put("timestamp", home.timestamp)
            prefs.edit().putString(KEY_HOME_LOCATION, obj.toString()).apply()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save home location", e)
        }
    }
    
    // ==================== AGENT DATA ====================
    
    // Agent group/title data (from AgentDataUpdate message)
    private var _activeGroupId: UUID? = null
    private var _activeGroupTitle: String = ""
    private var _activeGroupPowers: Long = 0
    private var _activeGroupName: String = ""
    
    val activeGroupId: UUID? get() = _activeGroupId
    val activeGroupTitle: String get() = _activeGroupTitle
    val activeGroupName: String get() = _activeGroupName
    
    /**
     * Update agent data from AgentDataUpdate message
     */
    fun updateAgentData(
        firstName: String,
        lastName: String,
        groupTitle: String,
        activeGroupId: UUID?,
        groupPowers: Long,
        groupName: String
    ) {
        // Update name if provided (non-empty)
        if (firstName.isNotEmpty()) {
            this.avatarFirstName = firstName
        }
        if (lastName.isNotEmpty()) {
            this.avatarLastName = lastName
        }
        
        // Update group data
        this._activeGroupId = activeGroupId
        this._activeGroupTitle = groupTitle
        this._activeGroupPowers = groupPowers
        this._activeGroupName = groupName
        
        Log.i(TAG, "Agent data updated: $avatarFirstName $avatarLastName, group='$groupTitle' ($groupName)")
    }
}

enum class ConnectionState {
    DISCONNECTED,
    CONNECTING,
    CONNECTED,
    RECONNECTING,
    ERROR
}

data class RegionInfo(
    val name: String,
    val handle: Long,
    val x: Int,
    val y: Int,
    val simIP: String,
    val simPort: Int,
    val seedCapability: String? = null
)

data class TeleportHistoryEntry(
    val regionName: String,
    val x: Int,
    val y: Int,
    val z: Int,
    val timestamp: Long
) {
    fun toSLURL(): String = "secondlife://$regionName/$x/$y/$z"
}
