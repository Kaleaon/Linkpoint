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

public class GroupRoleMembersReply extends SLMessage
{
    public static class AgentData
    {

        public UUID AgentID;
        public UUID GroupID;
        public UUID RequestID;
        public int TotalPairs;

        public AgentData()
        {
        }
    }

    public static class MemberData
    {

        public UUID MemberID;
        public UUID RoleID;

        public MemberData()
        {
        }
    }


    public AgentData AgentData_Field;
    public ArrayList MemberData_Fields;

    public GroupRoleMembersReply()
    {
        MemberData_Fields = new ArrayList();
        zeroCoded = false;
        AgentData_Field = new AgentData();
    }

    public int CalcPayloadSize()
    {
        return MemberData_Fields.size() * 32 + 57;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleGroupRoleMembersReply(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)1);
        bytebuffer.put((byte)118);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packUUID(bytebuffer, AgentData_Field.GroupID);
        packUUID(bytebuffer, AgentData_Field.RequestID);
        packInt(bytebuffer, AgentData_Field.TotalPairs);
        bytebuffer.put((byte)MemberData_Fields.size());
        MemberData memberdata;
        for (Iterator iterator = MemberData_Fields.iterator(); iterator.hasNext(); packUUID(bytebuffer, memberdata.MemberID))
        {
            memberdata = (MemberData)iterator.next();
            packUUID(bytebuffer, memberdata.RoleID);
        }

    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        AgentData_Field.AgentID = unpackUUID(bytebuffer);
        AgentData_Field.GroupID = unpackUUID(bytebuffer);
        AgentData_Field.RequestID = unpackUUID(bytebuffer);
        AgentData_Field.TotalPairs = unpackInt(bytebuffer);
        byte byte0 = bytebuffer.get();
        for (int i = 0; i < (byte0 & 0xff); i++)
        {
            MemberData memberdata = new MemberData();
            memberdata.RoleID = unpackUUID(bytebuffer);
            memberdata.MemberID = unpackUUID(bytebuffer);
            MemberData_Fields.add(memberdata);
        }

    }
}
