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
 * Full XML-RPC login implementation for Second Life / OpenSim grids.
 * Ported from the legacy Lumiya viewer so the modern Linkpoint build can perform
 * real logins against live grids.
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

        fun md5Hash(input: String): String {
            val md = MessageDigest.getInstance("MD5")
            val digest = md.digest(input.toByteArray())
            return digest.joinToString("") { "%02x".format(it) }
        }
    }

    suspend fun sendLoginRequest(params: SLAuthParams): SLAuthReply = withContext(Dispatchers.IO) {
        try {
            val (firstName, lastName) = parseLoginName(params.loginName)
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

            val loginUri = params.loginUri.ifEmpty { DEFAULT_LOGIN_URI }
            val response = sendHttpPost(loginUri, xmlRequest)
            parseLoginResponse(response)
        } catch (e: Exception) {
            SLAuthReply(
                success = false,
                message = "Login failed: ${e.message}",
                reason = e.javaClass.simpleName
            )
        }
    }

    private fun parseLoginName(loginName: String): Pair<String, String> {
        val trimmed = loginName.trim()
        if (trimmed.isEmpty()) return "Resident" to "Resident"

        val separators = charArrayOf(' ', '.', '_')
        var splitIndex = trimmed.length
        separators.forEach { separator ->
            val index = trimmed.indexOf(separator)
            if (index in 1 until splitIndex) {
                splitIndex = index
            }
        }

        val firstName = trimmed.substring(0, splitIndex).trim()
        val lastName = if (splitIndex < trimmed.length) {
            trimmed.substring(splitIndex + 1).trim()
        } else {
            "Resident"
        }
        return firstName to if (lastName.isEmpty()) "Resident" else lastName
    }

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
    ): String = buildString {
        append("<?xml version=&quot;1.0&quot;?>\n")
        append("<methodCall>\n")
        append("<methodName>login_to_simulator</methodName>\n")
        append("<params>\n")
        append("<param>\n")
        append("<value><struct>\n")

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
            .replace("\"", "&quot;")
            .replace("'", "&apos;")
    }

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

    private fun parseLoginResponse(xmlResponse: String): SLAuthReply {
        val factory = DocumentBuilderFactory.newInstance()
        val builder = factory.newDocumentBuilder()
        val doc: Document = builder.parse(ByteArrayInputStream(xmlResponse.toByteArray()))
        doc.documentElement.normalize()

        val faultNodes = doc.getElementsByTagName("fault")
        if (faultNodes.length > 0) {
            val faultString = extractValue(doc, "faultString") ?: "Unknown error"
            return SLAuthReply(success = false, message = faultString, reason = "fault")
        }

        val login = extractValue(doc, "login") ?: "false"
        if (login != "true") {
            val reason = extractValue(doc, "reason") ?: "Unknown reason"
            val message = extractValue(doc, "message") ?: reason
            return SLAuthReply(success = false, message = message, reason = reason)
        }

        return SLAuthReply(
            success = true,
            message = extractValue(doc, "message") ?: "Login successful",
            agentId = extractValue(doc, "agent_id"),
            sessionId = extractValue(doc, "session_id"),
            secureSessionId = extractValue(doc, "secure_session_id"),
            firstName = extractValue(doc, "first_name"),
            lastName = extractValue(doc, "last_name"),
            startLocation = extractValue(doc, "start_location"),
            lookAt = extractValue(doc, "look_at"),
            simIp = extractValue(doc, "sim_ip"),
            simPort = extractValue(doc, "sim_port")?.toIntOrNull(),
            regionX = extractValue(doc, "region_x")?.toIntOrNull(),
            regionY = extractValue(doc, "region_y")?.toIntOrNull(),
            seedCapability = extractValue(doc, "seed_capability"),
            circuitCode = extractValue(doc, "circuit_code")?.toIntOrNull(),
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
                return valueNode.textContent?.trim()
            }
        }
        return null
    }

    private fun extractArrayValue(doc: Document, tagName: String): List<Map<String, String>>? {
        val nodes = doc.getElementsByTagName("name")
        for (i in 0 until nodes.length) {
            val node = nodes.item(i)
            if (node.textContent == tagName) {
                val parent = node.parentNode as? Element ?: continue
                val valueNode = parent.getElementsByTagName("value").item(0) as? Element ?: continue
                val arrayNode = valueNode.getElementsByTagName("array").item(0) as? Element ?: continue

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
