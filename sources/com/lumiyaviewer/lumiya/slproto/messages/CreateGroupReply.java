// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class CreateGroupReply extends SLMessage
{
    public static class AgentData
    {

        public UUID AgentID;

        public AgentData()
        {
        }
    }

    public static class ReplyData
    {

        public UUID GroupID;
        public byte Message[];
        public boolean Success;

        public ReplyData()
        {
        }
    }


    public AgentData AgentData_Field;
    public ReplyData ReplyData_Field;

    public CreateGroupReply()
    {
        zeroCoded = false;
        AgentData_Field = new AgentData();
        ReplyData_Field = new ReplyData();
    }

    public int CalcPayloadSize()
    {
        return ReplyData_Field.Message.length + 18 + 20;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleCreateGroupReply(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)1);
        bytebuffer.put((byte)84);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packUUID(bytebuffer, ReplyData_Field.GroupID);
        packBoolean(bytebuffer, ReplyData_Field.Success);
        packVariable(bytebuffer, ReplyData_Field.Message, 1);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        AgentData_Field.AgentID = unpackUUID(bytebuffer);
        ReplyData_Field.GroupID = unpackUUID(bytebuffer);
        ReplyData_Field.Success = unpackBoolean(bytebuffer);
        ReplyData_Field.Message = unpackVariable(bytebuffer, 1);
    }
}
