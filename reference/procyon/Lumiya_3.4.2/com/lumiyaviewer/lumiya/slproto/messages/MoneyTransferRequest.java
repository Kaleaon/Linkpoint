// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class MoneyTransferRequest extends SLMessage
{
    public AgentData AgentData_Field;
    public MoneyData MoneyData_Field;
    
    public MoneyTransferRequest() {
        this.zeroCoded = true;
        this.AgentData_Field = new AgentData();
        this.MoneyData_Field = new MoneyData();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.MoneyData_Field.Description.length + 44 + 36;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleMoneyTransferRequest(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)1);
        byteBuffer.put((byte)55);
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packUUID(byteBuffer, this.AgentData_Field.SessionID);
        this.packUUID(byteBuffer, this.MoneyData_Field.SourceID);
        this.packUUID(byteBuffer, this.MoneyData_Field.DestID);
        this.packByte(byteBuffer, (byte)this.MoneyData_Field.Flags);
        this.packInt(byteBuffer, this.MoneyData_Field.Amount);
        this.packByte(byteBuffer, (byte)this.MoneyData_Field.AggregatePermNextOwner);
        this.packByte(byteBuffer, (byte)this.MoneyData_Field.AggregatePermInventory);
        this.packInt(byteBuffer, this.MoneyData_Field.TransactionType);
        this.packVariable(byteBuffer, this.MoneyData_Field.Description, 1);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = this.unpackUUID(byteBuffer);
        this.MoneyData_Field.SourceID = this.unpackUUID(byteBuffer);
        this.MoneyData_Field.DestID = this.unpackUUID(byteBuffer);
        this.MoneyData_Field.Flags = (this.unpackByte(byteBuffer) & 0xFF);
        this.MoneyData_Field.Amount = this.unpackInt(byteBuffer);
        this.MoneyData_Field.AggregatePermNextOwner = (this.unpackByte(byteBuffer) & 0xFF);
        this.MoneyData_Field.AggregatePermInventory = (this.unpackByte(byteBuffer) & 0xFF);
        this.MoneyData_Field.TransactionType = this.unpackInt(byteBuffer);
        this.MoneyData_Field.Description = this.unpackVariable(byteBuffer, 1);
    }
    
    public static class AgentData
    {
        public UUID AgentID;
        public UUID SessionID;
    }
    
    public static class MoneyData
    {
        public int AggregatePermInventory;
        public int AggregatePermNextOwner;
        public int Amount;
        public byte[] Description;
        public UUID DestID;
        public int Flags;
        public UUID SourceID;
        public int TransactionType;
    }
}
