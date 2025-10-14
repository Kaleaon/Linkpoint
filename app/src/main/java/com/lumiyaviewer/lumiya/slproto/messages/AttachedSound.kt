package com.lumiyaviewer.lumiya.slproto.messages

import com.google.common.base.Ascii
import com.google.common.primitives.UnsignedBytes
import com.lumiyaviewer.lumiya.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class AttachedSound : SLMessage {
    DataBlock DataBlock_Field = DataBlock()

    class DataBlock {
        Int Flags
        float Gain
        UUID ObjectID
        UUID OwnerID
        UUID SoundID
    }

    AttachedSound() {
        this.zeroCoded = false
    }

    Int CalcPayloadSize() {
        return 55
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleAttachedSound(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.put((byte) -1)
        byteBuffer.put(Ascii.CR)
        packUUID(byteBuffer, this.DataBlock_Field.SoundID)
        packUUID(byteBuffer, this.DataBlock_Field.ObjectID)
        packUUID(byteBuffer, this.DataBlock_Field.OwnerID)
        packFloat(byteBuffer, this.DataBlock_Field.Gain)
        packByte(byteBuffer, (byte) this.DataBlock_Field.Flags)
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.DataBlock_Field.SoundID = unpackUUID(byteBuffer)
        this.DataBlock_Field.ObjectID = unpackUUID(byteBuffer)
        this.DataBlock_Field.OwnerID = unpackUUID(byteBuffer)
        this.DataBlock_Field.Gain = unpackFloat(byteBuffer)
        this.DataBlock_Field.Flags = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
    }
}
