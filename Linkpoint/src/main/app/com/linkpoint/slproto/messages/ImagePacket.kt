package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class ImagePacket : SLMessage {
    ImageData ImageData_Field = ImageData()
    ImageID ImageID_Field = ImageID()

    class ImageData {
        byte[] Data
    }

    class ImageID {
        UUID ID
        Int Packet
    }

    ImagePacket() {
        this.zeroCoded = false
    }

    Int CalcPayloadSize() {
        return this.ImageData_Field.Data.length + 2 + 19
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleImagePacket(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.put((byte) 10)
        packUUID(byteBuffer, this.ImageID_Field.ID)
        packShort(byteBuffer, (short) this.ImageID_Field.Packet)
        packVariable(byteBuffer, this.ImageData_Field.Data, 2)
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.ImageID_Field.ID = unpackUUID(byteBuffer)
        this.ImageID_Field.Packet = unpackShort(byteBuffer) & 65535
        this.ImageData_Field.Data = unpackVariable(byteBuffer, 2)
    }
}
