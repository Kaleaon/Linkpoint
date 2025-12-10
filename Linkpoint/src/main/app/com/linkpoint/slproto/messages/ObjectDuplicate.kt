package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.types.LLVector3
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.UUID

class ObjectDuplicate : SLMessage {
    AgentData AgentData_Field
    ArrayList<ObjectData> ObjectData_Fields = ArrayList<>()
    SharedData SharedData_Field

    class AgentData {
        UUID AgentID
        UUID GroupID
        UUID SessionID
    }

    class ObjectData {
        Int ObjectLocalID
    }

    class SharedData {
        Int DuplicateFlags
        LLVector3 Offset
    }

    ObjectDuplicate() {
        this.zeroCoded = true
        this.AgentData_Field = AgentData()
        this.SharedData_Field = SharedData()
    }

    fun CalcPayloadSize(): Int {
        return (this.ObjectData_Fields.size() * 4) + 69
    }

    fun Handle(SLMessageHandler sLMessageHandler): Unit {
        sLMessageHandler.HandleObjectDuplicate(this)
    }

    fun PackPayload(ByteBuffer byteBuffer): Unit {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) 90)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packUUID(byteBuffer, this.AgentData_Field.GroupID)
        packLLVector3(byteBuffer, this.SharedData_Field.Offset)
        packInt(byteBuffer, this.SharedData_Field.DuplicateFlags)
        byteBuffer.put((this as Byte).ObjectData_Fields.size())
        for (ObjectData objectData : this.ObjectData_Fields) {
            packInt(byteBuffer, objectData.ObjectLocalID)
        }
    }

    fun UnpackPayload(ByteBuffer byteBuffer): Unit {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.AgentData_Field.GroupID = unpackUUID(byteBuffer)
        this.SharedData_Field.Offset = unpackLLVector3(byteBuffer)
        this.SharedData_Field.DuplicateFlags = unpackInt(byteBuffer)
        Byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (i in 0 until b) {
            ObjectData objectData = ObjectData()
            objectData.ObjectLocalID = unpackInt(byteBuffer)
            this.ObjectData_Fields.add(objectData)
        }
    }
}
