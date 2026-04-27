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

public class SystemKickUser extends SLMessage
{
    public static class AgentInfo
    {

        public UUID AgentID;

        public AgentInfo()
        {
        }
    }


    public ArrayList AgentInfo_Fields;

    public SystemKickUser()
    {
        AgentInfo_Fields = new ArrayList();
        zeroCoded = false;
    }

    public int CalcPayloadSize()
    {
        return AgentInfo_Fields.size() * 16 + 5;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleSystemKickUser(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)-90);
        bytebuffer.put((byte)AgentInfo_Fields.size());
        for (Iterator iterator = AgentInfo_Fields.iterator(); iterator.hasNext(); packUUID(bytebuffer, ((AgentInfo)iterator.next()).AgentID)) { }
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        byte byte0 = bytebuffer.get();
        for (int i = 0; i < (byte0 & 0xff); i++)
        {
            AgentInfo agentinfo = new AgentInfo();
            agentinfo.AgentID = unpackUUID(bytebuffer);
            AgentInfo_Fields.add(agentinfo);
        }

    }
}
