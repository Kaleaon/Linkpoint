package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class ScriptMailRegistration : SLMessage {
    DataBlock DataBlock_Field = DataBlock()

    class DataBlock {
        Int Flags
        ByteArray TargetIP
        Int TargetPort
        UUID TaskID
    }

    ScriptMailRegistration() {
        this.zeroCoded = false
    }

    fun CalcPayloadSize(): Int {
        return this.DataBlock_Field.TargetIP.size + 1 + 2 + 16 + 4 + 4
    }

    fun Handle(SLMessageHandler sLMessageHandler): Unit {
        sLMessageHandler.HandleScriptMailRegistration(this)
    }

    fun PackPayload(ByteBuffer byteBuffer): Unit {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) -94)
        packVariable(byteBuffer, this.DataBlock_Field.TargetIP, 1)
        packShort(byteBuffer, (this as Short).DataBlock_Field.TargetPort)
        packUUID(byteBuffer, this.DataBlock_Field.TaskID)
        packInt(byteBuffer, this.DataBlock_Field.Flags)
    }

    fun UnpackPayload(ByteBuffer byteBuffer): Unit {
        this.DataBlock_Field.TargetIP = unpackVariable(byteBuffer, 1)
        this.DataBlock_Field.TargetPort = unpackShort(byteBuffer) & 65535
        this.DataBlock_Field.TaskID = unpackUUID(byteBuffer)
        this.DataBlock_Field.Flags = unpackInt(byteBuffer)
    }
}
