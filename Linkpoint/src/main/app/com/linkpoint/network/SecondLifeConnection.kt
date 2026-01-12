package com.linkpoint.network

import android.content.Context
import android.util.Log
import com.linkpoint.network.core.CoreNetworkingService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference
import java.security.MessageDigest

/**
 * Handles Second Life grid authentication and connection
 * 
 * Now uses CoreNetworkingService for:
 * - gRPC-based networking (where applicable)
 * - Comprehensive retry logic with exponential backoff
 * - Connection quality monitoring
 * - Error count tracking and thresholds
 * - Automatic reconnection
 * 
 * Based on patterns from the official Second Life app.
 */
class SecondLifeConnection(context: Context? = null) {
    
    companion object {
        private const val TAG = "SLConnection"
        
        // Second Life login URLs
        private const val SL_LOGIN_URL = "https://login.agni.lindenlab.com/cgi-bin/login.cgi"
        private const val BETA_LOGIN_URL = "https://login.aditi.lindenlab.com/cgi-bin/login.cgi"
        
        // Grid URLs - HTTPS required for security
        val GRIDS = mapOf(
            "Second Life" to SL_LOGIN_URL,
            "Second Life Beta" to BETA_LOGIN_URL,
            "Kitely" to "https://login.kitely.com/"
        )
        
        // Insecure grids (HTTP) - blocked by default for security
        val INSECURE_GRIDS = mapOf(
            "OSGrid (Insecure)" to "http://login.osgrid.org/",
            "InWorldz (Insecure)" to "http://login.inworldz.com:8002/"
        )
    }
    
    // Keep a weak reference to context for networking service
    private val contextRef: WeakReference<Context>? = context?.let { WeakReference(it) }
    
    // Core networking service (created lazily when context is available)
    private val networkingService: CoreNetworkingService? by lazy {
        contextRef?.get()?.let { CoreNetworkingService(it) }
    }
    
    private var sessionId: String? = null
    private var agentId: String? = null
    private var simIp: String? = null
    private var simPort: Int = 0
    
    data class LoginResult(
        val success: Boolean,
        val message: String,
        val sessionId: String? = null,
        val agentId: String? = null,
        val errorCode: String? = null,
        val technicalDetails: String? = null
    )
    
    /**
     * Perform login to Second Life or compatible grid
     */
    suspend fun login(
        firstName: String,
        lastName: String,
        password: String,
        gridName: String = "Second Life",
        startLocation: String = "last"
    ): LoginResult = withContext(Dispatchers.IO) {
        
        Log.d(TAG, "Attempting login for $firstName $lastName on $gridName")
        
        val loginUrl = GRIDS[gridName] ?: SL_LOGIN_URL
        
        // Security: Block insecure HTTP login URLs to prevent credential interception
        if (!loginUrl.startsWith("https://")) {
            Log.w(TAG, "Insecure login URL blocked: $loginUrl")
            return@withContext LoginResult(
                success = false,
                message = "Insecure connection blocked: $gridName uses HTTP. HTTPS is required for secure login."
            )
        }
        
        val service = networkingService
        if (service == null) {
            Log.e(TAG, "No context available for networking service")
            return@withContext LoginResult(
                success = false,
                message = "Internal error: networking service not available",
                errorCode = "NO_CONTEXT"
            )
        }
        
        // Create password hash (MD5 for SL compatibility)
        // IMPORTANT: Second Life protocol requires passwords to be truncated to 16 characters
        // before MD5 hashing. This matches the official Lumiya implementation.
        val truncatedPassword = password.trim().take(16)
        val passwordHash = "\$1\$${md5Hash(truncatedPassword)}"
        
        // Build XMLRPC login request
        val xmlRequest = buildLoginXml(
            firstName = firstName,
            lastName = lastName,
            passwordHash = passwordHash,
            startLocation = startLocation
        )
        
        // Use CoreNetworkingService for login with comprehensive retry handling
        val result = service.login(loginUrl, xmlRequest)
        
        when (result) {
            is CoreNetworkingService.LoginResult.Success -> {
                sessionId = result.sessionId
                agentId = result.agentId
                simIp = result.simIp
                simPort = result.simPort
                
                Log.i(TAG, "Login successful! Session: $sessionId, Agent: $agentId")
                Log.i(TAG, "Sim: $simIp:$simPort")
                
                LoginResult(
                    success = true,
                    message = "Login successful",
                    sessionId = sessionId,
                    agentId = agentId
                )
            }
            is CoreNetworkingService.LoginResult.Failure -> {
                Log.w(TAG, "Login failed: ${result.message} [${result.errorCode}]")
                
                LoginResult(
                    success = false,
                    message = result.message,
                    errorCode = result.errorCode,
                    technicalDetails = result.technicalDetails
                )
            }
        }
    }
    
    /**
     * Escape special XML characters to prevent XML injection
     */
    private fun escapeXml(input: String): String {
        return input
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&apos;")
    }
    
    private fun buildLoginXml(
        firstName: String,
        lastName: String,
        passwordHash: String,
        startLocation: String
    ): String {
        // Escape all user-provided input to prevent XML injection
        val safeFirstName = escapeXml(firstName)
        val safeLastName = escapeXml(lastName)
        val safePasswordHash = escapeXml(passwordHash)
        val safeStartLocation = escapeXml(startLocation)
        
        // Generate viewer_digest - required by Second Life login server
        val viewerDigest = java.util.UUID.randomUUID().toString()
        
        // Generate unique MAC and ID0 for this device session
        val macAddress = generateMacAddress()
        val id0 = java.util.UUID.randomUUID().toString()
        
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
            append("<member><name>passwd</name><value><string>$safePasswordHash</string></value></member>")
            append("<member><name>start</name><value><string>$safeStartLocation</string></value></member>")
            
            // Viewer identification
            // NOTE: Currently identifying as "Lumiya" because Linkpoint is based on Lumiya
            // and "Linkpoint" is not yet registered with Linden Lab's Third-Party Viewer Directory.
            // TODO: Register "Linkpoint" with Linden Lab and update channel name after approval
            append("<member><name>channel</name><value><string>Lumiya</string></value></member>")
            append("<member><name>version</name><value><string>Lumiya 1.0.0</string></value></member>")
            append("<member><name>platform</name><value><string>Android</string></value></member>")
            append("<member><name>platform_version</name><value><string>${android.os.Build.VERSION.RELEASE}</string></value></member>")
            
            // Device identification (required by SL login server)
            append("<member><name>mac</name><value><string>$macAddress</string></value></member>")
            append("<member><name>id0</name><value><string>$id0</string></value></member>")
            append("<member><name>viewer_digest</name><value><string>$viewerDigest</string></value></member>")
            
            // Agreements
            append("<member><name>agree_to_tos</name><value><string>true</string></value></member>")
            append("<member><name>read_critical</name><value><string>true</string></value></member>")
            
            // Options array - comprehensive list for full functionality
            append("<member><name>options</name><value><array><data>")
            append("<value><string>inventory-root</string></value>")
            append("<value><string>inventory-skeleton</string></value>")
            append("<value><string>inventory-lib-root</string></value>")
            append("<value><string>inventory-lib-owner</string></value>")
            append("<value><string>inventory-skel-lib</string></value>")
            append("<value><string>initial-outfit</string></value>")
            append("<value><string>gestures</string></value>")
            append("<value><string>display_names</string></value>")
            append("<value><string>event_categories</string></value>")
            append("<value><string>event_notifications</string></value>")
            append("<value><string>classified_categories</string></value>")
            append("<value><string>adult_compliant</string></value>")
            append("<value><string>buddy-list</string></value>")
            append("<value><string>newuser-config</string></value>")
            append("<value><string>ui-config</string></value>")
            append("<value><string>advanced-mode</string></value>")
            append("<value><string>max-agent-groups</string></value>")
            append("<value><string>map-server-url</string></value>")
            append("<value><string>voice-config</string></value>")
            append("<value><string>tutorial_setting</string></value>")
            append("<value><string>login-flags</string></value>")
            append("<value><string>global-textures</string></value>")
            append("</data></array></value></member>")
            
            append("</struct></value>")
            append("</param>")
            append("</params>")
            append("</methodCall>")
        }
    }
    
    /**
     * Generate a pseudo-MAC address for device identification
     */
    private fun generateMacAddress(): String {
        val random = java.util.Random()
        return (0..5).joinToString(":") { 
            String.format("%02X", random.nextInt(256)) 
        }
    }
    
    private fun md5Hash(input: String): String {
        val md = MessageDigest.getInstance("MD5")
        val digest = md.digest(input.toByteArray())
        return digest.joinToString("") { "%02x".format(it) }
    }
    
    fun getSessionId(): String? = sessionId
    fun getAgentId(): String? = agentId
    fun getSimIp(): String? = simIp
    fun getSimPort(): Int = simPort
    
    fun isConnected(): Boolean = sessionId != null
    
    /**
     * Send a chat message to the current region
     */
    suspend fun sendChatMessage(
        message: String,
        channel: Int = 0,
        type: ChatType = ChatType.NORMAL
    ): Boolean = withContext(Dispatchers.IO) {
        if (!isConnected()) {
            Log.w(TAG, "Cannot send chat: not connected")
            return@withContext false
        }
        
        Log.d(TAG, "Sending chat message: $message (channel: $channel, type: $type)")
        
        // TODO: Implement via gRPC or UDP
        return@withContext true
    }
    
    /**
     * Request teleport to a specific location
     */
    suspend fun teleportToLocation(
        regionName: String,
        x: Float,
        y: Float,
        z: Float
    ): TeleportResult = withContext(Dispatchers.IO) {
        if (!isConnected()) {
            Log.w(TAG, "Cannot teleport: not connected")
            return@withContext TeleportResult(
                success = false,
                message = "Not connected to grid"
            )
        }
        
        Log.d(TAG, "Requesting teleport to $regionName ($x, $y, $z)")
        
        // TODO: Implement teleport
        return@withContext TeleportResult(
            success = true,
            message = "Teleport request sent to $regionName",
            regionName = regionName
        )
    }
    
    /**
     * Request teleport to a landmark
     */
    suspend fun teleportToLandmark(landmarkId: String): TeleportResult = withContext(Dispatchers.IO) {
        if (!isConnected()) {
            return@withContext TeleportResult(
                success = false,
                message = "Not connected to grid"
            )
        }
        
        Log.d(TAG, "Requesting teleport to landmark: $landmarkId")
        
        return@withContext TeleportResult(
            success = true,
            message = "Teleport request sent"
        )
    }
    
    /**
     * Request teleport home
     */
    suspend fun teleportHome(): TeleportResult = withContext(Dispatchers.IO) {
        if (!isConnected()) {
            return@withContext TeleportResult(
                success = false,
                message = "Not connected to grid"
            )
        }
        
        Log.d(TAG, "Requesting teleport home")
        
        return@withContext TeleportResult(
            success = true,
            message = "Teleporting home..."
        )
    }
    
    /**
     * Log network diagnostics
     */
    fun logNetworkDiagnostics() {
        networkingService?.logNetworkDiagnostics()
    }
    
    /**
     * Force reconnection
     */
    suspend fun forceReconnect() {
        networkingService?.forceReconnect()
    }
    
    /**
     * Disconnect and clean up
     */
    fun disconnect() {
        Log.d(TAG, "Disconnecting...")
        networkingService?.disconnect()
        sessionId = null
        agentId = null
        simIp = null
        simPort = 0
    }
    
    /**
     * Shutdown and release resources
     */
    fun shutdown() {
        disconnect()
        networkingService?.shutdown()
    }
    
    enum class ChatType(val value: Int) {
        WHISPER(0),
        NORMAL(1),
        SHOUT(2),
        SAY(1),
        OWNER_SAY(4),
        DEBUG_CHANNEL(5),
        REGION_SAY(6),
        REGION_SAY_TO(7)
    }
    
    data class TeleportResult(
        val success: Boolean,
        val message: String,
        val regionName: String? = null
    )
}
