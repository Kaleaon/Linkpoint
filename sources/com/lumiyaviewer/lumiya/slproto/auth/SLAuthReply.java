// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.auth;

import com.google.common.collect.ImmutableList;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.utils.UUIDPool;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public final class SLAuthReply
{
    public static class Friend
    {

        public final int rightsGiven;
        public final int rightsHas;
        public final UUID uuid;

        public Friend(UUID uuid1, int i, int j)
        {
            uuid = uuid1;
            rightsGiven = i;
            rightsHas = j;
        }
    }


    public final String agentAppearanceService;
    public final UUID agentID;
    public final int circuitCode;
    public final ImmutableList friends;
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

    public SLAuthReply(SLAuthReply slauthreply, boolean flag, boolean flag1, UUID uuid, String s, int i, String s1)
    {
        gridName = slauthreply.gridName;
        loginURL = slauthreply.loginURL;
        sessionID = slauthreply.sessionID;
        secureSessionID = slauthreply.secureSessionID;
        if (uuid == null)
        {
            uuid = slauthreply.agentID;
        }
        agentID = uuid;
        circuitCode = slauthreply.circuitCode;
        simAddress = s;
        simPort = i;
        seedCapability = s1;
        success = slauthreply.success;
        message = slauthreply.message;
        agentAppearanceService = slauthreply.agentAppearanceService;
        inventoryRoot = slauthreply.inventoryRoot;
        friends = slauthreply.friends;
        isIndeterminate = slauthreply.isIndeterminate;
        nextMethod = slauthreply.nextMethod;
        nextURL = slauthreply.nextURL;
        fromTeleport = flag;
        isTemporary = flag1;
    }

    public SLAuthReply(String s, String s1, XmlPullParser xmlpullparser)
        throws XmlPullParserException, IOException
    {
        Object obj;
        String s2;
        Object obj1;
        String s3;
        Object obj2;
        Object obj3;
        Object obj4;
        Object obj5;
        Object obj6;
        Object obj7;
        Object obj8;
        String s4;
        Object obj9;
        String s5;
        Object obj10;
        Object obj11;
        Object obj12;
        Object obj13;
        Object obj14;
        String s6;
        String s7;
        Object obj15;
        Object obj16;
        Object obj17;
        Object obj18;
        Object obj19;
        Object obj20;
        Object obj21;
        Object obj22;
        Object obj23;
        Object obj24;
        Object obj25;
        Object obj26;
        Object obj27;
        Object obj28;
        Object obj29;
        Object obj30;
        Object obj31;
        Object obj32;
        Object obj33;
        Object obj34;
        Object obj35;
        Object obj36;
        Object obj37;
        Object obj38;
        Object obj39;
        Object obj40;
        int i;
        int j;
        int k;
        int l;
        boolean flag;
        boolean flag1;
        boolean flag2;
        boolean flag3;
        boolean flag4;
        boolean flag5;
        boolean flag6;
        boolean flag7;
        boolean flag8;
        boolean flag9;
        boolean flag10;
        boolean flag11;
        boolean flag12;
        boolean flag13;
        boolean flag14;
        boolean flag15;
        gridName = s;
        loginURL = s1;
        flag10 = false;
        flag12 = false;
        flag14 = false;
        flag7 = false;
        flag9 = false;
        s7 = null;
        obj23 = null;
        obj32 = null;
        obj8 = null;
        s6 = null;
        obj15 = null;
        obj24 = null;
        obj33 = null;
        obj7 = null;
        obj14 = null;
        obj16 = null;
        obj25 = null;
        obj34 = null;
        obj6 = null;
        obj13 = null;
        obj17 = null;
        obj26 = null;
        obj35 = null;
        obj5 = null;
        obj12 = null;
        obj18 = null;
        obj27 = null;
        obj36 = null;
        obj4 = null;
        obj11 = null;
        flag = false;
        flag2 = false;
        flag4 = false;
        j = 0;
        l = 0;
        obj19 = null;
        obj28 = null;
        obj37 = null;
        obj3 = null;
        obj10 = null;
        flag1 = false;
        flag3 = false;
        flag5 = false;
        i = 0;
        k = 0;
        obj20 = null;
        obj29 = null;
        obj38 = null;
        obj2 = null;
        s5 = null;
        flag11 = false;
        flag13 = false;
        flag15 = false;
        flag6 = false;
        flag8 = false;
        s1 = "";
        obj21 = null;
        obj30 = null;
        obj39 = null;
        obj1 = null;
        obj9 = null;
        obj22 = null;
        obj31 = null;
        obj40 = null;
        s2 = null;
        s4 = null;
        s = ImmutableList.of();
        xmlpullparser.nextTag();
        xmlpullparser.require(2, null, "methodResponse");
        xmlpullparser.nextTag();
        obj = s;
        s3 = s1;
        if (!skipUntilTag(xmlpullparser, "params")) goto _L2; else goto _L1
_L1:
        obj = s;
        s2 = obj40;
        obj1 = obj39;
        s3 = s1;
        flag6 = flag15;
        obj2 = obj38;
        i = ((flag5) ? 1 : 0);
        obj3 = obj37;
        j = ((flag4) ? 1 : 0);
        obj4 = obj36;
        obj5 = obj35;
        obj6 = obj34;
        obj7 = obj33;
        obj8 = obj32;
        flag7 = flag14;
        if (!skipUntilTag(xmlpullparser, "param")) goto _L4; else goto _L3
_L3:
        obj = s;
        s2 = obj31;
        obj1 = obj30;
        s3 = s1;
        flag6 = flag13;
        obj2 = obj29;
        i = ((flag3) ? 1 : 0);
        obj3 = obj28;
        j = ((flag2) ? 1 : 0);
        obj4 = obj27;
        obj5 = obj26;
        obj6 = obj25;
        obj7 = obj24;
        obj8 = obj23;
        flag7 = flag12;
        if (!skipUntilTag(xmlpullparser, "value")) goto _L6; else goto _L5
_L5:
        obj = s;
        s2 = obj22;
        obj1 = obj21;
        s3 = s1;
        flag6 = flag11;
        obj2 = obj20;
        i = ((flag1) ? 1 : 0);
        obj3 = obj19;
        j = ((flag) ? 1 : 0);
        obj4 = obj18;
        obj5 = obj17;
        obj6 = obj16;
        obj7 = obj15;
        obj8 = s7;
        flag7 = flag10;
        if (!skipUntilTag(xmlpullparser, "struct")) goto _L8; else goto _L7
_L7:
        flag7 = flag9;
        obj6 = s6;
        obj5 = obj14;
        obj4 = obj13;
        obj3 = obj12;
        obj2 = obj11;
        j = l;
        s3 = ((String) (obj10));
        i = k;
        obj1 = s5;
        flag6 = flag8;
        s2 = s1;
        obj = obj9;
        s1 = s4;
_L19:
        if (!skipUntilTag(xmlpullparser, "member")) goto _L10; else goto _L9
_L9:
        if (!skipUntilTag(xmlpullparser, "name")) goto _L12; else goto _L11
_L11:
        s7 = getInnerText(xmlpullparser);
        finishTag(xmlpullparser);
        s6 = s;
        obj7 = s1;
        obj8 = obj;
        s4 = s2;
        flag8 = flag6;
        obj9 = obj1;
        k = i;
        s5 = s3;
        l = j;
        obj10 = obj2;
        obj11 = obj3;
        obj12 = obj4;
        obj13 = obj5;
        obj14 = obj6;
        flag9 = flag7;
        if (!skipUntilTag(xmlpullparser, "value")) goto _L14; else goto _L13
_L13:
        if (!s7.equalsIgnoreCase("session_id")) goto _L16; else goto _L15
_L15:
        obj12 = UUIDPool.getUUID(getSimpleValue(xmlpullparser));
        flag9 = flag7;
        obj14 = obj6;
        obj13 = obj5;
        obj11 = obj3;
        obj10 = obj2;
        l = j;
        s5 = s3;
        k = i;
        obj9 = obj1;
        flag8 = flag6;
        s4 = s2;
        obj8 = obj;
        obj7 = s1;
_L17:
        finishTag(xmlpullparser);
        s6 = s;
_L14:
        finishTag(xmlpullparser);
        s = s6;
        s1 = ((String) (obj7));
        obj = obj8;
        s2 = s4;
        flag6 = flag8;
        obj1 = obj9;
        i = k;
        s3 = s5;
        j = l;
        obj2 = obj10;
        obj3 = obj11;
        obj4 = obj12;
        obj5 = obj13;
        obj6 = obj14;
        flag7 = flag9;
        continue; /* Loop/switch isn't completed */
_L12:
        throw new XmlPullParserException("Not found name", xmlpullparser, null);
_L16:
        if (s7.equalsIgnoreCase("secure_session_id"))
        {
            obj11 = UUIDPool.getUUID(getSimpleValue(xmlpullparser));
            obj7 = s1;
            obj8 = obj;
            s4 = s2;
            flag8 = flag6;
            obj9 = obj1;
            k = i;
            s5 = s3;
            l = j;
            obj10 = obj2;
            obj12 = obj4;
            obj13 = obj5;
            obj14 = obj6;
            flag9 = flag7;
        } else
        if (s7.equalsIgnoreCase("agent_id"))
        {
            obj10 = UUIDPool.getUUID(getSimpleValue(xmlpullparser));
            obj7 = s1;
            obj8 = obj;
            s4 = s2;
            flag8 = flag6;
            obj9 = obj1;
            k = i;
            s5 = s3;
            l = j;
            obj11 = obj3;
            obj12 = obj4;
            obj13 = obj5;
            obj14 = obj6;
            flag9 = flag7;
        } else
        if (s7.equalsIgnoreCase("circuit_code"))
        {
            l = Integer.decode(getSimpleValue(xmlpullparser)).intValue();
            obj7 = s1;
            obj8 = obj;
            s4 = s2;
            flag8 = flag6;
            obj9 = obj1;
            k = i;
            s5 = s3;
            obj10 = obj2;
            obj11 = obj3;
            obj12 = obj4;
            obj13 = obj5;
            obj14 = obj6;
            flag9 = flag7;
        } else
        if (s7.equalsIgnoreCase("sim_ip"))
        {
            s5 = getSimpleValue(xmlpullparser);
            obj7 = s1;
            obj8 = obj;
            s4 = s2;
            flag8 = flag6;
            obj9 = obj1;
            k = i;
            l = j;
            obj10 = obj2;
            obj11 = obj3;
            obj12 = obj4;
            obj13 = obj5;
            obj14 = obj6;
            flag9 = flag7;
        } else
        if (s7.equalsIgnoreCase("sim_port"))
        {
            k = Integer.decode(getSimpleValue(xmlpullparser)).intValue();
            obj7 = s1;
            obj8 = obj;
            s4 = s2;
            flag8 = flag6;
            obj9 = obj1;
            s5 = s3;
            l = j;
            obj10 = obj2;
            obj11 = obj3;
            obj12 = obj4;
            obj13 = obj5;
            obj14 = obj6;
            flag9 = flag7;
        } else
        if (s7.equalsIgnoreCase("seed_capability"))
        {
            obj9 = getSimpleValue(xmlpullparser);
            obj7 = s1;
            obj8 = obj;
            s4 = s2;
            flag8 = flag6;
            k = i;
            s5 = s3;
            l = j;
            obj10 = obj2;
            obj11 = obj3;
            obj12 = obj4;
            obj13 = obj5;
            obj14 = obj6;
            flag9 = flag7;
        } else
        if (s7.equalsIgnoreCase("login"))
        {
            obj7 = getSimpleValue(xmlpullparser);
            flag8 = ((String) (obj7)).equalsIgnoreCase("true");
            flag9 = ((String) (obj7)).equalsIgnoreCase("indeterminate");
            obj7 = s1;
            obj8 = obj;
            s4 = s2;
            obj9 = obj1;
            k = i;
            s5 = s3;
            l = j;
            obj10 = obj2;
            obj11 = obj3;
            obj12 = obj4;
            obj13 = obj5;
            obj14 = obj6;
        } else
        if (s7.equalsIgnoreCase("next_url"))
        {
            obj14 = getSimpleValue(xmlpullparser);
            obj7 = s1;
            obj8 = obj;
            s4 = s2;
            flag8 = flag6;
            obj9 = obj1;
            k = i;
            s5 = s3;
            l = j;
            obj10 = obj2;
            obj11 = obj3;
            obj12 = obj4;
            obj13 = obj5;
            flag9 = flag7;
        } else
        if (s7.equalsIgnoreCase("next_method"))
        {
            obj13 = getSimpleValue(xmlpullparser);
            obj7 = s1;
            obj8 = obj;
            s4 = s2;
            flag8 = flag6;
            obj9 = obj1;
            k = i;
            s5 = s3;
            l = j;
            obj10 = obj2;
            obj11 = obj3;
            obj12 = obj4;
            obj14 = obj6;
            flag9 = flag7;
        } else
        if (s7.equalsIgnoreCase("message"))
        {
            s4 = getSimpleValue(xmlpullparser);
            obj7 = s1;
            obj8 = obj;
            flag8 = flag6;
            obj9 = obj1;
            k = i;
            s5 = s3;
            l = j;
            obj10 = obj2;
            obj11 = obj3;
            obj12 = obj4;
            obj13 = obj5;
            obj14 = obj6;
            flag9 = flag7;
        } else
        if (s7.equalsIgnoreCase("agent_appearance_service"))
        {
            obj8 = getSimpleValue(xmlpullparser);
            obj7 = s1;
            s4 = s2;
            flag8 = flag6;
            obj9 = obj1;
            k = i;
            s5 = s3;
            l = j;
            obj10 = obj2;
            obj11 = obj3;
            obj12 = obj4;
            obj13 = obj5;
            obj14 = obj6;
            flag9 = flag7;
        } else
        if (s7.equalsIgnoreCase("inventory-root"))
        {
            obj7 = getInventoryRootValue(xmlpullparser);
            obj8 = obj;
            s4 = s2;
            flag8 = flag6;
            obj9 = obj1;
            k = i;
            s5 = s3;
            l = j;
            obj10 = obj2;
            obj11 = obj3;
            obj12 = obj4;
            obj13 = obj5;
            obj14 = obj6;
            flag9 = flag7;
        } else
        {
            obj7 = s1;
            obj8 = obj;
            s4 = s2;
            flag8 = flag6;
            obj9 = obj1;
            k = i;
            s5 = s3;
            l = j;
            obj10 = obj2;
            obj11 = obj3;
            obj12 = obj4;
            obj13 = obj5;
            obj14 = obj6;
            flag9 = flag7;
            if (s7.equalsIgnoreCase("buddy-list"))
            {
                s = parseBuddyList(xmlpullparser);
                obj7 = s1;
                obj8 = obj;
                s4 = s2;
                flag8 = flag6;
                obj9 = obj1;
                k = i;
                s5 = s3;
                l = j;
                obj10 = obj2;
                obj11 = obj3;
                obj12 = obj4;
                obj13 = obj5;
                obj14 = obj6;
                flag9 = flag7;
            }
        }
        if (true) goto _L17; else goto _L10
_L10:
        finishTag(xmlpullparser);
        obj8 = obj6;
        obj7 = obj5;
        obj6 = obj4;
        obj5 = obj3;
        obj4 = obj2;
        obj3 = s3;
        obj2 = obj1;
        s3 = s2;
        obj1 = obj;
        s2 = s1;
        obj = s;
_L8:
        finishTag(xmlpullparser);
_L6:
        finishTag(xmlpullparser);
_L4:
        finishTag(xmlpullparser);
_L2:
        sessionID = ((UUID) (obj6));
        secureSessionID = ((UUID) (obj5));
        agentID = ((UUID) (obj4));
        circuitCode = j;
        simAddress = ((String) (obj3));
        simPort = i;
        seedCapability = ((String) (obj2));
        success = flag6;
        message = s3;
        agentAppearanceService = ((String) (obj1));
        inventoryRoot = s2;
        friends = ImmutableList.copyOf(((java.util.Collection) (obj)));
        fromTeleport = false;
        isTemporary = false;
        isIndeterminate = flag7;
        nextURL = ((String) (obj8));
        nextMethod = ((String) (obj7));
        return;
        if (true) goto _L19; else goto _L18
_L18:
    }

    private void finishTag(XmlPullParser xmlpullparser)
        throws XmlPullParserException, IOException
    {
        do
        {
label0:
            {
                if (xmlpullparser.getEventType() != 1)
                {
                    if (xmlpullparser.getEventType() != 3)
                    {
                        break label0;
                    }
                    xmlpullparser.next();
                }
                return;
            }
            if (xmlpullparser.getEventType() == 2)
            {
                skipTag(xmlpullparser);
            } else
            {
                xmlpullparser.next();
            }
        } while (true);
    }

    private String getInnerText(XmlPullParser xmlpullparser)
        throws XmlPullParserException, IOException
    {
        if (xmlpullparser.getEventType() == 4)
        {
            String s = xmlpullparser.getText();
            xmlpullparser.next();
            return s;
        } else
        {
            return "";
        }
    }

    private UUID getInventoryRootValue(XmlPullParser xmlpullparser)
        throws XmlPullParserException, IOException
    {
        Object obj = null;
        UUID uuid = null;
        UUID uuid1 = null;
        if (skipUntilTag(xmlpullparser, "array"))
        {
            uuid = obj;
            if (skipUntilTag(xmlpullparser, "data"))
            {
                for (uuid = uuid1; skipUntilTag(xmlpullparser, "value"); uuid = uuid1)
                {
                    uuid1 = uuid;
                    if (skipUntilTag(xmlpullparser, "struct"))
                    {
                        while (skipUntilTag(xmlpullparser, "member")) 
                        {
                            uuid1 = uuid;
                            if (skipUntilTag(xmlpullparser, "name"))
                            {
                                String s = getInnerText(xmlpullparser);
                                finishTag(xmlpullparser);
                                uuid1 = uuid;
                                if (skipUntilTag(xmlpullparser, "value"))
                                {
                                    if (s.equalsIgnoreCase("folder_id"))
                                    {
                                        uuid = UUID.fromString(getSimpleValue(xmlpullparser));
                                    }
                                    finishTag(xmlpullparser);
                                    uuid1 = uuid;
                                }
                            }
                            finishTag(xmlpullparser);
                            uuid = uuid1;
                        }
                        finishTag(xmlpullparser);
                        uuid1 = uuid;
                    }
                    finishTag(xmlpullparser);
                }

                finishTag(xmlpullparser);
            }
            finishTag(xmlpullparser);
        }
        return uuid;
    }

    private String getSimpleValue(XmlPullParser xmlpullparser)
        throws XmlPullParserException, IOException
    {
        for (; xmlpullparser.getEventType() == 4; xmlpullparser.next()) { }
        String s = xmlpullparser.nextText();
        xmlpullparser.nextTag();
        Debug.Printf("got value '%s'", new Object[] {
            s
        });
        return s;
    }

    private List parseBuddyList(XmlPullParser xmlpullparser)
        throws XmlPullParserException, IOException
    {
        LinkedList linkedlist = new LinkedList();
        if (skipUntilTag(xmlpullparser, "array"))
        {
            if (skipUntilTag(xmlpullparser, "data"))
            {
                for (; skipUntilTag(xmlpullparser, "value"); finishTag(xmlpullparser))
                {
                    if (!skipUntilTag(xmlpullparser, "struct"))
                    {
                        continue;
                    }
                    int j = 0;
                    int i = 0;
                    UUID uuid = null;
                    while (skipUntilTag(xmlpullparser, "member")) 
                    {
                        int l = j;
                        int k = i;
                        UUID uuid1 = uuid;
                        if (skipUntilTag(xmlpullparser, "name"))
                        {
                            String s = getInnerText(xmlpullparser);
                            finishTag(xmlpullparser);
                            l = j;
                            k = i;
                            uuid1 = uuid;
                            if (skipUntilTag(xmlpullparser, "value"))
                            {
                                if (s.equalsIgnoreCase("buddy_id"))
                                {
                                    uuid1 = UUIDPool.getUUID(getSimpleValue(xmlpullparser));
                                    k = i;
                                } else
                                if (s.equalsIgnoreCase("buddy_rights_given"))
                                {
                                    k = Integer.parseInt(getSimpleValue(xmlpullparser));
                                    uuid1 = uuid;
                                } else
                                {
                                    k = i;
                                    uuid1 = uuid;
                                    if (s.equalsIgnoreCase("buddy_rights_has"))
                                    {
                                        j = Integer.parseInt(getSimpleValue(xmlpullparser));
                                        k = i;
                                        uuid1 = uuid;
                                    }
                                }
                                finishTag(xmlpullparser);
                                l = j;
                            }
                        }
                        finishTag(xmlpullparser);
                        j = l;
                        i = k;
                        uuid = uuid1;
                    }
                    if (uuid != null)
                    {
                        linkedlist.add(new Friend(uuid, i, j));
                    }
                    finishTag(xmlpullparser);
                }

                finishTag(xmlpullparser);
            }
            finishTag(xmlpullparser);
        }
        return linkedlist;
    }

    private void skipTag(XmlPullParser xmlpullparser)
        throws XmlPullParserException, IOException
    {
        int i = 0;
        do
        {
            switch (xmlpullparser.next())
            {
            case 1: // '\001'
                return;

            case 2: // '\002'
                i++;
                break;

            case 3: // '\003'
                if (i == 0)
                {
                    xmlpullparser.nextTag();
                    return;
                }
                i--;
                break;
            }
        } while (true);
    }

    private boolean skipUntilTag(XmlPullParser xmlpullparser, String s)
        throws XmlPullParserException, IOException
    {
        while (xmlpullparser.getEventType() != 3 && xmlpullparser.getEventType() != 1) 
        {
            if (xmlpullparser.getEventType() == 4)
            {
                xmlpullparser.next();
            } else
            {
                if (xmlpullparser.getEventType() == 2 && xmlpullparser.getName().equalsIgnoreCase(s))
                {
                    xmlpullparser.next();
                    return true;
                }
                skipTag(xmlpullparser);
            }
        }
        return false;
    }

    public boolean equals(Object obj)
    {
        if (obj == this)
        {
            return true;
        }
        if (!(obj instanceof SLAuthReply))
        {
            return false;
        }
        obj = (SLAuthReply)obj;
        if (!simAddress.equals(((SLAuthReply) (obj)).simAddress))
        {
            return false;
        }
        if (simPort != ((SLAuthReply) (obj)).simPort)
        {
            return false;
        }
        if (!agentID.equals(((SLAuthReply) (obj)).agentID))
        {
            return false;
        }
        if (!sessionID.equals(((SLAuthReply) (obj)).sessionID))
        {
            return false;
        }
        return circuitCode == ((SLAuthReply) (obj)).circuitCode;
    }

    public int hashCode()
    {
        return simAddress.hashCode() + 0 + simPort + agentID.hashCode() + sessionID.hashCode() + circuitCode;
    }
}
