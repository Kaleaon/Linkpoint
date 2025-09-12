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

public class GroupTitlesReply extends SLMessage
{
    public static class AgentData
    {

        public UUID AgentID;
        public UUID GroupID;
        public UUID RequestID;

        public AgentData()
        {
        }
    }

    public static class GroupData
    {

        public UUID RoleID;
        public boolean Selected;
        public byte Title[];

        public GroupData()
        {
        }
    }


    public AgentData AgentData_Field;
    public ArrayList GroupData_Fields;

    public GroupTitlesReply()
    {
        GroupData_Fields = new ArrayList();
        zeroCoded = true;
        AgentData_Field = new AgentData();
    }

    public int CalcPayloadSize()
    {
        Iterator iterator = GroupData_Fields.iterator();
        int i;
        for (i = 53; iterator.hasNext(); i = ((GroupData)iterator.next()).Title.length + 1 + 16 + 1 + i) { }
        return i;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleGroupTitlesReply(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)1);
        bytebuffer.put((byte)120);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packUUID(bytebuffer, AgentData_Field.GroupID);
        packUUID(bytebuffer, AgentData_Field.RequestID);
        bytebuffer.put((byte)GroupData_Fields.size());
        GroupData groupdata;
        for (Iterator iterator = GroupData_Fields.iterator(); iterator.hasNext(); packBoolean(bytebuffer, groupdata.Selected))
        {
            groupdata = (GroupData)iterator.next();
            packVariable(bytebuffer, groupdata.Title, 1);
            packUUID(bytebuffer, groupdata.RoleID);
        }

    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        AgentData_Field.AgentID = unpackUUID(bytebuffer);
        AgentData_Field.GroupID = unpackUUID(bytebuffer);
        AgentData_Field.RequestID = unpackUUID(bytebuffer);
        byte byte0 = bytebuffer.get();
        for (int i = 0; i < (byte0 & 0xff); i++)
        {
            GroupData groupdata = new GroupData();
            groupdata.Title = unpackVariable(bytebuffer, 1);
            groupdata.RoleID = unpackUUID(bytebuffer);
            groupdata.Selected = unpackBoolean(bytebuffer);
            GroupData_Fields.add(groupdata);
        }

    }
}
