package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
import java.nio.ByteBuffer

class HealthMessage : SLMessage {
    HealthData HealthData_Field = HealthData()

    class HealthData {
        float Health
    }

    HealthMessage() {
        this.zeroCoded = true
    }

    Int CalcPayloadSize() {
        return 8
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleHealthMessage(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 0)
        byteBuffer.put((byte) -118)
        packFloat(byteBuffer, this.HealthData_Field.Health)
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.HealthData_Field.Health = unpackFloat(byteBuffer)
    }
}
