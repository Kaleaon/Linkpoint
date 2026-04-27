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

public class GroupRoleDataReply extends SLMessage
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
        public UUID RequestID;
        public int RoleCount;

        public GroupData()
        {
        }
    }

    public static class RoleData
    {

        public byte Description[];
        public int Members;
        public byte Name[];
        public long Powers;
        public UUID RoleID;
        public byte Title[];

        public RoleData()
        {
        }
    }


    public AgentData AgentData_Field;
    public GroupData GroupData_Field;
    public ArrayList RoleData_Fields;

    public GroupRoleDataReply()
    {
        RoleData_Fields = new ArrayList();
        zeroCoded = false;
        AgentData_Field = new AgentData();
        GroupData_Field = new GroupData();
    }

    public int CalcPayloadSize()
    {
        Iterator iterator = RoleData_Fields.iterator();
        RoleData roledata;
        int i;
        int j;
        int k;
        for (i = 57; iterator.hasNext(); i = roledata.Description.length + (j + 17 + 1 + k + 1) + 8 + 4 + i)
        {
            roledata = (RoleData)iterator.next();
            j = roledata.Name.length;
            k = roledata.Title.length;
        }

        return i;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleGroupRoleDataReply(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)1);
        bytebuffer.put((byte)116);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packUUID(bytebuffer, GroupData_Field.GroupID);
        packUUID(bytebuffer, GroupData_Field.RequestID);
        packInt(bytebuffer, GroupData_Field.RoleCount);
        bytebuffer.put((byte)RoleData_Fields.size());
        RoleData roledata;
        for (Iterator iterator = RoleData_Fields.iterator(); iterator.hasNext(); packInt(bytebuffer, roledata.Members))
        {
            roledata = (RoleData)iterator.next();
            packUUID(bytebuffer, roledata.RoleID);
            packVariable(bytebuffer, roledata.Name, 1);
            packVariable(bytebuffer, roledata.Title, 1);
            packVariable(bytebuffer, roledata.Description, 1);
            packLong(bytebuffer, roledata.Powers);
        }

    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        AgentData_Field.AgentID = unpackUUID(bytebuffer);
        GroupData_Field.GroupID = unpackUUID(bytebuffer);
        GroupData_Field.RequestID = unpackUUID(bytebuffer);
        GroupData_Field.RoleCount = unpackInt(bytebuffer);
        byte byte0 = bytebuffer.get();
        for (int i = 0; i < (byte0 & 0xff); i++)
        {
            RoleData roledata = new RoleData();
            roledata.RoleID = unpackUUID(bytebuffer);
            roledata.Name = unpackVariable(bytebuffer, 1);
            roledata.Title = unpackVariable(bytebuffer, 1);
            roledata.Description = unpackVariable(bytebuffer, 1);
            roledata.Powers = unpackLong(bytebuffer);
            roledata.Members = unpackInt(bytebuffer);
            RoleData_Fields.add(roledata);
        }

    }
}
