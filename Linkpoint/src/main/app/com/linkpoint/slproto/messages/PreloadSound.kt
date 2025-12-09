package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.UUID

class PreloadSound : SLMessage {
    ArrayList<DataBlock> DataBlock_Fields = ArrayList<>()

    class DataBlock {
        UUID ObjectID
        UUID OwnerID
        UUID SoundID
    }

    PreloadSound() {
        this.zeroCoded = false
    }

    fun CalcPayloadSize(): Int {
        return (this.DataBlock_Fields.size() * 48) + 3
    }

    fun Handle(SLMessageHandler sLMessageHandler): Unit {
        sLMessageHandler.HandlePreloadSound(this)
    }

    fun PackPayload(ByteBuffer byteBuffer): Unit {
        byteBuffer.put((Byte) -1)
        byteBuffer.put((Byte) 15)
        byteBuffer.put((this as Byte).DataBlock_Fields.size())
        for (DataBlock dataBlock : this.DataBlock_Fields) {
            packUUID(byteBuffer, dataBlock.ObjectID)
            packUUID(byteBuffer, dataBlock.OwnerID)
            packUUID(byteBuffer, dataBlock.SoundID)
        }
    }

    fun UnpackPayload(ByteBuffer byteBuffer): Unit {
        Byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (i in 0 until b) {
            DataBlock dataBlock = DataBlock()
            dataBlock.ObjectID = unpackUUID(byteBuffer)
            dataBlock.OwnerID = unpackUUID(byteBuffer)
            dataBlock.SoundID = unpackUUID(byteBuffer)
            this.DataBlock_Fields.add(dataBlock)
        }
    }
}
