package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
import com.lumiyaviewer.lumiya.slproto.handler.SLMessageHandler
import java.nio.ByteBuffer

class ConfirmXferPacket : SLMessage {
    var XferID_Field: XferID = XferID()

    class XferID {
        var ID: Long = 0
        var Packet: Long = 0
    }

    init {
        this.zeroCoded = false
    }

    override fun CalcPayloadSize(): Int {
        return 12
    }

    override fun Handle(sLMessageHandler: SLMessageHandler) {
        // sLMessageHandler.HandleConfirmXferPacket(this)
    }

    override fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put(0.toByte())
        byteBuffer.put((-25).toByte())
        packLong(byteBuffer, this.XferID_Field.ID)
        packInt(byteBuffer, this.XferID_Field.Packet.toInt())
    }

    override fun UnpackPayload(byteBuffer: ByteBuffer) {
        this.XferID_Field.ID = unpackLong(byteBuffer)
        this.XferID_Field.Packet = unpackInt(byteBuffer).toLong() and 0xFFFFFFFFL
    }
}
