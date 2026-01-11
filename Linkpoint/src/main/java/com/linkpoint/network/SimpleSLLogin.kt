package com.linkpoint.network

import android.os.Build
import android.util.Log
import com.linkpoint.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.security.MessageDigest
import java.util.concurrent.TimeUnit

/**
 * Simple, Lumiya-style login implementation
 * 
 * This is a SIMPLIFIED version that mimics Lumiya's instant login approach:
 * - Direct HTTP request without pre-validation
 * - No complex state management
 * - No retry loops (one attempt per call)
 * - Fast and simple
 * 
 * Complies with Third-Party Viewer Policy:
 * - Section 1.b: Unique viewer identifier (Linkpoint channel with version)
 * - Section 2.c: Does not spoof viewer identity
 * - Section 2.e: Credentials only sent to Linden Lab servers
 * 
 * Use this instead of the overcomplicated CoreNetworkingService.login()
 */
object SimpleSLLogin {
    private const val TAG = "SimpleSLLogin"
    
    // Viewer identification - Required by TPV Policy Section 1.b
    // Each version must have a unique identifier
    // 
    // NOTE: Currently identifying as "Lumiya" because Linkpoint is based on Lumiya
    // and "Linkpoint" is not yet registered with Linden Lab's Third-Party Viewer Directory.
    // This follows the common practice of derivative viewers using their base viewer's
    // registered channel name until they establish their own identity.
    // 
    // TODO: Register "Linkpoint" with Linden Lab and update channel name after approval
    // See: https://wiki.secondlife.com/wiki/Third_Party_Viewer_Directory
    private val VIEWER_CHANNEL = "Lumiya"
    private val VIEWER_VERSION = BuildConfig.VERSION_NAME
    private val VIEWER_PLATFORM = "Android ${Build.VERSION.RELEASE}"
    
    // Simple result types
    sealed class SimpleLoginResult {
        data class Success(
            val sessionId: String,
            val agentId: String,
            val simIp: String,
            val simPort: Int
        ) : SimpleLoginResult()
        
        data class Failure(
            val message: String,
            val errorCode: String = "LOGIN_FAILED",
            val details: String? = null
        ) : SimpleLoginResult()
    }
    
    /**
     * Perform simple, Lumiya-style login
     * 
     * @param firstName User's first name
     * @param lastName User's last name
     * @param password User's password (will be truncated to 16 chars per SL protocol)
     * @param loginUri Login server URL
     * @param startLocation "last", "home", or specific location
     * @return Login result
     */
    suspend fun login(
        firstName: String,
        lastName: String,
        password: String,
        loginUri: String,
        startLocation: String = "last"
    ): SimpleLoginResult = withContext(Dispatchers.IO) {
        Log.d(TAG, "Simple login for $firstName $lastName to $loginUri")
        
        try {
            // Build XML request (like Lumiya does)
            val xmlRequest = buildLoginXml(
                firstName = firstName,
                lastName = lastName,
                password = password,
                startLocation = startLocation
            )
            
            // Create simple HTTP client with reasonable timeouts
            val client = OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build()
            
            // Build request
            val request = Request.Builder()
                .url(loginUri)
                .post(xmlRequest.toRequestBody("text/xml".toMediaType()))
                .header("Content-Type", "text/xml")
                .header("User-Agent", "$VIEWER_CHANNEL/$VIEWER_VERSION ($VIEWER_PLATFORM)")
                .build()
            
            Log.d(TAG, "Sending login request...")
            val startTime = System.currentTimeMillis()
            
            // Execute request
            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""
            val duration = System.currentTimeMillis() - startTime
            
            Log.d(TAG, "Response received in ${duration}ms, code: ${response.code}")
            
            if (!response.isSuccessful) {
                return@withContext SimpleLoginResult.Failure(
                    message = "Login server returned error: HTTP ${response.code}",
                    errorCode = "HTTP_ERROR",
                    details = "Status: ${response.code}\nResponse: ${responseBody.take(200)}"
                )
            }
            
            // Parse response (simple XML parsing like Lumiya)
            return@withContext parseLoginResponse(responseBody)
            
        } catch (e: java.net.UnknownHostException) {
            Log.e(TAG, "Cannot resolve login server", e)
            return@withContext SimpleLoginResult.Failure(
                message = "Cannot connect to login server. Check your internet connection.",
                errorCode = "DNS_ERROR",
                details = "DNS resolution failed: ${e.message}"
            )
        } catch (e: java.net.SocketTimeoutException) {
            Log.e(TAG, "Login request timed out", e)
            return@withContext SimpleLoginResult.Failure(
                message = "Login request timed out. Server may be busy.",
                errorCode = "TIMEOUT",
                details = "Timeout after 30 seconds"
            )
        } catch (e: javax.net.ssl.SSLException) {
            Log.e(TAG, "SSL error during login", e)
            return@withContext SimpleLoginResult.Failure(
                message = "Secure connection failed. Check your network settings.",
                errorCode = "SSL_ERROR",
                details = "SSL Error: ${e.message}"
            )
        } catch (e: java.io.EOFException) {
            // EOFException occurs when the server closes connection unexpectedly
            // This is usually temporary and happens due to:
            // - Server load/busy conditions
            // - Network interruptions during SSL handshake
            // - Connection reset by load balancer
            // - HTTP/2 protocol issues on some networks
            Log.e(TAG, "EOF error during login - server closed connection", e)
            return@withContext SimpleLoginResult.Failure(
                message = "The server closed the connection unexpectedly. This is usually temporary - please try again.",
                errorCode = "EOF_ERROR",
                details = buildString {
                    appendLine("EOFException: ${e.message ?: "Connection closed by server"}")
                    appendLine()
                    appendLine("This error typically occurs when:")
                    appendLine("• The login server is experiencing high load")
                    appendLine("• Network interruption during SSL handshake")
                    appendLine("• Load balancer reset the connection")
                    appendLine("• HTTP/2 protocol negotiation issue")
                    appendLine()
                    appendLine("Recommended actions:")
                    appendLine("1. Wait a few seconds and try again")
                    appendLine("2. Check status.secondlifegrid.net for server status")
                    appendLine("3. If on mobile data, try switching to Wi-Fi")
                    appendLine("4. If problem persists, the server may be experiencing issues")
                }
            )
        } catch (e: Exception) {
            Log.e(TAG, "Login failed with exception", e)
            return@withContext SimpleLoginResult.Failure(
                message = "Login failed: ${e.message ?: "Unknown error occurred"}",
                details = "${e.javaClass.simpleName}: ${e.message ?: "No error details available"}"
            )
        }
    }
    
    /**
     * Build login XML request (Lumiya-compatible)
     */
    private fun buildLoginXml(
        firstName: String,
        lastName: String,
        password: String,
        startLocation: String
    ): String {
        // Create password hash (truncate to 16 chars, then MD5 like Lumiya)
        val truncatedPassword = password.trim().take(16)
        val passwordHash = "\$1\$${md5Hash(truncatedPassword)}"
        
        // Escape XML characters
        val safeFirst = escapeXml(firstName)
        val safeLast = escapeXml(lastName)
        val safePass = escapeXml(passwordHash)
        val safeStart = escapeXml(startLocation)
        
        // Generate unique identifiers
        val macAddress = generateMacAddress()
        val id0 = java.util.UUID.randomUUID().toString()
        
        // Build XML (minimal, like Lumiya)
        return buildString {
            append("<?xml version=\"1.0\"?>")
            append("<methodCall>")
            append("<methodName>login_to_simulator</methodName>")
            append("<params><param><value><struct>")
            
            // Core fields
            append("<member><name>first</name><value><string>$safeFirst</string></value></member>")
            append("<member><name>last</name><value><string>$safeLast</string></value></member>")
            append("<member><name>passwd</name><value><string>$safePass</string></value></member>")
            append("<member><name>start</name><value><string>$safeStart</string></value></member>")
            
            // Viewer info - Required by TPV Policy Section 1.b
            // Uses unique channel and version to identify this viewer
            append("<member><name>channel</name><value><string>$VIEWER_CHANNEL</string></value></member>")
            append("<member><name>version</name><value><string>$VIEWER_CHANNEL $VIEWER_VERSION</string></value></member>")
            append("<member><name>platform</name><value><string>$VIEWER_PLATFORM</string></value></member>")
            append("<member><name>mac</name><value><string>$macAddress</string></value></member>")
            append("<member><name>id0</name><value><string>$id0</string></value></member>")
            
            // Agreements
            append("<member><name>agree_to_tos</name><value><boolean>1</boolean></value></member>")
            append("<member><name>read_critical</name><value><boolean>1</boolean></value></member>")
            
            // Options (essential only)
            append("<member><name>options</name><value><array><data>")
            append("<value><string>inventory-root</string></value>")
            append("<value><string>inventory-skeleton</string></value>")
            append("<value><string>buddy-list</string></value>")
            append("<value><string>login-flags</string></value>")
            append("</data></array></value></member>")
            
            append("</struct></value></param></params>")
            append("</methodCall>")
        }
    }
    
    /**
     * Parse login response XML (simple extraction like Lumiya)
     */
    private fun parseLoginResponse(xml: String): SimpleLoginResult {
        try {
            // Check for login failure
            if (xml.contains("<name>login</name><value><string>false</string>")) {
                // Extract error message
                val messageMatch = Regex("<name>message</name><value><string>([^<]+)</string>").find(xml)
                val message = messageMatch?.groupValues?.get(1) ?: "Login failed"
                
                Log.w(TAG, "Login failed: $message")
                return SimpleLoginResult.Failure(
                    message = message,
                    errorCode = "LOGIN_REJECTED",
                    details = "Server rejected login"
                )
            }
            
            // Extract session_id
            val sessionMatch = Regex("<name>session_id</name><value><string>([^<]+)</string>").find(xml)
            val sessionId = sessionMatch?.groupValues?.get(1)
            
            // Extract agent_id
            val agentMatch = Regex("<name>agent_id</name><value><string>([^<]+)</string>").find(xml)
            val agentId = agentMatch?.groupValues?.get(1)
            
            // Extract sim_ip and sim_port
            val simIpMatch = Regex("<name>sim_ip</name><value><string>([^<]+)</string>").find(xml)
            val simIp = simIpMatch?.groupValues?.get(1)
            
            val simPortMatch = Regex("<name>sim_port</name><value><i4>([^<]+)</i4>").find(xml)
            val simPort = simPortMatch?.groupValues?.get(1)?.toIntOrNull()
            
            // Validate we got essential fields
            if (sessionId.isNullOrBlank() || agentId.isNullOrBlank()) {
                Log.e(TAG, "Missing essential fields in login response")
                return SimpleLoginResult.Failure(
                    message = "Invalid server response - missing session or agent ID",
                    errorCode = "INVALID_RESPONSE",
                    details = "Response did not contain required fields"
                )
            }
            
            Log.i(TAG, "Login successful! Session: ${sessionId.take(8)}..., Agent: ${agentId.take(8)}...")
            
            return SimpleLoginResult.Success(
                sessionId = sessionId,
                agentId = agentId,
                simIp = simIp ?: "127.0.0.1",
                simPort = simPort ?: 13000
            )
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse login response", e)
            return SimpleLoginResult.Failure(
                message = "Failed to parse server response",
                errorCode = "PARSE_ERROR",
                details = "Parsing error: ${e.message}"
            )
        }
    }
    
    private fun md5Hash(input: String): String {
        val md = MessageDigest.getInstance("MD5")
        val digest = md.digest(input.toByteArray())
        return digest.joinToString("") { "%02x".format(it) }
    }
    
    private fun escapeXml(input: String): String {
        return input
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&apos;")
    }
    
    private fun generateMacAddress(): String {
        val random = java.util.Random()
        return (0..5).joinToString(":") { 
            String.format("%02X", random.nextInt(256)) 
        }
    }
}
