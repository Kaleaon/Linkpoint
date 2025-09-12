// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class CreateGroupRequest extends SLMessage
{
    public static class AgentData
    {

        public UUID AgentID;
        public UUID SessionID;

        public AgentData()
        {
        }
    }

    public static class GroupData
    {

        public boolean AllowPublish;
        public byte Charter[];
        public UUID InsigniaID;
        public boolean MaturePublish;
        public int MembershipFee;
        public byte Name[];
        public boolean OpenEnrollment;
        public boolean ShowInList;

        public GroupData()
        {
        }
    }


    public AgentData AgentData_Field;
    public GroupData GroupData_Field;

    public CreateGroupRequest()
    {
        zeroCoded = true;
        AgentData_Field = new AgentData();
        GroupData_Field = new GroupData();
    }

    public int CalcPayloadSize()
    {
        return GroupData_Field.Name.length + 1 + 2 + GroupData_Field.Charter.length + 1 + 16 + 4 + 1 + 1 + 1 + 36;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleCreateGroupRequest(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)1);
        bytebuffer.put((byte)83);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packUUID(bytebuffer, AgentData_Field.SessionID);
        packVariable(bytebuffer, GroupData_Field.Name, 1);
        packVariable(bytebuffer, GroupData_Field.Charter, 2);
        packBoolean(bytebuffer, GroupData_Field.ShowInList);
        packUUID(bytebuffer, GroupData_Field.InsigniaID);
        packInt(bytebuffer, GroupData_Field.MembershipFee);
        packBoolean(bytebuffer, GroupData_Field.OpenEnrollment);
        packBoolean(bytebuffer, GroupData_Field.AllowPublish);
        packBoolean(bytebuffer, GroupData_Field.MaturePublish);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        AgentData_Field.AgentID = unpackUUID(bytebuffer);
        AgentData_Field.SessionID = unpackUUID(bytebuffer);
        GroupData_Field.Name = unpackVariable(bytebuffer, 1);
        GroupData_Field.Charter = unpackVariable(bytebuffer, 2);
        GroupData_Field.ShowInList = unpackBoolean(bytebuffer);
        GroupData_Field.InsigniaID = unpackUUID(bytebuffer);
        GroupData_Field.MembershipFee = unpackInt(bytebuffer);
        GroupData_Field.OpenEnrollment = unpackBoolean(bytebuffer);
        GroupData_Field.AllowPublish = unpackBoolean(bytebuffer);
        GroupData_Field.MaturePublish = unpackBoolean(bytebuffer);
    }
}
