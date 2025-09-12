// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class GroupProfileReply extends SLMessage
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

        public boolean AllowPublish;
        public byte Charter[];
        public UUID FounderID;
        public UUID GroupID;
        public int GroupMembershipCount;
        public int GroupRolesCount;
        public UUID InsigniaID;
        public boolean MaturePublish;
        public byte MemberTitle[];
        public int MembershipFee;
        public int Money;
        public byte Name[];
        public boolean OpenEnrollment;
        public UUID OwnerRole;
        public long PowersMask;
        public boolean ShowInList;

        public GroupData()
        {
        }
    }


    public AgentData AgentData_Field;
    public GroupData GroupData_Field;

    public GroupProfileReply()
    {
        zeroCoded = true;
        AgentData_Field = new AgentData();
        GroupData_Field = new GroupData();
    }

    public int CalcPayloadSize()
    {
        return GroupData_Field.Name.length + 17 + 2 + GroupData_Field.Charter.length + 1 + 1 + GroupData_Field.MemberTitle.length + 8 + 16 + 16 + 4 + 1 + 4 + 4 + 4 + 1 + 1 + 16 + 20;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleGroupProfileReply(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)1);
        bytebuffer.put((byte)96);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packUUID(bytebuffer, GroupData_Field.GroupID);
        packVariable(bytebuffer, GroupData_Field.Name, 1);
        packVariable(bytebuffer, GroupData_Field.Charter, 2);
        packBoolean(bytebuffer, GroupData_Field.ShowInList);
        packVariable(bytebuffer, GroupData_Field.MemberTitle, 1);
        packLong(bytebuffer, GroupData_Field.PowersMask);
        packUUID(bytebuffer, GroupData_Field.InsigniaID);
        packUUID(bytebuffer, GroupData_Field.FounderID);
        packInt(bytebuffer, GroupData_Field.MembershipFee);
        packBoolean(bytebuffer, GroupData_Field.OpenEnrollment);
        packInt(bytebuffer, GroupData_Field.Money);
        packInt(bytebuffer, GroupData_Field.GroupMembershipCount);
        packInt(bytebuffer, GroupData_Field.GroupRolesCount);
        packBoolean(bytebuffer, GroupData_Field.AllowPublish);
        packBoolean(bytebuffer, GroupData_Field.MaturePublish);
        packUUID(bytebuffer, GroupData_Field.OwnerRole);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        AgentData_Field.AgentID = unpackUUID(bytebuffer);
        GroupData_Field.GroupID = unpackUUID(bytebuffer);
        GroupData_Field.Name = unpackVariable(bytebuffer, 1);
        GroupData_Field.Charter = unpackVariable(bytebuffer, 2);
        GroupData_Field.ShowInList = unpackBoolean(bytebuffer);
        GroupData_Field.MemberTitle = unpackVariable(bytebuffer, 1);
        GroupData_Field.PowersMask = unpackLong(bytebuffer);
        GroupData_Field.InsigniaID = unpackUUID(bytebuffer);
        GroupData_Field.FounderID = unpackUUID(bytebuffer);
        GroupData_Field.MembershipFee = unpackInt(bytebuffer);
        GroupData_Field.OpenEnrollment = unpackBoolean(bytebuffer);
        GroupData_Field.Money = unpackInt(bytebuffer);
        GroupData_Field.GroupMembershipCount = unpackInt(bytebuffer);
        GroupData_Field.GroupRolesCount = unpackInt(bytebuffer);
        GroupData_Field.AllowPublish = unpackBoolean(bytebuffer);
        GroupData_Field.MaturePublish = unpackBoolean(bytebuffer);
        GroupData_Field.OwnerRole = unpackUUID(bytebuffer);
    }
}
