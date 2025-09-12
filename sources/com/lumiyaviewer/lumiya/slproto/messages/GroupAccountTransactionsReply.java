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

public class GroupAccountTransactionsReply extends SLMessage
{
    public static class AgentData
    {

        public UUID AgentID;
        public UUID GroupID;

        public AgentData()
        {
        }
    }

    public static class HistoryData
    {

        public int Amount;
        public byte Item[];
        public byte Time[];
        public int Type;
        public byte User[];

        public HistoryData()
        {
        }
    }

    public static class MoneyData
    {

        public int CurrentInterval;
        public int IntervalDays;
        public UUID RequestID;
        public byte StartDate[];

        public MoneyData()
        {
        }
    }


    public AgentData AgentData_Field;
    public ArrayList HistoryData_Fields;
    public MoneyData MoneyData_Field;

    public GroupAccountTransactionsReply()
    {
        HistoryData_Fields = new ArrayList();
        zeroCoded = true;
        AgentData_Field = new AgentData();
        MoneyData_Field = new MoneyData();
    }

    public int CalcPayloadSize()
    {
        int i = MoneyData_Field.StartDate.length;
        Iterator iterator = HistoryData_Fields.iterator();
        HistoryData historydata;
        int j;
        int k;
        for (i = i + 25 + 36 + 1; iterator.hasNext(); i = historydata.Item.length + (j + 1 + 1 + k + 4 + 1) + 4 + i)
        {
            historydata = (HistoryData)iterator.next();
            j = historydata.Time.length;
            k = historydata.User.length;
        }

        return i;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleGroupAccountTransactionsReply(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)1);
        bytebuffer.put((byte)102);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packUUID(bytebuffer, AgentData_Field.GroupID);
        packUUID(bytebuffer, MoneyData_Field.RequestID);
        packInt(bytebuffer, MoneyData_Field.IntervalDays);
        packInt(bytebuffer, MoneyData_Field.CurrentInterval);
        packVariable(bytebuffer, MoneyData_Field.StartDate, 1);
        bytebuffer.put((byte)HistoryData_Fields.size());
        HistoryData historydata;
        for (Iterator iterator = HistoryData_Fields.iterator(); iterator.hasNext(); packInt(bytebuffer, historydata.Amount))
        {
            historydata = (HistoryData)iterator.next();
            packVariable(bytebuffer, historydata.Time, 1);
            packVariable(bytebuffer, historydata.User, 1);
            packInt(bytebuffer, historydata.Type);
            packVariable(bytebuffer, historydata.Item, 1);
        }

    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        AgentData_Field.AgentID = unpackUUID(bytebuffer);
        AgentData_Field.GroupID = unpackUUID(bytebuffer);
        MoneyData_Field.RequestID = unpackUUID(bytebuffer);
        MoneyData_Field.IntervalDays = unpackInt(bytebuffer);
        MoneyData_Field.CurrentInterval = unpackInt(bytebuffer);
        MoneyData_Field.StartDate = unpackVariable(bytebuffer, 1);
        byte byte0 = bytebuffer.get();
        for (int i = 0; i < (byte0 & 0xff); i++)
        {
            HistoryData historydata = new HistoryData();
            historydata.Time = unpackVariable(bytebuffer, 1);
            historydata.User = unpackVariable(bytebuffer, 1);
            historydata.Type = unpackInt(bytebuffer);
            historydata.Item = unpackVariable(bytebuffer, 1);
            historydata.Amount = unpackInt(bytebuffer);
            HistoryData_Fields.add(historydata);
        }

    }
}
