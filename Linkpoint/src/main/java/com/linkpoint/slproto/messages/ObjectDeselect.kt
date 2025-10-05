package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class ObjectDeselect : SLMessage() {
    val AgentData_Field = AgentData()
    val ObjectData_Fields = ArrayList<ObjectData>()

    class AgentData {
        var AgentID: UUID? = null
        var SessionID: UUID? = null
    }

    class ObjectData {
        var ObjectLocalID: Int = 0
    }

    init {
        zeroCoded = true
    }

    override fun CalcPayloadSize(): Int = (ObjectData_Fields.size * 4) + 37

    override fun Handle(handler: SLMessageHandler) {
        handler.HandleObjectDeselect(this)
    }

    override fun PackPayload(buffer: ByteBuffer) {
        buffer.putShort(-1)
        buffer.put(0.toByte())
        buffer.put(111.toByte())
        packUUID(buffer, AgentData_Field.AgentID)
        packUUID(buffer, AgentData_Field.SessionID)
        buffer.put(ObjectData_Fields.size.toByte())
        for (objectData in ObjectData_Fields) {
            packInt(buffer, objectData.ObjectLocalID)
        }
    }

    override fun UnpackPayload(buffer: ByteBuffer) {
        AgentData_Field.AgentID = unpackUUID(buffer)
        AgentData_Field.SessionID = unpackUUID(buffer)
        val count = buffer.get().toInt() and UnsignedBytes.MAX_VALUE.toInt()
        for (i in 0 until count) {
            val objectData = ObjectData()
            objectData.ObjectLocalID = unpackInt(buffer)
            ObjectData_Fields.add(objectData)
        }
    }
}