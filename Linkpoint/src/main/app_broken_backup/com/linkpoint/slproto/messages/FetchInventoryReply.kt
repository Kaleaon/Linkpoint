package com.linkpoint.slproto.messages

import com.google.common.base.Ascii
import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.Iterator
import java.util.UUID

class FetchInventoryReply : SLMessage {
    AgentData AgentData_Field
    ArrayList<InventoryData> InventoryData_Fields = ArrayList<>()

    class AgentData {
        UUID AgentID
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

    FetchInventoryReply() {
        this.zeroCoded = true
        this.AgentData_Field = AgentData()
    }

    fun CalcPayloadSize(): Int {
        var i: Int = 21
        Iterator<T> it = this.InventoryData_Fields.iterator()
        while (true) {
            var i2: Int = i
            if (!it.hasNext()) {
                return i2
            }
            InventoryData inventoryData = (it as InventoryData).next()
            i = inventoryData.Description.size + inventoryData.Name.size + 129 + 1 + 4 + 4 + i2
        }
    }

    fun Handle(SLMessageHandler sLMessageHandler)  {
        sLMessageHandler.HandleFetchInventoryReply(this)
    }

    fun PackPayload(ByteBuffer byteBuffer)  {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 1)
        byteBuffer.put(Ascii.CAN)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        byteBuffer.put((this as byte).InventoryData_Fields.size())
        for (InventoryData inventoryData : this.InventoryData_Fields) {
            packUUID(byteBuffer, inventoryData.ItemID)
            packUUID(byteBuffer, inventoryData.FolderID)
            packUUID(byteBuffer, inventoryData.CreatorID)
            packUUID(byteBuffer, inventoryData.OwnerID)
            packUUID(byteBuffer, inventoryData.GroupID)
            packInt(byteBuffer, inventoryData.BaseMask)
            packInt(byteBuffer, inventoryData.OwnerMask)
            packInt(byteBuffer, inventoryData.GroupMask)
            packInt(byteBuffer, inventoryData.EveryoneMask)
            packInt(byteBuffer, inventoryData.NextOwnerMask)
            packBoolean(byteBuffer, inventoryData.GroupOwned)
            packUUID(byteBuffer, inventoryData.AssetID)
            packByte(byteBuffer, (inventoryData as byte).Type)
            packByte(byteBuffer, (inventoryData as byte).InvType)
            packInt(byteBuffer, inventoryData.Flags)
            packByte(byteBuffer, (inventoryData as byte).SaleType)
            packInt(byteBuffer, inventoryData.SalePrice)
            packVariable(byteBuffer, inventoryData.Name, 1)
            packVariable(byteBuffer, inventoryData.Description, 1)
            packInt(byteBuffer, inventoryData.CreationDate)
            packInt(byteBuffer, inventoryData.CRC)
        }
    }

    fun UnpackPayload(ByteBuffer byteBuffer)  {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (i in 0 until b) {
            InventoryData inventoryData = InventoryData()
            inventoryData.ItemID = unpackUUID(byteBuffer)
            inventoryData.FolderID = unpackUUID(byteBuffer)
            inventoryData.CreatorID = unpackUUID(byteBuffer)
            inventoryData.OwnerID = unpackUUID(byteBuffer)
            inventoryData.GroupID = unpackUUID(byteBuffer)
            inventoryData.BaseMask = unpackInt(byteBuffer)
            inventoryData.OwnerMask = unpackInt(byteBuffer)
            inventoryData.GroupMask = unpackInt(byteBuffer)
            inventoryData.EveryoneMask = unpackInt(byteBuffer)
            inventoryData.NextOwnerMask = unpackInt(byteBuffer)
            inventoryData.GroupOwned = unpackBoolean(byteBuffer)
            inventoryData.AssetID = unpackUUID(byteBuffer)
            inventoryData.Type = unpackByte(byteBuffer)
            inventoryData.InvType = unpackByte(byteBuffer)
            inventoryData.Flags = unpackInt(byteBuffer)
            inventoryData.SaleType = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
            inventoryData.SalePrice = unpackInt(byteBuffer)
            inventoryData.Name = unpackVariable(byteBuffer, 1)
            inventoryData.Description = unpackVariable(byteBuffer, 1)
            inventoryData.CreationDate = unpackInt(byteBuffer)
            inventoryData.CRC = unpackInt(byteBuffer)
            this.InventoryData_Fields.add(inventoryData)
        }
    }
}
