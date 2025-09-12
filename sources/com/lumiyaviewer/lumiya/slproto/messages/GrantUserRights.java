// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class GrantUserRights extends SLMessage
{
    public static class AgentData
    {

        public UUID AgentID;
        public UUID SessionID;

        public AgentData()
        {
        }
    }

    public static class Rights
    {

        public UUID AgentRelated;
        public int RelatedRights;

        public Rights()
        {
        }
    }


    public AgentData AgentData_Field;
    public ArrayList Rights_Fields;

    public GrantUserRights()
    {
        Rights_Fields = new ArrayList();
        zeroCoded = false;
        AgentData_Field = new AgentData();
    }

    public int CalcPayloadSize()
    {
        return Rights_Fields.size() * 20 + 37;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleGrantUserRights(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)1);
        bytebuffer.put((byte)64);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packUUID(bytebuffer, AgentData_Field.SessionID);
        bytebuffer.put((byte)Rights_Fields.size());
        Rights rights;
        for (Iterator iterator = Rights_Fields.iterator(); iterator.hasNext(); packInt(bytebuffer, rights.RelatedRights))
        {
            rights = (Rights)iterator.next();
            packUUID(bytebuffer, rights.AgentRelated);
        }

    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        AgentData_Field.AgentID = unpackUUID(bytebuffer);
        AgentData_Field.SessionID = unpackUUID(bytebuffer);
        byte byte0 = bytebuffer.get();
        for (int i = 0; i < (byte0 & 0xff); i++)
        {
            Rights rights = new Rights();
            rights.AgentRelated = unpackUUID(bytebuffer);
            rights.RelatedRights = unpackInt(bytebuffer);
            Rights_Fields.add(rights);
        }

    }
}
