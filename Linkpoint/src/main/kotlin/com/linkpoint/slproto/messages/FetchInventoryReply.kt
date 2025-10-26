package com.linkpoint.slproto.messages

import com.google.common.base.Ascii
import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.Iterator
import java.util.UUID

class FetchInventoryReply : SLMessage() {
    public AgentData AgentData_Field
    public ArrayList<InventoryData> InventoryData_Fields = ArrayList<>()

    @JvmStatic
    class AgentData {
        public UUID AgentID
    }

    @JvmStatic
    class InventoryData {
        public UUID AssetID
        public Int BaseMask
        public Int CRC
        public Int CreationDate
        public UUID CreatorID
        public ByteArray Description
        public Int EveryoneMask
        public Int Flags
        public UUID FolderID
        public UUID GroupID
        public Int GroupMask
        public Boolean GroupOwned
        public Int InvType
        public UUID ItemID
        public ByteArray Name
        public Int NextOwnerMask
        public UUID OwnerID
        public Int OwnerMask
        public Int SalePrice
        public Int SaleType
        public Int Type
    }

    public FetchInventoryReply() {
        this.zeroCoded = true
        this.AgentData_Field = AgentData()
    }

    public fun CalcPayloadSize(): Int {
        val i: Int = 21
        val it: Iterator<T> = this.InventoryData_Fields.iterator()
        while (true) {
            val i2: Int = i
            if (!it.hasNext()) {
                return i2
            }
            val inventoryData: InventoryData = (InventoryData) it.next()
            i = inventoryData.Description.length + inventoryData.Name.length + 129 + 1 + 4 + 4 + i2
        }
    }

    fun Handle(sLMessageHandler: SLMessageHandler) {
        sLMessageHandler.HandleFetchInventoryReply(this)
    }

    fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put(Ascii.CAN)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        byteBuffer.put((Byte) this.InventoryData_Fields.size())
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
            packByte(byteBuffer, (Byte) inventoryData.Type)
            packByte(byteBuffer, (Byte) inventoryData.InvType)
            packInt(byteBuffer, inventoryData.Flags)
            packByte(byteBuffer, (Byte) inventoryData.SaleType)
            packInt(byteBuffer, inventoryData.SalePrice)
            packVariable(byteBuffer, inventoryData.Name, 1)
            packVariable(byteBuffer, inventoryData.Description, 1)
            packInt(byteBuffer, inventoryData.CreationDate)
            packInt(byteBuffer, inventoryData.CRC)
        }
    }

    fun UnpackPayload(byteBuffer: ByteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        val b: Byte = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            val inventoryData: InventoryData = InventoryData()
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
