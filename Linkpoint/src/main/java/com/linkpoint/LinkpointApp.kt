package com.linkpoint

import android.app.Application
import android.content.Context
import android.util.Log
import com.linkpoint.assets.*
import com.linkpoint.utils.CrashReporter
import com.linkpoint.avatar.AvatarManager
import com.linkpoint.avatar.baking.AvatarBakingSystem
import com.linkpoint.chat.ChatManager
import com.linkpoint.chat.IMManager
import com.linkpoint.chat.dialogs.ScriptDialogManager
import com.linkpoint.core.GridManager
import com.linkpoint.core.SessionManager
import com.linkpoint.core.StartLocationManager
import com.linkpoint.core.DestinationGuide
import com.linkpoint.core.AvatarSelectionManager
import com.linkpoint.economy.EconomyManager
import com.linkpoint.inventory.GestureManager
import com.linkpoint.inventory.InventoryManager
import com.linkpoint.inventory.OutfitManager
import com.linkpoint.inventory.notecard.NotecardManager
import com.linkpoint.network.NetworkSettings
import com.linkpoint.network.SecondLifeProtocol
import com.linkpoint.network.NetworkLogger
import com.linkpoint.objects.BuildTools
import com.linkpoint.objects.ObjectManager
import com.linkpoint.objects.inventory.TaskInventoryManager
import com.linkpoint.objects.prim.FlexiblePrimSimulator
import com.linkpoint.protocol.capabilities.CapabilityManager
import com.linkpoint.protocol.messages.UDPConnectionFixed
import com.linkpoint.protocol.messages.parseRegionHandshake
import com.linkpoint.protocol.messages.parseAgentMovementComplete
import com.linkpoint.protocol.messages.parseObjectUpdateCached
import com.linkpoint.protocol.messages.parseObjectProperties
import com.linkpoint.protocol.messages.parseScriptControlChange
import com.linkpoint.protocol.messages.parseTeleportFinish
import com.linkpoint.protocol.messages.parseTeleportFailed
import com.linkpoint.protocol.messages.parseTeleportProgress
import com.linkpoint.protocol.messages.parseAlertMessage
import com.linkpoint.protocol.messages.parseAgentAlertMessage
import com.linkpoint.protocol.messages.parseEnableSimulator
import com.linkpoint.protocol.messages.parseCrossedRegion
import com.linkpoint.protocol.messages.parseImprovedInstantMessage
import com.linkpoint.protocol.transfer.TransferManager
import com.linkpoint.protocol.transfer.XferManager
import com.linkpoint.render.DrawDistanceManager
import com.linkpoint.render.HoverTextManager
import com.linkpoint.render.RenderManager
import com.linkpoint.render.particles.ParticleSystem
import com.linkpoint.rlv.RLVController
import com.linkpoint.service.ConnectionKeepAliveManager
import com.linkpoint.service.IdleHandler
import com.linkpoint.service.LinkpointConnectionService
import com.linkpoint.users.DisplayNameManager
import com.linkpoint.users.MuteManager
import com.linkpoint.users.UserProfileManager
import com.linkpoint.voice.VoiceManager
import com.linkpoint.world.FriendsManager
import com.linkpoint.world.ParcelManager
import com.linkpoint.world.ProfileManager
import com.linkpoint.world.SearchManager
import com.linkpoint.world.WorldMap
import com.linkpoint.world.environment.EnvironmentManager
import com.linkpoint.world.minimap.MinimapManager
import com.linkpoint.groups.GroupsManager
import com.linkpoint.animesh.AnimeshManager
import com.linkpoint.avatar.AnimationController
import com.linkpoint.bom.BakesOnMeshManager
import com.linkpoint.inventory.LandmarkManager
import com.linkpoint.media.MediaManager
import com.linkpoint.objects.SitManager
import com.linkpoint.snapshot.SnapshotManager
import com.linkpoint.teleport.TeleportManager
import com.linkpoint.hud.HUDManager
import com.linkpoint.world.estate.EstateManager
import com.linkpoint.xr.XRManager
import com.linkpoint.protocol.textures.TextureEntryParser
import com.linkpoint.protocol.types.getUUID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.util.UUID
import java.util.concurrent.atomic.AtomicBoolean

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
    lateinit var udpConnection: UDPConnectionFixed
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
    lateinit var cacheManager: CacheManager
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
    
    // Transfer system (NEW)
    lateinit var transferManager: TransferManager
        private set
    lateinit var xferManager: XferManager
        private set
    
    // Notecard system (NEW)
    lateinit var notecardManager: NotecardManager
        private set
    
    // Task inventory (NEW)
    lateinit var taskInventoryManager: TaskInventoryManager
        private set
    
    // Environment/Windlight (NEW)
    lateinit var environmentManager: EnvironmentManager
        private set
    
    // Display names (NEW)
    lateinit var displayNameManager: DisplayNameManager
        private set
    
    // Mute list (NEW)
    lateinit var muteManager: MuteManager
        private set
    
    // Script dialogs (NEW)
    lateinit var scriptDialogManager: ScriptDialogManager
        private set
    
    // Hover text (NEW)
    lateinit var hoverTextManager: HoverTextManager
        private set
    
    // Economy/L$ (NEW)
    lateinit var economyManager: EconomyManager
        private set
    
    // RLV Controller (NEW)
    lateinit var rlvController: RLVController
        private set
    
    // User Profile Manager (NEW)
    lateinit var userProfileManager: UserProfileManager
        private set
    
    // Draw Distance Manager (NEW)
    lateinit var drawDistanceManager: DrawDistanceManager
        private set
    
    // Minimap (NEW)
    lateinit var minimapManager: MinimapManager
        private set
    
    // Avatar Baking System (NEW)
    lateinit var avatarBakingSystem: AvatarBakingSystem
        private set
    
    // Flexible Prim Simulator (NEW)
    lateinit var flexiblePrimSimulator: FlexiblePrimSimulator
        private set
    
    // Connection Keep-Alive (NEW)
    lateinit var connectionKeepAlive: ConnectionKeepAliveManager
        private set
    
    // Idle Handler (NEW)
    lateinit var idleHandler: IdleHandler
        private set
    
    // Landmark Manager (NEW)
    lateinit var landmarkManager: LandmarkManager
        private set
    
    // Media Manager (NEW)
    lateinit var mediaManager: MediaManager
        private set
    
    // Estate Manager (NEW)
    lateinit var estateManager: EstateManager
        private set
    
    // Snapshot Manager (NEW)
    lateinit var snapshotManager: SnapshotManager
        private set
    
    // Script Manager (NEW)
    lateinit var scriptManager: ScriptManager
        private set
    
    // Sit Manager (NEW)
    lateinit var sitManager: SitManager
        private set
    
    // Animation Controller (NEW)
    lateinit var animationController: AnimationController
        private set
    
    // Terrain Manager (NEW)
    lateinit var terrainManager: com.linkpoint.protocol.terrain.TerrainManager
        private set
    
    // Crash Reporter
    lateinit var crashReporter: CrashReporter
        private set
    
    // Agent ID (set after login)
    var agentId: UUID? = null
        private set
    
    // Flag to track if CompleteAgentMovement has been sent
    // Per Lumiya protocol: This must be sent immediately when UseCircuitCode (seq 0) is ACKed
    // The server won't send RegionHandshake until we send this
    // Using AtomicBoolean to prevent race conditions with concurrent PacketAck messages
    private val completeAgentMovementSent = AtomicBoolean(false)
    
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
        
        // Initialize session log recorder for comprehensive packet logging
        com.linkpoint.utils.SessionLogRecorder.initialize(this)
        Log.i(TAG, "Session log recorder initialized")
        
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
        udpConnection = UDPConnectionFixed()
        
        // Protocol handler
        protocol = SecondLifeProtocol(this)
        
        // Rendering (Filament-based)
        renderManager = RenderManager(this)
        
        // XR/VR support
        xrManager = XRManager(this)
        
        // Cache management system (Lumiya Cache structure)
        cacheManager = CacheManager(this)
        
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
        
        // NEW: Environment/Windlight
        environmentManager = EnvironmentManager(capabilityManager)
        
        // NEW: Display names
        displayNameManager = DisplayNameManager(capabilityManager)
        
        // NEW: Hover text
        hoverTextManager = HoverTextManager()
        
        // NEW: Xfer manager (needs UDP connection)
        xferManager = XferManager(udpConnection)
        
        // NEW: RLV Controller
        rlvController = RLVController(
            chatManager = { if (::chatManager.isInitialized) chatManager else null },
            sitManager = { if (::sitManager.isInitialized) sitManager else null }
        )
        
        // Load RLV setting from SharedPreferences
        val prefs = getSharedPreferences("com.linkpoint_preferences", Context.MODE_PRIVATE)
        val rlvEnabled = prefs.getBoolean("rlv_enabled", false)
        rlvController.setEnabled(rlvEnabled)
        Log.i(TAG, "RLV initialized: enabled=$rlvEnabled")
        
        // NEW: Draw Distance Manager
        drawDistanceManager = DrawDistanceManager()
        
        // NEW: Minimap
        minimapManager = MinimapManager(udpConnection)
        
        // NEW: Avatar Baking System
        avatarBakingSystem = AvatarBakingSystem()
        
        // NEW: Flexible Prim Simulator
        flexiblePrimSimulator = FlexiblePrimSimulator()
        
        // NEW: Connection Keep-Alive (critical for background operation)
        connectionKeepAlive = ConnectionKeepAliveManager(this, udpConnection)
        
        // NEW: Idle Handler
        idleHandler = IdleHandler(connectionKeepAlive)
        
        // NEW: Media Manager
        mediaManager = MediaManager(this, udpConnection)
        
        // NEW: Snapshot Manager
        snapshotManager = SnapshotManager(this, capabilityManager)
        
        // NEW: Terrain Manager (for processing LayerData terrain patches)
        terrainManager = com.linkpoint.protocol.terrain.TerrainManager()
        
        Log.d(TAG, "Core managers initialized")
    }
    
    /**
     * Initialize managers that require agent ID (call after login)
     */
    fun initializeAgentManagers(agentId: UUID) {
        this.agentId = agentId
        
        // Reset connection state tracking for new session
        completeAgentMovementSent.set(false)
        
        // Initialize friendsManager here since it requires agentId
        friendsManager = FriendsManager(udpConnection, capabilityManager, agentId)
        
        // Initialize groupsManager
        groupsManager = GroupsManager(udpConnection, capabilityManager, agentId)
        
        Log.d(TAG, "Initializing agent-specific managers for $agentId")
        
        // NEW: Transfer manager (needs agent ID and session)
        transferManager = TransferManager(udpConnection, agentId, udpConnection.getSessionId())
        
        // NEW: Notecard manager
        notecardManager = NotecardManager(transferManager)
        
        // NEW: Task inventory manager
        taskInventoryManager = TaskInventoryManager(udpConnection, xferManager, agentId)
        
        // NEW: Mute manager
        muteManager = MuteManager(udpConnection, xferManager, agentId)
        
        // NEW: User Profile Manager
        userProfileManager = UserProfileManager(capabilityManager, udpConnection, agentId)
        
        // NEW: Initialize connection keep-alive with credentials
        connectionKeepAlive.initialize(agentId, udpConnection.getSessionId())
        
        // Start background service for connection persistence
        LinkpointConnectionService.start(this)
        
        // NEW: Script dialog manager
        scriptDialogManager = ScriptDialogManager(udpConnection, agentId)
        
        // NEW: Economy manager
        economyManager = EconomyManager(udpConnection, capabilityManager, agentId)
        
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
        inventoryManager = InventoryManager(capabilityManager, udpConnection, agentId)
        
        // Gesture manager
        gestureManager = GestureManager(
            assetCache,
            animationManager,
            soundManager,
            udpConnection,
            agentId,
            udpConnection.getSessionId()
        ) { message ->
            chatManager.sendChat(message)
        }

        // Object manager
        objectManager = ObjectManager(udpConnection)
        buildTools = BuildTools(objectManager)

        // Outfit manager (needs baker from avatar manager)
        val myAvatar = avatarManager.getMyAvatar()
        if (myAvatar != null) {
            outfitManager = OutfitManager(
                inventoryManager,
                myAvatar.baker,
                gestureManager,
                udpConnection,
                agentId,
                udpConnection.getSessionId(),
                objectManager
            )
        }
        
        // Modern features: Animesh and Bakes on Mesh
        animeshManager = AnimeshManager(meshManager, animationManager)
        bomManager = BakesOnMeshManager(capabilityManager, textureManager)
        
        // Teleport manager
        teleportManager = TeleportManager(udpConnection, capabilityManager, agentId)
        
        // HUD manager
        hudManager = HUDManager(objectManager, udpConnection, agentId)
        
        // NEW: Landmark Manager
        landmarkManager = LandmarkManager(capabilityManager, transferManager, inventoryManager, udpConnection, agentId)
        
        // NEW: Estate Manager
        estateManager = EstateManager(udpConnection, capabilityManager, agentId)
        
        // NEW: Script Manager
        scriptManager = ScriptManager(capabilityManager, transferManager)
        
        // NEW: Sit Manager
        sitManager = SitManager(udpConnection, agentId)
        
        // NEW: Animation Controller
        animationController = AnimationController(udpConnection, agentId)
        
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
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.REGION_HANDSHAKE) { _, rawPacket ->
            com.linkpoint.utils.InitializationTracker.startPhase(
                com.linkpoint.utils.InitializationTracker.Phase.REGION_HANDSHAKE_RECEIVED,
                "Processing RegionHandshake"
            )
            Log.i(TAG, "╔══════════════════════════════════════════════════════════════════")
            Log.i(TAG, "║ ⭐ REGION_HANDSHAKE RECEIVED (CRITICAL MESSAGE)")
            Log.i(TAG, "╚══════════════════════════════════════════════════════════════════")
            Log.d(TAG, "RegionHandshake raw packet size: ${rawPacket.size} bytes")
            try {
                // Extract payload from raw packet (skip header and message ID)
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload == null) {
                    Log.e(TAG, "Failed to extract RegionHandshake payload from raw packet")
                    com.linkpoint.utils.InitializationTracker.failPhase(
                        com.linkpoint.utils.InitializationTracker.Phase.REGION_HANDSHAKE_RECEIVED,
                        "Payload extraction failed"
                    )
                    return@registerHandler
                }
                Log.d(TAG, "RegionHandshake extracted payload size: ${payload.size} bytes")
                
                val regionData = com.linkpoint.protocol.messages.MessageParser.parseRegionHandshake(payload)
                if (regionData != null) {
                    Log.i(TAG, "RegionHandshake parsed: simName='${regionData.simName}'")
                    com.linkpoint.utils.InitializationTracker.logInfo("Region: ${regionData.simName}")
                    
                    // Update session with region info
                    sessionManager.updateRegionName(regionData.simName)
                    Log.d(TAG, "Session region name updated to: ${regionData.simName}")
                    
                    // Log to session recorder if active
                    com.linkpoint.utils.SessionLogRecorder.logRegionChange(
                        regionData.simName, 0L, null
                    )
                    
                    // Update terrain manager with water height
                    if (::terrainManager.isInitialized) {
                        terrainManager.setWaterHeight(regionData.waterHeight)
                        terrainManager.reset()  // Reset terrain for new region
                        Log.d(TAG, "Terrain manager reset for new region, water height: ${regionData.waterHeight}")
                    }
                    
                    // Send RegionHandshakeReply to acknowledge - THIS IS REQUIRED!
                    // NOTE: CompleteAgentMovement and AgentThrottle are sent earlier in PacketAck
                    // handler when UseCircuitCode is acknowledged (per Lumiya protocol)
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
                        } catch (e: Exception) {
                            com.linkpoint.utils.InitializationTracker.failPhase(
                                com.linkpoint.utils.InitializationTracker.Phase.REGION_HANDSHAKE_RECEIVED,
                                "Failed to send reply: ${e.message}"
                            )
                            Log.e(TAG, "✗ Error sending RegionHandshakeReply", e)
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
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.AGENT_MOVEMENT_COMPLETE) { _, rawPacket ->
            com.linkpoint.utils.InitializationTracker.startPhase(
                com.linkpoint.utils.InitializationTracker.Phase.AGENT_MOVEMENT_COMPLETE,
                "Agent fully in region"
            )
            Log.i(TAG, "╔══════════════════════════════════════════════════════════════════")
            Log.i(TAG, "║ ⭐ AGENT_MOVEMENT_COMPLETE RECEIVED")
            Log.i(TAG, "╚══════════════════════════════════════════════════════════════════")
            try {
                // Extract payload from raw packet (skip header and message ID)
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload == null) {
                    Log.e(TAG, "Failed to extract AgentMovementComplete payload from raw packet")
                    com.linkpoint.utils.InitializationTracker.failPhase(
                        com.linkpoint.utils.InitializationTracker.Phase.AGENT_MOVEMENT_COMPLETE,
                        "Payload extraction failed"
                    )
                    return@registerHandler
                }
                
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
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.CHAT_FROM_SIMULATOR) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload == null) {
                    Log.w(TAG, "Failed to extract ChatFromSimulator payload")
                    return@registerHandler
                }
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
        var avatarUpdateCount = 0
        
        // PCode constants (from Lumiya)
        val PCODE_PRIM = 9
        val PCODE_AVATAR = 47
        
        // Helper function to process object updates by PCode
        fun processObjectUpdate(update: com.linkpoint.protocol.messages.ObjectUpdateData) {
            when (update.pcode) {
                PCODE_AVATAR -> {
                    avatarUpdateCount++
                    if (avatarUpdateCount <= 5 || avatarUpdateCount % 50 == 0) {
                        Log.d(TAG, "Avatar update: localId=${update.localId}, fullId=${update.fullId} (total: $avatarUpdateCount)")
                    }
                    if (::avatarManager.isInitialized) {
                        avatarManager.updateAvatar(
                            agentId = update.fullId,
                            position = update.position,
                            rotation = update.rotation,
                            velocity = update.velocity
                        )
                    }
                    // Add avatar to scene for rendering
                    if (::renderManager.isInitialized) {
                        renderManager.getSceneManager()?.updateAvatar(
                            agentId = update.fullId,
                            position = update.position,
                            rotation = update.rotation
                        )
                    }
                }
                else -> {
                    // Prims, trees, grass, particles all go to object manager
                    if (::objectManager.isInitialized) {
                        objectManager.handleObjectUpdate(update)
                    }
                    // Add object to scene for rendering
                    if (::renderManager.isInitialized) {
                        renderManager.getSceneManager()?.updateObject(
                            objectId = update.fullId,
                            localId = update.localId,
                            position = update.position,
                            rotation = update.rotation,
                            scale = update.scale
                        )
                    }
                    
                    // Extract and prefetch textures from the object's TextureEntry
                    if (::textureManager.isInitialized && update.textureEntry.isNotEmpty()) {
                        val textureIds = TextureEntryParser.extractTextureIds(update.textureEntry)
                        val downloadableIds = textureIds.filter { TextureEntryParser.shouldDownload(it) }
                        if (downloadableIds.isNotEmpty()) {
                            textureManager.prefetch(downloadableIds.toList())
                        }
                    }
                }
            }
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.OBJECT_UPDATE) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload == null) return@registerHandler
                val updates = com.linkpoint.protocol.messages.MessageParser.parseObjectUpdate(payload)
                objectUpdateCount += updates.size
                // Log occasionally to avoid spam
                if (objectUpdateCount <= 5 || objectUpdateCount % 100 == 0) {
                    Log.d(TAG, "OBJECT_UPDATE received: ${updates.size} objects (total: $objectUpdateCount)")
                }
                updates.forEach { processObjectUpdate(it) }
            } catch (e: Exception) {
                Log.e(TAG, "Error handling ObjectUpdate", e)
            }
        }
        
        // Compressed object updates
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.OBJECT_UPDATE_COMPRESSED) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload == null) return@registerHandler
                val updates = com.linkpoint.protocol.messages.MessageParser.parseObjectUpdateCompressed(payload)
                compressedObjectUpdateCount += updates.size
                // Log occasionally to avoid spam
                if (compressedObjectUpdateCount <= 5 || compressedObjectUpdateCount % 100 == 0) {
                    Log.d(TAG, "OBJECT_UPDATE_COMPRESSED received: ${updates.size} objects (total: $compressedObjectUpdateCount)")
                }
                updates.forEach { processObjectUpdate(it) }
            } catch (e: Exception) {
                Log.e(TAG, "Error handling ObjectUpdateCompressed", e)
            }
        }
        
        // ObjectUpdateCached (ID 14) - Server notifies about cached objects
        // We respond with RequestMultipleObjects to get full data
        var cachedObjectUpdateCount = 0
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.OBJECT_UPDATE_CACHED) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload == null) return@registerHandler
                val cachedData = com.linkpoint.protocol.messages.MessageParser.parseObjectUpdateCached(payload)
                if (cachedData != null) {
                    cachedObjectUpdateCount += cachedData.objects.size
                    // Log occasionally to avoid spam
                    if (cachedObjectUpdateCount <= 5 || cachedObjectUpdateCount % 50 == 0) {
                        Log.d(TAG, "OBJECT_UPDATE_CACHED received: ${cachedData.objects.size} cached objects (total: $cachedObjectUpdateCount)")
                    }
                    
                    // Request full object data for all cached objects
                    // Per Lumiya protocol: respond with RequestMultipleObjects with CacheMissType=0
                    if (cachedData.objects.isNotEmpty()) {
                        val objectIds = cachedData.objects.map { it.localId }
                        applicationScope.launch {
                            udpConnection.sendRequestMultipleObjects(objectIds, cacheMissType = 0)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error handling ObjectUpdateCached", e)
            }
        }
        
        // ObjectProperties (ID 65289 / 0xFF09) - Object metadata (name, description, owner, etc.)
        // Sent by server in response to ObjectSelect or when properties change
        var objectPropertiesCount = 0
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.OBJECT_PROPERTIES) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload == null) return@registerHandler
                val propsData = com.linkpoint.protocol.messages.MessageParser.parseObjectProperties(payload)
                if (propsData != null) {
                    objectPropertiesCount += propsData.objects.size
                    // Log occasionally to avoid spam
                    if (objectPropertiesCount <= 10 || objectPropertiesCount % 50 == 0) {
                        Log.d(TAG, "OBJECT_PROPERTIES received: ${propsData.objects.size} objects (total: $objectPropertiesCount)")
                    }
                    
                    // Update ObjectManager with the received properties
                    propsData.objects.forEach { props ->
                        objectManager.handleObjectProperties(props)
                        if (objectPropertiesCount <= 5) {
                            Log.d(TAG, "  - Object '${props.name}' owner=${props.ownerId}")
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error handling ObjectProperties", e)
            }
        }
        
        // ScriptControlChange (ID -65347 / 0xFFFF00BD) - Script control permissions
        var scriptControlChangeCount = 0
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.SCRIPT_CONTROL_CHANGE) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload == null) return@registerHandler
                val controlData = com.linkpoint.protocol.messages.MessageParser.parseScriptControlChange(payload)
                if (controlData != null) {
                    scriptControlChangeCount++
                    // Log occasionally to avoid spam
                    if (scriptControlChangeCount <= 10 || scriptControlChangeCount % 100 == 0) {
                        Log.d(TAG, "SCRIPT_CONTROL_CHANGE received: ${controlData.controls.size} control changes (total: $scriptControlChangeCount)")
                        controlData.controls.forEach { ctrl ->
                            Log.d(TAG, "  - Controls: 0x${ctrl.controls.toString(16)}, take=${ctrl.takeControls}, pass=${ctrl.passToAgent}")
                        }
                    }
                    
                    // Forward to script manager
                    controlData.controls.forEach { ctrl ->
                        scriptManager.handleScriptControlChange(
                            controls = ctrl.controls,
                            takeControls = ctrl.takeControls,
                            passToAgent = ctrl.passToAgent
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error handling ScriptControlChange", e)
            }
        }
        
        // Avatar animation updates
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.AVATAR_ANIMATION) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload == null) return@registerHandler
                val animData = com.linkpoint.protocol.messages.MessageParser.parseAvatarAnimation(payload)
                if (animData != null && ::avatarManager.isInitialized) {
                    avatarManager.handleAvatarAnimation(animData)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error handling AvatarAnimation", e)
            }
        }
        
        // LayerData - Terrain heightmap, wind, and cloud data
        // Type 76 ('L') = terrain, Type 87 ('W') = wind, Type 67 ('C') = cloud
        var layerDataCount = 0
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.LAYER_DATA) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload == null) return@registerHandler
                val result = com.linkpoint.protocol.terrain.LayerDataParser.parse(payload)
                if (result != null) {
                    layerDataCount++
                    if (layerDataCount <= 5 || layerDataCount % 50 == 0) {
                        Log.d(TAG, "LAYER_DATA received: type=${result.type}, patches=${result.patches.size} (total: $layerDataCount)")
                    }
                    
                    // Process terrain data if it's land type
                    if (result.type == com.linkpoint.protocol.terrain.LayerType.LAND ||
                        result.type == com.linkpoint.protocol.terrain.LayerType.LAND_EXTENDED) {
                        if (::terrainManager.isInitialized) {
                            terrainManager.processLayerData(result)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error handling LayerData", e)
            }
        }
        
        // StartPingCheck - CRITICAL: Must respond with CompletePingCheck to maintain connection
        // The simulator sends this periodically to verify the client is still alive
        // Format: PingID (1 byte) + OldestUnacked (4 bytes)
        // We only need PingID to respond; OldestUnacked is for the sim's reference
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.START_PING_CHECK) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null && payload.isNotEmpty()) {
                    val pingId = payload[0]
                    Log.d(TAG, "StartPingCheck received: pingId=$pingId")
                    applicationScope.launch {
                        // handleStartPingCheck computes our own OldestUnacked for the response
                        udpConnection.handleStartPingCheck(pingId, 0)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error handling StartPingCheck", e)
            }
        }
        
        // ImprovedTerseObjectUpdate - Fast position updates for objects/avatars
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.IMPROVED_TERSE_OBJECT_UPDATE) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload == null) return@registerHandler
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
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.KILL_OBJECT) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload == null) return@registerHandler
                // KillObject format: 1-byte count, then list of 4-byte local IDs
                val buffer = java.nio.ByteBuffer.wrap(payload).order(java.nio.ByteOrder.LITTLE_ENDIAN)
                val count = buffer.get().toInt() and 0xFF
                for (i in 0 until count) {
                    if (buffer.remaining() >= 4) {
                        val localId = buffer.int
                        if (::objectManager.isInitialized) {
                            // Get UUID before removal so we can remove from scene
                            val obj = objectManager.getObject(localId)
                            objectManager.removeObject(localId)
                            // Remove from scene
                            if (::renderManager.isInitialized && obj != null) {
                                renderManager.getSceneManager()?.removeObject(obj.fullId)
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error handling KillObject", e)
            }
        }
        
        // CoarseLocationUpdate - Location updates for nearby avatars
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.COARSE_LOCATION_UPDATE) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload == null) return@registerHandler
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
        
        // PacketAck - Acknowledgment messages for reliable packets
        // These are sent by the simulator to confirm receipt of our reliable packets.
        // CRITICAL: When UseCircuitCode (seq 0) is acknowledged, we MUST immediately send
        // CompleteAgentMovement. This follows Lumiya's protocol behavior - the server
        // won't send RegionHandshake until it receives CompleteAgentMovement.
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.PACKET_ACK) { _, rawPacket ->
            // PacketAck format: Count (1 byte), then list of acknowledged sequence numbers (4 bytes each)
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload == null) return@registerHandler
                val buffer = java.nio.ByteBuffer.wrap(payload).order(java.nio.ByteOrder.LITTLE_ENDIAN)
                val count = buffer.get().toInt() and 0xFF
                
                // Parse the acknowledged sequence numbers and check for seq 0 during parsing
                // (optimization: avoid extra contains() call on the list)
                val ackedSequences = mutableListOf<Int>()
                var containsSeq0 = false
                for (i in 0 until count) {
                    if (buffer.remaining() >= 4) {
                        val seq = buffer.int
                        ackedSequences.add(seq)
                        if (seq == 0) containsSeq0 = true
                    }
                }
                
                Log.d(TAG, "PacketAck received: $count packets acknowledged - sequences: $ackedSequences")
                
                // Check if UseCircuitCode (sequence 0) was acknowledged
                // This is CRITICAL - per Lumiya's protocol, we must send CompleteAgentMovement
                // immediately when UseCircuitCode is ACKed. The server waits for this before
                // sending RegionHandshake and other world data.
                // Using compareAndSet for thread-safe, atomic check-and-set operation
                if (containsSeq0 && completeAgentMovementSent.compareAndSet(false, true)) {
                    Log.i(TAG, "╔══════════════════════════════════════════════════════════════════")
                    Log.i(TAG, "║ ⭐ UseCircuitCode ACKNOWLEDGED - Sending CompleteAgentMovement")
                    Log.i(TAG, "╚══════════════════════════════════════════════════════════════════")
                    
                    applicationScope.launch {
                        try {
                            // Send CompleteAgentMovement to signal we're ready to enter the region
                            udpConnection.sendCompleteAgentMovement()
                            Log.i(TAG, "✓ CompleteAgentMovement SENT - server should now send RegionHandshake")
                            
                            // Also send AgentThrottle to configure bandwidth
                            udpConnection.sendAgentThrottle()
                            Log.i(TAG, "✓ AgentThrottle SENT - bandwidth configured")
                        } catch (e: Exception) {
                            Log.e(TAG, "✗ Error sending CompleteAgentMovement/AgentThrottle", e)
                            // Reset flag atomically to allow retry on next ACK
                            completeAgentMovementSent.set(false)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error handling PacketAck", e)
            }
        }
        
        // SoundTrigger - Sound triggered by in-world scripts (llTriggerSound)
        // These are sent when a script plays a sound that should be heard by nearby avatars.
        // We register a handler to prevent "No handler registered" warnings.
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.SOUND_TRIGGER) { _, rawPacket ->
            // SoundTrigger format:
            // - SoundID (UUID, 16 bytes) - The sound asset to play
            // - OwnerID (UUID, 16 bytes) - Owner of the object playing the sound  
            // - ObjectID (UUID, 16 bytes) - The object triggering the sound
            // - ParentID (UUID, 16 bytes) - Parent object (if linked)
            // - Handle (U64, 8 bytes) - Region handle
            // - Position (Vector3, 12 bytes) - Position of the sound
            // - Gain (F32, 4 bytes) - Volume (0.0 to 1.0)
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload == null) return@registerHandler
                
                if (payload.size >= 16) {
                    val soundIdBytes = payload.copyOfRange(0, 16)
                    val soundId = java.util.UUID(
                        java.nio.ByteBuffer.wrap(soundIdBytes.copyOfRange(0, 8)).long,
                        java.nio.ByteBuffer.wrap(soundIdBytes.copyOfRange(8, 16)).long
                    )
                    
                    // Pass to SoundManager for potential playback
                    // Note: Full sound playback implementation would require:
                    // 1. Downloading the sound asset
                    // 2. Decoding the OGG/Vorbis audio
                    // 3. Playing at the appropriate volume/position
                    Log.d(TAG, "SoundTrigger received: soundId=$soundId")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error handling SoundTrigger", e)
            }
        }
        
        // OnlineNotification - Friend came online (UDP fallback for capability events)
        // The simulator sends this when a friend comes online if event system unavailable
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.ONLINE_NOTIFICATION) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload == null) return@registerHandler
                
                // OnlineNotification format: AgentBlock[] containing AgentID (16 bytes each)
                val buffer = java.nio.ByteBuffer.wrap(payload).order(java.nio.ByteOrder.LITTLE_ENDIAN)
                val count = if (buffer.remaining() >= 1) buffer.get().toInt() and 0xFF else 0
                
                Log.i(TAG, "🟢 OnlineNotification received: $count friends came online")
                
                for (i in 0 until count) {
                    if (buffer.remaining() >= 16) {
                        val agentId = buffer.getUUID()
                        Log.i(TAG, "🟢 Friend online: $agentId")
                        
                        // Notify FriendsManager via shared flow if initialized
                        if (::friendsManager.isInitialized) {
                            friendsManager.handleUdpOnlineNotification(agentId)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error handling OnlineNotification", e)
            }
        }
        
        // OfflineNotification - Friend went offline (UDP fallback for capability events)
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.OFFLINE_NOTIFICATION) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload == null) return@registerHandler
                
                // OfflineNotification format: AgentBlock[] containing AgentID (16 bytes each)
                val buffer = java.nio.ByteBuffer.wrap(payload).order(java.nio.ByteOrder.LITTLE_ENDIAN)
                val count = if (buffer.remaining() >= 1) buffer.get().toInt() and 0xFF else 0
                
                Log.i(TAG, "🔴 OfflineNotification received: $count friends went offline")
                
                for (i in 0 until count) {
                    if (buffer.remaining() >= 16) {
                        val agentId = buffer.getUUID()
                        Log.i(TAG, "🔴 Friend offline: $agentId")
                        
                        // Notify FriendsManager via shared flow if initialized
                        if (::friendsManager.isInitialized) {
                            friendsManager.handleUdpOfflineNotification(agentId)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error handling OfflineNotification", e)
            }
        }
        
        // ChangeUserRights - Friend permissions changed
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.CHANGE_USER_RIGHTS) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload == null) return@registerHandler
                
                // ChangeUserRights format:
                // AgentData: AgentID (16 bytes)
                // Rights[]: AgentRelated (16 bytes), RelatedRights (4 bytes)
                val buffer = java.nio.ByteBuffer.wrap(payload).order(java.nio.ByteOrder.LITTLE_ENDIAN)
                
                if (buffer.remaining() >= 16) {
                    buffer.getUUID() // Skip AgentID (our ID)
                    
                    if (buffer.remaining() >= 1) {
                        val rightsCount = buffer.get().toInt() and 0xFF
                        
                        for (i in 0 until rightsCount) {
                            if (buffer.remaining() >= 20) {  // 16 bytes UUID + 4 bytes rights
                                val relatedId = buffer.getUUID()
                                val rights = buffer.int
                                
                                Log.i(TAG, "🔐 ChangeUserRights: friend=$relatedId rights=$rights")
                                
                                if (::friendsManager.isInitialized) {
                                    friendsManager.handleUdpRightsChange(relatedId, rights)
                                }
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error handling ChangeUserRights", e)
            }
        }
        
        // AgentDataUpdate - Agent data updated (active group, title, etc.)
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.AGENT_DATA_UPDATE) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload == null) return@registerHandler
                
                // AgentDataUpdate format:
                // AgentData: AgentID (16 bytes), FirstName (var), LastName (var), 
                // GroupTitle (var), ActiveGroupID (16 bytes), GroupPowers (8 bytes), GroupName (var)
                val buffer = java.nio.ByteBuffer.wrap(payload).order(java.nio.ByteOrder.LITTLE_ENDIAN)
                
                if (buffer.remaining() >= 16) {
                    buffer.getUUID() // Skip AgentID
                    
                    // Parse variable strings (null-terminated)
                    fun readVarString(): String {
                        val bytes = mutableListOf<Byte>()
                        while (buffer.remaining() > 0) {
                            val b = buffer.get()
                            if (b == 0.toByte()) break
                            bytes.add(b)
                        }
                        return String(bytes.toByteArray(), Charsets.UTF_8)
                    }
                    
                    val firstName = readVarString()
                    val lastName = readVarString()
                    val groupTitle = readVarString()
                    
                    var activeGroupId: java.util.UUID? = null
                    var groupPowers = 0L
                    var groupName = ""
                    
                    if (buffer.remaining() >= 16) {
                        activeGroupId = buffer.getUUID()
                    }
                    
                    if (buffer.remaining() >= 8) {
                        groupPowers = buffer.long
                    }
                    
                    if (buffer.remaining() > 0) {
                        groupName = readVarString()
                    }
                    
                    Log.i(TAG, "👤 AgentDataUpdate: $firstName $lastName, group='$groupTitle' ($groupName)")
                    
                    // Update session manager with agent data
                    if (::sessionManager.isInitialized) {
                        sessionManager.updateAgentData(firstName, lastName, groupTitle, activeGroupId, groupPowers, groupName)
                    }
                    
                    // Update groups manager
                    if (::groupsManager.isInitialized && activeGroupId != null) {
                        groupsManager.handleActiveGroupUpdate(activeGroupId, groupTitle, groupPowers)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error handling AgentDataUpdate", e)
            }
        }
        
        // HealthMessage - Agent health status
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.HEALTH_MESSAGE) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload == null) return@registerHandler
                
                // HealthMessage format: HealthData: Health (F32, 4 bytes)
                val buffer = java.nio.ByteBuffer.wrap(payload).order(java.nio.ByteOrder.LITTLE_ENDIAN)
                
                if (buffer.remaining() >= 4) {
                    val health = buffer.float
                    Log.d(TAG, "❤️ HealthMessage: health=$health%")
                    
                    // Update avatar state with health
                    if (::avatarManager.isInitialized) {
                        avatarManager.updateAgentHealth(health)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error handling HealthMessage", e)
            }
        }
        
        // ParcelOverlay - Parcel boundary data for minimap/rendering
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.PARCEL_OVERLAY) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload == null) return@registerHandler
                
                // ParcelOverlay format:
                // ParcelData: SequenceID (S32, 4 bytes), Data (variable - compressed bitmap)
                // The bitmap represents parcel boundaries in a 64x64 grid (4 bits per parcel)
                val buffer = java.nio.ByteBuffer.wrap(payload).order(java.nio.ByteOrder.LITTLE_ENDIAN)
                
                if (buffer.remaining() >= 4) {
                    val sequenceId = buffer.int
                    val dataSize = buffer.remaining()
                    
                    if (dataSize > 0) {
                        val overlayData = ByteArray(dataSize)
                        buffer.get(overlayData)
                        
                        Log.d(TAG, "🗺️ ParcelOverlay: sequence=$sequenceId, dataSize=$dataSize bytes")
                        
                        // Forward to parcel manager for minimap rendering
                        if (::parcelManager.isInitialized) {
                            parcelManager.handleParcelOverlay(sequenceId, overlayData)
                        }
                        
                        // Also forward to minimap manager if separate
                        if (::minimapManager.isInitialized) {
                            minimapManager.handleParcelOverlay(sequenceId, overlayData)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error handling ParcelOverlay", e)
            }
        }
        
        // =====================================
        // PHASE 1 CRITICAL HANDLERS
        // =====================================
        
        // TeleportFinish - Teleport completed successfully
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.TELEPORT_FINISH) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload == null) return@registerHandler
                
                val data = com.linkpoint.protocol.messages.MessageParser.parseTeleportFinish(payload)
                if (data != null) {
                    Log.i(TAG, "🚀 TeleportFinish: Connecting to ${data.simIP}:${data.simPort}, handle=${data.regionHandle}")
                    
                    // Notify teleport manager
                    if (::teleportManager.isInitialized) {
                        teleportManager.handleTeleportFinish(data)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error handling TeleportFinish", e)
            }
        }
        
        // TeleportFailed - Teleport failed with reason
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.TELEPORT_FAILED) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload == null) return@registerHandler
                
                val data = com.linkpoint.protocol.messages.MessageParser.parseTeleportFailed(payload)
                if (data != null) {
                    Log.e(TAG, "❌ TeleportFailed: ${data.reason}")
                    
                    // Notify teleport manager
                    if (::teleportManager.isInitialized) {
                        teleportManager.handleTeleportFailed(data.reason)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error handling TeleportFailed", e)
            }
        }
        
        // TeleportProgress - Teleport status update
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.TELEPORT_PROGRESS) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload == null) return@registerHandler
                
                val data = com.linkpoint.protocol.messages.MessageParser.parseTeleportProgress(payload)
                if (data != null) {
                    Log.i(TAG, "🔄 TeleportProgress: ${data.message}")
                    
                    // Notify teleport manager
                    if (::teleportManager.isInitialized) {
                        teleportManager.handleTeleportProgress(data.message)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error handling TeleportProgress", e)
            }
        }
        
        // TeleportStart - Teleport sequence starting
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.TELEPORT_START) { _, rawPacket ->
            try {
                Log.i(TAG, "🚀 TeleportStart: Teleport sequence beginning")
                // TeleportStart has minimal payload, just acknowledge it
                if (::teleportManager.isInitialized) {
                    teleportManager.handleTeleportStart()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error handling TeleportStart", e)
            }
        }
        
        // AlertMessage - System alert
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.ALERT_MESSAGE) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload == null) return@registerHandler
                
                val data = com.linkpoint.protocol.messages.MessageParser.parseAlertMessage(payload)
                if (data != null) {
                    Log.w(TAG, "⚠️ AlertMessage: ${data.message}")
                    
                    // Show alert to user via script dialog manager or notification
                    if (::scriptDialogManager.isInitialized) {
                        scriptDialogManager.showSystemAlert(data.message)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error handling AlertMessage", e)
            }
        }
        
        // AgentAlertMessage - Agent-specific alert
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.AGENT_ALERT_MESSAGE) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload == null) return@registerHandler
                
                val data = com.linkpoint.protocol.messages.MessageParser.parseAgentAlertMessage(payload)
                if (data != null) {
                    Log.w(TAG, "⚠️ AgentAlertMessage: ${data.message} (modal=${data.modal})")
                    
                    // Show alert to user
                    if (::scriptDialogManager.isInitialized) {
                        scriptDialogManager.showAgentAlert(data.message, data.modal)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error handling AgentAlertMessage", e)
            }
        }
        
        // EnableSimulator - Enable connection to neighbor sim
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.ENABLE_SIMULATOR) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload == null) return@registerHandler
                
                val data = com.linkpoint.protocol.messages.MessageParser.parseEnableSimulator(payload)
                if (data != null) {
                    Log.i(TAG, "🌐 EnableSimulator: Neighbor sim at ${data.ip}:${data.port}, handle=${data.handle}")
                    
                    // This would typically connect to the neighbor sim for seamless region crossings
                    // For now, just log it
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error handling EnableSimulator", e)
            }
        }
        
        // CrossedRegion - Agent crossed into new region (medium frequency)
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.CROSSED_REGION) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload == null) return@registerHandler
                
                val data = com.linkpoint.protocol.messages.MessageParser.parseCrossedRegion(payload)
                if (data != null) {
                    Log.i(TAG, "🚶 CrossedRegion: Moving to ${data.simIP}:${data.simPort}, position=${data.position}")
                    
                    // Handle region crossing - connect to new region
                    if (::teleportManager.isInitialized) {
                        teleportManager.handleCrossedRegion(data)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error handling CrossedRegion", e)
            }
        }
        
        // ParcelProperties - Full parcel information (high frequency)
        // TODO: Implement ParcelProperties parser - complex message with many fields
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.PARCEL_PROPERTIES) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload == null) return@registerHandler
                
                // ParcelProperties message is complex (70+ fields)
                // Need to implement parser before forwarding to parcelManager
                Log.d(TAG, "🗺️ ParcelProperties received (${payload.size} bytes)")
            } catch (e: Exception) {
                Log.e(TAG, "Error handling ParcelProperties", e)
            }
        }
        
        // =====================================
        // PHASE 2: 50 Additional Message Handlers
        // =====================================
        
        // --- Script/Dialog Messages ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.SCRIPT_DIALOG) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null && ::scriptDialogManager.isInitialized) {
                    scriptDialogManager.handleScriptDialog(payload)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error handling ScriptDialog", e)
            }
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.SCRIPT_QUESTION) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null && ::scriptDialogManager.isInitialized) {
                    scriptDialogManager.handleScriptQuestion(payload)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error handling ScriptQuestion", e)
            }
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.LOAD_URL) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null && ::scriptDialogManager.isInitialized) {
                    scriptDialogManager.handleLoadUrl(payload)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error handling LoadUrl", e)
            }
        }
        
        // --- Economy Messages ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.MONEY_BALANCE_REPLY) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null && ::economyManager.isInitialized) {
                    economyManager.handleMoneyBalanceReply(payload)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error handling MoneyBalanceReply", e)
            }
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.ECONOMY_DATA) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null && ::economyManager.isInitialized) {
                    economyManager.handleEconomyData(payload)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error handling EconomyData", e)
            }
        }
        
        // --- Inventory Messages ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.INVENTORY_DESCENDENTS) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null) {
                    val data = com.linkpoint.protocol.messages.AdditionalMessageParsers.parseInventoryDescendents(payload)
                    if (data != null && ::inventoryManager.isInitialized) {
                        Log.d(TAG, "📦 InventoryDescendents: ${data.folders.size} folders, ${data.items.size} items")
                        // Process folders and items via inventory manager
                        data.folders.forEach { folder ->
                            inventoryManager.addFolderFromLogin(folder.folderID, folder.parentID, folder.name, folder.type, 0)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error handling InventoryDescendents", e)
            }
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.FETCH_INVENTORY_REPLY) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null) {
                    val data = com.linkpoint.protocol.messages.AdditionalMessageParsers.parseInventoryDescendents(payload)
                    if (data != null) {
                        Log.d(TAG, "📦 FetchInventoryReply: ${data.items.size} items")
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error handling FetchInventoryReply", e)
            }
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.BULK_UPDATE_INVENTORY) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null) {
                    Log.d(TAG, "📦 BulkUpdateInventory received (${payload.size} bytes)")
                    // Complex message - forward to inventory manager when fully implemented
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error handling BulkUpdateInventory", e)
            }
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.UPDATE_CREATE_INVENTORY_ITEM) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null) {
                    Log.d(TAG, "📦 UpdateCreateInventoryItem received (${payload.size} bytes)")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error handling UpdateCreateInventoryItem", e)
            }
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.REMOVE_INVENTORY_ITEM) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null && ::inventoryManager.isInitialized) {
                    val buffer = java.nio.ByteBuffer.wrap(payload).order(java.nio.ByteOrder.LITTLE_ENDIAN)
                    buffer.position(32) // Skip AgentData
                    val itemCount = buffer.get().toInt() and 0xFF
                    for (i in 0 until itemCount) {
                        if (buffer.remaining() >= 16) {
                            val itemId = buffer.getUUID()
                            inventoryManager.removeItem(itemId)
                            Log.d(TAG, "📦 RemoveInventoryItem: $itemId")
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error handling RemoveInventoryItem", e)
            }
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.REMOVE_INVENTORY_FOLDER) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null && ::inventoryManager.isInitialized) {
                    val buffer = java.nio.ByteBuffer.wrap(payload).order(java.nio.ByteOrder.LITTLE_ENDIAN)
                    buffer.position(32) // Skip AgentData
                    val folderCount = buffer.get().toInt() and 0xFF
                    for (i in 0 until folderCount) {
                        if (buffer.remaining() >= 16) {
                            val folderId = buffer.getUUID()
                            inventoryManager.removeFolder(folderId)
                            Log.d(TAG, "📦 RemoveInventoryFolder: $folderId")
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error handling RemoveInventoryFolder", e)
            }
        }
        
        // --- Avatar/Appearance Messages ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.AVATAR_APPEARANCE) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null) {
                    val data = com.linkpoint.protocol.messages.AdditionalMessageParsers.parseAvatarAppearance(payload)
                    if (data != null && ::avatarManager.isInitialized) {
                        Log.d(TAG, "👤 AvatarAppearance: ${data.senderID} (${data.visualParams.size} params)")
                        avatarManager.handleAvatarAppearance(data.senderID, data.textureEntries, data.visualParams)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error handling AvatarAppearance", e)
            }
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.AGENT_WEARABLES_UPDATE) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null) {
                    val data = com.linkpoint.protocol.messages.AdditionalMessageParsers.parseAgentWearablesUpdate(payload)
                    if (data != null && ::avatarManager.isInitialized) {
                        Log.d(TAG, "👔 AgentWearablesUpdate: ${data.wearables.size} wearables")
                        avatarManager.handleWearablesUpdate(data.wearables)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error handling AgentWearablesUpdate", e)
            }
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.AGENT_CACHED_TEXTURE) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null) {
                    Log.d(TAG, "🖼️ AgentCachedTexture received (${payload.size} bytes)")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error handling AgentCachedTexture", e)
            }
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.AGENT_CACHED_TEXTURE_RESPONSE) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null) {
                    Log.d(TAG, "🖼️ AgentCachedTextureResponse received (${payload.size} bytes)")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error handling AgentCachedTextureResponse", e)
            }
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.AVATAR_PROPERTIES_REPLY) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null) {
                    val data = com.linkpoint.protocol.messages.AdditionalMessageParsers.parseAvatarPropertiesReply(payload)
                    if (data != null) {
                        Log.d(TAG, "👤 AvatarPropertiesReply: ${data.avatarID}")
                        // Cache profile data for later use
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error handling AvatarPropertiesReply", e)
            }
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.AVATAR_INTERESTS_REPLY) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null) {
                    Log.d(TAG, "👤 AvatarInterestsReply received (${payload.size} bytes)")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error handling AvatarInterestsReply", e)
            }
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.AVATAR_GROUPS_REPLY) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null) {
                    Log.d(TAG, "👤 AvatarGroupsReply received (${payload.size} bytes)")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error handling AvatarGroupsReply", e)
            }
        }
        
        // --- Group Messages ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.GROUP_PROFILE_REPLY) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null) {
                    val data = com.linkpoint.protocol.messages.AdditionalMessageParsers.parseGroupProfileReply(payload)
                    if (data != null && ::groupsManager.isInitialized) {
                        Log.d(TAG, "👥 GroupProfileReply: ${data.name} (${data.groupMembershipCount} members)")
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error handling GroupProfileReply", e)
            }
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.GROUP_MEMBERS_REPLY) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null) {
                    Log.d(TAG, "👥 GroupMembersReply received (${payload.size} bytes)")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error handling GroupMembersReply", e)
            }
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.GROUP_ROLE_DATA_REPLY) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null && ::groupsManager.isInitialized) {
                    Log.d(TAG, "👥 GroupRoleDataReply (${payload.size} bytes)")
                    // Parse group roles data
                    groupsManager.handleGroupRoleData(payload)
                }
            } catch (e: Exception) { Log.e(TAG, "Error handling GroupRoleDataReply", e) }
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.GROUP_TITLES_REPLY) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null && ::groupsManager.isInitialized) {
                    Log.d(TAG, "👥 GroupTitlesReply (${payload.size} bytes)")
                    groupsManager.handleGroupTitles(payload)
                }
            } catch (e: Exception) { Log.e(TAG, "Error handling GroupTitlesReply", e) }
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.GROUP_NOTICE_ADD) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null && ::groupsManager.isInitialized) {
                    Log.d(TAG, "📢 GroupNoticeAdd (${payload.size} bytes)")
                    groupsManager.handleGroupNoticeAdd(payload)
                }
            } catch (e: Exception) { Log.e(TAG, "Error handling GroupNoticeAdd", e) }
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.AGENT_GROUP_DATA_UPDATE) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null && ::groupsManager.isInitialized) {
                    Log.d(TAG, "👥 AgentGroupDataUpdate (${payload.size} bytes)")
                    groupsManager.handleAgentGroupDataUpdate(payload)
                }
            } catch (e: Exception) { Log.e(TAG, "Error handling AgentGroupDataUpdate", e) }
        }
        
        // --- Friends Messages ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.ACCEPT_FRIENDSHIP) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null) {
                    val data = com.linkpoint.protocol.messages.AdditionalMessageParsers.parseAcceptFriendship(payload)
                    if (data != null && ::friendsManager.isInitialized) {
                        Log.d(TAG, "🤝 AcceptFriendship: agent=${data.agentID}, transaction=${data.transactionID}")
                        friendsManager.handleFriendshipAccepted(data.agentID, data.transactionID)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error handling AcceptFriendship", e)
            }
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.DECLINE_FRIENDSHIP) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null) {
                    val data = com.linkpoint.protocol.messages.AdditionalMessageParsers.parseDeclineFriendship(payload)
                    if (data != null && ::friendsManager.isInitialized) {
                        Log.d(TAG, "🚫 DeclineFriendship: agent=${data.agentID}, transaction=${data.transactionID}")
                        friendsManager.handleFriendshipDeclined(data.agentID, data.transactionID)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error handling DeclineFriendship", e)
            }
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.FORM_FRIENDSHIP) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null) {
                    val data = com.linkpoint.protocol.messages.AdditionalMessageParsers.parseFormFriendship(payload)
                    if (data != null && ::friendsManager.isInitialized) {
                        Log.d(TAG, "🤝 FormFriendship: ${data.fromAgentID} -> ${data.toAgentID}")
                        friendsManager.handleFriendshipFormed(data.fromAgentID, data.toAgentID)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error handling FormFriendship", e)
            }
        }
        
        // --- Map Messages ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.MAP_BLOCK_REPLY) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null) {
                    val data = com.linkpoint.protocol.messages.AdditionalMessageParsers.parseMapBlockReply(payload)
                    if (data != null && ::worldMap.isInitialized) {
                        Log.d(TAG, "🗺️ MapBlockReply: ${data.blocks.size} blocks")
                        data.blocks.forEach { block ->
                            worldMap.cacheRegionInfo(block.x, block.y, block.name, block.mapImageID)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error handling MapBlockReply", e)
            }
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.MAP_ITEM_REPLY) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null) {
                    Log.d(TAG, "🗺️ MapItemReply received (${payload.size} bytes)")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error handling MapItemReply", e)
            }
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.MAP_LAYER_REPLY) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null) {
                    Log.d(TAG, "🗺️ MapLayerReply received (${payload.size} bytes)")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error handling MapLayerReply", e)
            }
        }
        
        // --- Search Messages ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.DIR_PLACES_REPLY) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null) {
                    val data = com.linkpoint.protocol.messages.AdditionalMessageParsers.parseDirPlacesReply(payload)
                    if (data != null) {
                        Log.d(TAG, "🔍 DirPlacesReply: ${data.places.size} places")
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error handling DirPlacesReply", e)
            }
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.DIR_PEOPLE_REPLY) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null) {
                    Log.d(TAG, "🔍 DirPeopleReply received (${payload.size} bytes)")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error handling DirPeopleReply", e)
            }
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.DIR_GROUPS_REPLY) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null) {
                    Log.d(TAG, "🔍 DirGroupsReply received (${payload.size} bytes)")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error handling DirGroupsReply", e)
            }
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.DIR_EVENTS_REPLY) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null) {
                    Log.d(TAG, "🔍 DirEventsReply received (${payload.size} bytes)")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error handling DirEventsReply", e)
            }
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.DIR_LAND_REPLY) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null) {
                    Log.d(TAG, "🔍 DirLandReply received (${payload.size} bytes)")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error handling DirLandReply", e)
            }
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.DIR_CLASSIFIED_REPLY) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null) {
                    Log.d(TAG, "🔍 DirClassifiedReply received (${payload.size} bytes)")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error handling DirClassifiedReply", e)
            }
        }
        
        // --- Region/Estate Messages ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.REGION_INFO) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null) {
                    val data = com.linkpoint.protocol.messages.AdditionalMessageParsers.parseRegionInfo(payload)
                    if (data != null && ::sessionManager.isInitialized) {
                        Log.d(TAG, "🌍 RegionInfo: ${data.regionName} (estate ${data.estateID})")
                        sessionManager.updateRegionInfo(data.regionName, data.waterHeight, data.terrainRaiseLimit, data.terrainLowerLimit)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error handling RegionInfo", e)
            }
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.SIM_STATS) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null) {
                    val data = com.linkpoint.protocol.messages.AdditionalMessageParsers.parseSimStats(payload)
                    if (data != null && ::sessionManager.isInitialized) {
                        sessionManager.updateSimStats(data)
                    }
                }
            } catch (e: Exception) { Log.e(TAG, "Error handling SimStats", e) }
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.ESTATE_COVENANT_REPLY) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null) {
                    Log.d(TAG, "📜 EstateCovenantReply received (${payload.size} bytes)")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error handling EstateCovenantReply", e)
            }
        }
        
        // --- Parcel Messages ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.PARCEL_INFO_REPLY) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null) {
                    val data = com.linkpoint.protocol.messages.AdditionalMessageParsers.parseParcelInfoReply(payload)
                    if (data != null && ::parcelManager.isInitialized) {
                        Log.d(TAG, "🏠 ParcelInfoReply: ${data.name} by ${data.ownerID}")
                        parcelManager.handleParcelInfoReply(data)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error handling ParcelInfoReply", e)
            }
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.PARCEL_ACCESS_LIST_REPLY) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null) {
                    Log.d(TAG, "🏠 ParcelAccessListReply received (${payload.size} bytes)")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error handling ParcelAccessListReply", e)
            }
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.PARCEL_DWELL_REPLY) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null) {
                    Log.d(TAG, "🏠 ParcelDwellReply received (${payload.size} bytes)")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error handling ParcelDwellReply", e)
            }
        }
        
        // --- Object Messages ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.OBJECT_PROPERTIES_FAMILY) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null) {
                    val data = com.linkpoint.protocol.messages.AdditionalMessageParsers.parseObjectPropertiesFamily(payload)
                    if (data != null && ::objectManager.isInitialized) {
                        Log.d(TAG, "📦 ObjectPropertiesFamily: ${data.name} (${data.objectID})")
                        objectManager.handleObjectPropertiesFamily(data)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error handling ObjectPropertiesFamily", e)
            }
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.OBJECT_ADD) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null) {
                    Log.d(TAG, "📦 ObjectAdd received (${payload.size} bytes)")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error handling ObjectAdd", e)
            }
        }
        
        // --- Sound Messages ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.ATTACHED_SOUND) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null) {
                    val data = com.linkpoint.protocol.messages.AdditionalMessageParsers.parseAttachedSound(payload)
                    if (data != null && ::soundManager.isInitialized) {
                        Log.d(TAG, "🔊 AttachedSound: ${data.soundID} on ${data.objectID}")
                        soundManager.playAttachedSound(data.soundID, data.objectID, data.ownerID, data.gain)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error handling AttachedSound", e)
            }
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.ATTACHED_SOUND_GAIN_CHANGE) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null) {
                    Log.d(TAG, "🔊 AttachedSoundGainChange received (${payload.size} bytes)")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error handling AttachedSoundGainChange", e)
            }
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.PRELOAD_SOUND) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null) {
                    val data = com.linkpoint.protocol.messages.AdditionalMessageParsers.parsePreloadSound(payload)
                    if (data != null && ::soundManager.isInitialized) {
                        Log.d(TAG, "🔊 PreloadSound: ${data.soundID}")
                        soundManager.preloadSound(data.soundID)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error handling PreloadSound", e)
            }
        }
        
        // --- Effect Messages ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.VIEWER_EFFECT) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null) {
                    val data = com.linkpoint.protocol.messages.AdditionalMessageParsers.parseViewerEffect(payload)
                    if (data != null) {
                        Log.d(TAG, "✨ ViewerEffect: ${data.effects.size} effects")
                        // Process viewer effects (beam, look at, point at, etc.)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error handling ViewerEffect", e)
            }
        }
        
        // --- Transfer/Asset Messages ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.TRANSFER_INFO) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null) {
                    val data = com.linkpoint.protocol.messages.AdditionalMessageParsers.parseTransferInfo(payload)
                    if (data != null && ::transferManager.isInitialized) {
                        Log.d(TAG, "📥 TransferInfo: ${data.transferID} status=${data.status}")
                        transferManager.handleTransferInfo(data)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error handling TransferInfo", e)
            }
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.TRANSFER_PACKET) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null) {
                    val data = com.linkpoint.protocol.messages.AdditionalMessageParsers.parseTransferPacket(payload)
                    if (data != null && ::transferManager.isInitialized) {
                        Log.d(TAG, "📥 TransferPacket: ${data.transferID} packet=${data.packet}")
                        transferManager.handleTransferPacket(data)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error handling TransferPacket", e)
            }
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.ABORT_XFER) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null) {
                    Log.d(TAG, "❌ AbortXfer received (${payload.size} bytes)")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error handling AbortXfer", e)
            }
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.IMAGE_NOT_IN_DATABASE) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null && ::textureManager.isInitialized) {
                    val buffer = java.nio.ByteBuffer.wrap(payload)
                    if (buffer.remaining() >= 16) {
                        val imageId = buffer.getUUID()
                        Log.d(TAG, "🖼️ ImageNotInDatabase: $imageId")
                        textureManager.handleImageNotInDatabase(imageId)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error handling ImageNotInDatabase", e)
            }
        }
        
        // --- Misc Messages ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.MEAN_COLLISION_ALERT) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null) {
                    val data = com.linkpoint.protocol.messages.AdditionalMessageParsers.parseMeanCollisionAlert(payload)
                    if (data != null) {
                        Log.d(TAG, "💥 MeanCollisionAlert: ${data.collisions.size} collisions")
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error handling MeanCollisionAlert", e)
            }
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.AVATAR_SIT_RESPONSE) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null) {
                    val data = com.linkpoint.protocol.messages.AdditionalMessageParsers.parseAvatarSitResponse(payload)
                    if (data != null && ::avatarManager.isInitialized) {
                        Log.d(TAG, "🪑 AvatarSitResponse: sitting on ${data.sitObjectID}")
                        avatarManager.handleSitResponse(data)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error handling AvatarSitResponse", e)
            }
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.CAMERA_CONSTRAINT) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null) {
                    Log.d(TAG, "📷 CameraConstraint received (${payload.size} bytes)")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error handling CameraConstraint", e)
            }
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.CONFIRM_ENABLE_SIMULATOR) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null) {
                    Log.d(TAG, "🌐 ConfirmEnableSimulator received (${payload.size} bytes)")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error handling ConfirmEnableSimulator", e)
            }
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.SIM_STATUS) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null) {
                    Log.d(TAG, "📊 SimStatus received (${payload.size} bytes)")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error handling SimStatus", e)
            }
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.LOGOUT_REPLY) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null) {
                    val data = com.linkpoint.protocol.messages.AdditionalMessageParsers.parseLogoutReply(payload)
                    if (data != null && ::sessionManager.isInitialized) {
                        Log.d(TAG, "👋 LogoutReply: session=${data.sessionID}")
                        sessionManager.disconnect()
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error handling LogoutReply", e)
            }
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.UUID_NAME_REPLY) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null) {
                    val data = com.linkpoint.protocol.messages.AdditionalMessageParsers.parseUUIDNameReply(payload)
                    if (data != null) {
                        Log.d(TAG, "🏷️ UUIDNameReply: ${data.entries.size} names")
                        data.entries.forEach { entry ->
                            // Cache names for display
                            Log.d(TAG, "  ${entry.id} -> ${entry.firstName} ${entry.lastName}")
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error handling UUIDNameReply", e)
            }
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.UUID_GROUP_NAME_REPLY) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null) {
                    Log.d(TAG, "🏷️ UUIDGroupNameReply received (${payload.size} bytes)")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error handling UUIDGroupNameReply", e)
            }
        }
        
        // =====================================
        // PHASE 3: 100 Additional Message Handlers
        // =====================================
        
        // --- High Frequency Messages ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.NEIGHBOR_LIST) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null) {
                    Log.d(TAG, "🌐 NeighborList (${payload.size} bytes)")
                    // Parse neighbor regions for region crossing preparation
                }
            } catch (e: Exception) { Log.e(TAG, "Error handling NeighborList", e) }
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.REQUEST_IMAGE) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null) Log.d(TAG, "🖼️ RequestImage (${payload.size} bytes)")
            } catch (e: Exception) { Log.e(TAG, "Error handling RequestImage", e) }
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.IMAGE_DATA) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null && ::textureManager.isInitialized) {
                    // First packet of texture data
                    textureManager.handleImageData(payload)
                }
            } catch (e: Exception) { Log.e(TAG, "Error handling ImageData", e) }
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.IMAGE_PACKET) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null && ::textureManager.isInitialized) {
                    // Subsequent packets of texture data
                    textureManager.handleImagePacket(payload)
                }
            } catch (e: Exception) { Log.e(TAG, "Error handling ImagePacket", e) }
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.EDGE_DATA_PACKET) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null) Log.d(TAG, "🌐 EdgeDataPacket (${payload.size} bytes)")
            } catch (e: Exception) { Log.e(TAG, "Error handling EdgeDataPacket", e) }
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.CHILD_AGENT_UPDATE) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null) Log.d(TAG, "👤 ChildAgentUpdate (${payload.size} bytes)")
            } catch (e: Exception) { Log.e(TAG, "Error handling ChildAgentUpdate", e) }
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.CHILD_AGENT_ALIVE) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null) Log.d(TAG, "👤 ChildAgentAlive (${payload.size} bytes)")
            } catch (e: Exception) { Log.e(TAG, "Error handling ChildAgentAlive", e) }
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.CHILD_AGENT_POSITION_UPDATE) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null) Log.d(TAG, "👤 ChildAgentPositionUpdate (${payload.size} bytes)")
            } catch (e: Exception) { Log.e(TAG, "Error handling ChildAgentPositionUpdate", e) }
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.ATOMIC_PASS_OBJECT) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null) Log.d(TAG, "📦 AtomicPassObject (${payload.size} bytes)")
            } catch (e: Exception) { Log.e(TAG, "Error handling AtomicPassObject", e) }
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.SEND_XFER_PACKET) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null && ::xferManager.isInitialized) {
                    xferManager.handleXferPacket(payload)
                }
            } catch (e: Exception) { Log.e(TAG, "Error handling SendXferPacket", e) }
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.CONFIRM_XFER_PACKET) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null) Log.d(TAG, "📥 ConfirmXferPacket (${payload.size} bytes)")
            } catch (e: Exception) { Log.e(TAG, "Error handling ConfirmXferPacket", e) }
        }
        
        // --- Agent Messages ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.AGENT_PAUSE) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null) Log.d(TAG, "⏸️ AgentPause (${payload.size} bytes)")
            } catch (e: Exception) { Log.e(TAG, "Error handling AgentPause", e) }
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.AGENT_RESUME) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null) Log.d(TAG, "▶️ AgentResume (${payload.size} bytes)")
            } catch (e: Exception) { Log.e(TAG, "Error handling AgentResume", e) }
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.AGENT_DROP_GROUP) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null && ::groupsManager.isInitialized) {
                    val buffer = java.nio.ByteBuffer.wrap(payload).order(java.nio.ByteOrder.LITTLE_ENDIAN)
                    buffer.position(32) // Skip AgentData
                    val groupId = buffer.getUUID()
                    Log.d(TAG, "👥 AgentDropGroup: $groupId")
                }
            } catch (e: Exception) { Log.e(TAG, "Error handling AgentDropGroup", e) }
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.AGENT_WEARABLES_REQUEST) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null) Log.d(TAG, "👔 AgentWearablesRequest (${payload.size} bytes)")
            } catch (e: Exception) { Log.e(TAG, "Error handling AgentWearablesRequest", e) }
        }
        
        // --- Avatar Messages ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.AVATAR_PICKER_REPLY) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null) Log.d(TAG, "👤 AvatarPickerReply (${payload.size} bytes)")
            } catch (e: Exception) { Log.e(TAG, "Error handling AvatarPickerReply", e) }
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.AVATAR_NOTES_REPLY) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null) Log.d(TAG, "📝 AvatarNotesReply (${payload.size} bytes)")
            } catch (e: Exception) { Log.e(TAG, "Error handling AvatarNotesReply", e) }
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.AVATAR_PICKS_REPLY) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null) Log.d(TAG, "⭐ AvatarPicksReply (${payload.size} bytes)")
            } catch (e: Exception) { Log.e(TAG, "Error handling AvatarPicksReply", e) }
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.AVATAR_CLASSIFIED_REPLY) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null) Log.d(TAG, "📰 AvatarClassifiedReply (${payload.size} bytes)")
            } catch (e: Exception) { Log.e(TAG, "Error handling AvatarClassifiedReply", e) }
        }
        
        // --- Classified Messages ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.CLASSIFIED_INFO_REPLY) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null) Log.d(TAG, "📰 ClassifiedInfoReply (${payload.size} bytes)")
            } catch (e: Exception) { Log.e(TAG, "Error handling ClassifiedInfoReply", e) }
        }
        
        // --- Pick Messages ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.PICK_INFO_REPLY) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null) Log.d(TAG, "⭐ PickInfoReply (${payload.size} bytes)")
            } catch (e: Exception) { Log.e(TAG, "Error handling PickInfoReply", e) }
        }
        
        // --- Event Messages ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.EVENT_INFO_REPLY) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null) Log.d(TAG, "📅 EventInfoReply (${payload.size} bytes)")
            } catch (e: Exception) { Log.e(TAG, "Error handling EventInfoReply", e) }
        }
        
        // --- Group Messages (Extended) ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.GROUP_ROLE_MEMBERS_REPLY) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null) Log.d(TAG, "👥 GroupRoleMembersReply (${payload.size} bytes)")
            } catch (e: Exception) { Log.e(TAG, "Error handling GroupRoleMembersReply", e) }
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.GROUP_NOTICES_LIST_REPLY) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null) Log.d(TAG, "📢 GroupNoticesListReply (${payload.size} bytes)")
            } catch (e: Exception) { Log.e(TAG, "Error handling GroupNoticesListReply", e) }
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.GROUP_NOTICE_REQUEST) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null) Log.d(TAG, "📢 GroupNoticeRequest (${payload.size} bytes)")
            } catch (e: Exception) { Log.e(TAG, "Error handling GroupNoticeRequest", e) }
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.CREATE_GROUP_REPLY) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null && ::groupsManager.isInitialized) {
                    Log.d(TAG, "👥 CreateGroupReply (${payload.size} bytes)")
                }
            } catch (e: Exception) { Log.e(TAG, "Error handling CreateGroupReply", e) }
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.JOIN_GROUP_REPLY) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null && ::groupsManager.isInitialized) {
                    Log.d(TAG, "👥 JoinGroupReply (${payload.size} bytes)")
                }
            } catch (e: Exception) { Log.e(TAG, "Error handling JoinGroupReply", e) }
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.LEAVE_GROUP_REPLY) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null && ::groupsManager.isInitialized) {
                    Log.d(TAG, "👥 LeaveGroupReply (${payload.size} bytes)")
                }
            } catch (e: Exception) { Log.e(TAG, "Error handling LeaveGroupReply", e) }
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.EJECT_GROUP_MEMBER_REPLY) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null) Log.d(TAG, "👥 EjectGroupMemberReply (${payload.size} bytes)")
            } catch (e: Exception) { Log.e(TAG, "Error handling EjectGroupMemberReply", e) }
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.INVITE_GROUP_RESPONSE) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null) Log.d(TAG, "👥 InviteGroupResponse (${payload.size} bytes)")
            } catch (e: Exception) { Log.e(TAG, "Error handling InviteGroupResponse", e) }
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.GROUP_ACCOUNT_SUMMARY_REPLY) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null) Log.d(TAG, "💰 GroupAccountSummaryReply (${payload.size} bytes)")
            } catch (e: Exception) { Log.e(TAG, "Error handling GroupAccountSummaryReply", e) }
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.GROUP_ACCOUNT_DETAILS_REPLY) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null) Log.d(TAG, "💰 GroupAccountDetailsReply (${payload.size} bytes)")
            } catch (e: Exception) { Log.e(TAG, "Error handling GroupAccountDetailsReply", e) }
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.GROUP_ACCOUNT_TRANSACTIONS_REPLY) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null) Log.d(TAG, "💰 GroupAccountTransactionsReply (${payload.size} bytes)")
            } catch (e: Exception) { Log.e(TAG, "Error handling GroupAccountTransactionsReply", e) }
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.GROUP_ACTIVE_PROPOSAL_ITEM_REPLY) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null) Log.d(TAG, "📋 GroupActiveProposalItemReply (${payload.size} bytes)")
            } catch (e: Exception) { Log.e(TAG, "Error handling GroupActiveProposalItemReply", e) }
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.GROUP_VOTE_HISTORY_ITEM_REPLY) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null) Log.d(TAG, "🗳️ GroupVoteHistoryItemReply (${payload.size} bytes)")
            } catch (e: Exception) { Log.e(TAG, "Error handling GroupVoteHistoryItemReply", e) }
        }
        
        // --- Calling Card Messages ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.OFFER_CALLING_CARD) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null) Log.d(TAG, "📇 OfferCallingCard (${payload.size} bytes)")
            } catch (e: Exception) { Log.e(TAG, "Error handling OfferCallingCard", e) }
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.ACCEPT_CALLING_CARD) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null) Log.d(TAG, "📇 AcceptCallingCard (${payload.size} bytes)")
            } catch (e: Exception) { Log.e(TAG, "Error handling AcceptCallingCard", e) }
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.DECLINE_CALLING_CARD) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null) Log.d(TAG, "📇 DeclineCallingCard (${payload.size} bytes)")
            } catch (e: Exception) { Log.e(TAG, "Error handling DeclineCallingCard", e) }
        }
        
        // --- Inventory Messages (Extended) ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.FETCH_INVENTORY_DESCENDENTS) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null && ::inventoryManager.isInitialized) {
                    Log.d(TAG, "📦 FetchInventoryDescendents (${payload.size} bytes)")
                }
            } catch (e: Exception) { Log.e(TAG, "Error handling FetchInventoryDescendents", e) }
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.FETCH_INVENTORY) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null && ::inventoryManager.isInitialized) {
                    Log.d(TAG, "📦 FetchInventory (${payload.size} bytes)")
                }
            } catch (e: Exception) { Log.e(TAG, "Error handling FetchInventory", e) }
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.INVENTORY_ASSET_RESPONSE) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null && ::inventoryManager.isInitialized) {
                    inventoryManager.handleAssetResponse(payload)
                }
            } catch (e: Exception) { Log.e(TAG, "Error handling InventoryAssetResponse", e) }
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.UPDATE_INVENTORY_FOLDER) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null && ::inventoryManager.isInitialized) {
                    inventoryManager.handleFolderUpdate(payload)
                }
            } catch (e: Exception) { Log.e(TAG, "Error handling UpdateInventoryFolder", e) }
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.MOVE_INVENTORY_FOLDER) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null && ::inventoryManager.isInitialized) {
                    inventoryManager.handleFolderMove(payload)
                }
            } catch (e: Exception) { Log.e(TAG, "Error handling MoveInventoryFolder", e) }
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.CREATE_INVENTORY_ITEM) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null && ::inventoryManager.isInitialized) {
                    inventoryManager.handleItemCreated(payload)
                }
            } catch (e: Exception) { Log.e(TAG, "Error handling CreateInventoryItem", e) }
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.SAVE_ASSET_INTO_INVENTORY) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null && ::inventoryManager.isInitialized) {
                    inventoryManager.handleAssetSaved(payload)
                }
            } catch (e: Exception) { Log.e(TAG, "Error handling SaveAssetIntoInventory", e) }
        }
        
        // --- Task/Object Inventory Messages ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.REQUEST_TASK_INVENTORY) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null) Log.d(TAG, "📦 RequestTaskInventory (${payload.size} bytes)")
            } catch (e: Exception) { Log.e(TAG, "Error handling RequestTaskInventory", e) }
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.REPLY_TASK_INVENTORY) { _, rawPacket ->
            Log.d(TAG, "📦 ReplyTaskInventory received")
        }
        
        // --- Object Messages (Extended) ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.OBJECT_DUPLICATE) { _, rawPacket ->
            Log.d(TAG, "📦 ObjectDuplicate received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.OBJECT_SCALE) { _, rawPacket ->
            Log.d(TAG, "📦 ObjectScale received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.OBJECT_ROTATION) { _, rawPacket ->
            Log.d(TAG, "📦 ObjectRotation received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.OBJECT_POSITION) { _, rawPacket ->
            Log.d(TAG, "📦 ObjectPosition received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.OBJECT_FLAG_UPDATE) { _, rawPacket ->
            Log.d(TAG, "📦 ObjectFlagUpdate received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.OBJECT_CLICK_ACTION) { _, rawPacket ->
            Log.d(TAG, "📦 ObjectClickAction received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.OBJECT_IMAGE) { _, rawPacket ->
            Log.d(TAG, "📦 ObjectImage received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.OBJECT_MATERIAL) { _, rawPacket ->
            Log.d(TAG, "📦 ObjectMaterial received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.OBJECT_SHAPE) { _, rawPacket ->
            Log.d(TAG, "📦 ObjectShape received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.OBJECT_OWNER) { _, rawPacket ->
            Log.d(TAG, "📦 ObjectOwner received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.OBJECT_GROUP) { _, rawPacket ->
            Log.d(TAG, "📦 ObjectGroup received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.OBJECT_BUY) { _, rawPacket ->
            Log.d(TAG, "💰 ObjectBuy received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.OBJECT_PERMISSIONS) { _, rawPacket ->
            Log.d(TAG, "🔒 ObjectPermissions received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.OBJECT_SALE_INFO) { _, rawPacket ->
            Log.d(TAG, "💰 ObjectSaleInfo received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.OBJECT_DESELECT) { _, rawPacket ->
            Log.d(TAG, "📦 ObjectDeselect received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.OBJECT_ATTACH) { _, rawPacket ->
            Log.d(TAG, "📦 ObjectAttach received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.OBJECT_DETACH) { _, rawPacket ->
            Log.d(TAG, "📦 ObjectDetach received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.OBJECT_DROP) { _, rawPacket ->
            Log.d(TAG, "📦 ObjectDrop received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.OBJECT_SPIN_START) { _, rawPacket ->
            Log.d(TAG, "📦 ObjectSpinStart received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.OBJECT_SPIN_UPDATE) { _, rawPacket ->
            Log.d(TAG, "📦 ObjectSpinUpdate received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.OBJECT_SPIN_STOP) { _, rawPacket ->
            Log.d(TAG, "📦 ObjectSpinStop received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.OBJECT_GRAB_UPDATE) { _, rawPacket ->
            Log.d(TAG, "📦 ObjectGrabUpdate received")
        }
        
        // --- Land/Terrain Messages ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.MODIFY_LAND) { _, rawPacket ->
            Log.d(TAG, "🏔️ ModifyLand received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.UNDO_LAND) { _, rawPacket ->
            Log.d(TAG, "🏔️ UndoLand received")
        }
        
        // --- Parcel Messages (Extended) ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.PARCEL_PROPERTIES_REQUEST) { _, rawPacket ->
            Log.d(TAG, "🏠 ParcelPropertiesRequest received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.PARCEL_DISABLE_OBJECTS) { _, rawPacket ->
            Log.d(TAG, "🏠 ParcelDisableObjects received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.PARCEL_SELECT_OBJECTS) { _, rawPacket ->
            Log.d(TAG, "🏠 ParcelSelectObjects received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.PARCEL_MEDIA_COMMAND_MESSAGE) { _, rawPacket ->
            Log.d(TAG, "🎬 ParcelMediaCommandMessage received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.PARCEL_MEDIA_UPDATE) { _, rawPacket ->
            Log.d(TAG, "🎬 ParcelMediaUpdate received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.PARCEL_OBJECT_OWNERS_REPLY) { _, rawPacket ->
            Log.d(TAG, "🏠 ParcelObjectOwnersReply received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.FORCE_OBJECT_SELECT) { _, rawPacket ->
            Log.d(TAG, "📦 ForceObjectSelect received")
        }
        
        // --- Money/Economy Messages ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.MONEY_TRANSFER_REQUEST) { _, rawPacket ->
            Log.d(TAG, "💰 MoneyTransferRequest received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.ROUTED_MONEY_BALANCE_REPLY) { _, rawPacket ->
            Log.d(TAG, "💰 RoutedMoneyBalanceReply received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.PAY_PRICE_REPLY) { _, rawPacket ->
            Log.d(TAG, "💰 PayPriceReply received")
        }
        
        // --- Script Messages (Extended) ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.SCRIPT_RUNNING_REPLY) { _, rawPacket ->
            Log.d(TAG, "📜 ScriptRunningReply received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.SET_SCRIPT_RUNNING) { _, rawPacket ->
            Log.d(TAG, "📜 SetScriptRunning received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.SCRIPT_RESET) { _, rawPacket ->
            Log.d(TAG, "📜 ScriptReset received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.SCRIPT_SENSOR_REPLY) { _, rawPacket ->
            Log.d(TAG, "📡 ScriptSensorReply received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.SCRIPT_TELEPORT_REQUEST) { _, rawPacket ->
            Log.d(TAG, "🚀 ScriptTeleportRequest received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.FORCE_SCRIPT_CONTROL_RELEASE) { _, rawPacket ->
            Log.d(TAG, "📜 ForceScriptControlRelease received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.REVOKE_PERMISSIONS) { _, rawPacket ->
            Log.d(TAG, "🔒 RevokePermissions received")
        }
        
        // --- Asset/Transfer Messages ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.TRANSFER_REQUEST) { _, rawPacket ->
            Log.d(TAG, "📥 TransferRequest received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.TRANSFER_ABORT) { _, rawPacket ->
            Log.d(TAG, "❌ TransferAbort received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.REQUEST_XFER) { _, rawPacket ->
            Log.d(TAG, "📥 RequestXfer received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.ASSET_UPLOAD_REQUEST) { _, rawPacket ->
            Log.d(TAG, "📤 AssetUploadRequest received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.ASSET_UPLOAD_COMPLETE) { _, rawPacket ->
            Log.d(TAG, "📤 AssetUploadComplete received")
        }
        
        // --- Region/Sim Messages ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.REQUEST_REGION_INFO) { _, rawPacket ->
            Log.d(TAG, "🌍 RequestRegionInfo received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.SIMULATOR_VIEWER_TIME_MESSAGE) { _, rawPacket ->
            Log.d(TAG, "🕐 SimulatorViewerTimeMessage received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.TELEPORT_LOCAL) { _, rawPacket ->
            Log.d(TAG, "🚀 TeleportLocal received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.TELEPORT_CANCEL) { _, rawPacket ->
            Log.d(TAG, "🚀 TeleportCancel received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.TELEPORT_REQUEST) { _, rawPacket ->
            Log.d(TAG, "🚀 TeleportRequest received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.SIM_CRASHED) { _, rawPacket ->
            Log.d(TAG, "💥 SimCrashed received")
        }
        
        // --- Map Messages (Extended) ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.MAP_BLOCK_REQUEST) { _, rawPacket ->
            Log.d(TAG, "🗺️ MapBlockRequest received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.MAP_NAME_REQUEST) { _, rawPacket ->
            Log.d(TAG, "🗺️ MapNameRequest received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.MAP_LAYER_REQUEST) { _, rawPacket ->
            Log.d(TAG, "🗺️ MapLayerRequest received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.MAP_ITEM_REQUEST) { _, rawPacket ->
            Log.d(TAG, "🗺️ MapItemRequest received")
        }
        
        // --- Mute Messages ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.MUTE_LIST_REQUEST) { _, rawPacket ->
            Log.d(TAG, "🔇 MuteListRequest received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.UPDATE_MUTE_LIST_ENTRY) { _, rawPacket ->
            Log.d(TAG, "🔇 UpdateMuteListEntry received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.REMOVE_MUTE_LIST_ENTRY) { _, rawPacket ->
            Log.d(TAG, "🔇 RemoveMuteListEntry received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.MUTE_LIST_UPDATE) { _, rawPacket ->
            Log.d(TAG, "🔇 MuteListUpdate received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.USE_CACHED_MUTE_LIST) { _, rawPacket ->
            Log.d(TAG, "🔇 UseCachedMuteList received")
        }
        
        // --- User Info Messages ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.USER_INFO_REQUEST) { _, rawPacket ->
            Log.d(TAG, "👤 UserInfoRequest received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.USER_INFO_REPLY) { _, rawPacket ->
            Log.d(TAG, "👤 UserInfoReply received")
        }
        
        // --- Generic/System Messages ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.GENERIC_MESSAGE) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null) {
                    val data = com.linkpoint.protocol.messages.AdditionalMessageParsers.parseGenericMessage(payload)
                    if (data != null) {
                        Log.d(TAG, "📨 GenericMessage: method=${data.methodName}, ${data.params.size} params")
                        handleGenericMessage(data.methodName, data.invoice, data.params)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error handling GenericMessage", e)
            }
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.SYSTEM_MESSAGE) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null) {
                    val data = com.linkpoint.protocol.messages.AdditionalMessageParsers.parseSystemMessage(payload)
                    if (data != null) {
                        Log.d(TAG, "📨 SystemMessage: method=${data.method}")
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error handling SystemMessage", e)
            }
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.ERROR_MESSAGE) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null) {
                    val data = com.linkpoint.protocol.messages.AdditionalMessageParsers.parseErrorMessage(payload)
                    if (data != null) {
                        Log.e(TAG, "❌ ErrorMessage: code=${data.errorCode}, message=${data.errorMessage}")
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error handling ErrorMessage", e)
            }
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.FEATURE_DISABLED) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null) {
                    Log.d(TAG, "🚫 FeatureDisabled received (${payload.size} bytes)")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error handling FeatureDisabled", e)
            }
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.VIEWER_FROZEN_MESSAGE) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null) {
                    Log.d(TAG, "🥶 ViewerFrozenMessage received (${payload.size} bytes)")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error handling ViewerFrozenMessage", e)
            }
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.VIEWER_STATS) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null) {
                    Log.d(TAG, "📊 ViewerStats received (${payload.size} bytes)")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error handling ViewerStats", e)
            }
        }
        
        // --- Attachment Messages ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.REZ_MULTIPLE_ATTACHMENTS_FROM_INV) { _, rawPacket ->
            Log.d(TAG, "📦 RezMultipleAttachmentsFromInv received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.DETACH_ATTACHMENT_INTO_INV) { _, rawPacket ->
            Log.d(TAG, "📦 DetachAttachmentIntoInv received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.CREATE_NEW_OUTFIT_ATTACHMENTS) { _, rawPacket ->
            Log.d(TAG, "👔 CreateNewOutfitAttachments received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.UPDATE_ATTACHMENT) { _, rawPacket ->
            Log.d(TAG, "📦 UpdateAttachment received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.REMOVE_ATTACHMENT) { _, rawPacket ->
            Log.d(TAG, "📦 RemoveAttachment received")
        }
        
        // --- Rez/DeRez Messages ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.REZ_OBJECT_FROM_NOTECARD) { _, rawPacket ->
            Log.d(TAG, "📦 RezObjectFromNotecard received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.REZ_RESTORE_TO_WORLD) { _, rawPacket ->
            Log.d(TAG, "📦 RezRestoreToWorld received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.REZ_SCRIPT) { _, rawPacket ->
            Log.d(TAG, "📜 RezScript received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.DEREZ_ACK) { _, rawPacket ->
            Log.d(TAG, "📦 DeRezAck received")
        }
        
        // --- Misc Messages ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.UNDO) { _, rawPacket ->
            Log.d(TAG, "↩️ Undo received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.REDO) { _, rawPacket ->
            Log.d(TAG, "↪️ Redo received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.SET_ALWAYS_RUN) { _, rawPacket ->
            Log.d(TAG, "🏃 SetAlwaysRun received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.INITIATE_DOWNLOAD) { _, rawPacket ->
            Log.d(TAG, "📥 InitiateDownload received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.DATA_HOME_LOCATION_REPLY) { _, rawPacket ->
            Log.d(TAG, "🏠 DataHomeLocationReply received")
        }
        
        // --- Places/Directory Messages ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.PLACES_REPLY) { _, rawPacket ->
            Log.d(TAG, "🔍 PlacesReply received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.DIR_POPULAR_REPLY) { _, rawPacket ->
            Log.d(TAG, "🔍 DirPopularReply received")
        }
        
        // =====================================
        // PHASE 4: 100 Additional Message Handlers
        // =====================================
        
        // --- Circuit/Connection Messages ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.CLOSE_CIRCUIT) { _, rawPacket ->
            Log.d(TAG, "🔌 CloseCircuit received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.OPEN_CIRCUIT) { _, rawPacket ->
            Log.d(TAG, "🔌 OpenCircuit received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.ADD_CIRCUIT_CODE) { _, rawPacket ->
            Log.d(TAG, "🔌 AddCircuitCode received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.CREATE_TRUSTED_CIRCUIT) { _, rawPacket ->
            Log.d(TAG, "🔒 CreateTrustedCircuit received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.DENY_TRUSTED_CIRCUIT) { _, rawPacket ->
            Log.d(TAG, "🚫 DenyTrustedCircuit received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.REQUEST_TRUSTED_CIRCUIT) { _, rawPacket ->
            Log.d(TAG, "🔒 RequestTrustedCircuit received")
        }
        
        // --- Auction Messages ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.CANCEL_AUCTION) { _, rawPacket ->
            Log.d(TAG, "🏷️ CancelAuction received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.COMPLETE_AUCTION) { _, rawPacket ->
            Log.d(TAG, "🏷️ CompleteAuction received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.CONFIRM_AUCTION_START) { _, rawPacket ->
            Log.d(TAG, "🏷️ ConfirmAuctionStart received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.START_AUCTION) { _, rawPacket ->
            Log.d(TAG, "🏷️ StartAuction received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.VIEWER_START_AUCTION) { _, rawPacket ->
            Log.d(TAG, "🏷️ ViewerStartAuction received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.CHECK_PARCEL_AUCTIONS) { _, rawPacket ->
            Log.d(TAG, "🏷️ CheckParcelAuctions received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.CHECK_PARCEL_SALES) { _, rawPacket ->
            Log.d(TAG, "💰 CheckParcelSales received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.PARCEL_AUCTIONS) { _, rawPacket ->
            Log.d(TAG, "🏷️ ParcelAuctions received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.PARCEL_SALES) { _, rawPacket ->
            Log.d(TAG, "💰 ParcelSales received")
        }
        
        // --- Parcel Extended Messages ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.PARCEL_BUY_PASS) { _, rawPacket ->
            Log.d(TAG, "🎫 ParcelBuyPass received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.PARCEL_CLAIM) { _, rawPacket ->
            Log.d(TAG, "🏠 ParcelClaim received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.PARCEL_DIVIDE) { _, rawPacket ->
            Log.d(TAG, "🏠 ParcelDivide received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.PARCEL_JOIN) { _, rawPacket ->
            Log.d(TAG, "🏠 ParcelJoin received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.PARCEL_RECLAIM) { _, rawPacket ->
            Log.d(TAG, "🏠 ParcelReclaim received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.PARCEL_RENAME) { _, rawPacket ->
            Log.d(TAG, "🏠 ParcelRename received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.PARCEL_SET_OTHER_CLEAN_TIME) { _, rawPacket ->
            Log.d(TAG, "🏠 ParcelSetOtherCleanTime received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.PARCEL_GOD_FORCE_OWNER) { _, rawPacket ->
            Log.d(TAG, "👑 ParcelGodForceOwner received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.PARCEL_GOD_MARK_AS_CONTENT) { _, rawPacket ->
            Log.d(TAG, "👑 ParcelGodMarkAsContent received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.MERGE_PARCEL) { _, rawPacket ->
            Log.d(TAG, "🏠 MergeParcel received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.REMOVE_PARCEL) { _, rawPacket ->
            Log.d(TAG, "🏠 RemoveParcel received")
        }
        
        // --- Land Statistics Messages ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.LAND_STAT_REQUEST) { _, rawPacket ->
            Log.d(TAG, "📊 LandStatRequest received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.LAND_STAT_REPLY) { _, rawPacket ->
            Log.d(TAG, "📊 LandStatReply received")
        }
        
        // --- Simulator Messages ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.SIMULATOR_LOAD) { _, rawPacket ->
            Log.d(TAG, "🖥️ SimulatorLoad received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.SIMULATOR_READY) { _, rawPacket ->
            Log.d(TAG, "🖥️ SimulatorReady received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.SIMULATOR_SHUTDOWN_REQUEST) { _, rawPacket ->
            Log.d(TAG, "🖥️ SimulatorShutdownRequest received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.SIMULATOR_MAP_UPDATE) { _, rawPacket ->
            Log.d(TAG, "🗺️ SimulatorMapUpdate received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.SIMULATOR_SET_MAP) { _, rawPacket ->
            Log.d(TAG, "🗺️ SimulatorSetMap received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.SIMULATOR_PRESENT_AT_LOCATION) { _, rawPacket ->
            Log.d(TAG, "🖥️ SimulatorPresentAtLocation received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.UPDATE_SIMULATOR) { _, rawPacket ->
            Log.d(TAG, "🖥️ UpdateSimulator received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.SIM_WIDE_DELETES) { _, rawPacket ->
            Log.d(TAG, "🖥️ SimWideDeletes received")
        }
        
        // --- Child Agent Messages ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.CHILD_AGENT_DYING) { _, rawPacket ->
            Log.d(TAG, "👤 ChildAgentDying received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.CHILD_AGENT_UNKNOWN) { _, rawPacket ->
            Log.d(TAG, "👤 ChildAgentUnknown received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.KILL_CHILD_AGENTS) { _, rawPacket ->
            Log.d(TAG, "👤 KillChildAgents received")
        }
        
        // --- Postcard/Email Messages ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.SEND_POSTCARD) { _, rawPacket ->
            Log.d(TAG, "📧 SendPostcard received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.EMAIL_MESSAGE_REQUEST) { _, rawPacket ->
            Log.d(TAG, "📧 EmailMessageRequest received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.EMAIL_MESSAGE_REPLY) { _, rawPacket ->
            Log.d(TAG, "📧 EmailMessageReply received")
        }
        
        // --- RPC Messages ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.RPC_CHANNEL_REQUEST) { _, rawPacket ->
            Log.d(TAG, "📡 RpcChannelRequest received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.RPC_CHANNEL_REPLY) { _, rawPacket ->
            Log.d(TAG, "📡 RpcChannelReply received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.RPC_SCRIPT_REQUEST_INBOUND) { _, rawPacket ->
            Log.d(TAG, "📡 RpcScriptRequestInbound received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.RPC_SCRIPT_REPLY_INBOUND) { _, rawPacket ->
            Log.d(TAG, "📡 RpcScriptReplyInbound received")
        }
        
        // --- Script Messages Extended ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.SCRIPT_DATA_REQUEST) { _, rawPacket ->
            Log.d(TAG, "📜 ScriptDataRequest received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.SCRIPT_DATA_REPLY) { _, rawPacket ->
            Log.d(TAG, "📜 ScriptDataReply received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.SCRIPT_MAIL_REGISTRATION) { _, rawPacket ->
            Log.d(TAG, "📜 ScriptMailRegistration received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.SCRIPT_SENSOR_REQUEST) { _, rawPacket ->
            Log.d(TAG, "📡 ScriptSensorRequest received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.SCRIPT_ANSWER_YES) { _, rawPacket ->
            Log.d(TAG, "📜 ScriptAnswerYes received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.INTERNAL_SCRIPT_MAIL) { _, rawPacket ->
            Log.d(TAG, "📜 InternalScriptMail received")
        }
        
        // --- Tracking Messages ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.TRACK_AGENT) { _, rawPacket ->
            Log.d(TAG, "📍 TrackAgent received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.FIND_AGENT_EXTENDED) { _, rawPacket ->
            Log.d(TAG, "📍 FindAgent received")
        }
        
        // --- Region Messages Extended ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.REGION_HANDLE_REQUEST) { _, rawPacket ->
            Log.d(TAG, "🌍 RegionHandleRequest received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.REGION_ID_AND_HANDLE_REPLY) { _, rawPacket ->
            Log.d(TAG, "🌍 RegionIDAndHandleReply received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.REGION_PRESENCE_REQUEST_BY_HANDLE) { _, rawPacket ->
            Log.d(TAG, "🌍 RegionPresenceRequestByHandle received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.REGION_PRESENCE_REQUEST_BY_REGION_ID) { _, rawPacket ->
            Log.d(TAG, "🌍 RegionPresenceRequestByRegionID received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.REGION_PRESENCE_RESPONSE) { _, rawPacket ->
            Log.d(TAG, "🌍 RegionPresenceResponse received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.TELEHUB_INFO) { _, rawPacket ->
            Log.d(TAG, "🚀 TelehubInfo received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.TELEPORT_LANDING_STATUS_CHANGED) { _, rawPacket ->
            Log.d(TAG, "🚀 TeleportLandingStatusChanged received")
        }
        
        // --- User Reports Messages ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.USER_REPORT) { _, rawPacket ->
            Log.d(TAG, "🚨 UserReport received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.USER_REPORT_INTERNAL) { _, rawPacket ->
            Log.d(TAG, "🚨 UserReportInternal received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.REPORT_AUTOSAVE_CRASH) { _, rawPacket ->
            Log.d(TAG, "🚨 ReportAutosaveCrash received")
        }
        
        // --- Event Messages Extended ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.EVENT_LOCATION_REQUEST) { _, rawPacket ->
            Log.d(TAG, "📅 EventLocationRequest received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.EVENT_LOCATION_REPLY) { _, rawPacket ->
            Log.d(TAG, "📅 EventLocationReply received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.EVENT_GOD_DELETE) { _, rawPacket ->
            Log.d(TAG, "👑 EventGodDelete received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.CREATE_LANDMARK_FOR_EVENT) { _, rawPacket ->
            Log.d(TAG, "📍 CreateLandmarkForEvent received")
        }
        
        // --- Directory Query Messages ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.DIR_FIND_QUERY) { _, rawPacket ->
            Log.d(TAG, "🔍 DirFindQuery received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.DIR_PLACES_QUERY) { _, rawPacket ->
            Log.d(TAG, "🔍 DirPlacesQuery received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.DIR_CLASSIFIED_QUERY) { _, rawPacket ->
            Log.d(TAG, "🔍 DirClassifiedQuery received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.DIR_LAND_QUERY) { _, rawPacket ->
            Log.d(TAG, "🔍 DirLandQuery received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.DIR_POPULAR_QUERY) { _, rawPacket ->
            Log.d(TAG, "🔍 DirPopularQuery received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.PLACES_QUERY) { _, rawPacket ->
            Log.d(TAG, "🔍 PlacesQuery received")
        }
        
        // --- Inventory Messages Extended ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.LINK_INVENTORY_ITEM) { _, rawPacket ->
            Log.d(TAG, "📦 LinkInventoryItem received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.CHANGE_INVENTORY_ITEM_FLAGS) { _, rawPacket ->
            Log.d(TAG, "📦 ChangeInventoryItemFlags received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.REQUEST_INVENTORY_ASSET) { _, rawPacket ->
            Log.d(TAG, "📦 RequestInventoryAsset received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.TRANSFER_INVENTORY) { _, rawPacket ->
            Log.d(TAG, "📦 TransferInventory received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.TRANSFER_INVENTORY_ACK) { _, rawPacket ->
            Log.d(TAG, "📦 TransferInventoryAck received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.RETRIEVE_INSTANT_MESSAGES) { _, rawPacket ->
            Log.d(TAG, "💬 RetrieveInstantMessages received")
        }
        
        // --- Group Request Messages ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.CREATE_GROUP_REQUEST) { _, rawPacket ->
            Log.d(TAG, "👥 CreateGroupRequest received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.JOIN_GROUP_REQUEST) { _, rawPacket ->
            Log.d(TAG, "👥 JoinGroupRequest received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.EJECT_GROUP_MEMBER_REQUEST) { _, rawPacket ->
            Log.d(TAG, "👥 EjectGroupMemberRequest received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.INVITE_GROUP_REQUEST) { _, rawPacket ->
            Log.d(TAG, "👥 InviteGroupRequest received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.GROUP_TITLES_REQUEST) { _, rawPacket ->
            Log.d(TAG, "👥 GroupTitlesRequest received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.GROUP_MEMBERS_REQUEST) { _, rawPacket ->
            Log.d(TAG, "👥 GroupMembersRequest received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.GROUP_ROLE_MEMBERS_REQUEST) { _, rawPacket ->
            Log.d(TAG, "👥 GroupRoleMembersRequest received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.GROUP_ROLE_DATA_REQUEST) { _, rawPacket ->
            Log.d(TAG, "👥 GroupRoleDataRequest received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.GROUP_NOTICES_LIST_REQUEST) { _, rawPacket ->
            Log.d(TAG, "👥 GroupNoticesListRequest received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.GROUP_ACTIVE_PROPOSALS_REQUEST) { _, rawPacket ->
            Log.d(TAG, "👥 GroupActiveProposalsRequest received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.GROUP_VOTE_HISTORY_REQUEST) { _, rawPacket ->
            Log.d(TAG, "🗳️ GroupVoteHistoryRequest received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.GROUP_ACCOUNT_SUMMARY_REQUEST) { _, rawPacket ->
            Log.d(TAG, "💰 GroupAccountSummaryRequest received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.GROUP_ACCOUNT_DETAILS_REQUEST) { _, rawPacket ->
            Log.d(TAG, "💰 GroupAccountDetailsRequest received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.GROUP_ACCOUNT_TRANSACTIONS_REQUEST) { _, rawPacket ->
            Log.d(TAG, "💰 GroupAccountTransactionsRequest received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.GROUP_DATA_UPDATE) { _, rawPacket ->
            Log.d(TAG, "👥 GroupDataUpdate received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.GROUP_ROLE_UPDATE) { _, rawPacket ->
            Log.d(TAG, "👥 GroupRoleUpdate received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.GROUP_ROLE_CHANGES) { _, rawPacket ->
            Log.d(TAG, "👥 GroupRoleChanges received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.GROUP_TITLE_UPDATE) { _, rawPacket ->
            Log.d(TAG, "👥 GroupTitleUpdate received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.UPDATE_GROUP_INFO) { _, rawPacket ->
            Log.d(TAG, "👥 UpdateGroupInfo received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.GROUP_PROPOSAL_BALLOT) { _, rawPacket ->
            Log.d(TAG, "🗳️ GroupProposalBallot received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.START_GROUP_PROPOSAL) { _, rawPacket ->
            Log.d(TAG, "🗳️ StartGroupProposal received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.SET_GROUP_ACCEPT_NOTICES) { _, rawPacket ->
            Log.d(TAG, "👥 SetGroupAcceptNotices received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.SET_GROUP_CONTRIBUTION) { _, rawPacket ->
            Log.d(TAG, "💰 SetGroupContribution received")
        }
        
        // --- Avatar Messages Extended ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.AVATAR_NOTES_UPDATE) { _, rawPacket ->
            Log.d(TAG, "📝 AvatarNotesUpdate received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.AVATAR_INTERESTS_UPDATE) { _, rawPacket ->
            Log.d(TAG, "👤 AvatarInterestsUpdate received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.AVATAR_PROPERTIES_UPDATE) { _, rawPacket ->
            Log.d(TAG, "👤 AvatarPropertiesUpdate received")
        }
        
        // --- Velocity Interpolation Messages ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.VELOCITY_INTERPOLATE_ON) { _, rawPacket ->
            Log.d(TAG, "⚡ VelocityInterpolateOn received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.VELOCITY_INTERPOLATE_OFF) { _, rawPacket ->
            Log.d(TAG, "⚡ VelocityInterpolateOff received")
        }
        
        // --- Object Messages Extended ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.OBJECT_INCLUDE_IN_SEARCH) { _, rawPacket ->
            Log.d(TAG, "📦 ObjectIncludeInSearch received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.OBJECT_EXPORT_SELECTED) { _, rawPacket ->
            Log.d(TAG, "📦 ObjectExportSelected received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.DEREZ_CONTAINER) { _, rawPacket ->
            Log.d(TAG, "📦 DerezContainer received")
        }
        
        // --- Test/Debug Messages ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.TEST_MESSAGE) { _, rawPacket ->
            Log.d(TAG, "🧪 TestMessage received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.NET_TEST) { _, rawPacket ->
            Log.d(TAG, "🧪 NetTest received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.STATE_SAVE) { _, rawPacket ->
            Log.d(TAG, "💾 StateSave received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.SUBSCRIBE_LOAD) { _, rawPacket ->
            Log.d(TAG, "📥 SubscribeLoad received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.UNSUBSCRIBE_LOAD) { _, rawPacket ->
            Log.d(TAG, "📤 UnsubscribeLoad received")
        }
        
        // --- Logging Messages ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.LOG_TEXT_MESSAGE) { _, rawPacket ->
            Log.d(TAG, "📝 LogTextMessage received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.LOG_DWELL_TIME) { _, rawPacket ->
            Log.d(TAG, "📝 LogDwellTime received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.LOG_FAILED_MONEY_TRANSACTION) { _, rawPacket ->
            Log.d(TAG, "📝 LogFailedMoneyTransaction received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.LOG_PARCEL_CHANGES) { _, rawPacket ->
            Log.d(TAG, "📝 LogParcelChanges received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.DATA_SERVER_LOGOUT) { _, rawPacket ->
            Log.d(TAG, "📝 DataServerLogout received")
        }
        
        // --- Parcel Request Messages ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.PARCEL_ACCESS_LIST_REQUEST) { _, rawPacket ->
            Log.d(TAG, "🏠 ParcelAccessListRequest received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.PARCEL_DWELL_REQUEST) { _, rawPacket ->
            Log.d(TAG, "🏠 ParcelDwellRequest received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.PARCEL_INFO_REQUEST) { _, rawPacket ->
            Log.d(TAG, "🏠 ParcelInfoRequest received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.PARCEL_OBJECT_OWNERS_REQUEST) { _, rawPacket ->
            Log.d(TAG, "🏠 ParcelObjectOwnersRequest received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.REQUEST_PARCEL_TRANSFER) { _, rawPacket ->
            Log.d(TAG, "🏠 RequestParcelTransfer received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.UPDATE_PARCEL) { _, rawPacket ->
            Log.d(TAG, "🏠 UpdateParcel received")
        }
        
        // --- Estate Request Messages ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.ESTATE_COVENANT_REQUEST) { _, rawPacket ->
            Log.d(TAG, "📜 EstateCovenantRequest received")
        }
        
        // --- Name/Value Messages ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.NAME_VALUE_PAIR) { _, rawPacket ->
            Log.d(TAG, "🏷️ NameValuePair received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.REMOVE_NAME_VALUE_PAIR) { _, rawPacket ->
            Log.d(TAG, "🏷️ RemoveNameValuePair received")
        }
        
        // --- CPU/System Messages ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.SET_CPU_RATIO) { _, rawPacket ->
            Log.d(TAG, "🖥️ SetCPURatio received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.SET_SIM_PRESENCE_IN_DATABASE) { _, rawPacket ->
            Log.d(TAG, "🖥️ SetSimPresenceInDatabase received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.SET_SIM_STATUS_IN_DATABASE) { _, rawPacket ->
            Log.d(TAG, "🖥️ SetSimStatusInDatabase received")
        }
        
        // =====================================
        // PHASE 5: 100 Additional Message Handlers
        // =====================================
        
        // --- Agent Movement Messages ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.AGENT_REQUEST_SIT) { _, rawPacket ->
            Log.d(TAG, "🪑 AgentRequestSit received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.AGENT_SIT) { _, rawPacket ->
            Log.d(TAG, "🪑 AgentSit received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.AGENT_HEIGHT_WIDTH) { _, rawPacket ->
            Log.d(TAG, "📏 AgentHeightWidth received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.AGENT_SET_APPEARANCE) { _, rawPacket ->
            Log.d(TAG, "👤 AgentSetAppearance received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.AGENT_QUIT_COPY) { _, rawPacket ->
            Log.d(TAG, "👤 AgentQuitCopy received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.AGENT_FOV) { _, rawPacket ->
            Log.d(TAG, "👁️ AgentFOV received")
        }
        
        // --- Object Request Messages ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.REQUEST_OBJECT_PROPERTIES_FAMILY) { _, rawPacket ->
            Log.d(TAG, "📦 RequestObjectPropertiesFamily received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.OBJECT_SELECT) { _, rawPacket ->
            Log.d(TAG, "📦 ObjectSelect received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.OBJECT_DESELECT_MSG) { _, rawPacket ->
            Log.d(TAG, "📦 ObjectDeselect received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.OBJECT_GRAB_MSG) { _, rawPacket ->
            Log.d(TAG, "✊ ObjectGrab received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.OBJECT_GRAB_UPDATE_MSG) { _, rawPacket ->
            Log.d(TAG, "✊ ObjectGrabUpdate received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.OBJECT_DE_GRAB) { _, rawPacket ->
            Log.d(TAG, "✊ ObjectDeGrab received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.OBJECT_SPIN_START_MSG) { _, rawPacket ->
            Log.d(TAG, "🔄 ObjectSpinStart received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.OBJECT_SPIN_UPDATE_MSG) { _, rawPacket ->
            Log.d(TAG, "🔄 ObjectSpinUpdate received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.OBJECT_SPIN_STOP_MSG) { _, rawPacket ->
            Log.d(TAG, "🔄 ObjectSpinStop received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.OBJECT_LINK) { _, rawPacket ->
            Log.d(TAG, "🔗 ObjectLink received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.OBJECT_DELINK) { _, rawPacket ->
            Log.d(TAG, "🔗 ObjectDelink received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.OBJECT_DESCRIPTION) { _, rawPacket ->
            Log.d(TAG, "📝 ObjectDescription received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.OBJECT_NAME) { _, rawPacket ->
            Log.d(TAG, "📝 ObjectName received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.OBJECT_CATEGORY) { _, rawPacket ->
            Log.d(TAG, "📦 ObjectCategory received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.OBJECT_EXTRA_PARAMS) { _, rawPacket ->
            Log.d(TAG, "📦 ObjectExtraParams received")
        }
        
        // --- Object Update Messages Extended ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.MULTIPLE_OBJECT_UPDATE) { _, rawPacket ->
            Log.d(TAG, "📦 MultipleObjectUpdate received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.REQUEST_MULTIPLE_OBJECTS) { _, rawPacket ->
            Log.d(TAG, "📦 RequestMultipleObjects received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.OBJECT_POSITION_MSG) { _, rawPacket ->
            Log.d(TAG, "📦 ObjectPosition received")
        }
        
        // --- Disable Simulator ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.DISABLE_SIMULATOR) { _, rawPacket ->
            Log.d(TAG, "🌍 DisableSimulator received")
        }
        
        // --- Sound Messages Extended ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.STOP_SOUND) { _, rawPacket ->
            Log.d(TAG, "🔊 StopSound received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.SOUND_PRELOAD) { _, rawPacket ->
            Log.d(TAG, "🔊 SoundPreload received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.SOUND_GAIN_CHANGE) { _, rawPacket ->
            Log.d(TAG, "🔊 SoundGainChange received")
        }
        
        // --- Animation Messages ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.AGENT_ANIMATION) { _, rawPacket ->
            Log.d(TAG, "💃 AgentAnimation received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.AGENT_REQUEST_ANIMATION) { _, rawPacket ->
            Log.d(TAG, "💃 AgentRequestAnimation received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.AVATAR_ANIMATION_DONE) { _, rawPacket ->
            Log.d(TAG, "💃 AvatarAnimationDone received")
        }
        
        // --- Gesture Messages ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.ACTIVATE_GESTURES) { _, rawPacket ->
            Log.d(TAG, "🖐️ ActivateGestures received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.DEACTIVATE_GESTURES) { _, rawPacket ->
            Log.d(TAG, "🖐️ DeactivateGestures received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.GESTURE_REQUEST) { _, rawPacket ->
            Log.d(TAG, "🖐️ GestureRequest received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.GESTURE_RESPONSE) { _, rawPacket ->
            Log.d(TAG, "🖐️ GestureResponse received")
        }
        
        // --- Appearance Messages Extended ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.REBAKE_AVATAR_TEXTURES) { _, rawPacket ->
            Log.d(TAG, "👤 RebakeAvatarTextures received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.SET_FOLLOW_CAM_PROPERTIES_MSG) { _, rawPacket ->
            Log.d(TAG, "📷 SetFollowCamProperties received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.CLEAR_FOLLOW_CAM_PROPERTIES_MSG) { _, rawPacket ->
            Log.d(TAG, "📷 ClearFollowCamProperties received")
        }
        
        // --- Attachment Messages Extended ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.OBJECT_ATTACH_RESPONSE) { _, rawPacket ->
            Log.d(TAG, "📎 ObjectAttachResponse received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.ATTACHMENT_INTO_INVENTORY) { _, rawPacket ->
            Log.d(TAG, "📎 AttachmentIntoInventory received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.ATTACH_FROM_INVENTORY) { _, rawPacket ->
            Log.d(TAG, "📎 AttachFromInventory received")
        }
        
        // --- User Data Messages ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.USER_INFO_REQ_MSG) { _, rawPacket ->
            Log.d(TAG, "👤 UserInfoReqMsg received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.USER_INFO_REPLY_MSG) { _, rawPacket ->
            Log.d(TAG, "👤 UserInfoReplyMsg received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.UPDATE_USER_INFO_MSG) { _, rawPacket ->
            Log.d(TAG, "👤 UpdateUserInfoMsg received")
        }
        
        // --- Friendship Messages Extended ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.TERMINATE_FRIENDSHIP) { _, rawPacket ->
            Log.d(TAG, "💔 TerminateFriendship received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.GRANT_USER_RIGHTS) { _, rawPacket ->
            Log.d(TAG, "🔑 GrantUserRights received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.TRACK_AGENT_SESSION) { _, rawPacket ->
            Log.d(TAG, "📍 TrackAgentSession received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.OFFER_FRIENDSHIP) { _, rawPacket ->
            Log.d(TAG, "🤝 OfferFriendship received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.FRIENDSHIP_OFFERED) { _, rawPacket ->
            Log.d(TAG, "🤝 FriendshipOffered received")
        }
        
        // --- Group Messages Extended ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.LEAVE_GROUP_REQUEST) { _, rawPacket ->
            Log.d(TAG, "👥 LeaveGroupRequest received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.ACTIVATE_GROUP) { _, rawPacket ->
            Log.d(TAG, "👥 ActivateGroup received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.GROUP_PROFILE_REQUEST) { _, rawPacket ->
            Log.d(TAG, "👥 GroupProfileRequest received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.GROUP_NOTICE_REQUEST_MSG) { _, rawPacket ->
            Log.d(TAG, "📋 GroupNoticeRequest received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.GROUP_NOTICES_RESPONSE) { _, rawPacket ->
            Log.d(TAG, "📋 GroupNoticesResponse received")
        }
        
        // --- Script Control Messages ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.SCRIPT_CONTROL_CHANGE_MSG) { _, rawPacket ->
            Log.d(TAG, "📜 ScriptControlChange received")
        }
        
        // --- Environment Messages ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.SIMULATOR_VIEWER_TIME_MESSAGE_MSG) { _, rawPacket ->
            Log.d(TAG, "🌅 SimulatorViewerTimeMessage received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.WINDLIGHT_SETTINGS_UPDATE) { _, rawPacket ->
            Log.d(TAG, "🌤️ WindLightSettingsUpdate received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.PARCEL_ENVIRONMENT_BLOCK) { _, rawPacket ->
            Log.d(TAG, "🌍 ParcelEnvironmentBlock received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.SET_ENVIRONMENT) { _, rawPacket ->
            Log.d(TAG, "🌍 SetEnvironment received")
        }
        
        // --- Notecard/Script Edit Messages ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.UPDATE_TASK_INVENTORY_NOTECARD_ITEM) { _, rawPacket ->
            Log.d(TAG, "📝 UpdateTaskInventoryNotecardItem received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.UPDATE_NOTECARD_AGENT_INVENTORY) { _, rawPacket ->
            Log.d(TAG, "📝 UpdateNotecardAgentInventory received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.UPDATE_GESTURE_AGENT_INVENTORY) { _, rawPacket ->
            Log.d(TAG, "🖐️ UpdateGestureAgentInventory received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.UPDATE_SCRIPT_AGENT) { _, rawPacket ->
            Log.d(TAG, "📜 UpdateScriptAgent received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.UPDATE_SCRIPT_TASK) { _, rawPacket ->
            Log.d(TAG, "📜 UpdateScriptTask received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.SCRIPT_SENSOR_REMOVE) { _, rawPacket ->
            Log.d(TAG, "📡 ScriptSensorRemove received")
        }
        
        // --- Autopilot Messages ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.AUTOPILOT) { _, rawPacket ->
            Log.d(TAG, "🚗 Autopilot received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.AUTOPILOT_CANCEL) { _, rawPacket ->
            Log.d(TAG, "🚗 AutopilotCancel received")
        }
        
        // --- Terrain Messages ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.TERRAIN_HEIGHT_DATA) { _, rawPacket ->
            Log.d(TAG, "🏔️ TerrainHeightData received")
        }
        
        // --- God Mode Messages ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.GOD_KICK_USER) { _, rawPacket ->
            Log.d(TAG, "👑 GodKickUser received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.GODLIKE_MESSAGE) { _, rawPacket ->
            Log.d(TAG, "👑 GodlikeMessage received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.GOD_UPDATE_REGION_INFO) { _, rawPacket ->
            Log.d(TAG, "👑 GodUpdateRegionInfo received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.GOD_DELETE_SIM) { _, rawPacket ->
            Log.d(TAG, "👑 GodDeleteSim received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.REQUEST_GODLIKE_POWERS) { _, rawPacket ->
            Log.d(TAG, "👑 RequestGodlikePowers received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.GRANT_GODLIKE_POWERS) { _, rawPacket ->
            Log.d(TAG, "👑 GrantGodlikePowers received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.SIM_OWNER_REQUEST) { _, rawPacket ->
            Log.d(TAG, "🏠 SimOwnerRequest received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.SIM_OWNER_RESPONSE) { _, rawPacket ->
            Log.d(TAG, "🏠 SimOwnerResponse received")
        }
        
        // --- Estate Manager Messages ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.ESTATE_OWNER_MESSAGE) { _, rawPacket ->
            Log.d(TAG, "🏰 EstateOwnerMessage received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.ESTATE_CHANGE_INFO) { _, rawPacket ->
            Log.d(TAG, "🏰 EstateChangeInfo received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.ESTATE_EXPERIENCE_REPLY) { _, rawPacket ->
            Log.d(TAG, "🏰 EstateExperienceReply received")
        }
        
        // --- Land Bank Messages ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.LAND_BUY) { _, rawPacket ->
            Log.d(TAG, "🏠 LandBuy received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.LAND_BUY_PASS) { _, rawPacket ->
            Log.d(TAG, "🏠 LandBuyPass received")
        }
        
        // --- Asset/Transfer Messages Extended ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.ASSET_INFO_REQUEST) { _, rawPacket ->
            Log.d(TAG, "📁 AssetInfoRequest received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.ASSET_INFO_RESPONSE) { _, rawPacket ->
            Log.d(TAG, "📁 AssetInfoResponse received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.MAP_LAYER_REQUEST_MSG) { _, rawPacket ->
            Log.d(TAG, "🗺️ MapLayerRequest received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.MAP_LAYER_REPLY_MSG) { _, rawPacket ->
            Log.d(TAG, "🗺️ MapLayerReply received")
        }
        
        // --- Agent Data Messages ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.AGENT_DATA_UPDATE_REQUEST) { _, rawPacket ->
            Log.d(TAG, "👤 AgentDataUpdateRequest received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.AGENT_DATA_UPDATE_MSG) { _, rawPacket ->
            Log.d(TAG, "👤 AgentDataUpdate received")
        }
        
        // --- Pick/Classified Messages Extended ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.PICK_DELETE) { _, rawPacket ->
            Log.d(TAG, "📍 PickDelete received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.PICK_UPDATE_INFO) { _, rawPacket ->
            Log.d(TAG, "📍 PickUpdateInfo received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.CLASSIFIED_DELETE) { _, rawPacket ->
            Log.d(TAG, "📰 ClassifiedDelete received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.CLASSIFIED_INFO_UPDATE) { _, rawPacket ->
            Log.d(TAG, "📰 ClassifiedInfoUpdate received")
        }
        
        // --- Interest List Messages ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.INTEREST_LIST_REQUEST) { _, rawPacket ->
            Log.d(TAG, "📋 InterestListRequest received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.INTEREST_LIST_REPLY) { _, rawPacket ->
            Log.d(TAG, "📋 InterestListReply received")
        }
        
        // --- Object Export Messages ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.EXPORT_DYNA_FILE) { _, rawPacket ->
            Log.d(TAG, "📤 ExportDynaFile received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.EXPORT_DYNA_FILE_REQUEST) { _, rawPacket ->
            Log.d(TAG, "📤 ExportDynaFileRequest received")
        }
        
        // --- Upload Messages ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.UPLOAD_BAKED_TEXTURE) { _, rawPacket ->
            Log.d(TAG, "📤 UploadBakedTexture received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.UPLOAD_BAKED_TEXTURE_RESULT) { _, rawPacket ->
            Log.d(TAG, "📤 UploadBakedTextureResult received")
        }
        
        // --- Object Permission Messages ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.OBJECT_PERMISSIONS_REQUEST) { _, rawPacket ->
            Log.d(TAG, "🔐 ObjectPermissionsRequest received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.OBJECT_PERMISSIONS_REPLY) { _, rawPacket ->
            Log.d(TAG, "🔐 ObjectPermissionsReply received")
        }
        
        // --- Agent Camera Messages ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.AGENT_CAMERA_CONSTRAINT) { _, rawPacket ->
            Log.d(TAG, "📷 AgentCameraConstraint received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.CAMERA_CONSTRAINT_MSG) { _, rawPacket ->
            Log.d(TAG, "📷 CameraConstraintMsg received")
        }
        
        // --- Voice Messages ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.PROVISION_VOICE_ACCOUNT_REQUEST) { _, rawPacket ->
            Log.d(TAG, "🎤 ProvisionVoiceAccountRequest received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.PROVISION_VOICE_ACCOUNT_REPLY) { _, rawPacket ->
            Log.d(TAG, "🎤 ProvisionVoiceAccountReply received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.PARCEL_VOICE_INFO_REQUEST) { _, rawPacket ->
            Log.d(TAG, "🎤 ParcelVoiceInfoRequest received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.PARCEL_VOICE_INFO_REPLY) { _, rawPacket ->
            Log.d(TAG, "🎤 ParcelVoiceInfoReply received")
        }
        
        // --- Experience Messages ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.EXPERIENCE_INFO_REQUEST) { _, rawPacket ->
            Log.d(TAG, "✨ ExperienceInfoRequest received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.EXPERIENCE_INFO_REPLY) { _, rawPacket ->
            Log.d(TAG, "✨ ExperienceInfoReply received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.EXPERIENCE_PERMISSION_REQUEST) { _, rawPacket ->
            Log.d(TAG, "✨ ExperiencePermissionRequest received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.EXPERIENCE_PERMISSION_REPLY) { _, rawPacket ->
            Log.d(TAG, "✨ ExperiencePermissionReply received")
        }
        
        // --- Region Object Messages ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.REGION_OBJECT_UPDATE) { _, rawPacket ->
            Log.d(TAG, "🌍 RegionObjectUpdate received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.REGION_OBJECT_COMPLETE) { _, rawPacket ->
            Log.d(TAG, "🌍 RegionObjectComplete received")
        }
        
        // --- Pathfinding Messages ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.NAV_MESH_STATUS_REQUEST) { _, rawPacket ->
            Log.d(TAG, "🧭 NavMeshStatusRequest received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.NAV_MESH_STATUS_REPLY) { _, rawPacket ->
            Log.d(TAG, "🧭 NavMeshStatusReply received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.CHARACTER_PROPERTIES_REQUEST) { _, rawPacket ->
            Log.d(TAG, "🧭 CharacterPropertiesRequest received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.CHARACTER_PROPERTIES_REPLY) { _, rawPacket ->
            Log.d(TAG, "🧭 CharacterPropertiesReply received")
        }
        
        // --- AO (Animation Override) Messages ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.AGENT_ANIMATION_OVERRIDE) { _, rawPacket ->
            Log.d(TAG, "💃 AgentAnimationOverride received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.CLEAR_ANIMATION_OVERRIDE) { _, rawPacket ->
            Log.d(TAG, "💃 ClearAnimationOverride received")
        }
        
        // =====================================
        // Phase 6: ALL REMAINING HANDLERS
        // =====================================
        
        // --- Agent Extended ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.AGENT_THROTTLE) { _, rawPacket ->
            Log.d(TAG, "🎛️ AgentThrottle received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.AGENT_IS_NOW_WEARING) { _, rawPacket ->
            Log.d(TAG, "👗 AgentIsNowWearing received")
        }
        
        // --- Avatar Request/Backend ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.AVATAR_PICKER_REQUEST) { _, rawPacket ->
            Log.d(TAG, "🔍 AvatarPickerRequest received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.AVATAR_PROPERTIES_REQUEST) { _, rawPacket ->
            Log.d(TAG, "📋 AvatarPropertiesRequest received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.AVATAR_TEXTURE_UPDATE) { _, rawPacket ->
            Log.d(TAG, "🖼️ AvatarTextureUpdate received")
        }
        
        // --- Buy/Economy ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.BUY_OBJECT_INVENTORY) { _, rawPacket ->
            Log.d(TAG, "🛒 BuyObjectInventory received")
        }
        
        // --- Chat Extended ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.CHAT_EVENT) { _, rawPacket ->
            Log.d(TAG, "💬 ChatEvent received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.CHAT_FROM_VIEWER) { _, rawPacket ->
            Log.d(TAG, "💬 ChatFromViewer received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.CHAT_PASS) { _, rawPacket ->
            Log.d(TAG, "💬 ChatPass received")
        }
        
        // --- Circuit Extended ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.CIRCUIT_READY) { _, rawPacket ->
            Log.d(TAG, "⚡ CircuitReady received")
        }
        
        // --- Classified Extended ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.CLASSIFIED_GOD_DELETE) { _, rawPacket ->
            Log.d(TAG, "📰 ClassifiedGodDelete received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.CLASSIFIED_INFO_REQUEST) { _, rawPacket ->
            Log.d(TAG, "📰 ClassifiedInfoRequest received")
        }
        
        // --- Agent Movement Extended ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.COMPLETE_AGENT_MOVEMENT) { _, rawPacket ->
            Log.d(TAG, "🚶 CompleteAgentMovement received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.COMPLETE_PING_CHECK) { _, rawPacket ->
            Log.d(TAG, "📡 CompletePingCheck received")
        }
        
        // --- Inventory Copy ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.COPY_INVENTORY_FROM_NOTECARD) { _, rawPacket ->
            Log.d(TAG, "📄 CopyInventoryFromNotecard received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.COPY_INVENTORY_ITEM) { _, rawPacket ->
            Log.d(TAG, "📁 CopyInventoryItem received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.CREATE_INVENTORY_FOLDER) { _, rawPacket ->
            Log.d(TAG, "📁 CreateInventoryFolder received")
        }
        
        // --- Data Home Location ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.DATA_HOME_LOCATION_REQUEST) { _, rawPacket ->
            Log.d(TAG, "🏠 DataHomeLocationRequest received")
        }
        
        // --- Economy Request ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.ECONOMY_DATA_REQUEST) { _, rawPacket ->
            Log.d(TAG, "💰 EconomyDataRequest received")
        }
        
        // --- User Management ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.EJECT_USER) { _, rawPacket ->
            Log.d(TAG, "🚪 EjectUser received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.FREEZE_USER) { _, rawPacket ->
            Log.d(TAG, "🥶 FreezeUser received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.KICK_USER_ACK) { _, rawPacket ->
            Log.d(TAG, "🚪 KickUserAck received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.SYSTEM_KICK_USER) { _, rawPacket ->
            Log.d(TAG, "🚪 SystemKickUser received")
        }
        
        // --- Event Extended ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.EVENT_INFO_REQUEST) { _, rawPacket ->
            Log.d(TAG, "📅 EventInfoRequest received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.EVENT_NOTIFICATION_ADD_REQUEST) { _, rawPacket ->
            Log.d(TAG, "📅 EventNotificationAddRequest received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.EVENT_NOTIFICATION_REMOVE_REQUEST) { _, rawPacket ->
            Log.d(TAG, "📅 EventNotificationRemoveRequest received")
        }
        
        // --- Script Extended ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.GET_SCRIPT_RUNNING) { _, rawPacket ->
            Log.d(TAG, "📜 GetScriptRunning received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.SCRIPT_DIALOG_REPLY) { _, rawPacket ->
            Log.d(TAG, "📜 ScriptDialogReply received")
        }
        
        // --- Global Options ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.GLOBAL_OPTIONS_CHANGE) { _, rawPacket ->
            Log.d(TAG, "⚙️ GlobalOptionsChange received")
        }
        
        // --- IM ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.IMPROVED_INSTANT_MESSAGE) { _, rawPacket ->
            Log.d(TAG, "💌 ImprovedInstantMessage received - processing via IM manager")
            val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
            if (payload != null) {
                val imData = com.linkpoint.protocol.messages.MessageParser.parseImprovedInstantMessage(payload)
                if (imData != null) {
                    imManager.handleIncomingIM(
                        fromAgentId = imData.fromAgentId,
                        fromName = imData.fromAgentName,
                        message = imData.message,
                        sessionId = imData.sessionId,
                        dialogType = imData.dialog,
                        timestamp = imData.timestamp
                    )
                } else {
                    Log.e(TAG, "Failed to parse ImprovedInstantMessage")
                }
            }
        }
        
        // --- Live Help ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.LIVE_HELP_GROUP_REPLY) { _, rawPacket ->
            Log.d(TAG, "❓ LiveHelpGroupReply received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.LIVE_HELP_GROUP_REQUEST) { _, rawPacket ->
            Log.d(TAG, "❓ LiveHelpGroupRequest received")
        }
        
        // --- Logout ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.LOGOUT_REQUEST) { _, rawPacket ->
            Log.d(TAG, "🚪 LogoutRequest received")
        }
        
        // --- Money Extended ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.MONEY_BALANCE_REQUEST) { _, rawPacket ->
            Log.d(TAG, "💰 MoneyBalanceRequest received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.MONEY_TRANSFER_BACKEND) { _, rawPacket ->
            Log.d(TAG, "💰 MoneyTransferBackend received")
        }
        
        // --- Inventory Move ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.MOVE_TASK_INVENTORY) { _, rawPacket ->
            Log.d(TAG, "📦 MoveTaskInventory received")
        }
        
        // --- Landing Region ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.NEAREST_LANDING_REGION_REPLY) { _, rawPacket ->
            Log.d(TAG, "🛬 NearestLandingRegionReply received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.NEAREST_LANDING_REGION_REQUEST) { _, rawPacket ->
            Log.d(TAG, "🛬 NearestLandingRegionRequest received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.NEAREST_LANDING_REGION_UPDATED) { _, rawPacket ->
            Log.d(TAG, "🛬 NearestLandingRegionUpdated received")
        }
        
        // --- Object Extended ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.OBJECT_DELETE) { _, rawPacket ->
            Log.d(TAG, "🗑️ ObjectDelete received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.OBJECT_DUPLICATE_ON_RAY) { _, rawPacket ->
            Log.d(TAG, "📋 ObjectDuplicateOnRay received")
        }
        
        // --- Parcel Extended ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.PARCEL_ACCESS_LIST_UPDATE) { _, rawPacket ->
            Log.d(TAG, "🏘️ ParcelAccessListUpdate received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.PARCEL_DEED_TO_GROUP) { _, rawPacket ->
            Log.d(TAG, "🏘️ ParcelDeedToGroup received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.PARCEL_PROPERTIES_UPDATE) { _, rawPacket ->
            Log.d(TAG, "🏘️ ParcelPropertiesUpdate received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.PARCEL_RELEASE) { _, rawPacket ->
            Log.d(TAG, "🏘️ ParcelRelease received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.PARCEL_RETURN_OBJECTS) { _, rawPacket ->
            Log.d(TAG, "🏘️ ParcelReturnObjects received")
        }
        
        // --- Pick Extended ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.PICK_GOD_DELETE) { _, rawPacket ->
            Log.d(TAG, "📌 PickGodDelete received")
        }
        
        // --- Inventory Purge ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.PURGE_INVENTORY_DESCENDENTS) { _, rawPacket ->
            Log.d(TAG, "📁 PurgeInventoryDescendents received")
        }
        
        // --- Region Handshake ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.REGION_HANDSHAKE_REPLY) { _, rawPacket ->
            Log.d(TAG, "🌍 RegionHandshakeReply received")
        }
        
        // --- Inventory Remove ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.REMOVE_INVENTORY_OBJECTS) { _, rawPacket ->
            Log.d(TAG, "📁 RemoveInventoryObjects received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.REMOVE_TASK_INVENTORY) { _, rawPacket ->
            Log.d(TAG, "📦 RemoveTaskInventory received")
        }
        
        // --- Pay Price ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.REQUEST_PAY_PRICE) { _, rawPacket ->
            Log.d(TAG, "💰 RequestPayPrice received")
        }
        
        // --- Rez Extended ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.REZ_SINGLE_ATTACHMENT_FROM_INV) { _, rawPacket ->
            Log.d(TAG, "📎 RezSingleAttachmentFromInv received")
        }
        
        // --- Start Location ---
        // Note: SET_START_LOCATION message ID doesn't exist, only SET_START_LOCATION_REQUEST
        // udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.SET_START_LOCATION) { _, rawPacket ->
        //     Log.d(TAG, "🏠 SetStartLocation received")
        // }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.SET_START_LOCATION_REQUEST) { _, rawPacket ->
            Log.d(TAG, "🏠 SetStartLocationRequest received")
        }
        
        // --- Teleport Lure ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.START_LURE) { _, rawPacket ->
            Log.d(TAG, "🌀 StartLure received")
        }
        
        // --- Voting ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.TALLY_VOTES) { _, rawPacket ->
            Log.d(TAG, "🗳️ TallyVotes received")
        }
        
        // --- Teleport Extended ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.TELEPORT_LANDMARK_REQUEST) { _, rawPacket ->
            Log.d(TAG, "🌀 TeleportLandmarkRequest received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.TELEPORT_LOCATION_REQUEST) { _, rawPacket ->
            Log.d(TAG, "🌀 TeleportLocationRequest received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.TELEPORT_LURE_REQUEST) { _, rawPacket ->
            Log.d(TAG, "🌀 TeleportLureRequest received")
        }
        
        // --- Inventory Update ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.UPDATE_INVENTORY_ITEM) { _, rawPacket ->
            Log.d(TAG, "📁 UpdateInventoryItem received")
        }
        
        // --- Circuit Code ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.USE_CIRCUIT_CODE) { _, rawPacket ->
            Log.d(TAG, "⚡ UseCircuitCode received")
        }
        
        // --- UUID Request ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.UUID_NAME_REQUEST) { _, rawPacket ->
            Log.d(TAG, "🔍 UUIDNameRequest received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.UUID_GROUP_NAME_REQUEST) { _, rawPacket ->
            Log.d(TAG, "🔍 UUIDGroupNameRequest received")
        }
        
        // Mark handlers as ready and process any buffered packets
        // This is critical for handling packets that arrived before handlers were registered
        udpConnection.setHandlersReady()
        
        Log.i(TAG, "╔══════════════════════════════════════════════════════════════════")
        Log.i(TAG, "║ UDP MESSAGE HANDLERS REGISTERED: ${udpConnection.getRegisteredHandlerCount()}")
        Log.i(TAG, "║ Handlers: ${udpConnection.getRegisteredHandlerIds().joinToString(", ")}")
        Log.i(TAG, "╚══════════════════════════════════════════════════════════════════")
    }
    
    override fun onTerminate() {
        super.onTerminate()
        Log.i(TAG, "Linkpoint application terminating")
        
        // Stop background connection service
        LinkpointConnectionService.stop(this)
        
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
        
        // Shutdown new managers
        if (::economyManager.isInitialized) economyManager.shutdown()
        if (::scriptDialogManager.isInitialized) scriptDialogManager.shutdown()
        if (::muteManager.isInitialized) muteManager.shutdown()
        if (::taskInventoryManager.isInitialized) taskInventoryManager.shutdown()
        if (::notecardManager.isInitialized) notecardManager.shutdown()
        if (::transferManager.isInitialized) transferManager.shutdown()
        if (::xferManager.isInitialized) xferManager.shutdown()
        if (::displayNameManager.isInitialized) displayNameManager.shutdown()
        if (::environmentManager.isInitialized) environmentManager.shutdown()
        
        // Shutdown additional new managers
        if (::rlvController.isInitialized) rlvController.shutdown()
        if (::userProfileManager.isInitialized) userProfileManager.shutdown()
        if (::minimapManager.isInitialized) minimapManager.shutdown()
        if (::avatarBakingSystem.isInitialized) avatarBakingSystem.shutdown()
        if (::connectionKeepAlive.isInitialized) connectionKeepAlive.shutdown()
        if (::idleHandler.isInitialized) idleHandler.shutdown()
        
        // Shutdown latest new managers
        if (::landmarkManager.isInitialized) landmarkManager.shutdown()
        if (::mediaManager.isInitialized) mediaManager.shutdown()
        if (::estateManager.isInitialized) estateManager.shutdown()
        if (::snapshotManager.isInitialized) snapshotManager.shutdown()
        if (::scriptManager.isInitialized) scriptManager.shutdown()
        if (::sitManager.isInitialized) sitManager.shutdown()
        if (::animationController.isInitialized) animationController.shutdown()
        
        capabilityManager.shutdown()
        
        // Reset connection state tracking
        completeAgentMovementSent.set(false)
        
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
     * Check if friends manager is initialized (for debug reports)
     */
    fun isFriendsManagerInitialized(): Boolean = ::friendsManager.isInitialized
    
    /**
     * Check if display name manager is initialized (for login data parsing)
     */
    fun isDisplayNameManagerInitialized(): Boolean = ::displayNameManager.isInitialized
    
    /**
     * Check if profile manager is initialized (for login data parsing)
     */
    fun isProfileManagerInitialized(): Boolean = ::profileManager.isInitialized
    
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
    
    // ==================== GENERIC MESSAGE HANDLING ====================
    
    /**
     * Handle GenericMessage method calls from simulator.
     * GenericMessage is used for various RPC-style calls from LSL scripts.
     */
    private fun handleGenericMessage(method: String, invoice: UUID, params: List<ByteArray>) {
        Log.d(TAG, "GenericMessage: method=$method, params=${params.size}")
        
        when (method) {
            "teleporthomerequest" -> {
                Log.i(TAG, "Teleport home request received")
            }
            "godpowers" -> {
                Log.i(TAG, "God powers message received")
            }
            "experience" -> {
                Log.i(TAG, "Experience message received")
            }
            "maturity" -> {
                if (params.isNotEmpty()) {
                    val maturity = String(params[0], Charsets.UTF_8)
                    Log.i(TAG, "Maturity rating: $maturity")
                }
            }
            else -> {
                Log.d(TAG, "Unknown GenericMessage method: $method")
            }
        }
    }
    
    // ==================== SESSION RECORDING ====================
    
    /**
     * Start recording a full session log including all packets.
     * Call this when detailed diagnostic logging is needed from app startup to close.
     * 
     * @return true if recording started, false if already recording or failed
     */
    fun startSessionRecording(): Boolean {
        return com.linkpoint.utils.SessionLogRecorder.startRecording()
    }
    
    /**
     * Stop session recording and get the log file.
     * 
     * @return The log file, or null if not recording
     */
    fun stopSessionRecording(): java.io.File? {
        return com.linkpoint.utils.SessionLogRecorder.stopRecording()
    }
    
    /**
     * Check if session recording is active.
     */
    fun isSessionRecordingActive(): Boolean {
        return com.linkpoint.utils.SessionLogRecorder.isRecording()
    }
    
    /**
     * Get session recording statistics.
     */
    fun getSessionRecordingStats(): com.linkpoint.utils.SessionLogRecorder.RecordingStats {
        return com.linkpoint.utils.SessionLogRecorder.getStats()
    }
    
    /**
     * Get the path where session logs are stored.
     * Returns the public Downloads/Lumiya Logs path.
     */
    fun getSessionLogDirectoryPath(): String {
        return com.linkpoint.utils.SessionLogRecorder.getLogDirectoryPath()
    }
}
