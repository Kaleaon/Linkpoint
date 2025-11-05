package com.linkpoint.slproto.auth

import com.linkpoint.Debug
import com.linkpoint.utils.UUIDPool
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.IOException
import java.io.Reader
import java.io.StringReader
import java.util.UUID

/**
 * Result of a Second Life XML-RPC login request.
 */
data class SLAuthReply(
    val gridName: String,
    val loginUrl: String,
    val agentId: UUID?,
    val sessionId: UUID?,
    val secureSessionId: UUID?,
    val circuitCode: Int,
    val simAddress: String?,
    val simPort: Int,
    val seedCapability: String?,
    val success: Boolean,
    val message: String,
    val agentAppearanceService: String?,
    val inventoryRoot: UUID?,
    val friends: List<Friend>,
    val isIndeterminate: Boolean,
    val nextUrl: String?,
    val nextMethod: String?,
    val fromTeleport: Boolean = false,
    val isTemporary: Boolean = false
) {

    data class Friend(val uuid: UUID, val rightsGiven: Int, val rightsHas: Int)

    fun withRegion(
        agentIdOverride: UUID?,
        newSimAddress: String?,
        newSimPort: Int,
        newSeedCapability: String?,
        fromTeleport: Boolean,
        isTemporary: Boolean
    ): SLAuthReply {
        return copy(
            agentId = agentIdOverride ?: agentId,
            simAddress = newSimAddress ?: simAddress,
            simPort = newSimPort,
            seedCapability = newSeedCapability ?: seedCapability,
            fromTeleport = fromTeleport,
            isTemporary = isTemporary
        )
    }

    companion object {
        private val parserFactory: XmlPullParserFactory by lazy {
            XmlPullParserFactory.newInstance().apply { isNamespaceAware = false }
        }

        fun fromXml(gridName: String, loginUrl: String, xml: String): SLAuthReply =
            fromXml(gridName, loginUrl, StringReader(xml))

        fun fromXml(gridName: String, loginUrl: String, reader: Reader): SLAuthReply {
            val parser = parserFactory.newPullParser().apply { setInput(reader) }
            val payload = parseMethodResponse(parser)

            val loginState = payload["login"]?.toString()?.lowercase()
            val success = loginState == "true"
            val isIndeterminate = loginState == "indeterminate"
            val message = payload["message"]?.toString() ?: ""
            val agentAppearanceService = payload["agent_appearance_service"]?.toString()
            val sessionId = payload["session_id"]?.toString()?.let(UUIDPool::getUUID)
            val secureSessionId = payload["secure_session_id"]?.toString()?.let(UUIDPool::getUUID)
            val agentId = payload["agent_id"]?.toString()?.let(UUIDPool::getUUID)
            val circuitCode = payload["circuit_code"].safeInt()
            val simAddress = payload["sim_ip"]?.toString()
            val simPort = payload["sim_port"].safeInt()
            val seedCapability = payload["seed_capability"]?.toString()
            val inventoryRoot = extractInventoryRoot(payload["inventory-root"])
            val friends = extractBuddyList(payload["buddy-list"])
            val nextUrl = payload["next_url"]?.toString()
            val nextMethod = payload["next_method"]?.toString()

            return SLAuthReply(
                gridName = gridName,
                loginUrl = loginUrl,
                agentId = agentId,
                sessionId = sessionId,
                secureSessionId = secureSessionId,
                circuitCode = circuitCode,
                simAddress = simAddress,
                simPort = simPort,
                seedCapability = seedCapability,
                success = success,
                message = message,
                agentAppearanceService = agentAppearanceService,
                inventoryRoot = inventoryRoot,
                friends = friends,
                isIndeterminate = isIndeterminate,
                nextUrl = nextUrl,
                nextMethod = nextMethod
            )
        }

        private fun Any?.safeInt(default: Int = 0): Int = when (this) {
            is Number -> toInt()
            is String -> toIntOrNull() ?: default
            else -> default
        }

        private fun extractInventoryRoot(node: Any?): UUID? {
            val list = node as? List<*> ?: return null
            val firstStruct = list.firstOrNull() as? Map<*, *> ?: return null
            val folderId = firstStruct["folder_id"]?.toString() ?: return null
            return UUIDPool.getUUID(folderId)
        }

        private fun extractBuddyList(node: Any?): List<Friend> {
            val list = node as? List<*> ?: return emptyList()
            return list.mapNotNull { entry ->
                val struct = entry as? Map<*, *> ?: return@mapNotNull null
                val id = struct["buddy_id"]?.toString()?.let(UUIDPool::getUUID) ?: return@mapNotNull null
                val rightsGiven = struct["buddy_rights_given"].safeInt()
                val rightsHas = struct["buddy_rights_has"].safeInt()
                Friend(id, rightsGiven, rightsHas)
            }
        }

        private fun parseMethodResponse(parser: XmlPullParser): Map<String, Any?> {
            advanceToStart(parser)
            if (parser.eventType != XmlPullParser.START_TAG || parser.name != "methodResponse") {
                throw IOException("Unexpected XML root: ${parser.name}")
            }
            parser.nextTag()
            return when (parser.name) {
                "params" -> parseParams(parser)
                "fault" -> {
                    parser.nextTag() // value
                    val faultValue = parseValue(parser)
                    val faultMap = faultValue as? Map<String, Any?>
                    val message = faultMap?.get("faultString")?.toString()
                    Debug.Log("Login fault: $message")
                    faultMap ?: emptyMap()
                }
                else -> emptyMap()
            }
        }

        private fun parseParams(parser: XmlPullParser): Map<String, Any?> {
            parser.require(XmlPullParser.START_TAG, null, "params")
            parser.nextTag() // param
            parser.require(XmlPullParser.START_TAG, null, "param")
            parser.nextTag() // value
            val payload = parseValue(parser)
            closeTag(parser, "param")
            closeTag(parser, "params")
            @Suppress("UNCHECKED_CAST")
            return payload as? Map<String, Any?> ?: emptyMap()
        }

        private fun parseValue(parser: XmlPullParser): Any? {
            parser.require(XmlPullParser.START_TAG, null, "value")
            var result: Any? = null
            when (parser.next()) {
                XmlPullParser.START_TAG -> result = parseTypedValue(parser)
                XmlPullParser.TEXT -> {
                    result = parser.text
                    parser.next()
                }
                XmlPullParser.END_TAG -> result = ""
            }
            closeTag(parser, "value")
            return result
        }

        private fun parseTypedValue(parser: XmlPullParser): Any? {
            return when (parser.name) {
                "string" -> parser.nextText().also { parser.nextTagOrEnd() }
                "boolean" -> parser.nextText().trim().let { it == "1" || it.equals("true", true) }
                    .also { parser.nextTagOrEnd() }
                "int", "i4" -> parser.nextText().trim().toIntOrNull().also { parser.nextTagOrEnd() }
                "double" -> parser.nextText().trim().toDoubleOrNull().also { parser.nextTagOrEnd() }
                "struct" -> parseStruct(parser)
                "array" -> parseArray(parser)
                "nil" -> {
                    closeTag(parser, "nil")
                    null
                }
                else -> parser.nextText().also { parser.nextTagOrEnd() }
            }
        }

        private fun parseStruct(parser: XmlPullParser): Map<String, Any?> {
            parser.require(XmlPullParser.START_TAG, null, "struct")
            val result = mutableMapOf<String, Any?>()
            while (true) {
                when (parser.next()) {
                    XmlPullParser.START_TAG -> {
                        if (parser.name == "member") {
                            val (key, value) = parseMember(parser)
                            if (key != null) {
                                result[key] = value
                            }
                        } else {
                            skipTag(parser)
                        }
                    }
                    XmlPullParser.END_TAG -> {
                        if (parser.name == "struct") {
                            parser.nextTagOrEnd()
                            return result
                        }
                    }
                    XmlPullParser.END_DOCUMENT -> return result
                }
            }
        }

        private fun parseMember(parser: XmlPullParser): Pair<String?, Any?> {
            parser.require(XmlPullParser.START_TAG, null, "member")
            var name: String? = null
            var value: Any? = null
            while (true) {
                when (parser.next()) {
                    XmlPullParser.START_TAG -> when (parser.name) {
                        "name" -> {
                            name = parser.nextText()
                            parser.nextTagOrEnd()
                        }
                        "value" -> {
                            value = parseValue(parser)
                        }
                        else -> skipTag(parser)
                    }
                    XmlPullParser.END_TAG -> if (parser.name == "member") {
                        parser.nextTagOrEnd()
                        return name to value
                    }
                    XmlPullParser.END_DOCUMENT -> return name to value
                }
            }
        }

        private fun parseArray(parser: XmlPullParser): List<Any?> {
            parser.require(XmlPullParser.START_TAG, null, "array")
            val values = mutableListOf<Any?>()
            parser.nextTag()
            if (parser.name == "data") {
                while (true) {
                    when (parser.next()) {
                        XmlPullParser.START_TAG -> if (parser.name == "value") {
                            values += parseValue(parser)
                        } else {
                            skipTag(parser)
                        }
                        XmlPullParser.END_TAG -> if (parser.name == "data") {
                            break
                        }
                        XmlPullParser.END_DOCUMENT -> return values
                    }
                }
            }
            closeTag(parser, "array")
            return values
        }

        private fun advanceToStart(parser: XmlPullParser) {
            while (parser.eventType != XmlPullParser.START_TAG &&
                parser.eventType != XmlPullParser.END_DOCUMENT
            ) {
                parser.next()
            }
        }

        private fun closeTag(parser: XmlPullParser, name: String) {
            while (parser.eventType != XmlPullParser.END_TAG || parser.name != name) {
                parser.next()
            }
            parser.nextTagOrEnd()
        }

        private fun XmlPullParser.nextTagOrEnd(): Int {
            var event = next()
            while (event != XmlPullParser.START_TAG &&
                event != XmlPullParser.END_TAG &&
                event != XmlPullParser.END_DOCUMENT
            ) {
                event = next()
            }
            return event
        }

        private fun skipTag(parser: XmlPullParser) {
            var depth = 1
            while (depth > 0) {
                when (parser.next()) {
                    XmlPullParser.START_TAG -> depth++
                    XmlPullParser.END_TAG -> depth--
                    XmlPullParser.END_DOCUMENT -> return
                }
            }
            parser.nextTagOrEnd()
        }
    }
}
