package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;
/* loaded from: classes.dex */
public class GroupAccountSummaryReply extends SLMessage {
    public AgentData AgentData_Field;
    public MoneyData MoneyData_Field;

    /* loaded from: classes.dex */
    public static class AgentData {
        public UUID AgentID;
        public UUID GroupID;
    }

    /* loaded from: classes.dex */
    public static class MoneyData {
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

    public GroupAccountSummaryReply() {
        this.zeroCoded = true;
        this.AgentData_Field = new AgentData();
        this.MoneyData_Field = new MoneyData();
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public int CalcPayloadSize() {
        return this.MoneyData_Field.StartDate.length + 25 + 4 + 4 + 4 + 4 + 4 + 4 + 4 + 4 + 4 + 4 + 4 + 4 + 4 + 4 + 1 + this.MoneyData_Field.LastTaxDate.length + 1 + this.MoneyData_Field.TaxDate.length + 36;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleGroupAccountSummaryReply(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort((short) -1);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) 98);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.AgentData_Field.GroupID);
        packUUID(byteBuffer, this.MoneyData_Field.RequestID);
        packInt(byteBuffer, this.MoneyData_Field.IntervalDays);
        packInt(byteBuffer, this.MoneyData_Field.CurrentInterval);
        packVariable(byteBuffer, this.MoneyData_Field.StartDate, 1);
        packInt(byteBuffer, this.MoneyData_Field.Balance);
        packInt(byteBuffer, this.MoneyData_Field.TotalCredits);
        packInt(byteBuffer, this.MoneyData_Field.TotalDebits);
        packInt(byteBuffer, this.MoneyData_Field.ObjectTaxCurrent);
        packInt(byteBuffer, this.MoneyData_Field.LightTaxCurrent);
        packInt(byteBuffer, this.MoneyData_Field.LandTaxCurrent);
        packInt(byteBuffer, this.MoneyData_Field.GroupTaxCurrent);
        packInt(byteBuffer, this.MoneyData_Field.ParcelDirFeeCurrent);
        packInt(byteBuffer, this.MoneyData_Field.ObjectTaxEstimate);
        packInt(byteBuffer, this.MoneyData_Field.LightTaxEstimate);
        packInt(byteBuffer, this.MoneyData_Field.LandTaxEstimate);
        packInt(byteBuffer, this.MoneyData_Field.GroupTaxEstimate);
        packInt(byteBuffer, this.MoneyData_Field.ParcelDirFeeEstimate);
        packInt(byteBuffer, this.MoneyData_Field.NonExemptMembers);
        packVariable(byteBuffer, this.MoneyData_Field.LastTaxDate, 1);
        packVariable(byteBuffer, this.MoneyData_Field.TaxDate, 1);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.GroupID = unpackUUID(byteBuffer);
        this.MoneyData_Field.RequestID = unpackUUID(byteBuffer);
        this.MoneyData_Field.IntervalDays = unpackInt(byteBuffer);
        this.MoneyData_Field.CurrentInterval = unpackInt(byteBuffer);
        this.MoneyData_Field.StartDate = unpackVariable(byteBuffer, 1);
        this.MoneyData_Field.Balance = unpackInt(byteBuffer);
        this.MoneyData_Field.TotalCredits = unpackInt(byteBuffer);
        this.MoneyData_Field.TotalDebits = unpackInt(byteBuffer);
        this.MoneyData_Field.ObjectTaxCurrent = unpackInt(byteBuffer);
        this.MoneyData_Field.LightTaxCurrent = unpackInt(byteBuffer);
        this.MoneyData_Field.LandTaxCurrent = unpackInt(byteBuffer);
        this.MoneyData_Field.GroupTaxCurrent = unpackInt(byteBuffer);
        this.MoneyData_Field.ParcelDirFeeCurrent = unpackInt(byteBuffer);
        this.MoneyData_Field.ObjectTaxEstimate = unpackInt(byteBuffer);
        this.MoneyData_Field.LightTaxEstimate = unpackInt(byteBuffer);
        this.MoneyData_Field.LandTaxEstimate = unpackInt(byteBuffer);
        this.MoneyData_Field.GroupTaxEstimate = unpackInt(byteBuffer);
        this.MoneyData_Field.ParcelDirFeeEstimate = unpackInt(byteBuffer);
        this.MoneyData_Field.NonExemptMembers = unpackInt(byteBuffer);
        this.MoneyData_Field.LastTaxDate = unpackVariable(byteBuffer, 1);
        this.MoneyData_Field.TaxDate = unpackVariable(byteBuffer, 1);
    }
}
