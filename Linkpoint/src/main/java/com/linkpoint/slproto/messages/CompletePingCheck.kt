package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer

class CompletePingCheck : SLMessage() {
    val PingID_Field = PingID()

    class PingID {
        var PingID: Int = 0
    }

    init {
        zeroCoded = false
    }

    override fun CalcPayloadSize(): Int {
        return 2
    }

    override fun Handle(handler: SLMessageHandler) {
        handler.HandleCompletePingCheck(this)
    }

    override fun PackPayload(buffer: ByteBuffer) {
        buffer.put(2.toByte())
        packByte(buffer, PingID_Field.PingID.toByte())
    }

    override fun UnpackPayload(buffer: ByteBuffer) {
        PingID_Field.PingID = unpackByte(buffer).toInt() and UnsignedBytes.MAX_VALUE.toInt()
    }
}