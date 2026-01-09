package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class ImagePacket : SLMessage {
    ImageData ImageData_Field = ImageData()
    ImageID ImageID_Field = ImageID()

    class ImageData {
        ByteArray Data
    }

    class ImageID {
        UUID ID
        Int Packet
    }

    ImagePacket() {
        this.zeroCoded = false
    }

    fun CalcPayloadSize(): Int {
        return this.ImageData_Field.Data.size + 2 + 19
    }

    fun Handle(SLMessageHandler sLMessageHandler)  {
        sLMessageHandler.HandleImagePacket(this)
    }

    fun PackPayload(ByteBuffer byteBuffer)  {
        byteBuffer.put((byte) 10)
        packUUID(byteBuffer, this.ImageID_Field.ID)
        packShort(byteBuffer, (this as short).ImageID_Field.Packet)
        packVariable(byteBuffer, this.ImageData_Field.Data, 2)
    }

    fun UnpackPayload(ByteBuffer byteBuffer)  {
        this.ImageID_Field.ID = unpackUUID(byteBuffer)
        this.ImageID_Field.Packet = unpackShort(byteBuffer) & 65535
        this.ImageData_Field.Data = unpackVariable(byteBuffer, 2)
    }
}
