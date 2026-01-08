package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
import com.lumiyaviewer.lumiya.slproto.handler.SLMessageHandler
import com.lumiyaviewer.lumiya.slproto.types.UUID
import com.lumiyaviewer.lumiya.slproto.types.UUIDPool
import java.nio.ByteBuffer

class CreateInventoryItem : SLMessage {
    var AgentData_Field: AgentData = AgentData()
    var InventoryBlock_Field: InventoryBlock = InventoryBlock()

    class AgentData {
        var AgentID: UUID = UUIDPool.ZeroUUID
        var SessionID: UUID = UUIDPool.ZeroUUID
    }

    class InventoryBlock {
        var CallbackID: Int = 0
        var CreationDate: Int = 0
        var Desc: ByteArray = ByteArray(0)
        var EveryoneMask: Int = 0
        var Flags: Int = 0
        var FolderID: UUID = UUIDPool.ZeroUUID
        var GroupMask: Int = 0
        var InvType: Int = 0
        var Name: ByteArray = ByteArray(0)
        var NextOwnerMask: Int = 0
        var OwnerMask: Int = 0 // Renamed from OwnerMask to match packet spec usually, assuming OwnerMask
        var TransactionID: UUID = UUIDPool.ZeroUUID
        var Type: Int = 0
        var WearableType: Int = 0
    }

    init {
        this.zeroCoded = true
    }

    override fun CalcPayloadSize(): Int {
        return this.InventoryBlock_Field.Name.size + 67 + 1 + this.InventoryBlock_Field.Desc.size + 1 + 32
    }

    override fun Handle(sLMessageHandler: SLMessageHandler) {
        // sLMessageHandler.HandleCreateInventoryItem(this)
    }

    override fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put(0.toByte())
        byteBuffer.put(33.toByte())
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packInt(byteBuffer, this.InventoryBlock_Field.CallbackID)
        packUUID(byteBuffer, this.InventoryBlock_Field.FolderID)
        packUUID(byteBuffer, this.InventoryBlock_Field.TransactionID)
        packInt(byteBuffer, this.InventoryBlock_Field.NextOwnerMask)
        packByte(byteBuffer, this.InventoryBlock_Field.Type.toByte())
        packByte(byteBuffer, this.InventoryBlock_Field.InvType.toByte())
        packByte(byteBuffer, this.InventoryBlock_Field.WearableType.toByte())
        packVariable(byteBuffer, this.InventoryBlock_Field.Name, 1)
        packVariable(byteBuffer, this.InventoryBlock_Field.Desc, 1)
        packInt(byteBuffer, this.InventoryBlock_Field.CreationDate)
    }

    override fun UnpackPayload(byteBuffer: ByteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.InventoryBlock_Field.CallbackID = unpackInt(byteBuffer)
        this.InventoryBlock_Field.FolderID = unpackUUID(byteBuffer)
        this.InventoryBlock_Field.TransactionID = unpackUUID(byteBuffer)
        this.InventoryBlock_Field.NextOwnerMask = unpackInt(byteBuffer)
        this.InventoryBlock_Field.Type = unpackByte(byteBuffer).toInt()
        this.InventoryBlock_Field.InvType = unpackByte(byteBuffer).toInt()
        this.InventoryBlock_Field.WearableType = unpackByte(byteBuffer).toInt()
        this.InventoryBlock_Field.Name = unpackVariable(byteBuffer, 1)
        this.InventoryBlock_Field.Desc = unpackVariable(byteBuffer, 1)
        this.InventoryBlock_Field.CreationDate = unpackInt(byteBuffer)
    }
}
