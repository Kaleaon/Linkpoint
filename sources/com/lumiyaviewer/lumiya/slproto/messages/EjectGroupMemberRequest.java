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

public class EjectGroupMemberRequest extends SLMessage
{
    public static class AgentData
    {

        public UUID AgentID;
        public UUID SessionID;

        public AgentData()
        {
        }
    }

    public static class EjectData
    {

        public UUID EjecteeID;

        public EjectData()
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


    public AgentData AgentData_Field;
    public ArrayList EjectData_Fields;
    public GroupData GroupData_Field;

    public EjectGroupMemberRequest()
    {
        EjectData_Fields = new ArrayList();
        zeroCoded = false;
        AgentData_Field = new AgentData();
        GroupData_Field = new GroupData();
    }

    public int CalcPayloadSize()
    {
        return EjectData_Fields.size() * 16 + 53;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleEjectGroupMemberRequest(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)1);
        bytebuffer.put((byte)89);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packUUID(bytebuffer, AgentData_Field.SessionID);
        packUUID(bytebuffer, GroupData_Field.GroupID);
        bytebuffer.put((byte)EjectData_Fields.size());
        for (Iterator iterator = EjectData_Fields.iterator(); iterator.hasNext(); packUUID(bytebuffer, ((EjectData)iterator.next()).EjecteeID)) { }
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        AgentData_Field.AgentID = unpackUUID(bytebuffer);
        AgentData_Field.SessionID = unpackUUID(bytebuffer);
        GroupData_Field.GroupID = unpackUUID(bytebuffer);
        byte byte0 = bytebuffer.get();
        for (int i = 0; i < (byte0 & 0xff); i++)
        {
            EjectData ejectdata = new EjectData();
            ejectdata.EjecteeID = unpackUUID(bytebuffer);
            EjectData_Fields.add(ejectdata);
        }

    }
}
