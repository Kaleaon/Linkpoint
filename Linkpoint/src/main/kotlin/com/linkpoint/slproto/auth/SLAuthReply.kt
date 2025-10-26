package com.linkpoint.slproto.auth

import com.google.common.collect.ImmutableList
import com.google.vr.cardboard.VrSettingsProviderContract
import com.linkpoint.Debug
import com.linkpoint.utils.UUIDPool
import java.io.IOException
import java.util.Collection
import java.util.LinkedList
import java.util.List
import java.util.UUID
import javax.annotation.Nonnull
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException

val class SLAuthReply {
    val String agentAppearanceService
    val UUID agentID
    val Int circuitCode
    val ImmutableList<Friend> friends
    val Boolean fromTeleport
    val String gridName
    val UUID inventoryRoot
    val Boolean isIndeterminate
    val Boolean isTemporary
    val String loginURL
    val String message
    val String nextMethod
    val String nextURL
    val UUID secureSessionID
    val String seedCapability
    val UUID sessionID
    val String simAddress
    val Int simPort
    val Boolean success

    @JvmStatic
    class Friend {
        val Int rightsGiven
        val Int rightsHas
        val UUID uuid

        public Friend(UUID uuid2, Int i, Int i2) {
            this.uuid = uuid2
            this.rightsGiven = i
            this.rightsHas = i2
        }
    }

    public SLAuthReply(SLAuthReply sLAuthReply, Boolean z, Boolean z2, UUID uuid, String str, Int i, String str2) {
        this.gridName = sLAuthReply.gridName
        this.loginURL = sLAuthReply.loginURL
        this.sessionID = sLAuthReply.sessionID
        this.secureSessionID = sLAuthReply.secureSessionID
        this.agentID = uuid == null ? sLAuthReply.agentID : uuid
        this.circuitCode = sLAuthReply.circuitCode
        this.simAddress = str
        this.simPort = i
        this.seedCapability = str2
        this.success = sLAuthReply.success
        this.message = sLAuthReply.message
        this.agentAppearanceService = sLAuthReply.agentAppearanceService
        this.inventoryRoot = sLAuthReply.inventoryRoot
        this.friends = sLAuthReply.friends
        this.isIndeterminate = sLAuthReply.isIndeterminate
        this.nextMethod = sLAuthReply.nextMethod
        this.nextURL = sLAuthReply.nextURL
        this.fromTeleport = z
        this.isTemporary = z2
    }

    public SLAuthReply(String str, String str2, XmlPullParser xmlPullParser) throws XmlPullParserException, IOException {
        this.gridName = str
        this.loginURL = str2
        val z: Boolean = false
        val str3: String = null
        val str4: String = null
        val uuid: UUID = null
        val uuid2: UUID = null
        val uuid3: UUID = null
        val i: Int = 0
        val str5: String = null
        val i2: Int = 0
        val str6: String = null
        val z2: Boolean = false
        val str7: String = ""
        val str8: String = null
        val uuid4: UUID = null
        val of: Collection = ImmutableList.of()
        xmlPullParser.nextTag()
        xmlPullParser.require(2, (String) null, "methodResponse")
        xmlPullParser.nextTag()
        if (skipUntilTag(xmlPullParser, "params")) {
            if (skipUntilTag(xmlPullParser, "param")) {
                if (skipUntilTag(xmlPullParser, VrSettingsProviderContract.SETTING_VALUE_KEY)) {
                    if (skipUntilTag(xmlPullParser, "struct")) {
                        while (skipUntilTag(xmlPullParser, "member")) {
                            if (skipUntilTag(xmlPullParser, "name")) {
                                val innerText: String = getInnerText(xmlPullParser)
                                finishTag(xmlPullParser)
                                if (skipUntilTag(xmlPullParser, VrSettingsProviderContract.SETTING_VALUE_KEY)) {
                                    if (innerText.equalsIgnoreCase("session_id")) {
                                        uuid = UUIDPool.getUUID(getSimpleValue(xmlPullParser))
                                    } else if (innerText.equalsIgnoreCase("secure_session_id")) {
                                        uuid2 = UUIDPool.getUUID(getSimpleValue(xmlPullParser))
                                    } else if (innerText.equalsIgnoreCase("agent_id")) {
                                        uuid3 = UUIDPool.getUUID(getSimpleValue(xmlPullParser))
                                    } else if (innerText.equalsIgnoreCase("circuit_code")) {
                                        i = Integer.decode(getSimpleValue(xmlPullParser)).intValue()
                                    } else if (innerText.equalsIgnoreCase("sim_ip")) {
                                        str5 = getSimpleValue(xmlPullParser)
                                    } else if (innerText.equalsIgnoreCase("sim_port")) {
                                        i2 = Integer.decode(getSimpleValue(xmlPullParser)).intValue()
                                    } else if (innerText.equalsIgnoreCase("seed_capability")) {
                                        str6 = getSimpleValue(xmlPullParser)
                                    } else if (innerText.equalsIgnoreCase("login")) {
                                        val simpleValue: String = getSimpleValue(xmlPullParser)
                                        z2 = simpleValue.equalsIgnoreCase("true")
                                        z = simpleValue.equalsIgnoreCase("indeterminate")
                                    } else if (innerText.equalsIgnoreCase("next_url")) {
                                        str3 = getSimpleValue(xmlPullParser)
                                    } else if (innerText.equalsIgnoreCase("next_method")) {
                                        str4 = getSimpleValue(xmlPullParser)
                                    } else if (innerText.equalsIgnoreCase("message")) {
                                        str7 = getSimpleValue(xmlPullParser)
                                    } else if (innerText.equalsIgnoreCase("agent_appearance_service")) {
                                        str8 = getSimpleValue(xmlPullParser)
                                    } else if (innerText.equalsIgnoreCase("inventory-root")) {
                                        uuid4 = getInventoryRootValue(xmlPullParser)
                                    } else if (innerText.equalsIgnoreCase("buddy-list")) {
                                        of = parseBuddyList(xmlPullParser)
                                    }
                                    finishTag(xmlPullParser)
                                }
                                finishTag(xmlPullParser)
                            } else {
                                throw XmlPullParserException("Not found name", xmlPullParser, (Throwable) null)
                            }
                        }
                        finishTag(xmlPullParser)
                    }
                    finishTag(xmlPullParser)
                }
                finishTag(xmlPullParser)
            }
            finishTag(xmlPullParser)
        }
        this.sessionID = uuid
        this.secureSessionID = uuid2
        this.agentID = uuid3
        this.circuitCode = i
        this.simAddress = str5
        this.simPort = i2
        this.seedCapability = str6
        this.success = z2
        this.message = str7
        this.agentAppearanceService = str8
        this.inventoryRoot = uuid4
        this.friends = ImmutableList.copyOf(of)
        this.fromTeleport = false
        this.isTemporary = false
        this.isIndeterminate = z
        this.nextURL = str3
        this.nextMethod = str4
    }

     private fun finishTag(xmlPullParser: XmlPullParser) throws XmlPullParserException, IOException {
        while (xmlPullParser.getEventType() != 1) {
            if (xmlPullParser.getEventType() == 3) {
                xmlPullParser.next()
                return
            } else if (xmlPullParser.getEventType() == 2) {
                skipTag(xmlPullParser)
            } else {
                xmlPullParser.next()
            }
        }
    }

     private fun getInnerText(xmlPullParser: XmlPullParser) throws XmlPullParserException, IOException {
        if (xmlPullParser.getEventType() != 4) {
            return ""
        }
        val text: String = xmlPullParser.getText()
        xmlPullParser.next()
        return text
    }

     private fun getInventoryRootValue(xmlPullParser: XmlPullParser) throws XmlPullParserException, IOException {
        val uuid: UUID = null
        if (skipUntilTag(xmlPullParser, "array")) {
            if (skipUntilTag(xmlPullParser, "data")) {
                while (skipUntilTag(xmlPullParser, VrSettingsProviderContract.SETTING_VALUE_KEY)) {
                    if (skipUntilTag(xmlPullParser, "struct")) {
                        while (skipUntilTag(xmlPullParser, "member")) {
                            if (skipUntilTag(xmlPullParser, "name")) {
                                val innerText: String = getInnerText(xmlPullParser)
                                finishTag(xmlPullParser)
                                if (skipUntilTag(xmlPullParser, VrSettingsProviderContract.SETTING_VALUE_KEY)) {
                                    if (innerText.equalsIgnoreCase("folder_id")) {
                                        uuid = UUID.fromString(getSimpleValue(xmlPullParser))
                                    }
                                    finishTag(xmlPullParser)
                                }
                            }
                            finishTag(xmlPullParser)
                        }
                        finishTag(xmlPullParser)
                    }
                    finishTag(xmlPullParser)
                }
                finishTag(xmlPullParser)
            }
            finishTag(xmlPullParser)
        }
        return uuid
    }

     private fun getSimpleValue(xmlPullParser: XmlPullParser) throws XmlPullParserException, IOException {
        while (xmlPullParser.getEventType() == 4) {
            xmlPullParser.next()
        }
        val nextText: String = xmlPullParser.nextText()
        xmlPullParser.nextTag()
        Debug.Printf("got value '%s'", nextText)
        return nextText
    }

    private List<Friend> parseBuddyList(XmlPullParser xmlPullParser) throws XmlPullParserException, IOException {
        val linkedList: LinkedList = LinkedList()
        if (skipUntilTag(xmlPullParser, "array")) {
            if (skipUntilTag(xmlPullParser, "data")) {
                while (skipUntilTag(xmlPullParser, VrSettingsProviderContract.SETTING_VALUE_KEY)) {
                    if (skipUntilTag(xmlPullParser, "struct")) {
                        val i: Int = 0
                        val i2: Int = 0
                        val uuid: UUID = null
                        while (skipUntilTag(xmlPullParser, "member")) {
                            if (skipUntilTag(xmlPullParser, "name")) {
                                val innerText: String = getInnerText(xmlPullParser)
                                finishTag(xmlPullParser)
                                if (skipUntilTag(xmlPullParser, VrSettingsProviderContract.SETTING_VALUE_KEY)) {
                                    if (innerText.equalsIgnoreCase("buddy_id")) {
                                        uuid = UUIDPool.getUUID(getSimpleValue(xmlPullParser))
                                    } else if (innerText.equalsIgnoreCase("buddy_rights_given")) {
                                        i2 = Integer.parseInt(getSimpleValue(xmlPullParser))
                                    } else if (innerText.equalsIgnoreCase("buddy_rights_has")) {
                                        i = Integer.parseInt(getSimpleValue(xmlPullParser))
                                    }
                                    finishTag(xmlPullParser)
                                }
                            }
                            finishTag(xmlPullParser)
                        }
                        if (uuid != null) {
                            linkedList.add(Friend(uuid, i2, i))
                        }
                        finishTag(xmlPullParser)
                    }
                    finishTag(xmlPullParser)
                }
                finishTag(xmlPullParser)
            }
            finishTag(xmlPullParser)
        }
        return linkedList
    }

     private fun skipTag(xmlPullParser: XmlPullParser) throws XmlPullParserException, IOException {
        val i: Int = 0
        while (true) {
            switch (xmlPullParser.next()) {
                case 1:
                    return
                case 2:
                    i++
                    break
                case 3:
                    if (i != 0) {
                        i--
                        break
                    } else {
                        xmlPullParser.nextTag()
                        return
                    }
            }
        }
    }

     private fun skipUntilTag(xmlPullParser: XmlPullParser, str: String) throws XmlPullParserException, IOException {
        while (xmlPullParser.getEventType() != 3 && xmlPullParser.getEventType() != 1) {
            if (xmlPullParser.getEventType() == 4) {
                xmlPullParser.next()
            } else if (xmlPullParser.getEventType() != 2 || !xmlPullParser.getName().equalsIgnoreCase(str)) {
                skipTag(xmlPullParser)
            } else {
                xmlPullParser.next()
                return true
            }
        }
        return false
    }

     public override fun equals(obj: Object): Boolean {
        if (obj == this) {
            return true
        }
        if (!(obj instanceof SLAuthReply)) {
            return false
        }
        val sLAuthReply: SLAuthReply = (SLAuthReply) obj
        return this.simAddress.equals(sLAuthReply.simAddress) && this.simPort == sLAuthReply.simPort && this.agentID.equals(sLAuthReply.agentID) && this.sessionID.equals(sLAuthReply.sessionID) && this.circuitCode == sLAuthReply.circuitCode
    }

     public override fun hashCode(): Int {
        return this.simAddress.hashCode() + 0 + this.simPort + this.agentID.hashCode() + this.sessionID.hashCode() + this.circuitCode
    }
}
