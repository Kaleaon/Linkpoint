package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer

class SimulatorMapUpdateMessage : SLMessage() {
    var flags: Int = 0


    override fun packPayload(buffer: ByteBuffer) {
        packInt(buffer, flags)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        flags = unpackInt(buffer)
    }

    override fun getMessageID(): Int = 0xFFFF0005

    override fun getMessageName(): String = "SimulatorMapUpdate"
}
