package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class ScriptMailRegistration : SLMessage {
    DataBlock DataBlock_Field = DataBlock()

    class DataBlock {
        Int Flags
        Byte[] TargetIP
        Int TargetPort
        UUID TaskID
    }

    ScriptMailRegistration() {
        this.zeroCoded = false
    }

    Int CalcPayloadSize() {
        return this.DataBlock_Field.TargetIP.length + 1 + 2 + 16 + 4 + 4
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleScriptMailRegistration(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) -94)
        packVariable(byteBuffer, this.DataBlock_Field.TargetIP, 1)
        packShort(byteBuffer, (Short) this.DataBlock_Field.TargetPort)
        packUUID(byteBuffer, this.DataBlock_Field.TaskID)
        packInt(byteBuffer, this.DataBlock_Field.Flags)
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.DataBlock_Field.TargetIP = unpackVariable(byteBuffer, 1)
        this.DataBlock_Field.TargetPort = unpackShort(byteBuffer) & 65535
        this.DataBlock_Field.TaskID = unpackUUID(byteBuffer)
        this.DataBlock_Field.Flags = unpackInt(byteBuffer)
    }
}
