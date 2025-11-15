package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class EmailMessageReplyMessage : SLMessage() {
    var objectId: UUID = UUID(0L, 0L)
    var more: Int = 0
    var time: Int = 0
    var fromAddress: ByteArray = ByteArray(0)
    var subject: ByteArray = ByteArray(0)
    var data: ByteArray = ByteArray(0)
    var mailFilter: ByteArray = ByteArray(0)


    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, objectId)
        packInt(buffer, more)
        packInt(buffer, time)
        packVariable(buffer, fromAddress, 1)
        packVariable(buffer, subject, 1)
        packVariable(buffer, data, 2)
        packVariable(buffer, mailFilter, 1)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        objectId = unpackUUID(buffer)
        more = unpackInt(buffer)
        time = unpackInt(buffer)
        fromAddress = unpackVariable(buffer, 1)
        subject = unpackVariable(buffer, 1)
        data = unpackVariable(buffer, 2)
        mailFilter = unpackVariable(buffer, 1)
    }

    override fun getMessageID(): Int = 0xFFFF0150

    override fun getMessageName(): String = "EmailMessageReply"
}
