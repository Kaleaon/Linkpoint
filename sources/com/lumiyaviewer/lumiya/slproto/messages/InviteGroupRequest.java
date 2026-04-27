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

public class InviteGroupRequest extends SLMessage
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

        public UUID GroupID;

        public GroupData()
        {
        }
    }

    public static class InviteData
    {

        public UUID InviteeID;
        public UUID RoleID;

        public InviteData()
        {
        }
    }


    public AgentData AgentData_Field;
    public GroupData GroupData_Field;
    public ArrayList InviteData_Fields;

    public InviteGroupRequest()
    {
        InviteData_Fields = new ArrayList();
        zeroCoded = false;
        AgentData_Field = new AgentData();
        GroupData_Field = new GroupData();
    }

    public int CalcPayloadSize()
    {
        return InviteData_Fields.size() * 32 + 53;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleInviteGroupRequest(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)1);
        bytebuffer.put((byte)93);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packUUID(bytebuffer, AgentData_Field.SessionID);
        packUUID(bytebuffer, GroupData_Field.GroupID);
        bytebuffer.put((byte)InviteData_Fields.size());
        InviteData invitedata;
        for (Iterator iterator = InviteData_Fields.iterator(); iterator.hasNext(); packUUID(bytebuffer, invitedata.RoleID))
        {
            invitedata = (InviteData)iterator.next();
            packUUID(bytebuffer, invitedata.InviteeID);
        }

    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        AgentData_Field.AgentID = unpackUUID(bytebuffer);
        AgentData_Field.SessionID = unpackUUID(bytebuffer);
        GroupData_Field.GroupID = unpackUUID(bytebuffer);
        byte byte0 = bytebuffer.get();
        for (int i = 0; i < (byte0 & 0xff); i++)
        {
            InviteData invitedata = new InviteData();
            invitedata.InviteeID = unpackUUID(bytebuffer);
            invitedata.RoleID = unpackUUID(bytebuffer);
            InviteData_Fields.add(invitedata);
        }

    }
}
