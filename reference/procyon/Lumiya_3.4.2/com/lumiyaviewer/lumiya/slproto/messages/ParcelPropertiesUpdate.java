// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class ParcelPropertiesUpdate extends SLMessage
{
    public AgentData AgentData_Field;
    public ParcelData ParcelData_Field;
    
    public ParcelPropertiesUpdate() {
        this.zeroCoded = true;
        this.AgentData_Field = new AgentData();
        this.ParcelData_Field = new ParcelData();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.ParcelData_Field.Name.length + 17 + 1 + this.ParcelData_Field.Desc.length + 1 + this.ParcelData_Field.MusicURL.length + 1 + this.ParcelData_Field.MediaURL.length + 16 + 1 + 16 + 4 + 4 + 1 + 16 + 16 + 12 + 12 + 1 + 36;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleParcelPropertiesUpdate(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)(-58));
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packUUID(byteBuffer, this.AgentData_Field.SessionID);
        this.packInt(byteBuffer, this.ParcelData_Field.LocalID);
        this.packInt(byteBuffer, this.ParcelData_Field.Flags);
        this.packInt(byteBuffer, this.ParcelData_Field.ParcelFlags);
        this.packInt(byteBuffer, this.ParcelData_Field.SalePrice);
        this.packVariable(byteBuffer, this.ParcelData_Field.Name, 1);
        this.packVariable(byteBuffer, this.ParcelData_Field.Desc, 1);
        this.packVariable(byteBuffer, this.ParcelData_Field.MusicURL, 1);
        this.packVariable(byteBuffer, this.ParcelData_Field.MediaURL, 1);
        this.packUUID(byteBuffer, this.ParcelData_Field.MediaID);
        this.packByte(byteBuffer, (byte)this.ParcelData_Field.MediaAutoScale);
        this.packUUID(byteBuffer, this.ParcelData_Field.GroupID);
        this.packInt(byteBuffer, this.ParcelData_Field.PassPrice);
        this.packFloat(byteBuffer, this.ParcelData_Field.PassHours);
        this.packByte(byteBuffer, (byte)this.ParcelData_Field.Category);
        this.packUUID(byteBuffer, this.ParcelData_Field.AuthBuyerID);
        this.packUUID(byteBuffer, this.ParcelData_Field.SnapshotID);
        this.packLLVector3(byteBuffer, this.ParcelData_Field.UserLocation);
        this.packLLVector3(byteBuffer, this.ParcelData_Field.UserLookAt);
        this.packByte(byteBuffer, (byte)this.ParcelData_Field.LandingType);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = this.unpackUUID(byteBuffer);
        this.ParcelData_Field.LocalID = this.unpackInt(byteBuffer);
        this.ParcelData_Field.Flags = this.unpackInt(byteBuffer);
        this.ParcelData_Field.ParcelFlags = this.unpackInt(byteBuffer);
        this.ParcelData_Field.SalePrice = this.unpackInt(byteBuffer);
        this.ParcelData_Field.Name = this.unpackVariable(byteBuffer, 1);
        this.ParcelData_Field.Desc = this.unpackVariable(byteBuffer, 1);
        this.ParcelData_Field.MusicURL = this.unpackVariable(byteBuffer, 1);
        this.ParcelData_Field.MediaURL = this.unpackVariable(byteBuffer, 1);
        this.ParcelData_Field.MediaID = this.unpackUUID(byteBuffer);
        this.ParcelData_Field.MediaAutoScale = (this.unpackByte(byteBuffer) & 0xFF);
        this.ParcelData_Field.GroupID = this.unpackUUID(byteBuffer);
        this.ParcelData_Field.PassPrice = this.unpackInt(byteBuffer);
        this.ParcelData_Field.PassHours = this.unpackFloat(byteBuffer);
        this.ParcelData_Field.Category = (this.unpackByte(byteBuffer) & 0xFF);
        this.ParcelData_Field.AuthBuyerID = this.unpackUUID(byteBuffer);
        this.ParcelData_Field.SnapshotID = this.unpackUUID(byteBuffer);
        this.ParcelData_Field.UserLocation = this.unpackLLVector3(byteBuffer);
        this.ParcelData_Field.UserLookAt = this.unpackLLVector3(byteBuffer);
        this.ParcelData_Field.LandingType = (this.unpackByte(byteBuffer) & 0xFF);
    }
    
    public static class AgentData
    {
        public UUID AgentID;
        public UUID SessionID;
    }
    
    public static class ParcelData
    {
        public UUID AuthBuyerID;
        public int Category;
        public byte[] Desc;
        public int Flags;
        public UUID GroupID;
        public int LandingType;
        public int LocalID;
        public int MediaAutoScale;
        public UUID MediaID;
        public byte[] MediaURL;
        public byte[] MusicURL;
        public byte[] Name;
        public int ParcelFlags;
        public float PassHours;
        public int PassPrice;
        public int SalePrice;
        public UUID SnapshotID;
        public LLVector3 UserLocation;
        public LLVector3 UserLookAt;
    }
}
