// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class GroupAccountSummaryReply extends SLMessage
{
    public AgentData AgentData_Field;
    public MoneyData MoneyData_Field;
    
    public GroupAccountSummaryReply() {
        this.zeroCoded = true;
        this.AgentData_Field = new AgentData();
        this.MoneyData_Field = new MoneyData();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.MoneyData_Field.StartDate.length + 25 + 4 + 4 + 4 + 4 + 4 + 4 + 4 + 4 + 4 + 4 + 4 + 4 + 4 + 4 + 1 + this.MoneyData_Field.LastTaxDate.length + 1 + this.MoneyData_Field.TaxDate.length + 36;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleGroupAccountSummaryReply(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)1);
        byteBuffer.put((byte)98);
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packUUID(byteBuffer, this.AgentData_Field.GroupID);
        this.packUUID(byteBuffer, this.MoneyData_Field.RequestID);
        this.packInt(byteBuffer, this.MoneyData_Field.IntervalDays);
        this.packInt(byteBuffer, this.MoneyData_Field.CurrentInterval);
        this.packVariable(byteBuffer, this.MoneyData_Field.StartDate, 1);
        this.packInt(byteBuffer, this.MoneyData_Field.Balance);
        this.packInt(byteBuffer, this.MoneyData_Field.TotalCredits);
        this.packInt(byteBuffer, this.MoneyData_Field.TotalDebits);
        this.packInt(byteBuffer, this.MoneyData_Field.ObjectTaxCurrent);
        this.packInt(byteBuffer, this.MoneyData_Field.LightTaxCurrent);
        this.packInt(byteBuffer, this.MoneyData_Field.LandTaxCurrent);
        this.packInt(byteBuffer, this.MoneyData_Field.GroupTaxCurrent);
        this.packInt(byteBuffer, this.MoneyData_Field.ParcelDirFeeCurrent);
        this.packInt(byteBuffer, this.MoneyData_Field.ObjectTaxEstimate);
        this.packInt(byteBuffer, this.MoneyData_Field.LightTaxEstimate);
        this.packInt(byteBuffer, this.MoneyData_Field.LandTaxEstimate);
        this.packInt(byteBuffer, this.MoneyData_Field.GroupTaxEstimate);
        this.packInt(byteBuffer, this.MoneyData_Field.ParcelDirFeeEstimate);
        this.packInt(byteBuffer, this.MoneyData_Field.NonExemptMembers);
        this.packVariable(byteBuffer, this.MoneyData_Field.LastTaxDate, 1);
        this.packVariable(byteBuffer, this.MoneyData_Field.TaxDate, 1);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.GroupID = this.unpackUUID(byteBuffer);
        this.MoneyData_Field.RequestID = this.unpackUUID(byteBuffer);
        this.MoneyData_Field.IntervalDays = this.unpackInt(byteBuffer);
        this.MoneyData_Field.CurrentInterval = this.unpackInt(byteBuffer);
        this.MoneyData_Field.StartDate = this.unpackVariable(byteBuffer, 1);
        this.MoneyData_Field.Balance = this.unpackInt(byteBuffer);
        this.MoneyData_Field.TotalCredits = this.unpackInt(byteBuffer);
        this.MoneyData_Field.TotalDebits = this.unpackInt(byteBuffer);
        this.MoneyData_Field.ObjectTaxCurrent = this.unpackInt(byteBuffer);
        this.MoneyData_Field.LightTaxCurrent = this.unpackInt(byteBuffer);
        this.MoneyData_Field.LandTaxCurrent = this.unpackInt(byteBuffer);
        this.MoneyData_Field.GroupTaxCurrent = this.unpackInt(byteBuffer);
        this.MoneyData_Field.ParcelDirFeeCurrent = this.unpackInt(byteBuffer);
        this.MoneyData_Field.ObjectTaxEstimate = this.unpackInt(byteBuffer);
        this.MoneyData_Field.LightTaxEstimate = this.unpackInt(byteBuffer);
        this.MoneyData_Field.LandTaxEstimate = this.unpackInt(byteBuffer);
        this.MoneyData_Field.GroupTaxEstimate = this.unpackInt(byteBuffer);
        this.MoneyData_Field.ParcelDirFeeEstimate = this.unpackInt(byteBuffer);
        this.MoneyData_Field.NonExemptMembers = this.unpackInt(byteBuffer);
        this.MoneyData_Field.LastTaxDate = this.unpackVariable(byteBuffer, 1);
        this.MoneyData_Field.TaxDate = this.unpackVariable(byteBuffer, 1);
    }
    
    public static class AgentData
    {
        public UUID AgentID;
        public UUID GroupID;
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
        public byte[] LastTaxDate;
        public int LightTaxCurrent;
        public int LightTaxEstimate;
        public int NonExemptMembers;
        public int ObjectTaxCurrent;
        public int ObjectTaxEstimate;
        public int ParcelDirFeeCurrent;
        public int ParcelDirFeeEstimate;
        public UUID RequestID;
        public byte[] StartDate;
        public byte[] TaxDate;
        public int TotalCredits;
        public int TotalDebits;
    }
}
