// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.auth;

import java.util.LinkedList;
import java.util.List;
import com.lumiyaviewer.lumiya.Debug;
import java.io.IOException;
import java.util.Collection;
import com.lumiyaviewer.lumiya.utils.UUIDPool;
import org.xmlpull.v1.XmlPullParserException;
import javax.annotation.Nonnull;
import org.xmlpull.v1.XmlPullParser;
import com.google.common.collect.ImmutableList;
import java.util.UUID;

public final class SLAuthReply
{
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
    
    public SLAuthReply(final SLAuthReply slAuthReply, final boolean fromTeleport, final boolean isTemporary, UUID agentID, final String simAddress, final int simPort, final String seedCapability) {
        this.gridName = slAuthReply.gridName;
        this.loginURL = slAuthReply.loginURL;
        this.sessionID = slAuthReply.sessionID;
        this.secureSessionID = slAuthReply.secureSessionID;
        if (agentID == null) {
            agentID = slAuthReply.agentID;
        }
        this.agentID = agentID;
        this.circuitCode = slAuthReply.circuitCode;
        this.simAddress = simAddress;
        this.simPort = simPort;
        this.seedCapability = seedCapability;
        this.success = slAuthReply.success;
        this.message = slAuthReply.message;
        this.agentAppearanceService = slAuthReply.agentAppearanceService;
        this.inventoryRoot = slAuthReply.inventoryRoot;
        this.friends = slAuthReply.friends;
        this.isIndeterminate = slAuthReply.isIndeterminate;
        this.nextMethod = slAuthReply.nextMethod;
        this.nextURL = slAuthReply.nextURL;
        this.fromTeleport = fromTeleport;
        this.isTemporary = isTemporary;
    }
    
    public SLAuthReply(final String gridName, String loginURL, @Nonnull final XmlPullParser xmlPullParser) throws XmlPullParserException, IOException {
        this.gridName = gridName;
        this.loginURL = loginURL;
        final boolean b = false;
        final boolean b2 = false;
        final boolean b3 = false;
        boolean isIndeterminate = false;
        final boolean b4 = false;
        final String s = null;
        final String s2 = null;
        final String s3 = null;
        String nextURL = null;
        final String s4 = null;
        final String s5 = null;
        final String s6 = null;
        final String s7 = null;
        String nextMethod = null;
        final String s8 = null;
        UUID uuid = null;
        final UUID uuid2 = null;
        final UUID uuid3 = null;
        UUID sessionID = null;
        final UUID uuid4 = null;
        UUID uuid5 = null;
        final UUID uuid6 = null;
        final UUID uuid7 = null;
        UUID secureSessionID = null;
        final UUID uuid8 = null;
        final UUID uuid9 = null;
        final UUID uuid10 = null;
        final UUID uuid11 = null;
        UUID agentID = null;
        final UUID uuid12 = null;
        final int n = 0;
        final int n2 = 0;
        final int n3 = 0;
        int circuitCode = 0;
        final int n4 = 0;
        String s9 = null;
        final String s10 = null;
        final String s11 = null;
        String simAddress = null;
        final String s12 = null;
        final int n5 = 0;
        final int n6 = 0;
        final int n7 = 0;
        int simPort = 0;
        final int n8 = 0;
        final String s13 = null;
        final String s14 = null;
        final String s15 = null;
        String seedCapability = null;
        final String s16 = null;
        final boolean b5 = false;
        final boolean b6 = false;
        final boolean b7 = false;
        boolean success = false;
        final boolean b8 = false;
        loginURL = "";
        String s17 = null;
        final String s18 = null;
        final String s19 = null;
        String agentAppearanceService = null;
        final String s20 = null;
        final UUID uuid13 = null;
        final UUID uuid14 = null;
        final UUID uuid15 = null;
        UUID inventoryRoot = null;
        final UUID uuid16 = null;
        Object o = ImmutableList.of();
        xmlPullParser.nextTag();
        xmlPullParser.require(2, (String)null, "methodResponse");
        xmlPullParser.nextTag();
        Object o2 = o;
        String message = loginURL;
        if (this.skipUntilTag(xmlPullParser, "params")) {
            o2 = o;
            inventoryRoot = uuid15;
            agentAppearanceService = s19;
            message = loginURL;
            success = b7;
            seedCapability = s15;
            simPort = n7;
            simAddress = s11;
            circuitCode = n3;
            agentID = uuid11;
            secureSessionID = uuid7;
            sessionID = uuid3;
            nextMethod = s7;
            nextURL = s3;
            isIndeterminate = b3;
            if (this.skipUntilTag(xmlPullParser, "param")) {
                o2 = o;
                inventoryRoot = uuid14;
                agentAppearanceService = s18;
                message = loginURL;
                success = b6;
                seedCapability = s14;
                simPort = n6;
                simAddress = s10;
                circuitCode = n2;
                agentID = uuid10;
                secureSessionID = uuid6;
                sessionID = uuid2;
                nextMethod = s6;
                nextURL = s2;
                isIndeterminate = b2;
                if (this.skipUntilTag(xmlPullParser, "value")) {
                    o2 = o;
                    inventoryRoot = uuid13;
                    message = loginURL;
                    success = b5;
                    seedCapability = s13;
                    simPort = n5;
                    circuitCode = n;
                    agentID = uuid9;
                    nextMethod = s5;
                    nextURL = s;
                    isIndeterminate = b;
                    if (this.skipUntilTag(xmlPullParser, "struct")) {
                        isIndeterminate = b4;
                        String s21 = s4;
                        String s22 = s8;
                        UUID uuid17 = uuid4;
                        UUID uuid18 = uuid8;
                        UUID uuid19 = uuid12;
                        circuitCode = n4;
                        String s23 = s12;
                        simPort = n8;
                        String s24 = s16;
                        success = b8;
                        String s25 = loginURL;
                        loginURL = s20;
                        UUID uuid20 = uuid16;
                        while (this.skipUntilTag(xmlPullParser, "member")) {
                            if (!this.skipUntilTag(xmlPullParser, "name")) {
                                throw new XmlPullParserException("Not found name", xmlPullParser, (Throwable)null);
                            }
                            final String innerText = this.getInnerText(xmlPullParser);
                            this.finishTag(xmlPullParser);
                            Object o3 = o;
                            UUID inventoryRootValue = uuid20;
                            String simpleValue = loginURL;
                            String simpleValue2 = s25;
                            boolean equalsIgnoreCase = success;
                            String simpleValue3 = s24;
                            int intValue = simPort;
                            String simpleValue4 = s23;
                            int intValue2 = circuitCode;
                            UUID uuid21 = uuid19;
                            UUID uuid22 = uuid18;
                            UUID uuid23 = uuid17;
                            String simpleValue5 = s22;
                            String simpleValue6 = s21;
                            boolean equalsIgnoreCase2 = isIndeterminate;
                            if (this.skipUntilTag(xmlPullParser, "value")) {
                                if (innerText.equalsIgnoreCase("session_id")) {
                                    uuid23 = UUIDPool.getUUID(this.getSimpleValue(xmlPullParser));
                                    equalsIgnoreCase2 = isIndeterminate;
                                    simpleValue6 = s21;
                                    simpleValue5 = s22;
                                    uuid22 = uuid18;
                                    uuid21 = uuid19;
                                    intValue2 = circuitCode;
                                    simpleValue4 = s23;
                                    intValue = simPort;
                                    simpleValue3 = s24;
                                    equalsIgnoreCase = success;
                                    simpleValue2 = s25;
                                    simpleValue = loginURL;
                                    inventoryRootValue = uuid20;
                                }
                                else if (innerText.equalsIgnoreCase("secure_session_id")) {
                                    uuid22 = UUIDPool.getUUID(this.getSimpleValue(xmlPullParser));
                                    inventoryRootValue = uuid20;
                                    simpleValue = loginURL;
                                    simpleValue2 = s25;
                                    equalsIgnoreCase = success;
                                    simpleValue3 = s24;
                                    intValue = simPort;
                                    simpleValue4 = s23;
                                    intValue2 = circuitCode;
                                    uuid21 = uuid19;
                                    uuid23 = uuid17;
                                    simpleValue5 = s22;
                                    simpleValue6 = s21;
                                    equalsIgnoreCase2 = isIndeterminate;
                                }
                                else if (innerText.equalsIgnoreCase("agent_id")) {
                                    uuid21 = UUIDPool.getUUID(this.getSimpleValue(xmlPullParser));
                                    inventoryRootValue = uuid20;
                                    simpleValue = loginURL;
                                    simpleValue2 = s25;
                                    equalsIgnoreCase = success;
                                    simpleValue3 = s24;
                                    intValue = simPort;
                                    simpleValue4 = s23;
                                    intValue2 = circuitCode;
                                    uuid22 = uuid18;
                                    uuid23 = uuid17;
                                    simpleValue5 = s22;
                                    simpleValue6 = s21;
                                    equalsIgnoreCase2 = isIndeterminate;
                                }
                                else if (innerText.equalsIgnoreCase("circuit_code")) {
                                    intValue2 = Integer.decode(this.getSimpleValue(xmlPullParser));
                                    inventoryRootValue = uuid20;
                                    simpleValue = loginURL;
                                    simpleValue2 = s25;
                                    equalsIgnoreCase = success;
                                    simpleValue3 = s24;
                                    intValue = simPort;
                                    simpleValue4 = s23;
                                    uuid21 = uuid19;
                                    uuid22 = uuid18;
                                    uuid23 = uuid17;
                                    simpleValue5 = s22;
                                    simpleValue6 = s21;
                                    equalsIgnoreCase2 = isIndeterminate;
                                }
                                else if (innerText.equalsIgnoreCase("sim_ip")) {
                                    simpleValue4 = this.getSimpleValue(xmlPullParser);
                                    inventoryRootValue = uuid20;
                                    simpleValue = loginURL;
                                    simpleValue2 = s25;
                                    equalsIgnoreCase = success;
                                    simpleValue3 = s24;
                                    intValue = simPort;
                                    intValue2 = circuitCode;
                                    uuid21 = uuid19;
                                    uuid22 = uuid18;
                                    uuid23 = uuid17;
                                    simpleValue5 = s22;
                                    simpleValue6 = s21;
                                    equalsIgnoreCase2 = isIndeterminate;
                                }
                                else if (innerText.equalsIgnoreCase("sim_port")) {
                                    intValue = Integer.decode(this.getSimpleValue(xmlPullParser));
                                    inventoryRootValue = uuid20;
                                    simpleValue = loginURL;
                                    simpleValue2 = s25;
                                    equalsIgnoreCase = success;
                                    simpleValue3 = s24;
                                    simpleValue4 = s23;
                                    intValue2 = circuitCode;
                                    uuid21 = uuid19;
                                    uuid22 = uuid18;
                                    uuid23 = uuid17;
                                    simpleValue5 = s22;
                                    simpleValue6 = s21;
                                    equalsIgnoreCase2 = isIndeterminate;
                                }
                                else if (innerText.equalsIgnoreCase("seed_capability")) {
                                    simpleValue3 = this.getSimpleValue(xmlPullParser);
                                    inventoryRootValue = uuid20;
                                    simpleValue = loginURL;
                                    simpleValue2 = s25;
                                    equalsIgnoreCase = success;
                                    intValue = simPort;
                                    simpleValue4 = s23;
                                    intValue2 = circuitCode;
                                    uuid21 = uuid19;
                                    uuid22 = uuid18;
                                    uuid23 = uuid17;
                                    simpleValue5 = s22;
                                    simpleValue6 = s21;
                                    equalsIgnoreCase2 = isIndeterminate;
                                }
                                else if (innerText.equalsIgnoreCase("login")) {
                                    final String simpleValue7 = this.getSimpleValue(xmlPullParser);
                                    equalsIgnoreCase = simpleValue7.equalsIgnoreCase("true");
                                    equalsIgnoreCase2 = simpleValue7.equalsIgnoreCase("indeterminate");
                                    inventoryRootValue = uuid20;
                                    simpleValue = loginURL;
                                    simpleValue2 = s25;
                                    simpleValue3 = s24;
                                    intValue = simPort;
                                    simpleValue4 = s23;
                                    intValue2 = circuitCode;
                                    uuid21 = uuid19;
                                    uuid22 = uuid18;
                                    uuid23 = uuid17;
                                    simpleValue5 = s22;
                                    simpleValue6 = s21;
                                }
                                else if (innerText.equalsIgnoreCase("next_url")) {
                                    simpleValue6 = this.getSimpleValue(xmlPullParser);
                                    inventoryRootValue = uuid20;
                                    simpleValue = loginURL;
                                    simpleValue2 = s25;
                                    equalsIgnoreCase = success;
                                    simpleValue3 = s24;
                                    intValue = simPort;
                                    simpleValue4 = s23;
                                    intValue2 = circuitCode;
                                    uuid21 = uuid19;
                                    uuid22 = uuid18;
                                    uuid23 = uuid17;
                                    simpleValue5 = s22;
                                    equalsIgnoreCase2 = isIndeterminate;
                                }
                                else if (innerText.equalsIgnoreCase("next_method")) {
                                    simpleValue5 = this.getSimpleValue(xmlPullParser);
                                    inventoryRootValue = uuid20;
                                    simpleValue = loginURL;
                                    simpleValue2 = s25;
                                    equalsIgnoreCase = success;
                                    simpleValue3 = s24;
                                    intValue = simPort;
                                    simpleValue4 = s23;
                                    intValue2 = circuitCode;
                                    uuid21 = uuid19;
                                    uuid22 = uuid18;
                                    uuid23 = uuid17;
                                    simpleValue6 = s21;
                                    equalsIgnoreCase2 = isIndeterminate;
                                }
                                else if (innerText.equalsIgnoreCase("message")) {
                                    simpleValue2 = this.getSimpleValue(xmlPullParser);
                                    inventoryRootValue = uuid20;
                                    simpleValue = loginURL;
                                    equalsIgnoreCase = success;
                                    simpleValue3 = s24;
                                    intValue = simPort;
                                    simpleValue4 = s23;
                                    intValue2 = circuitCode;
                                    uuid21 = uuid19;
                                    uuid22 = uuid18;
                                    uuid23 = uuid17;
                                    simpleValue5 = s22;
                                    simpleValue6 = s21;
                                    equalsIgnoreCase2 = isIndeterminate;
                                }
                                else if (innerText.equalsIgnoreCase("agent_appearance_service")) {
                                    simpleValue = this.getSimpleValue(xmlPullParser);
                                    inventoryRootValue = uuid20;
                                    simpleValue2 = s25;
                                    equalsIgnoreCase = success;
                                    simpleValue3 = s24;
                                    intValue = simPort;
                                    simpleValue4 = s23;
                                    intValue2 = circuitCode;
                                    uuid21 = uuid19;
                                    uuid22 = uuid18;
                                    uuid23 = uuid17;
                                    simpleValue5 = s22;
                                    simpleValue6 = s21;
                                    equalsIgnoreCase2 = isIndeterminate;
                                }
                                else if (innerText.equalsIgnoreCase("inventory-root")) {
                                    inventoryRootValue = this.getInventoryRootValue(xmlPullParser);
                                    simpleValue = loginURL;
                                    simpleValue2 = s25;
                                    equalsIgnoreCase = success;
                                    simpleValue3 = s24;
                                    intValue = simPort;
                                    simpleValue4 = s23;
                                    intValue2 = circuitCode;
                                    uuid21 = uuid19;
                                    uuid22 = uuid18;
                                    uuid23 = uuid17;
                                    simpleValue5 = s22;
                                    simpleValue6 = s21;
                                    equalsIgnoreCase2 = isIndeterminate;
                                }
                                else {
                                    inventoryRootValue = uuid20;
                                    simpleValue = loginURL;
                                    simpleValue2 = s25;
                                    equalsIgnoreCase = success;
                                    simpleValue3 = s24;
                                    intValue = simPort;
                                    simpleValue4 = s23;
                                    intValue2 = circuitCode;
                                    uuid21 = uuid19;
                                    uuid22 = uuid18;
                                    uuid23 = uuid17;
                                    simpleValue5 = s22;
                                    simpleValue6 = s21;
                                    equalsIgnoreCase2 = isIndeterminate;
                                    if (innerText.equalsIgnoreCase("buddy-list")) {
                                        o = this.parseBuddyList(xmlPullParser);
                                        inventoryRootValue = uuid20;
                                        simpleValue = loginURL;
                                        simpleValue2 = s25;
                                        equalsIgnoreCase = success;
                                        simpleValue3 = s24;
                                        intValue = simPort;
                                        simpleValue4 = s23;
                                        intValue2 = circuitCode;
                                        uuid21 = uuid19;
                                        uuid22 = uuid18;
                                        uuid23 = uuid17;
                                        simpleValue5 = s22;
                                        simpleValue6 = s21;
                                        equalsIgnoreCase2 = isIndeterminate;
                                    }
                                }
                                this.finishTag(xmlPullParser);
                                o3 = o;
                            }
                            this.finishTag(xmlPullParser);
                            o = o3;
                            uuid20 = inventoryRootValue;
                            loginURL = simpleValue;
                            s25 = simpleValue2;
                            success = equalsIgnoreCase;
                            s24 = simpleValue3;
                            simPort = intValue;
                            s23 = simpleValue4;
                            circuitCode = intValue2;
                            uuid19 = uuid21;
                            uuid18 = uuid22;
                            uuid17 = uuid23;
                            s22 = simpleValue5;
                            s21 = simpleValue6;
                            isIndeterminate = equalsIgnoreCase2;
                        }
                        this.finishTag(xmlPullParser);
                        nextURL = s21;
                        nextMethod = s22;
                        uuid = uuid17;
                        uuid5 = uuid18;
                        agentID = uuid19;
                        s9 = s23;
                        seedCapability = s24;
                        message = s25;
                        s17 = loginURL;
                        inventoryRoot = uuid20;
                        o2 = o;
                    }
                    this.finishTag(xmlPullParser);
                    sessionID = uuid;
                    secureSessionID = uuid5;
                    simAddress = s9;
                    agentAppearanceService = s17;
                }
                this.finishTag(xmlPullParser);
            }
            this.finishTag(xmlPullParser);
        }
        this.sessionID = sessionID;
        this.secureSessionID = secureSessionID;
        this.agentID = agentID;
        this.circuitCode = circuitCode;
        this.simAddress = simAddress;
        this.simPort = simPort;
        this.seedCapability = seedCapability;
        this.success = success;
        this.message = message;
        this.agentAppearanceService = agentAppearanceService;
        this.inventoryRoot = inventoryRoot;
        this.friends = ImmutableList.copyOf((Collection<? extends Friend>)o2);
        this.fromTeleport = false;
        this.isTemporary = false;
        this.isIndeterminate = isIndeterminate;
        this.nextURL = nextURL;
        this.nextMethod = nextMethod;
    }
    
    private void finishTag(final XmlPullParser xmlPullParser) throws XmlPullParserException, IOException {
        while (xmlPullParser.getEventType() != 1) {
            if (xmlPullParser.getEventType() == 3) {
                xmlPullParser.next();
                break;
            }
            if (xmlPullParser.getEventType() == 2) {
                this.skipTag(xmlPullParser);
            }
            else {
                xmlPullParser.next();
            }
        }
    }
    
    private String getInnerText(final XmlPullParser xmlPullParser) throws XmlPullParserException, IOException {
        String s;
        if (xmlPullParser.getEventType() == 4) {
            final String text = xmlPullParser.getText();
            xmlPullParser.next();
            s = text;
        }
        else {
            s = "";
        }
        return s;
    }
    
    private UUID getInventoryRootValue(final XmlPullParser xmlPullParser) throws XmlPullParserException, IOException {
        final UUID uuid = null;
        UUID fromString = null;
        final UUID uuid2 = null;
        if (this.skipUntilTag(xmlPullParser, "array")) {
            fromString = uuid;
            if (this.skipUntilTag(xmlPullParser, "data")) {
                fromString = uuid2;
                while (this.skipUntilTag(xmlPullParser, "value")) {
                    UUID uuid3 = fromString;
                    if (this.skipUntilTag(xmlPullParser, "struct")) {
                        while (this.skipUntilTag(xmlPullParser, "member")) {
                            UUID uuid4 = fromString;
                            if (this.skipUntilTag(xmlPullParser, "name")) {
                                final String innerText = this.getInnerText(xmlPullParser);
                                this.finishTag(xmlPullParser);
                                uuid4 = fromString;
                                if (this.skipUntilTag(xmlPullParser, "value")) {
                                    if (innerText.equalsIgnoreCase("folder_id")) {
                                        fromString = UUID.fromString(this.getSimpleValue(xmlPullParser));
                                    }
                                    this.finishTag(xmlPullParser);
                                    uuid4 = fromString;
                                }
                            }
                            this.finishTag(xmlPullParser);
                            fromString = uuid4;
                        }
                        this.finishTag(xmlPullParser);
                        uuid3 = fromString;
                    }
                    this.finishTag(xmlPullParser);
                    fromString = uuid3;
                }
                this.finishTag(xmlPullParser);
            }
            this.finishTag(xmlPullParser);
        }
        return fromString;
    }
    
    private String getSimpleValue(final XmlPullParser xmlPullParser) throws XmlPullParserException, IOException {
        while (xmlPullParser.getEventType() == 4) {
            xmlPullParser.next();
        }
        final String nextText = xmlPullParser.nextText();
        xmlPullParser.nextTag();
        Debug.Printf("got value '%s'", nextText);
        return nextText;
    }
    
    private List<Friend> parseBuddyList(final XmlPullParser xmlPullParser) throws XmlPullParserException, IOException {
        final LinkedList list = new LinkedList();
        if (this.skipUntilTag(xmlPullParser, "array")) {
            if (this.skipUntilTag(xmlPullParser, "data")) {
                while (this.skipUntilTag(xmlPullParser, "value")) {
                    if (this.skipUntilTag(xmlPullParser, "struct")) {
                        int int1 = 0;
                        int n = 0;
                        UUID uuid = null;
                        while (this.skipUntilTag(xmlPullParser, "member")) {
                            int n2 = int1;
                            int int2 = n;
                            UUID uuid2 = uuid;
                            if (this.skipUntilTag(xmlPullParser, "name")) {
                                final String innerText = this.getInnerText(xmlPullParser);
                                this.finishTag(xmlPullParser);
                                n2 = int1;
                                int2 = n;
                                uuid2 = uuid;
                                if (this.skipUntilTag(xmlPullParser, "value")) {
                                    if (innerText.equalsIgnoreCase("buddy_id")) {
                                        uuid2 = UUIDPool.getUUID(this.getSimpleValue(xmlPullParser));
                                        int2 = n;
                                    }
                                    else if (innerText.equalsIgnoreCase("buddy_rights_given")) {
                                        int2 = Integer.parseInt(this.getSimpleValue(xmlPullParser));
                                        uuid2 = uuid;
                                    }
                                    else {
                                        int2 = n;
                                        uuid2 = uuid;
                                        if (innerText.equalsIgnoreCase("buddy_rights_has")) {
                                            int1 = Integer.parseInt(this.getSimpleValue(xmlPullParser));
                                            int2 = n;
                                            uuid2 = uuid;
                                        }
                                    }
                                    this.finishTag(xmlPullParser);
                                    n2 = int1;
                                }
                            }
                            this.finishTag(xmlPullParser);
                            int1 = n2;
                            n = int2;
                            uuid = uuid2;
                        }
                        if (uuid != null) {
                            list.add(new Friend(uuid, n, int1));
                        }
                        this.finishTag(xmlPullParser);
                    }
                    this.finishTag(xmlPullParser);
                }
                this.finishTag(xmlPullParser);
            }
            this.finishTag(xmlPullParser);
        }
        return list;
    }
    
    private void skipTag(final XmlPullParser xmlPullParser) throws XmlPullParserException, IOException {
        int n = 0;
        while (true) {
            switch (xmlPullParser.next()) {
                default: {
                    continue;
                }
                case 1: {
                    return;
                }
                case 2: {
                    ++n;
                    continue;
                }
                case 3: {
                    if (n == 0) {
                        xmlPullParser.nextTag();
                        return;
                    }
                    --n;
                    continue;
                }
            }
        }
    }
    
    private boolean skipUntilTag(final XmlPullParser xmlPullParser, final String anotherString) throws XmlPullParserException, IOException {
        while (xmlPullParser.getEventType() != 3 && xmlPullParser.getEventType() != 1) {
            if (xmlPullParser.getEventType() == 4) {
                xmlPullParser.next();
            }
            else {
                if (xmlPullParser.getEventType() == 2 && xmlPullParser.getName().equalsIgnoreCase(anotherString)) {
                    xmlPullParser.next();
                    return true;
                }
                this.skipTag(xmlPullParser);
            }
        }
        return false;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof SLAuthReply)) {
            return false;
        }
        final SLAuthReply slAuthReply = (SLAuthReply)o;
        return this.simAddress.equals(slAuthReply.simAddress) && this.simPort == slAuthReply.simPort && this.agentID.equals(slAuthReply.agentID) && this.sessionID.equals(slAuthReply.sessionID) && this.circuitCode == slAuthReply.circuitCode;
    }
    
    @Override
    public int hashCode() {
        return this.simAddress.hashCode() + 0 + this.simPort + this.agentID.hashCode() + this.sessionID.hashCode() + this.circuitCode;
    }
    
    public static class Friend
    {
        public final int rightsGiven;
        public final int rightsHas;
        @Nonnull
        public final UUID uuid;
        
        public Friend(@Nonnull final UUID uuid, final int rightsGiven, final int rightsHas) {
            this.uuid = uuid;
            this.rightsGiven = rightsGiven;
            this.rightsHas = rightsHas;
        }
    }
}
