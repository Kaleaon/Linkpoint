package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class RpcScriptReplyInboundMessage : SLMessage() {
    var taskId: UUID = UUID(0L, 0L)
    var itemId: UUID = UUID(0L, 0L)
    var channelId: UUID = UUID(0L, 0L)
    var intValue: Int = 0
    var stringValue: ByteArray = ByteArray(0)


    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, taskId)
        packUUID(buffer, itemId)
        packUUID(buffer, channelId)
        packInt(buffer, intValue)
        packVariable(buffer, stringValue, 2)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        taskId = unpackUUID(buffer)
        itemId = unpackUUID(buffer)
        channelId = unpackUUID(buffer)
        intValue = unpackInt(buffer)
        stringValue = unpackVariable(buffer, 2)
    }

    override fun getMessageID(): Int = 0xFFFF01A1.toInt()

    override fun getMessageName(): String = "RpcScriptReplyInbound"
}
