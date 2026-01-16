package com.linkpoint.world

import android.util.Log
import com.linkpoint.protocol.capabilities.CapabilityManager
import com.linkpoint.protocol.messages.UDPConnection
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * Manages region crossing and child agent connections.
 * 
 * When an avatar moves from one region to another, this manager:
 * 1. Establishes connection to the new region
 * 2. Transfers agent state
 * 3. Closes connection to old region
 * 
 * Also manages child agent connections to neighboring regions for seamless
 * region crossing (you can see into adjacent regions).
 * 
 * Based on LibreMetaverse NetworkManager region handling and
 * Firestorm LLAgent::teleportCore()
 * 
 * @see <a href="https://wiki.secondlife.com/wiki/Simulator/Region_Crossing">Region Crossing</a>
 */
class RegionCrossingManager(
    private val udpConnection: UDPConnection,
    private val capabilityManager: CapabilityManager
) {
    companion object {
        private const val TAG = "RegionCrossing"
        
        // Region size in meters
        const val REGION_SIZE = 256
        
        // Max child agents (neighboring regions we're connected to)
        const val MAX_CHILD_AGENTS = 9
        
        // Time to keep child connections alive (ms)
        const val CHILD_CONNECTION_TIMEOUT_MS = 300_000L // 5 minutes
    }
    
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    // Current region info
    private val _currentRegion = MutableStateFlow<RegionInfo?>(null)
    val currentRegion: StateFlow<RegionInfo?> = _currentRegion
    
    // Child agent connections (for neighboring regions)
    private val childConnections = ConcurrentHashMap<Long, ChildAgentConnection>()
    
    // Region crossing events
    private val _crossingEvents = MutableSharedFlow<RegionCrossingEvent>(extraBufferCapacity = 10)
    val crossingEvents: SharedFlow<RegionCrossingEvent> = _crossingEvents
    
    // Crossing state
    @Volatile private var isCrossing = false
    
    /**
     * Set the current region after login or teleport.
     */
    fun setCurrentRegion(region: RegionInfo) {
        _currentRegion.value = region
        Log.i(TAG, "Current region set: ${region.name} (${region.handle})")
    }
    
    /**
     * Handle region crossing initiated by the simulator.
     * 
     * Called when we receive CrossedRegion or TeleportFinish from the sim.
     * 
     * @param newSimIP New simulator IP address
     * @param newSimPort New simulator port
     * @param newCircuitCode New circuit code for the connection
     * @param seedCapability Seed capability URL for the new region
     * @param regionHandle Handle of the new region (unique identifier)
     */
    suspend fun handleRegionCrossing(
        newSimIP: String,
        newSimPort: Int,
        newCircuitCode: Int,
        seedCapability: String,
        regionHandle: Long,
        regionName: String = "Unknown"
    ): Boolean {
        if (isCrossing) {
            Log.w(TAG, "Already crossing, ignoring duplicate request")
            return false
        }
        
        isCrossing = true
        Log.i(TAG, "═══════════════════════════════════════════════════════════════════")
        Log.i(TAG, "║ REGION CROSSING INITIATED                                         ║")
        Log.i(TAG, "║ To: $regionName ($newSimIP:$newSimPort)                           ║")
        Log.i(TAG, "═══════════════════════════════════════════════════════════════════")
        
        scope.launch { _crossingEvents.emit(RegionCrossingEvent.Started(regionName)) }
        
        return try {
            val oldRegion = _currentRegion.value
            
            // 1. Establish connection to new region
            Log.d(TAG, "Step 1: Connecting to new region...")
            udpConnection.configure(newSimIP, newSimPort, newCircuitCode)
            
            if (!udpConnection.connect()) {
                throw Exception("Failed to connect to new region")
            }
            Log.d(TAG, "✓ Connected to new region")
            
            // 2. Fetch new capabilities
            Log.d(TAG, "Step 2: Fetching capabilities...")
            capabilityManager.setSeedCapability(seedCapability)
            capabilityManager.fetchCapabilities()
            Log.d(TAG, "✓ Capabilities fetched")
            
            // 3. Update current region info
            val newRegion = RegionInfo(
                handle = regionHandle,
                name = regionName,
                simIP = newSimIP,
                simPort = newSimPort,
                seedCapability = seedCapability
            )
            _currentRegion.value = newRegion
            
            // 4. Convert old region to child connection (if needed)
            if (oldRegion != null && oldRegion.handle != regionHandle) {
                Log.d(TAG, "Step 4: Converting old region to child agent...")
                // In practice, the simulator handles this, but we track it
                // Old region connection is automatically closed when we reconfigure
            }
            
            // 5. Start agent updates in new region
            Log.d(TAG, "Step 5: Starting agent updates...")
            udpConnection.startAgentUpdates()
            
            Log.i(TAG, "═══════════════════════════════════════════════════════════════════")
            Log.i(TAG, "║ REGION CROSSING COMPLETE                                          ║")
            Log.i(TAG, "═══════════════════════════════════════════════════════════════════")
            
            scope.launch { _crossingEvents.emit(RegionCrossingEvent.Completed(newRegion)) }
            true
            
        } catch (e: Exception) {
            Log.e(TAG, "Region crossing failed", e)
            scope.launch { _crossingEvents.emit(RegionCrossingEvent.Failed(e.message ?: "Unknown error")) }
            false
        } finally {
            isCrossing = false
        }
    }
    
    /**
     * Handle child agent update from simulator.
     * 
     * The simulator sends these to tell us about neighboring regions
     * we should maintain connections to.
     */
    fun handleChildAgentUpdate(
        regionHandle: Long,
        simIP: String,
        simPort: Int,
        seedCapability: String
    ) {
        // Store child connection info
        val connection = ChildAgentConnection(
            regionHandle = regionHandle,
            simIP = simIP,
            simPort = simPort,
            seedCapability = seedCapability,
            lastUpdate = System.currentTimeMillis()
        )
        childConnections[regionHandle] = connection
        
        Log.d(TAG, "Child agent updated: $regionHandle ($simIP:$simPort)")
        
        // Clean up old child connections
        cleanupOldChildConnections()
    }
    
    /**
     * Get neighboring region that would be entered if moving in given direction.
     */
    fun getNeighborRegion(localX: Float, localY: Float): Long? {
        val currentHandle = _currentRegion.value?.handle ?: return null
        
        // Decode current region coordinates from handle
        val currentRegionX = (currentHandle shr 40).toInt()
        val currentRegionY = ((currentHandle shr 8) and 0xFFFFFFFF).toInt()
        
        // Check boundaries
        val neighborX = when {
            localX < 0 -> currentRegionX - REGION_SIZE
            localX >= REGION_SIZE -> currentRegionX + REGION_SIZE
            else -> currentRegionX
        }
        
        val neighborY = when {
            localY < 0 -> currentRegionY - REGION_SIZE
            localY >= REGION_SIZE -> currentRegionY + REGION_SIZE
            else -> currentRegionY
        }
        
        // If we're still in current region, no crossing needed
        if (neighborX == currentRegionX && neighborY == currentRegionY) {
            return null
        }
        
        // Compute neighbor handle
        val neighborHandle = (neighborX.toLong() shl 40) or (neighborY.toLong() shl 8)
        
        // Check if we have a child connection to this region
        return if (childConnections.containsKey(neighborHandle)) {
            neighborHandle
        } else {
            Log.w(TAG, "No child connection for neighbor region: $neighborHandle")
            null
        }
    }
    
    /**
     * Check if we're near a region border.
     * Used to proactively establish child connections.
     */
    fun isNearRegionBorder(localX: Float, localY: Float, threshold: Float = 10f): Boolean {
        return localX < threshold || localX > (REGION_SIZE - threshold) ||
               localY < threshold || localY > (REGION_SIZE - threshold)
    }
    
    /**
     * Get all active child connections.
     */
    fun getChildConnections(): List<ChildAgentConnection> {
        return childConnections.values.toList()
    }
    
    /**
     * Clean up expired child connections.
     */
    private fun cleanupOldChildConnections() {
        val now = System.currentTimeMillis()
        val toRemove = childConnections.filter { 
            now - it.value.lastUpdate > CHILD_CONNECTION_TIMEOUT_MS 
        }.keys
        
        toRemove.forEach { handle ->
            childConnections.remove(handle)
            Log.d(TAG, "Removed stale child connection: $handle")
        }
    }
    
    /**
     * Clear all child connections (e.g., on logout).
     */
    fun clearChildConnections() {
        childConnections.clear()
        Log.d(TAG, "Cleared all child connections")
    }
    
    /**
     * Check if currently crossing regions.
     */
    fun isCrossing(): Boolean = isCrossing
    
    fun shutdown() {
        clearChildConnections()
        scope.cancel()
    }
}

/**
 * Information about a region.
 */
data class RegionInfo(
    val handle: Long,
    val name: String,
    val simIP: String,
    val simPort: Int,
    val seedCapability: String,
    val flags: Int = 0,
    val regionSizeX: Int = 256,
    val regionSizeY: Int = 256
) {
    /**
     * Get global X coordinate of region.
     */
    val globalX: Int get() = (handle shr 40).toInt()
    
    /**
     * Get global Y coordinate of region.
     */
    val globalY: Int get() = ((handle shr 8) and 0xFFFFFFFF).toInt()
}

/**
 * Child agent connection to a neighboring region.
 */
data class ChildAgentConnection(
    val regionHandle: Long,
    val simIP: String,
    val simPort: Int,
    val seedCapability: String,
    val lastUpdate: Long
)

/**
 * Region crossing events.
 */
sealed class RegionCrossingEvent {
    data class Started(val targetRegion: String) : RegionCrossingEvent()
    data class Completed(val region: RegionInfo) : RegionCrossingEvent()
    data class Failed(val error: String) : RegionCrossingEvent()
}
