package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
import com.lumiyaviewer.lumiya.slproto.handler.SLMessageHandler
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.UUID

class BulkUpdateInventory : SLMessage {
    var AgentData_Field: AgentData = AgentData()
    var FolderData_Fields: ArrayList<FolderData> = ArrayList()
    var ItemData_Fields: ArrayList<ItemData> = ArrayList()

    class AgentData {
        var AgentID: UUID? = null
        var TransactionID: UUID? = null
    }

    class FolderData {
        var FolderID: UUID? = null
        var Name: ByteArray = ByteArray(0)
        var ParentID: UUID? = null
        var Type: Int = 0
    }

    class ItemData {
        var AssetID: UUID? = null
        var BaseMask: Int = 0
        var CRC: Int = 0
        var CallbackID: Int = 0
        var CreationDate: Int = 0
        var CreatorID: UUID? = null
        var Description: ByteArray = ByteArray(0)
        var EveryoneMask: Int = 0
        var Flags: Int = 0
        var FolderID: UUID? = null
        var GroupID: UUID? = null
        var GroupMask: Int = 0
        var GroupOwned: Boolean = false
        var InvType: Int = 0
        var ItemID: UUID? = null
        var Name: ByteArray = ByteArray(0)
        var NextOwnerMask: Int = 0
        var OwnerID: UUID? = null
        var OwnerMask: Int = 0
        var SalePrice: Int = 0
        var SaleType: Int = 0
        var Type: Int = 0
    }

    constructor() {
        this.zeroCoded = true
    }

    override fun CalcPayloadSize(): Int {
        var length = 37
        for (folderData in FolderData_Fields) {
            length += folderData.Name.size + 34
        }
        length++
        for (itemData in ItemData_Fields) {
            length += itemData.Description.size + itemData.Name.size + 133 + 1 + 4 + 4
        }
        return length
    }

    override fun Handle(sLMessageHandler: SLMessageHandler) {
        sLMessageHandler.HandleBulkUpdateInventory(this)
    }

    override fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put(1.toByte())
        byteBuffer.put(25.toByte()) // Ascii.EM
        packUUID(byteBuffer, AgentData_Field.AgentID!!)
        packUUID(byteBuffer, AgentData_Field.TransactionID!!)
        byteBuffer.put(FolderData_Fields.size.toByte())
        for (folderData in FolderData_Fields) {
            packUUID(byteBuffer, folderData.FolderID!!)
            packUUID(byteBuffer, folderData.ParentID!!)
            packByte(byteBuffer, folderData.Type.toByte())
            packVariable(byteBuffer, folderData.Name, 1)
        }
        byteBuffer.put(ItemData_Fields.size.toByte())
        for (itemData in ItemData_Fields) {
            packUUID(byteBuffer, itemData.ItemID!!)
            packInt(byteBuffer, itemData.CallbackID)
            packUUID(byteBuffer, itemData.FolderID!!)
            packUUID(byteBuffer, itemData.CreatorID!!)
            packUUID(byteBuffer, itemData.OwnerID!!)
            packUUID(byteBuffer, itemData.GroupID!!)
            packInt(byteBuffer, itemData.BaseMask)
            packInt(byteBuffer, itemData.OwnerMask)
            packInt(byteBuffer, itemData.GroupMask)
            packInt(byteBuffer, itemData.EveryoneMask)
            packInt(byteBuffer, itemData.NextOwnerMask)
            packBoolean(byteBuffer, itemData.GroupOwned)
            packUUID(byteBuffer, itemData.AssetID!!)
            packByte(byteBuffer, itemData.Type.toByte())
            packByte(byteBuffer, itemData.InvType.toByte())
            packInt(byteBuffer, itemData.Flags)
            packByte(byteBuffer, itemData.SaleType.toByte())
            packInt(byteBuffer, itemData.SalePrice)
            packVariable(byteBuffer, itemData.Name, 1)
            packVariable(byteBuffer, itemData.Description, 1)
            packInt(byteBuffer, itemData.CreationDate)
            packInt(byteBuffer, itemData.CRC)
        }
    }

    override fun UnpackPayload(byteBuffer: ByteBuffer) {
        AgentData_Field.AgentID = unpackUUID(byteBuffer)
        AgentData_Field.TransactionID = unpackUUID(byteBuffer)
        val b = (byteBuffer.get().toInt() and 0xFF)
        for (i in 0 until b) {
            val folderData = FolderData()
            folderData.FolderID = unpackUUID(byteBuffer)
            folderData.ParentID = unpackUUID(byteBuffer)
            folderData.Type = (unpackByte(byteBuffer).toInt() and 0xFF)
            folderData.Name = unpackVariable(byteBuffer, 1)
            FolderData_Fields.add(folderData)
        }
        val b2 = (byteBuffer.get().toInt() and 0xFF)
        for (i in 0 until b2) {
            val itemData = ItemData()
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
            itemData.Type = (unpackByte(byteBuffer).toInt() and 0xFF)
            itemData.InvType = (unpackByte(byteBuffer).toInt() and 0xFF)
            itemData.Flags = unpackInt(byteBuffer)
            itemData.SaleType = (unpackByte(byteBuffer).toInt() and 0xFF)
            itemData.SalePrice = unpackInt(byteBuffer)
            itemData.Name = unpackVariable(byteBuffer, 1)
            itemData.Description = unpackVariable(byteBuffer, 1)
            itemData.CreationDate = unpackInt(byteBuffer)
            itemData.CRC = unpackInt(byteBuffer)
            ItemData_Fields.add(itemData)
        }
    }
}
