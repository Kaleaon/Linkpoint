package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
import java.nio.ByteBuffer

class CompletePingCheck : SLMessage() {

    val PingID_Field: PingID = PingID()

    data class PingID(var PingID: Int = 0)

    init {
        zeroCoded = false
    }

    override fun CalcPayloadSize(): Int = 2

    override fun handleMessage(handler: SLMessageHandler) {
        handler.HandleCompletePingCheck(this)
    }

    override fun PackPayload(buffer: ByteBuffer) {
        buffer.put(1) // Number of PingID blocks
        packByte(buffer, PingID_Field.PingID.toByte())
    }

    override fun UnpackPayload(buffer: ByteBuffer) {
        val blockCount = buffer.get().toInt() and 0xFF
        if (blockCount > 0) {
            PingID_Field.PingID = unpackByte(buffer).toInt() and 0xFF
        }
    }
}
