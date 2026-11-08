package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;
/* loaded from: classes.dex */
public class MoneyTransferRequest extends SLMessage {
    public AgentData AgentData_Field;
    public MoneyData MoneyData_Field;

    /* loaded from: classes.dex */
    public static class AgentData {
        public UUID AgentID;
        public UUID SessionID;
    }

    /* loaded from: classes.dex */
    public static class MoneyData {
        public int AggregatePermInventory;
        public int AggregatePermNextOwner;
        public int Amount;
        public byte[] Description;
        public UUID DestID;
        public int Flags;
        public UUID SourceID;
        public int TransactionType;
    }

    public MoneyTransferRequest() {
        this.zeroCoded = true;
        this.AgentData_Field = new AgentData();
        this.MoneyData_Field = new MoneyData();
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public int CalcPayloadSize() {
        return this.MoneyData_Field.Description.length + 44 + 36;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleMoneyTransferRequest(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort((short) -1);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) 55);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.AgentData_Field.SessionID);
        packUUID(byteBuffer, this.MoneyData_Field.SourceID);
        packUUID(byteBuffer, this.MoneyData_Field.DestID);
        packByte(byteBuffer, (byte) this.MoneyData_Field.Flags);
        packInt(byteBuffer, this.MoneyData_Field.Amount);
        packByte(byteBuffer, (byte) this.MoneyData_Field.AggregatePermNextOwner);
        packByte(byteBuffer, (byte) this.MoneyData_Field.AggregatePermInventory);
        packInt(byteBuffer, this.MoneyData_Field.TransactionType);
        packVariable(byteBuffer, this.MoneyData_Field.Description, 1);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer);
        this.MoneyData_Field.SourceID = unpackUUID(byteBuffer);
        this.MoneyData_Field.DestID = unpackUUID(byteBuffer);
        this.MoneyData_Field.Flags = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
        this.MoneyData_Field.Amount = unpackInt(byteBuffer);
        this.MoneyData_Field.AggregatePermNextOwner = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
        this.MoneyData_Field.AggregatePermInventory = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
        this.MoneyData_Field.TransactionType = unpackInt(byteBuffer);
        this.MoneyData_Field.Description = unpackVariable(byteBuffer, 1);
    }
}
