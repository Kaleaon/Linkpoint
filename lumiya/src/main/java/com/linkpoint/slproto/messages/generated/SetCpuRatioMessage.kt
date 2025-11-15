package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer

class SetCpuRatioMessage : SLMessage() {
    var ratio: Int = 0


    override fun packPayload(buffer: ByteBuffer) {
        packByte(buffer, ratio)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        ratio = unpackByte(buffer)
    }

    override fun getMessageID(): Int = 0xFFFF0147

    override fun getMessageName(): String = "SetCPURatio"
}
