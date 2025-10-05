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
        public Int Type
    }

    public FetchInventoryReply() {
        this.zeroCoded = true
        this.AgentData_Field = AgentData()
    }

    public Int CalcPayloadSize() {
        Int i = 21
        Iterator<T> it = this.InventoryData_Fields.iterator()
        while (true) {
            Int i2 = i
            if (!it.hasNext()) {
                return i2
            }
            InventoryData inventoryData = (InventoryData) it.next()
            i = inventoryData.Description.length + inventoryData.Name.length + 129 + 1 + 4 + 4 + i2
        }
    }

    public Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleFetchInventoryReply(this)
    }

    public Unit PackPayload(ByteBuffer byteBuffer) {
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

    public Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        Byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
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
