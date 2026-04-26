package com.linkpoint.network

import android.content.Context
import android.os.Build
import android.util.Log
import com.linkpoint.auth.CrashTracker
import com.linkpoint.auth.DeviceIdentifier
import com.linkpoint.auth.MfaHashStorage
import com.linkpoint.core.ConnectionState
import com.linkpoint.core.RegionInfo
import com.linkpoint.LinkpointApp
import com.linkpoint.network.core.CoreNetworkingService
import com.linkpoint.network.core.NetworkStateManager
import com.linkpoint.network.NetworkLogger
import com.linkpoint.protocol.auth.LoginResponseParser
import com.linkpoint.protocol.llsd.LLSDArray
import com.linkpoint.protocol.llsd.LLSDMap
import com.linkpoint.protocol.llsd.LLSDReal
import com.linkpoint.protocol.llsd.LLSDString
import com.linkpoint.protocol.messages.MessageIds
import com.linkpoint.protocol.types.putUUID
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import java.security.MessageDigest
import java.util.UUID

/**
 * Second Life protocol implementation
 * Handles login, message sending, and grid communication
 * 
 * Now integrated with CoreNetworkingService for:
 * - gRPC-based networking (where applicable)
 * - Comprehensive retry logic with exponential backoff
 * - Connection quality monitoring
 * - Error count tracking and thresholds
 * - Automatic reconnection
 * - Network diagnostics
 * 
 * Enhanced with official viewer compliance:
 * - Persistent device identifiers (MAC, ID0)
 * - Crash tracking (last_exec_event)
 * - MFA hash storage
 * - Pre-hashed password support
 * 
 * Based on patterns from the official Second Life app and reference viewer compatibility.
 */
class SecondLifeProtocol(private val context: Context) {
    
    companion object {
        private const val TAG = "SLProtocol"
        // NOTE: Identifying as "Linkpoint" for grid compatibility.
        // Linkpoint is based on the reference viewer's protocol implementation.
        private const val VIEWER_NAME = "Linkpoint"
        private const val VIEWER_VERSION = "1.0.0"
    }
    
    // Reference to the LinkpointApp instance
    private val app get() = LinkpointApp.getInstance()
    
    // Core networking service with all connection management features
    private val networkingService = CoreNetworkingService(context)
    
    // Device identification (persistent across sessions) - matches official viewer behavior
    private val deviceIdentifier = DeviceIdentifier(context)
    
    // Crash tracking for last_exec_event parameter - matches official viewer behavior
    private val crashTracker = CrashTracker(context)
    
    // MFA hash storage for skipping MFA on trusted devices
    private val mfaHashStorage = MfaHashStorage(context)
    
    // Expose connection quality and state for UI
    val qualityManager get() = networkingService.qualityManager
    val stateManager get() = networkingService.stateManager
    val connectionEvents get() = networkingService.connectionEvents
    
    init {
        // Observe connection events and update app state
        LinkpointApp.getInstance().applicationScope.launch {
            networkingService.connectionEvents.collectLatest { event ->
                when (event) {
                    is CoreNetworkingService.ConnectionEvent.Connected -> {
                        Log.d(TAG, "Connection established")
                    }
                    is CoreNetworkingService.ConnectionEvent.Disconnected -> {
                        Log.d(TAG, "Connection lost")
                        LinkpointApp.getInstance().sessionManager.setConnectionState(ConnectionState.DISCONNECTED)
                    }
                    is CoreNetworkingService.ConnectionEvent.Reconnecting -> {
                        Log.d(TAG, "Reconnecting (attempt ${event.attempt}, delay ${event.delayMs}ms)")
                        LinkpointApp.getInstance().sessionManager.setConnectionState(ConnectionState.CONNECTING)
                    }
                    is CoreNetworkingService.ConnectionEvent.Error -> {
                        Log.e(TAG, "Connection error: ${event.message} [${event.code}]")
                        if (!event.recoverable) {
                            LinkpointApp.getInstance().sessionManager.setConnectionState(ConnectionState.ERROR)
                        }
                    }
                    is CoreNetworkingService.ConnectionEvent.ConnectionReset -> {
                        Log.w(TAG, "Connection reset triggered")
                    }
                    else -> {}
                }
            }
        }
    }
    
    /**
     * Record that the app has started (for crash tracking).
     * Call this from Application.onCreate().
     */
    fun recordAppStart() {
        crashTracker.recordAppStart()
    }
    
    /**
     * Record a clean shutdown (for crash tracking).
     * Call this when the user logs out properly.
     */
    fun recordCleanShutdown() {
        crashTracker.recordCleanShutdown()
    }
    
    /**
     * Get stored MFA hash for a user (to skip MFA prompt).
     */
    fun getStoredMfaHash(username: String): String? {
        return mfaHashStorage.getMfaHash(username)
    }
    
    /**
     * Store MFA hash after successful MFA verification.
     */
    fun storeMfaHash(username: String, mfaHash: String) {
        mfaHashStorage.saveMfaHash(username, mfaHash)
    }
    
    /**
     * Perform login to the grid
     * 
     * @param firstName User's first name
     * @param lastName User's last name
     * @param password User's password
     * @param loginUri Grid login URI
     * @param startLocation Start location ("last", "home", or specific)
     * @param mfaToken TOTP code from authenticator app (required after MFARequired result)
     * @param mfaHash Cached MFA hash from previous successful login (allows skipping MFA)
     * @return LoginResult (Success, MFARequired, or Failure)
     */
    suspend fun login(
        firstName: String,
        lastName: String,
        password: String,
        loginUri: String,
        startLocation: String = "last",
        mfaToken: String = "",
        mfaHash: String = ""
    ): LoginResult = withContext(Dispatchers.IO) {
        val app = LinkpointApp.getInstance()
        app.sessionManager.setConnectionState(ConnectionState.CONNECTING)
        
        // Start initialization tracking
        com.linkpoint.utils.InitializationTracker.startSession()
        com.linkpoint.utils.InitializationTracker.reachPhase(
            com.linkpoint.utils.InitializationTracker.Phase.LOGIN_STARTING,
            "Login for $firstName $lastName"
        )
        
        Log.d(TAG, "Attempting login for $firstName $lastName")
        NetworkLogger.logProtocol(
            "Second Life Login",
            "Grid: $loginUri, User: $firstName $lastName, Start: $startLocation"
        )
        
        // Log network diagnostics before login
        networkingService.logNetworkDiagnostics()
        
        // Create password hash - IMPORTANT: Must truncate to 16 chars like the reference viewer does
        // This is a Second Life protocol requirement
        val truncatedPassword = password.trim().take(16)
        val passwordHash = createPasswordHash(password)
        
        Log.d(TAG, "Login details - URI: $loginUri, firstName: $firstName, lastName: $lastName, " +
            "passwordLen: ${password.length}, truncatedLen: ${truncatedPassword.length}, startLoc: $startLocation")
        
        // Log detailed authentication parameters (without sensitive data)
        NetworkLogger.logAuth("Password Hash Generation", mapOf(
            "originalLength" to password.length.toString(),
            "truncatedLength" to truncatedPassword.length.toString(),
            "hashFormat" to "\$1\$MD5"
        ))
        
        // Build XMLRPC request with MFA support and Modern Viewer compatibility
        val xmlRequest = buildLoginXml(
            firstName = firstName,
            lastName = lastName,
            passwordHash = passwordHash,
            startLocation = startLocation,
            mfaToken = mfaToken,
            mfaHash = mfaHash
        )
        
        // Use CoreNetworkingService for login with comprehensive retry handling
        com.linkpoint.utils.InitializationTracker.startPhase(
            com.linkpoint.utils.InitializationTracker.Phase.LOGIN_HTTP_REQUEST,
            "Sending login request"
        )
        
        val result = networkingService.login(loginUri, xmlRequest)
        
        when (result) {
            is CoreNetworkingService.LoginResult.Success -> {
                com.linkpoint.utils.InitializationTracker.completePhase(
                    com.linkpoint.utils.InitializationTracker.Phase.LOGIN_HTTP_REQUEST,
                    "Login response received"
                )
                com.linkpoint.utils.InitializationTracker.startPhase(
                    com.linkpoint.utils.InitializationTracker.Phase.LOGIN_SUCCESS,
                    "Processing login success"
                )
                
                val agentId = try { 
                    UUID.fromString(result.agentId) 
                } catch (e: Exception) { 
                    UUID.randomUUID() 
                }
                
                Log.i(TAG, "Login successful! Agent: ${result.agentId}")
                
                NetworkLogger.logAuth("Login Success", mapOf(
                    "agentId" to result.agentId,
                    "sessionId" to "***REDACTED***",
                    "simIp" to result.simIp,
                    "simPort" to result.simPort.toString()
                ))
                
                // Validate critical login response fields
                val circuitCode = result.circuitCode ?: 0
                if (circuitCode == 0) {
                    Log.e(TAG, "Login failed: Server did not provide a valid circuit code")
                    com.linkpoint.utils.InitializationTracker.failPhase(
                        com.linkpoint.utils.InitializationTracker.Phase.LOGIN_SUCCESS,
                        "Missing circuit code in login response"
                    )
                    return@withContext LoginResult.Failure(
                        message = "Login server did not provide connection information (missing circuit code)",
                        errorCode = "MISSING_CIRCUIT_CODE",
                        technicalDetails = "The server returned a successful login but did not include the circuit code required for simulator connection."
                    )
                }
                
                if (result.simIp.isEmpty()) {
                    Log.e(TAG, "Login failed: Server did not provide simulator IP address")
                    com.linkpoint.utils.InitializationTracker.failPhase(
                        com.linkpoint.utils.InitializationTracker.Phase.LOGIN_SUCCESS,
                        "Missing simulator IP in login response"
                    )
                    return@withContext LoginResult.Failure(
                        message = "Login server did not provide connection information (missing simulator IP)",
                        errorCode = "MISSING_SIM_IP",
                        technicalDetails = "The server returned a successful login but did not include the simulator IP address."
                    )
                }
                
                if (result.simPort <= 0) {
                    Log.e(TAG, "Login failed: Server did not provide a valid simulator port")
                    com.linkpoint.utils.InitializationTracker.failPhase(
                        com.linkpoint.utils.InitializationTracker.Phase.LOGIN_SUCCESS,
                        "Missing or invalid simulator port in login response"
                    )
                    return@withContext LoginResult.Failure(
                        message = "Login server did not provide connection information (invalid simulator port: ${result.simPort})",
                        errorCode = "INVALID_SIM_PORT",
                        technicalDetails = "The server returned simPort=${result.simPort}, which is not valid."
                    )
                }
                
                val regionInfo = RegionInfo(
                    name = result.regionName ?: "Unknown",
                    handle = 0,
                    x = 128,
                    y = 128,
                    simIP = result.simIp,
                    simPort = result.simPort,
                    seedCapability = result.seedCapability
                )
                
                com.linkpoint.utils.InitializationTracker.startPhase(
                    com.linkpoint.utils.InitializationTracker.Phase.SESSION_SETUP,
                    "Setting up session"
                )
                
                app.sessionManager.onLoginSuccess(
                    sessionId = result.sessionId,
                    agentId = agentId,
                    secureSessionId = "",
                    firstName = firstName,
                    lastName = lastName,
                    regionInfo = regionInfo
                )
                
                // Configure cache manager with grid and user info for Linkpoint Cache structure.
                // Grid path:  Documents/Linkpoint/Public/<gridId>/<assetType>/...
                // User path:  Documents/Linkpoint/Private/<gridId>/<userId>/...
                //
                // Use GridInfo.id (always present, e.g. "secondlife", "secondlife_beta",
                // "kitely") rather than gridNick — gridNick is the in-protocol display
                // label and may be empty for user-added custom grids, which would collapse
                // distinct grids into the same cache directory and corrupt assets.
                val grid = app.gridManager.getSelectedGrid()
                app.cacheManager.setCurrentGrid(grid.id)
                app.cacheManager.setCurrentUser(result.agentId)
                Log.i(TAG, "Cache configured for grid: ${grid.id} (${grid.name}), user: ${result.agentId}")
                
                // Initialize agent-specific managers (sets app.agentId)
                app.initializeAgentManagers(agentId)
                
                com.linkpoint.utils.InitializationTracker.completePhase(
                    com.linkpoint.utils.InitializationTracker.Phase.SESSION_SETUP,
                    "Session and managers initialized"
                )
                
                Log.i(TAG, "╔══════════════════════════════════════════════════════════════════")
                Log.i(TAG, "║ POST-LOGIN INITIALIZATION SEQUENCE STARTING")
                Log.i(TAG, "╚══════════════════════════════════════════════════════════════════")
                
                // Configure and connect UDP connection for simulator communication
                // This is critical for receiving object updates, chat, IMs, etc.
                // IMPORTANT: We now wait for UDP connection before returning success
                com.linkpoint.utils.InitializationTracker.startPhase(
                    com.linkpoint.utils.InitializationTracker.Phase.UDP_CONNECTING,
                    "Connecting to ${result.simIp}:${result.simPort}"
                )
                
                Log.i(TAG, "[STEP 1/2] Establishing UDP connection to ${result.simIp}:${result.simPort} with circuit $circuitCode")
                app.udpConnection.configure(result.simIp, result.simPort, circuitCode)
                
                // Set session info for circuit establishment
                val sessionUUID = try {
                    UUID.fromString(result.sessionId)
                } catch (e: Exception) {
                    Log.w(TAG, "Invalid session ID format, using random UUID")
                    UUID.randomUUID()
                }
                app.udpConnection.setSessionInfo(sessionUUID, agentId)
                
                // Connect UDP synchronously - wait for connection before continuing
                val udpConnected = try {
                    Log.d(TAG, "[STEP 1/2] UDP connect() starting...")
                    app.udpConnection.connect()
                } catch (e: Exception) {
                    Log.e(TAG, "[STEP 1/2] ✗ Error establishing UDP connection", e)
                    com.linkpoint.utils.InitializationTracker.failPhase(
                        com.linkpoint.utils.InitializationTracker.Phase.UDP_CONNECTING,
                        "Exception: ${e.message}"
                    )
                    false
                }
                
                if (udpConnected) {
                    com.linkpoint.utils.InitializationTracker.completePhase(
                        com.linkpoint.utils.InitializationTracker.Phase.UDP_CONNECTING,
                        "UDP connected"
                    )
                    com.linkpoint.utils.InitializationTracker.reachPhase(
                        com.linkpoint.utils.InitializationTracker.Phase.UDP_CONNECTED,
                        "Waiting for simulator messages"
                    )
                    Log.i(TAG, "[STEP 1/2] ✓ UDP connection established - simulator packets active")
                    Log.i(TAG, "[STEP 1/2] Registered handlers: ${app.udpConnection.getRegisteredHandlerIds()}")
                } else {
                    com.linkpoint.utils.InitializationTracker.failPhase(
                        com.linkpoint.utils.InitializationTracker.Phase.UDP_CONNECTING,
                        "UDP connect() returned false"
                    )
                    Log.w(TAG, "[STEP 1/2] ✗ Failed to establish UDP connection")
                    // Continue with degraded functionality:
                    // - HTTP/capabilities features will work (texture fetching, profile lookup, map tiles)
                    // - Local chat and IMs will NOT work (these require UDP)
                    // - Object updates won't be received (avatar won't see objects appear/move)
                    // - Avatar movement won't be sent to simulator
                    // User will need to restart app to retry connection
                }
                
                // Initialize capabilities from seed capability (for textures, meshes, etc.)
                // This is critical for rendering - like the reference viewer's SLCaps.GetCapabilities()
                // IMPORTANT: We now wait for capabilities initialization before returning success
                var capsInitialized = false
                val seedCap = result.seedCapability
                if (seedCap != null && seedCap.isNotBlank()) {
                    com.linkpoint.utils.InitializationTracker.startPhase(
                        com.linkpoint.utils.InitializationTracker.Phase.CAPABILITIES_FETCHING,
                        "Fetching capabilities from seed"
                    )
                    Log.i(TAG, "[STEP 2/2] Initializing capabilities from seed...")
                    Log.d(TAG, "[STEP 2/2] Seed URL: ${seedCap.take(80)}...")
                    Log.d(TAG, "[STEP 2/2] Using Linkpoint translation layer with login URL: ${loginUri.take(60)}...")
                    
                    try {
                        Log.d(TAG, "[STEP 2/2] capabilityManager.initialize() starting with Linkpoint translation...")
                        // Use the overload that accepts loginUri for Linkpoint-compatible URL repair
                        capsInitialized = app.capabilityManager.initialize(seedCap, loginUri)
                        if (capsInitialized) {
                            com.linkpoint.utils.InitializationTracker.completePhase(
                                com.linkpoint.utils.InitializationTracker.Phase.CAPABILITIES_FETCHING,
                                "${app.capabilityManager.getCapabilityCount()} capabilities loaded"
                            )
                            com.linkpoint.utils.InitializationTracker.reachPhase(
                                com.linkpoint.utils.InitializationTracker.Phase.CAPABILITIES_READY,
                                "Capabilities available for use"
                            )
                            Log.i(TAG, "[STEP 2/2] ✓ Capabilities initialized - textures and assets ready")
                            Log.i(TAG, "[STEP 2/2] Capabilities loaded: ${app.capabilityManager.getCapabilityCount()}")
                            // Connect texture manager to capability-based fetching
                            app.textureManager.onCapabilitiesReady()
                            
                            // Parse login response for buddy-list, inventory, etc.
                            // This populates FriendsManager and InventoryManager with initial data
                            parseAndPopulateLoginData(result.responseXml, agentId)
                        } else {
                            com.linkpoint.utils.InitializationTracker.failPhase(
                                com.linkpoint.utils.InitializationTracker.Phase.CAPABILITIES_FETCHING,
                                "initialize() returned false - see CapabilityManager logs"
                            )
                            Log.w(TAG, "[STEP 2/2] ✗ Failed to initialize capabilities - textures may not load")
                            Log.w(TAG, "[STEP 2/2] Check CapabilityManager logs for detailed error information")
                        }
                    } catch (e: Exception) {
                        com.linkpoint.utils.InitializationTracker.failPhase(
                            com.linkpoint.utils.InitializationTracker.Phase.CAPABILITIES_FETCHING,
                            "Exception: ${e.message}"
                        )
                        Log.e(TAG, "[STEP 2/2] ✗ Error initializing capabilities", e)
                    }
                } else {
                    com.linkpoint.utils.InitializationTracker.failPhase(
                        com.linkpoint.utils.InitializationTracker.Phase.CAPABILITIES_FETCHING,
                        "No seed capability in login response"
                    )
                    Log.w(TAG, "[STEP 2/2] ✗ No seed capability in login response - textures may not load")
                    Log.w(TAG, "  seedCapability was null or empty in login response")
                }
                
                // Log final initialization status
                if (udpConnected && capsInitialized) {
                    Log.i(TAG, "╔══════════════════════════════════════════════════════════════════")
                    Log.i(TAG, "║ POST-LOGIN INITIALIZATION COMPLETE - WORLD SHOULD START LOADING")
                    Log.i(TAG, "╚══════════════════════════════════════════════════════════════════")
                } else {
                    Log.w(TAG, "╔══════════════════════════════════════════════════════════════════")
                    Log.w(TAG, "║ POST-LOGIN INITIALIZATION PARTIAL - some features may not work")
                    Log.w(TAG, "║   UDP Connected: $udpConnected")
                    Log.w(TAG, "║   Capabilities Initialized: $capsInitialized")
                    Log.w(TAG, "╚══════════════════════════════════════════════════════════════════")
                }
                
                com.linkpoint.utils.InitializationTracker.completePhase(
                    com.linkpoint.utils.InitializationTracker.Phase.LOGIN_SUCCESS,
                    "Login completed, waiting for world data"
                )
                
                NetworkLogger.logProtocol("Login Complete", "Successfully connected to ${result.simIp}:${result.simPort}")

                // Cache credentials so the auto re-login coordinator can
                // re-drive a full HTTP login when the UDP layer reports a
                // dead circuit. See LinkpointApp.attemptAutoRelogin().
                app.rememberLoginCredentials(
                    firstName = firstName,
                    lastName = lastName,
                    password = password,
                    loginUri = loginUri,
                    startLocation = startLocation,
                    mfaHash = result.mfaHash
                )

                LoginResult.Success(agentId, result.sessionId, result.mfaHash)
            }
            is CoreNetworkingService.LoginResult.MFARequired -> {
                app.sessionManager.setConnectionState(ConnectionState.DISCONNECTED)
                Log.i(TAG, "MFA required for login: ${result.message}")
                
                LoginResult.MFARequired(
                    message = result.message,
                    agentId = result.agentId
                )
            }
            is CoreNetworkingService.LoginResult.Failure -> {
                com.linkpoint.utils.InitializationTracker.failPhase(
                    com.linkpoint.utils.InitializationTracker.Phase.LOGIN_HTTP_REQUEST,
                    "Login failed: ${result.message}"
                )
                app.sessionManager.setConnectionState(ConnectionState.ERROR)
                Log.w(TAG, "Login failed: ${result.message} [${result.errorCode}]")
                NetworkLogger.log(
                    NetworkLogger.Level.ERROR,
                    NetworkLogger.Category.AUTHENTICATION,
                    "Login failed: ${result.message} [${result.errorCode}]"
                )
                LoginResult.Failure(
                    message = result.message,
                    errorCode = result.errorCode,
                    technicalDetails = result.technicalDetails
                )
            }
        }
    }
    
    /**
     * Parse the login response XML and populate managers with initial data.
     * 
     * This extracts:
     * - buddy-list: Friends list from login response
     * - inventory-skeleton: Initial inventory folder structure
     * 
     * Based on the reference viewer's login response parsing logic.
     */
    private fun parseAndPopulateLoginData(responseXml: String, agentId: UUID) {
        try {
            Log.i(TAG, "[LOGIN DATA] Parsing login response for buddy-list and inventory...")
            
            val parsedData = LoginResponseParser.parse(responseXml)
            
            // Populate friends from buddy-list
            if (parsedData.buddyList.isNotEmpty()) {
                Log.i(TAG, "[LOGIN DATA] Populating ${parsedData.buddyList.size} friends from login response")
                
                if (app.isFriendsManagerInitialized()) {
                    // First, add all friends with placeholder names
                    parsedData.buddyList.forEach { buddy ->
                        app.friendsManager.addFriendFromLogin(
                            agentId = buddy.agentId,
                            name = "", // Will be resolved via display names
                            rightsGiven = buddy.buddyRightsGiven,
                            rightsHas = buddy.buddyRightsHas
                        )
                    }
                    Log.i(TAG, "[LOGIN DATA] ✓ Friends added: ${parsedData.buddyList.size}")
                    
                    // Now resolve display names for all friends
                    if (app.isDisplayNameManagerInitialized()) {
                        try {
                            val friendIds = parsedData.buddyList.map { it.agentId }
                            Log.d(TAG, "[LOGIN DATA] Resolving display names for ${friendIds.size} friends...")

                            // Use ProfileManager's batch display name lookup (which uses capabilities)
                            // Using app.applicationScope for proper lifecycle management
                            app.applicationScope.launch(kotlinx.coroutines.Dispatchers.IO) {
                                try {
                                    val ready = withTimeoutOrNull(10000L) {
                                        app.capabilityManager.isReady.filter { it }.first()
                                    } ?: false
                                    if (!ready) {
                                        Log.w(TAG, "[LOGIN DATA] Capabilities not ready in 10s; using UDP UUIDNameRequest for ${friendIds.size} friends")
                                        try {
                                            app.udpConnection.sendUUIDNameRequest(friendIds)
                                        } catch (e: Exception) {
                                            Log.w(TAG, "[LOGIN DATA] UUIDNameRequest fallback failed: ${e.message}")
                                        }
                                        return@launch
                                    }

                                    val displayNames = app.profileManager.getDisplayNames(friendIds)
                                    displayNames.forEach { (agentId, displayName) ->
                                        app.friendsManager.updateFriendName(agentId, displayName)
                                    }
                                    Log.i(TAG, "[LOGIN DATA] ✓ Resolved display names for ${displayNames.size}/${friendIds.size} friends via cap")

                                    // UDP fallback: anything the GetDisplayNames cap
                                    // didn't resolve is asked over UDP UUIDNameRequest.
                                    // The cap silently failed in the 2026-04-25 Athanasia
                                    // capture (851 friends stuck on `Resident (xxxx)`);
                                    // UDP UUIDNameRequest is `NotTrusted Unencoded` and
                                    // works even when the HTTP cap pipeline is broken.
                                    val unresolved = friendIds - displayNames.keys
                                    if (unresolved.isNotEmpty()) {
                                        Log.i(TAG, "[LOGIN DATA] Falling back to UUIDNameRequest for ${unresolved.size} unresolved friends")
                                        try {
                                            app.udpConnection.sendUUIDNameRequest(unresolved.toList())
                                        } catch (e: Exception) {
                                            Log.w(TAG, "[LOGIN DATA] UUIDNameRequest fallback failed: ${e.message}")
                                        }
                                    }
                                } catch (e: Exception) {
                                    Log.w(TAG, "[LOGIN DATA] Failed to resolve some display names: ${e.message}")
                                    // If the whole HTTP batch threw, still try the UDP
                                    // fallback so the friends list isn't all placeholders.
                                    try {
                                        app.udpConnection.sendUUIDNameRequest(friendIds)
                                    } catch (udpErr: Exception) {
                                        Log.w(TAG, "[LOGIN DATA] UUIDNameRequest fallback also failed: ${udpErr.message}")
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            Log.w(TAG, "[LOGIN DATA] Error starting display name resolution: ${e.message}")
                        }
                    }
                } else {
                    Log.w(TAG, "[LOGIN DATA] FriendsManager not initialized, skipping buddy-list")
                }
            } else {
                Log.d(TAG, "[LOGIN DATA] No friends in buddy-list")
            }
            
            // Populate inventory skeleton
            if (parsedData.inventorySkeleton.isNotEmpty()) {
                Log.i(TAG, "[LOGIN DATA] Populating ${parsedData.inventorySkeleton.size} inventory folders from login response")
                
                if (app.isInventoryManagerInitialized()) {
                    // Set root folder first
                    parsedData.inventoryRoot?.let { rootId ->
                        app.inventoryManager.setRootFolder(rootId)
                        Log.d(TAG, "[LOGIN DATA] Set inventory root: $rootId")
                    }
                    
                    // Add all folders to inventory cache
                    parsedData.inventorySkeleton.forEach { folder ->
                        app.inventoryManager.addFolderFromLogin(
                            folderId = folder.folderId,
                            parentId = folder.parentId,
                            name = folder.name,
                            typeDefault = folder.typeDefault,
                            version = folder.version
                        )
                    }
                    Log.i(TAG, "[LOGIN DATA] ✓ Inventory skeleton populated: ${parsedData.inventorySkeleton.size} folders cached")
                } else {
                    Log.w(TAG, "[LOGIN DATA] InventoryManager not initialized, skipping inventory-skeleton")
                }
            } else {
                Log.d(TAG, "[LOGIN DATA] No folders in inventory-skeleton")
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "[LOGIN DATA] Error parsing login response data", e)
        }
    }
    
    private fun buildLoginXml(
        firstName: String,
        lastName: String,
        passwordHash: String,
        startLocation: String,
        mfaToken: String = "",
        mfaHash: String = ""
    ): String {
        val safeFirstName = escapeXml(firstName)
        val safeLastName = escapeXml(lastName)
        val safePassword = escapeXml(passwordHash)
        val safeStart = escapeXml(startLocation)
        val safeToken = escapeXml(mfaToken)
        val safeMfaHash = escapeXml(mfaHash)
        
        // Use persistent device identifiers (matches official viewer behavior)
        val viewerDigest = deviceIdentifier.getViewerDigest()
        val macAddress = deviceIdentifier.getMacAddress()
        val id0 = deviceIdentifier.getId0()
        
        // Get last execution status for crash reporting (matches official viewer behavior)
        val lastExecEvent = crashTracker.getLastExecStatus()
        
        // Build XML-RPC request with minimal whitespace for maximum compatibility
        return buildString {
            append("<?xml version=\"1.0\"?>")
            append("<methodCall>")
            append("<methodName>login_to_simulator</methodName>")
            append("<params>")
            append("<param>")
            append("<value><struct>")
            
            // Core login fields
            append("<member><name>first</name><value><string>$safeFirstName</string></value></member>")
            append("<member><name>last</name><value><string>$safeLastName</string></value></member>")
            append("<member><name>passwd</name><value><string>$safePassword</string></value></member>")
            append("<member><name>start</name><value><string>$safeStart</string></value></member>")
            
            // MFA fields - required by Second Life MFA login flow
            // See: https://wiki.secondlife.com/wiki/User:Brad_Linden/Login_MFA
            // - token: TOTP code from authenticator app (empty string if not responding to challenge)
            // - mfa_hash: Cached hash from previous successful MFA (allows skipping token entry)
            append("<member><name>token</name><value><string>$safeToken</string></value></member>")
            append("<member><name>mfa_hash</name><value><string>$safeMfaHash</string></value></member>")
            
            // Viewer identification
            append("<member><name>channel</name><value><string>$VIEWER_NAME</string></value></member>")
            append("<member><name>version</name><value><string>$VIEWER_NAME $VIEWER_VERSION</string></value></member>")
            append("<member><name>platform</name><value><string>Android</string></value></member>")
            append("<member><name>platform_version</name><value><string>${android.os.Build.VERSION.RELEASE}</string></value></member>")
            
            // Device identification (persistent, hashed - matches official viewer behavior)
            append("<member><name>mac</name><value><string>$macAddress</string></value></member>")
            append("<member><name>id0</name><value><string>$id0</string></value></member>")
            append("<member><name>viewer_digest</name><value><string>$viewerDigest</string></value></member>")
            
            // Agreements and status
            append("<member><name>agree_to_tos</name><value><string>true</string></value></member>")
            append("<member><name>read_critical</name><value><string>true</string></value></member>")
            
            // Last execution event - tracks previous app exit status for crash reporting
            // Required by official protocol, all desktop viewers send this
            append("<member><name>last_exec_event</name><value><i4>$lastExecEvent</i4></value></member>")
            
            // Options array - comprehensive list matching official viewers
            append("<member><name>options</name><value><array><data>")
            // Core inventory options
            append("<value><string>inventory-root</string></value>")
            append("<value><string>inventory-skeleton</string></value>")
            append("<value><string>inventory-lib-root</string></value>")
            append("<value><string>inventory-lib-owner</string></value>")
            append("<value><string>inventory-skel-lib</string></value>")
            // Avatar and UI options
            append("<value><string>initial-outfit</string></value>")
            append("<value><string>gestures</string></value>")
            append("<value><string>display_names</string></value>")
            append("<value><string>adult_compliant</string></value>")
            append("<value><string>buddy-list</string></value>")
            append("<value><string>newuser-config</string></value>")
            append("<value><string>ui-config</string></value>")
            append("<value><string>advanced-mode</string></value>")
            // Events and classifieds
            append("<value><string>event_categories</string></value>")
            append("<value><string>event_notifications</string></value>")
            append("<value><string>classified_categories</string></value>")
            // Server configuration
            append("<value><string>max-agent-groups</string></value>")
            append("<value><string>map-server-url</string></value>")
            append("<value><string>voice-config</string></value>")
            append("<value><string>tutorial_settings</string></value>")  // Fixed: was tutorial_setting (singular)
            append("<value><string>login-flags</string></value>")
            append("<value><string>global-textures</string></value>")
            // OpenSim compatibility options
            append("<value><string>avatar_picker_url</string></value>")
            append("<value><string>classified_fee</string></value>")
            append("<value><string>currency</string></value>")
            append("<value><string>destination_guide_url</string></value>")
            append("<value><string>profile-server-url</string></value>")
            append("<value><string>search</string></value>")
            append("</data></array></value></member>")
            
            append("</struct></value>")
            append("</param>")
            append("</params>")
            append("</methodCall>")
        }
    }
    
    // Note: generateMacAddress() removed - now using DeviceIdentifier for persistent IDs
    
    private fun escapeXml(input: String): String {
        return input
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&apos;")
    }
    
    /**
     * Create MD5 hash of input string
     */
    private fun md5Hash(input: String): String {
        val md = MessageDigest.getInstance("MD5")
        val digest = md.digest(input.toByteArray())
        return digest.joinToString("") { "%02x".format(it) }
    }
    
    /**
     * Create Second Life password hash.
     * 
     * IMPORTANT: Second Life protocol requires passwords to be truncated to 16 characters
     * before MD5 hashing. This matches the official viewer implementation and is required
     * for compatibility with Second Life login servers.
     * 
     * Supports already-hashed passwords: If the input is already in $1$<md5> format
     * (35 characters starting with $1$), it is returned unchanged. This matches
     * LibreMetaverse, Firestorm, and other official viewer behavior.
     * 
     * @param password The plain text password (will be trimmed and truncated to 16 chars)
     *                 OR an already-hashed password in $1$<md5> format
     * @return Password hash in format "$1$<md5_hash>"
     */
    fun createPasswordHash(password: String): String {
        // Support already-hashed passwords (35 chars: "$1$" + 32 hex)
        // This matches LibreMetaverse/Firestorm behavior
        if (password.length == 35 && password.startsWith("\$1\$")) {
            return password
        }
        val truncatedPassword = password.trim().take(16)
        return "\$1\$${md5Hash(truncatedPassword)}"
    }
    
    /**
     * Force a reconnection
     */
    suspend fun forceReconnect() {
        Log.d(TAG, "Force reconnect requested")
        networkingService.forceReconnect()
    }
    
    /**
     * Get network diagnostics report
     */
    fun getNetworkDiagnosticsReport(): String {
        val qualityReport = qualityManager.getQualityReport()
        val connectionDetails = stateManager.getConnectionDetails()
        
        return buildString {
            appendLine("=== Linkpoint Network Diagnostics ===")
            appendLine()
            appendLine("Device: ${Build.MANUFACTURER} ${Build.MODEL}")
            appendLine("Android: ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})")
            appendLine()
            appendLine("=== Connection Status ===")
            appendLine("Status: ${connectionDetails.status}")
            appendLine("Connected: ${qualityReport.isConnected}")
            appendLine("Quality: ${qualityReport.quality}")
            appendLine("Is Reconnecting: ${connectionDetails.isReconnecting}")
            appendLine("Is Faulted: ${connectionDetails.isFaulted}")
            appendLine()
            appendLine("=== Network Quality ===")
            appendLine("Type: ${qualityReport.networkType}")
            appendLine("Average Latency: ${qualityReport.averageLatencyMs}ms")
            appendLine("Est. Bandwidth: ${qualityReport.estimatedBandwidthKbps}kbps")
            appendLine("Error Rate: ${(qualityReport.errorRate * 100).toInt()}%")
            appendLine("Timeout Multiplier: ${qualityReport.timeoutMultiplier}x")
            appendLine()
            appendLine("=== Connection Details ===")
            appendLine("Connection ID: ${connectionDetails.connectionInstanceId}")
            appendLine("Reconnect Count: ${connectionDetails.reconnectCount}")
            appendLine("Force Reconnect: ${connectionDetails.forceReconnect}")
            appendLine("Always Reconnect: ${connectionDetails.alwaysReconnect}")
        }
    }
    
    /**
     * Send a chat message via ChatManager.
     * Delegates to the ChatManager which handles the actual UDP packet construction and sending.
     */
    suspend fun sendChat(message: String, channel: Int = 0, type: ChatType = ChatType.NORMAL) {
        Log.d(TAG, "Sending chat: $message on channel $channel")
        val app = LinkpointApp.getInstance()
        if (app.isChatManagerInitialized()) {
            // Map our ChatType to the protocol ChatType
            val protocolChatType = when (type) {
                ChatType.WHISPER -> com.linkpoint.protocol.messages.ChatType.WHISPER
                ChatType.NORMAL -> com.linkpoint.protocol.messages.ChatType.NORMAL
                ChatType.SHOUT -> com.linkpoint.protocol.messages.ChatType.SHOUT
            }
            app.chatManager.sendChat(message, protocolChatType, channel)
        } else {
            Log.w(TAG, "ChatManager not initialized, cannot send chat")
        }
    }
    
    /**
     * Request teleport to location
     */
    suspend fun teleport(regionName: String, x: Float, y: Float, z: Float): TeleportResult {
        Log.d(TAG, "Requesting teleport to $regionName ($x, $y, $z)")
        val app = LinkpointApp.getInstance()

        // 1. Try Capability (Preferred for named regions)
        val caps = app.capabilityManager
        if (caps.hasCapability("TeleportLocation")) {
            try {
                val request = LLSDMap().apply {
                    this["region_name"] = LLSDString(regionName)
                    this["position"] = LLSDArray().apply {
                        add(LLSDReal(x.toDouble()))
                        add(LLSDReal(y.toDouble()))
                        add(LLSDReal(z.toDouble()))
                    }
                    this["look_at"] = LLSDArray().apply {
                        add(LLSDReal(1.0))
                        add(LLSDReal(0.0))
                        add(LLSDReal(0.0))
                    }
                }

                val response = caps.request("TeleportLocation", request)
                if (response is LLSDMap) {
                    val success = response.getBoolean("success") ?: false
                    if (success) {
                        return TeleportResult.Success(regionName)
                    } else {
                        val msg = response.getString("message") ?: "Unknown capability error"
                        Log.w(TAG, "Capability teleport failed: $msg")
                        return TeleportResult.Failure(msg)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error using TeleportLocation capability", e)
                // Fallthrough to UDP
            }
        }

        // 2. Fallback to UDP
        // Get agent ID (required for packet)
        val agentId = app.agentId ?: return TeleportResult.Failure("Agent ID not initialized")
        val sessionId = app.udpConnection.getSessionId()

        try {
            // Payload size:
            // AgentData: 32 bytes
            // Info: 8 (Handle) + 12 (Pos) + 12 (Look) + 4 (Flags) = 36 bytes
            // Total: 68 bytes
            val payload = ByteBuffer.allocate(68).order(ByteOrder.LITTLE_ENDIAN)

            // AgentData
            payload.putUUID(agentId)
            payload.putUUID(sessionId)

            // Info
            payload.putLong(0) // RegionHandle (0)

            payload.putFloat(x)
            payload.putFloat(y)
            payload.putFloat(z)

            // LookAt (1,0,0)
            payload.putFloat(1f)
            payload.putFloat(0f)
            payload.putFloat(0f)

            // Flags (TELEPORT_FLAGS_VIA_LOCATION = 0x00000010)
            val teleportFlagsViaLocation = 0x00000010
            payload.putInt(teleportFlagsViaLocation)

            app.udpConnection.sendPacket(
                MessageIds.TELEPORT_LOCATION_REQUEST,
                payload.array(),
                reliable = true
            )

            Log.i(TAG, "Sent UDP teleport request to handle 0 (fallback for $regionName)")
            return TeleportResult.Success(regionName)

        } catch (e: Exception) {
            Log.e(TAG, "Error sending teleport request", e)
            return TeleportResult.Failure("Error sending request: ${e.message}")
        }
    }
    
    /**
     * Disconnect from grid
     */
    fun disconnect() {
        Log.i(TAG, "Disconnecting from grid")
        // Clear cached login credentials so the auto re-login coordinator
        // doesn't fire after a user-initiated logout. Mirrors Lumiya's
        // `userWantsConnected = false` in `SLGridConnection.disconnect()`.
        LinkpointApp.getInstance().forgetLoginCredentials()
        networkingService.disconnect()
        LinkpointApp.getInstance().sessionManager.disconnect()
    }
    
    /**
     * Clean up resources
     */
    fun shutdown() {
        Log.d(TAG, "Shutting down SecondLifeProtocol")
        networkingService.shutdown()
    }
}

sealed class LoginResult {
    data class Success(
        val agentId: UUID, 
        val sessionId: String,
        /** MFA hash returned by server for future logins (to skip MFA prompt) */
        val mfaHash: String? = null
    ) : LoginResult()
    
    /**
     * Multi-Factor Authentication is required.
     * The user must provide a TOTP code from their authenticator app.
     * Call login() again with the mfaToken parameter after obtaining the code.
     */
    data class MFARequired(
        val message: String,
        val agentId: String? = null
    ) : LoginResult()
    
    data class Failure(
        val message: String,
        val errorCode: String? = null,
        val technicalDetails: String? = null,
        /** Error category for classification */
        val category: NetworkExceptionUtils.ErrorCategory = NetworkExceptionUtils.ErrorCategory.UNKNOWN,
        /** Root cause exception type name */
        val rootCauseType: String? = null,
        /** Root cause message */
        val rootCauseMessage: String? = null,
        /** Full exception chain for debugging */
        val exceptionChain: String? = null,
        /** Recommended actions for the user */
        val recommendations: List<String> = emptyList(),
        /** Whether this error is likely transient */
        val isTransient: Boolean = false,
        /** Time elapsed during the failed request(s) */
        val elapsedTimeMs: Long = 0,
        /** Number of attempts made */
        val attemptsMade: Int = 1
    ) : LoginResult() {
        /**
         * Get a comprehensive error report for debugging.
         */
        fun getFullReport(): String = buildString {
            appendLine("=== Login Error Report ===")
            appendLine()
            appendLine("Error Code: ${errorCode ?: "UNKNOWN"}")
            appendLine("Category: $category")
            appendLine("Message: $message")
            appendLine()
            appendLine("Attempts Made: $attemptsMade")
            appendLine("Total Time: ${elapsedTimeMs}ms")
            appendLine("Is Transient: $isTransient")
            appendLine()
            if (rootCauseType != null) {
                appendLine("=== Root Cause ===")
                appendLine("Type: $rootCauseType")
                appendLine("Message: ${rootCauseMessage ?: "(none)"}")
                appendLine()
            }
            if (!exceptionChain.isNullOrBlank()) {
                appendLine("=== Exception Chain ===")
                appendLine(exceptionChain)
                appendLine()
            }
            if (!technicalDetails.isNullOrBlank()) {
                appendLine("=== Technical Details ===")
                appendLine(technicalDetails)
                appendLine()
            }
            if (recommendations.isNotEmpty()) {
                appendLine("=== Recommended Actions ===")
                recommendations.forEachIndexed { idx, rec ->
                    appendLine("${idx + 1}. $rec")
                }
            }
        }
    }
}

sealed class TeleportResult {
    data class Success(val regionName: String) : TeleportResult()
    data class Failure(val message: String) : TeleportResult()
}

enum class ChatType(val value: Int) {
    WHISPER(0),
    NORMAL(1),
    SHOUT(2)
}
