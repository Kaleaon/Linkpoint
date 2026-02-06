package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3d;
import java.nio.ByteBuffer;
import java.util.UUID;
/* loaded from: classes.dex */
public class ClassifiedInfoUpdate extends SLMessage {
    public AgentData AgentData_Field;
    public Data Data_Field;

    /* loaded from: classes.dex */
    public static class AgentData {
        public UUID AgentID;
        public UUID SessionID;
    }

    /* loaded from: classes.dex */
    public static class Data {
        public int Category;
        public int ClassifiedFlags;
        public UUID ClassifiedID;
        public byte[] Desc;
        public byte[] Name;
        public UUID ParcelID;
        public int ParentEstate;
        public LLVector3d PosGlobal;
        public int PriceForListing;
        public UUID SnapshotID;
    }

    public ClassifiedInfoUpdate() {
        this.zeroCoded = false;
        this.AgentData_Field = new AgentData();
        this.Data_Field = new Data();
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public int CalcPayloadSize() {
        return this.Data_Field.Name.length + 21 + 2 + this.Data_Field.Desc.length + 16 + 4 + 16 + 24 + 1 + 4 + 36;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleClassifiedInfoUpdate(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort((short) -1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) 45);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.AgentData_Field.SessionID);
        packUUID(byteBuffer, this.Data_Field.ClassifiedID);
        packInt(byteBuffer, this.Data_Field.Category);
        packVariable(byteBuffer, this.Data_Field.Name, 1);
        packVariable(byteBuffer, this.Data_Field.Desc, 2);
        packUUID(byteBuffer, this.Data_Field.ParcelID);
        packInt(byteBuffer, this.Data_Field.ParentEstate);
        packUUID(byteBuffer, this.Data_Field.SnapshotID);
        packLLVector3d(byteBuffer, this.Data_Field.PosGlobal);
        packByte(byteBuffer, (byte) this.Data_Field.ClassifiedFlags);
        packInt(byteBuffer, this.Data_Field.PriceForListing);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer);
        this.Data_Field.ClassifiedID = unpackUUID(byteBuffer);
        this.Data_Field.Category = unpackInt(byteBuffer);
        this.Data_Field.Name = unpackVariable(byteBuffer, 1);
        this.Data_Field.Desc = unpackVariable(byteBuffer, 2);
        this.Data_Field.ParcelID = unpackUUID(byteBuffer);
        this.Data_Field.ParentEstate = unpackInt(byteBuffer);
        this.Data_Field.SnapshotID = unpackUUID(byteBuffer);
        this.Data_Field.PosGlobal = unpackLLVector3d(byteBuffer);
        this.Data_Field.ClassifiedFlags = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
        this.Data_Field.PriceForListing = unpackInt(byteBuffer);
    }
}
