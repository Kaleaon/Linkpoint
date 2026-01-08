package com.linkpoint.network

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.security.MessageDigest
import java.util.concurrent.TimeUnit

/**
 * Handles Second Life grid authentication and connection
 */
class SecondLifeConnection {
    
    companion object {
        private const val TAG = "SLConnection"
        
        // Second Life login URLs
        private const val SL_LOGIN_URL = "https://login.agni.lindenlab.com/cgi-bin/login.cgi"
        private const val BETA_LOGIN_URL = "https://login.aditi.lindenlab.com/cgi-bin/login.cgi"
        
        // Grid URLs
        val GRIDS = mapOf(
            "Second Life" to SL_LOGIN_URL,
            "Second Life Beta" to BETA_LOGIN_URL,
            "OSGrid" to "http://login.osgrid.org/",
            "InWorldz" to "http://login.inworldz.com:8002/",
            "Kitely" to "https://login.kitely.com/"
        )
    }
    
    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
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
            LoginResult(
                success = false,
                message = "Network error: ${e.message}"
            )
        } catch (e: Exception) {
            Log.e(TAG, "Login error", e)
            LoginResult(
                success = false,
                message = "Error: ${e.message}"
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
