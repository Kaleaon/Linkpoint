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

public class AgentGroupDataUpdate extends SLMessage
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

        public boolean AcceptNotices;
        public int Contribution;
        public UUID GroupID;
        public UUID GroupInsigniaID;
        public byte GroupName[];
        public long GroupPowers;

        public GroupData()
        {
        }
    }


    public AgentData AgentData_Field;
    public ArrayList GroupData_Fields;

    public AgentGroupDataUpdate()
    {
        GroupData_Fields = new ArrayList();
        zeroCoded = true;
        AgentData_Field = new AgentData();
    }

    public int CalcPayloadSize()
    {
        Iterator iterator = GroupData_Fields.iterator();
        int i;
        for (i = 21; iterator.hasNext(); i = ((GroupData)iterator.next()).GroupName.length + 46 + i) { }
        return i;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleAgentGroupDataUpdate(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)1);
        bytebuffer.put((byte)-123);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        bytebuffer.put((byte)GroupData_Fields.size());
        GroupData groupdata;
        for (Iterator iterator = GroupData_Fields.iterator(); iterator.hasNext(); packVariable(bytebuffer, groupdata.GroupName, 1))
        {
            groupdata = (GroupData)iterator.next();
            packUUID(bytebuffer, groupdata.GroupID);
            packLong(bytebuffer, groupdata.GroupPowers);
            packBoolean(bytebuffer, groupdata.AcceptNotices);
            packUUID(bytebuffer, groupdata.GroupInsigniaID);
            packInt(bytebuffer, groupdata.Contribution);
        }

    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        AgentData_Field.AgentID = unpackUUID(bytebuffer);
        byte byte0 = bytebuffer.get();
        for (int i = 0; i < (byte0 & 0xff); i++)
        {
            GroupData groupdata = new GroupData();
            groupdata.GroupID = unpackUUID(bytebuffer);
            groupdata.GroupPowers = unpackLong(bytebuffer);
            groupdata.AcceptNotices = unpackBoolean(bytebuffer);
            groupdata.GroupInsigniaID = unpackUUID(bytebuffer);
            groupdata.Contribution = unpackInt(bytebuffer);
            groupdata.GroupName = unpackVariable(bytebuffer, 1);
            GroupData_Fields.add(groupdata);
        }

    }
}
