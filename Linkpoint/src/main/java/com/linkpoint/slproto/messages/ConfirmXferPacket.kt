package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer

class ConfirmXferPacket : SLMessage() {
    val XferID_Field = XferID()

    class XferID {
        var ID: Long = 0
        var Packet: Int = 0
    }

    init {
        zeroCoded = false
    }

    override fun CalcPayloadSize(): Int = 13

    override fun Handle(handler: SLMessageHandler) {
        handler.HandleConfirmXferPacket(this)
    }

    override fun PackPayload(buffer: ByteBuffer) {
        buffer.put(19.toByte())
        packLong(buffer, XferID_Field.ID)
        packInt(buffer, XferID_Field.Packet)
    }

    override fun UnpackPayload(buffer: ByteBuffer) {
        XferID_Field.ID = unpackLong(buffer)
        XferID_Field.Packet = unpackInt(buffer)
    }
}