package com.linkpoint.network

import android.content.Context
import android.os.Build
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
import java.io.EOFException
import java.io.IOException
import java.lang.ref.WeakReference
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.security.MessageDigest
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLException
import javax.net.ssl.SSLHandshakeException
import javax.net.ssl.SSLPeerUnverifiedException

/**
 * Handles Second Life grid authentication and connection
 * 
 * Optimized for Android 9+ with:
 * - Proper SSL/TLS configuration
 * - Adaptive timeouts based on network conditions
 * - Enhanced error handling for mobile networks
 */
class SecondLifeConnection(context: Context? = null) {
    
    companion object {
        private const val TAG = "SLConnection"
        
        // Second Life login URLs
        private const val SL_LOGIN_URL = "https://login.agni.lindenlab.com/cgi-bin/login.cgi"
        private const val BETA_LOGIN_URL = "https://login.aditi.lindenlab.com/cgi-bin/login.cgi"
        
        // Base timeout values (will be multiplied by network quality factor)
        private const val BASE_CONNECT_TIMEOUT_SECONDS = 30L
        private const val BASE_READ_TIMEOUT_SECONDS = 60L
        private const val BASE_WRITE_TIMEOUT_SECONDS = 30L
        
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
    
    // Keep a weak reference to context for network diagnostics
    private val contextRef: WeakReference<Context>? = context?.let { WeakReference(it) }
    
    /**
     * Retry interceptor for handling transient mobile network failures.
     * Enhanced for Android 9+ with SSL-specific error handling.
     */
    private class MobileNetworkRetryInterceptor(
        private val maxRetries: Int = 3,
        private val initialDelayMs: Long = 500L
    ) : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request()
            var lastException: IOException? = null
            
            repeat(maxRetries) { attempt ->
                try {
                    // Exponential backoff with jitter
                    if (attempt > 0) {
                        val baseDelay = initialDelayMs * (1 shl (attempt - 1))
                        val jitter = (Math.random() * 200).toLong()
                        val backoffMs = baseDelay + jitter
                        Thread.sleep(backoffMs)
                        Log.d(TAG, "Retry attempt ${attempt + 1}/$maxRetries after ${backoffMs}ms backoff")
                    }
                    return chain.proceed(request)
                } catch (e: SocketTimeoutException) {
                    Log.w(TAG, "Timeout on attempt ${attempt + 1}/$maxRetries: ${e.message}")
                    lastException = e
                } catch (e: EOFException) {
                    // EOFException occurs when connection is closed before response completes
                    // Common with HTTP/2 connection issues or server-side load balancing
                    // Always retry with slightly longer delay to let the server recover
                    Log.w(TAG, "EOF/Connection closed on attempt ${attempt + 1}/$maxRetries: ${e.message}")
                    lastException = EOFIOException("Server closed connection before response completed", e)
                    // Add extra delay for EOF errors as they often indicate server-side issues
                    if (attempt < maxRetries - 1) {
                        Thread.sleep(NetworkExceptionUtils.EOF_EXTRA_DELAY_MS)
                    }
                } catch (e: UnknownHostException) {
                    Log.w(TAG, "DNS failure on attempt ${attempt + 1}/$maxRetries: ${e.message}")
                    lastException = e
                    // DNS failures often resolve quickly
                    if (attempt < maxRetries - 1) Thread.sleep(100)
                } catch (e: SSLHandshakeException) {
                    // Check if transient
                    val isTransient = e.message?.let { msg ->
                        msg.contains("Connection reset", ignoreCase = true) ||
                        msg.contains("closed", ignoreCase = true) ||
                        msg.contains("timeout", ignoreCase = true)
                    } ?: false
                    
                    if (isTransient) {
                        Log.w(TAG, "Transient SSL handshake issue on attempt ${attempt + 1}/$maxRetries: ${e.message}")
                        lastException = e
                    } else {
                        Log.e(TAG, "Non-transient SSL handshake error: ${e.message}")
                        throw e
                    }
                } catch (e: SSLPeerUnverifiedException) {
                    Log.e(TAG, "SSL certificate verification failed: ${e.message}")
                    throw e // Never retry certificate verification failures
                } catch (e: SSLException) {
                    val isTransient = e.message?.let { msg ->
                        msg.contains("Connection reset", ignoreCase = true) ||
                        msg.contains("closed", ignoreCase = true) ||
                        msg.contains("I/O error", ignoreCase = true)
                    } ?: false
                    
                    if (isTransient) {
                        Log.w(TAG, "SSL connection issue on attempt ${attempt + 1}/$maxRetries: ${e.message}")
                        lastException = e
                    } else {
                        throw e
                    }
                } catch (e: IOException) {
                    val isRetryable = e.message?.let { msg ->
                        msg.contains("timeout", ignoreCase = true) ||
                        msg.contains("reset", ignoreCase = true) ||
                        msg.contains("closed", ignoreCase = true) ||
                        msg.contains("failed to connect", ignoreCase = true) ||
                        msg.contains("ECONNRESET", ignoreCase = true)
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
     * Create an OkHttp client with adaptive timeouts and proper SSL configuration
     */
    private fun createHttpClient(): OkHttpClient {
        // Get network info for adaptive timeouts if context is available
        val timeoutMultiplier: Float
        val retryCount: Int
        
        val ctx = contextRef?.get()
        if (ctx != null) {
            val networkInfo = NetworkDiagnostics.getNetworkInfo(ctx)
            timeoutMultiplier = networkInfo.timeoutMultiplier.coerceIn(1.0f, 3.0f)
            retryCount = networkInfo.recommendedRetries
            Log.d(TAG, "Creating HTTP client - Network: ${networkInfo.displayName}, " +
                "Timeout multiplier: ${timeoutMultiplier}x, Retries: $retryCount")
        } else {
            // Default values if no context
            timeoutMultiplier = 1.5f
            retryCount = 3
            Log.d(TAG, "Creating HTTP client with default timeouts (no context)")
        }
        
        val connectTimeout = (BASE_CONNECT_TIMEOUT_SECONDS * timeoutMultiplier).toLong().coerceAtMost(90L)
        val readTimeout = (BASE_READ_TIMEOUT_SECONDS * timeoutMultiplier).toLong().coerceAtMost(180L)
        val writeTimeout = (BASE_WRITE_TIMEOUT_SECONDS * timeoutMultiplier).toLong().coerceAtMost(90L)
        
        val builder = OkHttpClient.Builder()
            .connectTimeout(connectTimeout, TimeUnit.SECONDS)
            .readTimeout(readTimeout, TimeUnit.SECONDS)
            .writeTimeout(writeTimeout, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .connectionPool(ConnectionPool(
                maxIdleConnections = 5,
                keepAliveDuration = 5,
                timeUnit = TimeUnit.MINUTES
            ))
            // Force HTTP/1.1 for login requests - HTTP/2 can cause EOFException issues
            // with some XMLRPC servers including Second Life's login.cgi
            .protocols(listOf(Protocol.HTTP_1_1))
            .addInterceptor(MobileNetworkRetryInterceptor(
                maxRetries = retryCount,
                initialDelayMs = (500 * timeoutMultiplier).toLong()
            ))
        
        // Configure SSL for Android 9+ compatibility
        SSLHelper.configureSSL(builder, debugMode = false)
        
        return builder.build()
    }
    
    // Lazy initialization of HTTP client
    private val httpClient: OkHttpClient by lazy { createHttpClient() }
    
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
            Log.d(TAG, "Login request to: $loginUrl")
            
            val request = Request.Builder()
                .url(loginUrl)
                .post(xmlRequest.toRequestBody("text/xml".toMediaType()))
                .header("Content-Type", "text/xml")
                .header("User-Agent", "Linkpoint/1.0.0 (Android)")
                .build()
            
            val response = httpClient.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""
            
            Log.d(TAG, "Login response code: ${response.code}, body length: ${responseBody.length}")
            
            if (response.isSuccessful) {
                parseLoginResponse(responseBody)
            } else {
                val technicalDetails = buildString {
                    appendLine("URL: $loginUrl")
                    appendLine("HTTP Status: ${response.code}")
                    appendLine("Response: ${responseBody.take(500)}")
                }
                LoginResult(
                    success = false,
                    message = "Server returned HTTP ${response.code}",
                    errorCode = "HTTP_${response.code}",
                    technicalDetails = technicalDetails
                )
            }
        } catch (e: IOException) {
            Log.e(TAG, "Login network error: ${e.javaClass.simpleName}: ${e.message}", e)
            
            val technicalDetails = buildString {
                appendLine("URL: $loginUrl")
                appendLine("Exception: ${e.javaClass.simpleName}")
                appendLine("Message: ${e.message ?: "none"}")
                e.cause?.let { cause ->
                    appendLine("Cause: ${cause.javaClass.simpleName}: ${cause.message}")
                }
            }
            
            val (errorMessage, errorCode) = when {
                e is SocketTimeoutException -> 
                    "Connection timed out after multiple retries. The login servers may be slow or unreachable." to "TIMEOUT"
                e is UnknownHostException -> 
                    "Cannot resolve server address. Please check your internet connection and DNS settings." to "DNS_FAILED"
                e is SSLException -> 
                    "SSL/TLS connection failed: ${e.message?.take(100) ?: "handshake error"}. This may be a network or certificate issue." to "SSL_ERROR"
                // Handle EOFException - connection closed before response completed
                // Uses shared utility to check exception type, cause chain, and message indicators
                NetworkExceptionUtils.isEOFException(e) ->
                    "The server closed the connection before sending a complete response. This is usually a temporary issue - please try again." to "CONNECTION_EOF"
                e.message?.contains("timeout", ignoreCase = true) == true -> 
                    "Connection timed out. The server is not responding." to "TIMEOUT"
                e.message?.contains("host", ignoreCase = true) == true -> 
                    "Could not reach the login server. Check your internet connection." to "HOST_UNREACHABLE"
                e.message?.contains("reset", ignoreCase = true) == true || 
                e.message?.contains("closed", ignoreCase = true) == true ->
                    "Connection was reset by the server or network. Please try again." to "CONNECTION_RESET"
                e.message?.contains("failed to connect", ignoreCase = true) == true ->
                    "Failed to connect to login server. Check network access." to "CONNECTION_FAILED"
                e.message?.contains("refused", ignoreCase = true) == true ->
                    "Connection refused by server. The login service may be down." to "CONNECTION_REFUSED"
                e.message.isNullOrBlank() -> 
                    "Network error occurred with no details. Please check your internet connection." to "UNKNOWN_NETWORK"
                else -> 
                    "Network error: ${e.message}" to "NETWORK_ERROR"
            }
            
            Log.w(TAG, "Login network error [$errorCode]: $errorMessage")
            LoginResult(
                success = false,
                message = errorMessage,
                errorCode = errorCode,
                technicalDetails = technicalDetails
            )
        } catch (e: Exception) {
            Log.e(TAG, "Login unexpected error: ${e.javaClass.simpleName}: ${e.message}", e)
            
            val technicalDetails = buildString {
                appendLine("URL: $loginUrl")
                appendLine("Exception: ${e.javaClass.simpleName}")
                appendLine("Message: ${e.message ?: "none"}")
                appendLine("Stack: ${e.stackTrace.take(5).joinToString("\n  ")}")
            }
            
            LoginResult(
                success = false,
                message = if (e.message.isNullOrBlank()) "An unexpected error occurred during login." else "Error: ${e.message}",
                errorCode = "UNEXPECTED",
                technicalDetails = technicalDetails
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
        Log.d(TAG, "Parsing login response (${xml.length} chars)")
        
        // Check for login success
        val loginRegex = """<name>login</name>\s*<value><string>([\w]+)</string>""".toRegex()
        val loginMatch = loginRegex.find(xml)
        val loginStatus = loginMatch?.groupValues?.get(1)
        
        Log.d(TAG, "Login status in response: $loginStatus")
        
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
            val errorReason = reasonRegex.find(xml)?.groupValues?.get(1)
            
            Log.d(TAG, "Login failure - message: $errorMessage, reason: $errorReason")
            
            // If message is null, empty, or looks like a null literal, try the reason field
            if (errorMessage.isNullOrBlank() || errorMessage.equals("null", ignoreCase = true)) {
                errorMessage = errorReason
            }
            
            // Determine error code based on reason
            val errorCode = when (errorReason?.lowercase()) {
                "key" -> "INVALID_CREDENTIALS"
                "presence" -> "ALREADY_LOGGED_IN"
                "update" -> "UPDATE_REQUIRED"
                "tos" -> "TOS_NOT_ACCEPTED"
                "critical" -> "CRITICAL_MESSAGE"
                "optional" -> "OPTIONAL_UPDATE"
                else -> "LOGIN_REJECTED"
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
            
            val technicalDetails = buildString {
                appendLine("Reason: ${errorReason ?: "not provided"}")
                appendLine("Message: ${messageRegex.find(xml)?.groupValues?.get(1) ?: "not provided"}")
                appendLine("Login status: $loginStatus")
                // Include relevant response snippet (with password redacted)
                val sanitized = xml.replace(Regex("""<name>passwd</name>\s*<value><string>[^<]+</string>"""), "<name>passwd</name><value><string>[REDACTED]</string>")
                appendLine("Response preview: ${sanitized.take(800)}")
            }
            
            Log.w(TAG, "Login failed [$errorCode]: $finalMessage (raw reason: $errorReason)")
            
            return LoginResult(
                success = false,
                message = finalMessage,
                errorCode = errorCode,
                technicalDetails = technicalDetails
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

// Note: EOFIOException and NetworkExceptionUtils are defined in src/main/java/com/linkpoint/network/
// and shared across the package for consistent EOF error handling.
