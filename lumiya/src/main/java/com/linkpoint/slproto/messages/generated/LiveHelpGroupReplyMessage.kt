package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class LiveHelpGroupReplyMessage : SLMessage() {
    var requestId: UUID = UUID(0L, 0L)
    var groupId: UUID = UUID(0L, 0L)
    var selection: ByteArray = ByteArray(0)


    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, requestId)
        packUUID(buffer, groupId)
        packVariable(buffer, selection, 1)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        requestId = unpackUUID(buffer)
        groupId = unpackUUID(buffer)
        selection = unpackVariable(buffer, 1)
    }

    override fun getMessageID(): Int = 0xFFFF017C

    override fun getMessageName(): String = "LiveHelpGroupReply"
}
