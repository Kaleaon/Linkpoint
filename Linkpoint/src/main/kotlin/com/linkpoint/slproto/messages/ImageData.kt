package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class ImageData : SLMessage() {
    public ImageDataData ImageDataData_Field = ImageDataData()
    public ImageID ImageID_Field = ImageID()

    @JvmStatic
    class ImageDataData {
        public Byte[] Data
    }

    @JvmStatic
    class ImageID {
        public Int Codec
        public UUID ID
        public Int Packets
        public Int Size
    }

    public ImageData() {
        this.zeroCoded = false
    }

    public Int CalcPayloadSize() {
        return this.ImageDataData_Field.Data.length + 2 + 24
    }

    fun Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleImageData(this)
    }

    fun PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.put((Byte) 9)
        packUUID(byteBuffer, this.ImageID_Field.ID)
        packByte(byteBuffer, (Byte) this.ImageID_Field.Codec)
        packInt(byteBuffer, this.ImageID_Field.Size)
        packShort(byteBuffer, (Short) this.ImageID_Field.Packets)
        packVariable(byteBuffer, this.ImageDataData_Field.Data, 2)
    }

    fun UnpackPayload(ByteBuffer byteBuffer) {
        this.ImageID_Field.ID = unpackUUID(byteBuffer)
        this.ImageID_Field.Codec = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
        this.ImageID_Field.Size = unpackInt(byteBuffer)
        this.ImageID_Field.Packets = unpackShort(byteBuffer) & 65535
        this.ImageDataData_Field.Data = unpackVariable(byteBuffer, 2)
    }
}
