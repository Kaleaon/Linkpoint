package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer

class GenericStreamingMessage : SLMessage() {
    var method: Int = 0
    var data: ByteArray = ByteArray(0)


    override fun packPayload(buffer: ByteBuffer) {
        packUInt16(buffer, method)
        packVariable(buffer, data, 2)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        method = unpackUInt16(buffer)
        data = unpackVariable(buffer, 2)
    }

    override fun getMessageID(): Int = 0x0000001F

    override fun getMessageName(): String = "GenericStreamingMessage"
}
