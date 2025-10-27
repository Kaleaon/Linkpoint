package com.lumiyaviewer.lumiya.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.lumiyaviewer.lumiya.slproto.SLMessage
import com.lumiyaviewer.lumiya.slproto.types.LLVector3
import java.nio.ByteBuffer
import java.util.UUID

class RezObject : SLMessage {
    AgentData AgentData_Field = AgentData()
    InventoryData InventoryData_Field = InventoryData()
    RezData RezData_Field = RezData()

    class AgentData {
        UUID AgentID
        UUID GroupID
        UUID SessionID
    }

    class InventoryData {
        Int BaseMask
        Int CRC
        Int CreationDate
        UUID CreatorID
        ByteArray Description
        Int EveryoneMask
        Int Flags
        UUID FolderID
        UUID GroupID
        Int GroupMask
        Boolean GroupOwned
        Int InvType
        UUID ItemID
        ByteArray Name
        Int NextOwnerMask
        UUID OwnerID
        Int OwnerMask
        Int SalePrice
        Int SaleType
        UUID TransactionID
        Int Type
    }

    class RezData {
        Int BypassRaycast
        Int EveryoneMask
        UUID FromTaskID
        Int GroupMask
        Int ItemFlags
        Int NextOwnerMask
        LLVector3 RayEnd
        Boolean RayEndIsIntersection
        LLVector3 RayStart
        UUID RayTargetID
        Boolean RemoveItem
        Boolean RezSelected
    }

    RezObject() {
        this.zeroCoded = true
    }

    Int CalcPayloadSize() {
        return this.InventoryData_Field.Name.length + 129 + 1 + this.InventoryData_Field.Description.length + 4 + 4 + 128
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleRezObject(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
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

    Unit UnpackPayload(ByteBuffer byteBuffer) {
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
