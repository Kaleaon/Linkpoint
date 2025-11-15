package com.linkpoint.slproto.auth

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.w3c.dom.Document
import org.w3c.dom.Element
import java.io.ByteArrayInputStream
import java.io.IOException
import java.security.MessageDigest
import java.util.concurrent.TimeUnit
import javax.xml.parsers.DocumentBuilderFactory

/**
 * Complete Second Life Authentication Implementation
 * 
 * Implements XMLRPC login protocol for Second Life
 * Based on Firestorm viewer and Libremetaverse implementations
 */
class SLAuthImpl {
    
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()
    
    companion object {
        private const val DEFAULT_LOGIN_URI = "https://login.agni.lindenlab.com/cgi-bin/login.cgi"
        private const val CONTENT_TYPE_XML = "text/xml"
        
        /**
         * Generate MD5 hash for password
         */
        fun md5Hash(input: String): String {
            val md = MessageDigest.getInstance("MD5")
            val digest = md.digest(input.toByteArray())
            return digest.joinToString("") { "%02x".format(it) }
        }
    }
    
    /**
     * Send login request to Second Life
     */
    suspend fun sendLoginRequest(params: SLAuthParams): SLAuthReply = withContext(Dispatchers.IO) {
        try {
            // Parse login name
            val (firstName, lastName) = parseLoginName(params.loginName)
            
            // Build XMLRPC request
            val xmlRequest = buildLoginRequest(
                firstName = firstName,
                lastName = lastName,
                passwordHash = params.passwordHash,
                startLocation = params.startLocation ?: "last",
                channel = params.channel,
                version = params.version,
                macAddress = params.macAddress,
                id0 = params.id0,
                skipOptionalUpdate = params.skipOptionalUpdate
            )
            
            // Send request
            val loginUri = params.loginUri ?: DEFAULT_LOGIN_URI
            val response = sendHttpPost(loginUri, xmlRequest)
            
            // Parse response
            parseLoginResponse(response)
            
        } catch (e: Exception) {
            SLAuthReply(
                success = false,
                message = "Login failed: ${e.message}",
                reason = e.javaClass.simpleName
            )
        }
    }
    
    /**
     * Parse login name into first and last name
     */
    private fun parseLoginName(loginName: String): Pair<String, String> {
        val trimmed = loginName.trim()
        val separators = charArrayOf(' ', '.', '_')
        
        var splitIndex = trimmed.length
        for (separator in separators) {
            val index = trimmed.indexOf(separator)
            if (index != -1 && index < splitIndex) {
                splitIndex = index
            }
        }
        
        val firstName = trimmed.substring(0, splitIndex).trim()
        val lastName = if (splitIndex < trimmed.length) {
            trimmed.substring(splitIndex + 1).trim()
        } else {
            ""
        }
        
        val finalLastName = if (lastName.isEmpty()) "Resident" else lastName
        
        return Pair(firstName, finalLastName)
    }
    
    /**
     * Build XMLRPC login request
     */
    private fun buildLoginRequest(
        firstName: String,
        lastName: String,
        passwordHash: String,
        startLocation: String,
        channel: String,
        version: String,
        macAddress: String,
        id0: String,
        skipOptionalUpdate: Boolean
    ): String {
        return buildString {
            append("<?xml version=&quot;1.0&quot;?>\n")
            append("<methodCall>\n")
            append("<methodName>login_to_simulator</methodName>\n")
            append("<params>\n")
            append("<param>\n")
            append("<value><struct>\n")
            
            // Add all login fields
            addMember("first", firstName)
            addMember("last", lastName)
            addMember("passwd", "\$1\$$passwordHash")
            addMember("start", startLocation)
            addMember("channel", channel)
            addMember("version", version)
            addMember("platform", "Android")
            addMember("mac", macAddress)
            addMember("id0", id0)
            addMember("agree_to_tos", "true")
            addMember("read_critical", "true")
            
            if (skipOptionalUpdate) {
                addMember("skipoptional", "true")
            }
            
            append("</struct></value>\n")
            append("</param>\n")
            append("</params>\n")
            append("</methodCall>\n")
        }
    }
    
    private fun StringBuilder.addMember(name: String, value: String) {
        append("<member>\n")
        append("<name>$name</name>\n")
        append("<value><string>${escapeXml(value)}</string></value>\n")
        append("</member>\n")
    }
    
    private fun escapeXml(text: String): String {
        return text
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("&quot;", "&quot;")
            .replace("'", "&apos;")
    }
    
    /**
     * Send HTTP POST request
     */
    private fun sendHttpPost(url: String, body: String): String {
        val mediaType = CONTENT_TYPE_XML.toMediaType()
        val requestBody = body.toRequestBody(mediaType)
        
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .addHeader("Content-Type", CONTENT_TYPE_XML)
            .build()
        
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw IOException("HTTP error: ${response.code}")
            }
            return response.body?.string() ?: throw IOException("Empty response body")
        }
    }
    
    /**
     * Parse XMLRPC login response
     */
    private fun parseLoginResponse(xmlResponse: String): SLAuthReply {
        val factory = DocumentBuilderFactory.newInstance()
        val builder = factory.newDocumentBuilder()
        val doc: Document = builder.parse(ByteArrayInputStream(xmlResponse.toByteArray()))
        
        doc.documentElement.normalize()
        
        // Check for fault (error response)
        val faultNodes = doc.getElementsByTagName("fault")
        if (faultNodes.length > 0) {
            val faultString = extractValue(doc, "faultString") ?: "Unknown error"
            return SLAuthReply(
                success = false,
                message = faultString,
                reason = "fault"
            )
        }
        
        // Parse successful response
        val login = extractValue(doc, "login") ?: "false"
        val success = login == "true"
        
        if (!success) {
            val reason = extractValue(doc, "reason") ?: "Unknown reason"
            val message = extractValue(doc, "message") ?: reason
            return SLAuthReply(
                success = false,
                message = message,
                reason = reason
            )
        }
        
        // Extract all login response fields
        return SLAuthReply(
            success = true,
            message = extractValue(doc, "message") ?: "Login successful",
            agentId = extractValue(doc, "agent_id"),
            sessionId = extractValue(doc, "session_id"),
            secureSessionId = extractValue(doc, "secure_session_id"),
            firstName = extractValue(doc, "first_name"),
            lastName = extractValue(doc, "last_name"),
            startLocation = extractValue(doc, "start_location"),
            simIp = extractValue(doc, "sim_ip"),
            simPort = extractValue(doc, "sim_port")?.toIntOrNull(),
            regionX = extractValue(doc, "region_x")?.toIntOrNull(),
            regionY = extractValue(doc, "region_y")?.toIntOrNull(),
            lookAt = extractValue(doc, "look_at"),
            circuitCode = extractValue(doc, "circuit_code")?.toIntOrNull(),
            seedCapability = extractValue(doc, "seed_capability"),
            agentAccess = extractValue(doc, "agent_access"),
            inventoryRoot = extractArrayValue(doc, "inventory-root"),
            inventorySkeleton = extractArrayValue(doc, "inventory-skeleton"),
            inventoryLibRoot = extractArrayValue(doc, "inventory-lib-root"),
            inventoryLibOwner = extractArrayValue(doc, "inventory-lib-owner"),
            inventorySkel = extractArrayValue(doc, "inventory-skel-lib"),
            buddyList = extractArrayValue(doc, "buddy-list"),
            uiConfig = extractArrayValue(doc, "ui-config"),
            loginFlags = extractArrayValue(doc, "login-flags"),
            globalTextures = extractArrayValue(doc, "global-textures"),
            eventCategories = extractArrayValue(doc, "event_categories"),
            eventNotifications = extractArrayValue(doc, "event_notifications"),
            classifiedCategories = extractArrayValue(doc, "classified_categories"),
            home = extractValue(doc, "home"),
            maxAgentGroups = extractValue(doc, "max-agent-groups")?.toIntOrNull(),
            secondsSinceEpoch = extractValue(doc, "seconds_since_epoch")?.toLongOrNull()
        )
    }
    
    /**
     * Extract string value from XML document
     */
    private fun extractValue(doc: Document, tagName: String): String? {
        val nodes = doc.getElementsByTagName("name")
        for (i in 0 until nodes.length) {
            val node = nodes.item(i)
            if (node.textContent == tagName) {
                val parent = node.parentNode as? Element ?: continue
                val valueNode = parent.getElementsByTagName("value").item(0) as? Element ?: continue
                val stringNode = valueNode.getElementsByTagName("string").item(0)
                if (stringNode != null) {
                    return stringNode.textContent
                }
                // Try direct text content
                return valueNode.textContent?.trim()
            }
        }
        return null
    }
    
    /**
     * Extract array value from XML document
     */
    private fun extractArrayValue(doc: Document, tagName: String): List<Map<String, String>>? {
        val nodes = doc.getElementsByTagName("name")
        for (i in 0 until nodes.length) {
            val node = nodes.item(i)
            if (node.textContent == tagName) {
                val parent = node.parentNode as? Element ?: continue
                val valueNode = parent.getElementsByTagName("value").item(0) as? Element ?: continue
                val arrayNode = valueNode.getElementsByTagName("array").item(0) as? Element ?: continue
                
                // Parse array of structs
                val result = mutableListOf<Map<String, String>>()
                val dataNode = arrayNode.getElementsByTagName("data").item(0) as? Element ?: continue
                val values = dataNode.getElementsByTagName("value")
                
                for (j in 0 until values.length) {
                    val value = values.item(j) as? Element ?: continue
                    val struct = value.getElementsByTagName("struct").item(0) as? Element ?: continue
                    val members = struct.getElementsByTagName("member")
                    
                    val map = mutableMapOf<String, String>()
                    for (k in 0 until members.length) {
                        val member = members.item(k) as? Element ?: continue
                        val name = member.getElementsByTagName("name").item(0)?.textContent ?: continue
                        val memberValue = member.getElementsByTagName("value").item(0)?.textContent ?: continue
                        map[name] = memberValue
                    }
                    
                    if (map.isNotEmpty()) {
                        result.add(map)
                    }
                }
                
                return result
            }
        }
        return null
    }
}