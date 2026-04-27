// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class EjectGroupMemberReply extends SLMessage
{
    public static class AgentData
    {

        public UUID AgentID;

        public AgentData()
        {
        }
    }

    public static class EjectData
    {

        public boolean Success;

        public EjectData()
        {
        }
    }

    public static class GroupData
    {

        public UUID GroupID;

        public GroupData()
        {
        }
    }


    public AgentData AgentData_Field;
    public EjectData EjectData_Field;
    public GroupData GroupData_Field;

    public EjectGroupMemberReply()
    {
        zeroCoded = false;
        AgentData_Field = new AgentData();
        GroupData_Field = new GroupData();
        EjectData_Field = new EjectData();
    }

    public int CalcPayloadSize()
    {
        return 37;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleEjectGroupMemberReply(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)1);
        bytebuffer.put((byte)90);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packUUID(bytebuffer, GroupData_Field.GroupID);
        packBoolean(bytebuffer, EjectData_Field.Success);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        AgentData_Field.AgentID = unpackUUID(bytebuffer);
        GroupData_Field.GroupID = unpackUUID(bytebuffer);
        EjectData_Field.Success = unpackBoolean(bytebuffer);
    }
}
