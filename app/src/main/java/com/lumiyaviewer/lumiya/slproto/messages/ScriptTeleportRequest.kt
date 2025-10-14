package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
import com.lumiyaviewer.lumiya.slproto.types.LLVector3
import java.nio.ByteBuffer

class ScriptTeleportRequest : SLMessage {
    Data Data_Field = Data()

    class Data {
        LLVector3 LookAt
        Byte[] ObjectName
        Byte[] SimName
        LLVector3 SimPosition
    }

    ScriptTeleportRequest() {
        this.zeroCoded = false
    }

    Int CalcPayloadSize() {
        return this.Data_Field.ObjectName.length + 1 + 1 + this.Data_Field.SimName.length + 12 + 12 + 4
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleScriptTeleportRequest(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) -61)
        packVariable(byteBuffer, this.Data_Field.ObjectName, 1)
        packVariable(byteBuffer, this.Data_Field.SimName, 1)
        packLLVector3(byteBuffer, this.Data_Field.SimPosition)
        packLLVector3(byteBuffer, this.Data_Field.LookAt)
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.Data_Field.ObjectName = unpackVariable(byteBuffer, 1)
        this.Data_Field.SimName = unpackVariable(byteBuffer, 1)
        this.Data_Field.SimPosition = unpackLLVector3(byteBuffer)
        this.Data_Field.LookAt = unpackLLVector3(byteBuffer)
    }
}
