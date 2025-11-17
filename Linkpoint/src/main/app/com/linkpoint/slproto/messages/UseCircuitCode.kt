package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class UseCircuitCode : SLMessage() {

    data class CircuitCode(
        var Code: Int = 0,
        var ID: UUID = UUID(0, 0),
        var SessionID: UUID = UUID(0, 0),
    )

    val CircuitCode_Field: CircuitCode = CircuitCode()

    init {
        zeroCoded = false
    }

    override fun CalcPayloadSize(): Int = 40

    override fun handleMessage(handler: SLMessageHandler) {
        handler.HandleUseCircuitCode(this)
    }

    override fun PackPayload(buffer: ByteBuffer) {
        buffer.putShort((-1).toShort())
        buffer.put(0x00.toByte())
        buffer.put(0x03.toByte())
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
