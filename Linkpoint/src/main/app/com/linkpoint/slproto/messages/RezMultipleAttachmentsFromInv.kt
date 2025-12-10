package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.Iterator
import java.util.UUID

class RezMultipleAttachmentsFromInv : SLMessage {
    AgentData AgentData_Field
    HeaderData HeaderData_Field
    ArrayList<ObjectData> ObjectData_Fields = ArrayList<>()

    class AgentData {
        UUID AgentID
        UUID SessionID
    }

    class HeaderData {
        UUID CompoundMsgID
        Boolean FirstDetachAll
        Int TotalObjects
    }

    class ObjectData {
        Int AttachmentPt
        ByteArray Description
        Int EveryoneMask
        Int GroupMask
        Int ItemFlags
        UUID ItemID
        ByteArray Name
        Int NextOwnerMask
        UUID OwnerID
    }

    RezMultipleAttachmentsFromInv() {
        this.zeroCoded = true
        this.AgentData_Field = AgentData()
        this.HeaderData_Field = HeaderData()
    }

    fun CalcPayloadSize(): Int {
        Int i = 55
        Iterator<T> it = this.ObjectData_Fields.iterator()
        while (true) {
            Int i2 = i
            if (!it.hasNext()) {
                return i2
            }
            ObjectData objectData = (it as ObjectData).next()
            i = objectData.Description.size + objectData.Name.size + 50 + 1 + i2
        }
    }

    fun Handle(SLMessageHandler sLMessageHandler): Unit {
        sLMessageHandler.HandleRezMultipleAttachmentsFromInv(this)
    }

    fun PackPayload(ByteBuffer byteBuffer): Unit {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) -116)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packUUID(byteBuffer, this.HeaderData_Field.CompoundMsgID)
        packByte(byteBuffer, (this as Byte).HeaderData_Field.TotalObjects)
        packBoolean(byteBuffer, this.HeaderData_Field.FirstDetachAll)
        byteBuffer.put((this as Byte).ObjectData_Fields.size())
        for (ObjectData objectData : this.ObjectData_Fields) {
            packUUID(byteBuffer, objectData.ItemID)
            packUUID(byteBuffer, objectData.OwnerID)
            packByte(byteBuffer, (objectData as Byte).AttachmentPt)
            packInt(byteBuffer, objectData.ItemFlags)
            packInt(byteBuffer, objectData.GroupMask)
            packInt(byteBuffer, objectData.EveryoneMask)
            packInt(byteBuffer, objectData.NextOwnerMask)
            packVariable(byteBuffer, objectData.Name, 1)
            packVariable(byteBuffer, objectData.Description, 1)
        }
    }

    fun UnpackPayload(ByteBuffer byteBuffer): Unit {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.HeaderData_Field.CompoundMsgID = unpackUUID(byteBuffer)
        this.HeaderData_Field.TotalObjects = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
        this.HeaderData_Field.FirstDetachAll = unpackBoolean(byteBuffer)
        Byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (i in 0 until b) {
            ObjectData objectData = ObjectData()
            objectData.ItemID = unpackUUID(byteBuffer)
            objectData.OwnerID = unpackUUID(byteBuffer)
            objectData.AttachmentPt = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
            objectData.ItemFlags = unpackInt(byteBuffer)
            objectData.GroupMask = unpackInt(byteBuffer)
            objectData.EveryoneMask = unpackInt(byteBuffer)
            objectData.NextOwnerMask = unpackInt(byteBuffer)
            objectData.Name = unpackVariable(byteBuffer, 1)
            objectData.Description = unpackVariable(byteBuffer, 1)
            this.ObjectData_Fields.add(objectData)
        }
    }
}
