package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class EmailMessageRequestMessage : SLMessage() {
    var objectId: UUID = UUID(0L, 0L)
    var fromAddress: ByteArray = ByteArray(0)
    var subject: ByteArray = ByteArray(0)


    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, objectId)
        packVariable(buffer, fromAddress, 1)
        packVariable(buffer, subject, 1)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        objectId = unpackUUID(buffer)
        fromAddress = unpackVariable(buffer, 1)
        subject = unpackVariable(buffer, 1)
    }

    override fun getMessageID(): Int = 0xFFFF014F

    override fun getMessageName(): String = "EmailMessageRequest"
}
