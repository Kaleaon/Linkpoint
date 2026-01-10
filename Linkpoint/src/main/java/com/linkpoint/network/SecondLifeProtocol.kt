package com.linkpoint.network

import android.content.Context
import android.util.Log
import com.linkpoint.core.ConnectionState
import com.linkpoint.core.RegionInfo
import com.linkpoint.LinkpointApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.security.MessageDigest
import java.util.UUID
import java.util.concurrent.TimeUnit

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
    
    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
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
        } catch (e: Exception) {
            Log.e(TAG, "Login error", e)
            app.sessionManager.setConnectionState(ConnectionState.ERROR)
            val errorMessage = e.message?.takeIf { it.isNotBlank() } 
                ?: "An unexpected error occurred. Please try again."
            LoginResult.Failure("Network error: $errorMessage")
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
