package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class ParcelMediaUpdate : SLMessage() {
    public DataBlockExtended DataBlockExtended_Field = DataBlockExtended()
    public DataBlock DataBlock_Field = DataBlock()

    @JvmStatic
    class DataBlock {
        public Int MediaAutoScale
        public UUID MediaID
        public Byte[] MediaURL
    }

    @JvmStatic
    class DataBlockExtended {
        public Byte[] MediaDesc
        public Int MediaHeight
        public Int MediaLoop
        public Byte[] MediaType
        public Int MediaWidth
    }

    public ParcelMediaUpdate() {
        this.zeroCoded = false
    }

    public Int CalcPayloadSize() {
        return this.DataBlock_Field.MediaURL.length + 1 + 16 + 1 + 4 + this.DataBlockExtended_Field.MediaType.length + 1 + 1 + this.DataBlockExtended_Field.MediaDesc.length + 4 + 4 + 1
    }

    fun Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleParcelMediaUpdate(this)
    }

    fun PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) -92)
        packVariable(byteBuffer, this.DataBlock_Field.MediaURL, 1)
        packUUID(byteBuffer, this.DataBlock_Field.MediaID)
        packByte(byteBuffer, (Byte) this.DataBlock_Field.MediaAutoScale)
        packVariable(byteBuffer, this.DataBlockExtended_Field.MediaType, 1)
        packVariable(byteBuffer, this.DataBlockExtended_Field.MediaDesc, 1)
        packInt(byteBuffer, this.DataBlockExtended_Field.MediaWidth)
        packInt(byteBuffer, this.DataBlockExtended_Field.MediaHeight)
        packByte(byteBuffer, (Byte) this.DataBlockExtended_Field.MediaLoop)
    }

    fun UnpackPayload(ByteBuffer byteBuffer) {
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
