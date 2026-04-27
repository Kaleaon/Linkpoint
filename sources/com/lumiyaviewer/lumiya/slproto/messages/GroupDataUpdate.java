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

public class GroupDataUpdate extends SLMessage
{
    public static class AgentGroupData
    {

        public UUID AgentID;
        public long AgentPowers;
        public UUID GroupID;
        public byte GroupTitle[];

        public AgentGroupData()
        {
        }
    }


    public ArrayList AgentGroupData_Fields;

    public GroupDataUpdate()
    {
        AgentGroupData_Fields = new ArrayList();
        zeroCoded = true;
    }

    public int CalcPayloadSize()
    {
        Iterator iterator = AgentGroupData_Fields.iterator();
        int i;
        for (i = 5; iterator.hasNext(); i = ((AgentGroupData)iterator.next()).GroupTitle.length + 41 + i) { }
        return i;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleGroupDataUpdate(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)1);
        bytebuffer.put((byte)-124);
        bytebuffer.put((byte)AgentGroupData_Fields.size());
        AgentGroupData agentgroupdata;
        for (Iterator iterator = AgentGroupData_Fields.iterator(); iterator.hasNext(); packVariable(bytebuffer, agentgroupdata.GroupTitle, 1))
        {
            agentgroupdata = (AgentGroupData)iterator.next();
            packUUID(bytebuffer, agentgroupdata.AgentID);
            packUUID(bytebuffer, agentgroupdata.GroupID);
            packLong(bytebuffer, agentgroupdata.AgentPowers);
        }

    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        byte byte0 = bytebuffer.get();
        for (int i = 0; i < (byte0 & 0xff); i++)
        {
            AgentGroupData agentgroupdata = new AgentGroupData();
            agentgroupdata.AgentID = unpackUUID(bytebuffer);
            agentgroupdata.GroupID = unpackUUID(bytebuffer);
            agentgroupdata.AgentPowers = unpackLong(bytebuffer);
            agentgroupdata.GroupTitle = unpackVariable(bytebuffer, 1);
            AgentGroupData_Fields.add(agentgroupdata);
        }

    }
}
