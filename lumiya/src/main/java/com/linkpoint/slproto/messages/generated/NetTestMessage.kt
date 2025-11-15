package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer

class NetTestMessage : SLMessage() {
    var port: Int = 0


    override fun packPayload(buffer: ByteBuffer) {
        packUInt16(buffer, port)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        port = unpackUInt16(buffer)
    }

    override fun getMessageID(): Int = 0xFFFF0146

    override fun getMessageName(): String = "NetTest"
}
