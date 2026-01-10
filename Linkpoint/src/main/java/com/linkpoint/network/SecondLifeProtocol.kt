package com.linkpoint.network

import android.content.Context
import android.util.Log
import com.linkpoint.core.ConnectionState
import com.linkpoint.core.RegionInfo
import com.linkpoint.LinkpointApp
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
import java.util.UUID
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLException

/**
 * Second Life protocol implementation
 * Handles login, message sending, and grid communication
 */
class SecondLifeProtocol(private val context: Context) {
    
    companion object {
        private const val TAG = "SLProtocol"
        private const val VIEWER_NAME = "Linkpoint"
        private const val VIEWER_VERSION = "1.0.0"
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
    
    /**
     * Perform login to the grid
     */
    suspend fun login(
        firstName: String,
        lastName: String,
        password: String,
        loginUri: String,
        startLocation: String = "last"
    ): LoginResult = withContext(Dispatchers.IO) {
        val app = LinkpointApp.getInstance()
        app.sessionManager.setConnectionState(ConnectionState.CONNECTING)
        
        Log.d(TAG, "Attempting login for $firstName $lastName")
        
        // Create password hash
        val passwordHash = "\$1\$${md5Hash(password)}"
        
        // Build XMLRPC request
        val xmlRequest = buildLoginXml(
            firstName = firstName,
            lastName = lastName,
            passwordHash = passwordHash,
            startLocation = startLocation
        )
        
        try {
            val request = Request.Builder()
                .url(loginUri)
                .post(xmlRequest.toRequestBody("text/xml".toMediaType()))
                .header("Content-Type", "text/xml")
                .header("User-Agent", "$VIEWER_NAME/$VIEWER_VERSION (Android)")
                .build()
            
            val response = httpClient.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""
            
            if (response.isSuccessful) {
                parseLoginResponse(responseBody, firstName, lastName)
            } else {
                app.sessionManager.setConnectionState(ConnectionState.ERROR)
                LoginResult.Failure("Server error: ${response.code}")
            }
        } catch (e: IOException) {
            Log.e(TAG, "Login network error", e)
            app.sessionManager.setConnectionState(ConnectionState.ERROR)
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
            LoginResult.Failure(errorMessage)
        } catch (e: Exception) {
            Log.e(TAG, "Login error", e)
            app.sessionManager.setConnectionState(ConnectionState.ERROR)
            val errorMessage = e.message?.takeIf { it.isNotBlank() } 
                ?: "An unexpected error occurred. Please try again."
            LoginResult.Failure("Error: $errorMessage")
        }
    }
    
    private fun buildLoginXml(
        firstName: String,
        lastName: String,
        passwordHash: String,
        startLocation: String
    ): String {
        val safeFirstName = escapeXml(firstName)
        val safeLastName = escapeXml(lastName)
        val safePassword = escapeXml(passwordHash)
        val safeStart = escapeXml(startLocation)
        
        return """
<?xml version="1.0"?>
<methodCall>
    <methodName>login_to_simulator</methodName>
    <params>
        <param>
            <value>
                <struct>
                    <member><name>first</name><value><string>$safeFirstName</string></value></member>
                    <member><name>last</name><value><string>$safeLastName</string></value></member>
                    <member><name>passwd</name><value><string>$safePassword</string></value></member>
                    <member><name>start</name><value><string>$safeStart</string></value></member>
                    <member><name>channel</name><value><string>$VIEWER_NAME</string></value></member>
                    <member><name>version</name><value><string>$VIEWER_NAME $VIEWER_VERSION</string></value></member>
                    <member><name>platform</name><value><string>Android</string></value></member>
                    <member><name>platform_version</name><value><string>${android.os.Build.VERSION.RELEASE}</string></value></member>
                    <member><name>mac</name><value><string>00:00:00:00:00:00</string></value></member>
                    <member><name>id0</name><value><string>00000000-0000-0000-0000-000000000000</string></value></member>
                    <member><name>agree_to_tos</name><value><boolean>1</boolean></value></member>
                    <member><name>read_critical</name><value><boolean>1</boolean></value></member>
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
    
    private fun parseLoginResponse(xml: String, firstName: String, lastName: String): LoginResult {
        val app = LinkpointApp.getInstance()
        
        // Check for login success
        val loginRegex = """<name>login</name>\s*<value><string>([\w]+)</string>""".toRegex()
        val loginMatch = loginRegex.find(xml)
        val loginStatus = loginMatch?.groupValues?.get(1)
        
        if (loginStatus == "true") {
            // Extract session info
            val sessionId = extractValue(xml, "session_id") ?: ""
            val agentIdStr = extractValue(xml, "agent_id") ?: ""
            val secureSessionId = extractValue(xml, "secure_session_id") ?: ""
            val simIp = extractValue(xml, "sim_ip") ?: ""
            val simPort = extractIntValue(xml, "sim_port")
            val regionName = extractValue(xml, "region_x")?.let { 
                "Region ${it.toIntOrNull() ?: 0 / 256}"
            } ?: "Unknown"
            
            val agentId = try { UUID.fromString(agentIdStr) } catch (e: Exception) { UUID.randomUUID() }
            
            val regionInfo = RegionInfo(
                name = regionName,
                handle = 0,
                x = 128,
                y = 128,
                simIP = simIp,
                simPort = simPort
            )
            
            app.sessionManager.onLoginSuccess(
                sessionId = sessionId,
                agentId = agentId,
                secureSessionId = secureSessionId,
                firstName = firstName,
                lastName = lastName,
                regionInfo = regionInfo
            )
            
            Log.i(TAG, "Login successful!")
            return LoginResult.Success(agentId, sessionId)
        } else {
            // Extract error message - try multiple fields that SL might use
            var errorMessage = extractValue(xml, "message")
            
            // If message is null, empty, or looks like a null literal, try the reason field
            if (errorMessage.isNullOrBlank() || errorMessage.equals("null", ignoreCase = true)) {
                errorMessage = extractValue(xml, "reason")
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
            
            app.sessionManager.setConnectionState(ConnectionState.ERROR)
            Log.w(TAG, "Login failed: $finalMessage (raw: $errorMessage)")
            return LoginResult.Failure(finalMessage)
        }
    }
    
    private fun extractValue(xml: String, name: String): String? {
        val regex = """<name>$name</name>\s*<value><string>([^<]+)</string>""".toRegex()
        return regex.find(xml)?.groupValues?.get(1)
    }
    
    private fun extractIntValue(xml: String, name: String): Int {
        val regex = """<name>$name</name>\s*<value><i4>(\d+)</i4>""".toRegex()
        return regex.find(xml)?.groupValues?.get(1)?.toIntOrNull() ?: 0
    }
    
    private fun escapeXml(input: String): String {
        return input
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&apos;")
    }
    
    private fun md5Hash(input: String): String {
        val md = MessageDigest.getInstance("MD5")
        val digest = md.digest(input.toByteArray())
        return digest.joinToString("") { "%02x".format(it) }
    }
    
    /**
     * Send a chat message
     */
    suspend fun sendChat(message: String, channel: Int = 0, type: ChatType = ChatType.NORMAL) {
        Log.d(TAG, "Sending chat: $message on channel $channel")
        // TODO: Implement UDP message sending
    }
    
    /**
     * Request teleport to location
     */
    suspend fun teleport(regionName: String, x: Float, y: Float, z: Float): TeleportResult {
        Log.d(TAG, "Requesting teleport to $regionName ($x, $y, $z)")
        // TODO: Implement teleport request
        return TeleportResult.Success(regionName)
    }
    
    /**
     * Disconnect from grid
     */
    fun disconnect() {
        Log.i(TAG, "Disconnecting from grid")
        LinkpointApp.getInstance().sessionManager.disconnect()
    }
}

sealed class LoginResult {
    data class Success(val agentId: UUID, val sessionId: String) : LoginResult()
    data class Failure(val message: String) : LoginResult()
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
