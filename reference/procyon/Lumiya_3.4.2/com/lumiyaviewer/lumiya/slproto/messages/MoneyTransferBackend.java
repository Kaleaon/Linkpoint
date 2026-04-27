// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class MoneyTransferBackend extends SLMessage
{
    public MoneyData MoneyData_Field;
    
    public MoneyTransferBackend() {
        this.zeroCoded = true;
        this.MoneyData_Field = new MoneyData();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.MoneyData_Field.Description.length + 88 + 4;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleMoneyTransferBackend(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)1);
        byteBuffer.put((byte)56);
        this.packUUID(byteBuffer, this.MoneyData_Field.TransactionID);
        this.packInt(byteBuffer, this.MoneyData_Field.TransactionTime);
        this.packUUID(byteBuffer, this.MoneyData_Field.SourceID);
        this.packUUID(byteBuffer, this.MoneyData_Field.DestID);
        this.packByte(byteBuffer, (byte)this.MoneyData_Field.Flags);
        this.packInt(byteBuffer, this.MoneyData_Field.Amount);
        this.packByte(byteBuffer, (byte)this.MoneyData_Field.AggregatePermNextOwner);
        this.packByte(byteBuffer, (byte)this.MoneyData_Field.AggregatePermInventory);
        this.packInt(byteBuffer, this.MoneyData_Field.TransactionType);
        this.packUUID(byteBuffer, this.MoneyData_Field.RegionID);
        this.packInt(byteBuffer, this.MoneyData_Field.GridX);
        this.packInt(byteBuffer, this.MoneyData_Field.GridY);
        this.packVariable(byteBuffer, this.MoneyData_Field.Description, 1);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.MoneyData_Field.TransactionID = this.unpackUUID(byteBuffer);
        this.MoneyData_Field.TransactionTime = this.unpackInt(byteBuffer);
        this.MoneyData_Field.SourceID = this.unpackUUID(byteBuffer);
        this.MoneyData_Field.DestID = this.unpackUUID(byteBuffer);
        this.MoneyData_Field.Flags = (this.unpackByte(byteBuffer) & 0xFF);
        this.MoneyData_Field.Amount = this.unpackInt(byteBuffer);
        this.MoneyData_Field.AggregatePermNextOwner = (this.unpackByte(byteBuffer) & 0xFF);
        this.MoneyData_Field.AggregatePermInventory = (this.unpackByte(byteBuffer) & 0xFF);
        this.MoneyData_Field.TransactionType = this.unpackInt(byteBuffer);
        this.MoneyData_Field.RegionID = this.unpackUUID(byteBuffer);
        this.MoneyData_Field.GridX = this.unpackInt(byteBuffer);
        this.MoneyData_Field.GridY = this.unpackInt(byteBuffer);
        this.MoneyData_Field.Description = this.unpackVariable(byteBuffer, 1);
    }
    
    public static class MoneyData
    {
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
}
