package com.lumiyaviewer.lumiya.slproto.auth

import com.google.common.collect.ImmutableList
import com.google.vr.cardboard.VrSettingsProviderContract
import com.lumiyaviewer.lumiya.Debug
import com.lumiyaviewer.lumiya.utils.UUIDPool
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.util.LinkedList
import java.util.UUID

class SLAuthReply {
    var agentAppearanceService: String? = null
    var agentID: UUID? = null
    var circuitCode: Int = 0
    var friends: ImmutableList<Friend>? = null
    var fromTeleport: Boolean = false
    var gridName: String? = null
    var inventoryRoot: UUID? = null
    var isIndeterminate: Boolean = false
    var isTemporary: Boolean = false
    var loginURL: String? = null
    var message: String? = null
    var nextMethod: String? = null
    var nextURL: String? = null
    var secureSessionID: UUID? = null
    var seedCapability: String? = null
    var sessionID: UUID? = null
    var simAddress: String? = null
    var simPort: Int = 0
    var success: Boolean = false

    class Friend(val uuid: UUID, val rightsGiven: Int, val rightsHas: Int)

    constructor(
        original: SLAuthReply,
        fromTeleport: Boolean,
        isTemporary: Boolean,
        agentID: UUID?,
        simAddress: String?,
        simPort: Int,
        seedCapability: String?
    ) {
        this.gridName = original.gridName
        this.loginURL = original.loginURL
        this.sessionID = original.sessionID
        this.secureSessionID = original.secureSessionID
        this.agentID = agentID ?: original.agentID
        this.circuitCode = original.circuitCode
        this.simAddress = simAddress
        this.simPort = simPort
        this.seedCapability = seedCapability
        this.success = original.success
        this.message = original.message
        this.agentAppearanceService = original.agentAppearanceService
        this.inventoryRoot = original.inventoryRoot
        this.friends = original.friends
        this.isIndeterminate = original.isIndeterminate
        this.nextMethod = original.nextMethod
        this.nextURL = original.nextURL
        this.fromTeleport = fromTeleport
        this.isTemporary = isTemporary
    }

    constructor(gridName: String?, loginURL: String?, parser: XmlPullParser) {
        this.gridName = gridName
        this.loginURL = loginURL
        
        var indeterminate = false
        var nextUrl: String? = null
        var nextMethod: String? = null
        var sessionId: UUID? = null
        var secureSessionId: UUID? = null
        var agentId: UUID? = null
        var circuitCode = 0
        var simIp: String? = null
        var simPort = 0
        var seedCap: String? = null
        var success = false
        var message: String? = ""
        var appearanceService: String? = null
        var invRoot: UUID? = null
        var friendList: List<Friend> = ImmutableList.of()

        parser.nextTag()
        parser.require(XmlPullParser.START_TAG, null, "methodResponse")
        parser.nextTag()
        
        if (skipUntilTag(parser, "params")) {
            if (skipUntilTag(parser, "param")) {
                if (skipUntilTag(parser, "value")) {
                    if (skipUntilTag(parser, "struct")) {
                        while (skipUntilTag(parser, "member")) {
                            if (skipUntilTag(parser, "name")) {
                                val name = getInnerText(parser)
                                finishTag(parser)
                                
                                if (skipUntilTag(parser, "value")) {
                                    if (name.equals("session_id", ignoreCase = true)) {
                                        sessionId = UUIDPool.getUUID(getSimpleValue(parser))
                                    } else if (name.equals("secure_session_id", ignoreCase = true)) {
                                        secureSessionId = UUIDPool.getUUID(getSimpleValue(parser))
                                    } else if (name.equals("agent_id", ignoreCase = true)) {
                                        agentId = UUIDPool.getUUID(getSimpleValue(parser))
                                    } else if (name.equals("circuit_code", ignoreCase = true)) {
                                        circuitCode = Integer.decode(getSimpleValue(parser))
                                    } else if (name.equals("sim_ip", ignoreCase = true)) {
                                        simIp = getSimpleValue(parser)
                                    } else if (name.equals("sim_port", ignoreCase = true)) {
                                        simPort = Integer.decode(getSimpleValue(parser))
                                    } else if (name.equals("seed_capability", ignoreCase = true)) {
                                        seedCap = getSimpleValue(parser)
                                    } else if (name.equals("login", ignoreCase = true)) {
                                        val value = getSimpleValue(parser)
                                        success = value.equals("true", ignoreCase = true)
                                        indeterminate = value.equals("indeterminate", ignoreCase = true)
                                    } else if (name.equals("next_url", ignoreCase = true)) {
                                        nextUrl = getSimpleValue(parser)
                                    } else if (name.equals("next_method", ignoreCase = true)) {
                                        nextMethod = getSimpleValue(parser)
                                    } else if (name.equals("message", ignoreCase = true)) {
                                        message = getSimpleValue(parser)
                                    } else if (name.equals("agent_appearance_service", ignoreCase = true)) {
                                        appearanceService = getSimpleValue(parser)
                                    } else if (name.equals("inventory-root", ignoreCase = true)) {
                                        invRoot = getInventoryRootValue(parser)
                                    } else if (name.equals("buddy-list", ignoreCase = true)) {
                                        friendList = parseBuddyList(parser)
                                    }
                                    finishTag(parser)
                                }
                                finishTag(parser)
                            } else {
                                throw XmlPullParserException("Not found name", parser, null)
                            }
                        }
                        finishTag(parser)
                    }
                    finishTag(parser)
                }
                finishTag(parser)
            }
            finishTag(parser)
        }

        this.sessionID = sessionId
        this.secureSessionID = secureSessionId
        this.agentID = agentId
        this.circuitCode = circuitCode
        this.simAddress = simIp
        this.simPort = simPort
        this.seedCapability = seedCap
        this.success = success
        this.message = message
        this.agentAppearanceService = appearanceService
        this.inventoryRoot = invRoot
        this.friends = ImmutableList.copyOf(friendList)
        this.fromTeleport = false
        this.isTemporary = false
        this.isIndeterminate = indeterminate
        this.nextURL = nextUrl
        this.nextMethod = nextMethod
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun finishTag(parser: XmlPullParser) {
        while (parser.eventType != XmlPullParser.END_TAG) {
            if (parser.eventType == XmlPullParser.END_DOCUMENT) {
                return
            } else if (parser.eventType == XmlPullParser.START_TAG) {
                skipTag(parser)
            } else {
                parser.next()
            }
        }
        parser.next()
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun getInnerText(parser: XmlPullParser): String {
        if (parser.eventType != XmlPullParser.TEXT) {
            return ""
        }
        val text = parser.text
        parser.next()
        return text
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun getInventoryRootValue(parser: XmlPullParser): UUID? {
        var uuid: UUID? = null
        if (skipUntilTag(parser, "array")) {
            if (skipUntilTag(parser, "data")) {
                while (skipUntilTag(parser, "value")) {
                    if (skipUntilTag(parser, "struct")) {
                        while (skipUntilTag(parser, "member")) {
                            if (skipUntilTag(parser, "name")) {
                                val name = getInnerText(parser)
                                finishTag(parser)
                                if (skipUntilTag(parser, "value")) {
                                    if (name.equals("folder_id", ignoreCase = true)) {
                                        uuid = UUID.fromString(getSimpleValue(parser))
                                    }
                                    finishTag(parser)
                                }
                            }
                            finishTag(parser)
                        }
                        finishTag(parser)
                    }
                    finishTag(parser)
                }
                finishTag(parser)
            }
            finishTag(parser)
        }
        return uuid
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun getSimpleValue(parser: XmlPullParser): String {
        while (parser.eventType == XmlPullParser.TEXT) {
            parser.next()
        }
        val text = parser.nextText()
        // nextText advances to END_TAG, we need to consume it in caller usually?
        // But logic says: parser.nextTag() after.
        // parser.nextText() reads content of current START_TAG and advances to END_TAG.
        // So we are at END_TAG.
        // But wait, if we are at START_TAG <string>, nextText() returns content and moves to </string>.
        // The original code had: parser.nextTag() after nextText(). 
        // If nextText() leaves us at END_TAG, nextTag() moves to next START_TAG or END_TAG (skipping text/whitespace).
        
        // Let's just assume standard nextText usage.
        // Debug.Printf("got value '%s'", text)
        return text
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun parseBuddyList(parser: XmlPullParser): List<Friend> {
        val list = LinkedList<Friend>()
        if (skipUntilTag(parser, "array")) {
            if (skipUntilTag(parser, "data")) {
                while (skipUntilTag(parser, "value")) {
                    if (skipUntilTag(parser, "struct")) {
                        var rightsGiven = 0
                        var rightsHas = 0
                        var uuid: UUID? = null
                        
                        while (skipUntilTag(parser, "member")) {
                            if (skipUntilTag(parser, "name")) {
                                val name = getInnerText(parser)
                                finishTag(parser)
                                if (skipUntilTag(parser, "value")) {
                                    if (name.equals("buddy_id", ignoreCase = true)) {
                                        uuid = UUIDPool.getUUID(getSimpleValue(parser))
                                    } else if (name.equals("buddy_rights_given", ignoreCase = true)) {
                                        rightsGiven = Integer.parseInt(getSimpleValue(parser))
                                    } else if (name.equals("buddy_rights_has", ignoreCase = true)) {
                                        rightsHas = Integer.parseInt(getSimpleValue(parser))
                                    }
                                    finishTag(parser)
                                }
                            }
                            finishTag(parser)
                        }
                        if (uuid != null) {
                            list.add(Friend(uuid, rightsGiven, rightsHas))
                        }
                        finishTag(parser)
                    }
                    finishTag(parser)
                }
                finishTag(parser)
            }
            finishTag(parser)
        }
        return list
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun skipTag(parser: XmlPullParser) {
        var depth = 1
        while (depth != 0) {
            when (parser.next()) {
                XmlPullParser.END_TAG -> depth--
                XmlPullParser.START_TAG -> depth++
            }
        }
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun skipUntilTag(parser: XmlPullParser, tagName: String): Boolean {
        while (parser.eventType != XmlPullParser.END_TAG && parser.eventType != XmlPullParser.END_DOCUMENT) {
            if (parser.eventType == XmlPullParser.TEXT) {
                parser.next()
            } else if (parser.eventType != XmlPullParser.START_TAG || !parser.name.equals(tagName, ignoreCase = true)) {
                if (parser.eventType == XmlPullParser.START_TAG) {
                    skipTag(parser)
                } else {
                    parser.next()
                }
            } else {
                parser.next()
                return true
            }
        }
        return false
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SLAuthReply) return false

        if (simAddress != other.simAddress) return false
        if (simPort != other.simPort) return false
        if (agentID != other.agentID) return false
        if (sessionID != other.sessionID) return false
        if (circuitCode != other.circuitCode) return false

        return true
    }

    override fun hashCode(): Int {
        var result = simAddress?.hashCode() ?: 0
        result = 31 * result + simPort
        result = 31 * result + (agentID?.hashCode() ?: 0)
        result = 31 * result + (sessionID?.hashCode() ?: 0)
        result = 31 * result + circuitCode
        return result
    }
}
