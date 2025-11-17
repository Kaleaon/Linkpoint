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

    Int CalcPayloadSize() {
        return 36
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleGetScriptRunning(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 0)
        byteBuffer.put((byte) -13)
        packUUID(byteBuffer, this.Script_Field.ObjectID)
        packUUID(byteBuffer, this.Script_Field.ItemID)
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.Script_Field.ObjectID = unpackUUID(byteBuffer)
        this.Script_Field.ItemID = unpackUUID(byteBuffer)
    }
}
