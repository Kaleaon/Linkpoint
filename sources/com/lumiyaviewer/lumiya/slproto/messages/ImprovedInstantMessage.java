// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class ImprovedInstantMessage extends SLMessage
{
    public static class AgentData
    {

        public UUID AgentID;
        public UUID SessionID;

        public AgentData()
        {
        }
    }

    public static class MessageBlock
    {

        public byte BinaryBucket[];
        public int Dialog;
        public byte FromAgentName[];
        public boolean FromGroup;
        public UUID ID;
        public byte Message[];
        public int Offline;
        public int ParentEstateID;
        public LLVector3 Position;
        public UUID RegionID;
        public int Timestamp;
        public UUID ToAgentID;

        public MessageBlock()
        {
        }
    }


    public AgentData AgentData_Field;
    public MessageBlock MessageBlock_Field;

    public ImprovedInstantMessage()
    {
        zeroCoded = true;
        AgentData_Field = new AgentData();
        MessageBlock_Field = new MessageBlock();
    }

    public int CalcPayloadSize()
    {
        return MessageBlock_Field.FromAgentName.length + 72 + 2 + MessageBlock_Field.Message.length + 2 + MessageBlock_Field.BinaryBucket.length + 36;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleImprovedInstantMessage(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)-2);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packUUID(bytebuffer, AgentData_Field.SessionID);
        packBoolean(bytebuffer, MessageBlock_Field.FromGroup);
        packUUID(bytebuffer, MessageBlock_Field.ToAgentID);
        packInt(bytebuffer, MessageBlock_Field.ParentEstateID);
        packUUID(bytebuffer, MessageBlock_Field.RegionID);
        packLLVector3(bytebuffer, MessageBlock_Field.Position);
        packByte(bytebuffer, (byte)MessageBlock_Field.Offline);
        packByte(bytebuffer, (byte)MessageBlock_Field.Dialog);
        packUUID(bytebuffer, MessageBlock_Field.ID);
        packInt(bytebuffer, MessageBlock_Field.Timestamp);
        packVariable(bytebuffer, MessageBlock_Field.FromAgentName, 1);
        packVariable(bytebuffer, MessageBlock_Field.Message, 2);
        packVariable(bytebuffer, MessageBlock_Field.BinaryBucket, 2);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        AgentData_Field.AgentID = unpackUUID(bytebuffer);
        AgentData_Field.SessionID = unpackUUID(bytebuffer);
        MessageBlock_Field.FromGroup = unpackBoolean(bytebuffer);
        MessageBlock_Field.ToAgentID = unpackUUID(bytebuffer);
        MessageBlock_Field.ParentEstateID = unpackInt(bytebuffer);
        MessageBlock_Field.RegionID = unpackUUID(bytebuffer);
        MessageBlock_Field.Position = unpackLLVector3(bytebuffer);
        MessageBlock_Field.Offline = unpackByte(bytebuffer) & 0xff;
        MessageBlock_Field.Dialog = unpackByte(bytebuffer) & 0xff;
        MessageBlock_Field.ID = unpackUUID(bytebuffer);
        MessageBlock_Field.Timestamp = unpackInt(bytebuffer);
        MessageBlock_Field.FromAgentName = unpackVariable(bytebuffer, 1);
        MessageBlock_Field.Message = unpackVariable(bytebuffer, 2);
        MessageBlock_Field.BinaryBucket = unpackVariable(bytebuffer, 2);
    }
}
