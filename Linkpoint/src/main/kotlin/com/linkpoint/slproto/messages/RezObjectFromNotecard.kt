package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.types.LLVector3
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.UUID

class RezObjectFromNotecard : SLMessage() {
    public AgentData AgentData_Field
    public ArrayList<InventoryData> InventoryData_Fields = ArrayList<>()
    public NotecardData NotecardData_Field
    public RezData RezData_Field

    @JvmStatic
    class AgentData {
        public UUID AgentID
        public UUID GroupID
        public UUID SessionID
    }

    @JvmStatic
    class InventoryData {
        public UUID ItemID
    }

    @JvmStatic
    class NotecardData {
        public UUID NotecardItemID
        public UUID ObjectID
    }

    @JvmStatic
    class RezData {
        public Int BypassRaycast
        public Int EveryoneMask
        public UUID FromTaskID
        public Int GroupMask
        public Int ItemFlags
        public Int NextOwnerMask
        public LLVector3 RayEnd
        public Boolean RayEndIsIntersection
        public LLVector3 RayStart
        public UUID RayTargetID
        public Boolean RemoveItem
        public Boolean RezSelected
    }

    public RezObjectFromNotecard() {
        this.zeroCoded = true
        this.AgentData_Field = AgentData()
        this.RezData_Field = RezData()
        this.NotecardData_Field = NotecardData()
    }

    public Int CalcPayloadSize() {
        return (this.InventoryData_Fields.size() * 16) + 161
    }

    fun Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleRezObjectFromNotecard(this)
    }

    fun PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) 38)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packUUID(byteBuffer, this.AgentData_Field.GroupID)
        packUUID(byteBuffer, this.RezData_Field.FromTaskID)
        packByte(byteBuffer, (Byte) this.RezData_Field.BypassRaycast)
        packLLVector3(byteBuffer, this.RezData_Field.RayStart)
        packLLVector3(byteBuffer, this.RezData_Field.RayEnd)
        packUUID(byteBuffer, this.RezData_Field.RayTargetID)
        packBoolean(byteBuffer, this.RezData_Field.RayEndIsIntersection)
        packBoolean(byteBuffer, this.RezData_Field.RezSelected)
        packBoolean(byteBuffer, this.RezData_Field.RemoveItem)
        packInt(byteBuffer, this.RezData_Field.ItemFlags)
        packInt(byteBuffer, this.RezData_Field.GroupMask)
        packInt(byteBuffer, this.RezData_Field.EveryoneMask)
        packInt(byteBuffer, this.RezData_Field.NextOwnerMask)
        packUUID(byteBuffer, this.NotecardData_Field.NotecardItemID)
        packUUID(byteBuffer, this.NotecardData_Field.ObjectID)
        byteBuffer.put((Byte) this.InventoryData_Fields.size())
        for (InventoryData inventoryData : this.InventoryData_Fields) {
            packUUID(byteBuffer, inventoryData.ItemID)
        }
    }

    fun UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.AgentData_Field.GroupID = unpackUUID(byteBuffer)
        this.RezData_Field.FromTaskID = unpackUUID(byteBuffer)
        this.RezData_Field.BypassRaycast = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
        this.RezData_Field.RayStart = unpackLLVector3(byteBuffer)
        this.RezData_Field.RayEnd = unpackLLVector3(byteBuffer)
        this.RezData_Field.RayTargetID = unpackUUID(byteBuffer)
        this.RezData_Field.RayEndIsIntersection = unpackBoolean(byteBuffer)
        this.RezData_Field.RezSelected = unpackBoolean(byteBuffer)
        this.RezData_Field.RemoveItem = unpackBoolean(byteBuffer)
        this.RezData_Field.ItemFlags = unpackInt(byteBuffer)
        this.RezData_Field.GroupMask = unpackInt(byteBuffer)
        this.RezData_Field.EveryoneMask = unpackInt(byteBuffer)
        this.RezData_Field.NextOwnerMask = unpackInt(byteBuffer)
        this.NotecardData_Field.NotecardItemID = unpackUUID(byteBuffer)
        this.NotecardData_Field.ObjectID = unpackUUID(byteBuffer)
        Byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            InventoryData inventoryData = InventoryData()
            inventoryData.ItemID = unpackUUID(byteBuffer)
            this.InventoryData_Fields.add(inventoryData)
        }
    }
}
