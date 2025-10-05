package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer

class KillObject : SLMessage() {
    val ObjectData_Fields = ArrayList<ObjectData>()

    class ObjectData {
        var ID: Int = 0
    }

    init {
        zeroCoded = false
    }

    override fun CalcPayloadSize(): Int = (ObjectData_Fields.size * 4) + 2

    override fun Handle(handler: SLMessageHandler) {
        handler.HandleKillObject(this)
    }

    override fun PackPayload(buffer: ByteBuffer) {
        buffer.put(16.toByte())
        buffer.put(ObjectData_Fields.size.toByte())
        for (objectData in ObjectData_Fields) {
            packInt(buffer, objectData.ID)
        }
    }

    override fun UnpackPayload(buffer: ByteBuffer) {
        val count = buffer.get().toInt() and UnsignedBytes.MAX_VALUE.toInt()
        for (i in 0 until count) {
            val objectData = ObjectData()
            objectData.ID = unpackInt(buffer)
            ObjectData_Fields.add(objectData)
        }
    }
}