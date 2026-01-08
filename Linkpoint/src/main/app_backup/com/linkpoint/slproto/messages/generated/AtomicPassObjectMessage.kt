package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class AtomicPassObjectMessage : SLMessage() {
    var taskId: UUID = UUID(0L, 0L)
    var attachmentNeedsSave: Boolean = false


    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, taskId)
        packBoolean(buffer, attachmentNeedsSave)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        taskId = unpackUUID(buffer)
        attachmentNeedsSave = unpackBoolean(buffer)
    }

    override fun getMessageID(): Int = 0x0000001C

    override fun getMessageName(): String = "AtomicPassObject"
}
