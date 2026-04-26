package com.linkpoint

import android.app.Application
import android.content.Context
import android.util.Log
import android.os.Handler
import android.os.Looper
import android.widget.Toast
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
import com.linkpoint.protocol.capabilities.EventQueueDispatcher
import com.linkpoint.protocol.messages.CircuitDispatcher
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
import com.linkpoint.protocol.messages.parseParcelProperties
import com.linkpoint.protocol.messages.parseImprovedInstantMessage
import com.linkpoint.protocol.transfer.TransferManager
import com.linkpoint.protocol.transfer.XferManager
import com.linkpoint.render.DrawDistanceManager
import com.linkpoint.render.HoverTextManager
import com.linkpoint.render.RenderManager
import com.linkpoint.render.RenderableUpdate
import com.linkpoint.render.particles.ParticleSystem
import com.linkpoint.rlv.RLVController
import com.linkpoint.service.ConnectionKeepAliveManager
import com.linkpoint.service.BackgroundResumeScheduler
import com.linkpoint.service.IdleHandler
import com.linkpoint.service.LinkpointConnectionService
import com.linkpoint.users.DisplayNameManager
import com.linkpoint.chat.MuteManager
import com.linkpoint.users.UserProfileManager
import com.linkpoint.voice.VoiceManager
import com.linkpoint.world.FriendsManager
import com.linkpoint.world.ParcelManager
import com.linkpoint.world.ProfileManager
import com.linkpoint.world.RegionExperienceManager
import com.linkpoint.world.SearchManager
import com.linkpoint.world.WorldMap
import com.linkpoint.world.environment.EnvironmentManager
import com.linkpoint.world.minimap.MinimapManager
import com.linkpoint.groups.GroupsManager
import com.linkpoint.animesh.AnimeshManager
import com.linkpoint.avatar.AnimationController
import com.linkpoint.diagnostics.ScenePopulationDiagnostics
import com.linkpoint.bom.BakesOnMeshManager
import com.linkpoint.inventory.LandmarkManager
import com.linkpoint.media.MediaManager
import com.linkpoint.messaging.MessagingDispatcher
import com.linkpoint.objects.SitManager
import com.linkpoint.snapshot.SnapshotManager
import com.linkpoint.teleport.TeleportManager
import com.linkpoint.hud.HUDManager
import com.linkpoint.world.estate.EstateManager
import com.linkpoint.xr.XRManager
import com.linkpoint.protocol.textures.TextureEntryParser
import com.linkpoint.protocol.types.getUUID
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.UUID
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

/**
 * Main Application class for Linkpoint - Second Life viewer for Android and XR
 * 
 * Based on the reference viewer's architecture, modernized for:
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

        /**
         * Explicit list of UDP message names that have runtime parser/handler coverage in LinkpointApp.
         * Used by protocol conformance tests to keep handler registration parity visible in CI reports.
         */
        val parserSupportedMessageNamesForConformance: Set<String> = setOf(
            "AgentAlertMessage",
            "AgentDataUpdate",
            "AgentMovementComplete",
            "AvatarAnimation",
            "ChangeUserRights",
            "ChatFromSimulator",
            "CoarseLocationUpdate",
            "CrossedRegion",
            "EnableSimulator",
            "FetchInventory",
            "FetchInventoryDescendents",
            "GroupTitlesRequest",
            "HealthMessage",
            "ImprovedInstantMessage",
            "ImprovedTerseObjectUpdate",
            "KillObject",
            "LayerData",
            "MapNameRequest",
            "ObjectProperties",
            "ObjectUpdate",
            "ObjectUpdateCached",
            "ObjectUpdateCompressed",
            "OfflineNotification",
            "OnlineNotification",
            "PacketAck",
            "ParcelOverlay",
            "RequestPayPrice",
            "RegionHandshake",
            "ScriptControlChange",
            "SoundTrigger",
            "StartPingCheck",
            "TeleportFailed",
            "TeleportFinish",
            "TeleportProgress",
            "TeleportStart",
            "AgentPause",
            "AgentResume",
            "DirFindQuery",
        )
    }
    
    // Application-wide coroutine scope for background operations
    val applicationScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    /**
     * Last AgentThrottle band sent to the simulator. Tracked so we only
     * re-send when the band actually changes (hysteresis), and so the
     * post-reconnect path can re-advertise the current band — the simulator
     * resets to its default throttle on a fresh circuit. See Lumiya parity
     * doc segment 02 §3.4 (L02-L / L02-M).
     */
    private enum class ThrottleBand { HIGH, LOW }
    @Volatile private var currentThrottleBand: ThrottleBand? = null
    @Volatile private var adaptiveThrottleStarted = false

    /**
     * Send an AgentThrottle whose budget is matched to the current network
     * quality. POOR/FAIR/UNKNOWN → LOW band (~310 kbps total, with texture
     * and task bandwidth heavily reduced). EXCELLENT/GOOD → HIGH band
     * (Linkpoint's desktop default). Lumiya advertises a single throttle at
     * circuit-ready and never adapts; Linkpoint adapts because it actually
     * downloads textures/meshes (Lumiya is a thin client) and a desktop
     * budget on cellular saturates the link, which the user observed as
     * "Lumiya worked on 2G/3G but Linkpoint won't run on 5G".
     *
     * @param force re-send even if the band hasn't changed (used after a
     *   socket reconnect, since the simulator forgot our previous throttle).
     */
    private fun applyAdaptiveAgentThrottle(force: Boolean = false) {
        if (!::udpConnection.isInitialized || !udpConnection.isConnected.value) return
        val q = try { protocol.qualityManager.quality.value } catch (_: Exception) { null }
        val targetBand = when (q) {
            com.linkpoint.network.core.ConnectionQualityManager.Quality.EXCELLENT,
            com.linkpoint.network.core.ConnectionQualityManager.Quality.GOOD -> ThrottleBand.HIGH
            else -> ThrottleBand.LOW
        }
        if (!force && targetBand == currentThrottleBand) return
        try {
            if (targetBand == ThrottleBand.LOW) {
                // ~310 kbps total. Wind/cloud reduced to a token amount;
                // texture/asset deeply cut so foreground work (chat, IM,
                // group, object updates) keeps flowing.
                udpConnection.sendAgentThrottle(
                    resend = 50_000f,
                    land = 50_000f,
                    wind = 5_000f,
                    cloud = 5_000f,
                    task = 100_000f,
                    texture = 50_000f,
                    asset = 50_000f
                )
                Log.i(TAG, "🐢 AgentThrottle: LOW band (quality=$q, force=$force)")
            } else {
                // Linkpoint default — see UDPConnectionFixed.sendAgentThrottle.
                udpConnection.sendAgentThrottle()
                Log.i(TAG, "🐇 AgentThrottle: HIGH band (quality=$q, force=$force)")
            }
            currentThrottleBand = targetBand
        } catch (e: Exception) {
            Log.w(TAG, "applyAdaptiveAgentThrottle failed: ${e.message}")
        }
    }

    /**
     * Start a one-shot coroutine that observes connection-quality changes
     * and re-sends AgentThrottle when the band crosses HIGH↔LOW. Idempotent.
     */
    private fun ensureAdaptiveThrottleObserver() {
        if (adaptiveThrottleStarted) return
        adaptiveThrottleStarted = true
        applicationScope.launch {
            try {
                protocol.qualityManager.quality.collect {
                    applyAdaptiveAgentThrottle(force = false)
                }
            } catch (e: Exception) {
                Log.w(TAG, "Adaptive throttle observer ended: ${e.message}")
            }
        }
    }

    // ─── Auto re-login coordinator ────────────────────────────────────────
    //
    // When the UDP circuit is detected as dead (post-reconnect-silence
    // escalation, or a 3-unanswered-ping watchdog trip), the LLUDP protocol
    // gives us no in-band way to revive it: the simulator-side circuit has
    // already timed us out (typically because cellular NAT rotated our public
    // source port). The only correct recovery is a full HTTP re-login, which
    // gets us a fresh circuit code and a new sim handoff.
    //
    // Architecture matches Lumiya's `SLGridConnection.Reconnect()` (3s
    // backoff, max 10 attempts) and the official SL mobile viewer's
    // `CoreNetworkingService` (`_alwaysReconnect`, `IsReconnecting`,
    // `MaxReconnectTime`, `_currentLogin`-style cached login response). The
    // SL mobile viewer abandoned LLUDP for gRPC so its transport is not a
    // model for us, but its lifecycle pattern is transport-agnostic and
    // correct.

    private data class LoginCredentials(
        val firstName: String,
        val lastName: String,
        val password: String,
        val loginUri: String,
        val startLocation: String,
        val mfaHash: String
    )

    @Volatile private var cachedLoginCredentials: LoginCredentials? = null
    private val userWantsConnected = AtomicBoolean(false)
    private val isReconnecting = AtomicBoolean(false)
    private val reconnectAttempts = AtomicInteger(0)
    @Volatile private var firstReconnectAttemptAt: Long = 0L
    private val reloginMutex = Mutex()

    /** 3 seconds — Lumiya's `Reconnect()` sleep before each attempt. */
    private val reloginBackoffMs: Long = 3_000L
    /** 10 — Lumiya's `GlobalOptions.MaxReconnectAttempts`. */
    private val reloginMaxAttempts: Int = 10
    /** 120s deadline — mirrors SL mobile's `MaxReconnectTime` so we surrender if a recovery cycle is dragging on. */
    private val reloginMaxWindowMs: Long = 120_000L

    /**
     * Called by [SecondLifeProtocol.login] on Success. We cache enough to
     * re-drive a full login from the auto-reconnect path without bouncing
     * through the login UI. MFA hash is updated each time so the next
     * resume can skip the prompt.
     */
    internal fun rememberLoginCredentials(
        firstName: String, lastName: String, password: String,
        loginUri: String, startLocation: String, mfaHash: String?
    ) {
        cachedLoginCredentials = LoginCredentials(
            firstName, lastName, password, loginUri, startLocation, mfaHash ?: ""
        )
        userWantsConnected.set(true)
        reconnectAttempts.set(0)
        firstReconnectAttemptAt = 0L
        Log.d(TAG, "Cached login credentials for auto re-login")
    }

    /**
     * Called from [SecondLifeProtocol.disconnect] (user-initiated logout)
     * so we don't auto re-login after an explicit logout. Mirrors Lumiya's
     * `userWantsConnected = false` in `disconnect()`.
     */
    internal fun forgetLoginCredentials() {
        userWantsConnected.set(false)
        cachedLoginCredentials = null
        reconnectAttempts.set(0)
        firstReconnectAttemptAt = 0L
        Log.d(TAG, "Cleared cached login credentials (user logout)")
    }

    /**
     * Drive the auto-reconnect loop. Safe to call from anywhere; a single
     * coordinator runs at a time (gated by [reloginMutex]). Bails when:
     *   - the user explicitly logged out (`userWantsConnected == false`),
     *   - we have no cached credentials (no login yet, or already cleared),
     *   - we've exhausted [reloginMaxAttempts], OR
     *   - cumulative recovery time exceeds [reloginMaxWindowMs].
     */
    internal fun attemptAutoRelogin() {
        applicationScope.launch {
            reloginMutex.withLock {
                runReloginLoop()
            }
        }
    }

    private suspend fun runReloginLoop() {
        if (!userWantsConnected.get()) {
            Log.i(TAG, "Auto re-login skipped: user is not connected (manual logout)")
            return
        }
        val creds = cachedLoginCredentials ?: run {
            Log.w(TAG, "Auto re-login skipped: no cached credentials")
            return
        }

        if (firstReconnectAttemptAt == 0L) firstReconnectAttemptAt = System.currentTimeMillis()
        isReconnecting.set(true)

        try {
            while (userWantsConnected.get()) {
                val attempt = reconnectAttempts.incrementAndGet()
                val elapsed = System.currentTimeMillis() - firstReconnectAttemptAt

                if (attempt > reloginMaxAttempts) {
                    Log.e(TAG, "Auto re-login: exceeded $reloginMaxAttempts attempts — giving up")
                    sessionManager.setConnectionState(
                        com.linkpoint.network.events.ConnectionState.DISCONNECTED
                    )
                    return
                }
                if (elapsed >= reloginMaxWindowMs) {
                    Log.e(TAG, "Auto re-login: ${elapsed}ms exceeds ${reloginMaxWindowMs}ms window — giving up")
                    sessionManager.setConnectionState(
                        com.linkpoint.network.events.ConnectionState.DISCONNECTED
                    )
                    return
                }

                Log.i(TAG, "🔄 Auto re-login attempt $attempt/$reloginMaxAttempts (elapsed=${elapsed}ms)")
                protocol.stateManager.setStatus(
                    com.linkpoint.network.core.NetworkStateManager.ConnectionStatus.RECONNECTING
                )

                // Tear down the dead UDP socket cleanly so the new login
                // doesn't fight with stale I/O thread state.
                try { udpConnection.disconnect() } catch (e: Exception) {
                    Log.w(TAG, "udpConnection.disconnect() during re-login: ${e.message}")
                }

                delay(reloginBackoffMs)

                val result = try {
                    protocol.login(
                        firstName = creds.firstName,
                        lastName = creds.lastName,
                        password = creds.password,
                        loginUri = creds.loginUri,
                        startLocation = creds.startLocation,
                        mfaToken = "",
                        mfaHash = creds.mfaHash
                    )
                } catch (e: Exception) {
                    Log.w(TAG, "Auto re-login attempt $attempt threw: ${e.message}")
                    null
                }

                when (result) {
                    is com.linkpoint.network.LoginResult.Success -> {
                        Log.i(TAG, "✓ Auto re-login succeeded on attempt $attempt")
                        // Refresh cached MFA hash — Linden rotates these.
                        cachedLoginCredentials = creds.copy(mfaHash = result.mfaHash ?: creds.mfaHash)
                        reconnectAttempts.set(0)
                        firstReconnectAttemptAt = 0L
                        return
                    }
                    is com.linkpoint.network.LoginResult.MFARequired -> {
                        Log.w(TAG, "Auto re-login: server now requires MFA — cannot proceed automatically")
                        userWantsConnected.set(false)
                        sessionManager.setConnectionState(
                            com.linkpoint.network.events.ConnectionState.DISCONNECTED
                        )
                        return
                    }
                    is com.linkpoint.network.LoginResult.Failure -> {
                        Log.w(TAG, "Auto re-login attempt $attempt failed: ${result.message} [${result.errorCode}]")
                        // Loop continues; next iteration applies backoff.
                    }
                    null -> {
                        // Exception path — treat like failure, retry.
                    }
                }
            }
            Log.i(TAG, "Auto re-login loop exited: user no longer wants connected")
        } finally {
            isReconnecting.set(false)
        }
    }

    /**
     * Single-threaded dispatcher for serialized messaging work (MessageThread).
     */
    val messagingDispatcher: CoroutineDispatcher
        get() = MessagingDispatcher.dispatcher
    
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
    /**
     * Per-region SimulatorFeatures snapshot. Populated after the seed cap
     * is fetched and re-fetched on region change. Renderer / inventory /
     * UI code should consult `simulatorFeatures.features.value` rather
     * than assume the protocol baseline — modern sims advertise PBR,
     * BoM, animated objects, raised attachment limits, etc. via this cap.
     */
    lateinit var simulatorFeatures: com.linkpoint.world.SimulatorFeaturesManager
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
    lateinit var appearanceManager: com.linkpoint.avatar.AppearanceManager
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
    lateinit var regionExperienceManager: RegionExperienceManager
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
    
    // Flag to track if CompleteAgentMovement has been sent.
    // Per SL protocol: This must be sent immediately once UseCircuitCode is ACKed;
    // the server won't send RegionHandshake until it receives CompleteAgentMovement.
    // Using AtomicBoolean to prevent race conditions with concurrent PacketAck messages.
    private val completeAgentMovementSent = AtomicBoolean(false)
    
    override fun onCreate() {
        super.onCreate()
        instance = this
        Log.i(TAG, "Linkpoint application starting...")

        // Install Conscrypt as the highest-priority JCA provider before any
        // SSL handshake fires. Conscrypt's BoringSSL backend negotiates
        // ALPN reliably, which is what makes OkHttp's HTTP/2 upgrade
        // actually take effect — the platform SSL stack on many Android
        // devices fails ALPN silently and leaves us on HTTP/1.1 (the
        // 2026-04-25 Athanasia capture showed 0/73 H2 on textures despite
        // OkHttp.Builder().protocols(HTTP_2, HTTP_1_1)). Inserting at
        // position 1 makes Conscrypt the default for all subsequent
        // SSLSocketFactory.getDefault() calls and any explicitly built
        // SSL contexts.
        try {
            java.security.Security.insertProviderAt(
                org.conscrypt.Conscrypt.newProvider(), 1
            )
            Log.i(TAG, "Conscrypt installed as primary JCA provider — HTTP/2 ALPN should now work")
        } catch (e: Throwable) {
            // UnsatisfiedLinkError on a device without the Conscrypt
            // native library, or NoClassDefFoundError if the dep is
            // somehow stripped — fall through to platform SSL. We log
            // loudly because this silently degrades H2 → H1.1.
            Log.e(TAG, "Conscrypt install failed; HTTP/2 will fall back to platform ALPN: ${e.message}", e)
        }

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
        Log.i(TAG, "Network logger initialized with auto-save to Documents/Linkpoint Logs/")

        val decoderStatus = JPEG2000Decoder.runStartupSelfTest()
        if (!decoderStatus.available) {
            Handler(Looper.getMainLooper()).post {
                Toast.makeText(
                    this,
                    "JPEG2000 support unavailable. Some textures may use placeholders.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        
        // Initialize and AUTO-START the session log recorder so every
        // packet, HTTP request, capability event, and render-pipeline
        // moment from app launch onward is captured. Without this the
        // recorder was initialized but never actually recording, so
        // SessionLogRecorder.log* calls (including the new
        // RenderDiagnostics events) silently no-op'd.
        com.linkpoint.utils.SessionLogRecorder.initialize(this)
        com.linkpoint.utils.SessionLogRecorder.startRecording()
        Log.i(TAG, "Session log recorder initialized and recording")
        
        // Initialize Linkpoint circuit integration (device-adaptive settings, DNS, IPv4)
        com.linkpoint.protocol.circuit.LinkpointCircuitIntegration.initialize(this)

        initializeManagers()
        BackgroundResumeScheduler.schedule(this, immediate = false)

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
        simulatorFeatures = com.linkpoint.world.SimulatorFeaturesManager(capabilityManager)
        udpConnection = UDPConnectionFixed()
        
        // Protocol handler
        protocol = SecondLifeProtocol(this)
        
        // Rendering (Filament-based)
        renderManager = RenderManager(this)
        
        // XR/VR support
        xrManager = XRManager(this)
        
        // Cache management system (Linkpoint Cache structure)
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
        regionExperienceManager = RegionExperienceManager(capabilityManager)
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
        mediaManager = MediaManager(this, udpConnection, capabilityManager)
        
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
        notecardManager = NotecardManager(transferManager, capabilityManager)
        
        // NEW: Task inventory manager
        taskInventoryManager = TaskInventoryManager(udpConnection, xferManager, agentId)
        
        // NEW: Mute manager
        muteManager = MuteManager(this)
        
        // NEW: User Profile Manager
        userProfileManager = UserProfileManager(capabilityManager, udpConnection, agentId)
        
        // NEW: Initialize connection keep-alive with credentials
        connectionKeepAlive.initialize(agentId, udpConnection.getSessionId())
        
        // Drive NetworkStateManager status transitions from the UDP layer's
        // watchdog. Without this, the live diagnostic counter stays at 0 even
        // when the watchdog has fired and a socket reconnect is in flight.
        // Captured by the 2026-04-25 Athanasia debug report.
        udpConnection.setNetworkStateListener { transition ->
            when (transition) {
                UDPConnectionFixed.NetworkStateTransition.RECONNECTING -> {
                    Log.i(TAG, "🔄 UDP watchdog: RECONNECTING (socket reconnect in flight)")
                    protocol.stateManager.setStatus(
                        com.linkpoint.network.core.NetworkStateManager.ConnectionStatus.RECONNECTING
                    )
                }
                UDPConnectionFixed.NetworkStateTransition.CONNECTED -> {
                    Log.i(TAG, "✓ UDP watchdog: CONNECTED (socket reconnect succeeded)")
                    protocol.stateManager.setStatus(
                        com.linkpoint.network.core.NetworkStateManager.ConnectionStatus.CONNECTED
                    )
                    // Re-advertise AgentThrottle: a fresh circuit on the
                    // simulator side has reset to the default budget.
                    // Without this, the cellular-friendly low-band throttle
                    // we sent originally would be silently lost on every
                    // socket reconnect (the live debug report had four
                    // reconnects in 2.3 minutes — see segment 02 §3.7
                    // L02-U).
                    applicationScope.launch { applyAdaptiveAgentThrottle(force = true) }
                }
                UDPConnectionFixed.NetworkStateTransition.FAULTED -> {
                    Log.e(TAG, "✗ UDP watchdog: FAULTED (socket reconnect failed)")
                    protocol.stateManager.setStatus(
                        com.linkpoint.network.core.NetworkStateManager.ConnectionStatus.FAULTED
                    )
                }
            }
        }

        // Hard-failure callback: fires when the UDP layer concludes the
        // simulator-side circuit is dead (post-reconnect-silence escalation,
        // or 3-unanswered-pings watchdog trip). The simulator no longer has
        // a circuit slot for us, so socket-level rebinds are pointless —
        // the only protocol-correct recovery is a full HTTP re-login. The
        // coordinator here matches Lumiya's `Reconnect()` (3s backoff,
        // 10 attempts) and the SL-mobile `CoreNetworkingService` lifecycle
        // (cached LoginResponse, IsReconnecting flag, MaxReconnectTime
        // deadline). See `attemptAutoRelogin()` above.
        udpConnection.setReconnectionCallback {
            Log.w(TAG, "⚠️ UDP layer reports dead circuit — escalating to auto re-login")
            // Notify the keep-alive manager for any UI/cleanup it owns.
            applicationScope.launch { connectionKeepAlive.notifyConnectionIssue() }
            attemptAutoRelogin()
        }
        
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

        // Outfit manager (needs baker from avatar manager).
        //
        // AvatarManager.setMyAgentId() above proactively creates the local
        // Avatar entry so getMyAvatar() must be non-null here. If it is null
        // it means the agent ID never made it into AvatarManager — bail loudly
        // rather than silently leaving outfitManager/appearanceManager
        // uninitialized for the entire session (the failure mode captured in
        // the 2026-04-25 Athanasia debug report, where Force Refresh
        // Appearance reported "not logged in" despite a healthy session).
        val myAvatar = avatarManager.getMyAvatar()
        if (myAvatar == null) {
            Log.e(TAG, "❌ AvatarManager.getMyAvatar() == null after setMyAgentId — outfit/appearance pipeline NOT initialized for $agentId")
        }
        if (myAvatar != null) {
            outfitManager = OutfitManager(
                inventoryManager,
                myAvatar.baker,
                gestureManager,
                udpConnection,
                agentId,
                udpConnection.getSessionId(),
                objectManager
            ) { item ->
                val cacheType = when (item.assetType) {
                    AssetType.CLOTHING.value -> AssetType.CLOTHING
                    AssetType.BODYPART.value -> AssetType.BODYPART
                    else -> AssetType.CLOTHING
                }
                assetCache.get(item.assetId, cacheType)
                    ?: transferManager.fetchAsset(item.assetId, item.assetType)?.also { fetched ->
                        assetCache.put(item.assetId, cacheType, fetched)
                    }
            }
            avatarManager.setOutfitManager(outfitManager)

            // Wire the local-agent appearance pipeline. AppearanceManager
            // bakes (via AvatarBaker), uploads each baked texture via the
            // UploadBakedTexture capability, then sends AgentSetAppearance.
            // The trigger lambda is called from AvatarManager whenever
            // AgentWearablesUpdate has been applied so the simulator
            // gets fresh bakes without an explicit user "save outfit" action.
            appearanceManager = com.linkpoint.avatar.AppearanceManager(
                udpConnection, myAvatar.baker
            )
            appearanceManager.setSessionInfo(agentId, udpConnection.getSessionId())
            avatarManager.appearanceUpdateTrigger = {
                try {
                    appearanceManager.sendAppearanceUpdate()
                } catch (e: Exception) {
                    Log.w(TAG, "sendAppearanceUpdate failed: ${e.message}")
                }
            }
        }
        
        // Modern features: Animesh and Bakes on Mesh
        animeshManager = AnimeshManager(meshManager, animationManager)
        bomManager = BakesOnMeshManager(capabilityManager, textureManager)
        
        // Teleport manager
        teleportManager = TeleportManager(udpConnection, capabilityManager, agentId)
        
        // HUD manager
        hudManager = HUDManager(this, objectManager, udpConnection, agentId)
        
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
                    // Parser already trims null padding; trim whitespace for display safety.
                    val regionName = regionData.simName.trim()
                    if (regionName.isNotEmpty()) {
                        sessionManager.updateRegionName(regionName)
                        Log.d(TAG, "Session region name updated to: $regionName")
                        com.linkpoint.utils.SessionLogRecorder.logRegionChange(
                            regionName, 0L, null
                        )
                    } else {
                        Log.w(TAG, "RegionHandshake simName was empty after trimming")
                    }
                    
                    // Update terrain manager with water height
                    if (::terrainManager.isInitialized) {
                        terrainManager.setWaterHeight(regionData.waterHeight)
                        terrainManager.reset()  // Reset terrain for new region
                        Log.d(TAG, "Terrain manager reset for new region, water height: ${regionData.waterHeight}")
                    }

                    // Wire RegionHandshake terrain texture UUIDs + elevation
                    // bounds into the renderer. terrainTextures is in the
                    // order [Base0..3, Detail0..3]; the splatting material
                    // uses the four detail textures.
                    if (::renderManager.isInitialized) {
                        val tr = renderManager.getTerrainRenderer()
                        if (tr != null) {
                            val starts = regionData.terrainStartHeights
                            val ranges = regionData.terrainHeightRanges
                            if (starts.size == 4 && ranges.size == 4) {
                                tr.setHeightBlendParams(starts.toFloatArray(), ranges.toFloatArray())
                            }
                            // Resolve detail texture UUIDs through TextureManager;
                            // each setDetailTexture() call will fire as soon as
                            // the JPEG2000 decode lands.
                            if (::textureManager.isInitialized && regionData.terrainTextures.size >= 8) {
                                val detailIds = regionData.terrainTextures.subList(4, 8)
                                detailIds.forEachIndexed { idx, uuid ->
                                    applicationScope.launch {
                                        val bmp = try {
                                            textureManager.getTexture(uuid)
                                        } catch (e: Exception) { null }
                                        if (bmp != null) {
                                            renderManager.dispatcher.post(Runnable {
                                                renderManager.uploadTerrainDetailTexture(idx, bmp)
                                            })
                                        }
                                    }
                                }
                            }
                        }
                    }
                    
                    // Send RegionHandshakeReply DIRECTLY from I/O thread (non-suspend now)
                    try {
                        Log.d(TAG, "Sending RegionHandshakeReply...")
                        udpConnection.sendRegionHandshakeReply()
                        com.linkpoint.utils.InitializationTracker.completePhase(
                            com.linkpoint.utils.InitializationTracker.Phase.REGION_HANDSHAKE_RECEIVED,
                            "Reply sent to ${regionData.simName}"
                        )
                        com.linkpoint.utils.InitializationTracker.reachPhase(
                            com.linkpoint.utils.InitializationTracker.Phase.REGION_HANDSHAKE_REPLIED,
                            "Waiting for world data"
                        )
                        Log.i(TAG, "✓ RegionHandshakeReply SENT - world data should start loading")
                        ScenePopulationDiagnostics.markRegionHandshakeComplete()
                    } catch (e: Exception) {
                        com.linkpoint.utils.InitializationTracker.failPhase(
                            com.linkpoint.utils.InitializationTracker.Phase.REGION_HANDSHAKE_RECEIVED,
                            "Failed to send reply: ${e.message}"
                        )
                        Log.e(TAG, "✗ Error sending RegionHandshakeReply", e)
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
                    Log.i(TAG, "AgentMovementComplete: position=${moveData.position}, regionHandle=${moveData.regionHandle}")
                    com.linkpoint.utils.InitializationTracker.logInfo("Position: ${moveData.position}")
                    
                    // Update region info with region handle and position from AgentMovementComplete
                    // This is critical for proper sim registration - the region handle identifies
                    // which simulator region we're connected to
                    sessionManager.updateRegionInfo(
                        regionHandle = moveData.regionHandle,
                        x = moveData.position.x.toInt(),
                        y = moveData.position.y.toInt()
                    )
                    Log.i(TAG, "✓ Region handle registered: ${moveData.regionHandle}")
                    
                    // Update connection state to fully connected
                    sessionManager.setConnectionState(com.linkpoint.core.ConnectionState.CONNECTED)
                    udpConnection.startAgentUpdates()
                    Log.i(TAG, "✓ AgentUpdate loop started")
                    
                    com.linkpoint.utils.InitializationTracker.completePhase(
                        com.linkpoint.utils.InitializationTracker.Phase.AGENT_MOVEMENT_COMPLETE,
                        "Agent at ${moveData.position}"
                    )
                    com.linkpoint.utils.InitializationTracker.reachPhase(
                        com.linkpoint.utils.InitializationTracker.Phase.FULLY_CONNECTED,
                        "Agent is now in world"
                    )
                    com.linkpoint.utils.InitializationTracker.logCritical(
                        "FULLY CONNECTED - World loading should begin"
                    )

                    // Warm-fetch priority inventory folders so Current Outfit /
                    // Favorites / Landmarks / My Outfits / Inbox are already
                    // populated by the time the UI opens them. Fire-and-forget.
                    if (::inventoryManager.isInitialized) {
                        inventoryManager.warmFetch()
                    }

                    // Request own avatar profile so the Profile tab is populated
                    // by the time the user opens it. Without this request the
                    // sim never volunteers AvatarPropertiesReply for self and
                    // the profile sits blank (2026-04-25 Athanasia capture).
                    val myId = this@LinkpointApp.agentId
                    if (myId != null && ::userProfileManager.isInitialized) {
                        applicationScope.launch {
                            try {
                                userProfileManager.requestProfile(myId)
                            } catch (e: Exception) {
                                Log.w(TAG, "Failed to request own profile: ${e.message}")
                            }
                        }
                    }

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
        udpConnection.registerParsedHandler(com.linkpoint.protocol.messages.MessageIds.CHAT_FROM_SIMULATOR) { _, parsed ->
            try {
                val chatData = parsed as? com.linkpoint.protocol.messages.ChatData
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
        
        // PCode constants (from the reference viewer)
        val PCODE_PRIM = 9
        val PCODE_AVATAR = 47
        
        // Helper function to process object updates by PCode
        fun processObjectUpdate(update: com.linkpoint.protocol.messages.ObjectUpdateData) {
            when (update.pcode) {
                PCODE_AVATAR -> {
                    avatarUpdateCount++
                    ScenePopulationDiagnostics.markPacketReceived(ScenePopulationDiagnostics.EntityType.AVATAR)
                    ScenePopulationDiagnostics.markParsed(ScenePopulationDiagnostics.EntityType.AVATAR)
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
                        ScenePopulationDiagnostics.markManagerApplied(ScenePopulationDiagnostics.EntityType.AVATAR, true)
                    } else {
                        ScenePopulationDiagnostics.markManagerApplied(ScenePopulationDiagnostics.EntityType.AVATAR, false)
                    }
                    // Add avatar to scene for rendering
                    if (::renderManager.isInitialized) {
                        renderManager.enqueueUpdate(
                            RenderableUpdate.AvatarUpdate(
                                agentId = update.fullId,
                                position = update.position,
                                rotation = update.rotation
                            )
                        )
                        // Drive the follow / mouselook camera off the local
                        // agent's position so user input doesn't fight the
                        // avatar's actual world location every frame.
                        if (::avatarManager.isInitialized &&
                            avatarManager.getMyAvatar()?.agentId == update.fullId
                        ) {
                            renderManager.cameraController.setAgentPosition(update.position)
                        }
                    } else {
                        ScenePopulationDiagnostics.markRendererSubmitted(ScenePopulationDiagnostics.EntityType.AVATAR, false)
                    }
                }
                else -> {
                    // Prims, trees, grass, particles all go to object manager
                    if (::objectManager.isInitialized) {
                        objectManager.handleObjectUpdate(update)
                    }
                    // Add object to scene for rendering using PrimRenderer
                    // PrimRenderer creates actual renderable meshes (box, sphere, etc.)
                    if (::renderManager.isInitialized) {
                        renderManager.enqueueUpdate(RenderableUpdate.PrimUpdate(update))
                    }

                    // Mesh-asset prims: kick off MeshManager fetch and ask
                    // PrimRenderer to swap the path/profile fallback geometry
                    // for the parsed mesh once it lands. Failures fall back
                    // gracefully to the path/profile box already rendered.
                    val meshAssetId = update.getMeshAssetId()
                    if (meshAssetId != null && ::meshManager.isInitialized && ::renderManager.isInitialized) {
                        val textureEntrySnapshot = update.textureEntry
                        applicationScope.launch {
                            val meshData = try {
                                meshManager.getMesh(meshAssetId)
                            } catch (e: Exception) {
                                Log.w(TAG, "Mesh fetch failed for ${update.localId}: ${e.message}")
                                null
                            }
                            if (meshData != null) {
                                // TextureBinder: per face, BoM-resolve the
                                // texture UUID, fetch via TextureManager,
                                // hop to the render thread and call onLoaded.
                                val binder = com.linkpoint.render.prims.MeshPrimRenderer.TextureBinder { _, texId, onLoaded ->
                                    val resolvedId = if (::avatarManager.isInitialized) {
                                        com.linkpoint.avatar.BakesOnMesh.resolve(texId) { slot ->
                                            avatarManager.getMyAvatar()?.baker?.getBakedTextures()?.get(slot)
                                        }
                                    } else texId
                                    if (!com.linkpoint.protocol.textures.TextureEntryParser.shouldDownload(resolvedId)) return@TextureBinder
                                    if (!::textureManager.isInitialized) return@TextureBinder
                                    applicationScope.launch {
                                        val bmp = try { textureManager.getTexture(resolvedId) } catch (_: Exception) { null }
                                        if (bmp != null) {
                                            renderManager.dispatcher.post(Runnable {
                                                val tex = renderManager.uploadBitmapAsTexture(bmp)
                                                if (tex != null) onLoaded(tex)
                                            })
                                        }
                                    }
                                }
                                renderManager.dispatcher.post(Runnable {
                                    renderManager.attachMeshAsset(
                                        update.localId, meshData,
                                        textureEntrySnapshot, binder
                                    )
                                })
                            }
                        }
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
        
        udpConnection.registerParsedHandler(com.linkpoint.protocol.messages.MessageIds.OBJECT_UPDATE) { _, parsed ->
            try {
                val updates = (parsed as? List<*>)?.filterIsInstance<com.linkpoint.protocol.messages.ObjectUpdateData>() ?: emptyList()
                ScenePopulationDiagnostics.markPacketReceived(ScenePopulationDiagnostics.EntityType.OBJECT, updates.size)
                ScenePopulationDiagnostics.markParsed(ScenePopulationDiagnostics.EntityType.OBJECT, updates.size)
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
        udpConnection.registerParsedHandler(com.linkpoint.protocol.messages.MessageIds.OBJECT_UPDATE_COMPRESSED) { _, parsed ->
            try {
                val updates = (parsed as? List<*>)?.filterIsInstance<com.linkpoint.protocol.messages.ObjectUpdateData>() ?: emptyList()
                ScenePopulationDiagnostics.markPacketReceived(ScenePopulationDiagnostics.EntityType.OBJECT, updates.size)
                ScenePopulationDiagnostics.markParsed(ScenePopulationDiagnostics.EntityType.OBJECT, updates.size)
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
                    // Per SL protocol: respond with RequestMultipleObjects with CacheMissType=0
                    if (cachedData.objects.isNotEmpty()) {
                        val objectIds = cachedData.objects.map { it.localId }
                        udpConnection.sendRequestMultipleObjects(objectIds, cacheMissType = 0)
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
                    // Send ping response directly (non-suspend now)
                    udpConnection.handleStartPingCheck(pingId, 0)
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
                            // Remove from PrimRenderer and SceneManager
                            if (::renderManager.isInitialized) {
                                renderManager.removePrim(localId)
                                if (obj != null) {
                                    renderManager.getSceneManager()?.removeObject(obj.fullId)
                                }
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
        
        // PacketAck - Acknowledgment messages for reliable packets.
        // These are sent by the simulator to confirm receipt of our reliable packets.
        // CRITICAL: When UseCircuitCode is acknowledged, we MUST immediately send
        // CompleteAgentMovement - the simulator won't send RegionHandshake until then.
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.PACKET_ACK) { _, rawPacket ->
            // PacketAck format: Count (1 byte), then list of acknowledged sequence numbers (4 bytes each)
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload == null) return@registerHandler
                val buffer = java.nio.ByteBuffer.wrap(payload).order(java.nio.ByteOrder.LITTLE_ENDIAN)
                val count = buffer.get().toInt() and 0xFF

                // Match the ACK list against the exact sequence number we used for
                // UseCircuitCode, not a hardcoded value. Lumiya sends UseCircuitCode
                // with the counter's first increment (seq=1), and any reconnect uses
                // a larger number — so we must match on what the sender recorded.
                val useCircuitCodeSeq = udpConnection.useCircuitCodeSequence
                val ackedSequences = mutableListOf<Int>()
                var containsUseCircuitCodeAck = false
                for (i in 0 until count) {
                    if (buffer.remaining() >= 4) {
                        val seq = buffer.int
                        ackedSequences.add(seq)
                        if (useCircuitCodeSeq >= 0 && seq == useCircuitCodeSeq) {
                            containsUseCircuitCodeAck = true
                        }
                    }
                }

                Log.d(TAG, "PacketAck received: $count packets acknowledged - sequences: $ackedSequences")

                if (containsUseCircuitCodeAck && completeAgentMovementSent.compareAndSet(false, true)) {
                    Log.i(TAG, "╔══════════════════════════════════════════════════════════════════")
                    Log.i(TAG, "║ ⭐ UseCircuitCode ACKNOWLEDGED - Sending CompleteAgentMovement")
                    Log.i(TAG, "╚══════════════════════════════════════════════════════════════════")

                    // CRITICAL FIX: Send CompleteAgentMovement DIRECTLY from the I/O thread.
                    // Previously this was wrapped in applicationScope.launch which added latency
                    // and could fail if the dispatcher was starved. The send methods are now
                    // non-suspend and thread-safe, matching Lumiya's synchronous approach.
                    try {
                        udpConnection.sendCompleteAgentMovement()
                        Log.i(TAG, "✓ CompleteAgentMovement SENT - server should now send RegionHandshake")

                        // Send AgentThrottle adapted to the current network
                        // band; a desktop-default throttle saturates cellular
                        // links and starves chat/IM/group traffic.
                        applyAdaptiveAgentThrottle(force = true)
                        ensureAdaptiveThrottleObserver()
                    } catch (e: Exception) {
                        Log.e(TAG, "✗ Error sending CompleteAgentMovement/AgentThrottle", e)
                        completeAgentMovementSent.set(false)
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
        udpConnection.registerParsedHandler(com.linkpoint.protocol.messages.MessageIds.TELEPORT_FINISH) { _, parsed ->
            try {
                val data = parsed as? com.linkpoint.protocol.messages.TeleportFinishData
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
        udpConnection.registerParsedHandler(com.linkpoint.protocol.messages.MessageIds.TELEPORT_FAILED) { _, parsed ->
            try {
                val data = parsed as? com.linkpoint.protocol.messages.TeleportFailedData
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
        udpConnection.registerParsedHandler(com.linkpoint.protocol.messages.MessageIds.TELEPORT_PROGRESS) { _, parsed ->
            try {
                val data = parsed as? com.linkpoint.protocol.messages.TeleportProgressData
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
                
                val data = com.linkpoint.protocol.messages.MessageParser.parseParcelProperties(payload)
                if (data != null && ::parcelManager.isInitialized) {
                    Log.d(TAG, "🗺️ ParcelProperties: ${data.name} (${data.area} sqm)")
                    parcelManager.handleParcelProperties(
                        localId = data.localId,
                        ownerId = data.ownerId,
                        groupId = data.groupId,
                        name = data.name,
                        description = data.description,
                        claimDate = data.claimDate.toLong(),
                        claimPrice = data.claimPrice,
                        rentPrice = data.rentPrice,
                        aabbMin = data.aabbMin,
                        aabbMax = data.aabbMax,
                        area = data.area,
                        actualArea = data.area,
                        simWideMaxPrims = data.simWideMaxPrims,
                        simWideTotal = data.simWideTotalPrims,
                        maxPrims = data.maxPrims,
                        totalPrims = data.totalPrims,
                        ownerPrims = data.ownerPrims,
                        groupPrims = data.groupPrims,
                        otherPrims = data.otherPrims,
                        selectedPrims = data.selectedPrims,
                        parcelPrimBonus = data.parcelPrimBonus,
                        cleanTime = data.otherCleanTime,
                        flags = data.parcelFlags,
                        landingType = data.landingType,
                        musicUrl = data.musicUrl,
                        mediaUrl = data.mediaUrl,
                        mediaId = data.mediaId,
                        mediaAutoScale = data.mediaAutoScale != 0,
                        groupPrimeOverride = data.groupPrimsAllowed,
                        category = data.category,
                        snapshotId = data.snapshotId,
                        userLocation = data.userLocation,
                        userLookAt = data.userLookAt,
                        status = data.status,
                        passPrice = data.passPrice,
                        passHours = data.passHours
                    )
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

                        // Apply VisualParam 33 (height) to the rendered
                        // avatar so different avatars have visibly different
                        // statures. Drives the avatar root's Z scale.
                        if (::renderManager.isInitialized && data.visualParams.isNotEmpty()) {
                            val heightByte = data.visualParams.firstOrNull()?.toInt()?.and(0xFF) ?: 128
                            val heightScale = 0.85f + (heightByte / 255f) * (1.20f - 0.85f)
                            renderManager.dispatcher.post(Runnable {
                                renderManager.getSceneManager()?.setAvatarHeightScale(data.senderID, heightScale)
                            })

                            // Full body-shape morphing. Lazily populate the
                            // VisualParam table from avatar_lad.xml on the
                            // first AvatarAppearance, then drive system
                            // avatar mesh morphs.
                            if (!com.linkpoint.avatar.VisualParamLoader.isReady) {
                                com.linkpoint.avatar.VisualParamLoader.load(applicationContext)
                            }
                            val morphParams = com.linkpoint.avatar.VisualParamLoader.allMorphParams()
                            val skeletonParams = com.linkpoint.avatar.VisualParamLoader.allSkeletonParams()
                            val colorParams = com.linkpoint.avatar.VisualParamLoader.allColorParams()
                            val visualParamsCopy = data.visualParams.copyOf()

                            if (morphParams.isNotEmpty()) {
                                renderManager.dispatcher.post(Runnable {
                                    renderManager.getSceneManager()?.applyAvatarMorphs(
                                        data.senderID, visualParamsCopy, morphParams
                                    )
                                })
                            }

                            // Skeleton params: per-bone scale offsets that
                            // accumulate onto the AvatarSkeleton's rest pose.
                            // Affects mesh deformation through the GPU
                            // skinning path on the next frame.
                            if (skeletonParams.isNotEmpty() && ::avatarManager.isInitialized) {
                                val a = avatarManager.getAvatar(data.senderID)
                                a?.skeleton?.applySkeletonParams(visualParamsCopy, skeletonParams)
                            }

                            // Color params: blend a palette by param weight to
                            // produce a per-channel tint (skin colour, hair
                            // colour, etc.). For now we just pull skin (the
                            // wearable="skin" subset) and feed it as a
                            // baseColor tint on the avatar material — which
                            // is the most-visible LL bake-time tint.
                            if (colorParams.isNotEmpty()) {
                                val skinTint = blendSkinColorParams(visualParamsCopy, colorParams)
                                if (skinTint != null) {
                                    renderManager.dispatcher.post(Runnable {
                                        renderManager.getSceneManager()?.setAvatarSkinTint(
                                            data.senderID,
                                            skinTint[0], skinTint[1], skinTint[2], skinTint[3]
                                        )
                                    })
                                }
                            }
                        }
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
                        // Feed legacy names into FriendsManager so the friends
                        // list stops showing `Resident (xxxx)` placeholders
                        // when GetDisplayNames cap silently fails (the 2026-04-25
                        // Athanasia capture). Capability path remains primary;
                        // this is the UDP fallback wired by sendUUIDNameRequest.
                        val friendsReady = ::friendsManager.isInitialized
                        data.entries.forEach { entry ->
                            val resolved = "${entry.firstName} ${entry.lastName}".trim()
                            if (resolved.isNotBlank() && friendsReady) {
                                friendsManager.updateFriendName(entry.id, resolved)
                            }
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
                    xferManager.handleSendXferPacket(payload)
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
                com.linkpoint.protocol.messages.DeclaredMessageSlices.handle(
                    com.linkpoint.protocol.messages.MessageIds.AGENT_PAUSE,
                    rawPacket
                ) { summary -> Log.d(TAG, "⏸️ $summary") }
            } catch (e: Exception) { Log.e(TAG, "Error handling AgentPause", e) }
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.AGENT_RESUME) { _, rawPacket ->
            try {
                com.linkpoint.protocol.messages.DeclaredMessageSlices.handle(
                    com.linkpoint.protocol.messages.MessageIds.AGENT_RESUME,
                    rawPacket
                ) { summary -> Log.d(TAG, "▶️ $summary") }
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
                com.linkpoint.protocol.messages.DeclaredMessageSlices.handle(
                    com.linkpoint.protocol.messages.MessageIds.FETCH_INVENTORY_DESCENDENTS,
                    rawPacket
                ) { summary -> Log.d(TAG, "📦 $summary") }
            } catch (e: Exception) { Log.e(TAG, "Error handling FetchInventoryDescendents", e) }
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.FETCH_INVENTORY) { _, rawPacket ->
            try {
                com.linkpoint.protocol.messages.DeclaredMessageSlices.handle(
                    com.linkpoint.protocol.messages.MessageIds.FETCH_INVENTORY,
                    rawPacket
                ) { summary -> Log.d(TAG, "📦 $summary") }
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
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null && ::objectManager.isInitialized) {
                    objectManager.handleObjectScaleUpdate(payload)
                }
            } catch (e: Exception) { Log.e(TAG, "Error handling ObjectScale", e) }
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.OBJECT_ROTATION) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null && ::objectManager.isInitialized) {
                    objectManager.handleObjectRotationUpdate(payload)
                }
            } catch (e: Exception) { Log.e(TAG, "Error handling ObjectRotation", e) }
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.OBJECT_POSITION) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null && ::objectManager.isInitialized) {
                    objectManager.handleObjectPositionUpdate(payload)
                }
            } catch (e: Exception) { Log.e(TAG, "Error handling ObjectPosition", e) }
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.OBJECT_FLAG_UPDATE) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null && ::objectManager.isInitialized) {
                    objectManager.handleObjectFlagUpdate(payload)
                }
            } catch (e: Exception) { Log.e(TAG, "Error handling ObjectFlagUpdate", e) }
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.OBJECT_CLICK_ACTION) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null) Log.d(TAG, "📦 ObjectClickAction (${payload.size} bytes)")
            } catch (e: Exception) { Log.e(TAG, "Error handling ObjectClickAction", e) }
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.OBJECT_IMAGE) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null) Log.d(TAG, "📦 ObjectImage (${payload.size} bytes)")
            } catch (e: Exception) { Log.e(TAG, "Error handling ObjectImage", e) }
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.OBJECT_MATERIAL) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null) Log.d(TAG, "📦 ObjectMaterial (${payload.size} bytes)")
            } catch (e: Exception) { Log.e(TAG, "Error handling ObjectMaterial", e) }
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.OBJECT_SHAPE) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null) Log.d(TAG, "📦 ObjectShape (${payload.size} bytes)")
            } catch (e: Exception) { Log.e(TAG, "Error handling ObjectShape", e) }
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.OBJECT_OWNER) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null) Log.d(TAG, "📦 ObjectOwner (${payload.size} bytes)")
            } catch (e: Exception) { Log.e(TAG, "Error handling ObjectOwner", e) }
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.OBJECT_GROUP) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null) Log.d(TAG, "📦 ObjectGroup (${payload.size} bytes)")
            } catch (e: Exception) { Log.e(TAG, "Error handling ObjectGroup", e) }
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.OBJECT_BUY) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null) Log.d(TAG, "💰 ObjectBuy (${payload.size} bytes)")
            } catch (e: Exception) { Log.e(TAG, "Error handling ObjectBuy", e) }
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.OBJECT_PERMISSIONS) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null) Log.d(TAG, "🔒 ObjectPermissions (${payload.size} bytes)")
            } catch (e: Exception) { Log.e(TAG, "Error handling ObjectPermissions", e) }
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.OBJECT_SALE_INFO) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null) Log.d(TAG, "💰 ObjectSaleInfo (${payload.size} bytes)")
            } catch (e: Exception) { Log.e(TAG, "Error handling ObjectSaleInfo", e) }
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.OBJECT_DESELECT) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null) Log.d(TAG, "📦 ObjectDeselect (${payload.size} bytes)")
            } catch (e: Exception) { Log.e(TAG, "Error handling ObjectDeselect", e) }
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.OBJECT_ATTACH) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null) Log.d(TAG, "📦 ObjectAttach (${payload.size} bytes)")
            } catch (e: Exception) { Log.e(TAG, "Error handling ObjectAttach", e) }
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.OBJECT_DETACH) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null) Log.d(TAG, "📦 ObjectDetach (${payload.size} bytes)")
            } catch (e: Exception) { Log.e(TAG, "Error handling ObjectDetach", e) }
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.OBJECT_DROP) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null) Log.d(TAG, "📦 ObjectDrop (${payload.size} bytes)")
            } catch (e: Exception) { Log.e(TAG, "Error handling ObjectDrop", e) }
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.OBJECT_SPIN_START) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null) Log.d(TAG, "📦 ObjectSpinStart (${payload.size} bytes)")
            } catch (e: Exception) { Log.e(TAG, "Error handling ObjectSpinStart", e) }
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.OBJECT_SPIN_UPDATE) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null) Log.d(TAG, "📦 ObjectSpinUpdate (${payload.size} bytes)")
            } catch (e: Exception) { Log.e(TAG, "Error handling ObjectSpinUpdate", e) }
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.OBJECT_SPIN_STOP) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null) Log.d(TAG, "📦 ObjectSpinStop (${payload.size} bytes)")
            } catch (e: Exception) { Log.e(TAG, "Error handling ObjectSpinStop", e) }
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.OBJECT_GRAB_UPDATE) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null) Log.d(TAG, "📦 ObjectGrabUpdate (${payload.size} bytes)")
            } catch (e: Exception) { Log.e(TAG, "Error handling ObjectGrabUpdate", e) }
        }
        
        // --- Land/Terrain Messages ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.MODIFY_LAND) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null) Log.d(TAG, "🏔️ ModifyLand (${payload.size} bytes)")
            } catch (e: Exception) { 
                Log.e(TAG, "Error handling ModifyLand", e) 
            }
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
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null) Log.d(TAG, "🌍 RequestRegionInfo (${payload.size} bytes)")
            } catch (e: Exception) { Log.e(TAG, "Error handling RequestRegionInfo", e) }
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.SIMULATOR_VIEWER_TIME_MESSAGE) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null) {
                    // Parse time of day for environment effects
                    val buffer = java.nio.ByteBuffer.wrap(payload).order(java.nio.ByteOrder.LITTLE_ENDIAN)
                    if (buffer.remaining() >= 24) {
                        val sunPhase = buffer.float
                        val sunDirection = com.linkpoint.protocol.types.LLVector3(buffer.float, buffer.float, buffer.float)
                        Log.d(TAG, "🕐 SimulatorViewerTimeMessage: sunPhase=$sunPhase")
                    }
                }
            } catch (e: Exception) { Log.e(TAG, "Error handling SimulatorViewerTimeMessage", e) }
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.TELEPORT_LOCAL) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null && ::teleportManager.isInitialized) {
                    teleportManager.handleTeleportLocal(payload)
                }
            } catch (e: Exception) { Log.e(TAG, "Error handling TeleportLocal", e) }
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.TELEPORT_CANCEL) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null && ::teleportManager.isInitialized) {
                    teleportManager.handleTeleportCancel()
                }
            } catch (e: Exception) { Log.e(TAG, "Error handling TeleportCancel", e) }
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.TELEPORT_REQUEST) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null) Log.d(TAG, "🚀 TeleportRequest (${payload.size} bytes)")
            } catch (e: Exception) { Log.e(TAG, "Error handling TeleportRequest", e) }
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.SIM_CRASHED) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null) {
                    Log.e(TAG, "💥 SimCrashed! Region has crashed (${payload.size} bytes)")
                    // Notify session manager
                    if (::sessionManager.isInitialized) {
                        sessionManager.setConnectionState(com.linkpoint.core.ConnectionState.ERROR)
                    }
                }
            } catch (e: Exception) { Log.e(TAG, "Error handling SimCrashed", e) }
        }
        
        // --- Map Messages (Extended) ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.MAP_BLOCK_REQUEST) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null) Log.d(TAG, "🗺️ MapBlockRequest (${payload.size} bytes)")
            } catch (e: Exception) { Log.e(TAG, "Error handling MapBlockRequest", e) }
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.MAP_NAME_REQUEST) { _, rawPacket ->
            try {
                com.linkpoint.protocol.messages.DeclaredMessageSlices.handle(
                    com.linkpoint.protocol.messages.MessageIds.MAP_NAME_REQUEST,
                    rawPacket
                ) { summary -> Log.d(TAG, "🗺️ $summary") }
            } catch (e: Exception) { Log.e(TAG, "Error handling MapNameRequest", e) }
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.MAP_LAYER_REQUEST) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null) Log.d(TAG, "🗺️ MapLayerRequest (${payload.size} bytes)")
            } catch (e: Exception) { Log.e(TAG, "Error handling MapLayerRequest", e) }
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.MAP_ITEM_REQUEST) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null) Log.d(TAG, "🗺️ MapItemRequest (${payload.size} bytes)")
            } catch (e: Exception) { Log.e(TAG, "Error handling MapItemRequest", e) }
        }
        
        // --- Mute Messages ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.MUTE_LIST_REQUEST) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null) Log.d(TAG, "🔇 MuteListRequest (${payload.size} bytes)")
            } catch (e: Exception) { Log.e(TAG, "Error handling MuteListRequest", e) }
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.UPDATE_MUTE_LIST_ENTRY) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null && ::muteManager.isInitialized) {
                    muteManager.handleMuteListUpdate(payload)
                }
            } catch (e: Exception) { Log.e(TAG, "Error handling UpdateMuteListEntry", e) }
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.REMOVE_MUTE_LIST_ENTRY) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null && ::muteManager.isInitialized) {
                    muteManager.handleMuteEntryRemoved(payload)
                }
            } catch (e: Exception) { Log.e(TAG, "Error handling RemoveMuteListEntry", e) }
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.MUTE_LIST_UPDATE) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null && ::muteManager.isInitialized) {
                    muteManager.handleMuteListRefresh(payload)
                }
            } catch (e: Exception) { Log.e(TAG, "Error handling MuteListUpdate", e) }
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.USE_CACHED_MUTE_LIST) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null) Log.d(TAG, "🔇 UseCachedMuteList (${payload.size} bytes)")
            } catch (e: Exception) { Log.e(TAG, "Error handling UseCachedMuteList", e) }
        }
        
        // --- User Info Messages ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.USER_INFO_REQUEST) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null) Log.d(TAG, "👤 UserInfoRequest (${payload.size} bytes)")
            } catch (e: Exception) { Log.e(TAG, "Error handling UserInfoRequest", e) }
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.USER_INFO_REPLY) { _, rawPacket ->
            try {
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                if (payload != null) {
                    val data = com.linkpoint.protocol.messages.AdditionalMessageParsers.parseUserInfoReply(payload)
                    if (data != null) {
                        Log.d(TAG, "👤 UserInfoReply: imVia=${data.imViaEmail}, directoryVisibility=${data.directoryVisibility}")
                    }
                }
            } catch (e: Exception) { Log.e(TAG, "Error handling UserInfoReply", e) }
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
            com.linkpoint.protocol.messages.DeclaredMessageSlices.handle(
                com.linkpoint.protocol.messages.MessageIds.DIR_FIND_QUERY,
                rawPacket
            ) { summary -> Log.d(TAG, "🔍 $summary") }
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
            com.linkpoint.protocol.messages.DeclaredMessageSlices.handle(
                com.linkpoint.protocol.messages.MessageIds.GROUP_TITLES_REQUEST,
                rawPacket
            ) { summary -> Log.d(TAG, "👥 $summary") }
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
        
        // Register additional message handlers (split for Kotlin compiler)
        registerLateMessageHandlers()
        registerOutboundOnlyMessageHandlers()

        
        // Mark handlers as ready and process any buffered packets
        // This is critical for handling packets that arrived before handlers were registered
        udpConnection.setHandlersReady()
        
        Log.i(TAG, "╔══════════════════════════════════════════════════════════════════")
        Log.i(TAG, "║ UDP MESSAGE HANDLERS REGISTERED: ${udpConnection.getRegisteredHandlerCount()}")
        Log.i(TAG, "║ Handlers: ${udpConnection.getRegisteredHandlerIds().joinToString(", ")}")
        Log.i(TAG, "╚══════════════════════════════════════════════════════════════════")
    }

    /**
     * Register no-op handlers for messages we currently only send outbound.
     * This makes parity decisions explicit and gives diagnostics if simulators send these unexpectedly.
     */
    private fun registerOutboundOnlyMessageHandlers() {
        val outboundOnlyIds = listOf(
            com.linkpoint.protocol.messages.MessageIds.MOVE_INVENTORY_ITEM,
            com.linkpoint.protocol.messages.MessageIds.UPDATE_TASK_INVENTORY,
            com.linkpoint.protocol.messages.MessageIds.PARCEL_BUY,
            com.linkpoint.protocol.messages.MessageIds.OBJECT_GRAB,
            com.linkpoint.protocol.messages.MessageIds.OBJECT_DEGRAB,
            com.linkpoint.protocol.messages.MessageIds.KICK_USER,
            com.linkpoint.protocol.messages.MessageIds.UPDATE_USER_INFO,
            com.linkpoint.protocol.messages.MessageIds.FIND_AGENT
        )

        outboundOnlyIds.forEach { messageId ->
            udpConnection.registerHandler(messageId) { _, rawPacket ->
                val payload = com.linkpoint.protocol.messages.MessageParser.extractPayload(rawPacket)
                Log.w(
                    TAG,
                    "⚠️ Received outbound-only message id=$messageId payloadBytes=${payload?.size ?: 0}; no inbound parser wired."
                )
            }
        }
    }
    
    /**
     * Register late message handlers (split from registerMessageHandlers for Kotlin compiler)
     * This function was extracted because the original function exceeded Kotlin's internal
     * compiler limits for lambda type inference.
     */
    private fun registerLateMessageHandlers() {
        // --- Autopilot Messages ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.AUTOPILOT) { _: Int, rawPacket: ByteArray ->
            Log.d(TAG, "🚗 Autopilot received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.AUTOPILOT_CANCEL) { _: Int, rawPacket: ByteArray ->
            Log.d(TAG, "🚗 AutopilotCancel received")
        }
        
        // --- Terrain Messages ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.TERRAIN_HEIGHT_DATA) { _: Int, rawPacket: ByteArray ->
            Log.d(TAG, "🏔️ TerrainHeightData received")
        }
        
        // --- God Mode Messages ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.GOD_KICK_USER) { _: Int, rawPacket: ByteArray ->
            Log.d(TAG, "👑 GodKickUser received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.GODLIKE_MESSAGE) { _: Int, rawPacket: ByteArray ->
            Log.d(TAG, "👑 GodlikeMessage received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.GOD_UPDATE_REGION_INFO) { _: Int, rawPacket: ByteArray ->
            Log.d(TAG, "👑 GodUpdateRegionInfo received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.GOD_DELETE_SIM) { _: Int, rawPacket: ByteArray ->
            Log.d(TAG, "👑 GodDeleteSim received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.REQUEST_GODLIKE_POWERS) { _: Int, rawPacket: ByteArray ->
            Log.d(TAG, "👑 RequestGodlikePowers received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.GRANT_GODLIKE_POWERS) { _: Int, rawPacket: ByteArray ->
            Log.d(TAG, "👑 GrantGodlikePowers received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.SIM_OWNER_REQUEST) { _: Int, rawPacket: ByteArray ->
            Log.d(TAG, "🏠 SimOwnerRequest received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.SIM_OWNER_RESPONSE) { _: Int, rawPacket: ByteArray ->
            Log.d(TAG, "🏠 SimOwnerResponse received")
        }
        
        // --- Estate Manager Messages ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.ESTATE_OWNER_MESSAGE) { _: Int, rawPacket: ByteArray ->
            Log.d(TAG, "🏰 EstateOwnerMessage received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.ESTATE_CHANGE_INFO) { _: Int, rawPacket: ByteArray ->
            Log.d(TAG, "🏰 EstateChangeInfo received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.ESTATE_EXPERIENCE_REPLY) { _: Int, rawPacket: ByteArray ->
            Log.d(TAG, "🏰 EstateExperienceReply received")
        }
        
        // --- Land Bank Messages ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.LAND_BUY) { _: Int, rawPacket: ByteArray ->
            Log.d(TAG, "🏠 LandBuy received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.LAND_BUY_PASS) { _: Int, rawPacket: ByteArray ->
            Log.d(TAG, "🏠 LandBuyPass received")
        }
        
        // --- Asset/Transfer Messages Extended ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.ASSET_INFO_REQUEST) { _: Int, rawPacket: ByteArray ->
            Log.d(TAG, "📁 AssetInfoRequest received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.ASSET_INFO_RESPONSE) { _: Int, rawPacket: ByteArray ->
            Log.d(TAG, "📁 AssetInfoResponse received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.MAP_LAYER_REQUEST_MSG) { _: Int, rawPacket: ByteArray ->
            Log.d(TAG, "🗺️ MapLayerRequest received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.MAP_LAYER_REPLY_MSG) { _: Int, rawPacket: ByteArray ->
            Log.d(TAG, "🗺️ MapLayerReply received")
        }
        
        // --- Agent Data Messages ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.AGENT_DATA_UPDATE_REQUEST) { _: Int, rawPacket: ByteArray ->
            Log.d(TAG, "👤 AgentDataUpdateRequest received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.AGENT_DATA_UPDATE_MSG) { _: Int, rawPacket: ByteArray ->
            Log.d(TAG, "👤 AgentDataUpdate received")
        }
        
        // --- Pick/Classified Messages Extended ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.PICK_DELETE) { _: Int, rawPacket: ByteArray ->
            Log.d(TAG, "📍 PickDelete received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.PICK_UPDATE_INFO) { _: Int, rawPacket: ByteArray ->
            Log.d(TAG, "📍 PickUpdateInfo received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.CLASSIFIED_DELETE) { _: Int, rawPacket: ByteArray ->
            Log.d(TAG, "📰 ClassifiedDelete received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.CLASSIFIED_INFO_UPDATE) { _: Int, rawPacket: ByteArray ->
            Log.d(TAG, "📰 ClassifiedInfoUpdate received")
        }
        
        // --- Interest List Messages ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.INTEREST_LIST_REQUEST) { _: Int, rawPacket: ByteArray ->
            Log.d(TAG, "📋 InterestListRequest received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.INTEREST_LIST_REPLY) { _: Int, rawPacket: ByteArray ->
            Log.d(TAG, "📋 InterestListReply received")
        }
        
        // --- Object Export Messages ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.EXPORT_DYNA_FILE) { _: Int, rawPacket: ByteArray ->
            Log.d(TAG, "📤 ExportDynaFile received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.EXPORT_DYNA_FILE_REQUEST) { _: Int, rawPacket: ByteArray ->
            Log.d(TAG, "📤 ExportDynaFileRequest received")
        }
        
        // --- Upload Messages ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.UPLOAD_BAKED_TEXTURE) { _: Int, rawPacket: ByteArray ->
            Log.d(TAG, "📤 UploadBakedTexture received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.UPLOAD_BAKED_TEXTURE_RESULT) { _: Int, rawPacket: ByteArray ->
            Log.d(TAG, "📤 UploadBakedTextureResult received")
        }
        
        // --- Object Permission Messages ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.OBJECT_PERMISSIONS_REQUEST) { _: Int, rawPacket: ByteArray ->
            Log.d(TAG, "🔐 ObjectPermissionsRequest received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.OBJECT_PERMISSIONS_REPLY) { _: Int, rawPacket: ByteArray ->
            Log.d(TAG, "🔐 ObjectPermissionsReply received")
        }
        
        // --- Agent Camera Messages ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.AGENT_CAMERA_CONSTRAINT) { _: Int, rawPacket: ByteArray ->
            Log.d(TAG, "📷 AgentCameraConstraint received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.CAMERA_CONSTRAINT_MSG) { _: Int, rawPacket: ByteArray ->
            Log.d(TAG, "📷 CameraConstraintMsg received")
        }
        
        // --- Voice Messages ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.PROVISION_VOICE_ACCOUNT_REQUEST) { _: Int, rawPacket: ByteArray ->
            Log.d(TAG, "🎤 ProvisionVoiceAccountRequest received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.PROVISION_VOICE_ACCOUNT_REPLY) { _: Int, rawPacket: ByteArray ->
            Log.d(TAG, "🎤 ProvisionVoiceAccountReply received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.PARCEL_VOICE_INFO_REQUEST) { _: Int, rawPacket: ByteArray ->
            Log.d(TAG, "🎤 ParcelVoiceInfoRequest received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.PARCEL_VOICE_INFO_REPLY) { _: Int, rawPacket: ByteArray ->
            Log.d(TAG, "🎤 ParcelVoiceInfoReply received")
        }
        
        // --- Experience Messages ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.EXPERIENCE_INFO_REQUEST) { _: Int, rawPacket: ByteArray ->
            Log.d(TAG, "✨ ExperienceInfoRequest received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.EXPERIENCE_INFO_REPLY) { _: Int, rawPacket: ByteArray ->
            Log.d(TAG, "✨ ExperienceInfoReply received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.EXPERIENCE_PERMISSION_REQUEST) { _: Int, rawPacket: ByteArray ->
            Log.d(TAG, "✨ ExperiencePermissionRequest received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.EXPERIENCE_PERMISSION_REPLY) { _: Int, rawPacket: ByteArray ->
            Log.d(TAG, "✨ ExperiencePermissionReply received")
        }
        
        // --- Region Object Messages ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.REGION_OBJECT_UPDATE) { _: Int, rawPacket: ByteArray ->
            Log.d(TAG, "🌍 RegionObjectUpdate received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.REGION_OBJECT_COMPLETE) { _: Int, rawPacket: ByteArray ->
            Log.d(TAG, "🌍 RegionObjectComplete received")
        }
        
        // --- Pathfinding Messages ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.NAV_MESH_STATUS_REQUEST) { _: Int, rawPacket: ByteArray ->
            Log.d(TAG, "🧭 NavMeshStatusRequest received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.NAV_MESH_STATUS_REPLY) { _: Int, rawPacket: ByteArray ->
            Log.d(TAG, "🧭 NavMeshStatusReply received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.CHARACTER_PROPERTIES_REQUEST) { _: Int, rawPacket: ByteArray ->
            Log.d(TAG, "🧭 CharacterPropertiesRequest received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.CHARACTER_PROPERTIES_REPLY) { _: Int, rawPacket: ByteArray ->
            Log.d(TAG, "🧭 CharacterPropertiesReply received")
        }
        
        // --- AO (Animation Override) Messages ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.AGENT_ANIMATION_OVERRIDE) { _: Int, rawPacket: ByteArray ->
            Log.d(TAG, "💃 AgentAnimationOverride received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.CLEAR_ANIMATION_OVERRIDE) { _: Int, rawPacket: ByteArray ->
            Log.d(TAG, "💃 ClearAnimationOverride received")
        }
        
        // =====================================
        // Phase 6: ALL REMAINING HANDLERS
        // =====================================
        
        // --- Agent Extended ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.AGENT_THROTTLE) { _: Int, rawPacket: ByteArray ->
            Log.d(TAG, "🎛️ AgentThrottle received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.AGENT_IS_NOW_WEARING) { _: Int, rawPacket: ByteArray ->
            Log.d(TAG, "👗 AgentIsNowWearing received")
        }
        
        // --- Avatar Request/Backend ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.AVATAR_PICKER_REQUEST) { _: Int, rawPacket: ByteArray ->
            Log.d(TAG, "🔍 AvatarPickerRequest received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.AVATAR_PROPERTIES_REQUEST) { _: Int, rawPacket: ByteArray ->
            Log.d(TAG, "📋 AvatarPropertiesRequest received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.AVATAR_TEXTURE_UPDATE) { _: Int, rawPacket: ByteArray ->
            Log.d(TAG, "🖼️ AvatarTextureUpdate received")
        }
        
        // --- Buy/Economy ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.BUY_OBJECT_INVENTORY) { _: Int, rawPacket: ByteArray ->
            Log.d(TAG, "🛒 BuyObjectInventory received")
        }
        
        // --- Chat Extended ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.CHAT_EVENT) { _: Int, rawPacket: ByteArray ->
            Log.d(TAG, "💬 ChatEvent received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.CHAT_FROM_VIEWER) { _: Int, rawPacket: ByteArray ->
            Log.d(TAG, "💬 ChatFromViewer received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.CHAT_PASS) { _: Int, rawPacket: ByteArray ->
            Log.d(TAG, "💬 ChatPass received")
        }
        
        // --- Circuit Extended ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.CIRCUIT_READY) { _: Int, rawPacket: ByteArray ->
            Log.d(TAG, "⚡ CircuitReady received")
        }
        
        // --- Classified Extended ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.CLASSIFIED_GOD_DELETE) { _: Int, rawPacket: ByteArray ->
            Log.d(TAG, "📰 ClassifiedGodDelete received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.CLASSIFIED_INFO_REQUEST) { _: Int, rawPacket: ByteArray ->
            Log.d(TAG, "📰 ClassifiedInfoRequest received")
        }
        
        // --- Agent Movement Extended ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.COMPLETE_AGENT_MOVEMENT) { _: Int, rawPacket: ByteArray ->
            Log.d(TAG, "🚶 CompleteAgentMovement received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.COMPLETE_PING_CHECK) { _: Int, rawPacket: ByteArray ->
            Log.d(TAG, "📡 CompletePingCheck received")
            if (::connectionKeepAlive.isInitialized) {
                connectionKeepAlive.onPongReceived()
            }
        }
        
        // --- Inventory Copy ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.COPY_INVENTORY_FROM_NOTECARD) { _: Int, rawPacket: ByteArray ->
            Log.d(TAG, "📄 CopyInventoryFromNotecard received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.COPY_INVENTORY_ITEM) { _: Int, rawPacket: ByteArray ->
            Log.d(TAG, "📁 CopyInventoryItem received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.CREATE_INVENTORY_FOLDER) { _: Int, rawPacket: ByteArray ->
            Log.d(TAG, "📁 CreateInventoryFolder received")
        }
        
        // --- Data Home Location ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.DATA_HOME_LOCATION_REQUEST) { _: Int, rawPacket: ByteArray ->
            Log.d(TAG, "🏠 DataHomeLocationRequest received")
        }
        
        // --- Economy Request ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.ECONOMY_DATA_REQUEST) { _: Int, rawPacket: ByteArray ->
            Log.d(TAG, "💰 EconomyDataRequest received")
        }
        
        // --- User Management ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.EJECT_USER) { _: Int, rawPacket: ByteArray ->
            Log.d(TAG, "🚪 EjectUser received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.FREEZE_USER) { _: Int, rawPacket: ByteArray ->
            Log.d(TAG, "🥶 FreezeUser received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.KICK_USER_ACK) { _: Int, rawPacket: ByteArray ->
            Log.d(TAG, "🚪 KickUserAck received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.SYSTEM_KICK_USER) { _: Int, rawPacket: ByteArray ->
            Log.d(TAG, "🚪 SystemKickUser received")
        }
        
        // --- Event Extended ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.EVENT_INFO_REQUEST) { _: Int, rawPacket: ByteArray ->
            Log.d(TAG, "📅 EventInfoRequest received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.EVENT_NOTIFICATION_ADD_REQUEST) { _: Int, rawPacket: ByteArray ->
            Log.d(TAG, "📅 EventNotificationAddRequest received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.EVENT_NOTIFICATION_REMOVE_REQUEST) { _: Int, rawPacket: ByteArray ->
            Log.d(TAG, "📅 EventNotificationRemoveRequest received")
        }
        
        // --- Script Extended ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.GET_SCRIPT_RUNNING) { _: Int, rawPacket: ByteArray ->
            Log.d(TAG, "📜 GetScriptRunning received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.SCRIPT_DIALOG_REPLY) { _: Int, rawPacket: ByteArray ->
            Log.d(TAG, "📜 ScriptDialogReply received")
        }
        
        // --- Global Options ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.GLOBAL_OPTIONS_CHANGE) { _: Int, rawPacket: ByteArray ->
            Log.d(TAG, "⚙️ GlobalOptionsChange received")
        }
        
        // --- IM ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.IMPROVED_INSTANT_MESSAGE) { _: Int, rawPacket: ByteArray ->
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
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.LIVE_HELP_GROUP_REPLY) { _: Int, rawPacket: ByteArray ->
            Log.d(TAG, "❓ LiveHelpGroupReply received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.LIVE_HELP_GROUP_REQUEST) { _: Int, rawPacket: ByteArray ->
            Log.d(TAG, "❓ LiveHelpGroupRequest received")
        }
        
        // --- Logout ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.LOGOUT_REQUEST) { _: Int, rawPacket: ByteArray ->
            Log.d(TAG, "🚪 LogoutRequest received")
        }
        
        // --- Money Extended ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.MONEY_BALANCE_REQUEST) { _: Int, rawPacket: ByteArray ->
            Log.d(TAG, "💰 MoneyBalanceRequest received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.MONEY_TRANSFER_BACKEND) { _: Int, rawPacket: ByteArray ->
            Log.d(TAG, "💰 MoneyTransferBackend received")
        }
        
        // --- Inventory Move ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.MOVE_TASK_INVENTORY) { _: Int, rawPacket: ByteArray ->
            Log.d(TAG, "📦 MoveTaskInventory received")
        }
        
        // --- Landing Region ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.NEAREST_LANDING_REGION_REPLY) { _: Int, rawPacket: ByteArray ->
            Log.d(TAG, "🛬 NearestLandingRegionReply received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.NEAREST_LANDING_REGION_REQUEST) { _: Int, rawPacket: ByteArray ->
            Log.d(TAG, "🛬 NearestLandingRegionRequest received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.NEAREST_LANDING_REGION_UPDATED) { _: Int, rawPacket: ByteArray ->
            Log.d(TAG, "🛬 NearestLandingRegionUpdated received")
        }
        
        // --- Object Extended ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.OBJECT_DELETE) { _: Int, rawPacket: ByteArray ->
            Log.d(TAG, "🗑️ ObjectDelete received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.OBJECT_DUPLICATE_ON_RAY) { _: Int, rawPacket: ByteArray ->
            Log.d(TAG, "📋 ObjectDuplicateOnRay received")
        }
        
        // --- Parcel Extended ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.PARCEL_ACCESS_LIST_UPDATE) { _: Int, rawPacket: ByteArray ->
            Log.d(TAG, "🏘️ ParcelAccessListUpdate received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.PARCEL_DEED_TO_GROUP) { _: Int, rawPacket: ByteArray ->
            Log.d(TAG, "🏘️ ParcelDeedToGroup received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.PARCEL_PROPERTIES_UPDATE) { _: Int, rawPacket: ByteArray ->
            Log.d(TAG, "🏘️ ParcelPropertiesUpdate received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.PARCEL_RELEASE) { _: Int, rawPacket: ByteArray ->
            Log.d(TAG, "🏘️ ParcelRelease received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.PARCEL_RETURN_OBJECTS) { _: Int, rawPacket: ByteArray ->
            Log.d(TAG, "🏘️ ParcelReturnObjects received")
        }
        
        // --- Pick Extended ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.PICK_GOD_DELETE) { _: Int, rawPacket: ByteArray ->
            Log.d(TAG, "📌 PickGodDelete received")
        }
        
        // --- Inventory Purge ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.PURGE_INVENTORY_DESCENDENTS) { _: Int, rawPacket: ByteArray ->
            Log.d(TAG, "📁 PurgeInventoryDescendents received")
        }
        
        // --- Region Handshake ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.REGION_HANDSHAKE_REPLY) { _: Int, rawPacket: ByteArray ->
            Log.d(TAG, "🌍 RegionHandshakeReply received")
        }
        
        // --- Inventory Remove ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.REMOVE_INVENTORY_OBJECTS) { _: Int, rawPacket: ByteArray ->
            Log.d(TAG, "📁 RemoveInventoryObjects received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.REMOVE_TASK_INVENTORY) { _: Int, rawPacket: ByteArray ->
            Log.d(TAG, "📦 RemoveTaskInventory received")
        }
        
        // --- Pay Price ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.REQUEST_PAY_PRICE) { _: Int, rawPacket: ByteArray ->
            com.linkpoint.protocol.messages.DeclaredMessageSlices.handle(
                com.linkpoint.protocol.messages.MessageIds.REQUEST_PAY_PRICE,
                rawPacket
            ) { summary -> Log.d(TAG, "💰 $summary") }
        }
        
        // --- Rez Extended ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.REZ_SINGLE_ATTACHMENT_FROM_INV) { _: Int, rawPacket: ByteArray ->
            Log.d(TAG, "📎 RezSingleAttachmentFromInv received")
        }
        
        // --- Start Location ---
        // Note: SET_START_LOCATION message ID doesn't exist, only SET_START_LOCATION_REQUEST
        // udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.SET_START_LOCATION) { _: Int, rawPacket: ByteArray ->
        //     Log.d(TAG, "🏠 SetStartLocation received")
        // }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.SET_START_LOCATION_REQUEST) { _: Int, rawPacket: ByteArray ->
            Log.d(TAG, "🏠 SetStartLocationRequest received")
        }
        
        // --- Teleport Lure ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.START_LURE) { _: Int, rawPacket: ByteArray ->
            Log.d(TAG, "🌀 StartLure received")
        }
        
        // --- Voting ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.TALLY_VOTES) { _: Int, rawPacket: ByteArray ->
            Log.d(TAG, "🗳️ TallyVotes received")
        }
        
        // --- Teleport Extended ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.TELEPORT_LANDMARK_REQUEST) { _: Int, rawPacket: ByteArray ->
            Log.d(TAG, "🌀 TeleportLandmarkRequest received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.TELEPORT_LOCATION_REQUEST) { _: Int, rawPacket: ByteArray ->
            Log.d(TAG, "🌀 TeleportLocationRequest received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.TELEPORT_LURE_REQUEST) { _: Int, rawPacket: ByteArray ->
            Log.d(TAG, "🌀 TeleportLureRequest received")
        }
        
        // --- Inventory Update ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.UPDATE_INVENTORY_ITEM) { _: Int, rawPacket: ByteArray ->
            Log.d(TAG, "📁 UpdateInventoryItem received")
        }
        
        // --- Circuit Code ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.USE_CIRCUIT_CODE) { _: Int, rawPacket: ByteArray ->
            Log.d(TAG, "⚡ UseCircuitCode received")
        }
        
        // --- UUID Request ---
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.UUID_NAME_REQUEST) { _: Int, rawPacket: ByteArray ->
            Log.d(TAG, "🔍 UUIDNameRequest received")
        }
        
        udpConnection.registerHandler(com.linkpoint.protocol.messages.MessageIds.UUID_GROUP_NAME_REQUEST) { _: Int, rawPacket: ByteArray ->
            Log.d(TAG, "🔍 UUIDGroupNameRequest received")
        }
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
        EventQueueDispatcher.shutdown()
        
        // Reset connection state tracking
        completeAgentMovementSent.set(false)
        
        udpConnection.disconnect()
        
        xrManager.shutdown()
        renderManager.shutdownOnRenderThread()
        
        // Shutdown protocol and networking
        protocol.shutdown()
        CircuitDispatcher.shutdown()
        
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

    fun isXREntryAvailable(): Boolean = xrManager.isUiEntryAvailable()
    
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
     * Combine all `wearable="skin"` colour-palette params into a single
     * baseColor tint by sampling each param's palette at the visual-params
     * byte and averaging the results. Returns null if no skin colour
     * params apply (avatar at default appearance).
     *
     * This is a deliberate simplification: the LL bake compositor blends
     * many colour layers across head / upper / lower / eyes channels
     * separately. Without that channel split we get a single tint that's
     * roughly "the skin colour the user picked" — visibly close to the
     * intended look on the system avatar without the full bake pipeline.
     */
    private fun blendSkinColorParams(
        visualParams: ByteArray,
        params: List<com.linkpoint.avatar.VisualParamLoader.VisualParam>
    ): FloatArray? {
        var r = 0f; var g = 0f; var b = 0f; var a = 0f; var n = 0
        val maxIdx = minOf(visualParams.size, params.size)
        for (i in 0 until maxIdx) {
            val p = params[i]
            if (p.wearable != "skin" || p.colorPalette.isEmpty()) continue
            val w = p.weightForByte(visualParams[i].toInt())
            val sample = p.sampleColor(w) ?: continue
            r += sample[0]; g += sample[1]; b += sample[2]; a += sample[3]; n++
        }
        if (n == 0) return null
        return floatArrayOf(r / n, g / n, b / n, a / n)
    }
    
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
     * Check if terrain manager is initialized (for late-binding the
     * TerrainRenderer once the render thread is up).
     */
    fun isTerrainManagerInitialized(): Boolean = ::terrainManager.isInitialized

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
     * Check if appearance manager is initialized. Distinct from outfit manager
     * because callers like the Force Refresh debug action operate on
     * AppearanceManager directly.
     */
    fun isAppearanceManagerInitialized(): Boolean = ::appearanceManager.isInitialized

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
     * Returns the public Documents/Linkpoint Logs path.
     */
    fun getSessionLogDirectoryPath(): String {
        return com.linkpoint.utils.SessionLogRecorder.getLogDirectoryPath()
    }

    fun runScenePopulationSmokeCheck(): ScenePopulationDiagnostics.SmokeCheckResult {
        val objectCount = if (::objectManager.isInitialized) objectManager.getAllObjects().size else 0
        val avatarCount = if (::avatarManager.isInitialized) avatarManager.getAllAvatars().size else 0
        return ScenePopulationDiagnostics.runSmokeCheck(objectCount, avatarCount)
    }

}
