package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.UUID

class PreloadSound : SLMessage() {
    public ArrayList<DataBlock> DataBlock_Fields = ArrayList<>()

    @JvmStatic
    class DataBlock {
        public UUID ObjectID
        public UUID OwnerID
        public UUID SoundID
    }

    public PreloadSound() {
        this.zeroCoded = false
    }

    public fun CalcPayloadSize(): Int {
        return (this.DataBlock_Fields.size() * 48) + 3
    }

    fun Handle(sLMessageHandler: SLMessageHandler) {
        sLMessageHandler.HandlePreloadSound(this)
    }

    fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.put((Byte) -1)
        byteBuffer.put((Byte) 15)
        byteBuffer.put((Byte) this.DataBlock_Fields.size())
        for (DataBlock dataBlock : this.DataBlock_Fields) {
            packUUID(byteBuffer, dataBlock.ObjectID)
            packUUID(byteBuffer, dataBlock.OwnerID)
            packUUID(byteBuffer, dataBlock.SoundID)
        }
    }

    fun UnpackPayload(byteBuffer: ByteBuffer) {
        val b: Byte = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            val dataBlock: DataBlock = DataBlock()
            dataBlock.ObjectID = unpackUUID(byteBuffer)
            dataBlock.OwnerID = unpackUUID(byteBuffer)
            dataBlock.SoundID = unpackUUID(byteBuffer)
            this.DataBlock_Fields.add(dataBlock)
        }
    }
}
