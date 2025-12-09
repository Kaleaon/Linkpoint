package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class GetScriptRunning : SLMessage {
    Script Script_Field = Script()

    class Script {
        UUID ItemID
        UUID ObjectID
    }

    GetScriptRunning() {
        this.zeroCoded = false
    }

    fun CalcPayloadSize(): Int {
        return 36
    }

    fun Handle(SLMessageHandler sLMessageHandler): Unit {
        sLMessageHandler.HandleGetScriptRunning(this)
    }

    fun PackPayload(ByteBuffer byteBuffer): Unit {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 0)
        byteBuffer.put((byte) -13)
        packUUID(byteBuffer, this.Script_Field.ObjectID)
        packUUID(byteBuffer, this.Script_Field.ItemID)
    }

    fun UnpackPayload(ByteBuffer byteBuffer): Unit {
        this.Script_Field.ObjectID = unpackUUID(byteBuffer)
        this.Script_Field.ItemID = unpackUUID(byteBuffer)
    }
}
