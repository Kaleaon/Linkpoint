// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.net.Inet4Address;
import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class LogFailedMoneyTransaction extends SLMessage
{
    public TransactionData TransactionData_Field;
    
    public LogFailedMoneyTransaction() {
        this.zeroCoded = false;
        this.TransactionData_Field = new TransactionData();
    }
    
    @Override
    public int CalcPayloadSize() {
        return 78;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleLogFailedMoneyTransaction(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)20);
        this.packUUID(byteBuffer, this.TransactionData_Field.TransactionID);
        this.packInt(byteBuffer, this.TransactionData_Field.TransactionTime);
        this.packInt(byteBuffer, this.TransactionData_Field.TransactionType);
        this.packUUID(byteBuffer, this.TransactionData_Field.SourceID);
        this.packUUID(byteBuffer, this.TransactionData_Field.DestID);
        this.packByte(byteBuffer, (byte)this.TransactionData_Field.Flags);
        this.packInt(byteBuffer, this.TransactionData_Field.Amount);
        this.packIPAddress(byteBuffer, this.TransactionData_Field.SimulatorIP);
        this.packInt(byteBuffer, this.TransactionData_Field.GridX);
        this.packInt(byteBuffer, this.TransactionData_Field.GridY);
        this.packByte(byteBuffer, (byte)this.TransactionData_Field.FailureType);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.TransactionData_Field.TransactionID = this.unpackUUID(byteBuffer);
        this.TransactionData_Field.TransactionTime = this.unpackInt(byteBuffer);
        this.TransactionData_Field.TransactionType = this.unpackInt(byteBuffer);
        this.TransactionData_Field.SourceID = this.unpackUUID(byteBuffer);
        this.TransactionData_Field.DestID = this.unpackUUID(byteBuffer);
        this.TransactionData_Field.Flags = (this.unpackByte(byteBuffer) & 0xFF);
        this.TransactionData_Field.Amount = this.unpackInt(byteBuffer);
        this.TransactionData_Field.SimulatorIP = this.unpackIPAddress(byteBuffer);
        this.TransactionData_Field.GridX = this.unpackInt(byteBuffer);
        this.TransactionData_Field.GridY = this.unpackInt(byteBuffer);
        this.TransactionData_Field.FailureType = (this.unpackByte(byteBuffer) & 0xFF);
    }
    
    public static class TransactionData
    {
        public int Amount;
        public UUID DestID;
        public int FailureType;
        public int Flags;
        public int GridX;
        public int GridY;
        public Inet4Address SimulatorIP;
        public UUID SourceID;
        public UUID TransactionID;
        public int TransactionTime;
        public int TransactionType;
    }
}
