package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer

class ConfirmXferPacket : SLMessage {
    XferID XferID_Field = XferID()

    class XferID {
        Long ID
        Int Packet
    }

    ConfirmXferPacket() {
        this.zeroCoded = false
    }

    fun CalcPayloadSize(): Int {
        return 13
    }

    fun Handle(SLMessageHandler sLMessageHandler)  {
        sLMessageHandler.HandleConfirmXferPacket(this)
    }

    fun PackPayload(ByteBuffer byteBuffer)  {
        byteBuffer.put((byte) 19)
        packLong(byteBuffer, this.XferID_Field.ID)
        packInt(byteBuffer, this.XferID_Field.Packet)
    }

    fun UnpackPayload(ByteBuffer byteBuffer)  {
        this.XferID_Field.ID = unpackLong(byteBuffer)
        this.XferID_Field.Packet = unpackInt(byteBuffer)
    }
}
