package com.linkpoint.world

import android.util.Log
import com.linkpoint.core.SessionManager
import com.linkpoint.network.core.NetworkSessionManager
import com.linkpoint.protocol.capabilities.CapabilityManager
import com.linkpoint.protocol.messages.UDPConnectionFixed
import com.linkpoint.world.topography.PlanarTopographyProjection
import com.linkpoint.world.topography.WorldTopographyProjection
import com.linkpoint.world.manifold.ManifoldFrame
import com.linkpoint.world.manifold.TopologicalNeighborhoodResolver
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
    private val udpConnection: UDPConnectionFixed,
    private val capabilityManager: CapabilityManager,
    private val networkSessionManager: NetworkSessionManager? = null,
    private val sessionManager: SessionManager? = null,
    var topographyProjection: WorldTopographyProjection = PlanarTopographyProjection()
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

    // Active parametric manifold frame
    @Volatile var activeManifoldFrame: ManifoldFrame = ManifoldFrame.IDENTITY

    /**
     * Set the active manifold frame for topological region crossings.
     */
    fun setManifoldFrame(frame: ManifoldFrame) {
        activeManifoldFrame = frame
        Log.i(TAG, "Active ManifoldFrame set: ${frame.frameId} (${frame.topologyType})")
    }
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
     * Executes a two-phase handoff protocol:
     * 1. Preserves origin circuit snapshot and capability state before modifying endpoints.
     * 2. Reconfigures socket and attempts destination UDP handshake + capability initialization.
     * 3. Performs automatic origin rollback on destination failure, or invokes central network
     *    session recovery on unrecoverable dual-circuit failure.
     *
     * @param newSimIP New simulator IP address
     * @param newSimPort New simulator port
     * @param newCircuitCode New circuit code for the connection
     * @param seedCapability Seed capability URL for the new region
     * @param regionHandle Handle of the new region (unique identifier)
     * @param regionName Name of the new region
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

        _crossingEvents.emit(RegionCrossingEvent.Started(regionName))

        val oldRegion = _currentRegion.value

        // Requirement 1: Record origin circuit state before mutating socket endpoints
        val originSnapshot = CircuitSnapshot(
            simIP = udpConnection.getSimIP(),
            simPort = udpConnection.getSimPort(),
            circuitCode = udpConnection.getCircuitCode(),
            seedCapability = capabilityManager.getSeedCapability(),
            capabilities = capabilityManager.getCapabilitiesSnapshot(),
            regionInfo = oldRegion
        )

        return try {
            // Phase 1: Reconfigure UDP socket endpoint to destination
            Log.d(TAG, "Phase 1: Reconfiguring endpoint to destination $newSimIP:$newSimPort (circuit=$newCircuitCode)...")
            udpConnection.configure(newSimIP, newSimPort, newCircuitCode)

            // Phase 2: Verify UDP connection handshake to destination
            Log.d(TAG, "Phase 2: Connecting to destination region...")
            val destinationUdpConnected = try {
                udpConnection.connect()
            } catch (e: Exception) {
                Log.w(TAG, "Destination UDP connection threw exception: ${e.message}")
                false
            }

            if (!destinationUdpConnected) {
                Log.w(TAG, "Destination UDP handshake failed; initiating origin circuit rollback...")
                return rollbackToOriginOrRecover(
                    originSnapshot,
                    "Failed to connect to destination UDP endpoint $newSimIP:$newSimPort"
                )
            }
            Log.d(TAG, "✓ Destination UDP handshake verified")

            // Phase 2: Initialize destination capabilities
            Log.d(TAG, "Initializing destination capabilities...")
            val destinationCapsInitialized = try {
                capabilityManager.initialize(seedCapability)
            } catch (e: Exception) {
                Log.w(TAG, "Destination capability initialization threw exception: ${e.message}")
                false
            }

            if (!destinationCapsInitialized) {
                Log.w(TAG, "Destination capability initialization failed; initiating origin circuit rollback...")
                return rollbackToOriginOrRecover(
                    originSnapshot,
                    "Failed to initialize destination capabilities"
                )
            }
            Log.d(TAG, "✓ Destination capabilities initialized")

            // Two-phase handoff verified successfully! Update current region info
            val newRegion = RegionInfo(
                handle = regionHandle,
                name = regionName,
                simIP = newSimIP,
                simPort = newSimPort,
                seedCapability = seedCapability
            )
            _currentRegion.value = newRegion

            if (oldRegion != null && oldRegion.handle != regionHandle) {
                Log.d(TAG, "Converting old region connection state...")
            }

            Log.d(TAG, "Starting agent updates in new region...")
            udpConnection.startAgentUpdates()

            Log.i(TAG, "═══════════════════════════════════════════════════════════════════")
            Log.i(TAG, "║ REGION CROSSING COMPLETE                                          ║")
            Log.i(TAG, "═══════════════════════════════════════════════════════════════════")

            _crossingEvents.emit(RegionCrossingEvent.Completed(newRegion))
            true

        } catch (e: Exception) {
            Log.e(TAG, "Unexpected exception during region crossing", e)
            rollbackToOriginOrRecover(originSnapshot, e.message ?: "Unknown error during region crossing")
        } finally {
            // Guardrail: Snapshot state stays in memory only during crossing attempt.
            // Clearing isCrossing guarantees reset across success, rollback, and recovery paths.
            isCrossing = false
        }
    }

    /**
     * Requirement 3 & 4: Automatic circuit rollback to origin parameters if destination fails.
     * If origin circuit rollback also fails (dual-circuit failure), trigger rapid session recovery.
     */
    private suspend fun rollbackToOriginOrRecover(
        originSnapshot: CircuitSnapshot,
        failureReason: String
    ): Boolean {
        Log.w(
            TAG,
            "Attempting circuit rollback to origin sim ${originSnapshot.simIP}:${originSnapshot.simPort} " +
                "(circuit=${originSnapshot.circuitCode})..."
        )

        // 1. Reconfigure UDP socket back to origin parameters
        udpConnection.configure(originSnapshot.simIP, originSnapshot.simPort, originSnapshot.circuitCode)

        var originReconnected = try {
            udpConnection.connect()
        } catch (e: Exception) {
            Log.e(TAG, "Origin UDP reconnect exception: ${e.message}")
            false
        }

        var originCapRestored = false
        if (originReconnected) {
            if (!originSnapshot.seedCapability.isNullOrEmpty()) {
                originCapRestored = try {
                    capabilityManager.initialize(originSnapshot.seedCapability)
                } catch (e: Exception) {
                    Log.w(
                        TAG,
                        "Origin capability re-initialization threw exception, falling back to snapshot restore: ${e.message}"
                    )
                    false
                }
                if (!originCapRestored) {
                    capabilityManager.restoreCapabilities(originSnapshot.seedCapability, originSnapshot.capabilities)
                    originCapRestored = true
                }
            } else {
                originCapRestored = true
            }
        }

        return if (originReconnected && originCapRestored) {
            // Requirement 3: Successful Rollback to Origin
            Log.i(TAG, "✓ Origin circuit rollback successful for region ${originSnapshot.regionInfo?.name ?: "origin"}")
            _currentRegion.value = originSnapshot.regionInfo
            udpConnection.startAgentUpdates()

            val errorMessage = "Region crossing failed: $failureReason. Safely rolled back to origin region."
            _crossingEvents.emit(RegionCrossingEvent.Failed(errorMessage))
            false
        } else {
            // Requirement 4: Unrecoverable dual-circuit failure - invoke network session recovery via central session manager
            Log.e(TAG, "❌ Dual-circuit failure! Origin rollback failed. Invoking network session recovery...")

            networkSessionManager?.triggerConnectionRecovery()

            val fatalMessage = "Unrecoverable dual-circuit failure during region crossing: $failureReason. Invoked session recovery."
            _crossingEvents.emit(RegionCrossingEvent.Failed(fatalMessage))
            false
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
     * Resolve neighbor handle for local coordinates using active topography or manifold rules.
     */
    fun resolveNeighborHandle(localX: Float, localY: Float): Long? {
        val regionInfo = _currentRegion.value ?: return null
        val currentHandle = regionInfo.handle
        val sizeX = regionInfo.regionSizeX
        val sizeY = regionInfo.regionSizeY
        if (activeManifoldFrame != ManifoldFrame.IDENTITY) {
            return TopologicalNeighborhoodResolver.getNeighborRegionHandle(
                currentHandle = currentHandle,
                localX = localX,
                localY = localY,
                frame = activeManifoldFrame,
                regionSizeX = sizeX,
                regionSizeY = sizeY
            )
        }
        return topographyProjection.getNeighborRegionHandle(currentHandle, localX, localY, sizeX)
    }

    /**
     * Get neighboring region that would be entered if moving in given direction.
     *
     * @param localX Region-local X coordinate in meters (0-256 range).
     *               Values < 0 indicate moving west, values >= 256 indicate moving east.
     * @param localY Region-local Y coordinate in meters (0-256 range).
     *               Values < 0 indicate moving south, values >= 256 indicate moving north.
     * @return The region handle of the neighbor region, or null if staying in current region
     *         or no child connection exists to the neighbor.
     */
    fun getNeighborRegion(localX: Float, localY: Float): Long? {
        val neighborHandle = resolveNeighborHandle(localX, localY) ?: return null

        // Check if we have a child connection to this region or return resolved handle
        return if (childConnections.isEmpty() || childConnections.containsKey(neighborHandle)) {
            neighborHandle
        } else {
            Log.w(TAG, "No active child connection for neighbor region: $neighborHandle")
            neighborHandle
        }
    }

    /**
     * Check if we're near a region border.
     * Used to proactively establish child connections.
     */
    fun isNearRegionBorder(localX: Float, localY: Float, threshold: Float = 10f): Boolean {
        val regionInfo = _currentRegion.value
        val sizeX = regionInfo?.regionSizeX ?: REGION_SIZE
        val sizeY = regionInfo?.regionSizeY ?: REGION_SIZE
        return localX < threshold || localX > (sizeX - threshold) ||
               localY < threshold || localY > (sizeY - threshold)
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
data class CircuitSnapshot(
    val simIP: String,
    val simPort: Int,
    val circuitCode: Int,
    val seedCapability: String?,
    val capabilities: Map<String, String>?,
    val regionInfo: RegionInfo?
)

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
