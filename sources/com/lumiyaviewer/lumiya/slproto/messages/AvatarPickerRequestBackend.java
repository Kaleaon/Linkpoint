// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class AvatarPickerRequestBackend extends SLMessage
{
    public static class AgentData
    {

        public UUID AgentID;
        public int GodLevel;
        public UUID QueryID;
        public UUID SessionID;

        public AgentData()
        {
        }
    }

    public static class Data
    {

        public byte Name[];

        public Data()
        {
        }
    }


    public AgentData AgentData_Field;
    public Data Data_Field;

    public AvatarPickerRequestBackend()
    {
        zeroCoded = false;
        AgentData_Field = new AgentData();
        Data_Field = new Data();
    }

    public int CalcPayloadSize()
    {
        return Data_Field.Name.length + 1 + 53;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleAvatarPickerRequestBackend(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)27);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packUUID(bytebuffer, AgentData_Field.SessionID);
        packUUID(bytebuffer, AgentData_Field.QueryID);
        packByte(bytebuffer, (byte)AgentData_Field.GodLevel);
        packVariable(bytebuffer, Data_Field.Name, 1);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        AgentData_Field.AgentID = unpackUUID(bytebuffer);
        AgentData_Field.SessionID = unpackUUID(bytebuffer);
        AgentData_Field.QueryID = unpackUUID(bytebuffer);
        AgentData_Field.GodLevel = unpackByte(bytebuffer) & 0xff;
        Data_Field.Name = unpackVariable(bytebuffer, 1);
    }
}
