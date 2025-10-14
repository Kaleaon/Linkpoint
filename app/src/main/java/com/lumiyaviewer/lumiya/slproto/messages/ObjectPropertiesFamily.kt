package com.lumiyaviewer.lumiya.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.lumiyaviewer.lumiya.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class ObjectPropertiesFamily : SLMessage {
    ObjectData ObjectData_Field = ObjectData()

    class ObjectData {
        Int BaseMask
        Int Category
        Byte[] Description
        Int EveryoneMask
        UUID GroupID
        Int GroupMask
        UUID LastOwnerID
        Byte[] Name
        Int NextOwnerMask
        UUID ObjectID
        UUID OwnerID
        Int OwnerMask
        Int OwnershipCost
        Int RequestFlags
        Int SalePrice
        Int SaleType
    }

    ObjectPropertiesFamily() {
        this.zeroCoded = true
    }

    Int CalcPayloadSize() {
        return this.ObjectData_Field.Name.length + 102 + 1 + this.ObjectData_Field.Description.length + 2
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleObjectPropertiesFamily(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.put((Byte) -1)
        byteBuffer.put((Byte) 10)
        packInt(byteBuffer, this.ObjectData_Field.RequestFlags)
        packUUID(byteBuffer, this.ObjectData_Field.ObjectID)
        packUUID(byteBuffer, this.ObjectData_Field.OwnerID)
        packUUID(byteBuffer, this.ObjectData_Field.GroupID)
        packInt(byteBuffer, this.ObjectData_Field.BaseMask)
        packInt(byteBuffer, this.ObjectData_Field.OwnerMask)
        packInt(byteBuffer, this.ObjectData_Field.GroupMask)
        packInt(byteBuffer, this.ObjectData_Field.EveryoneMask)
        packInt(byteBuffer, this.ObjectData_Field.NextOwnerMask)
        packInt(byteBuffer, this.ObjectData_Field.OwnershipCost)
        packByte(byteBuffer, (Byte) this.ObjectData_Field.SaleType)
        packInt(byteBuffer, this.ObjectData_Field.SalePrice)
        packInt(byteBuffer, this.ObjectData_Field.Category)
        packUUID(byteBuffer, this.ObjectData_Field.LastOwnerID)
        packVariable(byteBuffer, this.ObjectData_Field.Name, 1)
        packVariable(byteBuffer, this.ObjectData_Field.Description, 1)
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.ObjectData_Field.RequestFlags = unpackInt(byteBuffer)
        this.ObjectData_Field.ObjectID = unpackUUID(byteBuffer)
        this.ObjectData_Field.OwnerID = unpackUUID(byteBuffer)
        this.ObjectData_Field.GroupID = unpackUUID(byteBuffer)
        this.ObjectData_Field.BaseMask = unpackInt(byteBuffer)
        this.ObjectData_Field.OwnerMask = unpackInt(byteBuffer)
        this.ObjectData_Field.GroupMask = unpackInt(byteBuffer)
        this.ObjectData_Field.EveryoneMask = unpackInt(byteBuffer)
        this.ObjectData_Field.NextOwnerMask = unpackInt(byteBuffer)
        this.ObjectData_Field.OwnershipCost = unpackInt(byteBuffer)
        this.ObjectData_Field.SaleType = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
        this.ObjectData_Field.SalePrice = unpackInt(byteBuffer)
        this.ObjectData_Field.Category = unpackInt(byteBuffer)
        this.ObjectData_Field.LastOwnerID = unpackUUID(byteBuffer)
        this.ObjectData_Field.Name = unpackVariable(byteBuffer, 1)
        this.ObjectData_Field.Description = unpackVariable(byteBuffer, 1)
    }
}
