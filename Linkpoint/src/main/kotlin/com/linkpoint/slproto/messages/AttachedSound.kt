package com.linkpoint.slproto.messages

import com.google.common.base.Ascii
import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class AttachedSound : SLMessage() {
    public DataBlock DataBlock_Field = DataBlock()

    @JvmStatic
    class DataBlock {
        public Int Flags
        public Float Gain
        public UUID ObjectID
        public UUID OwnerID
        public UUID SoundID
    }

    public AttachedSound() {
        this.zeroCoded = false
    }

    public fun CalcPayloadSize(): Int {
        return 55
    }

    fun Handle(sLMessageHandler: SLMessageHandler) {
        sLMessageHandler.HandleAttachedSound(this)
    }

    fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.put((Byte) -1)
        byteBuffer.put(Ascii.CR)
        packUUID(byteBuffer, this.DataBlock_Field.SoundID)
        packUUID(byteBuffer, this.DataBlock_Field.ObjectID)
        packUUID(byteBuffer, this.DataBlock_Field.OwnerID)
        packFloat(byteBuffer, this.DataBlock_Field.Gain)
        packByte(byteBuffer, (Byte) this.DataBlock_Field.Flags)
    }

    fun UnpackPayload(byteBuffer: ByteBuffer) {
        this.DataBlock_Field.SoundID = unpackUUID(byteBuffer)
        this.DataBlock_Field.ObjectID = unpackUUID(byteBuffer)
        this.DataBlock_Field.OwnerID = unpackUUID(byteBuffer)
        this.DataBlock_Field.Gain = unpackFloat(byteBuffer)
        this.DataBlock_Field.Flags = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
    }
}
