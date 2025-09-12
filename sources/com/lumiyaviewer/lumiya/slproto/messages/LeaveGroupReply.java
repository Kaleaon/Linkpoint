// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class LeaveGroupReply extends SLMessage
{
    public static class AgentData
    {

        public UUID AgentID;

        public AgentData()
        {
        }
    }

    public static class GroupData
    {

        public UUID GroupID;
        public boolean Success;

        public GroupData()
        {
        }
    }


    public AgentData AgentData_Field;
    public GroupData GroupData_Field;

    public LeaveGroupReply()
    {
        zeroCoded = false;
        AgentData_Field = new AgentData();
        GroupData_Field = new GroupData();
    }

    public int CalcPayloadSize()
    {
        return 37;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleLeaveGroupReply(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)1);
        bytebuffer.put((byte)92);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packUUID(bytebuffer, GroupData_Field.GroupID);
        packBoolean(bytebuffer, GroupData_Field.Success);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        AgentData_Field.AgentID = unpackUUID(bytebuffer);
        GroupData_Field.GroupID = unpackUUID(bytebuffer);
        GroupData_Field.Success = unpackBoolean(bytebuffer);
    }
}
