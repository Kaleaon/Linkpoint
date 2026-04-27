package com.lumiyaviewer.lumiya.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.lumiyaviewer.lumiya.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.Iterator
import java.util.UUID

class ObjectProperties : SLMessage {
    ArrayList<ObjectData> ObjectData_Fields = ArrayList<>()

    class ObjectData {
        Int AggregatePermTextures
        Int AggregatePermTexturesOwner
        Int AggregatePerms
        Int BaseMask
        Int Category
        Long CreationDate
        UUID CreatorID
        ByteArray Description
        Int EveryoneMask
        UUID FolderID
        UUID FromTaskID
        UUID GroupID
        Int GroupMask
        Int InventorySerial
        UUID ItemID
        UUID LastOwnerID
        ByteArray Name
        Int NextOwnerMask
        UUID ObjectID
        UUID OwnerID
        Int OwnerMask
        Int OwnershipCost
        Int SalePrice
        Int SaleType
        ByteArray SitName
        ByteArray TextureID
        ByteArray TouchName
    }

    ObjectProperties() {
        this.zeroCoded = true
    }

    Int CalcPayloadSize() {
        Int i = 3
        Iterator<T> it = this.ObjectData_Fields.iterator()
        while (true) {
            Int i2 = i
            if (!it.hasNext()) {
                return i2
            }
            ObjectData objectData = (ObjectData) it.next()
            i = objectData.TextureID.length + objectData.Name.length + 175 + 1 + objectData.Description.length + 1 + objectData.TouchName.length + 1 + objectData.SitName.length + 1 + i2
        }
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleObjectProperties(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.put((Byte) -1)
        byteBuffer.put((Byte) 9)
        byteBuffer.put((Byte) this.ObjectData_Fields.size())
        for (ObjectData objectData : this.ObjectData_Fields) {
            packUUID(byteBuffer, objectData.ObjectID)
            packUUID(byteBuffer, objectData.CreatorID)
            packUUID(byteBuffer, objectData.OwnerID)
            packUUID(byteBuffer, objectData.GroupID)
            packLong(byteBuffer, objectData.CreationDate)
            packInt(byteBuffer, objectData.BaseMask)
            packInt(byteBuffer, objectData.OwnerMask)
            packInt(byteBuffer, objectData.GroupMask)
            packInt(byteBuffer, objectData.EveryoneMask)
            packInt(byteBuffer, objectData.NextOwnerMask)
            packInt(byteBuffer, objectData.OwnershipCost)
            packByte(byteBuffer, (Byte) objectData.SaleType)
            packInt(byteBuffer, objectData.SalePrice)
            packByte(byteBuffer, (Byte) objectData.AggregatePerms)
            packByte(byteBuffer, (Byte) objectData.AggregatePermTextures)
            packByte(byteBuffer, (Byte) objectData.AggregatePermTexturesOwner)
            packInt(byteBuffer, objectData.Category)
            packShort(byteBuffer, (Short) objectData.InventorySerial)
            packUUID(byteBuffer, objectData.ItemID)
            packUUID(byteBuffer, objectData.FolderID)
            packUUID(byteBuffer, objectData.FromTaskID)
            packUUID(byteBuffer, objectData.LastOwnerID)
            packVariable(byteBuffer, objectData.Name, 1)
            packVariable(byteBuffer, objectData.Description, 1)
            packVariable(byteBuffer, objectData.TouchName, 1)
            packVariable(byteBuffer, objectData.SitName, 1)
            packVariable(byteBuffer, objectData.TextureID, 1)
        }
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        Byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            ObjectData objectData = ObjectData()
            objectData.ObjectID = unpackUUID(byteBuffer)
            objectData.CreatorID = unpackUUID(byteBuffer)
            objectData.OwnerID = unpackUUID(byteBuffer)
            objectData.GroupID = unpackUUID(byteBuffer)
            objectData.CreationDate = unpackLong(byteBuffer)
            objectData.BaseMask = unpackInt(byteBuffer)
            objectData.OwnerMask = unpackInt(byteBuffer)
            objectData.GroupMask = unpackInt(byteBuffer)
            objectData.EveryoneMask = unpackInt(byteBuffer)
            objectData.NextOwnerMask = unpackInt(byteBuffer)
            objectData.OwnershipCost = unpackInt(byteBuffer)
            objectData.SaleType = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
            objectData.SalePrice = unpackInt(byteBuffer)
            objectData.AggregatePerms = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
            objectData.AggregatePermTextures = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
            objectData.AggregatePermTexturesOwner = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
            objectData.Category = unpackInt(byteBuffer)
            objectData.InventorySerial = unpackShort(byteBuffer)
            objectData.ItemID = unpackUUID(byteBuffer)
            objectData.FolderID = unpackUUID(byteBuffer)
            objectData.FromTaskID = unpackUUID(byteBuffer)
            objectData.LastOwnerID = unpackUUID(byteBuffer)
            objectData.Name = unpackVariable(byteBuffer, 1)
            objectData.Description = unpackVariable(byteBuffer, 1)
            objectData.TouchName = unpackVariable(byteBuffer, 1)
            objectData.SitName = unpackVariable(byteBuffer, 1)
            objectData.TextureID = unpackVariable(byteBuffer, 1)
            this.ObjectData_Fields.add(objectData)
        }
    }
}
