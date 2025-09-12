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

public class AvatarGroupsReply extends SLMessage
{
    public static class AgentData
    {

        public UUID AgentID;
        public UUID AvatarID;

        public AgentData()
        {
        }
    }

    public static class GroupData
    {

        public boolean AcceptNotices;
        public UUID GroupID;
        public UUID GroupInsigniaID;
        public byte GroupName[];
        public long GroupPowers;
        public byte GroupTitle[];

        public GroupData()
        {
        }
    }

    public static class NewGroupData
    {

        public boolean ListInProfile;

        public NewGroupData()
        {
        }
    }


    public AgentData AgentData_Field;
    public ArrayList GroupData_Fields;
    public NewGroupData NewGroupData_Field;

    public AvatarGroupsReply()
    {
        GroupData_Fields = new ArrayList();
        zeroCoded = true;
        AgentData_Field = new AgentData();
        NewGroupData_Field = new NewGroupData();
    }

    public int CalcPayloadSize()
    {
        Iterator iterator = GroupData_Fields.iterator();
        GroupData groupdata;
        int i;
        int j;
        for (i = 37; iterator.hasNext(); i = groupdata.GroupName.length + (j + 10 + 16 + 1) + 16 + i)
        {
            groupdata = (GroupData)iterator.next();
            j = groupdata.GroupTitle.length;
        }

        return i + 1;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleAvatarGroupsReply(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)-83);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packUUID(bytebuffer, AgentData_Field.AvatarID);
        bytebuffer.put((byte)GroupData_Fields.size());
        GroupData groupdata;
        for (Iterator iterator = GroupData_Fields.iterator(); iterator.hasNext(); packUUID(bytebuffer, groupdata.GroupInsigniaID))
        {
            groupdata = (GroupData)iterator.next();
            packLong(bytebuffer, groupdata.GroupPowers);
            packBoolean(bytebuffer, groupdata.AcceptNotices);
            packVariable(bytebuffer, groupdata.GroupTitle, 1);
            packUUID(bytebuffer, groupdata.GroupID);
            packVariable(bytebuffer, groupdata.GroupName, 1);
        }

        packBoolean(bytebuffer, NewGroupData_Field.ListInProfile);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        AgentData_Field.AgentID = unpackUUID(bytebuffer);
        AgentData_Field.AvatarID = unpackUUID(bytebuffer);
        byte byte0 = bytebuffer.get();
        for (int i = 0; i < (byte0 & 0xff); i++)
        {
            GroupData groupdata = new GroupData();
            groupdata.GroupPowers = unpackLong(bytebuffer);
            groupdata.AcceptNotices = unpackBoolean(bytebuffer);
            groupdata.GroupTitle = unpackVariable(bytebuffer, 1);
            groupdata.GroupID = unpackUUID(bytebuffer);
            groupdata.GroupName = unpackVariable(bytebuffer, 1);
            groupdata.GroupInsigniaID = unpackUUID(bytebuffer);
            GroupData_Fields.add(groupdata);
        }

        NewGroupData_Field.ListInProfile = unpackBoolean(bytebuffer);
    }
}
