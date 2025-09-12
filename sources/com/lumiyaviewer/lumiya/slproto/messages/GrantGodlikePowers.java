// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class GrantGodlikePowers extends SLMessage
{
    public static class AgentData
    {

        public UUID AgentID;
        public UUID SessionID;

        public AgentData()
        {
        }
    }

    public static class GrantData
    {

        public int GodLevel;
        public UUID Token;

        public GrantData()
        {
        }
    }


    public AgentData AgentData_Field;
    public GrantData GrantData_Field;

    public GrantGodlikePowers()
    {
        zeroCoded = false;
        AgentData_Field = new AgentData();
        GrantData_Field = new GrantData();
    }

    public int CalcPayloadSize()
    {
        return 53;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleGrantGodlikePowers(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)1);
        bytebuffer.put((byte)2);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packUUID(bytebuffer, AgentData_Field.SessionID);
        packByte(bytebuffer, (byte)GrantData_Field.GodLevel);
        packUUID(bytebuffer, GrantData_Field.Token);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        AgentData_Field.AgentID = unpackUUID(bytebuffer);
        AgentData_Field.SessionID = unpackUUID(bytebuffer);
        GrantData_Field.GodLevel = unpackByte(bytebuffer) & 0xff;
        GrantData_Field.Token = unpackUUID(bytebuffer);
    }
}
