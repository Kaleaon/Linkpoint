package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class ImageData : SLMessage {
    ImageDataData ImageDataData_Field = ImageDataData()
    ImageID ImageID_Field = ImageID()

    class ImageDataData {
        byte[] Data
    }

    class ImageID {
        Int Codec
        UUID ID
        Int Packets
        Int Size
    }

    ImageData() {
        this.zeroCoded = false
    }

    Int CalcPayloadSize() {
        return this.ImageDataData_Field.Data.length + 2 + 24
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleImageData(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.put((byte) 9)
        packUUID(byteBuffer, this.ImageID_Field.ID)
        packByte(byteBuffer, (byte) this.ImageID_Field.Codec)
        packInt(byteBuffer, this.ImageID_Field.Size)
        packShort(byteBuffer, (short) this.ImageID_Field.Packets)
        packVariable(byteBuffer, this.ImageDataData_Field.Data, 2)
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.ImageID_Field.ID = unpackUUID(byteBuffer)
        this.ImageID_Field.Codec = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
        this.ImageID_Field.Size = unpackInt(byteBuffer)
        this.ImageID_Field.Packets = unpackShort(byteBuffer) & 65535
        this.ImageDataData_Field.Data = unpackVariable(byteBuffer, 2)
    }
}
