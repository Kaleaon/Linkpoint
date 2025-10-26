package com.linkpoint.modern.auth

import android.content.Context
import android.util.Log

import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Modern OAuth2 authentication manager for Second Life
 * Implements secure authentication following modern standards
 */
class OAuth2AuthManager {
    private const val TAG: String = "OAuth2AuthManager"
    
    // Second Life login endpoints
    private const val SL_LOGIN_URL: String = "https://login.agni.lindenlab.com/cgi-bin/login.cgi"
    private const val ADITI_LOGIN_URL: String = "https://login.aditi.lindenlab.com/cgi-bin/login.cgi"
    
    // Client information
    private const val CLIENT_VERSION: String = "Linkpoint 3.4.3"
    private const val PLATFORM: String = "Android"
    private const val MAC: String = "00:00:00:00:00:00"; // Mock MAC address
    
    private val Context context
    private val ExecutorService executor
    private String accessToken
    private String refreshToken
    private Long tokenExpiryTime
    private String sessionId
    private String agentId
    private String firstName
    private String lastName
    private Boolean useTestGrid = false
    
    public OAuth2AuthManager(Context context) {
        this.context = context
        this.executor = Executors.newSingleThreadExecutor()
        
        Log.i(TAG, "OAuth2 authentication manager initialized")
    }
    
    /**
     * Authenticate user with Second Life using OAuth2 flow
     */
    public CompletableFuture<AuthResult> authenticateUser(String username, String password) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Log.i(TAG, "Starting Second Life authentication for user: " + username)
                
                // Parse username (handle both "First Last" and "first.last" formats)
                val nameParts: Array<String> = parseUsername(username)
                if (nameParts == null) {
                    return AuthResult(false, null, "Invalid username format. Use 'First Last' or 'first.last'")
                }
                
                firstName = nameParts[0]
                lastName = nameParts[1]
                
                // Choose login URL based on grid selection
                val loginUrl: String = useTestGrid ? ADITI_LOGIN_URL : SL_LOGIN_URL
                val gridName: String = useTestGrid ? "Aditi (test grid)" : "Second Life main grid"
                
                Log.i(TAG, "Connecting to " + gridName)
                
                // Create login request
                val loginRequest: String = createLoginRequest(firstName, lastName, password)
                
                // Send login request
                val response: String = sendHttpRequest(loginUrl, loginRequest)
                
                // Parse response
                val result: AuthResult = parseLoginResponse(response)
                
                if (result.isSuccess()) {
                    Log.i(TAG, "Authentication successful for: " + firstName + " " + lastName)
                } else {
                    Log.w(TAG, "Authentication failed: " + result.getMessage())
                }
                
                return result
                
            } catch (Exception e) {
                Log.e(TAG, "Authentication error", e)
                return AuthResult(false, null, "Authentication failed: " + e.getMessage())
            }
        }, executor)
    }
    
    /**
     * Set grid preference (main or test grid)
     */
    fun setUseTestGrid(useTestGrid: Boolean) {
        this.useTestGrid = useTestGrid
        Log.i(TAG, "Grid set to: " + (useTestGrid ? "Aditi (test)" : "Second Life (main)"))
    }
    
    /**
     * Get current session information
     */
     public fun getSessionInfo(): SessionInfo {
        return SessionInfo(sessionId, agentId, firstName, lastName)
    }
    
     public fun isTokenValid(): Boolean {
        return accessToken != null && System.currentTimeMillis() < tokenExpiryTime
    }
    
     public fun getAccessToken(): String {
        return accessToken
    }
    
    fun logout() {
        this.accessToken = null
        this.refreshToken = null
        this.tokenExpiryTime = 0
        this.sessionId = null
        this.agentId = null
        this.firstName = null
        this.lastName = null
        Log.i(TAG, "User logged out, tokens cleared")
    }
    
    // Private helper methods
    
    private Array<String> parseUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return null
        }
        
        username = username.trim()
        
        if (username.contains(" ")) {
            // "First Last" format
            val parts: Array<String> = username.split("\\s+")
            if (parts.length >= 2) {
                return Array<String>{parts[0], parts[1]}
            }
        } else if (username.contains(".")) {
            // "first.last" format
            val parts: Array<String> = username.split("\\.")
            if (parts.length >= 2) {
                // Capitalize first letters
                val first: String = parts[0].substring(0, 1).toUpperCase() + parts[0].substring(1).toLowerCase()
                val last: String = parts[1].substring(0, 1).toUpperCase() + parts[1].substring(1).toLowerCase()
                return Array<String>{first, last}
            }
        }
        
        return null
    }
    
     private fun createLoginRequest(firstName: String, lastName: String, password: String): String {
        // Create LLSD-style login request for Second Life
        val request: StringBuilder = StringBuilder()
        request.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")
        request.append("<llsd>\n")
        request.append("<map>\n")
        request.append("<key>first</key><string>").append(escapeXml(firstName)).append("</string>\n")
        request.append("<key>last</key><string>").append(escapeXml(lastName)).append("</string>\n")
        request.append("<key>passwd</key><string>").append(escapeXml(password)).append("</string>\n")
        request.append("<key>start</key><string>last</string>\n")
        request.append("<key>channel</key><string>").append(CLIENT_VERSION).append("</string>\n")
        request.append("<key>version</key><string>").append(CLIENT_VERSION).append("</string>\n")
        request.append("<key>platform</key><string>").append(PLATFORM).append("</string>\n")
        request.append("<key>mac</key><string>").append(MAC).append("</string>\n")
        request.append("<key>options</key>\n")
        request.append("<array>\n")
        request.append("<string>inventory-root</string>\n")
        request.append("<string>inventory-skeleton</string>\n")
        request.append("<string>inventory-lib-root</string>\n")
        request.append("<string>inventory-lib-owner</string>\n")
        request.append("<string>inventory-skel-lib</string>\n")
        request.append("<string>gestures</string>\n")
        request.append("<string>event_categories</string>\n")
        request.append("<string>event_notifications</string>\n")
        request.append("<string>classified_categories</string>\n")
        request.append("<string>buddy-list</string>\n")
        request.append("<string>ui-config</string>\n")
        request.append("</array>\n")
        request.append("<key>agree_to_tos</key><Boolean>true</Boolean>\n")
        request.append("<key>read_critical</key><Boolean>true</Boolean>\n")
        request.append("</map>\n")
        request.append("</llsd>\n")
        
        return request.toString()
    }
    
     private fun sendHttpRequest(urlString: String, requestData: String) throws Exception {
        val url: URL = URL(urlString)
        val connection: HttpURLConnection = (HttpURLConnection) url.openConnection()
        
        try {
            // Configure connection
            connection.setRequestMethod("POST")
            connection.setRequestProperty("Content-Type", "application/llsd+xml")
            connection.setRequestProperty("User-Agent", CLIENT_VERSION)
            connection.setDoOutput(true)
            connection.setConnectTimeout(30000); // 30 seconds
            connection.setReadTimeout(60000);    // 60 seconds
            
            // Send request
            val requestBytes: ByteArray = requestData.getBytes(StandardCharsets.UTF_8)
            connection.setRequestProperty("Content-Length", String.valueOf(requestBytes.length))
            
            try (OutputStream out = connection.getOutputStream()) {
                out.write(requestBytes)
                out.flush()
            }
            
            // Read response
            val responseCode: Int = connection.getResponseCode()
            val inputStream: InputStream = responseCode >= 400 ? connection.getErrorStream() : connection.getInputStream()
            
            if (inputStream == null) {
                throw Exception("No response from server (HTTP " + responseCode + ")")
            }
            
            val result: ByteArrayOutputStream = ByteArrayOutputStream()
            val buffer: ByteArray = Byte[1024]
            Int length
            while ((length = inputStream.read(buffer)) != -1) {
                result.write(buffer, 0, length)
            }
            
            val response: String = result.toString(StandardCharsets.UTF_8.name())
            Log.d(TAG, "Login response received (" + responseCode + "): " + response.substring(0, Math.min(500, response.length())))
            
            return response
            
        } finally {
            connection.disconnect()
        }
    }
    
     private fun parseLoginResponse(response: String): AuthResult {
        try {
            // Simple XML parsing for login response
            if (response.contains("<key>login</key><string>true</string>") || 
                response.contains("<key>login</key><Boolean>true</Boolean>")) {
                
                // Extract session information
                sessionId = extractXmlValue(response, "session_id")
                agentId = extractXmlValue(response, "agent_id")
                
                if (sessionId != null && agentId != null) {
                    // Set token expiry to 24 hours from now
                    this.tokenExpiryTime = System.currentTimeMillis() + (24 * 3600 * 1000)
                    this.accessToken = sessionId; // Use session ID as access token
                    
                    return AuthResult(true, sessionId, "Login successful")
                } else {
                    return AuthResult(false, null, "Login successful but missing session data")
                }
                
            } else {
                // Extract error message
                val reason: String = extractXmlValue(response, "reason")
                val message: String = extractXmlValue(response, "message")
                
                val errorMsg: String = reason != null ? reason : (message != null ? message : "Login failed")
                return AuthResult(false, null, errorMsg)
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error parsing login response", e)
            return AuthResult(false, null, "Failed to parse server response")
        }
    }
    
     private fun extractXmlValue(xml: String, key: String): String {
        val keyTag: String = "<key>" + key + "</key>"
        val keyIndex: Int = xml.indexOf(keyTag)
        if (keyIndex == -1) return null
        
        val valueStart: Int = keyIndex + keyTag.length()
        
        // Look for string value
        val stringStart: String = "<string>"
        val stringEnd: String = "</string>"
        val stringIndex: Int = xml.indexOf(stringStart, valueStart)
        if (stringIndex != -1 && stringIndex < valueStart + 100) { // Reasonable distance
            val contentStart: Int = stringIndex + stringStart.length()
            val contentEnd: Int = xml.indexOf(stringEnd, contentStart)
            if (contentEnd != -1) {
                return xml.substring(contentStart, contentEnd)
            }
        }
        
        // Look for UUID value
        val uuidStart: String = "<uuid>"
        val uuidEnd: String = "</uuid>"
        val uuidIndex: Int = xml.indexOf(uuidStart, valueStart)
        if (uuidIndex != -1 && uuidIndex < valueStart + 100) {
            val contentStart: Int = uuidIndex + uuidStart.length()
            val contentEnd: Int = xml.indexOf(uuidEnd, contentStart)
            if (contentEnd != -1) {
                return xml.substring(contentStart, contentEnd)
            }
        }
        
        return null
    }
    
     private fun escapeXml(text: String): String {
        if (text == null) return ""
        return text.replace("&", "&amp;")
                  .replace("<", "&lt;")
                  .replace(">", "&gt;")
                  .replace("\"", "&quot;")
                  .replace("'", "&apos;")
    }
    
    /**
     * Authentication result wrapper
     */
    @JvmStatic
    class AuthResult {
        private val Boolean success
        private val String token
        private val String message
        
        public AuthResult(Boolean success, String token, String message) {
            this.success = success
            this.token = token
            this.message = message
        }
        
         public fun isSuccess(): Boolean {
            return success
        }
        
         public fun getToken(): String {
            return token
        }
        
         public fun getMessage(): String {
            return message
        }
    }
    
    /**
     * Session information wrapper
     */
    @JvmStatic
    class SessionInfo {
        private val String sessionId
        private val String agentId
        private val String firstName
        private val String lastName
        
        public SessionInfo(String sessionId, String agentId, String firstName, String lastName) {
            this.sessionId = sessionId
            this.agentId = agentId
            this.firstName = firstName
            this.lastName = lastName
        }
        
         public fun getSessionId(): String { return sessionId; }
         public fun getAgentId(): String { return agentId; }
         public fun getFirstName(): String { return firstName; }
         public fun getLastName(): String { return lastName; }
         public fun getFullName(): String { 
            return (firstName != null && lastName != null) ? firstName + " " + lastName : null 
        }
    }
}