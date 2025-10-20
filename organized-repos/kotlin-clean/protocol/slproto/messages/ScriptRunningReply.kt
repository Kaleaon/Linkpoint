package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class ScriptRunningReply : SLMessage() {
    public Script Script_Field = Script()

    @JvmStatic
    class Script {
        public UUID ItemID
        public UUID ObjectID
        public Boolean Running
    }

    public ScriptRunningReply() {
        this.zeroCoded = false
    }

    public Int CalcPayloadSize() {
        return 37
    }

    fun Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleScriptRunningReply(this)
    }

    fun PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) -12)
        packUUID(byteBuffer, this.Script_Field.ObjectID)
        packUUID(byteBuffer, this.Script_Field.ItemID)
        packBoolean(byteBuffer, this.Script_Field.Running)
    }

    fun UnpackPayload(ByteBuffer byteBuffer) {
        this.Script_Field.ObjectID = unpackUUID(byteBuffer)
        this.Script_Field.ItemID = unpackUUID(byteBuffer)
        this.Script_Field.Running = unpackBoolean(byteBuffer)
    }
}
