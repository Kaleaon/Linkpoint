package com.linkpoint.slproto.auth

import com.linkpoint.slproto.llsd.LLSD
import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.util.UUID

/**
 * SLAuthSystem - Authentication for Second Life grid
 * 
 * Handles:
 * - XML-RPC login to grid
 * - Circuit establishment
 * - Session management
 * - Region handoff
 * 
 * Based on Firestorm's login system
 */
class SLAuthSystem(
    val gridURL: String = "https://login.agni.lindenlab.com/cgi-bin/login.cgi"
) {
    
    companion object {
        const val PROTOCOL_VERSION = "i001"
        const val VIEWER_NAME = "Linkpoint"
        const val VIEWER_VERSION = "1.0.0"
        const val CHANNEL = "Linkpoint Release"
        
        // Login parameters
        const val START_LOCATION = "last"  // or "home" or "regionName/x/y/z"
    }
    
    /**
     * Login to Second Life
     */
    suspend fun login(
        firstName: String,
        lastName: String,
        password: String
    ): LoginResponse = withContext(Dispatchers.IO) {
        
        try {
            // Build login parameters
            val params = buildLoginParams(firstName, lastName, password)
            
            // Make XML-RPC call
            val xmlRequest = buildXMLRPCRequest("login_to_simulator", params)
            val xmlResponse = makeHTTPRequest(gridURL, xmlRequest)
            
            // Parse response
            parseLoginResponse(xmlResponse)
            
        } catch (e: Exception) {
            LoginResponse(
                success = false,
                message = "Login failed: ${e.message}",
                reason = "connection_failed"
            )
        }
    }
    
    /**
     * Build login parameters
     */
    private fun buildLoginParams(
        firstName: String,
        lastName: String,
        password: String
    ): Map<String, Any> {
        // Hash password with MD5 prefix (SL uses "$1$" + md5hash)
        val passwordHash = hashPassword(password)
        
        return mapOf(
            "first" to firstName,
            "last" to lastName,
            "passwd" to passwordHash,
            "start" to START_LOCATION,
            "version" to VIEWER_VERSION,
            "channel" to CHANNEL,
            "platform" to "Android",
            "mac" to generateMAC(),
            "id0" to generateID0(),
            "agree_to_tos" to "true",
            "read_critical" to "true",
            "viewer_digest" to "",
            "options" to listOf(
                "inventory-root",
                "inventory-skeleton",
                "inventory-lib-root",
                "inventory-lib-owner",
                "inventory-skel-lib",
                "gestures",
                "event_categories",
                "event_notifications",
                "classified_categories",
                "adult_compliant",
                "buddy-list",
                "ui-config",
                "login-flags",
                "global-textures"
            )
        )
    }
    
    /**
     * Hash password for SL login (MD5 with $1$ prefix)
     */
    private fun hashPassword(password: String): String {
        val md = java.security.MessageDigest.getInstance("MD5")
        val digest = md.digest(password.toByteArray())
        val hexString = digest.joinToString("") { "%02x".format(it) }
        return "\$1\$$hexString"
    }
    
    /**
     * Generate MAC address (can be random for mobile)
     */
    private fun generateMAC(): String {
        val random = UUID.randomUUID().toString().replace("-", "").take(12)
        return random.chunked(2).joinToString(":")
    }
    
    /**
     * Generate ID0 (hardware identifier)
     */
    private fun generateID0(): String {
        return UUID.randomUUID().toString()
    }
    
    /**
     * Build XML-RPC request
     */
    private fun buildXMLRPCRequest(method: String, params: Map<String, Any>): String {
        val xml = StringBuilder()
        xml.append("<?xml version=\"1.0\"?>\n")
        xml.append("<methodCall>\n")
        xml.append("  <methodName>$method</methodName>\n")
        xml.append("  <params>\n")
        xml.append("    <param>\n")
        xml.append("      <value><struct>\n")
        
        for ((key, value) in params) {
            xml.append("        <member>\n")
            xml.append("          <name>$key</name>\n")
            xml.append("          ")
            xml.append(encodeXMLRPCValue(value))
            xml.append("\n")
            xml.append("        </member>\n")
        }
        
        xml.append("      </struct></value>\n")
        xml.append("    </param>\n")
        xml.append("  </params>\n")
        xml.append("</methodCall>\n")
        
        return xml.toString()
    }
    
    /**
     * Encode value for XML-RPC
     */
    private fun encodeXMLRPCValue(value: Any): String {
        return when (value) {
            is String -> "<value><string>$value</string></value>"
            is Int -> "<value><int>$value</int></value>"
            is Boolean -> "<value><boolean>${if (value) "1" else "0"}</boolean></value>"
            is List<*> -> {
                val items = value.joinToString("\n") { encodeXMLRPCValue(it!!) }
                "<value><array><data>\n$items\n</data></array></value>"
            }
            else -> "<value><string>$value</string></value>"
        }
    }
    
    /**
     * Make HTTP POST request
     */
    private fun makeHTTPRequest(url: String, xmlRequest: String): String {
        val connection = URL(url).openConnection() as HttpURLConnection
        connection.requestMethod = "POST"
        connection.setRequestProperty("Content-Type", "text/xml")
        connection.doOutput = true
        
        // Send request
        OutputStreamWriter(connection.outputStream).use { writer ->
            writer.write(xmlRequest)
            writer.flush()
        }
        
        // Read response
        val response = StringBuilder()
        BufferedReader(InputStreamReader(connection.inputStream)).use { reader ->
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                response.append(line).append("\n")
            }
        }
        
        return response.toString()
    }
    
    /**
     * Parse login response
     */
    private fun parseLoginResponse(xmlResponse: String): LoginResponse {
        // Simple XML parsing (should use proper XML parser in production)
        
        // Check for fault
        if (xmlResponse.contains("<fault>")) {
            val reason = extractXMLValue(xmlResponse, "reason") ?: "unknown"
            val message = extractXMLValue(xmlResponse, "message") ?: "Login failed"
            return LoginResponse(
                success = false,
                message = message,
                reason = reason
            )
        }
        
        // Extract success response
        val agentID = extractXMLValue(xmlResponse, "agent_id") ?: ""
        val sessionID = extractXMLValue(xmlResponse, "session_id") ?: ""
        val simIP = extractXMLValue(xmlResponse, "sim_ip") ?: ""
        val simPort = extractXMLValue(xmlResponse, "sim_port")?.toIntOrNull() ?: 0
        val circuitCode = extractXMLValue(xmlResponse, "circuit_code")?.toIntOrNull() ?: 0
        val seedCapability = extractXMLValue(xmlResponse, "seed_capability") ?: ""
        val firstName = extractXMLValue(xmlResponse, "first_name") ?: ""
        val lastName = extractXMLValue(xmlResponse, "last_name") ?: ""
        val startLocation = extractXMLValue(xmlResponse, "start_location") ?: ""
        
        return LoginResponse(
            success = true,
            message = "Login successful",
            agentID = UUID.fromString(agentID),
            sessionID = UUID.fromString(sessionID),
            simIP = simIP,
            simPort = simPort,
            circuitCode = circuitCode,
            seedCapability = seedCapability,
            firstName = firstName,
            lastName = lastName,
            startLocation = startLocation
        )
    }
    
    /**
     * Extract value from XML (simple parser)
     */
    private fun extractXMLValue(xml: String, tagName: String): String? {
        val startTag = "<name>$tagName</name>"
        val startIndex = xml.indexOf(startTag)
        if (startIndex == -1) return null
        
        // Find the value after the name tag
        val valueStart = xml.indexOf("<value>", startIndex)
        if (valueStart == -1) return null
        
        val valueEnd = xml.indexOf("</value>", valueStart)
        if (valueEnd == -1) return null
        
        val valueContent = xml.substring(valueStart + 7, valueEnd)
        
        // Extract from inner tags (string, int, etc.)
        val innerStart = valueContent.indexOf('>')
        val innerEnd = valueContent.lastIndexOf('<')
        if (innerStart == -1 || innerEnd == -1) return valueContent.trim()
        
        return valueContent.substring(innerStart + 1, innerEnd).trim()
    }
}

/**
 * Login response data class
 */
data class LoginResponse(
    val success: Boolean,
    val message: String,
    val reason: String = "",
    val agentID: UUID = UUID(0, 0),
    val sessionID: UUID = UUID(0, 0),
    val simIP: String = "",
    val simPort: Int = 0,
    val circuitCode: Int = 0,
    val seedCapability: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val startLocation: String = ""
)
