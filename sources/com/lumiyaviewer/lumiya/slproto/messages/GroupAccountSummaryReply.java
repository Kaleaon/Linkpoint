// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class GroupAccountSummaryReply extends SLMessage
{
    public static class AgentData
    {

        public UUID AgentID;
        public UUID GroupID;

        public AgentData()
        {
        }
    }

    public static class MoneyData
    {

        public int Balance;
        public int CurrentInterval;
        public int GroupTaxCurrent;
        public int GroupTaxEstimate;
        public int IntervalDays;
        public int LandTaxCurrent;
        public int LandTaxEstimate;
        public byte LastTaxDate[];
        public int LightTaxCurrent;
        public int LightTaxEstimate;
        public int NonExemptMembers;
        public int ObjectTaxCurrent;
        public int ObjectTaxEstimate;
        public int ParcelDirFeeCurrent;
        public int ParcelDirFeeEstimate;
        public UUID RequestID;
        public byte StartDate[];
        public byte TaxDate[];
        public int TotalCredits;
        public int TotalDebits;

        public MoneyData()
        {
        }
    }


    public AgentData AgentData_Field;
    public MoneyData MoneyData_Field;

    public GroupAccountSummaryReply()
    {
        zeroCoded = true;
        AgentData_Field = new AgentData();
        MoneyData_Field = new MoneyData();
    }

    public int CalcPayloadSize()
    {
        return MoneyData_Field.StartDate.length + 25 + 4 + 4 + 4 + 4 + 4 + 4 + 4 + 4 + 4 + 4 + 4 + 4 + 4 + 4 + 1 + MoneyData_Field.LastTaxDate.length + 1 + MoneyData_Field.TaxDate.length + 36;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleGroupAccountSummaryReply(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)1);
        bytebuffer.put((byte)98);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packUUID(bytebuffer, AgentData_Field.GroupID);
        packUUID(bytebuffer, MoneyData_Field.RequestID);
        packInt(bytebuffer, MoneyData_Field.IntervalDays);
        packInt(bytebuffer, MoneyData_Field.CurrentInterval);
        packVariable(bytebuffer, MoneyData_Field.StartDate, 1);
        packInt(bytebuffer, MoneyData_Field.Balance);
        packInt(bytebuffer, MoneyData_Field.TotalCredits);
        packInt(bytebuffer, MoneyData_Field.TotalDebits);
        packInt(bytebuffer, MoneyData_Field.ObjectTaxCurrent);
        packInt(bytebuffer, MoneyData_Field.LightTaxCurrent);
        packInt(bytebuffer, MoneyData_Field.LandTaxCurrent);
        packInt(bytebuffer, MoneyData_Field.GroupTaxCurrent);
        packInt(bytebuffer, MoneyData_Field.ParcelDirFeeCurrent);
        packInt(bytebuffer, MoneyData_Field.ObjectTaxEstimate);
        packInt(bytebuffer, MoneyData_Field.LightTaxEstimate);
        packInt(bytebuffer, MoneyData_Field.LandTaxEstimate);
        packInt(bytebuffer, MoneyData_Field.GroupTaxEstimate);
        packInt(bytebuffer, MoneyData_Field.ParcelDirFeeEstimate);
        packInt(bytebuffer, MoneyData_Field.NonExemptMembers);
        packVariable(bytebuffer, MoneyData_Field.LastTaxDate, 1);
        packVariable(bytebuffer, MoneyData_Field.TaxDate, 1);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        AgentData_Field.AgentID = unpackUUID(bytebuffer);
        AgentData_Field.GroupID = unpackUUID(bytebuffer);
        MoneyData_Field.RequestID = unpackUUID(bytebuffer);
        MoneyData_Field.IntervalDays = unpackInt(bytebuffer);
        MoneyData_Field.CurrentInterval = unpackInt(bytebuffer);
        MoneyData_Field.StartDate = unpackVariable(bytebuffer, 1);
        MoneyData_Field.Balance = unpackInt(bytebuffer);
        MoneyData_Field.TotalCredits = unpackInt(bytebuffer);
        MoneyData_Field.TotalDebits = unpackInt(bytebuffer);
        MoneyData_Field.ObjectTaxCurrent = unpackInt(bytebuffer);
        MoneyData_Field.LightTaxCurrent = unpackInt(bytebuffer);
        MoneyData_Field.LandTaxCurrent = unpackInt(bytebuffer);
        MoneyData_Field.GroupTaxCurrent = unpackInt(bytebuffer);
        MoneyData_Field.ParcelDirFeeCurrent = unpackInt(bytebuffer);
        MoneyData_Field.ObjectTaxEstimate = unpackInt(bytebuffer);
        MoneyData_Field.LightTaxEstimate = unpackInt(bytebuffer);
        MoneyData_Field.LandTaxEstimate = unpackInt(bytebuffer);
        MoneyData_Field.GroupTaxEstimate = unpackInt(bytebuffer);
        MoneyData_Field.ParcelDirFeeEstimate = unpackInt(bytebuffer);
        MoneyData_Field.NonExemptMembers = unpackInt(bytebuffer);
        MoneyData_Field.LastTaxDate = unpackVariable(bytebuffer, 1);
        MoneyData_Field.TaxDate = unpackVariable(bytebuffer, 1);
    }
}
