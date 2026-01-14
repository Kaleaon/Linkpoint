package com.linkpoint.core

import android.content.Context
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.UUID

/**
 * Manages the active session with a Second Life grid
 */
class SessionManager(private val context: Context) {
    
    companion object {
        private const val TAG = "SessionManager"
    }
    
    // Session state
    private val _connectionState = MutableStateFlow(ConnectionState.DISCONNECTED)
    val connectionState: StateFlow<ConnectionState> = _connectionState
    
    private val _currentRegion = MutableStateFlow<RegionInfo?>(null)
    val currentRegion: StateFlow<RegionInfo?> = _currentRegion
    
    // Session data
    private var sessionId: String? = null
    private var agentId: UUID? = null
    private var secureSessionId: String? = null
    
    // Avatar info
    private var avatarFirstName: String = ""
    private var avatarLastName: String = ""
    
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
