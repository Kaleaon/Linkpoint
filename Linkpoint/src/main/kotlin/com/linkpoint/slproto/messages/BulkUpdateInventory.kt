package com.linkpoint.slproto.messages

import com.google.common.base.Ascii
import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.Iterator
import java.util.UUID

class BulkUpdateInventory : SLMessage() {
    public AgentData AgentData_Field
    public ArrayList<FolderData> FolderData_Fields = ArrayList<>()
    public ArrayList<ItemData> ItemData_Fields = ArrayList<>()

    @JvmStatic
    class AgentData {
        public UUID AgentID
        public UUID TransactionID
    }

    @JvmStatic
    class FolderData {
        public UUID FolderID
        public Byte[] Name
        public UUID ParentID
        public Int Type
    }

    @JvmStatic
    class ItemData {
        public UUID AssetID
        public Int BaseMask
        public Int CRC
        public Int CallbackID
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

    public BulkUpdateInventory() {
        this.zeroCoded = true
        this.AgentData_Field = AgentData()
    }

    public Int CalcPayloadSize() {
        Int i2 = 37
        Iterator<T> it = this.FolderData_Fields.iterator()
        while (true) {
            i = i2
            if (!it.hasNext()) {
                break
            }
            i2 = ((FolderData) it.next()).Name.length + 34 + i
        }
        Int i3 = i + 1
        Iterator<T> it2 = this.ItemData_Fields.iterator()
        while (true) {
            Int i4 = i3
            if (!it2.hasNext()) {
                return i4
            }
            ItemData itemData = (ItemData) it2.next()
            i3 = itemData.Description.length + itemData.Name.length + 133 + 1 + 4 + 4 + i4
        }
    }

    public Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleBulkUpdateInventory(this)
    }

    public Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put(Ascii.EM)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.TransactionID)
        byteBuffer.put((Byte) this.FolderData_Fields.size())
        for (FolderData folderData : this.FolderData_Fields) {
            packUUID(byteBuffer, folderData.FolderID)
            packUUID(byteBuffer, folderData.ParentID)
            packByte(byteBuffer, (Byte) folderData.Type)
            packVariable(byteBuffer, folderData.Name, 1)
        }
        byteBuffer.put((Byte) this.ItemData_Fields.size())
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
            packByte(byteBuffer, (Byte) itemData.Type)
            packByte(byteBuffer, (Byte) itemData.InvType)
            packInt(byteBuffer, itemData.Flags)
            packByte(byteBuffer, (Byte) itemData.SaleType)
            packInt(byteBuffer, itemData.SalePrice)
            packVariable(byteBuffer, itemData.Name, 1)
            packVariable(byteBuffer, itemData.Description, 1)
            packInt(byteBuffer, itemData.CreationDate)
            packInt(byteBuffer, itemData.CRC)
        }
    }

    public Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.TransactionID = unpackUUID(byteBuffer)
        Byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            FolderData folderData = FolderData()
            folderData.FolderID = unpackUUID(byteBuffer)
            folderData.ParentID = unpackUUID(byteBuffer)
            folderData.Type = unpackByte(byteBuffer)
            folderData.Name = unpackVariable(byteBuffer, 1)
            this.FolderData_Fields.add(folderData)
        }
        Byte b2 = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i2 = 0; i2 < b2; i2++) {
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
