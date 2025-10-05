package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class Redo : SLMessage() {
    val AgentData_Field = AgentData()
    val ObjectData_Fields = ArrayList<ObjectData>()

    class AgentData {
        var AgentID: UUID? = null
        var SessionID: UUID? = null
        var GroupID: UUID? = null
    }

    class ObjectData {
        var ObjectID: UUID? = null
    }

    init {
        zeroCoded = false
    }

    override fun CalcPayloadSize(): Int = (ObjectData_Fields.size * 16) + 53

    override fun Handle(handler: SLMessageHandler) {
        handler.HandleRedo(this)
    }

    override fun PackPayload(buffer: ByteBuffer) {
        buffer.putShort(-1)
        buffer.put(0.toByte())
        buffer.put(76.toByte())
        packUUID(buffer, AgentData_Field.AgentID)
        packUUID(buffer, AgentData_Field.SessionID)
        packUUID(buffer, AgentData_Field.GroupID)
        buffer.put(ObjectData_Fields.size.toByte())
        for (objectData in ObjectData_Fields) {
            packUUID(buffer, objectData.ObjectID)
        }
    }

    override fun UnpackPayload(buffer: ByteBuffer) {
        AgentData_Field.AgentID = unpackUUID(buffer)
        AgentData_Field.SessionID = unpackUUID(buffer)
        AgentData_Field.GroupID = unpackUUID(buffer)
        val count = buffer.get().toInt() and UnsignedBytes.MAX_VALUE.toInt()
        for (i in 0 until count) {
            val objectData = ObjectData()
            objectData.ObjectID = unpackUUID(buffer)
            ObjectData_Fields.add(objectData)
        }
    }
}