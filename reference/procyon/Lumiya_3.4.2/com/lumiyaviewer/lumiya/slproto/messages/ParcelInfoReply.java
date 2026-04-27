// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class ParcelInfoReply extends SLMessage
{
    public AgentData AgentData_Field;
    public Data Data_Field;
    
    public ParcelInfoReply() {
        this.zeroCoded = true;
        this.AgentData_Field = new AgentData();
        this.Data_Field = new Data();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.Data_Field.Name.length + 33 + 1 + this.Data_Field.Desc.length + 4 + 4 + 1 + 4 + 4 + 4 + 1 + this.Data_Field.SimName.length + 16 + 4 + 4 + 4 + 20;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleParcelInfoReply(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)55);
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packUUID(byteBuffer, this.Data_Field.ParcelID);
        this.packUUID(byteBuffer, this.Data_Field.OwnerID);
        this.packVariable(byteBuffer, this.Data_Field.Name, 1);
        this.packVariable(byteBuffer, this.Data_Field.Desc, 1);
        this.packInt(byteBuffer, this.Data_Field.ActualArea);
        this.packInt(byteBuffer, this.Data_Field.BillableArea);
        this.packByte(byteBuffer, (byte)this.Data_Field.Flags);
        this.packFloat(byteBuffer, this.Data_Field.GlobalX);
        this.packFloat(byteBuffer, this.Data_Field.GlobalY);
        this.packFloat(byteBuffer, this.Data_Field.GlobalZ);
        this.packVariable(byteBuffer, this.Data_Field.SimName, 1);
        this.packUUID(byteBuffer, this.Data_Field.SnapshotID);
        this.packFloat(byteBuffer, this.Data_Field.Dwell);
        this.packInt(byteBuffer, this.Data_Field.SalePrice);
        this.packInt(byteBuffer, this.Data_Field.AuctionID);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.Data_Field.ParcelID = this.unpackUUID(byteBuffer);
        this.Data_Field.OwnerID = this.unpackUUID(byteBuffer);
        this.Data_Field.Name = this.unpackVariable(byteBuffer, 1);
        this.Data_Field.Desc = this.unpackVariable(byteBuffer, 1);
        this.Data_Field.ActualArea = this.unpackInt(byteBuffer);
        this.Data_Field.BillableArea = this.unpackInt(byteBuffer);
        this.Data_Field.Flags = (this.unpackByte(byteBuffer) & 0xFF);
        this.Data_Field.GlobalX = this.unpackFloat(byteBuffer);
        this.Data_Field.GlobalY = this.unpackFloat(byteBuffer);
        this.Data_Field.GlobalZ = this.unpackFloat(byteBuffer);
        this.Data_Field.SimName = this.unpackVariable(byteBuffer, 1);
        this.Data_Field.SnapshotID = this.unpackUUID(byteBuffer);
        this.Data_Field.Dwell = this.unpackFloat(byteBuffer);
        this.Data_Field.SalePrice = this.unpackInt(byteBuffer);
        this.Data_Field.AuctionID = this.unpackInt(byteBuffer);
    }
    
    public static class AgentData
    {
        public UUID AgentID;
    }
    
    public static class Data
    {
        public int ActualArea;
        public int AuctionID;
        public int BillableArea;
        public byte[] Desc;
        public float Dwell;
        public int Flags;
        public float GlobalX;
        public float GlobalY;
        public float GlobalZ;
        public byte[] Name;
        public UUID OwnerID;
        public UUID ParcelID;
        public int SalePrice;
        public byte[] SimName;
        public UUID SnapshotID;
    }
}
