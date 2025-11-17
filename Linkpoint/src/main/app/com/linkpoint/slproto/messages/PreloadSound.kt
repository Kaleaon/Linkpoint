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

    Int CalcPayloadSize() {
        return (this.DataBlock_Fields.size() * 48) + 3
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandlePreloadSound(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.put((Byte) -1)
        byteBuffer.put((Byte) 15)
        byteBuffer.put((Byte) this.DataBlock_Fields.size())
        for (DataBlock dataBlock : this.DataBlock_Fields) {
            packUUID(byteBuffer, dataBlock.ObjectID)
            packUUID(byteBuffer, dataBlock.OwnerID)
            packUUID(byteBuffer, dataBlock.SoundID)
        }
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        Byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            DataBlock dataBlock = DataBlock()
            dataBlock.ObjectID = unpackUUID(byteBuffer)
            dataBlock.OwnerID = unpackUUID(byteBuffer)
            dataBlock.SoundID = unpackUUID(byteBuffer)
            this.DataBlock_Fields.add(dataBlock)
        }
    }
}
