package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.types.LLVector3
import java.nio.ByteBuffer
import java.util.UUID

class RezObject : SLMessage() {
    public AgentData AgentData_Field = AgentData()
    public InventoryData InventoryData_Field = InventoryData()
    public RezData RezData_Field = RezData()

    @JvmStatic
    class AgentData {
        public UUID AgentID
        public UUID GroupID
        public UUID SessionID
    }

    @JvmStatic
    class InventoryData {
        public Int BaseMask
        public Int CRC
        public Int CreationDate
        public UUID CreatorID
        public Byte[] Description
        public Int EveryoneMask
        public Int Flags
        public UUID FolderID
        public UUID GroupID
        public Int GroupMask
        public Boolean GroupOwned
        public Int InvType
        public UUID ItemID
        public Byte[] Name
        public Int NextOwnerMask
        public UUID OwnerID
        public Int OwnerMask
        public Int SalePrice
        public Int SaleType
        public UUID TransactionID
        public Int Type
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

    public RezObject() {
        this.zeroCoded = true
    }

    public Int CalcPayloadSize() {
        return this.InventoryData_Field.Name.length + 129 + 1 + this.InventoryData_Field.Description.length + 4 + 4 + 128
    }

    public Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleRezObject(this)
    }

    public Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) 37)
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
        packUUID(byteBuffer, this.InventoryData_Field.ItemID)
        packUUID(byteBuffer, this.InventoryData_Field.FolderID)
        packUUID(byteBuffer, this.InventoryData_Field.CreatorID)
        packUUID(byteBuffer, this.InventoryData_Field.OwnerID)
        packUUID(byteBuffer, this.InventoryData_Field.GroupID)
        packInt(byteBuffer, this.InventoryData_Field.BaseMask)
        packInt(byteBuffer, this.InventoryData_Field.OwnerMask)
        packInt(byteBuffer, this.InventoryData_Field.GroupMask)
        packInt(byteBuffer, this.InventoryData_Field.EveryoneMask)
        packInt(byteBuffer, this.InventoryData_Field.NextOwnerMask)
        packBoolean(byteBuffer, this.InventoryData_Field.GroupOwned)
        packUUID(byteBuffer, this.InventoryData_Field.TransactionID)
        packByte(byteBuffer, (Byte) this.InventoryData_Field.Type)
        packByte(byteBuffer, (Byte) this.InventoryData_Field.InvType)
        packInt(byteBuffer, this.InventoryData_Field.Flags)
        packByte(byteBuffer, (Byte) this.InventoryData_Field.SaleType)
        packInt(byteBuffer, this.InventoryData_Field.SalePrice)
        packVariable(byteBuffer, this.InventoryData_Field.Name, 1)
        packVariable(byteBuffer, this.InventoryData_Field.Description, 1)
        packInt(byteBuffer, this.InventoryData_Field.CreationDate)
        packInt(byteBuffer, this.InventoryData_Field.CRC)
    }

    public Unit UnpackPayload(ByteBuffer byteBuffer) {
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
        this.InventoryData_Field.ItemID = unpackUUID(byteBuffer)
        this.InventoryData_Field.FolderID = unpackUUID(byteBuffer)
        this.InventoryData_Field.CreatorID = unpackUUID(byteBuffer)
        this.InventoryData_Field.OwnerID = unpackUUID(byteBuffer)
        this.InventoryData_Field.GroupID = unpackUUID(byteBuffer)
        this.InventoryData_Field.BaseMask = unpackInt(byteBuffer)
        this.InventoryData_Field.OwnerMask = unpackInt(byteBuffer)
        this.InventoryData_Field.GroupMask = unpackInt(byteBuffer)
        this.InventoryData_Field.EveryoneMask = unpackInt(byteBuffer)
        this.InventoryData_Field.NextOwnerMask = unpackInt(byteBuffer)
        this.InventoryData_Field.GroupOwned = unpackBoolean(byteBuffer)
        this.InventoryData_Field.TransactionID = unpackUUID(byteBuffer)
        this.InventoryData_Field.Type = unpackByte(byteBuffer)
        this.InventoryData_Field.InvType = unpackByte(byteBuffer)
        this.InventoryData_Field.Flags = unpackInt(byteBuffer)
        this.InventoryData_Field.SaleType = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
        this.InventoryData_Field.SalePrice = unpackInt(byteBuffer)
        this.InventoryData_Field.Name = unpackVariable(byteBuffer, 1)
        this.InventoryData_Field.Description = unpackVariable(byteBuffer, 1)
        this.InventoryData_Field.CreationDate = unpackInt(byteBuffer)
        this.InventoryData_Field.CRC = unpackInt(byteBuffer)
    }
}
