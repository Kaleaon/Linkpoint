package com.linkpoint

import android.app.Application
import android.util.Log
import com.linkpoint.assets.*
import com.linkpoint.utils.CrashReporter
import com.linkpoint.avatar.AvatarManager
import com.linkpoint.chat.ChatManager
import com.linkpoint.chat.IMManager
import com.linkpoint.core.GridManager
import com.linkpoint.core.SessionManager
import com.linkpoint.core.StartLocationManager
import com.linkpoint.core.DestinationGuide
import com.linkpoint.core.AvatarSelectionManager
import com.linkpoint.inventory.GestureManager
import com.linkpoint.inventory.InventoryManager
import com.linkpoint.inventory.OutfitManager
import com.linkpoint.network.NetworkSettings
import com.linkpoint.network.SecondLifeProtocol
import com.linkpoint.network.NetworkLogger
import com.linkpoint.objects.BuildTools
import com.linkpoint.objects.ObjectManager
import com.linkpoint.protocol.capabilities.CapabilityManager
import com.linkpoint.protocol.messages.UDPConnection
import com.linkpoint.protocol.messages.parseRegionHandshake
import com.linkpoint.protocol.messages.parseAgentMovementComplete
import com.linkpoint.render.RenderManager
import com.linkpoint.voice.VoiceManager
import com.linkpoint.world.FriendsManager
import com.linkpoint.world.ParcelManager
import com.linkpoint.world.ProfileManager
import com.linkpoint.world.SearchManager
import com.linkpoint.world.WorldMap
import com.linkpoint.groups.GroupsManager
import com.linkpoint.animesh.AnimeshManager
import com.linkpoint.bom.BakesOnMeshManager
import com.linkpoint.teleport.TeleportManager
import com.linkpoint.hud.HUDManager
import com.linkpoint.xr.XRManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * Main Application class for Linkpoint - Second Life viewer for Android and XR
 * 
 * Based on Lumiya's architecture, modernized for:
 * - Kotlin
 * - Filament rendering
 * - Android XR / VR support
 * - WebRTC voice
 */
class LinkpointApp : Application() {
    
    companion object {
        private const val TAG = "LinkpointApp"
        
        @Volatile
        private var instance: LinkpointApp? = null
        
        fun getInstance(): LinkpointApp {
            return instance ?: throw IllegalStateException("Application not initialized")
        }
    }
    
    // Application-wide coroutine scope for background operations
    val applicationScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    
    // Core managers
    lateinit var gridManager: GridManager
        private set
    lateinit var sessionManager: SessionManager
        private set
    lateinit var startLocationManager: StartLocationManager
        private set
    lateinit var destinationGuide: DestinationGuide
        private set
    lateinit var avatarSelectionManager: AvatarSelectionManager
        private set
    lateinit var renderManager: RenderManager
        private set
    lateinit var xrManager: XRManager
        private set
    lateinit var protocol: SecondLifeProtocol
        private set
    
    // Protocol layer
    lateinit var capabilityManager: CapabilityManager
        private set
    lateinit var udpConnection: UDPConnection
        private set
    
    // Asset system
    lateinit var assetCache: AssetCache
        private set
    lateinit var textureManager: TextureManager
        private set
    lateinit var meshManager: MeshManager
        private set
    lateinit var animationManager: AnimationManager
        private set
    lateinit var soundManager: SoundManager
        private set
    
    // Avatar system
    lateinit var avatarManager: AvatarManager
        private set
    
    // Chat & IM
    lateinit var chatManager: ChatManager
        private set
    lateinit var imManager: IMManager
        private set
    
    // Inventory
    lateinit var inventoryManager: InventoryManager
        private set
    lateinit var outfitManager: OutfitManager
        private set
    lateinit var gestureManager: GestureManager
        private set
    
    // World
    lateinit var worldMap: WorldMap
        private set
    lateinit var searchManager: SearchManager
        private set
    lateinit var profileManager: ProfileManager
        private set
    lateinit var parcelManager: ParcelManager
        private set
    lateinit var friendsManager: FriendsManager
        private set
    lateinit var groupsManager: GroupsManager
        private set
    
    // Animesh and BoM (modern features)
    lateinit var animeshManager: AnimeshManager
        private set
    lateinit var bomManager: BakesOnMeshManager
        private set
    
    // Teleport
    lateinit var teleportManager: TeleportManager
        private set
    
    // HUD
    lateinit var hudManager: HUDManager
        private set
    
    // Objects
    lateinit var objectManager: ObjectManager
        private set
    lateinit var buildTools: BuildTools
        private set
    
    // Voice
    lateinit var voiceManager: VoiceManager
        private set
    
    // Crash Reporter
    lateinit var crashReporter: CrashReporter
        private set
    
    // Agent ID (set after login)
    var agentId: UUID? = null
        private set
    
    override fun onCreate() {
        super.onCreate()
        instance = this
        Log.i(TAG, "Linkpoint application starting...")
        
        // Initialize crash reporter first for early crash capture
        try {
            crashReporter = CrashReporter.initialize(this)
            Log.i(TAG, "Crash reporter initialized")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize crash reporter", e)
        }
        
        // Initialize network settings (XML buffer size 3-500 MB)
        NetworkSettings.getInstance(this)
        Log.i(TAG, "Network settings initialized")
        
        // Initialize network logger first for early debugging
        NetworkLogger.initialize(this)
        Log.i(TAG, "Network logger initialized with auto-save to Downloads/Lumiya Logs/")
        
        initializeManagers()
        
        Log.i(TAG, "Linkpoint initialized successfully")
    }
    
    private fun initializeManagers() {
        Log.d(TAG, "Initializing managers...")
        
        // Grid management (login, multiple grids)
        gridManager = GridManager(this)
        
        // Session management (active connection state)
        sessionManager = SessionManager(this)
        
        // Start location management (landmarks, destinations)
        startLocationManager = StartLocationManager(this)
        
        // Destination guide (themed locations)
        destinationGuide = DestinationGuide(this)
        
        // Avatar selection management
        avatarSelectionManager = AvatarSelectionManager(this)
        
        // Protocol components
        capabilityManager = CapabilityManager()
        udpConnection = UDPConnection()
        
        // Protocol handler
        protocol = SecondLifeProtocol(this)
        
        // Rendering (Filament-based)
        renderManager = RenderManager(this)
        
        // XR/VR support
        xrManager = XRManager(this)
        
        // Asset system
        assetCache = AssetCache(this)
        textureManager = TextureManager(this, assetCache, capabilityManager)
        meshManager = MeshManager(assetCache, capabilityManager)
        animationManager = AnimationManager(this, assetCache)
        soundManager = SoundManager(this, assetCache)
        
        // World features
        worldMap = WorldMap(capabilityManager)
        searchManager = SearchManager(capabilityManager)
        profileManager = ProfileManager(capabilityManager)
        parcelManager = ParcelManager(udpConnection)
        
        // Voice
        voiceManager = VoiceManager(this, capabilityManager)
        
        Log.d(TAG, "Core managers initialized")
    }
    
    /**
     * Initialize managers that require agent ID (call after login)
     */
    fun initializeAgentManagers(agentId: UUID) {
        this.agentId = agentId
        
        // Initialize friendsManager here since it requires agentId
        friendsManager = FriendsManager(udpConnection, capabilityManager, agentId)
        
        // Initialize groupsManager
        groupsManager = GroupsManager(udpConnection, capabilityManager, agentId)
        
        Log.d(TAG, "Initializing agent-specific managers for $agentId")
        
        // Avatar manager
        avatarManager = AvatarManager(
            this, meshManager, textureManager, animationManager, capabilityManager, udpConnection
        )
        avatarManager.setMyAgentId(agentId)
        
        // Chat manager
        chatManager = ChatManager(udpConnection, agentId)
        
        // IM manager
        imManager = IMManager(udpConnection, capabilityManager, agentId)
        
        // Inventory
        inventoryManager = InventoryManager(capabilityManager, agentId)
        
        // Outfit manager (needs baker from avatar manager)
        val myAvatar = avatarManager.getMyAvatar()
        if (myAvatar != null) {
            outfitManager = OutfitManager(inventoryManager, myAvatar.baker)
        }
        
        // Gesture manager
        gestureManager = GestureManager(assetCache, animationManager, soundManager) { message ->
            chatManager.sendChat(message)
        }
        
        // Object manager
        objectManager = ObjectManager(udpConnection)
        buildTools = BuildTools(objectManager)
        
        // Modern features: Animesh and Bakes on Mesh
        animeshManager = AnimeshManager(meshManager, animationManager)
        bomManager = BakesOnMeshManager(capabilityManager, textureManager)
        
        // Teleport manager
        teleportManager = TeleportManager(udpConnection, capabilityManager, agentId)
        
        // HUD manager
        hudManager = HUDManager(objectManager, udpConnection, agentId)
        
        // Connect WorldMap to AvatarManager and FriendsManager for nearby users
        worldMap.setAvatarManagerProvider { avatarManager }
        worldMap.setFriendsManagerProvider { friendsManager }
        
        // Register UDP message handlers for real-time data
        registerMessageHandlers()
        
        Log.d(TAG, "Agent managers initialized")
    }
    
    /**
     * Register message handlers for UDP packet processing.
     * This connects the parsed messages to their respective managers.
     */
    private fun registerMessageHandlers() {
        Log.i(TAG, "╔══════════════════════════════════════════════════════════════════")
        Log.i(TAG, "║ REGISTERING UDP MESSAGE HANDLERS")
        Log.i(TAG, "╚══════════════════════════════════════════════════════════════════")
        
        // RegionHandshake - CRITICAL: Must respond with RegionHandshakeReply for world data to load
        // This is why nothing was loading after login - we weren't acknowledging the region handshake
        // Register handlers for all critical packets
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.REGION_HANDSHAKE) { _, payload ->
            com.linkpoint.utils.InitializationTracker.startPhase(
                com.linkpoint.utils.InitializationTracker.Phase.REGION_HANDSHAKE_RECEIVED,
                "Processing RegionHandshake"
            )
            Log.i(TAG, "╔══════════════════════════════════════════════════════════════════")
            Log.i(TAG, "║ ⭐ REGION_HANDSHAKE RECEIVED (CRITICAL MESSAGE)")
            Log.i(TAG, "╚══════════════════════════════════════════════════════════════════")
            Log.d(TAG, "RegionHandshake payload size: ${payload.size} bytes")
            try {
                val regionData = com.linkpoint.protocol.messages.MessageParser.parseRegionHandshake(payload)
                if (regionData != null) {
                    Log.i(TAG, "RegionHandshake parsed: simName='${regionData.simName}'")
                    com.linkpoint.utils.InitializationTracker.logInfo("Region: ${regionData.simName}")
                    
                    // Update session with region info
                    sessionManager.updateRegionName(regionData.simName)
                    Log.d(TAG, "Session region name updated to: ${regionData.simName}")
                    
                    // Send RegionHandshakeReply to acknowledge - THIS IS REQUIRED!
                    applicationScope.launch {
                        try {
                            Log.d(TAG, "Sending RegionHandshakeReply...")
                            udpConnection.sendRegionHandshakeReply()
                            com.linkpoint.utils.InitializationTracker.completePhase(
                                com.linkpoint.utils.InitializationTracker.Phase.REGION_HANDSHAKE_RECEIVED,
                                "Reply sent to ${regionData.simName}"
                            )
                            com.linkpoint.utils.InitializationTracker.startPhase(
                                com.linkpoint.utils.InitializationTracker.Phase.REGION_HANDSHAKE_REPLIED,
                                "Waiting for world data"
                            )
                            Log.i(TAG, "✓ RegionHandshakeReply SENT - world data should start loading")
                            
                            // Also send AgentThrottle to set bandwidth allocation
                            Log.d(TAG, "Sending AgentThrottle...")
                            udpConnection.sendAgentThrottle()
                            Log.i(TAG, "✓ AgentThrottle SENT - bandwidth configured")
                        } catch (e: Exception) {
                            com.linkpoint.utils.InitializationTracker.failPhase(
                                com.linkpoint.utils.InitializationTracker.Phase.REGION_HANDSHAKE_RECEIVED,
                                "Failed to send reply: ${e.message}"
                            )
                            Log.e(TAG, "✗ Error sending RegionHandshakeReply/AgentThrottle", e)
                        }
                    }
                } else {
                    com.linkpoint.utils.InitializationTracker.logWarning("RegionHandshake parse returned null")
                    Log.w(TAG, "RegionHandshake parse returned null - payload may be malformed")
                }
            } catch (e: Exception) {
                com.linkpoint.utils.InitializationTracker.failPhase(
                    com.linkpoint.utils.InitializationTracker.Phase.REGION_HANDSHAKE_RECEIVED,
                    "Parse error: ${e.message}"
                )
                Log.e(TAG, "Error handling RegionHandshake", e)
            }
        }
        
        // AgentMovementComplete - Confirms agent is fully in region
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.AGENT_MOVEMENT_COMPLETE) { _, payload ->
            com.linkpoint.utils.InitializationTracker.startPhase(
                com.linkpoint.utils.InitializationTracker.Phase.AGENT_MOVEMENT_COMPLETE,
                "Agent fully in region"
            )
            Log.i(TAG, "╔══════════════════════════════════════════════════════════════════")
            Log.i(TAG, "║ ⭐ AGENT_MOVEMENT_COMPLETE RECEIVED")
            Log.i(TAG, "╚══════════════════════════════════════════════════════════════════")
            try {
                val moveData = com.linkpoint.protocol.messages.MessageParser.parseAgentMovementComplete(payload)
                if (moveData != null) {
                    Log.i(TAG, "AgentMovementComplete: position=${moveData.position}")
                    com.linkpoint.utils.InitializationTracker.logInfo("Position: ${moveData.position}")
                    
                    // Update connection state to fully connected
                    sessionManager.setConnectionState(com.linkpoint.core.ConnectionState.CONNECTED)
                    udpConnection.startAgentUpdates()
                    Log.i(TAG, "✓ AgentUpdate loop started")
                    
                    com.linkpoint.utils.InitializationTracker.completePhase(
                        com.linkpoint.utils.InitializationTracker.Phase.AGENT_MOVEMENT_COMPLETE,
                        "Agent at ${moveData.position}"
                    )
                    com.linkpoint.utils.InitializationTracker.startPhase(
                        com.linkpoint.utils.InitializationTracker.Phase.FULLY_CONNECTED,
                        "Agent is now in world"
                    )
                    com.linkpoint.utils.InitializationTracker.logCritical(
                        "FULLY CONNECTED - World loading should begin"
                    )
                    
                    Log.i(TAG, "✓ Connection state set to CONNECTED - agent is in world")
                } else {
                    com.linkpoint.utils.InitializationTracker.logWarning("AgentMovementComplete parse returned null")
                    Log.w(TAG, "AgentMovementComplete parse returned null")
                }
            } catch (e: Exception) {
                com.linkpoint.utils.InitializationTracker.failPhase(
                    com.linkpoint.utils.InitializationTracker.Phase.AGENT_MOVEMENT_COMPLETE,
                    "Parse error: ${e.message}"
                )
                Log.e(TAG, "Error handling AgentMovementComplete", e)
            }
        }
        
        // Chat from simulator (nearby chat)
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.CHAT_FROM_SIMULATOR) { _, payload ->
            try {
                val chatData = com.linkpoint.protocol.messages.MessageParser.parseChatFromSimulator(payload)
                if (chatData != null && ::chatManager.isInitialized) {
                    chatManager.handleChatFromSimulator(chatData)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error handling ChatFromSimulator", e)
            }
        }
        
        // Object updates - track counts for diagnostics
        var objectUpdateCount = 0
        var compressedObjectUpdateCount = 0
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.OBJECT_UPDATE) { _, payload ->
            try {
                val updates = com.linkpoint.protocol.messages.MessageParser.parseObjectUpdate(payload)
                objectUpdateCount += updates.size
                // Log occasionally to avoid spam
                if (objectUpdateCount <= 5 || objectUpdateCount % 100 == 0) {
                    Log.d(TAG, "OBJECT_UPDATE received: ${updates.size} objects (total: $objectUpdateCount)")
                }
                updates.forEach { update ->
                    if (::objectManager.isInitialized) {
                        objectManager.handleObjectUpdate(update)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error handling ObjectUpdate", e)
            }
        }
        
        // Compressed object updates
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.OBJECT_UPDATE_COMPRESSED) { _, payload ->
            try {
                val updates = com.linkpoint.protocol.messages.MessageParser.parseObjectUpdateCompressed(payload)
                compressedObjectUpdateCount += updates.size
                // Log occasionally to avoid spam
                if (compressedObjectUpdateCount <= 5 || compressedObjectUpdateCount % 100 == 0) {
                    Log.d(TAG, "OBJECT_UPDATE_COMPRESSED received: ${updates.size} objects (total: $compressedObjectUpdateCount)")
                }
                updates.forEach { update ->
                    if (::objectManager.isInitialized) {
                        objectManager.handleObjectUpdate(update)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error handling ObjectUpdateCompressed", e)
            }
        }
        
        // Avatar animation updates
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.AVATAR_ANIMATION) { _, payload ->
            try {
                val animData = com.linkpoint.protocol.messages.MessageParser.parseAvatarAnimation(payload)
                if (animData != null && ::avatarManager.isInitialized) {
                    avatarManager.handleAvatarAnimation(animData)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error handling AvatarAnimation", e)
            }
        }
        
        // StartPingCheck - CRITICAL: Must respond with CompletePingCheck to maintain connection
        // The simulator sends this periodically to verify the client is still alive
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.START_PING_CHECK) { _, payload ->
            try {
                if (payload.isNotEmpty()) {
                    val pingId = payload[0]
                    // OldestUnacked is at offset 1, 4 bytes, but we don't use it - we compute our own
                    Log.d(TAG, "StartPingCheck received: pingId=$pingId")
                    applicationScope.launch {
                        udpConnection.handleStartPingCheck(pingId, 0)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error handling StartPingCheck", e)
            }
        }
        
        // ImprovedTerseObjectUpdate - Fast position updates for objects/avatars
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.IMPROVED_TERSE_OBJECT_UPDATE) { _, payload ->
            try {
                val updates = com.linkpoint.protocol.messages.MessageParser.parseTerseObjectUpdate(payload)
                updates.forEach { update ->
                    if (update.isAvatar) {
                        // Avatar position update
                        if (::avatarManager.isInitialized) {
                            avatarManager.handleTerseUpdate(update)
                        }
                    } else {
                        // Object position update
                        if (::objectManager.isInitialized) {
                            objectManager.handleTerseUpdate(update)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error handling ImprovedTerseObjectUpdate", e)
            }
        }
        
        // KillObject - Notification when objects are removed from the scene
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.KILL_OBJECT) { _, payload ->
            try {
                // KillObject format: 1-byte count, then list of 4-byte local IDs
                val buffer = java.nio.ByteBuffer.wrap(payload).order(java.nio.ByteOrder.LITTLE_ENDIAN)
                val count = buffer.get().toInt() and 0xFF
                for (i in 0 until count) {
                    if (buffer.remaining() >= 4) {
                        val localId = buffer.int
                        if (::objectManager.isInitialized) {
                            objectManager.removeObject(localId)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error handling KillObject", e)
            }
        }
        
        // CoarseLocationUpdate - Location updates for nearby avatars
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.COARSE_LOCATION_UPDATE) { _, payload ->
            try {
                // CoarseLocationUpdate provides rough avatar positions in the region
                // Format: RegionData block, then AgentID blocks with X, Y, Z (bytes)
                // We'll use this to track nearby avatars even before full ObjectUpdate
                if (::avatarManager.isInitialized) {
                    avatarManager.handleCoarseLocationUpdate(payload)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error handling CoarseLocationUpdate", e)
            }
        }
        
        Log.i(TAG, "╔══════════════════════════════════════════════════════════════════")
        Log.i(TAG, "║ UDP MESSAGE HANDLERS REGISTERED: ${udpConnection.getRegisteredHandlerCount()}")
        Log.i(TAG, "║ Handlers: ${udpConnection.getRegisteredHandlerIds().joinToString(", ")}")
        Log.i(TAG, "╚══════════════════════════════════════════════════════════════════")
    }
    
    override fun onTerminate() {
        super.onTerminate()
        Log.i(TAG, "Linkpoint application terminating")
        
        // Cleanup
        voiceManager.shutdown()
        if (::avatarManager.isInitialized) avatarManager.shutdown()
        if (::objectManager.isInitialized) objectManager.shutdown()
        if (::chatManager.isInitialized) chatManager.shutdown()
        if (::imManager.isInitialized) imManager.shutdown()
        if (::inventoryManager.isInitialized) inventoryManager.shutdown()
        if (::gestureManager.isInitialized) gestureManager.shutdown()
        
        worldMap.shutdown()
        searchManager.shutdown()
        profileManager.shutdown()
        parcelManager.shutdown()
        
        soundManager.shutdown()
        animationManager.shutdown()
        meshManager.shutdown()
        textureManager.shutdown()
        
        capabilityManager.shutdown()
        udpConnection.disconnect()
        
        xrManager.shutdown()
        renderManager.shutdown()
        
        // Shutdown protocol and networking
        protocol.shutdown()
        
        // Shutdown destination guide
        destinationGuide.shutdown()
        
        sessionManager.disconnect()
        
        // Shutdown crash reporter
        if (::crashReporter.isInitialized) crashReporter.shutdown()
        
        // Cancel application scope
        applicationScope.cancel()
    }
    
    /**
     * Report a non-fatal exception
     */
    fun reportException(throwable: Throwable, context: String = "") {
        if (::crashReporter.isInitialized) {
            crashReporter.reportException(throwable, context)
        } else {
            Log.e(TAG, "Exception occurred (crash reporter not initialized): $context", throwable)
        }
    }
    
    /**
     * Check if XR mode is available on this device
     */
    fun isXRAvailable(): Boolean = xrManager.isAvailable()
    
    /**
     * Check if currently connected to a grid
     */
    fun isConnected(): Boolean = sessionManager.isConnected()
    
    /**
     * Get the current region name
     */
    fun getCurrentRegion(): String? = sessionManager.currentRegion.value?.name
    
    // ==================== DIAGNOSTIC HELPER METHODS ====================
    
    /**
     * Check if object manager is initialized (for debug reports)
     */
    fun isObjectManagerInitialized(): Boolean = ::objectManager.isInitialized
    
    /**
     * Check if avatar manager is initialized (for debug reports)
     */
    fun isAvatarManagerInitialized(): Boolean = ::avatarManager.isInitialized
    
    /**
     * Check if inventory manager is initialized (for debug reports)
     */
    fun isInventoryManagerInitialized(): Boolean = ::inventoryManager.isInitialized
    
    /**
     * Check if chat manager is initialized (for debug reports)
     */
    fun isChatManagerInitialized(): Boolean = ::chatManager.isInitialized
    
    /**
     * Check if IM manager is initialized (for debug reports)
     */
    fun isIMManagerInitialized(): Boolean = ::imManager.isInitialized
    
    /**
     * Check if texture manager is initialized (for debug reports)
     * Note: TextureManager is initialized early, so this is always true after app init
     */
    fun isTextureManagerInitialized(): Boolean = ::textureManager.isInitialized
    
    /**
     * Check if mesh manager is initialized (for debug reports)
     * Note: MeshManager is initialized early, so this is always true after app init
     */
    fun isMeshManagerInitialized(): Boolean = ::meshManager.isInitialized
    
    /**
     * Check if render manager is initialized (for debug reports)
     * Note: RenderManager is initialized early, so this is always true after app init
     */
    fun isRenderManagerInitialized(): Boolean = ::renderManager.isInitialized
    
    /**
     * Check if animation manager is initialized (for debug reports)
     */
    fun isAnimationManagerInitialized(): Boolean = ::animationManager.isInitialized
    
    /**
     * Check if sound manager is initialized (for debug reports)
     */
    fun isSoundManagerInitialized(): Boolean = ::soundManager.isInitialized
    
    /**
     * Check if gesture manager is initialized (for debug reports)
     */
    fun isGestureManagerInitialized(): Boolean = ::gestureManager.isInitialized
    
    /**
     * Check if outfit manager is initialized (for debug reports)
     */
    fun isOutfitManagerInitialized(): Boolean = ::outfitManager.isInitialized
    
    /**
     * Check if groups manager is initialized (for debug reports)
     */
    fun isGroupsManagerInitialized(): Boolean = ::groupsManager.isInitialized
    
    /**
     * Check if animesh manager is initialized (for debug reports)
     */
    fun isAnimeshManagerInitialized(): Boolean = ::animeshManager.isInitialized
    
    /**
     * Check if BoM manager is initialized (for debug reports)
     */
    fun isBomManagerInitialized(): Boolean = ::bomManager.isInitialized
    
    /**
     * Check if teleport manager is initialized (for debug reports)
     */
    fun isTeleportManagerInitialized(): Boolean = ::teleportManager.isInitialized
    
    /**
     * Check if HUD manager is initialized (for debug reports)
     */
    fun isHudManagerInitialized(): Boolean = ::hudManager.isInitialized
}
