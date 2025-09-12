package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3d;
import java.nio.ByteBuffer;
import java.util.UUID;

public class ClassifiedInfoReply extends SLMessage {
    public AgentData AgentData_Field = new AgentData();
    public Data Data_Field = new Data();

    public static class AgentData {
        public UUID AgentID;
    }

    public static class Data {
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

    public ClassifiedInfoReply() {
        this.zeroCoded = false;
    }

    public int CalcPayloadSize() {
        return this.Data_Field.Name.length + 45 + 2 + this.Data_Field.Desc.length + 16 + 4 + 16 + 1 + this.Data_Field.SimName.length + 24 + 1 + this.Data_Field.ParcelName.length + 1 + 4 + 20;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleClassifiedInfoReply(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) 44);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.Data_Field.ClassifiedID);
        packUUID(byteBuffer, this.Data_Field.CreatorID);
        packInt(byteBuffer, this.Data_Field.CreationDate);
        packInt(byteBuffer, this.Data_Field.ExpirationDate);
        packInt(byteBuffer, this.Data_Field.Category);
        packVariable(byteBuffer, this.Data_Field.Name, 1);
        packVariable(byteBuffer, this.Data_Field.Desc, 2);
        packUUID(byteBuffer, this.Data_Field.ParcelID);
        packInt(byteBuffer, this.Data_Field.ParentEstate);
        packUUID(byteBuffer, this.Data_Field.SnapshotID);
        packVariable(byteBuffer, this.Data_Field.SimName, 1);
        packLLVector3d(byteBuffer, this.Data_Field.PosGlobal);
        packVariable(byteBuffer, this.Data_Field.ParcelName, 1);
        packByte(byteBuffer, (byte) this.Data_Field.ClassifiedFlags);
        packInt(byteBuffer, this.Data_Field.PriceForListing);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.Data_Field.ClassifiedID = unpackUUID(byteBuffer);
        this.Data_Field.CreatorID = unpackUUID(byteBuffer);
        this.Data_Field.CreationDate = unpackInt(byteBuffer);
        this.Data_Field.ExpirationDate = unpackInt(byteBuffer);
        this.Data_Field.Category = unpackInt(byteBuffer);
        this.Data_Field.Name = unpackVariable(byteBuffer, 1);
        this.Data_Field.Desc = unpackVariable(byteBuffer, 2);
        this.Data_Field.ParcelID = unpackUUID(byteBuffer);
        this.Data_Field.ParentEstate = unpackInt(byteBuffer);
        this.Data_Field.SnapshotID = unpackUUID(byteBuffer);
        this.Data_Field.SimName = unpackVariable(byteBuffer, 1);
        this.Data_Field.PosGlobal = unpackLLVector3d(byteBuffer);
        this.Data_Field.ParcelName = unpackVariable(byteBuffer, 1);
        this.Data_Field.ClassifiedFlags = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
        this.Data_Field.PriceForListing = unpackInt(byteBuffer);
    }
}
