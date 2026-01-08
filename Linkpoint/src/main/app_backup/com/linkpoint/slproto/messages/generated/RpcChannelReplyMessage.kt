package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class RpcChannelReplyMessage : SLMessage() {
    var taskId: UUID = UUID(0L, 0L)
    var itemId: UUID = UUID(0L, 0L)
    var channelId: UUID = UUID(0L, 0L)


    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, taskId)
        packUUID(buffer, itemId)
        packUUID(buffer, channelId)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        taskId = unpackUUID(buffer)
        itemId = unpackUUID(buffer)
        channelId = unpackUUID(buffer)
    }

    override fun getMessageID(): Int = 0xFFFF019E.toInt()

    override fun getMessageName(): String = "RpcChannelReply"
}
