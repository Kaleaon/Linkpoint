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

public class GroupRoleUpdate extends SLMessage
{
    public static class AgentData
    {

        public UUID AgentID;
        public UUID GroupID;
        public UUID SessionID;

        public AgentData()
        {
        }
    }

    public static class RoleData
    {

        public byte Description[];
        public byte Name[];
        public long Powers;
        public UUID RoleID;
        public byte Title[];
        public int UpdateType;

        public RoleData()
        {
        }
    }


    public AgentData AgentData_Field;
    public ArrayList RoleData_Fields;

    public GroupRoleUpdate()
    {
        RoleData_Fields = new ArrayList();
        zeroCoded = false;
        AgentData_Field = new AgentData();
    }

    public int CalcPayloadSize()
    {
        Iterator iterator = RoleData_Fields.iterator();
        RoleData roledata;
        int i;
        int j;
        int k;
        for (i = 53; iterator.hasNext(); i = roledata.Title.length + (j + 17 + 1 + k + 1) + 8 + 1 + i)
        {
            roledata = (RoleData)iterator.next();
            j = roledata.Name.length;
            k = roledata.Description.length;
        }

        return i;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleGroupRoleUpdate(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)1);
        bytebuffer.put((byte)122);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packUUID(bytebuffer, AgentData_Field.SessionID);
        packUUID(bytebuffer, AgentData_Field.GroupID);
        bytebuffer.put((byte)RoleData_Fields.size());
        RoleData roledata;
        for (Iterator iterator = RoleData_Fields.iterator(); iterator.hasNext(); packByte(bytebuffer, (byte)roledata.UpdateType))
        {
            roledata = (RoleData)iterator.next();
            packUUID(bytebuffer, roledata.RoleID);
            packVariable(bytebuffer, roledata.Name, 1);
            packVariable(bytebuffer, roledata.Description, 1);
            packVariable(bytebuffer, roledata.Title, 1);
            packLong(bytebuffer, roledata.Powers);
        }

    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        AgentData_Field.AgentID = unpackUUID(bytebuffer);
        AgentData_Field.SessionID = unpackUUID(bytebuffer);
        AgentData_Field.GroupID = unpackUUID(bytebuffer);
        byte byte0 = bytebuffer.get();
        for (int i = 0; i < (byte0 & 0xff); i++)
        {
            RoleData roledata = new RoleData();
            roledata.RoleID = unpackUUID(bytebuffer);
            roledata.Name = unpackVariable(bytebuffer, 1);
            roledata.Description = unpackVariable(bytebuffer, 1);
            roledata.Title = unpackVariable(bytebuffer, 1);
            roledata.Powers = unpackLong(bytebuffer);
            roledata.UpdateType = unpackByte(bytebuffer) & 0xff;
            RoleData_Fields.add(roledata);
        }

    }
}
