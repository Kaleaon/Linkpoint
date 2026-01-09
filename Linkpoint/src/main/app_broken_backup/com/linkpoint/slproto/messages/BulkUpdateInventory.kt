package com.linkpoint.slproto.messages

import com.google.common.base.Ascii
import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.Iterator
import java.util.UUID

class BulkUpdateInventory : SLMessage {
    AgentData AgentData_Field
    ArrayList<FolderData> FolderData_Fields = ArrayList<>()
    ArrayList<ItemData> ItemData_Fields = ArrayList<>()

    class AgentData {
        UUID AgentID
        UUID TransactionID
    }

    class FolderData {
        UUID FolderID
        ByteArray Name
        UUID ParentID
        Int Type
    }

    class ItemData {
        UUID AssetID
        Int BaseMask
        Int CRC
        Int CallbackID
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

    BulkUpdateInventory() {
        this.zeroCoded = true
        this.AgentData_Field = AgentData()
    }

    fun CalcPayloadSize(): Int {
        Int i2 = 37
        Iterator<T> it = this.FolderData_Fields.iterator()
        while (true) {
            i = i2
            if (!it.hasNext()) {
                break
            }
            i2 = ((it as FolderData).next()).Name.size + 34 + i
        }
        Int i3 = i + 1
        Iterator<T> it2 = this.ItemData_Fields.iterator()
        while (true) {
            Int i4 = i3
            if (!it2.hasNext()) {
                return i4
            }
            ItemData itemData = (it2 as ItemData).next()
            i3 = itemData.Description.size + itemData.Name.size + 133 + 1 + 4 + 4 + i4
        }
    }

    fun Handle(SLMessageHandler sLMessageHandler): Unit {
        sLMessageHandler.HandleBulkUpdateInventory(this)
    }

    fun PackPayload(ByteBuffer byteBuffer): Unit {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 1)
        byteBuffer.put(Ascii.EM)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.TransactionID)
        byteBuffer.put((this as byte).FolderData_Fields.size())
        for (FolderData folderData : this.FolderData_Fields) {
            packUUID(byteBuffer, folderData.FolderID)
            packUUID(byteBuffer, folderData.ParentID)
            packByte(byteBuffer, (folderData as byte).Type)
            packVariable(byteBuffer, folderData.Name, 1)
        }
        byteBuffer.put((this as byte).ItemData_Fields.size())
        for (ItemData itemData : this.ItemData_Fields) {
            packUUID(byteBuffer, itemData.ItemID)
            packInt(byteBuffer, itemData.CallbackID)
            packUUID(byteBuffer, itemData.FolderID)
            packUUID(byteBuffer, itemData.CreatorID)
            packUUID(byteBuffer, itemData.OwnerID)
            packUUID(byteBuffer, itemData.GroupID)
            packInt(byteBuffer, itemData.BaseMask)
            packInt(byteBuffer, itemData.OwnerMask)
            packInt(byteBuffer, itemData.GroupMask)
            packInt(byteBuffer, itemData.EveryoneMask)
            packInt(byteBuffer, itemData.NextOwnerMask)
            packBoolean(byteBuffer, itemData.GroupOwned)
            packUUID(byteBuffer, itemData.AssetID)
            packByte(byteBuffer, (itemData as byte).Type)
            packByte(byteBuffer, (itemData as byte).InvType)
            packInt(byteBuffer, itemData.Flags)
            packByte(byteBuffer, (itemData as byte).SaleType)
            packInt(byteBuffer, itemData.SalePrice)
            packVariable(byteBuffer, itemData.Name, 1)
            packVariable(byteBuffer, itemData.Description, 1)
            packInt(byteBuffer, itemData.CreationDate)
            packInt(byteBuffer, itemData.CRC)
        }
    }

    fun UnpackPayload(ByteBuffer byteBuffer): Unit {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.TransactionID = unpackUUID(byteBuffer)
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (i in 0 until b) {
            FolderData folderData = FolderData()
            folderData.FolderID = unpackUUID(byteBuffer)
            folderData.ParentID = unpackUUID(byteBuffer)
            folderData.Type = unpackByte(byteBuffer)
            folderData.Name = unpackVariable(byteBuffer, 1)
            this.FolderData_Fields.add(folderData)
        }
        byte b2 = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (i2 in 0 until b2) {
            ItemData itemData = ItemData()
            itemData.ItemID = unpackUUID(byteBuffer)
            itemData.CallbackID = unpackInt(byteBuffer)
            itemData.FolderID = unpackUUID(byteBuffer)
            itemData.CreatorID = unpackUUID(byteBuffer)
            itemData.OwnerID = unpackUUID(byteBuffer)
            itemData.GroupID = unpackUUID(byteBuffer)
            itemData.BaseMask = unpackInt(byteBuffer)
            itemData.OwnerMask = unpackInt(byteBuffer)
            itemData.GroupMask = unpackInt(byteBuffer)
            itemData.EveryoneMask = unpackInt(byteBuffer)
            itemData.NextOwnerMask = unpackInt(byteBuffer)
            itemData.GroupOwned = unpackBoolean(byteBuffer)
            itemData.AssetID = unpackUUID(byteBuffer)
            itemData.Type = unpackByte(byteBuffer)
            itemData.InvType = unpackByte(byteBuffer)
            itemData.Flags = unpackInt(byteBuffer)
            itemData.SaleType = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
            itemData.SalePrice = unpackInt(byteBuffer)
            itemData.Name = unpackVariable(byteBuffer, 1)
            itemData.Description = unpackVariable(byteBuffer, 1)
            itemData.CreationDate = unpackInt(byteBuffer)
            itemData.CRC = unpackInt(byteBuffer)
            this.ItemData_Fields.add(itemData)
        }
    }
}
