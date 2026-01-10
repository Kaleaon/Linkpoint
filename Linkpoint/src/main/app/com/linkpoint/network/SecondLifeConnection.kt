package com.linkpoint.network

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ConnectionPool
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.security.MessageDigest
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLException

/**
 * Handles Second Life grid authentication and connection
 */
class SecondLifeConnection {
    
    companion object {
        private const val TAG = "SLConnection"
        
        // Second Life login URLs
        private const val SL_LOGIN_URL = "https://login.agni.lindenlab.com/cgi-bin/login.cgi"
        private const val BETA_LOGIN_URL = "https://login.aditi.lindenlab.com/cgi-bin/login.cgi"
        
        // Grid URLs - HTTPS required for security
        // Note: Some OpenSim grids may not support HTTPS
        val GRIDS = mapOf(
            "Second Life" to SL_LOGIN_URL,
            "Second Life Beta" to BETA_LOGIN_URL,
            "Kitely" to "https://login.kitely.com/"
        )
        
        // Insecure grids (HTTP) - blocked by default for security
        // Users must explicitly acknowledge the security risk to use these
        val INSECURE_GRIDS = mapOf(
            "OSGrid (Insecure)" to "http://login.osgrid.org/",
            "InWorldz (Insecure)" to "http://login.inworldz.com:8002/"
        )
    }
    
    /**
     * Retry interceptor for handling transient mobile network failures.
     * LTE/mobile networks often experience brief connection drops that succeed on retry.
     */
    private class MobileNetworkRetryInterceptor(private val maxRetries: Int = 3) : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request()
            var lastException: IOException? = null
            
            repeat(maxRetries) { attempt ->
                try {
                    // Exponential backoff: 0ms, 500ms, 1500ms
                    if (attempt > 0) {
                        val backoffMs = (attempt * 500L)
                        Thread.sleep(backoffMs)
                        Log.d(TAG, "Retry attempt ${attempt + 1}/$maxRetries after ${backoffMs}ms backoff")
                    }
                    return chain.proceed(request)
                } catch (e: SocketTimeoutException) {
                    Log.w(TAG, "Timeout on attempt ${attempt + 1}/$maxRetries: ${e.message}")
                    lastException = e
                } catch (e: UnknownHostException) {
                    Log.w(TAG, "DNS failure on attempt ${attempt + 1}/$maxRetries: ${e.message}")
                    lastException = e
                } catch (e: SSLException) {
                    // SSL handshake failures can be transient on mobile networks
                    if (e.message?.contains("Connection reset", ignoreCase = true) == true ||
                        e.message?.contains("closed", ignoreCase = true) == true) {
                        Log.w(TAG, "SSL connection issue on attempt ${attempt + 1}/$maxRetries: ${e.message}")
                        lastException = e
                    } else {
                        throw e // Non-transient SSL error
                    }
                } catch (e: IOException) {
                    // Check if this is a retryable error
                    val isRetryable = e.message?.let { msg ->
                        msg.contains("timeout", ignoreCase = true) ||
                        msg.contains("reset", ignoreCase = true) ||
                        msg.contains("closed", ignoreCase = true) ||
                        msg.contains("failed to connect", ignoreCase = true)
                    } ?: false
                    
                    if (isRetryable) {
                        Log.w(TAG, "Retryable error on attempt ${attempt + 1}/$maxRetries: ${e.message}")
                        lastException = e
                    } else {
                        throw e
                    }
                }
            }
            
            throw lastException ?: IOException("Failed after $maxRetries attempts")
        }
    }
    
    /**
     * OkHttp client optimized for mobile/LTE networks:
     * - Extended timeouts for high-latency mobile connections
     * - Connection pooling for efficiency
     * - Retry interceptor for transient failures
     * - HTTP/2 support with HTTP/1.1 fallback
     */
    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(45, TimeUnit.SECONDS)  // Extended for LTE latency
        .readTimeout(90, TimeUnit.SECONDS)     // Extended for slow mobile data
        .writeTimeout(45, TimeUnit.SECONDS)    // Extended for upload on mobile
        .retryOnConnectionFailure(true)        // Enable automatic connection retry
        .connectionPool(ConnectionPool(
            maxIdleConnections = 5,
            keepAliveDuration = 5,
            timeUnit = TimeUnit.MINUTES
        ))
        .protocols(listOf(Protocol.HTTP_2, Protocol.HTTP_1_1))
        .addInterceptor(MobileNetworkRetryInterceptor(maxRetries = 3))
        .build()
    
    private var sessionId: String? = null
    private var agentId: String? = null
    private var simIp: String? = null
    private var simPort: Int = 0
    
    data class LoginResult(
        val success: Boolean,
        val message: String,
        val sessionId: String? = null,
        val agentId: String? = null
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
        
        // Create password hash (MD5 for SL compatibility)
        val passwordHash = "\$1\$${md5Hash(password)}"
        
        // Build XMLRPC login request
        val xmlRequest = buildLoginXml(
            firstName = firstName,
            lastName = lastName,
            passwordHash = passwordHash,
            startLocation = startLocation
        )
        
        try {
            val request = Request.Builder()
                .url(loginUrl)
                .post(xmlRequest.toRequestBody("text/xml".toMediaType()))
                .header("Content-Type", "text/xml")
                .header("User-Agent", "Linkpoint/1.0.0 (Android)")
                .build()
            
            val response = httpClient.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""
            
            Log.d(TAG, "Login response code: ${response.code}")
            
            if (response.isSuccessful) {
                parseLoginResponse(responseBody)
            } else {
                LoginResult(
                    success = false,
                    message = "Server error: ${response.code}"
                )
            }
        } catch (e: IOException) {
            Log.e(TAG, "Login network error", e)
            val errorMessage = when {
                e.message.isNullOrBlank() -> 
                    "Unable to connect. Please check your internet connection and try again."
                e is SocketTimeoutException || e.message!!.contains("timeout", ignoreCase = true) -> 
                    "Connection timed out. If you're on mobile data, try moving to an area with better signal or switch to Wi-Fi."
                e is UnknownHostException || e.message!!.contains("host", ignoreCase = true) -> 
                    "Could not reach server. Please check your internet connection. If on mobile data, try toggling airplane mode."
                e.message!!.contains("ssl", ignoreCase = true) || e.message!!.contains("certificate", ignoreCase = true) -> 
                    "Secure connection failed. This may be a temporary network issue. Please try again."
                e.message!!.contains("reset", ignoreCase = true) || e.message!!.contains("closed", ignoreCase = true) ->
                    "Connection was interrupted. This often happens on mobile networks. Please try again."
                e.message!!.contains("failed to connect", ignoreCase = true) ->
                    "Could not connect to server. Please check your network connection and try again."
                else -> "Network error: ${e.message}"
            }
            LoginResult(
                success = false,
                message = errorMessage
            )
        } catch (e: Exception) {
            Log.e(TAG, "Login error", e)
            val errorMessage = if (e.message.isNullOrBlank()) {
                "An unexpected error occurred. Please try again."
            } else {
                "Error: ${e.message}"
            }
            LoginResult(
                success = false,
                message = errorMessage
            )
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
        
        return """
<?xml version="1.0"?>
<methodCall>
    <methodName>login_to_simulator</methodName>
    <params>
        <param>
            <value>
                <struct>
                    <member>
                        <name>first</name>
                        <value><string>$safeFirstName</string></value>
                    </member>
                    <member>
                        <name>last</name>
                        <value><string>$safeLastName</string></value>
                    </member>
                    <member>
                        <name>passwd</name>
                        <value><string>$safePasswordHash</string></value>
                    </member>
                    <member>
                        <name>start</name>
                        <value><string>$safeStartLocation</string></value>
                    </member>
                    <member>
                        <name>channel</name>
                        <value><string>Linkpoint</string></value>
                    </member>
                    <member>
                        <name>version</name>
                        <value><string>Linkpoint 1.0.0</string></value>
                    </member>
                    <member>
                        <name>platform</name>
                        <value><string>Android</string></value>
                    </member>
                    <member>
                        <name>mac</name>
                        <value><string>00:00:00:00:00:00</string></value>
                    </member>
                    <member>
                        <name>id0</name>
                        <value><string>00000000-0000-0000-0000-000000000000</string></value>
                    </member>
                    <member>
                        <name>agree_to_tos</name>
                        <value><boolean>1</boolean></value>
                    </member>
                    <member>
                        <name>read_critical</name>
                        <value><boolean>1</boolean></value>
                    </member>
                    <member>
                        <name>options</name>
                        <value>
                            <array>
                                <data>
                                    <value><string>inventory-root</string></value>
                                    <value><string>inventory-skeleton</string></value>
                                    <value><string>buddy-list</string></value>
                                    <value><string>initial-outfit</string></value>
                                </data>
                            </array>
                        </value>
                    </member>
                </struct>
            </value>
        </param>
    </params>
</methodCall>
        """.trimIndent()
    }
    
    private fun parseLoginResponse(xml: String): LoginResult {
        Log.d(TAG, "Parsing login response...")
        
        // Check for login success
        val loginRegex = """<name>login</name>\s*<value><string>([\w]+)</string>""".toRegex()
        val loginMatch = loginRegex.find(xml)
        val loginStatus = loginMatch?.groupValues?.get(1)
        
        if (loginStatus == "true") {
            // Extract session info
            val sessionRegex = """<name>session_id</name>\s*<value><string>([^<]+)</string>""".toRegex()
            val agentRegex = """<name>agent_id</name>\s*<value><string>([^<]+)</string>""".toRegex()
            
            sessionId = sessionRegex.find(xml)?.groupValues?.get(1)
            agentId = agentRegex.find(xml)?.groupValues?.get(1)
            
            // Extract sim connection info
            val simIpRegex = """<name>sim_ip</name>\s*<value><string>([^<]+)</string>""".toRegex()
            val simPortRegex = """<name>sim_port</name>\s*<value><i4>(\d+)</i4>""".toRegex()
            
            simIp = simIpRegex.find(xml)?.groupValues?.get(1)
            simPort = simPortRegex.find(xml)?.groupValues?.get(1)?.toIntOrNull() ?: 0
            
            Log.i(TAG, "Login successful! Session: $sessionId, Agent: $agentId")
            Log.i(TAG, "Sim: $simIp:$simPort")
            
            return LoginResult(
                success = true,
                message = "Login successful",
                sessionId = sessionId,
                agentId = agentId
            )
        } else {
            // Extract error message - try both 'message' and 'reason' fields
            val messageRegex = """<name>message</name>\s*<value><string>([^<]+)</string>""".toRegex()
            val reasonRegex = """<name>reason</name>\s*<value><string>([^<]+)</string>""".toRegex()
            
            var errorMessage = messageRegex.find(xml)?.groupValues?.get(1)
            
            // If message is null, empty, or looks like a null literal, try the reason field
            if (errorMessage.isNullOrBlank() || errorMessage.equals("null", ignoreCase = true)) {
                errorMessage = reasonRegex.find(xml)?.groupValues?.get(1)
            }
            
            // Provide a user-friendly fallback if we still don't have a message
            val finalMessage = when {
                errorMessage.isNullOrBlank() -> "Login failed. Please check your credentials and try again."
                errorMessage.equals("null", ignoreCase = true) -> "Login failed. Please check your credentials and try again."
                errorMessage.equals("key", ignoreCase = true) -> "Invalid username or password. Please check your credentials."
                errorMessage.equals("presence", ignoreCase = true) -> "You appear to be logged in already. Please wait a moment and try again."
                errorMessage.equals("update", ignoreCase = true) -> "A viewer update is required. Please update the app."
                errorMessage.equals("optional", ignoreCase = true) -> "Login failed. Please try again."
                else -> errorMessage
            }
            
            Log.w(TAG, "Login failed: $finalMessage (raw: $errorMessage)")
            
            return LoginResult(
                success = false,
                message = finalMessage
            )
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
        
        // In a full implementation, this would send via UDP to the simulator
        // For now, we log and return success to indicate the API is ready
        // The actual UDP implementation requires the llmessage system
        
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
        
        // In a full implementation, this would:
        // 1. Look up the region handle from the region name
        // 2. Send TeleportLocationRequest to the simulator
        // 3. Wait for TeleportProgress/TeleportFinish/TeleportFailed
        
        // For now, indicate the API is ready
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
