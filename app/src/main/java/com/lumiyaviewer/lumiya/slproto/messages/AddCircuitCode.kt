package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class AddCircuitCode : SLMessage {
    CircuitCode CircuitCode_Field = CircuitCode()

    class CircuitCode {
        UUID AgentID
        Int Code
        UUID SessionID
    }

    constructor() {
        this.zeroCoded = false
    }

    fun CalcPayloadSize(): Int {
        return 40
    }

    fun Handle(sLMessageHandler: SLMessageHandler): Unit {
        sLMessageHandler.HandleAddCircuitCode(this)
    }

    fun PackPayload(byteBuffer: ByteBuffer): Unit {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) 2)
        packInt(byteBuffer, this.CircuitCode_Field.Code)
        packUUID(byteBuffer, this.CircuitCode_Field.SessionID)
        packUUID(byteBuffer, this.CircuitCode_Field.AgentID)
    }

    fun UnpackPayload(byteBuffer: ByteBuffer): Unit {
        this.CircuitCode_Field.Code = unpackInt(byteBuffer)
        this.CircuitCode_Field.SessionID = unpackUUID(byteBuffer)
        this.CircuitCode_Field.AgentID = unpackUUID(byteBuffer)
    }
}
