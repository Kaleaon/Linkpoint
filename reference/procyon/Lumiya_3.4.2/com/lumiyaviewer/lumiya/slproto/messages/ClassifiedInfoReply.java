// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.types.LLVector3d;
import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class ClassifiedInfoReply extends SLMessage
{
    public AgentData AgentData_Field;
    public Data Data_Field;
    
    public ClassifiedInfoReply() {
        this.zeroCoded = false;
        this.AgentData_Field = new AgentData();
        this.Data_Field = new Data();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.Data_Field.Name.length + 45 + 2 + this.Data_Field.Desc.length + 16 + 4 + 16 + 1 + this.Data_Field.SimName.length + 24 + 1 + this.Data_Field.ParcelName.length + 1 + 4 + 20;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleClassifiedInfoReply(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)44);
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packUUID(byteBuffer, this.Data_Field.ClassifiedID);
        this.packUUID(byteBuffer, this.Data_Field.CreatorID);
        this.packInt(byteBuffer, this.Data_Field.CreationDate);
        this.packInt(byteBuffer, this.Data_Field.ExpirationDate);
        this.packInt(byteBuffer, this.Data_Field.Category);
        this.packVariable(byteBuffer, this.Data_Field.Name, 1);
        this.packVariable(byteBuffer, this.Data_Field.Desc, 2);
        this.packUUID(byteBuffer, this.Data_Field.ParcelID);
        this.packInt(byteBuffer, this.Data_Field.ParentEstate);
        this.packUUID(byteBuffer, this.Data_Field.SnapshotID);
        this.packVariable(byteBuffer, this.Data_Field.SimName, 1);
        this.packLLVector3d(byteBuffer, this.Data_Field.PosGlobal);
        this.packVariable(byteBuffer, this.Data_Field.ParcelName, 1);
        this.packByte(byteBuffer, (byte)this.Data_Field.ClassifiedFlags);
        this.packInt(byteBuffer, this.Data_Field.PriceForListing);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.Data_Field.ClassifiedID = this.unpackUUID(byteBuffer);
        this.Data_Field.CreatorID = this.unpackUUID(byteBuffer);
        this.Data_Field.CreationDate = this.unpackInt(byteBuffer);
        this.Data_Field.ExpirationDate = this.unpackInt(byteBuffer);
        this.Data_Field.Category = this.unpackInt(byteBuffer);
        this.Data_Field.Name = this.unpackVariable(byteBuffer, 1);
        this.Data_Field.Desc = this.unpackVariable(byteBuffer, 2);
        this.Data_Field.ParcelID = this.unpackUUID(byteBuffer);
        this.Data_Field.ParentEstate = this.unpackInt(byteBuffer);
        this.Data_Field.SnapshotID = this.unpackUUID(byteBuffer);
        this.Data_Field.SimName = this.unpackVariable(byteBuffer, 1);
        this.Data_Field.PosGlobal = this.unpackLLVector3d(byteBuffer);
        this.Data_Field.ParcelName = this.unpackVariable(byteBuffer, 1);
        this.Data_Field.ClassifiedFlags = (this.unpackByte(byteBuffer) & 0xFF);
        this.Data_Field.PriceForListing = this.unpackInt(byteBuffer);
    }
    
    public static class AgentData
    {
        public UUID AgentID;
    }
    
    public static class Data
    {
        public int Category;
        public int ClassifiedFlags;
        public UUID ClassifiedID;
        public int CreationDate;
        public UUID CreatorID;
        public byte[] Desc;
        public int ExpirationDate;
        public byte[] Name;
        public UUID ParcelID;
        public byte[] ParcelName;
        public int ParentEstate;
        public LLVector3d PosGlobal;
        public int PriceForListing;
        public byte[] SimName;
        public UUID SnapshotID;
    }
}
