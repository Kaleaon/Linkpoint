package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer

class HealthMessage : SLMessage {
    HealthData HealthData_Field = HealthData()

    class HealthData {
        float Health
    }

    HealthMessage() {
        this.zeroCoded = true
    }

    fun CalcPayloadSize(): Int {
        return 8
    }

    fun Handle(SLMessageHandler sLMessageHandler): Unit {
        sLMessageHandler.HandleHealthMessage(this)
    }

    fun PackPayload(ByteBuffer byteBuffer): Unit {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 0)
        byteBuffer.put((byte) -118)
        packFloat(byteBuffer, this.HealthData_Field.Health)
    }

    fun UnpackPayload(ByteBuffer byteBuffer): Unit {
        this.HealthData_Field.Health = unpackFloat(byteBuffer)
    }
}
