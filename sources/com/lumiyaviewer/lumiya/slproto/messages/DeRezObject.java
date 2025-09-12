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

public class DeRezObject extends SLMessage
{
    public static class AgentBlock
    {

        public int Destination;
        public UUID DestinationID;
        public UUID GroupID;
        public int PacketCount;
        public int PacketNumber;
        public UUID TransactionID;

        public AgentBlock()
        {
        }
    }

    public static class AgentData
    {

        public UUID AgentID;
        public UUID SessionID;

        public AgentData()
        {
        }
    }

    public static class ObjectData
    {

        public int ObjectLocalID;

        public ObjectData()
        {
        }
    }


    public AgentBlock AgentBlock_Field;
    public AgentData AgentData_Field;
    public ArrayList ObjectData_Fields;

    public DeRezObject()
    {
        ObjectData_Fields = new ArrayList();
        zeroCoded = true;
        AgentData_Field = new AgentData();
        AgentBlock_Field = new AgentBlock();
    }

    public int CalcPayloadSize()
    {
        return ObjectData_Fields.size() * 4 + 88;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleDeRezObject(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)1);
        bytebuffer.put((byte)35);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packUUID(bytebuffer, AgentData_Field.SessionID);
        packUUID(bytebuffer, AgentBlock_Field.GroupID);
        packByte(bytebuffer, (byte)AgentBlock_Field.Destination);
        packUUID(bytebuffer, AgentBlock_Field.DestinationID);
        packUUID(bytebuffer, AgentBlock_Field.TransactionID);
        packByte(bytebuffer, (byte)AgentBlock_Field.PacketCount);
        packByte(bytebuffer, (byte)AgentBlock_Field.PacketNumber);
        bytebuffer.put((byte)ObjectData_Fields.size());
        for (Iterator iterator = ObjectData_Fields.iterator(); iterator.hasNext(); packInt(bytebuffer, ((ObjectData)iterator.next()).ObjectLocalID)) { }
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        AgentData_Field.AgentID = unpackUUID(bytebuffer);
        AgentData_Field.SessionID = unpackUUID(bytebuffer);
        AgentBlock_Field.GroupID = unpackUUID(bytebuffer);
        AgentBlock_Field.Destination = unpackByte(bytebuffer) & 0xff;
        AgentBlock_Field.DestinationID = unpackUUID(bytebuffer);
        AgentBlock_Field.TransactionID = unpackUUID(bytebuffer);
        AgentBlock_Field.PacketCount = unpackByte(bytebuffer) & 0xff;
        AgentBlock_Field.PacketNumber = unpackByte(bytebuffer) & 0xff;
        byte byte0 = bytebuffer.get();
        for (int i = 0; i < (byte0 & 0xff); i++)
        {
            ObjectData objectdata = new ObjectData();
            objectdata.ObjectLocalID = unpackInt(bytebuffer);
            ObjectData_Fields.add(objectdata);
        }

    }
}
