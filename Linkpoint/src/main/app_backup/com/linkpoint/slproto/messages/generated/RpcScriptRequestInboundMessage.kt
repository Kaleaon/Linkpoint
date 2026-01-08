package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class RpcScriptRequestInboundMessage : SLMessage() {
    var gridX: Int = 0
    var gridY: Int = 0
    var taskId: UUID = UUID(0L, 0L)
    var itemId: UUID = UUID(0L, 0L)
    var channelId: UUID = UUID(0L, 0L)
    var intValue: Int = 0
    var stringValue: ByteArray = ByteArray(0)


    override fun packPayload(buffer: ByteBuffer) {
        packInt(buffer, gridX)
        packInt(buffer, gridY)
        packUUID(buffer, taskId)
        packUUID(buffer, itemId)
        packUUID(buffer, channelId)
        packInt(buffer, intValue)
        packVariable(buffer, stringValue, 2)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        gridX = unpackInt(buffer)
        gridY = unpackInt(buffer)
        taskId = unpackUUID(buffer)
        itemId = unpackUUID(buffer)
        channelId = unpackUUID(buffer)
        intValue = unpackInt(buffer)
        stringValue = unpackVariable(buffer, 2)
    }

    override fun getMessageID(): Int = 0xFFFF019F.toInt()

    override fun getMessageName(): String = "RpcScriptRequestInbound"
}
