package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class RpcChannelRequestMessage : SLMessage() {
    var gridX: Int = 0
    var gridY: Int = 0
    var taskId: UUID = UUID(0L, 0L)
    var itemId: UUID = UUID(0L, 0L)


    override fun packPayload(buffer: ByteBuffer) {
        packInt(buffer, gridX)
        packInt(buffer, gridY)
        packUUID(buffer, taskId)
        packUUID(buffer, itemId)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        gridX = unpackInt(buffer)
        gridY = unpackInt(buffer)
        taskId = unpackUUID(buffer)
        itemId = unpackUUID(buffer)
    }

    override fun getMessageID(): Int = 0xFFFF019D

    override fun getMessageName(): String = "RpcChannelRequest"
}
