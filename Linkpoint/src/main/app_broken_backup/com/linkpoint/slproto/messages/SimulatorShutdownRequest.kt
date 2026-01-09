package com.linkpoint.slproto.messages

import com.google.common.base.Ascii
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer

class SimulatorShutdownRequest : SLMessage {
    SimulatorShutdownRequest() {
        this.zeroCoded = false
    }

    fun CalcPayloadSize(): Int {
        return 4
    }

    fun Handle(SLMessageHandler sLMessageHandler): Unit {
        sLMessageHandler.HandleSimulatorShutdownRequest(this)
    }

    fun PackPayload(ByteBuffer byteBuffer): Unit {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put(Ascii.CR)
    }

    fun UnpackPayload(ByteBuffer byteBuffer): Unit {
    }
}
