package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.UUID;

public class RezObjectFromNotecard extends SLMessage {
    public AgentData AgentData_Field;
    public ArrayList<InventoryData> InventoryData_Fields = new ArrayList<>();
    public NotecardData NotecardData_Field;
    public RezData RezData_Field;

    public static class AgentData {
        public UUID AgentID;
        public UUID GroupID;
        public UUID SessionID;
    }

    public static class InventoryData {
        public UUID ItemID;
    }

    public static class NotecardData {
        public UUID NotecardItemID;
        public UUID ObjectID;
    }

    public static class RezData {
        public int BypassRaycast;
        public int EveryoneMask;
        public UUID FromTaskID;
        public int GroupMask;
        public int ItemFlags;
        public int NextOwnerMask;
        public LLVector3 RayEnd;
        public boolean RayEndIsIntersection;
        public LLVector3 RayStart;
        public UUID RayTargetID;
        public boolean RemoveItem;
        public boolean RezSelected;
    }

    public RezObjectFromNotecard() {
        this.zeroCoded = true;
        this.AgentData_Field = new AgentData();
        this.RezData_Field = new RezData();
        this.NotecardData_Field = new NotecardData();
    }

    public int CalcPayloadSize() {
        return (this.InventoryData_Fields.size() * 16) + 161;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleRezObjectFromNotecard(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) 38);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.AgentData_Field.SessionID);
        packUUID(byteBuffer, this.AgentData_Field.GroupID);
        packUUID(byteBuffer, this.RezData_Field.FromTaskID);
        packByte(byteBuffer, (byte) this.RezData_Field.BypassRaycast);
        packLLVector3(byteBuffer, this.RezData_Field.RayStart);
        packLLVector3(byteBuffer, this.RezData_Field.RayEnd);
        packUUID(byteBuffer, this.RezData_Field.RayTargetID);
        packBoolean(byteBuffer, this.RezData_Field.RayEndIsIntersection);
        packBoolean(byteBuffer, this.RezData_Field.RezSelected);
        packBoolean(byteBuffer, this.RezData_Field.RemoveItem);
        packInt(byteBuffer, this.RezData_Field.ItemFlags);
        packInt(byteBuffer, this.RezData_Field.GroupMask);
        packInt(byteBuffer, this.RezData_Field.EveryoneMask);
        packInt(byteBuffer, this.RezData_Field.NextOwnerMask);
        packUUID(byteBuffer, this.NotecardData_Field.NotecardItemID);
        packUUID(byteBuffer, this.NotecardData_Field.ObjectID);
        byteBuffer.put((byte) this.InventoryData_Fields.size());
        for (InventoryData inventoryData : this.InventoryData_Fields) {
            packUUID(byteBuffer, inventoryData.ItemID);
        }
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer);
        this.AgentData_Field.GroupID = unpackUUID(byteBuffer);
        this.RezData_Field.FromTaskID = unpackUUID(byteBuffer);
        this.RezData_Field.BypassRaycast = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
        this.RezData_Field.RayStart = unpackLLVector3(byteBuffer);
        this.RezData_Field.RayEnd = unpackLLVector3(byteBuffer);
        this.RezData_Field.RayTargetID = unpackUUID(byteBuffer);
        this.RezData_Field.RayEndIsIntersection = unpackBoolean(byteBuffer);
        this.RezData_Field.RezSelected = unpackBoolean(byteBuffer);
        this.RezData_Field.RemoveItem = unpackBoolean(byteBuffer);
        this.RezData_Field.ItemFlags = unpackInt(byteBuffer);
        this.RezData_Field.GroupMask = unpackInt(byteBuffer);
        this.RezData_Field.EveryoneMask = unpackInt(byteBuffer);
        this.RezData_Field.NextOwnerMask = unpackInt(byteBuffer);
        this.NotecardData_Field.NotecardItemID = unpackUUID(byteBuffer);
        this.NotecardData_Field.ObjectID = unpackUUID(byteBuffer);
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i = 0; i < b; i++) {
            InventoryData inventoryData = new InventoryData();
            inventoryData.ItemID = unpackUUID(byteBuffer);
            this.InventoryData_Fields.add(inventoryData);
        }
    }
}
