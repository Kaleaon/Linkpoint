package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer

class ConfirmXferPacketMessage : SLMessage() {
    var id: Long = 0L
    var packet: Int = 0


    override fun packPayload(buffer: ByteBuffer) {
        packLong(buffer, id)
        packInt(buffer, packet)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        id = unpackLong(buffer)
        packet = unpackInt(buffer)
    }

    override fun getMessageID(): Int = 0x00000013

    override fun getMessageName(): String = "ConfirmXferPacket"
}
