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

public class GroupMembersReply extends SLMessage
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
        public int MemberCount;
        public UUID RequestID;

        public GroupData()
        {
        }
    }

    public static class MemberData
    {

        public UUID AgentID;
        public long AgentPowers;
        public int Contribution;
        public boolean IsOwner;
        public byte OnlineStatus[];
        public byte Title[];

        public MemberData()
        {
        }
    }


    public AgentData AgentData_Field;
    public GroupData GroupData_Field;
    public ArrayList MemberData_Fields;

    public GroupMembersReply()
    {
        MemberData_Fields = new ArrayList();
        zeroCoded = true;
        AgentData_Field = new AgentData();
        GroupData_Field = new GroupData();
    }

    public int CalcPayloadSize()
    {
        Iterator iterator = MemberData_Fields.iterator();
        MemberData memberdata;
        int i;
        int j;
        for (i = 57; iterator.hasNext(); i = memberdata.Title.length + (j + 21 + 8 + 1) + 1 + i)
        {
            memberdata = (MemberData)iterator.next();
            j = memberdata.OnlineStatus.length;
        }

        return i;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleGroupMembersReply(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)1);
        bytebuffer.put((byte)111);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packUUID(bytebuffer, GroupData_Field.GroupID);
        packUUID(bytebuffer, GroupData_Field.RequestID);
        packInt(bytebuffer, GroupData_Field.MemberCount);
        bytebuffer.put((byte)MemberData_Fields.size());
        MemberData memberdata;
        for (Iterator iterator = MemberData_Fields.iterator(); iterator.hasNext(); packBoolean(bytebuffer, memberdata.IsOwner))
        {
            memberdata = (MemberData)iterator.next();
            packUUID(bytebuffer, memberdata.AgentID);
            packInt(bytebuffer, memberdata.Contribution);
            packVariable(bytebuffer, memberdata.OnlineStatus, 1);
            packLong(bytebuffer, memberdata.AgentPowers);
            packVariable(bytebuffer, memberdata.Title, 1);
        }

    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        AgentData_Field.AgentID = unpackUUID(bytebuffer);
        GroupData_Field.GroupID = unpackUUID(bytebuffer);
        GroupData_Field.RequestID = unpackUUID(bytebuffer);
        GroupData_Field.MemberCount = unpackInt(bytebuffer);
        byte byte0 = bytebuffer.get();
        for (int i = 0; i < (byte0 & 0xff); i++)
        {
            MemberData memberdata = new MemberData();
            memberdata.AgentID = unpackUUID(bytebuffer);
            memberdata.Contribution = unpackInt(bytebuffer);
            memberdata.OnlineStatus = unpackVariable(bytebuffer, 1);
            memberdata.AgentPowers = unpackLong(bytebuffer);
            memberdata.Title = unpackVariable(bytebuffer, 1);
            memberdata.IsOwner = unpackBoolean(bytebuffer);
            MemberData_Fields.add(memberdata);
        }

    }
}
