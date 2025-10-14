package com.lumiyaviewer.lumiya.slproto.messages

import com.google.common.base.Ascii
import com.lumiyaviewer.lumiya.slproto.SLMessage
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

    Int CalcPayloadSize() {
        return 22
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleAttachedSoundGainChange(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.put((byte) -1)
        byteBuffer.put(Ascii.SO)
        packUUID(byteBuffer, this.DataBlock_Field.ObjectID)
        packFloat(byteBuffer, this.DataBlock_Field.Gain)
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.DataBlock_Field.ObjectID = unpackUUID(byteBuffer)
        this.DataBlock_Field.Gain = unpackFloat(byteBuffer)
    }
}
