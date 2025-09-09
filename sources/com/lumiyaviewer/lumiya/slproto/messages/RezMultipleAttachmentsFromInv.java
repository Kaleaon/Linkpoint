package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

public class RezMultipleAttachmentsFromInv extends SLMessage {
    public AgentData AgentData_Field;
    public HeaderData HeaderData_Field;
    public ArrayList<ObjectData> ObjectData_Fields = new ArrayList<>();

    public static class AgentData {
        public UUID AgentID;
        public UUID SessionID;
    }

    public static class HeaderData {
        public UUID CompoundMsgID;
        public boolean FirstDetachAll;
        public int TotalObjects;
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

    public RezMultipleAttachmentsFromInv() {
        this.zeroCoded = true;
        this.AgentData_Field = new AgentData();
        this.HeaderData_Field = new HeaderData();
    }

    public int CalcPayloadSize() {
        int i = 55;
        Iterator<T> it = this.ObjectData_Fields.iterator();
        while (true) {
            int i2 = i;
            if (!it.hasNext()) {
                return i2;
            }
            ObjectData objectData = (ObjectData) it.next();
            i = objectData.Description.length + objectData.Name.length + 50 + 1 + i2;
        }
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleRezMultipleAttachmentsFromInv(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) -116);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.AgentData_Field.SessionID);
        packUUID(byteBuffer, this.HeaderData_Field.CompoundMsgID);
        packByte(byteBuffer, (byte) this.HeaderData_Field.TotalObjects);
        packBoolean(byteBuffer, this.HeaderData_Field.FirstDetachAll);
        byteBuffer.put((byte) this.ObjectData_Fields.size());
        for (ObjectData objectData : this.ObjectData_Fields) {
            packUUID(byteBuffer, objectData.ItemID);
            packUUID(byteBuffer, objectData.OwnerID);
            packByte(byteBuffer, (byte) objectData.AttachmentPt);
            packInt(byteBuffer, objectData.ItemFlags);
            packInt(byteBuffer, objectData.GroupMask);
            packInt(byteBuffer, objectData.EveryoneMask);
            packInt(byteBuffer, objectData.NextOwnerMask);
            packVariable(byteBuffer, objectData.Name, 1);
            packVariable(byteBuffer, objectData.Description, 1);
        }
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer);
        this.HeaderData_Field.CompoundMsgID = unpackUUID(byteBuffer);
        this.HeaderData_Field.TotalObjects = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
        this.HeaderData_Field.FirstDetachAll = unpackBoolean(byteBuffer);
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i = 0; i < b; i++) {
            ObjectData objectData = new ObjectData();
            objectData.ItemID = unpackUUID(byteBuffer);
            objectData.OwnerID = unpackUUID(byteBuffer);
            objectData.AttachmentPt = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
            objectData.ItemFlags = unpackInt(byteBuffer);
            objectData.GroupMask = unpackInt(byteBuffer);
            objectData.EveryoneMask = unpackInt(byteBuffer);
            objectData.NextOwnerMask = unpackInt(byteBuffer);
            objectData.Name = unpackVariable(byteBuffer, 1);
            objectData.Description = unpackVariable(byteBuffer, 1);
            this.ObjectData_Fields.add(objectData);
        }
    }
}
