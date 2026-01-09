package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class UpdateAttachment : SLMessage {
    AgentData AgentData_Field = AgentData()
    AttachmentBlock AttachmentBlock_Field = AttachmentBlock()
    InventoryData InventoryData_Field = InventoryData()
    OperationData OperationData_Field = OperationData()

    class AgentData {
        UUID AgentID
        UUID SessionID
    }

    class AttachmentBlock {
        Int AttachmentPoint
    }

    class InventoryData {
        UUID AssetID
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
        Int Type
    }

    class OperationData {
        Boolean AddItem
        Boolean UseExistingAsset
    }

    UpdateAttachment() {
        this.zeroCoded = true
    }

    fun CalcPayloadSize(): Int {
        return this.InventoryData_Field.Name.size + 129 + 1 + this.InventoryData_Field.Description.size + 4 + 4 + 39
    }

    fun Handle(SLMessageHandler sLMessageHandler)  {
        sLMessageHandler.HandleUpdateAttachment(this)
    }

    fun PackPayload(ByteBuffer byteBuffer)  {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) 75)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packByte(byteBuffer, (this as Byte).AttachmentBlock_Field.AttachmentPoint)
        packBoolean(byteBuffer, this.OperationData_Field.AddItem)
        packBoolean(byteBuffer, this.OperationData_Field.UseExistingAsset)
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
        packUUID(byteBuffer, this.InventoryData_Field.AssetID)
        packByte(byteBuffer, (this as Byte).InventoryData_Field.Type)
        packByte(byteBuffer, (this as Byte).InventoryData_Field.InvType)
        packInt(byteBuffer, this.InventoryData_Field.Flags)
        packByte(byteBuffer, (this as Byte).InventoryData_Field.SaleType)
        packInt(byteBuffer, this.InventoryData_Field.SalePrice)
        packVariable(byteBuffer, this.InventoryData_Field.Name, 1)
        packVariable(byteBuffer, this.InventoryData_Field.Description, 1)
        packInt(byteBuffer, this.InventoryData_Field.CreationDate)
        packInt(byteBuffer, this.InventoryData_Field.CRC)
    }

    fun UnpackPayload(ByteBuffer byteBuffer)  {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.AttachmentBlock_Field.AttachmentPoint = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
        this.OperationData_Field.AddItem = unpackBoolean(byteBuffer)
        this.OperationData_Field.UseExistingAsset = unpackBoolean(byteBuffer)
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
        this.InventoryData_Field.AssetID = unpackUUID(byteBuffer)
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
