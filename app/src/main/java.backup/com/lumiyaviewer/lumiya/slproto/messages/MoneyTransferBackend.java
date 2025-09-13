package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

public class MoneyTransferBackend extends SLMessage {
    public MoneyData MoneyData_Field = new MoneyData();

    public static class MoneyData {
        public int AggregatePermInventory;
        public int AggregatePermNextOwner;
        public int Amount;
        public byte[] Description;
        public UUID DestID;
        public int Flags;
        public int GridX;
        public int GridY;
        public UUID RegionID;
        public UUID SourceID;
        public UUID TransactionID;
        public int TransactionTime;
        public int TransactionType;
    }

    public MoneyTransferBackend() {
        this.zeroCoded = true;
    }

    public int CalcPayloadSize() {
        return this.MoneyData_Field.Description.length + 88 + 4;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleMoneyTransferBackend(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) 56);
        packUUID(byteBuffer, this.MoneyData_Field.TransactionID);
        packInt(byteBuffer, this.MoneyData_Field.TransactionTime);
        packUUID(byteBuffer, this.MoneyData_Field.SourceID);
        packUUID(byteBuffer, this.MoneyData_Field.DestID);
        packByte(byteBuffer, (byte) this.MoneyData_Field.Flags);
        packInt(byteBuffer, this.MoneyData_Field.Amount);
        packByte(byteBuffer, (byte) this.MoneyData_Field.AggregatePermNextOwner);
        packByte(byteBuffer, (byte) this.MoneyData_Field.AggregatePermInventory);
        packInt(byteBuffer, this.MoneyData_Field.TransactionType);
        packUUID(byteBuffer, this.MoneyData_Field.RegionID);
        packInt(byteBuffer, this.MoneyData_Field.GridX);
        packInt(byteBuffer, this.MoneyData_Field.GridY);
        packVariable(byteBuffer, this.MoneyData_Field.Description, 1);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.MoneyData_Field.TransactionID = unpackUUID(byteBuffer);
        this.MoneyData_Field.TransactionTime = unpackInt(byteBuffer);
        this.MoneyData_Field.SourceID = unpackUUID(byteBuffer);
        this.MoneyData_Field.DestID = unpackUUID(byteBuffer);
        this.MoneyData_Field.Flags = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
        this.MoneyData_Field.Amount = unpackInt(byteBuffer);
        this.MoneyData_Field.AggregatePermNextOwner = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
        this.MoneyData_Field.AggregatePermInventory = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
        this.MoneyData_Field.TransactionType = unpackInt(byteBuffer);
        this.MoneyData_Field.RegionID = unpackUUID(byteBuffer);
        this.MoneyData_Field.GridX = unpackInt(byteBuffer);
        this.MoneyData_Field.GridY = unpackInt(byteBuffer);
        this.MoneyData_Field.Description = unpackVariable(byteBuffer, 1);
    }
}
