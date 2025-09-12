// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class GroupNoticeAdd extends SLMessage
{
    public static class AgentData
    {

        public UUID AgentID;

        public AgentData()
        {
        }
    }

    public static class MessageBlock
    {

        public byte BinaryBucket[];
        public int Dialog;
        public byte FromAgentName[];
        public UUID ID;
        public byte Message[];
        public UUID ToGroupID;

        public MessageBlock()
        {
        }
    }


    public AgentData AgentData_Field;
    public MessageBlock MessageBlock_Field;

    public GroupNoticeAdd()
    {
        zeroCoded = false;
        AgentData_Field = new AgentData();
        MessageBlock_Field = new MessageBlock();
    }

    public int CalcPayloadSize()
    {
        return MessageBlock_Field.FromAgentName.length + 34 + 2 + MessageBlock_Field.Message.length + 2 + MessageBlock_Field.BinaryBucket.length + 20;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleGroupNoticeAdd(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)61);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packUUID(bytebuffer, MessageBlock_Field.ToGroupID);
        packUUID(bytebuffer, MessageBlock_Field.ID);
        packByte(bytebuffer, (byte)MessageBlock_Field.Dialog);
        packVariable(bytebuffer, MessageBlock_Field.FromAgentName, 1);
        packVariable(bytebuffer, MessageBlock_Field.Message, 2);
        packVariable(bytebuffer, MessageBlock_Field.BinaryBucket, 2);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        AgentData_Field.AgentID = unpackUUID(bytebuffer);
        MessageBlock_Field.ToGroupID = unpackUUID(bytebuffer);
        MessageBlock_Field.ID = unpackUUID(bytebuffer);
        MessageBlock_Field.Dialog = unpackByte(bytebuffer) & 0xff;
        MessageBlock_Field.FromAgentName = unpackVariable(bytebuffer, 1);
        MessageBlock_Field.Message = unpackVariable(bytebuffer, 2);
        MessageBlock_Field.BinaryBucket = unpackVariable(bytebuffer, 2);
    }
}
