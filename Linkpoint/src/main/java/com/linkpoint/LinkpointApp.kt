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
        rlvController = RLVController(chatManager = { if (::chatManager.isInitialized) chatManager else null })
        
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
