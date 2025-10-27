package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class ScriptMailRegistration : SLMessage() {
    public DataBlock DataBlock_Field = DataBlock()

    @JvmStatic
    class DataBlock {
        public Int Flags
        public ByteArray TargetIP
        public Int TargetPort
        public UUID TaskID
    }

    public ScriptMailRegistration() {
        this.zeroCoded = false
    }

    public Int CalcPayloadSize() {
        return this.DataBlock_Field.TargetIP.length + 1 + 2 + 16 + 4 + 4
    }

    fun Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleScriptMailRegistration(this)
    }

    fun PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) -94)
        packVariable(byteBuffer, this.DataBlock_Field.TargetIP, 1)
        packShort(byteBuffer, (Short) this.DataBlock_Field.TargetPort)
        packUUID(byteBuffer, this.DataBlock_Field.TaskID)
        packInt(byteBuffer, this.DataBlock_Field.Flags)
    }

    fun UnpackPayload(ByteBuffer byteBuffer) {
        this.DataBlock_Field.TargetIP = unpackVariable(byteBuffer, 1)
        this.DataBlock_Field.TargetPort = unpackShort(byteBuffer) & 65535
        this.DataBlock_Field.TaskID = unpackUUID(byteBuffer)
        this.DataBlock_Field.Flags = unpackInt(byteBuffer)
    }
}
