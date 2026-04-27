package com.lumiyaviewer.lumiya.voice

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.*
import okhttp3.*
import java.io.IOException

/**
 * Bridge between Second Life voice system and WebRTC implementation
 * Handles SL-specific voice server communication and authentication
 * Modern Kotlin implementation with coroutines
 */
class SecondLifeWebRTCBridge(private val context: Context) {
    
    companion object {
        private const val TAG = "SLWebRTCBridge"
        
        // Second Life voice server endpoints
        private const val SL_VOICE_PROVISION_URL = "https://cap.secondlife.com/cap/"
        private const val SL_VOICE_ACCOUNT_URL = "https://bhr.vivox.com/api2/viv_signin.php"
    }
    
    private val gson = Gson()
    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(LoggingInterceptor())
        .build()
    private val voiceAdapter = WebRTCVoiceAdapter.getInstance(context)
    
    // Voice session state
    private var voiceAccountName: String? = null
    private var voicePassword: String? = null
    private var voiceServerUri: String? = null
    private var currentChannelUri: String? = null
    
    /**
     * Process Second Life voice credentials and establish voice connection
     * This is called when SL sends voice credentials to the client
     */
    suspend fun processSecondLifeVoiceCredentials(
        slSessionId: String,
        slVoiceUser: String,
        slVoicePassword: String,
        slVoiceServer: String,
        channelUri: String
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            Log.i(TAG, "Processing Second Life voice credentials...")
            Log.i(TAG, "SL Session ID: $slSessionId")
            Log.i(TAG, "Voice User: $slVoiceUser")
            Log.i(TAG, "Voice Server: $slVoiceServer")
            Log.i(TAG, "Channel URI: $channelUri")
            
            // Step 1: Store credentials
            voiceAccountName = slVoiceUser
            voicePassword = slVoicePassword
            voiceServerUri = slVoiceServer
            currentChannelUri = channelUri
            
            // Step 2: Initialize WebRTC voice adapter if not already done
            if (!voiceAdapter.isVoiceInitialized()) {
                Log.i(TAG, "Initializing WebRTC voice adapter...")
                val initSuccess = voiceAdapter.initialize()
                if (!initSuccess) {
                    Log.e(TAG, "Failed to initialize WebRTC voice adapter")
                    return@withContext false
                }
            }
            
            // Step 3: Authenticate with voice server
            Log.i(TAG, "Authenticating with voice server...")
            val authSuccess = authenticateWithVoiceServer()
            if (!authSuccess) {
                Log.e(TAG, "Voice server authentication failed")
                return@withContext false
            }
            
            // Step 4: Connect to voice channel
            Log.i(TAG, "Connecting to voice channel...")
            val connectSuccess = connectToVoiceChannel()
            if (!connectSuccess) {
                Log.e(TAG, "Voice channel connection failed")
                return@withContext false
            }
            
            Log.i(TAG, "Second Life voice connection established successfully")
            true
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to process SL voice credentials", e)
            false
        }
    }
    
    /**
     * Authenticate with Second Life voice server
     */
    private suspend fun authenticateWithVoiceServer(): Boolean = withContext(Dispatchers.IO) {
        try {
            val serverUri = voiceServerUri ?: return@withContext false
            Log.i(TAG, "Authenticating with SL voice server: $serverUri")
            
            // Create authentication request
            val authRequest = JsonObject().apply {
                addProperty("user", voiceAccountName)
                addProperty("password", voicePassword)
                addProperty("method", "get_account_info")
            }
            
            val body = RequestBody.create(
                MediaType.parse("application/json"),
                gson.toJson(authRequest)
            )
            
            val request = Request.Builder()
                .url("$serverUri/viv_signin.php")
                .post(body)
                .addHeader("Content-Type", "application/json")
                .addHeader("User-Agent", "Linkpoint/3.4.3 (WebRTC)")
                .build()
            
            httpClient.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val responseBody = response.body()?.string() ?: return@withContext false
                    Log.i(TAG, "Voice server authentication successful")
                    Log.d(TAG, "Auth response: $responseBody")
                    
                    // Process authentication response
                    processAuthResponse(responseBody)
                } else {
                    Log.e(TAG, "Voice server authentication failed with code: ${response.code()}")
                    false
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Voice server authentication error", e)
            false
        }
    }
    
    /**
     * Process authentication response from voice server
     */
    private fun processAuthResponse(responseBody: String): Boolean {
        return try {
            val response = gson.fromJson(responseBody, JsonObject::class.java)
            
            if (response.has("status") && response.get("status").asString == "success") {
                Log.i(TAG, "Voice authentication successful")
                
                // Extract additional voice server information if available
                if (response.has("server_uri")) {
                    val serverUri = response.get("server_uri").asString
                    Log.i(TAG, "Voice server URI: $serverUri")
                }
                
                true
            } else {
                Log.e(TAG, "Voice authentication failed: $responseBody")
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to process auth response", e)
            false
        }
    }
    
    /**
     * Connect to the voice channel using WebRTC
     */
    private suspend fun connectToVoiceChannel(): Boolean {
        return try {
            val channelUri = currentChannelUri ?: return false
            Log.i(TAG, "Connecting to voice channel: $channelUri")
            
            // Use WebRTC adapter to connect to channel
            voiceAdapter.sessionConnect(channelUri, voicePassword)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to connect to voice channel", e)
            false
        }
    }
    
    /**
     * Disconnect from current voice channel
     */
    suspend fun disconnectFromVoice(): Boolean {
        return try {
            Log.i(TAG, "Disconnecting from voice...")
            
            currentChannelUri?.let { uri ->
                val success = voiceAdapter.sessionTerminate(uri)
                if (success) {
                    Log.i(TAG, "Successfully disconnected from voice channel")
                }
                currentChannelUri = null
                return success
            } ?: true // Already disconnected
        } catch (e: Exception) {
            Log.e(TAG, "Failed to disconnect from voice", e)
            false
        }
    }
    
    /**
     * Handle voice server URLs from Second Life CAPS system
     */
    suspend fun processVoiceServerCaps(capsUrl: String, sessionId: String): Boolean = withContext(Dispatchers.IO) {
        try {
            Log.i(TAG, "Processing voice server CAPS: $capsUrl")
            
            // Create CAPS request for voice provisioning
            val capsRequest = JsonObject().apply {
                addProperty("session_id", sessionId)
                addProperty("method", "provision_voice")
            }
            
            val body = RequestBody.create(
                MediaType.parse("application/json"),
                gson.toJson(capsRequest)
            )
            
            val request = Request.Builder()
                .url(capsUrl)
                .post(body)
                .addHeader("Content-Type", "application/json")
                .addHeader("User-Agent", "Linkpoint/3.4.3 (WebRTC)")
                .build()
            
            httpClient.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val responseBody = response.body()?.string() ?: return@withContext false
                    Log.i(TAG, "Voice CAPS response received")
                    Log.d(TAG, "CAPS response: $responseBody")
                    
                    processVoiceCapsResponse(responseBody)
                } else {
                    Log.e(TAG, "Voice CAPS request failed with code: ${response.code()}")
                    false
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Voice CAPS processing error", e)
            false
        }
    }
    
    /**
     * Process voice CAPS response
     */
    private fun processVoiceCapsResponse(responseBody: String): Boolean {
        return try {
            val response = gson.fromJson(responseBody, JsonObject::class.java)
            
            if (response.has("voice_server_uri")) {
                val serverUri = response.get("voice_server_uri").asString
                Log.i(TAG, "Voice server URI from CAPS: $serverUri")
                voiceServerUri = serverUri
            }
            
            if (response.has("voice_channel_uri")) {
                val channelUri = response.get("voice_channel_uri").asString
                Log.i(TAG, "Voice channel URI from CAPS: $channelUri")
                currentChannelUri = channelUri
            }
            
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to process voice CAPS response", e)
            false
        }
    }
    
    /**
     * Get current voice connection status
     */
    fun isConnectedToVoice(): Boolean = voiceAdapter.isConnectedToChannel()
    
    /**
     * Get current voice channel URI
     */
    fun getCurrentChannelUri(): String? = currentChannelUri
    
    /**
     * Cleanup voice bridge resources
     */
    fun cleanup() {
        Log.i(TAG, "Cleaning up Second Life WebRTC bridge...")
        
        // Disconnect from voice if connected
        if (isConnectedToVoice()) {
            disconnectFromVoice()
        }
        
        // Clear state
        voiceAccountName = null
        voicePassword = null
        voiceServerUri = null
        currentChannelUri = null
        
        Log.i(TAG, "Second Life WebRTC bridge cleanup completed")
    }
    
    /**
     * HTTP logging interceptor for debugging
     */
    private class LoggingInterceptor : Interceptor {
        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request()
            Log.d(TAG, "HTTP Request: ${request.method()} ${request.url()}")
            
            val response = chain.proceed(request)
            Log.d(TAG, "HTTP Response: ${response.code()} ${response.message()}")
            
            return response
        }
    }
}