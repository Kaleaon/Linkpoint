// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class GroupAccountTransactionsRequest extends SLMessage
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

    public static class MoneyData
    {

        public int CurrentInterval;
        public int IntervalDays;
        public UUID RequestID;

        public MoneyData()
        {
        }
    }


    public AgentData AgentData_Field;
    public MoneyData MoneyData_Field;

    public GroupAccountTransactionsRequest()
    {
        zeroCoded = true;
        AgentData_Field = new AgentData();
        MoneyData_Field = new MoneyData();
    }

    public int CalcPayloadSize()
    {
        return 76;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleGroupAccountTransactionsRequest(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)1);
        bytebuffer.put((byte)101);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packUUID(bytebuffer, AgentData_Field.SessionID);
        packUUID(bytebuffer, AgentData_Field.GroupID);
        packUUID(bytebuffer, MoneyData_Field.RequestID);
        packInt(bytebuffer, MoneyData_Field.IntervalDays);
        packInt(bytebuffer, MoneyData_Field.CurrentInterval);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        AgentData_Field.AgentID = unpackUUID(bytebuffer);
        AgentData_Field.SessionID = unpackUUID(bytebuffer);
        AgentData_Field.GroupID = unpackUUID(bytebuffer);
        MoneyData_Field.RequestID = unpackUUID(bytebuffer);
        MoneyData_Field.IntervalDays = unpackInt(bytebuffer);
        MoneyData_Field.CurrentInterval = unpackInt(bytebuffer);
    }
}
