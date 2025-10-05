package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class UseCircuitCode : SLMessage() {
    val CircuitCode_Field = CircuitCode()

    class CircuitCode {
        var Code: Int = 0
        var ID: UUID? = null
        var SessionID: UUID? = null
    }

    init {
        zeroCoded = false
    }

    override fun CalcPayloadSize(): Int = 40

    override fun Handle(handler: SLMessageHandler) {
        handler.HandleUseCircuitCode(this)
    }

    override fun PackPayload(buffer: ByteBuffer) {
        buffer.putShort(-1)
        buffer.put(0.toByte())
        buffer.put(3.toByte())
        packInt(buffer, CircuitCode_Field.Code)
        packUUID(buffer, CircuitCode_Field.SessionID)
        packUUID(buffer, CircuitCode_Field.ID)
    }

    override fun UnpackPayload(buffer: ByteBuffer) {
        CircuitCode_Field.Code = unpackInt(buffer)
        CircuitCode_Field.SessionID = unpackUUID(buffer)
        CircuitCode_Field.ID = unpackUUID(buffer)
    }
}