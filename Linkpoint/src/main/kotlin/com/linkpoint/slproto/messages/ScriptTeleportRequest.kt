package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.types.LLVector3
import java.nio.ByteBuffer

class ScriptTeleportRequest : SLMessage() {
    public Data Data_Field = Data()

    @JvmStatic
    class Data {
        public LLVector3 LookAt
        public Byte[] ObjectName
        public Byte[] SimName
        public LLVector3 SimPosition
    }

    public ScriptTeleportRequest() {
        this.zeroCoded = false
    }

    public Int CalcPayloadSize() {
        return this.Data_Field.ObjectName.length + 1 + 1 + this.Data_Field.SimName.length + 12 + 12 + 4
    }

    fun Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleScriptTeleportRequest(this)
    }

    fun PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) -61)
        packVariable(byteBuffer, this.Data_Field.ObjectName, 1)
        packVariable(byteBuffer, this.Data_Field.SimName, 1)
        packLLVector3(byteBuffer, this.Data_Field.SimPosition)
        packLLVector3(byteBuffer, this.Data_Field.LookAt)
    }

    fun UnpackPayload(ByteBuffer byteBuffer) {
        this.Data_Field.ObjectName = unpackVariable(byteBuffer, 1)
        this.Data_Field.SimName = unpackVariable(byteBuffer, 1)
        this.Data_Field.SimPosition = unpackLLVector3(byteBuffer)
        this.Data_Field.LookAt = unpackLLVector3(byteBuffer)
    }
}
