package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class ReplyTaskInventoryMessage : SLMessage() {
    var taskId: UUID = UUID(0L, 0L)
    var serial: Int = 0
    var filename: ByteArray = ByteArray(0)


    init {
        zeroCoded = true
    }

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, taskId)
        packShort(buffer, serial)
        packVariable(buffer, filename, 1)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        taskId = unpackUUID(buffer)
        serial = unpackShort(buffer)
        filename = unpackVariable(buffer, 1)
    }

    override fun getMessageID(): Int = 0xFFFF0122.toInt()

    override fun getMessageName(): String = "ReplyTaskInventory"
}
