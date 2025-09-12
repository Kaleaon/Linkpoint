// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class AgentDataUpdate extends SLMessage
{
    public static class AgentData
    {

        public UUID ActiveGroupID;
        public UUID AgentID;
        public byte FirstName[];
        public byte GroupName[];
        public long GroupPowers;
        public byte GroupTitle[];
        public byte LastName[];

        public AgentData()
        {
        }
    }


    public AgentData AgentData_Field;

    public AgentDataUpdate()
    {
        zeroCoded = true;
        AgentData_Field = new AgentData();
    }

    public int CalcPayloadSize()
    {
        return AgentData_Field.FirstName.length + 17 + 1 + AgentData_Field.LastName.length + 1 + AgentData_Field.GroupTitle.length + 16 + 8 + 1 + AgentData_Field.GroupName.length + 4;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleAgentDataUpdate(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)1);
        bytebuffer.put((byte)-125);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packVariable(bytebuffer, AgentData_Field.FirstName, 1);
        packVariable(bytebuffer, AgentData_Field.LastName, 1);
        packVariable(bytebuffer, AgentData_Field.GroupTitle, 1);
        packUUID(bytebuffer, AgentData_Field.ActiveGroupID);
        packLong(bytebuffer, AgentData_Field.GroupPowers);
        packVariable(bytebuffer, AgentData_Field.GroupName, 1);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        AgentData_Field.AgentID = unpackUUID(bytebuffer);
        AgentData_Field.FirstName = unpackVariable(bytebuffer, 1);
        AgentData_Field.LastName = unpackVariable(bytebuffer, 1);
        AgentData_Field.GroupTitle = unpackVariable(bytebuffer, 1);
        AgentData_Field.ActiveGroupID = unpackUUID(bytebuffer);
        AgentData_Field.GroupPowers = unpackLong(bytebuffer);
        AgentData_Field.GroupName = unpackVariable(bytebuffer, 1);
    }
}
