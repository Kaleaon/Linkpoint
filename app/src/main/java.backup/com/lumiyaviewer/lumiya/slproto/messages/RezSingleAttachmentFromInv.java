package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

public class RezSingleAttachmentFromInv extends SLMessage {
    public AgentData AgentData_Field = new AgentData();
    public ObjectData ObjectData_Field = new ObjectData();

    public static class AgentData {
        public UUID AgentID;
        public UUID SessionID;
    }

    public static class ObjectData {
        public int AttachmentPt;
        public byte[] Description;
        public int EveryoneMask;
        public int GroupMask;
        public int ItemFlags;
        public UUID ItemID;
        public byte[] Name;
        public int NextOwnerMask;
        public UUID OwnerID;
    }

    public RezSingleAttachmentFromInv() {
        this.zeroCoded = true;
    }

    public int CalcPayloadSize() {
        return this.ObjectData_Field.Name.length + 50 + 1 + this.ObjectData_Field.Description.length + 36;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleRezSingleAttachmentFromInv(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) -117);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.AgentData_Field.SessionID);
        packUUID(byteBuffer, this.ObjectData_Field.ItemID);
        packUUID(byteBuffer, this.ObjectData_Field.OwnerID);
        packByte(byteBuffer, (byte) this.ObjectData_Field.AttachmentPt);
        packInt(byteBuffer, this.ObjectData_Field.ItemFlags);
        packInt(byteBuffer, this.ObjectData_Field.GroupMask);
        packInt(byteBuffer, this.ObjectData_Field.EveryoneMask);
        packInt(byteBuffer, this.ObjectData_Field.NextOwnerMask);
        packVariable(byteBuffer, this.ObjectData_Field.Name, 1);
        packVariable(byteBuffer, this.ObjectData_Field.Description, 1);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer);
        this.ObjectData_Field.ItemID = unpackUUID(byteBuffer);
        this.ObjectData_Field.OwnerID = unpackUUID(byteBuffer);
        this.ObjectData_Field.AttachmentPt = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
        this.ObjectData_Field.ItemFlags = unpackInt(byteBuffer);
        this.ObjectData_Field.GroupMask = unpackInt(byteBuffer);
        this.ObjectData_Field.EveryoneMask = unpackInt(byteBuffer);
        this.ObjectData_Field.NextOwnerMask = unpackInt(byteBuffer);
        this.ObjectData_Field.Name = unpackVariable(byteBuffer, 1);
        this.ObjectData_Field.Description = unpackVariable(byteBuffer, 1);
    }
}
