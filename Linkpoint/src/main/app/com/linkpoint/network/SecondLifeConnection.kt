package com.linkpoint.network

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import okhttp3.ConnectionPool
import okhttp3.Dns
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.net.InetAddress
import java.net.UnknownHostException
import java.security.MessageDigest
import java.util.concurrent.TimeUnit

/**
 * Handles Second Life grid authentication and connection
 * 
 * Based on Lumiya's robust connection handling for mobile networks:
 * - Custom DNS with fallback to DNS-over-HTTPS
 * - Hardcoded fallback IPs for critical servers
 * - Retry logic with exponential backoff
 * - Long timeouts for mobile networks
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
        
        // Retry configuration (from Lumiya)
        private const val MAX_RETRY_ATTEMPTS = 3
        private const val INITIAL_RETRY_DELAY_MS = 1000L
        private const val RETRY_BACKOFF_MULTIPLIER = 2.0
    }
    
    /**
     * Custom DNS resolver with fallback for mobile networks
     * Handles cases where mobile carrier DNS fails or blocks certain domains
     */
    private class SecondLifeDns : Dns {
        private val systemDns = Dns.SYSTEM
        
        // Fallback IP addresses for Second Life login servers
        // These are used when DNS resolution fails (common on some mobile networks)
        private val fallbackIps = mapOf(
            "login.agni.lindenlab.com" to listOf("216.82.57.58"),
            "login.aditi.lindenlab.com" to listOf("216.82.45.35")
        )
        
        override fun lookup(hostname: String): List<InetAddress> {
            // First try system DNS
            return try {
                val addresses = systemDns.lookup(hostname)
                if (addresses.isNotEmpty()) {
                    Log.d(TAG, "DNS resolved $hostname via system DNS")
                    addresses
                } else {
                    throw UnknownHostException(hostname)
                }
            } catch (e: UnknownHostException) {
                // Try DNS-over-HTTPS fallback
                Log.d(TAG, "System DNS failed for $hostname, trying DNS-over-HTTPS")
                try {
                    resolveDnsOverHttps(hostname)
                } catch (dohException: Exception) {
                    Log.w(TAG, "DNS-over-HTTPS failed for $hostname, trying hardcoded fallback")
                    // Fall back to hardcoded IPs for critical Second Life servers
                    fallbackIps[hostname]?.mapNotNull { ip ->
                        try {
                            InetAddress.getByName(ip)
                        } catch (e: Exception) {
                            null
                        }
                    }?.takeIf { it.isNotEmpty() }
                        ?: throw UnknownHostException("Could not resolve $hostname")
                }
            }
        }
        
        private fun resolveDnsOverHttps(hostname: String): List<InetAddress> {
            // Use Google's DNS-over-HTTPS service
            val dohClient = OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build()
            
            val request = Request.Builder()
                .url("https://dns.google/resolve?name=$hostname&type=A")
                .header("Accept", "application/dns-json")
                .build()
            
            val response = dohClient.newCall(request).execute()
            if (!response.isSuccessful) {
                throw IOException("DNS-over-HTTPS failed with code ${response.code}")
            }
            
            val body = response.body?.string() ?: throw IOException("Empty DNS response")
            
            // Parse JSON response to extract IP addresses
            val addresses = mutableListOf<InetAddress>()
            val dataPattern = """"data"\s*:\s*"([^"]+)"""".toRegex()
            dataPattern.findAll(body).forEach { match ->
                val ip = match.groupValues[1]
                // Only accept valid IPv4 addresses
                if (ip.matches(Regex("""\d+\.\d+\.\d+\.\d+"""))) {
                    try {
                        addresses.add(InetAddress.getByName(ip))
                        Log.d(TAG, "DNS-over-HTTPS resolved $hostname -> $ip")
                    } catch (e: Exception) {
                        // Ignore invalid addresses
                    }
                }
            }
            
            if (addresses.isEmpty()) {
                throw IOException("No valid addresses found for $hostname")
            }
            
            return addresses
        }
    }
    
    // HTTP client configured for mobile networks (based on Lumiya's settings)
    private val httpClient = OkHttpClient.Builder()
        .dns(SecondLifeDns())  // Custom DNS with fallback
        .connectionPool(ConnectionPool(8, 5, TimeUnit.MINUTES))  // Keep connections alive
        .connectTimeout(60, TimeUnit.SECONDS)  // Long timeout for mobile networks
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)  // Automatic retry on connection failure
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
     * Includes retry logic with exponential backoff for mobile network reliability
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
        
        // Attempt login with retry logic
        var lastError: String = "Unknown error"
        var attemptNumber = 0
        
        while (attemptNumber < MAX_RETRY_ATTEMPTS) {
            attemptNumber++
            Log.d(TAG, "Login attempt $attemptNumber/$MAX_RETRY_ATTEMPTS")
            
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
                    val result = parseLoginResponse(responseBody)
                    if (result.success) {
                        return@withContext result
                    } else {
                        // Authentication failed - don't retry, return immediately
                        // (retrying won't help with wrong password)
                        return@withContext result
                    }
                } else {
                    lastError = "Server error: ${response.code}"
                    // Server error - might be temporary, worth retrying
                }
            } catch (e: IOException) {
                Log.w(TAG, "Login attempt $attemptNumber failed: ${e.message}", e)
                lastError = when {
                    e.message.isNullOrBlank() -> "Unable to connect. Please check your internet connection and try again."
                    e.message!!.contains("timeout", ignoreCase = true) -> "Connection timed out. Please check your network and try again."
                    e.message!!.contains("host", ignoreCase = true) || 
                    e.message!!.contains("resolve", ignoreCase = true) -> "Could not reach server. Please check your internet connection."
                    e.message!!.contains("ssl", ignoreCase = true) || 
                    e.message!!.contains("certificate", ignoreCase = true) -> "Secure connection failed. Please try again."
                    else -> "Network error: ${e.message}"
                }
            } catch (e: Exception) {
                Log.e(TAG, "Login attempt $attemptNumber failed with unexpected error", e)
                lastError = if (e.message.isNullOrBlank()) {
                    "An unexpected error occurred. Please try again."
                } else {
                    "Error: ${e.message}"
                }
            }
            
            // If we haven't exhausted retries, wait with exponential backoff before retrying
            if (attemptNumber < MAX_RETRY_ATTEMPTS) {
                val delayMs = (INITIAL_RETRY_DELAY_MS * Math.pow(RETRY_BACKOFF_MULTIPLIER, (attemptNumber - 1).toDouble())).toLong()
                Log.d(TAG, "Retrying in ${delayMs}ms...")
                delay(delayMs)
            }
        }
        
        // All retries exhausted
        Log.e(TAG, "All login attempts failed after $MAX_RETRY_ATTEMPTS tries")
        LoginResult(
            success = false,
            message = lastError
        )
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
            // Extract error message
            val messageRegex = """<name>message</name>\s*<value><string>([^<]+)</string>""".toRegex()
            val errorMessage = messageRegex.find(xml)?.groupValues?.get(1) ?: "Unknown error"
            
            Log.w(TAG, "Login failed: $errorMessage")
            
            return LoginResult(
                success = false,
                message = errorMessage
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
}
