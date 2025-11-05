package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
import java.nio.ByteBuffer

class StartPingCheck : SLMessage() {

    val PingID_Field: PingID = PingID()

    data class PingID(
        var PingID: Int = 0,
        var OldestUnacked: Int = 0,
    )

    init {
        zeroCoded = false
    }

    override fun CalcPayloadSize(): Int = 6

    override fun handleMessage(handler: SLMessageHandler) {
        handler.HandleStartPingCheck(this)
    }

    override fun PackPayload(buffer: ByteBuffer) {
        buffer.put(1) // Number of PingID blocks
        packByte(buffer, PingID_Field.PingID.toByte())
        packInt(buffer, PingID_Field.OldestUnacked)
    }

    override fun UnpackPayload(buffer: ByteBuffer) {
        val blockCount = buffer.get().toInt() and 0xFF
        if (blockCount > 0) {
            PingID_Field.PingID = unpackByte(buffer).toInt() and 0xFF
            PingID_Field.OldestUnacked = unpackInt(buffer)
        }
    }
}
