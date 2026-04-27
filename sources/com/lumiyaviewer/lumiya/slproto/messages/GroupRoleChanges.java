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

public class GroupRoleChanges extends SLMessage
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

    public static class RoleChange
    {

        public int Change;
        public UUID MemberID;
        public UUID RoleID;

        public RoleChange()
        {
        }
    }


    public AgentData AgentData_Field;
    public ArrayList RoleChange_Fields;

    public GroupRoleChanges()
    {
        RoleChange_Fields = new ArrayList();
        zeroCoded = false;
        AgentData_Field = new AgentData();
    }

    public int CalcPayloadSize()
    {
        return RoleChange_Fields.size() * 36 + 53;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleGroupRoleChanges(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)1);
        bytebuffer.put((byte)86);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packUUID(bytebuffer, AgentData_Field.SessionID);
        packUUID(bytebuffer, AgentData_Field.GroupID);
        bytebuffer.put((byte)RoleChange_Fields.size());
        RoleChange rolechange;
        for (Iterator iterator = RoleChange_Fields.iterator(); iterator.hasNext(); packInt(bytebuffer, rolechange.Change))
        {
            rolechange = (RoleChange)iterator.next();
            packUUID(bytebuffer, rolechange.RoleID);
            packUUID(bytebuffer, rolechange.MemberID);
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
            RoleChange rolechange = new RoleChange();
            rolechange.RoleID = unpackUUID(bytebuffer);
            rolechange.MemberID = unpackUUID(bytebuffer);
            rolechange.Change = unpackInt(bytebuffer);
            RoleChange_Fields.add(rolechange);
        }

    }
}
