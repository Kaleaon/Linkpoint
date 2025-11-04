// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class RequestParcelTransfer extends SLMessage
{
    public Data Data_Field;
    public RegionData RegionData_Field;
    
    public RequestParcelTransfer() {
        this.zeroCoded = true;
        this.Data_Field = new Data();
        this.RegionData_Field = new RegionData();
    }
    
    @Override
    public int CalcPayloadSize() {
        return 114;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleRequestParcelTransfer(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)(-36));
        this.packUUID(byteBuffer, this.Data_Field.TransactionID);
        this.packInt(byteBuffer, this.Data_Field.TransactionTime);
        this.packUUID(byteBuffer, this.Data_Field.SourceID);
        this.packUUID(byteBuffer, this.Data_Field.DestID);
        this.packUUID(byteBuffer, this.Data_Field.OwnerID);
        this.packByte(byteBuffer, (byte)this.Data_Field.Flags);
        this.packInt(byteBuffer, this.Data_Field.TransactionType);
        this.packInt(byteBuffer, this.Data_Field.Amount);
        this.packInt(byteBuffer, this.Data_Field.BillableArea);
        this.packInt(byteBuffer, this.Data_Field.ActualArea);
        this.packBoolean(byteBuffer, this.Data_Field.Final);
        this.packUUID(byteBuffer, this.RegionData_Field.RegionID);
        this.packInt(byteBuffer, this.RegionData_Field.GridX);
        this.packInt(byteBuffer, this.RegionData_Field.GridY);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.Data_Field.TransactionID = this.unpackUUID(byteBuffer);
        this.Data_Field.TransactionTime = this.unpackInt(byteBuffer);
        this.Data_Field.SourceID = this.unpackUUID(byteBuffer);
        this.Data_Field.DestID = this.unpackUUID(byteBuffer);
        this.Data_Field.OwnerID = this.unpackUUID(byteBuffer);
        this.Data_Field.Flags = (this.unpackByte(byteBuffer) & 0xFF);
        this.Data_Field.TransactionType = this.unpackInt(byteBuffer);
        this.Data_Field.Amount = this.unpackInt(byteBuffer);
        this.Data_Field.BillableArea = this.unpackInt(byteBuffer);
        this.Data_Field.ActualArea = this.unpackInt(byteBuffer);
        this.Data_Field.Final = this.unpackBoolean(byteBuffer);
        this.RegionData_Field.RegionID = this.unpackUUID(byteBuffer);
        this.RegionData_Field.GridX = this.unpackInt(byteBuffer);
        this.RegionData_Field.GridY = this.unpackInt(byteBuffer);
    }
    
    public static class Data
    {
        public int ActualArea;
        public int Amount;
        public int BillableArea;
        public UUID DestID;
        public boolean Final;
        public int Flags;
        public UUID OwnerID;
        public UUID SourceID;
        public UUID TransactionID;
        public int TransactionTime;
        public int TransactionType;
    }
    
    public static class RegionData
    {
        public int GridX;
        public int GridY;
        public UUID RegionID;
    }
}
