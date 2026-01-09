package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class ScriptRunningReply : SLMessage {
    Script Script_Field = Script()

    class Script {
        UUID ItemID
        UUID ObjectID
        Boolean Running
    }

    ScriptRunningReply() {
        this.zeroCoded = false
    }

    fun CalcPayloadSize(): Int {
        return 37
    }

    fun Handle(SLMessageHandler sLMessageHandler)  {
        sLMessageHandler.HandleScriptRunningReply(this)
    }

    fun PackPayload(ByteBuffer byteBuffer)  {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) -12)
        packUUID(byteBuffer, this.Script_Field.ObjectID)
        packUUID(byteBuffer, this.Script_Field.ItemID)
        packBoolean(byteBuffer, this.Script_Field.Running)
    }

    fun UnpackPayload(ByteBuffer byteBuffer)  {
        this.Script_Field.ObjectID = unpackUUID(byteBuffer)
        this.Script_Field.ItemID = unpackUUID(byteBuffer)
        this.Script_Field.Running = unpackBoolean(byteBuffer)
    }
}
