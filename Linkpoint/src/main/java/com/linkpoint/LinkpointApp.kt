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
                    
                    // TODO: Forward to script/control manager when implemented
                    // For now, just acknowledge the message - scripts can take controls
                    // The controls bitmask indicates which keys/mouse the script is capturing
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
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.PARCEL_PROPERTIES) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload == null) return@registerHandler
                
                // ParcelProperties is complex - for now just log that we received it
                Log.d(TAG, "🗺️ ParcelProperties received (${payload.size} bytes)")
                
                // Forward to parcel manager when full parsing is implemented
                if (::parcelManager.isInitialized) {
                    // parcelManager.handleParcelProperties(payload)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error handling ParcelProperties", e)
            }
        }
        
        // =====================================
        // PHASE 2: 50 Additional Message Handlers
        // =====================================
        
        // --- Script/Dialog Messages ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.SCRIPT_DIALOG) { _, rawPacket ->
            Log.d(TAG, "📋 ScriptDialog received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.SCRIPT_QUESTION) { _, rawPacket ->
            Log.d(TAG, "❓ ScriptQuestion (permission request) received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.LOAD_URL) { _, rawPacket ->
            Log.d(TAG, "🔗 LoadURL request received")
        }
        
        // --- Economy Messages ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.MONEY_BALANCE_REPLY) { _, rawPacket ->
            Log.d(TAG, "💰 MoneyBalanceReply received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.ECONOMY_DATA) { _, rawPacket ->
            Log.d(TAG, "📊 EconomyData received")
        }
        
        // --- Inventory Messages ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.INVENTORY_DESCENDENTS) { _, rawPacket ->
            Log.d(TAG, "📦 InventoryDescendents received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.FETCH_INVENTORY_REPLY) { _, rawPacket ->
            Log.d(TAG, "📦 FetchInventoryReply received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.BULK_UPDATE_INVENTORY) { _, rawPacket ->
            Log.d(TAG, "📦 BulkUpdateInventory received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.UPDATE_CREATE_INVENTORY_ITEM) { _, rawPacket ->
            Log.d(TAG, "📦 UpdateCreateInventoryItem received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.REMOVE_INVENTORY_ITEM) { _, rawPacket ->
            Log.d(TAG, "📦 RemoveInventoryItem received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.REMOVE_INVENTORY_FOLDER) { _, rawPacket ->
            Log.d(TAG, "📦 RemoveInventoryFolder received")
        }
        
        // --- Avatar/Appearance Messages ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.AVATAR_APPEARANCE) { _, rawPacket ->
            Log.d(TAG, "👤 AvatarAppearance received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.AGENT_WEARABLES_UPDATE) { _, rawPacket ->
            Log.d(TAG, "👔 AgentWearablesUpdate received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.AGENT_CACHED_TEXTURE) { _, rawPacket ->
            Log.d(TAG, "🖼️ AgentCachedTexture received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.AGENT_CACHED_TEXTURE_RESPONSE) { _, rawPacket ->
            Log.d(TAG, "🖼️ AgentCachedTextureResponse received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.AVATAR_PROPERTIES_REPLY) { _, rawPacket ->
            Log.d(TAG, "👤 AvatarPropertiesReply received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.AVATAR_INTERESTS_REPLY) { _, rawPacket ->
            Log.d(TAG, "👤 AvatarInterestsReply received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.AVATAR_GROUPS_REPLY) { _, rawPacket ->
            Log.d(TAG, "👤 AvatarGroupsReply received")
        }
        
        // --- Group Messages ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.GROUP_PROFILE_REPLY) { _, rawPacket ->
            Log.d(TAG, "👥 GroupProfileReply received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.GROUP_MEMBERS_REPLY) { _, rawPacket ->
            Log.d(TAG, "👥 GroupMembersReply received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.GROUP_ROLE_DATA_REPLY) { _, rawPacket ->
            Log.d(TAG, "👥 GroupRoleDataReply received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.GROUP_TITLES_REPLY) { _, rawPacket ->
            Log.d(TAG, "👥 GroupTitlesReply received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.GROUP_NOTICE_ADD) { _, rawPacket ->
            Log.d(TAG, "📢 GroupNoticeAdd received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.AGENT_GROUP_DATA_UPDATE) { _, rawPacket ->
            Log.d(TAG, "👥 AgentGroupDataUpdate received")
        }
        
        // --- Friends Messages ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.ACCEPT_FRIENDSHIP) { _, rawPacket ->
            Log.d(TAG, "🤝 AcceptFriendship received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.DECLINE_FRIENDSHIP) { _, rawPacket ->
            Log.d(TAG, "🚫 DeclineFriendship received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.FORM_FRIENDSHIP) { _, rawPacket ->
            Log.d(TAG, "🤝 FormFriendship received")
        }
        
        // --- Map Messages ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.MAP_BLOCK_REPLY) { _, rawPacket ->
            Log.d(TAG, "🗺️ MapBlockReply received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.MAP_ITEM_REPLY) { _, rawPacket ->
            Log.d(TAG, "🗺️ MapItemReply received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.MAP_LAYER_REPLY) { _, rawPacket ->
            Log.d(TAG, "🗺️ MapLayerReply received")
        }
        
        // --- Search Messages ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.DIR_PLACES_REPLY) { _, rawPacket ->
            Log.d(TAG, "🔍 DirPlacesReply received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.DIR_PEOPLE_REPLY) { _, rawPacket ->
            Log.d(TAG, "🔍 DirPeopleReply received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.DIR_GROUPS_REPLY) { _, rawPacket ->
            Log.d(TAG, "🔍 DirGroupsReply received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.DIR_EVENTS_REPLY) { _, rawPacket ->
            Log.d(TAG, "🔍 DirEventsReply received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.DIR_LAND_REPLY) { _, rawPacket ->
            Log.d(TAG, "🔍 DirLandReply received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.DIR_CLASSIFIED_REPLY) { _, rawPacket ->
            Log.d(TAG, "🔍 DirClassifiedReply received")
        }
        
        // --- Region/Estate Messages ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.REGION_INFO) { _, rawPacket ->
            Log.d(TAG, "🌍 RegionInfo received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.SIM_STATS) { _, rawPacket ->
            Log.d(TAG, "📈 SimStats received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.ESTATE_COVENANT_REPLY) { _, rawPacket ->
            Log.d(TAG, "📜 EstateCovenantReply received")
        }
        
        // --- Parcel Messages ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.PARCEL_INFO_REPLY) { _, rawPacket ->
            Log.d(TAG, "🏠 ParcelInfoReply received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.PARCEL_ACCESS_LIST_REPLY) { _, rawPacket ->
            Log.d(TAG, "🏠 ParcelAccessListReply received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.PARCEL_DWELL_REPLY) { _, rawPacket ->
            Log.d(TAG, "🏠 ParcelDwellReply received")
        }
        
        // --- Object Messages ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.OBJECT_PROPERTIES_FAMILY) { _, rawPacket ->
            Log.d(TAG, "📦 ObjectPropertiesFamily received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.OBJECT_ADD) { _, rawPacket ->
            Log.d(TAG, "📦 ObjectAdd received")
        }
        
        // --- Sound Messages ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.ATTACHED_SOUND) { _, rawPacket ->
            Log.d(TAG, "🔊 AttachedSound received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.ATTACHED_SOUND_GAIN_CHANGE) { _, rawPacket ->
            Log.d(TAG, "🔊 AttachedSoundGainChange received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.PRELOAD_SOUND) { _, rawPacket ->
            Log.d(TAG, "🔊 PreloadSound received")
        }
        
        // --- Effect Messages ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.VIEWER_EFFECT) { _, rawPacket ->
            Log.d(TAG, "✨ ViewerEffect received")
        }
        
        // --- Transfer/Asset Messages ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.TRANSFER_INFO) { _, rawPacket ->
            Log.d(TAG, "📥 TransferInfo received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.TRANSFER_PACKET) { _, rawPacket ->
            Log.d(TAG, "📥 TransferPacket received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.ABORT_XFER) { _, rawPacket ->
            Log.d(TAG, "❌ AbortXfer received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.IMAGE_NOT_IN_DATABASE) { _, rawPacket ->
            Log.d(TAG, "🖼️ ImageNotInDatabase received")
        }
        
        // --- Misc Messages ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.MEAN_COLLISION_ALERT) { _, rawPacket ->
            Log.d(TAG, "💥 MeanCollisionAlert received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.AVATAR_SIT_RESPONSE) { _, rawPacket ->
            Log.d(TAG, "🪑 AvatarSitResponse received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.CAMERA_CONSTRAINT) { _, rawPacket ->
            Log.d(TAG, "📷 CameraConstraint received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.CONFIRM_ENABLE_SIMULATOR) { _, rawPacket ->
            Log.d(TAG, "🌐 ConfirmEnableSimulator received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.SIM_STATUS) { _, rawPacket ->
            Log.d(TAG, "📊 SimStatus received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.LOGOUT_REPLY) { _, rawPacket ->
            Log.d(TAG, "👋 LogoutReply received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.UUID_NAME_REPLY) { _, rawPacket ->
            Log.d(TAG, "🏷️ UUIDNameReply received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.UUID_GROUP_NAME_REPLY) { _, rawPacket ->
            Log.d(TAG, "🏷️ UUIDGroupNameReply received")
        }
        
        // =====================================
        // PHASE 3: 100 Additional Message Handlers
        // =====================================
        
        // --- High Frequency Messages ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.NEIGHBOR_LIST) { _, rawPacket ->
            Log.d(TAG, "🌐 NeighborList received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.REQUEST_IMAGE) { _, rawPacket ->
            Log.d(TAG, "🖼️ RequestImage received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.IMAGE_DATA) { _, rawPacket ->
            Log.d(TAG, "🖼️ ImageData received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.IMAGE_PACKET) { _, rawPacket ->
            Log.d(TAG, "🖼️ ImagePacket received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.EDGE_DATA_PACKET) { _, rawPacket ->
            Log.d(TAG, "🌐 EdgeDataPacket received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.CHILD_AGENT_UPDATE) { _, rawPacket ->
            Log.d(TAG, "👤 ChildAgentUpdate received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.CHILD_AGENT_ALIVE) { _, rawPacket ->
            Log.d(TAG, "👤 ChildAgentAlive received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.CHILD_AGENT_POSITION_UPDATE) { _, rawPacket ->
            Log.d(TAG, "👤 ChildAgentPositionUpdate received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.ATOMIC_PASS_OBJECT) { _, rawPacket ->
            Log.d(TAG, "📦 AtomicPassObject received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.SEND_XFER_PACKET) { _, rawPacket ->
            Log.d(TAG, "📥 SendXferPacket received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.CONFIRM_XFER_PACKET) { _, rawPacket ->
            Log.d(TAG, "📥 ConfirmXferPacket received")
        }
        
        // --- Agent Messages ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.AGENT_PAUSE) { _, rawPacket ->
            Log.d(TAG, "⏸️ AgentPause received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.AGENT_RESUME) { _, rawPacket ->
            Log.d(TAG, "▶️ AgentResume received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.AGENT_DROP_GROUP) { _, rawPacket ->
            Log.d(TAG, "👥 AgentDropGroup received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.AGENT_WEARABLES_REQUEST) { _, rawPacket ->
            Log.d(TAG, "👔 AgentWearablesRequest received")
        }
        
        // --- Avatar Messages ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.AVATAR_PICKER_REPLY) { _, rawPacket ->
            Log.d(TAG, "👤 AvatarPickerReply received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.AVATAR_NOTES_REPLY) { _, rawPacket ->
            Log.d(TAG, "📝 AvatarNotesReply received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.AVATAR_PICKS_REPLY) { _, rawPacket ->
            Log.d(TAG, "⭐ AvatarPicksReply received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.AVATAR_CLASSIFIED_REPLY) { _, rawPacket ->
            Log.d(TAG, "📰 AvatarClassifiedReply received")
        }
        
        // --- Classified Messages ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.CLASSIFIED_INFO_REPLY) { _, rawPacket ->
            Log.d(TAG, "📰 ClassifiedInfoReply received")
        }
        
        // --- Pick Messages ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.PICK_INFO_REPLY) { _, rawPacket ->
            Log.d(TAG, "⭐ PickInfoReply received")
        }
        
        // --- Event Messages ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.EVENT_INFO_REPLY) { _, rawPacket ->
            Log.d(TAG, "📅 EventInfoReply received")
        }
        
        // --- Group Messages (Extended) ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.GROUP_ROLE_MEMBERS_REPLY) { _, rawPacket ->
            Log.d(TAG, "👥 GroupRoleMembersReply received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.GROUP_NOTICES_LIST_REPLY) { _, rawPacket ->
            Log.d(TAG, "📢 GroupNoticesListReply received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.GROUP_NOTICE_REQUEST) { _, rawPacket ->
            Log.d(TAG, "📢 GroupNoticeRequest received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.CREATE_GROUP_REPLY) { _, rawPacket ->
            Log.d(TAG, "👥 CreateGroupReply received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.JOIN_GROUP_REPLY) { _, rawPacket ->
            Log.d(TAG, "👥 JoinGroupReply received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.LEAVE_GROUP_REPLY) { _, rawPacket ->
            Log.d(TAG, "👥 LeaveGroupReply received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.EJECT_GROUP_MEMBER_REPLY) { _, rawPacket ->
            Log.d(TAG, "👥 EjectGroupMemberReply received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.INVITE_GROUP_RESPONSE) { _, rawPacket ->
            Log.d(TAG, "👥 InviteGroupResponse received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.GROUP_ACCOUNT_SUMMARY_REPLY) { _, rawPacket ->
            Log.d(TAG, "💰 GroupAccountSummaryReply received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.GROUP_ACCOUNT_DETAILS_REPLY) { _, rawPacket ->
            Log.d(TAG, "💰 GroupAccountDetailsReply received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.GROUP_ACCOUNT_TRANSACTIONS_REPLY) { _, rawPacket ->
            Log.d(TAG, "💰 GroupAccountTransactionsReply received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.GROUP_ACTIVE_PROPOSAL_ITEM_REPLY) { _, rawPacket ->
            Log.d(TAG, "📋 GroupActiveProposalItemReply received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.GROUP_VOTE_HISTORY_ITEM_REPLY) { _, rawPacket ->
            Log.d(TAG, "🗳️ GroupVoteHistoryItemReply received")
        }
        
        // --- Calling Card Messages ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.OFFER_CALLING_CARD) { _, rawPacket ->
            Log.d(TAG, "📇 OfferCallingCard received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.ACCEPT_CALLING_CARD) { _, rawPacket ->
            Log.d(TAG, "📇 AcceptCallingCard received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.DECLINE_CALLING_CARD) { _, rawPacket ->
            Log.d(TAG, "📇 DeclineCallingCard received")
        }
        
        // --- Inventory Messages (Extended) ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.FETCH_INVENTORY_DESCENDENTS) { _, rawPacket ->
            Log.d(TAG, "📦 FetchInventoryDescendents received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.FETCH_INVENTORY) { _, rawPacket ->
            Log.d(TAG, "📦 FetchInventory received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.INVENTORY_ASSET_RESPONSE) { _, rawPacket ->
            Log.d(TAG, "📦 InventoryAssetResponse received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.UPDATE_INVENTORY_FOLDER) { _, rawPacket ->
            Log.d(TAG, "📁 UpdateInventoryFolder received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.MOVE_INVENTORY_FOLDER) { _, rawPacket ->
            Log.d(TAG, "📁 MoveInventoryFolder received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.CREATE_INVENTORY_ITEM) { _, rawPacket ->
            Log.d(TAG, "📦 CreateInventoryItem received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.SAVE_ASSET_INTO_INVENTORY) { _, rawPacket ->
            Log.d(TAG, "📦 SaveAssetIntoInventory received")
        }
        
        // --- Task/Object Inventory Messages ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.REQUEST_TASK_INVENTORY) { _, rawPacket ->
            Log.d(TAG, "📦 RequestTaskInventory received")
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
            Log.d(TAG, "📨 GenericMessage received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.SYSTEM_MESSAGE) { _, rawPacket ->
            Log.d(TAG, "📨 SystemMessage received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.ERROR_MESSAGE) { _, rawPacket ->
            Log.d(TAG, "❌ ErrorMessage received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.FEATURE_DISABLED) { _, rawPacket ->
            Log.d(TAG, "🚫 FeatureDisabled received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.VIEWER_FROZEN_MESSAGE) { _, rawPacket ->
            Log.d(TAG, "🥶 ViewerFrozenMessage received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.VIEWER_STATS) { _, rawPacket ->
            Log.d(TAG, "📊 ViewerStats received")
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
