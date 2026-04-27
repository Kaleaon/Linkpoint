// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class RequestGodlikePowers extends SLMessage
{
    public static class AgentData
    {

        public UUID AgentID;
        public UUID SessionID;

        public AgentData()
        {
        }
    }

    public static class RequestBlock
    {

        public boolean Godlike;
        public UUID Token;

        public RequestBlock()
        {
        }
    }


    public AgentData AgentData_Field;
    public RequestBlock RequestBlock_Field;

    public RequestGodlikePowers()
    {
        zeroCoded = false;
        AgentData_Field = new AgentData();
        RequestBlock_Field = new RequestBlock();
    }

    public int CalcPayloadSize()
    {
        return 53;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleRequestGodlikePowers(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)1);
        bytebuffer.put((byte)1);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packUUID(bytebuffer, AgentData_Field.SessionID);
        packBoolean(bytebuffer, RequestBlock_Field.Godlike);
        packUUID(bytebuffer, RequestBlock_Field.Token);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        AgentData_Field.AgentID = unpackUUID(bytebuffer);
        AgentData_Field.SessionID = unpackUUID(bytebuffer);
        RequestBlock_Field.Godlike = unpackBoolean(bytebuffer);
        RequestBlock_Field.Token = unpackUUID(bytebuffer);
    }
}
