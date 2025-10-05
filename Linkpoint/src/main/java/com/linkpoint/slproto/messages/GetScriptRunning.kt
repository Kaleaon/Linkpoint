package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class GetScriptRunning : SLMessage() {
    val Script_Field = Script()

    class Script {
        var ObjectID: UUID? = null
        var ItemID: UUID? = null
    }

    init {
        zeroCoded = false
    }

    override fun CalcPayloadSize(): Int = 36

    override fun Handle(handler: SLMessageHandler) {
        handler.HandleGetScriptRunning(this)
    }

    override fun PackPayload(buffer: ByteBuffer) {
        buffer.putShort(-1)
        buffer.put(0.toByte())
        buffer.put((-13).toByte())
        packUUID(buffer, Script_Field.ObjectID)
        packUUID(buffer, Script_Field.ItemID)
    }

    override fun UnpackPayload(buffer: ByteBuffer) {
        Script_Field.ObjectID = unpackUUID(buffer)
        Script_Field.ItemID = unpackUUID(buffer)
    }
}