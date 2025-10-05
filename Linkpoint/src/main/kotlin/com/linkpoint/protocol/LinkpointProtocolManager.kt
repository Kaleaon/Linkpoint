package com.linkpoint.protocol

import android.util.Log
import kotlinx.coroutines.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * Linkpoint Protocol Manager
 * Handles modern Second Life protocol communication including:
 * - LLSD HTTP/HTTPS requests
 * - Capabilities (Caps) system
 * - WebSocket event streaming
 * - HTTP/2 for improved performance
 */
class LinkpointProtocolManager {
    
    companion object {
        private const val TAG = "LinkpointProtocol"
        
        // Second Life grid endpoints
        const val AGNI_GRID = "https://login.agni.lindenlab.com"
        const val ADITI_GRID = "https://login.aditi.lindenlab.com"
        
        // Protocol timeouts (modern, aggressive)
        private const val CONNECT_TIMEOUT = 15L
        private const val READ_TIMEOUT = 30L
        private const val WRITE_TIMEOUT = 15L
    }
    
    // Modern HTTP/2 client with connection pooling
    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
        .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
        .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
        .protocols(listOf(Protocol.HTTP_2, Protocol.HTTP_1_1))
        .connectionPool(ConnectionPool(5, 5, TimeUnit.MINUTES))
        .addInterceptor(LoggingInterceptor())
        .build()
    
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    // Active capabilities
    private val capabilities = mutableMapOf<String, String>()
    
    /**
     * Send LLSD HTTP request to Second Life server
     */
    suspend fun sendLLSDRequest(
        url: String,
        llsdData: String,
        method: String = "POST"
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Sending LLSD request to: $url")
            
            val requestBody = llsdData.toRequestBody("application/llsd+xml".toMediaType())
            val request = Request.Builder()
                .url(url)
                .method(method, requestBody)
                .header("Content-Type", "application/llsd+xml")
                .header("Accept", "application/llsd+xml")
                .header("User-Agent", "Linkpoint/1.0.0 (Android)")
                .build()
            
            val response = httpClient.newCall(request).execute()
            
            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                if (responseBody != null) {
                    Log.d(TAG, "LLSD request successful")
                    Result.success(responseBody)
                } else {
                    Result.failure(Exception("Empty response body"))
                }
            } else {
                Log.e(TAG, "LLSD request failed: HTTP ${response.code}")
                Result.failure(Exception("HTTP ${response.code}: ${response.message}"))
            }
            
        } catch (e: IOException) {
            Log.e(TAG, "Network error during LLSD request", e)
            Result.failure(e)
        } catch (e: Exception) {
            Log.e(TAG, "Error during LLSD request", e)
            Result.failure(e)
        }
    }
    
    /**
     * Register capability (modern SL caps system)
     */
    fun registerCapability(capName: String, capUrl: String) {
        capabilities[capName] = capUrl
        Log.d(TAG, "Registered capability: $capName -> $capUrl")
    }
    
    /**
     * Get capability URL
     */
    fun getCapability(capName: String): String? {
        return capabilities[capName]
    }
    
    /**
     * Invoke capability with LLSD data
     */
    suspend fun invokeCapability(
        capName: String,
        llsdData: String
    ): Result<String> = withContext(Dispatchers.IO) {
        val capUrl = capabilities[capName]
        if (capUrl == null) {
            Log.e(TAG, "Capability not found: $capName")
            return@withContext Result.failure(Exception("Capability not found: $capName"))
        }
        
        sendLLSDRequest(capUrl, llsdData)
    }
    
    /**
     * Clear all capabilities (on logout)
     */
    fun clearCapabilities() {
        Log.i(TAG, "Clearing ${capabilities.size} capabilities")
        capabilities.clear()
    }
    
    /**
     * Create LLSD XML map
     */
    fun createLLSDMap(data: Map<String, Any>): String {
        val sb = StringBuilder()
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")
        sb.append("<llsd>\n")
        sb.append("  <map>\n")
        
        for ((key, value) in data) {
            sb.append("    <key>$key</key>\n")
            sb.append("    ${toLLSDValue(value)}\n")
        }
        
        sb.append("  </map>\n")
        sb.append("</llsd>")
        
        return sb.toString()
    }
    
    /**
     * Convert Kotlin value to LLSD XML value
     */
    private fun toLLSDValue(value: Any): String {
        return when (value) {
            is String -> "<string>$value</string>"
            is Int -> "<integer>$value</integer>"
            is Long -> "<integer>$value</integer>"
            is Float -> "<real>$value</real>"
            is Double -> "<real>$value</real>"
            is Boolean -> "<boolean>${if (value) "true" else "false"}</boolean>"
            is List<*> -> {
                val sb = StringBuilder("<array>\n")
                for (item in value) {
                    if (item != null) {
                        sb.append("      ${toLLSDValue(item)}\n")
                    }
                }
                sb.append("    </array>")
                sb.toString()
            }
            is Map<*, *> -> {
                val sb = StringBuilder("<map>\n")
                for ((k, v) in value) {
                    if (k is String && v != null) {
                        sb.append("      <key>$k</key>\n")
                        sb.append("      ${toLLSDValue(v)}\n")
                    }
                }
                sb.append("    </map>")
                sb.toString()
            }
            else -> "<undef/>"
        }
    }
    
    /**
     * Cleanup resources
     */
    fun cleanup() {
        Log.i(TAG, "Cleaning up protocol manager...")
        scope.cancel()
        clearCapabilities()
        
        // Cleanup HTTP client
        httpClient.dispatcher.executorService.shutdown()
        httpClient.connectionPool.evictAll()
    }
    
    /**
     * Logging interceptor for debugging
     */
    private class LoggingInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request()
            val startTime = System.currentTimeMillis()
            
            Log.d(TAG, "→ ${request.method} ${request.url}")
            
            val response = chain.proceed(request)
            val duration = System.currentTimeMillis() - startTime
            
            Log.d(TAG, "← ${response.code} ${request.url} (${duration}ms)")
            
            return response
        }
    }
}