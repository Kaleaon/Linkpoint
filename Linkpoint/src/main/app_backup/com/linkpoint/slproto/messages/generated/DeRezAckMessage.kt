package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class DeRezAckMessage : SLMessage() {
    var transactionId: UUID = UUID(0L, 0L)
    var success: Boolean = false


    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, transactionId)
        packBoolean(buffer, success)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        transactionId = unpackUUID(buffer)
        success = unpackBoolean(buffer)
    }

    override fun getMessageID(): Int = 0xFFFF0124.toInt()

    override fun getMessageName(): String = "DeRezAck"
}
