package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer

class HealthMessage : SLMessage() {
    val HealthData_Field = HealthData()

    class HealthData {
        var Health: Float = 0f
    }

    init {
        zeroCoded = true
    }

    override fun CalcPayloadSize(): Int = 8

    override fun Handle(handler: SLMessageHandler) {
        handler.HandleHealthMessage(this)
    }

    override fun PackPayload(buffer: ByteBuffer) {
        buffer.putShort(-1)
        buffer.put(0.toByte())
        buffer.put((-118).toByte())
        packFloat(buffer, HealthData_Field.Health)
    }

    override fun UnpackPayload(buffer: ByteBuffer) {
        HealthData_Field.Health = unpackFloat(buffer)
    }
}