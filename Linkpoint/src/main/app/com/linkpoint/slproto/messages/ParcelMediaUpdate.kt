package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class ParcelMediaUpdate : SLMessage {
    DataBlockExtended DataBlockExtended_Field = DataBlockExtended()
    DataBlock DataBlock_Field = DataBlock()

    class DataBlock {
        Int MediaAutoScale
        UUID MediaID
        ByteArray MediaURL
    }

    class DataBlockExtended {
        ByteArray MediaDesc
        Int MediaHeight
        Int MediaLoop
        ByteArray MediaType
        Int MediaWidth
    }

    ParcelMediaUpdate() {
        this.zeroCoded = false
    }

    fun CalcPayloadSize(): Int {
        return this.DataBlock_Field.MediaURL.size + 1 + 16 + 1 + 4 + this.DataBlockExtended_Field.MediaType.size + 1 + 1 + this.DataBlockExtended_Field.MediaDesc.size + 4 + 4 + 1
    }

    fun Handle(SLMessageHandler sLMessageHandler): Unit {
        sLMessageHandler.HandleParcelMediaUpdate(this)
    }

    fun PackPayload(ByteBuffer byteBuffer): Unit {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) -92)
        packVariable(byteBuffer, this.DataBlock_Field.MediaURL, 1)
        packUUID(byteBuffer, this.DataBlock_Field.MediaID)
        packByte(byteBuffer, (this as Byte).DataBlock_Field.MediaAutoScale)
        packVariable(byteBuffer, this.DataBlockExtended_Field.MediaType, 1)
        packVariable(byteBuffer, this.DataBlockExtended_Field.MediaDesc, 1)
        packInt(byteBuffer, this.DataBlockExtended_Field.MediaWidth)
        packInt(byteBuffer, this.DataBlockExtended_Field.MediaHeight)
        packByte(byteBuffer, (this as Byte).DataBlockExtended_Field.MediaLoop)
    }

    fun UnpackPayload(ByteBuffer byteBuffer): Unit {
        this.DataBlock_Field.MediaURL = unpackVariable(byteBuffer, 1)
        this.DataBlock_Field.MediaID = unpackUUID(byteBuffer)
        this.DataBlock_Field.MediaAutoScale = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
        this.DataBlockExtended_Field.MediaType = unpackVariable(byteBuffer, 1)
        this.DataBlockExtended_Field.MediaDesc = unpackVariable(byteBuffer, 1)
        this.DataBlockExtended_Field.MediaWidth = unpackInt(byteBuffer)
        this.DataBlockExtended_Field.MediaHeight = unpackInt(byteBuffer)
        this.DataBlockExtended_Field.MediaLoop = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
    }
}
