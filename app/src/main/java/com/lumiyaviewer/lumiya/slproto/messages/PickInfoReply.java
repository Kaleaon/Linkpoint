package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3d;
import java.nio.ByteBuffer;
import java.util.UUID;

public class PickInfoReply extends SLMessage {
    public AgentData AgentData_Field = new AgentData();
    public Data Data_Field = new Data();

    public static class AgentData {
        public UUID AgentID;
    }

    public static class Data {
        public UUID CreatorID;
        public byte[] Desc;
        public boolean Enabled;
        public byte[] Name;
        public byte[] OriginalName;
        public UUID ParcelID;
        public UUID PickID;
        public LLVector3d PosGlobal;
        public byte[] SimName;
        public UUID SnapshotID;
        public int SortOrder;
        public boolean TopPick;
        public byte[] User;
    }

    public PickInfoReply() {
        this.zeroCoded = false;
    }

    public int CalcPayloadSize() {
        return this.Data_Field.Name.length + 50 + 2 + this.Data_Field.Desc.length + 16 + 1 + this.Data_Field.User.length + 1 + this.Data_Field.OriginalName.length + 1 + this.Data_Field.SimName.length + 24 + 4 + 1 + 20;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandlePickInfoReply(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) -72);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.Data_Field.PickID);
        packUUID(byteBuffer, this.Data_Field.CreatorID);
        packBoolean(byteBuffer, this.Data_Field.TopPick);
        packUUID(byteBuffer, this.Data_Field.ParcelID);
        packVariable(byteBuffer, this.Data_Field.Name, 1);
        packVariable(byteBuffer, this.Data_Field.Desc, 2);
        packUUID(byteBuffer, this.Data_Field.SnapshotID);
        packVariable(byteBuffer, this.Data_Field.User, 1);
        packVariable(byteBuffer, this.Data_Field.OriginalName, 1);
        packVariable(byteBuffer, this.Data_Field.SimName, 1);
        packLLVector3d(byteBuffer, this.Data_Field.PosGlobal);
        packInt(byteBuffer, this.Data_Field.SortOrder);
        packBoolean(byteBuffer, this.Data_Field.Enabled);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.Data_Field.PickID = unpackUUID(byteBuffer);
        this.Data_Field.CreatorID = unpackUUID(byteBuffer);
        this.Data_Field.TopPick = unpackBoolean(byteBuffer);
        this.Data_Field.ParcelID = unpackUUID(byteBuffer);
        this.Data_Field.Name = unpackVariable(byteBuffer, 1);
        this.Data_Field.Desc = unpackVariable(byteBuffer, 2);
        this.Data_Field.SnapshotID = unpackUUID(byteBuffer);
        this.Data_Field.User = unpackVariable(byteBuffer, 1);
        this.Data_Field.OriginalName = unpackVariable(byteBuffer, 1);
        this.Data_Field.SimName = unpackVariable(byteBuffer, 1);
        this.Data_Field.PosGlobal = unpackLLVector3d(byteBuffer);
        this.Data_Field.SortOrder = unpackInt(byteBuffer);
        this.Data_Field.Enabled = unpackBoolean(byteBuffer);
    }
}
