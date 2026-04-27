// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class MapNameRequest extends SLMessage
{
    public static class AgentData
    {

        public UUID AgentID;
        public int EstateID;
        public int Flags;
        public boolean Godlike;
        public UUID SessionID;

        public AgentData()
        {
        }
    }

    public static class NameData
    {

        public byte Name[];

        public NameData()
        {
        }
    }


    public AgentData AgentData_Field;
    public NameData NameData_Field;

    public MapNameRequest()
    {
        zeroCoded = false;
        AgentData_Field = new AgentData();
        NameData_Field = new NameData();
    }

    public int CalcPayloadSize()
    {
        return NameData_Field.Name.length + 1 + 45;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleMapNameRequest(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)1);
        bytebuffer.put((byte)-104);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packUUID(bytebuffer, AgentData_Field.SessionID);
        packInt(bytebuffer, AgentData_Field.Flags);
        packInt(bytebuffer, AgentData_Field.EstateID);
        packBoolean(bytebuffer, AgentData_Field.Godlike);
        packVariable(bytebuffer, NameData_Field.Name, 1);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        AgentData_Field.AgentID = unpackUUID(bytebuffer);
        AgentData_Field.SessionID = unpackUUID(bytebuffer);
        AgentData_Field.Flags = unpackInt(bytebuffer);
        AgentData_Field.EstateID = unpackInt(bytebuffer);
        AgentData_Field.Godlike = unpackBoolean(bytebuffer);
        NameData_Field.Name = unpackVariable(bytebuffer, 1);
    }
}
