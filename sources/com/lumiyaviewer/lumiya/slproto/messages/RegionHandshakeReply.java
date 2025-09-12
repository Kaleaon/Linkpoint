// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class RegionHandshakeReply extends SLMessage
{
    public static class AgentData
    {

        public UUID AgentID;
        public UUID SessionID;

        public AgentData()
        {
        }
    }

    public static class RegionInfo
    {

        public int Flags;

        public RegionInfo()
        {
        }
    }


    public AgentData AgentData_Field;
    public RegionInfo RegionInfo_Field;

    public RegionHandshakeReply()
    {
        zeroCoded = true;
        AgentData_Field = new AgentData();
        RegionInfo_Field = new RegionInfo();
    }

    public int CalcPayloadSize()
    {
        return 40;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleRegionHandshakeReply(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)-107);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packUUID(bytebuffer, AgentData_Field.SessionID);
        packInt(bytebuffer, RegionInfo_Field.Flags);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        AgentData_Field.AgentID = unpackUUID(bytebuffer);
        AgentData_Field.SessionID = unpackUUID(bytebuffer);
        RegionInfo_Field.Flags = unpackInt(bytebuffer);
    }
}
