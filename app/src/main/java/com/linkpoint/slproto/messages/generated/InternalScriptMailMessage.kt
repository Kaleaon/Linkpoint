package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class InternalScriptMailMessage : SLMessage() {
    var from_: ByteArray = ByteArray(0)
    var to: UUID = UUID(0L, 0L)
    var subject: ByteArray = ByteArray(0)
    var body: ByteArray = ByteArray(0)


    override fun packPayload(buffer: ByteBuffer) {
        packVariable(buffer, from_, 1)
        packUUID(buffer, to)
        packVariable(buffer, subject, 1)
        packVariable(buffer, body, 2)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        from_ = unpackVariable(buffer, 1)
        to = unpackUUID(buffer)
        subject = unpackVariable(buffer, 1)
        body = unpackVariable(buffer, 2)
    }

    override fun getMessageID(): Int = 0x0000FF10

    override fun getMessageName(): String = "InternalScriptMail"
}
