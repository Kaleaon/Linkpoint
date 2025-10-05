package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer

class StartPingCheck : SLMessage() {
    val PingID_Field = PingID()

    class PingID {
        var PingID: Int = 0
        var OldestUnacked: Int = 0
    }

    init {
        zeroCoded = false
    }

    override fun CalcPayloadSize(): Int = 6

    override fun Handle(handler: SLMessageHandler) {
        handler.HandleStartPingCheck(this)
    }

    override fun PackPayload(buffer: ByteBuffer) {
        buffer.put(1.toByte())
        packByte(buffer, PingID_Field.PingID.toByte())
        packInt(buffer, PingID_Field.OldestUnacked)
    }

    override fun UnpackPayload(buffer: ByteBuffer) {
        PingID_Field.PingID = unpackByte(buffer).toInt() and UnsignedBytes.MAX_VALUE.toInt()
        PingID_Field.OldestUnacked = unpackInt(buffer)
    }
}