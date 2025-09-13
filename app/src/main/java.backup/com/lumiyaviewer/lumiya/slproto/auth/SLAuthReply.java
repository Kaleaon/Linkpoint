package com.lumiyaviewer.lumiya.slproto.auth;

import com.google.common.collect.ImmutableList;
import com.google.vr.cardboard.VrSettingsProviderContract;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.utils.UUIDPool;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nonnull;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public final class SLAuthReply {
    public final String agentAppearanceService;
    public final UUID agentID;
    public final int circuitCode;
    public final ImmutableList<Friend> friends;
    public final boolean fromTeleport;
    public final String gridName;
    public final UUID inventoryRoot;
    public final boolean isIndeterminate;
    public final boolean isTemporary;
    public final String loginURL;
    public final String message;
    public final String nextMethod;
    public final String nextURL;
    public final UUID secureSessionID;
    public final String seedCapability;
    public final UUID sessionID;
    public final String simAddress;
    public final int simPort;
    public final boolean success;

    public static class Friend {
        public final int rightsGiven;
        public final int rightsHas;
        @Nonnull
        public final UUID uuid;

        public Friend(@Nonnull UUID uuid2, int i, int i2) {
            this.uuid = uuid2;
            this.rightsGiven = i;
            this.rightsHas = i2;
        }
    }

    public SLAuthReply(SLAuthReply sLAuthReply, boolean z, boolean z2, UUID uuid, String str, int i, String str2) {
        this.gridName = sLAuthReply.gridName;
        this.loginURL = sLAuthReply.loginURL;
        this.sessionID = sLAuthReply.sessionID;
        this.secureSessionID = sLAuthReply.secureSessionID;
        this.agentID = uuid == null ? sLAuthReply.agentID : uuid;
        this.circuitCode = sLAuthReply.circuitCode;
        this.simAddress = str;
        this.simPort = i;
        this.seedCapability = str2;
        this.success = sLAuthReply.success;
        this.message = sLAuthReply.message;
        this.agentAppearanceService = sLAuthReply.agentAppearanceService;
        this.inventoryRoot = sLAuthReply.inventoryRoot;
        this.friends = sLAuthReply.friends;
        this.isIndeterminate = sLAuthReply.isIndeterminate;
        this.nextMethod = sLAuthReply.nextMethod;
        this.nextURL = sLAuthReply.nextURL;
        this.fromTeleport = z;
        this.isTemporary = z2;
    }

    public SLAuthReply(String str, String str2, @Nonnull XmlPullParser xmlPullParser) throws XmlPullParserException, IOException {
        this.gridName = str;
        this.loginURL = str2;
        boolean z = false;
        String str3 = null;
        String str4 = null;
        UUID uuid = null;
        UUID uuid2 = null;
        UUID uuid3 = null;
        int i = 0;
        String str5 = null;
        int i2 = 0;
        String str6 = null;
        boolean z2 = false;
        String str7 = "";
        String str8 = null;
        UUID uuid4 = null;
        Collection of = ImmutableList.of();
        xmlPullParser.nextTag();
        xmlPullParser.require(2, (String) null, "methodResponse");
        xmlPullParser.nextTag();
        if (skipUntilTag(xmlPullParser, "params")) {
            if (skipUntilTag(xmlPullParser, "param")) {
                if (skipUntilTag(xmlPullParser, VrSettingsProviderContract.SETTING_VALUE_KEY)) {
                    if (skipUntilTag(xmlPullParser, "struct")) {
                        while (skipUntilTag(xmlPullParser, "member")) {
                            if (skipUntilTag(xmlPullParser, "name")) {
                                String innerText = getInnerText(xmlPullParser);
                                finishTag(xmlPullParser);
                                if (skipUntilTag(xmlPullParser, VrSettingsProviderContract.SETTING_VALUE_KEY)) {
                                    if (innerText.equalsIgnoreCase("session_id")) {
                                        uuid = UUIDPool.getUUID(getSimpleValue(xmlPullParser));
                                    } else if (innerText.equalsIgnoreCase("secure_session_id")) {
                                        uuid2 = UUIDPool.getUUID(getSimpleValue(xmlPullParser));
                                    } else if (innerText.equalsIgnoreCase("agent_id")) {
                                        uuid3 = UUIDPool.getUUID(getSimpleValue(xmlPullParser));
                                    } else if (innerText.equalsIgnoreCase("circuit_code")) {
                                        i = Integer.decode(getSimpleValue(xmlPullParser)).intValue();
                                    } else if (innerText.equalsIgnoreCase("sim_ip")) {
                                        str5 = getSimpleValue(xmlPullParser);
                                    } else if (innerText.equalsIgnoreCase("sim_port")) {
                                        i2 = Integer.decode(getSimpleValue(xmlPullParser)).intValue();
                                    } else if (innerText.equalsIgnoreCase("seed_capability")) {
                                        str6 = getSimpleValue(xmlPullParser);
                                    } else if (innerText.equalsIgnoreCase("login")) {
                                        String simpleValue = getSimpleValue(xmlPullParser);
                                        z2 = simpleValue.equalsIgnoreCase("true");
                                        z = simpleValue.equalsIgnoreCase("indeterminate");
                                    } else if (innerText.equalsIgnoreCase("next_url")) {
                                        str3 = getSimpleValue(xmlPullParser);
                                    } else if (innerText.equalsIgnoreCase("next_method")) {
                                        str4 = getSimpleValue(xmlPullParser);
                                    } else if (innerText.equalsIgnoreCase("message")) {
                                        str7 = getSimpleValue(xmlPullParser);
                                    } else if (innerText.equalsIgnoreCase("agent_appearance_service")) {
                                        str8 = getSimpleValue(xmlPullParser);
                                    } else if (innerText.equalsIgnoreCase("inventory-root")) {
                                        uuid4 = getInventoryRootValue(xmlPullParser);
                                    } else if (innerText.equalsIgnoreCase("buddy-list")) {
                                        of = parseBuddyList(xmlPullParser);
                                    }
                                    finishTag(xmlPullParser);
                                }
                                finishTag(xmlPullParser);
                            } else {
                                throw new XmlPullParserException("Not found name", xmlPullParser, (Throwable) null);
                            }
                        }
                        finishTag(xmlPullParser);
                    }
                    finishTag(xmlPullParser);
                }
                finishTag(xmlPullParser);
            }
            finishTag(xmlPullParser);
        }
        this.sessionID = uuid;
        this.secureSessionID = uuid2;
        this.agentID = uuid3;
        this.circuitCode = i;
        this.simAddress = str5;
        this.simPort = i2;
        this.seedCapability = str6;
        this.success = z2;
        this.message = str7;
        this.agentAppearanceService = str8;
        this.inventoryRoot = uuid4;
        this.friends = ImmutableList.copyOf(of);
        this.fromTeleport = false;
        this.isTemporary = false;
        this.isIndeterminate = z;
        this.nextURL = str3;
        this.nextMethod = str4;
    }

    private void finishTag(XmlPullParser xmlPullParser) throws XmlPullParserException, IOException {
        while (xmlPullParser.getEventType() != 1) {
            if (xmlPullParser.getEventType() == 3) {
                xmlPullParser.next();
                return;
            } else if (xmlPullParser.getEventType() == 2) {
                skipTag(xmlPullParser);
            } else {
                xmlPullParser.next();
            }
        }
    }

    private String getInnerText(XmlPullParser xmlPullParser) throws XmlPullParserException, IOException {
        if (xmlPullParser.getEventType() != 4) {
            return "";
        }
        String text = xmlPullParser.getText();
        xmlPullParser.next();
        return text;
    }

    private UUID getInventoryRootValue(XmlPullParser xmlPullParser) throws XmlPullParserException, IOException {
        UUID uuid = null;
        if (skipUntilTag(xmlPullParser, "array")) {
            if (skipUntilTag(xmlPullParser, "data")) {
                while (skipUntilTag(xmlPullParser, VrSettingsProviderContract.SETTING_VALUE_KEY)) {
                    if (skipUntilTag(xmlPullParser, "struct")) {
                        while (skipUntilTag(xmlPullParser, "member")) {
                            if (skipUntilTag(xmlPullParser, "name")) {
                                String innerText = getInnerText(xmlPullParser);
                                finishTag(xmlPullParser);
                                if (skipUntilTag(xmlPullParser, VrSettingsProviderContract.SETTING_VALUE_KEY)) {
                                    if (innerText.equalsIgnoreCase("folder_id")) {
                                        uuid = UUID.fromString(getSimpleValue(xmlPullParser));
                                    }
                                    finishTag(xmlPullParser);
                                }
                            }
                            finishTag(xmlPullParser);
                        }
                        finishTag(xmlPullParser);
                    }
                    finishTag(xmlPullParser);
                }
                finishTag(xmlPullParser);
            }
            finishTag(xmlPullParser);
        }
        return uuid;
    }

    private String getSimpleValue(XmlPullParser xmlPullParser) throws XmlPullParserException, IOException {
        while (xmlPullParser.getEventType() == 4) {
            xmlPullParser.next();
        }
        String nextText = xmlPullParser.nextText();
        xmlPullParser.nextTag();
        Debug.Printf("got value '%s'", nextText);
        return nextText;
    }

    private List<Friend> parseBuddyList(XmlPullParser xmlPullParser) throws XmlPullParserException, IOException {
        LinkedList linkedList = new LinkedList();
        if (skipUntilTag(xmlPullParser, "array")) {
            if (skipUntilTag(xmlPullParser, "data")) {
                while (skipUntilTag(xmlPullParser, VrSettingsProviderContract.SETTING_VALUE_KEY)) {
                    if (skipUntilTag(xmlPullParser, "struct")) {
                        int i = 0;
                        int i2 = 0;
                        UUID uuid = null;
                        while (skipUntilTag(xmlPullParser, "member")) {
                            if (skipUntilTag(xmlPullParser, "name")) {
                                String innerText = getInnerText(xmlPullParser);
                                finishTag(xmlPullParser);
                                if (skipUntilTag(xmlPullParser, VrSettingsProviderContract.SETTING_VALUE_KEY)) {
                                    if (innerText.equalsIgnoreCase("buddy_id")) {
                                        uuid = UUIDPool.getUUID(getSimpleValue(xmlPullParser));
                                    } else if (innerText.equalsIgnoreCase("buddy_rights_given")) {
                                        i2 = Integer.parseInt(getSimpleValue(xmlPullParser));
                                    } else if (innerText.equalsIgnoreCase("buddy_rights_has")) {
                                        i = Integer.parseInt(getSimpleValue(xmlPullParser));
                                    }
                                    finishTag(xmlPullParser);
                                }
                            }
                            finishTag(xmlPullParser);
                        }
                        if (uuid != null) {
                            linkedList.add(new Friend(uuid, i2, i));
                        }
                        finishTag(xmlPullParser);
                    }
                    finishTag(xmlPullParser);
                }
                finishTag(xmlPullParser);
            }
            finishTag(xmlPullParser);
        }
        return linkedList;
    }

    private void skipTag(XmlPullParser xmlPullParser) throws XmlPullParserException, IOException {
        int i = 0;
        while (true) {
            switch (xmlPullParser.next()) {
                case 1:
                    return;
                case 2:
                    i++;
                    break;
                case 3:
                    if (i != 0) {
                        i--;
                        break;
                    } else {
                        xmlPullParser.nextTag();
                        return;
                    }
            }
        }
    }

    private boolean skipUntilTag(XmlPullParser xmlPullParser, String str) throws XmlPullParserException, IOException {
        while (xmlPullParser.getEventType() != 3 && xmlPullParser.getEventType() != 1) {
            if (xmlPullParser.getEventType() == 4) {
                xmlPullParser.next();
            } else if (xmlPullParser.getEventType() != 2 || !xmlPullParser.getName().equalsIgnoreCase(str)) {
                skipTag(xmlPullParser);
            } else {
                xmlPullParser.next();
                return true;
            }
        }
        return false;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof SLAuthReply)) {
            return false;
        }
        SLAuthReply sLAuthReply = (SLAuthReply) obj;
        return this.simAddress.equals(sLAuthReply.simAddress) && this.simPort == sLAuthReply.simPort && this.agentID.equals(sLAuthReply.agentID) && this.sessionID.equals(sLAuthReply.sessionID) && this.circuitCode == sLAuthReply.circuitCode;
    }

    public int hashCode() {
        return this.simAddress.hashCode() + 0 + this.simPort + this.agentID.hashCode() + this.sessionID.hashCode() + this.circuitCode;
    }
}
