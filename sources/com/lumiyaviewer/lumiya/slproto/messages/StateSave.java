// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class StateSave extends SLMessage
{
    public static class AgentData
    {

        public UUID AgentID;
        public UUID SessionID;

        public AgentData()
        {
        }
    }

    public static class DataBlock
    {

        public byte Filename[];

        public DataBlock()
        {
        }
    }


    public AgentData AgentData_Field;
    public DataBlock DataBlock_Field;

    public StateSave()
    {
        zeroCoded = false;
        AgentData_Field = new AgentData();
        DataBlock_Field = new DataBlock();
    }

    public int CalcPayloadSize()
    {
        return DataBlock_Field.Filename.length + 1 + 36;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleStateSave(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)127);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packUUID(bytebuffer, AgentData_Field.SessionID);
        packVariable(bytebuffer, DataBlock_Field.Filename, 1);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        AgentData_Field.AgentID = unpackUUID(bytebuffer);
        AgentData_Field.SessionID = unpackUUID(bytebuffer);
        DataBlock_Field.Filename = unpackVariable(bytebuffer, 1);
    }
}
