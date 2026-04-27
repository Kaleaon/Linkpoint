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

public class OfflineNotification extends SLMessage
{
    public static class AgentBlock
    {

        public UUID AgentID;

        public AgentBlock()
        {
        }
    }


    public ArrayList AgentBlock_Fields;

    public OfflineNotification()
    {
        AgentBlock_Fields = new ArrayList();
        zeroCoded = false;
    }

    public int CalcPayloadSize()
    {
        return AgentBlock_Fields.size() * 16 + 5;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleOfflineNotification(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)1);
        bytebuffer.put((byte)67);
        bytebuffer.put((byte)AgentBlock_Fields.size());
        for (Iterator iterator = AgentBlock_Fields.iterator(); iterator.hasNext(); packUUID(bytebuffer, ((AgentBlock)iterator.next()).AgentID)) { }
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        byte byte0 = bytebuffer.get();
        for (int i = 0; i < (byte0 & 0xff); i++)
        {
            AgentBlock agentblock = new AgentBlock();
            agentblock.AgentID = unpackUUID(bytebuffer);
            AgentBlock_Fields.add(agentblock);
        }

    }
}
