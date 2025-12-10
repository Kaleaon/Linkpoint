package com.linkpoint.slproto.messages

import com.google.common.base.Ascii
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class AttachedSoundGainChange : SLMessage {
    DataBlock DataBlock_Field = DataBlock()

    class DataBlock {
        float Gain
        UUID ObjectID
    }

    AttachedSoundGainChange() {
        this.zeroCoded = false
    }

    fun CalcPayloadSize(): Int {
        return 22
    }

    fun Handle(SLMessageHandler sLMessageHandler): Unit {
        sLMessageHandler.HandleAttachedSoundGainChange(this)
    }

    fun PackPayload(ByteBuffer byteBuffer): Unit {
        byteBuffer.put((byte) -1)
        byteBuffer.put(Ascii.SO)
        packUUID(byteBuffer, this.DataBlock_Field.ObjectID)
        packFloat(byteBuffer, this.DataBlock_Field.Gain)
    }

    fun UnpackPayload(ByteBuffer byteBuffer): Unit {
        this.DataBlock_Field.ObjectID = unpackUUID(byteBuffer)
        this.DataBlock_Field.Gain = unpackFloat(byteBuffer)
    }
}
