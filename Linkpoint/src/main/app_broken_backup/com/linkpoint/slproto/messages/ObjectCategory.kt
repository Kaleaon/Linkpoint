package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.UUID

class ObjectCategory : SLMessage {
    AgentData AgentData_Field
    ArrayList<ObjectData> ObjectData_Fields = ArrayList<>()

    class AgentData {
        UUID AgentID
        UUID SessionID
    }

    class ObjectData {
        Int Category
        Int LocalID
    }

    ObjectCategory() {
        this.zeroCoded = true
        this.AgentData_Field = AgentData()
    }

    fun CalcPayloadSize(): Int {
        return (this.ObjectData_Fields.size() * 8) + 37
    }

    fun Handle(SLMessageHandler sLMessageHandler): Unit {
        sLMessageHandler.HandleObjectCategory(this)
    }

    fun PackPayload(ByteBuffer byteBuffer): Unit {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) 109)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        byteBuffer.put((this as Byte).ObjectData_Fields.size())
        for (ObjectData objectData : this.ObjectData_Fields) {
            packInt(byteBuffer, objectData.LocalID)
            packInt(byteBuffer, objectData.Category)
        }
    }

    fun UnpackPayload(ByteBuffer byteBuffer): Unit {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        Byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (i in 0 until b) {
            ObjectData objectData = ObjectData()
            objectData.LocalID = unpackInt(byteBuffer)
            objectData.Category = unpackInt(byteBuffer)
            this.ObjectData_Fields.add(objectData)
        }
    }
}
