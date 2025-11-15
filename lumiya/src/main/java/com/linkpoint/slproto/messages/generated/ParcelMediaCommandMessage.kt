package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer

class ParcelMediaCommandMessage : SLMessage() {
    var flags: Int = 0
    var command: Int = 0
    var time: Float = 0f


    override fun packPayload(buffer: ByteBuffer) {
        packInt(buffer, flags)
        packInt(buffer, command)
        packFloat(buffer, time)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        flags = unpackInt(buffer)
        command = unpackInt(buffer)
        time = unpackFloat(buffer)
    }

    override fun getMessageID(): Int = 0xFFFF01A3

    override fun getMessageName(): String = "ParcelMediaCommandMessage"
}
