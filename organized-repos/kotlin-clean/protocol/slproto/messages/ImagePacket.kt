package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class ImagePacket : SLMessage() {
    public ImageData ImageData_Field = ImageData()
    public ImageID ImageID_Field = ImageID()

    @JvmStatic
    class ImageData {
        public Byte[] Data
    }

    @JvmStatic
    class ImageID {
        public UUID ID
        public Int Packet
    }

    public ImagePacket() {
        this.zeroCoded = false
    }

    public Int CalcPayloadSize() {
        return this.ImageData_Field.Data.length + 2 + 19
    }

    fun Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleImagePacket(this)
    }

    fun PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.put((Byte) 10)
        packUUID(byteBuffer, this.ImageID_Field.ID)
        packShort(byteBuffer, (Short) this.ImageID_Field.Packet)
        packVariable(byteBuffer, this.ImageData_Field.Data, 2)
    }

    fun UnpackPayload(ByteBuffer byteBuffer) {
        this.ImageID_Field.ID = unpackUUID(byteBuffer)
        this.ImageID_Field.Packet = unpackShort(byteBuffer) & 65535
        this.ImageData_Field.Data = unpackVariable(byteBuffer, 2)
    }
}
