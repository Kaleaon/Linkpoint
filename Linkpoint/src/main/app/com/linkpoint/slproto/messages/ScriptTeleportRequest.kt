package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.types.LLVector3
import java.nio.ByteBuffer

class ScriptTeleportRequest : SLMessage {
    Data Data_Field = Data()

    class Data {
        LLVector3 LookAt
        ByteArray ObjectName
        ByteArray SimName
        LLVector3 SimPosition
    }

    ScriptTeleportRequest() {
        this.zeroCoded = false
    }

    fun CalcPayloadSize(): Int {
        return this.Data_Field.ObjectName.size + 1 + 1 + this.Data_Field.SimName.size + 12 + 12 + 4
    }

    fun Handle(SLMessageHandler sLMessageHandler): Unit {
        sLMessageHandler.HandleScriptTeleportRequest(this)
    }

    fun PackPayload(ByteBuffer byteBuffer): Unit {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) -61)
        packVariable(byteBuffer, this.Data_Field.ObjectName, 1)
        packVariable(byteBuffer, this.Data_Field.SimName, 1)
        packLLVector3(byteBuffer, this.Data_Field.SimPosition)
        packLLVector3(byteBuffer, this.Data_Field.LookAt)
    }

    fun UnpackPayload(ByteBuffer byteBuffer): Unit {
        this.Data_Field.ObjectName = unpackVariable(byteBuffer, 1)
        this.Data_Field.SimName = unpackVariable(byteBuffer, 1)
        this.Data_Field.SimPosition = unpackLLVector3(byteBuffer)
        this.Data_Field.LookAt = unpackLLVector3(byteBuffer)
    }
}
