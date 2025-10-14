package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class UseCircuitCode : SLMessage {
    CircuitCode CircuitCode_Field = CircuitCode()

    class CircuitCode {
        Int Code
        UUID ID
        UUID SessionID
    }

    UseCircuitCode() {
        this.zeroCoded = false
    }

    Int CalcPayloadSize() {
        return 40
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleUseCircuitCode(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) 3)
        packInt(byteBuffer, this.CircuitCode_Field.Code)
        packUUID(byteBuffer, this.CircuitCode_Field.SessionID)
        packUUID(byteBuffer, this.CircuitCode_Field.ID)
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.CircuitCode_Field.Code = unpackInt(byteBuffer)
        this.CircuitCode_Field.SessionID = unpackUUID(byteBuffer)
        this.CircuitCode_Field.ID = unpackUUID(byteBuffer)
    }
}
