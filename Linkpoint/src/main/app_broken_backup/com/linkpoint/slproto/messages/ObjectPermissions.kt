package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.UUID

class ObjectPermissions : SLMessage {
    AgentData AgentData_Field
    HeaderData HeaderData_Field
    ArrayList<ObjectData> ObjectData_Fields = ArrayList<>()

    class AgentData {
        UUID AgentID
        UUID SessionID
    }

    class HeaderData {
        Boolean Override
    }

    class ObjectData {
        Int Field
        Int Mask
        Int ObjectLocalID
        Int Set
    }

    ObjectPermissions() {
        this.zeroCoded = true
        this.AgentData_Field = AgentData()
        this.HeaderData_Field = HeaderData()
    }

    fun CalcPayloadSize(): Int {
        return (this.ObjectData_Fields.size() * 10) + 38
    }

    fun Handle(SLMessageHandler sLMessageHandler): Unit {
        sLMessageHandler.HandleObjectPermissions(this)
    }

    fun PackPayload(ByteBuffer byteBuffer): Unit {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) 105)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packBoolean(byteBuffer, this.HeaderData_Field.Override)
        byteBuffer.put((this as Byte).ObjectData_Fields.size())
        for (ObjectData objectData : this.ObjectData_Fields) {
            packInt(byteBuffer, objectData.ObjectLocalID)
            packByte(byteBuffer, (objectData as Byte).Field)
            packByte(byteBuffer, (objectData as Byte).Set)
            packInt(byteBuffer, objectData.Mask)
        }
    }

    fun UnpackPayload(ByteBuffer byteBuffer): Unit {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.HeaderData_Field.Override = unpackBoolean(byteBuffer)
        Byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (i in 0 until b) {
            ObjectData objectData = ObjectData()
            objectData.ObjectLocalID = unpackInt(byteBuffer)
            objectData.Field = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
            objectData.Set = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
            objectData.Mask = unpackInt(byteBuffer)
            this.ObjectData_Fields.add(objectData)
        }
    }
}
