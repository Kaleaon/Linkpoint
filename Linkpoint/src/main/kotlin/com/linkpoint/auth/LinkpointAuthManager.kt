package com.linkpoint.auth

import android.util.Log
import kotlinx.coroutines.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * Modern Linkpoint Authentication Manager
 * Handles Second Life login with modern OAuth2 and LLSD protocols
 */
class LinkpointAuthManager {
    
    companion object {
        private const val TAG = "LinkpointAuth"
        
        // Modern Second Life authentication endpoints
        private const val SL_LOGIN_URI = "https://login.agni.lindenlab.com/cgi-bin/login.cgi"
        private const val SL_OAUTH_URI = "https://id.secondlife.com/openid/requesttoken"
        
        // Authentication timeouts
        private const val CONNECT_TIMEOUT = 30L
        private const val READ_TIMEOUT = 30L
        private const val WRITE_TIMEOUT = 30L
    }
    
    private val client = OkHttpClient.Builder()
        .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
        .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
        .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
        .build()
    
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    // Authentication state
    private var sessionId: String? = null
    private var agentId: String? = null
    private var sessionToken: String? = null
    private var isAuthenticated = false
    
    interface AuthCallback {
        fun onAuthSuccess(agentId: String, sessionId: String)
        fun onAuthFailure(error: String)
        fun onAuthProgress(message: String)
    }
    
    /**
     * Authenticate with Second Life using modern LLSD XML-RPC
     */
    suspend fun authenticateWithSecondLife(
        firstName: String,
        lastName: String,
        password: String,
        gridUri: String = SL_LOGIN_URI,
        callback: AuthCallback
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            Log.i(TAG, "Authenticating $firstName $lastName with Second Life...")
            callback.onAuthProgress("Connecting to Second Life...")
            
            // Build modern LLSD login request
            val loginRequest = buildLLSDLoginRequest(firstName, lastName, password)
            
            val requestBody = loginRequest.toRequestBody("application/llsd+xml".toMediaType())
            val request = Request.Builder()
                .url(gridUri)
                .post(requestBody)
                .header("Content-Type", "application/llsd+xml")
                .header("User-Agent", "Linkpoint/1.0.0 (Android)")
                .build()
            
            callback.onAuthProgress("Sending authentication request...")
            
            val response = client.newCall(request).execute()
            
            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                if (responseBody != null) {
                    callback.onAuthProgress("Processing authentication response...")
                    val success = parseLoginResponse(responseBody, callback)
                    
                    if (success) {
                        isAuthenticated = true
                        Log.i(TAG, "Authentication successful for $firstName $lastName")
                        callback.onAuthSuccess(agentId!!, sessionId!!)
                    }
                    return@withContext success
                }
            } else {
                val errorMessage = "Authentication failed: HTTP ${response.code}"
                Log.e(TAG, errorMessage)
                callback.onAuthFailure(errorMessage)
                return@withContext false
            }
            
            false
            
        } catch (e: IOException) {
            Log.e(TAG, "Network error during authentication", e)
            callback.onAuthFailure("Network error: ${e.message}")
            false
        } catch (e: Exception) {
            Log.e(TAG, "Authentication error", e)
            callback.onAuthFailure("Authentication error: ${e.message}")
            false
        }
    }
    
    /**
     * Build LLSD XML-RPC login request with modern parameters
     */
    private fun buildLLSDLoginRequest(firstName: String, lastName: String, password: String): String {
        // Modern Second Life login parameters
        return """<?xml version="1.0" encoding="UTF-8"?>
<llsd>
  <map>
    <key>first</key>
    <string>$firstName</string>
    <key>last</key>
    <string>$lastName</string>
    <key>passwd</key>
    <string>$password</string>
    <key>start</key>
    <string>last</string>
    <key>channel</key>
    <string>Linkpoint Android</string>
    <key>version</key>
    <string>1.0.0</string>
    <key>platform</key>
    <string>Android</string>
    <key>platform_version</key>
    <string>14.0</string>
    <key>mac</key>
    <string>00:00:00:00:00:00</string>
    <key>agree_to_tos</key>
    <boolean>true</boolean>
    <key>read_critical</key>
    <boolean>true</boolean>
    <key>viewer_digest</key>
    <string>00000000-0000-0000-0000-000000000000</string>
    <key>id0</key>
    <string>00000000-0000-0000-0000-000000000000</string>
    <key>options</key>
    <array>
      <string>inventory-root</string>
      <string>inventory-skeleton</string>
      <string>inventory-lib-root</string>
      <string>inventory-lib-owner</string>
      <string>inventory-skel-lib</string>
      <string>gestures</string>
      <string>display_names</string>
      <string>event_categories</string>
      <string>event_notifications</string>
      <string>classified_categories</string>
      <string>adult_compliant</string>
      <string>buddy-list</string>
      <string>ui-config</string>
      <string>voice-config</string>
      <string>tutorial_setting</string>
      <string>login-flags</string>
      <string>global-textures</string>
    </array>
  </map>
</llsd>"""
    }
    
    /**
     * Parse LLSD login response
     */
    private fun parseLoginResponse(responseBody: String, callback: AuthCallback): Boolean {
        try {
            // This is a simplified parser - production would use full LLSD parser
            // For now, we'll look for key elements in the XML response
            
            if (responseBody.contains("<key>login</key>") && responseBody.contains("<string>true</string>")) {
                // Extract agent_id
                val agentIdMatch = Regex("<key>agent_id</key>\\s*<uuid>([^<]+)</uuid>").find(responseBody)
                agentId = agentIdMatch?.groupValues?.get(1)
                
                // Extract session_id
                val sessionIdMatch = Regex("<key>session_id</key>\\s*<uuid>([^<]+)</uuid>").find(responseBody)
                sessionId = sessionIdMatch?.groupValues?.get(1)
                
                if (agentId != null && sessionId != null) {
                    Log.i(TAG, "Successfully parsed login response")
                    return true
                } else {
                    Log.e(TAG, "Failed to extract agent_id or session_id from response")
                    callback.onAuthFailure("Invalid response from server")
                    return false
                }
            } else if (responseBody.contains("<key>reason</key>")) {
                // Extract error reason
                val reasonMatch = Regex("<key>reason</key>\\s*<string>([^<]+)</string>").find(responseBody)
                val reason = reasonMatch?.groupValues?.get(1) ?: "Unknown error"
                Log.e(TAG, "Login failed: $reason")
                callback.onAuthFailure(reason)
                return false
            } else {
                Log.e(TAG, "Unexpected response format")
                callback.onAuthFailure("Unexpected response from server")
                return false
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing login response", e)
            callback.onAuthFailure("Failed to parse server response")
            return false
        }
    }
    
    /**
     * Logout and clear session
     */
    fun logout() {
        Log.i(TAG, "Logging out...")
        sessionId = null
        agentId = null
        sessionToken = null
        isAuthenticated = false
    }
    
    /**
     * Check if currently authenticated
     */
    fun isAuthenticated() = isAuthenticated
    
    /**
     * Get current agent ID
     */
    fun getAgentId() = agentId
    
    /**
     * Get current session ID
     */
    fun getSessionId() = sessionId
    
    /**
     * Cleanup resources
     */
    fun cleanup() {
        Log.i(TAG, "Cleaning up auth manager...")
        scope.cancel()
        logout()
    }
}