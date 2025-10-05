package com.linkpoint.slproto.messages

import com.google.common.base.Ascii
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class AttachedSoundGainChange : SLMessage() {
    val DataBlock_Field = DataBlock()

    class DataBlock {
        var ObjectID: UUID? = null
        var Gain: Float = 0f
    }

    init {
        zeroCoded = false
    }

    override fun CalcPayloadSize(): Int = 22

    override fun Handle(handler: SLMessageHandler) {
        handler.HandleAttachedSoundGainChange(this)
    }

    override fun PackPayload(buffer: ByteBuffer) {
        buffer.put((-1).toByte())
        buffer.put(Ascii.SO)
        packUUID(buffer, DataBlock_Field.ObjectID)
        packFloat(buffer, DataBlock_Field.Gain)
    }

    override fun UnpackPayload(buffer: ByteBuffer) {
        DataBlock_Field.ObjectID = unpackUUID(buffer)
        DataBlock_Field.Gain = unpackFloat(buffer)
    }
}