package com.linkpoint.slproto.caps

import com.linkpoint.Debug
import com.linkpoint.slproto.llsd.LLSD
import com.linkpoint.slproto.llsd.LLSDXMLParser
import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.ConcurrentHashMap

/**
 * CAPSManager - Capabilities system for Second Life
 * 
 * CAPS (Capabilities) are HTTP-based services that provide:
 * - Asset uploads/downloads
 * - Event queues
 * - Inventory operations
 * - Object rezzing
 * - And much more
 * 
 * Based on Firestorm's LLSD HTTP and capabilities system
 */
class CAPSManager {
    
    companion object {
        const val TIMEOUT_MS = 30000
        const val MAX_RETRIES = 3
    }
    
    // Capability URLs
    private val capabilities = ConcurrentHashMap<String, String>()
    
    // Seed capability URL
    private var seedCapability: String? = null
    
    /**
     * Initialize with seed capability
     */
    suspend fun initializeFromSeed(seedURL: String) = withContext(Dispatchers.IO) {
        seedCapability = seedURL
        
        try {
            // Request all capabilities from seed
            val request = LLSD.emptyArray()
            
            // Add capability names we want
            val capNames = listOf(
                "EventQueueGet",
                "GetTexture",
                "GetMesh",
                "GetObjectCost",
                "GetObjectPhysicsData",
                "ObjectMedia",
                "ObjectMediaNavigate",
                "ObjectNavMeshProperties",
                "ProductInfoRequest",
                "ProvisionVoiceAccountRequest",
                "RemoteParcelRequest",
                "RequestTextureDownload",
                "SearchStatRequest",
                "SearchStatTracking",
                "SendPostcard",
                "SendUserReport",
                "SendUserReportWithScreenshot",
                "ServerReleaseNotes",
                "SetDisplayName",
                "SimConsole",
                "SimConsoleAsync",
                "SimulatorFeatures",
                "StartGroupProposal",
                "UpdateAgentInformation",
                "UpdateAgentLanguage",
                "UpdateGestureAgentInventory",
                "UpdateGestureTaskInventory",
                "UpdateNotecardAgentInventory",
                "UpdateNotecardTaskInventory",
                "UpdateScriptAgent",
                "UpdateScriptTask",
                "UploadBakedTexture",
                "ViewerAsset",
                "ViewerMetrics",
                "ViewerStartAuction",
                "ViewerStats"
            )
            
            for (name in capNames) {
                request.add(LLSD(name))
            }
            
            // Make request
            val response = postLLSD(seedURL, request)
            
            // Parse capabilities
            if (response.isMap()) {
                for (key in response.keys()) {
                    val value = response[key]
                    if (value.isString()) {
                        capabilities[key] = value.asString()
                        Debug.Log("Got capability: $key = ${value.asString()}")
                    }
                }
            }
            
            Debug.Log("Initialized ${capabilities.size} capabilities")
            
        } catch (e: Exception) {
            Debug.Log("Failed to initialize capabilities: ${e.message}")
        }
    }
    
    /**
     * Get capability URL by name
     */
    fun getCapability(name: String): String? {
        return capabilities[name]
    }
    
    /**
     * Check if capability exists
     */
    fun hasCapability(name: String): Boolean {
        return capabilities.containsKey(name)
    }
    
    /**
     * Set capability manually
     */
    fun setCapability(name: String, url: String) {
        capabilities[name] = url
    }
    
    /**
     * Clear all capabilities
     */
    fun clear() {
        capabilities.clear()
        seedCapability = null
    }
    
    /**
     * Make CAPS POST request with LLSD
     */
    suspend fun postLLSD(url: String, llsd: LLSD): LLSD = withContext(Dispatchers.IO) {
        var lastError: Exception? = null
        
        for (attempt in 1..MAX_RETRIES) {
            try {
                return@withContext postLLSDInternal(url, llsd)
            } catch (e: Exception) {
                lastError = e
                Debug.Log("CAPS request failed (attempt $attempt): ${e.message}")
                if (attempt < MAX_RETRIES) {
                    delay(1000 * attempt.toLong())
                }
            }
        }
        
        throw lastError ?: Exception("CAPS request failed")
    }
    
    /**
     * Internal CAPS POST request
     */
    private suspend fun postLLSDInternal(url: String, llsd: LLSD): LLSD = withContext(Dispatchers.IO) {
        val connection = URL(url).openConnection() as HttpURLConnection
        connection.requestMethod = "POST"
        connection.setRequestProperty("Content-Type", "application/llsd+xml")
        connection.setRequestProperty("Accept", "application/llsd+xml")
        connection.connectTimeout = TIMEOUT_MS
        connection.readTimeout = TIMEOUT_MS
        connection.doOutput = true
        
        try {
            // Send LLSD request
            OutputStreamWriter(connection.outputStream).use { writer ->
                writer.write(llsd.toXML())
                writer.flush()
            }
            
            // Read LLSD response
            val responseCode = connection.responseCode
            if (responseCode != 200) {
                throw Exception("HTTP error: $responseCode")
            }
            
            val xmlResponse = BufferedReader(InputStreamReader(connection.inputStream)).use { reader ->
                reader.readText()
            }
            
            // Parse LLSD response
            LLSDXMLParser.parse(xmlResponse)
            
        } finally {
            connection.disconnect()
        }
    }
    
    /**
     * Make CAPS GET request
     */
    suspend fun getLLSD(url: String): LLSD = withContext(Dispatchers.IO) {
        val connection = URL(url).openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.setRequestProperty("Accept", "application/llsd+xml")
        connection.connectTimeout = TIMEOUT_MS
        connection.readTimeout = TIMEOUT_MS
        
        try {
            val responseCode = connection.responseCode
            if (responseCode != 200) {
                throw Exception("HTTP error: $responseCode")
            }
            
            val xmlResponse = BufferedReader(InputStreamReader(connection.inputStream)).use { reader ->
                reader.readText()
            }
            
            LLSDXMLParser.parse(xmlResponse)
            
        } finally {
            connection.disconnect()
        }
    }
    
    /**
     * Get capability names
     */
    fun getCapabilityNames(): Set<String> {
        return capabilities.keys.toSet()
    }
    
    /**
     * Get capability count
     */
    fun getCapabilityCount(): Int {
        return capabilities.size
    }
}
