package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer

class ParcelOverlayMessage : SLMessage() {
    var sequenceId: Int = 0
    var data: ByteArray = ByteArray(0)


    init {
        zeroCoded = true
    }

    override fun packPayload(buffer: ByteBuffer) {
        packInt(buffer, sequenceId)
        packVariable(buffer, data, 2)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        sequenceId = unpackInt(buffer)
        data = unpackVariable(buffer, 2)
    }

    override fun getMessageID(): Int = 0xFFFF00C4.toInt()

    override fun getMessageName(): String = "ParcelOverlay"
}
