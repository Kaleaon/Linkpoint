package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.nio.ByteBuffer;
import java.util.UUID;

public class ParcelPropertiesUpdate extends SLMessage {
    public AgentData AgentData_Field = new AgentData();
    public ParcelData ParcelData_Field = new ParcelData();

    public static class AgentData {
        public UUID AgentID;
        public UUID SessionID;
    }

    public static class ParcelData {
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

    public ParcelPropertiesUpdate() {
        this.zeroCoded = true;
    }

    public int CalcPayloadSize() {
        return this.ParcelData_Field.Name.length + 17 + 1 + this.ParcelData_Field.Desc.length + 1 + this.ParcelData_Field.MusicURL.length + 1 + this.ParcelData_Field.MediaURL.length + 16 + 1 + 16 + 4 + 4 + 1 + 16 + 16 + 12 + 12 + 1 + 36;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleParcelPropertiesUpdate(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) -58);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.AgentData_Field.SessionID);
        packInt(byteBuffer, this.ParcelData_Field.LocalID);
        packInt(byteBuffer, this.ParcelData_Field.Flags);
        packInt(byteBuffer, this.ParcelData_Field.ParcelFlags);
        packInt(byteBuffer, this.ParcelData_Field.SalePrice);
        packVariable(byteBuffer, this.ParcelData_Field.Name, 1);
        packVariable(byteBuffer, this.ParcelData_Field.Desc, 1);
        packVariable(byteBuffer, this.ParcelData_Field.MusicURL, 1);
        packVariable(byteBuffer, this.ParcelData_Field.MediaURL, 1);
        packUUID(byteBuffer, this.ParcelData_Field.MediaID);
        packByte(byteBuffer, (byte) this.ParcelData_Field.MediaAutoScale);
        packUUID(byteBuffer, this.ParcelData_Field.GroupID);
        packInt(byteBuffer, this.ParcelData_Field.PassPrice);
        packFloat(byteBuffer, this.ParcelData_Field.PassHours);
        packByte(byteBuffer, (byte) this.ParcelData_Field.Category);
        packUUID(byteBuffer, this.ParcelData_Field.AuthBuyerID);
        packUUID(byteBuffer, this.ParcelData_Field.SnapshotID);
        packLLVector3(byteBuffer, this.ParcelData_Field.UserLocation);
        packLLVector3(byteBuffer, this.ParcelData_Field.UserLookAt);
        packByte(byteBuffer, (byte) this.ParcelData_Field.LandingType);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer);
        this.ParcelData_Field.LocalID = unpackInt(byteBuffer);
        this.ParcelData_Field.Flags = unpackInt(byteBuffer);
        this.ParcelData_Field.ParcelFlags = unpackInt(byteBuffer);
        this.ParcelData_Field.SalePrice = unpackInt(byteBuffer);
        this.ParcelData_Field.Name = unpackVariable(byteBuffer, 1);
        this.ParcelData_Field.Desc = unpackVariable(byteBuffer, 1);
        this.ParcelData_Field.MusicURL = unpackVariable(byteBuffer, 1);
        this.ParcelData_Field.MediaURL = unpackVariable(byteBuffer, 1);
        this.ParcelData_Field.MediaID = unpackUUID(byteBuffer);
        this.ParcelData_Field.MediaAutoScale = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
        this.ParcelData_Field.GroupID = unpackUUID(byteBuffer);
        this.ParcelData_Field.PassPrice = unpackInt(byteBuffer);
        this.ParcelData_Field.PassHours = unpackFloat(byteBuffer);
        this.ParcelData_Field.Category = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
        this.ParcelData_Field.AuthBuyerID = unpackUUID(byteBuffer);
        this.ParcelData_Field.SnapshotID = unpackUUID(byteBuffer);
        this.ParcelData_Field.UserLocation = unpackLLVector3(byteBuffer);
        this.ParcelData_Field.UserLookAt = unpackLLVector3(byteBuffer);
        this.ParcelData_Field.LandingType = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
    }
}
