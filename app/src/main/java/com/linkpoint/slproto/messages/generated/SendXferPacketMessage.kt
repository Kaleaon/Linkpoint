package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer

class SendXferPacketMessage : SLMessage() {
    var id: Long = 0L
    var packet: Int = 0
    var data: ByteArray = ByteArray(0)


    override fun packPayload(buffer: ByteBuffer) {
        packLong(buffer, id)
        packInt(buffer, packet)
        packVariable(buffer, data, 2)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        id = unpackLong(buffer)
        packet = unpackInt(buffer)
        data = unpackVariable(buffer, 2)
    }

    override fun getMessageID(): Int = 0x00000012

    override fun getMessageName(): String = "SendXferPacket"
}
