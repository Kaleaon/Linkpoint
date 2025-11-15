package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class ScriptDialogReplyMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var sessionId: UUID = UUID(0L, 0L)
    var objectId: UUID = UUID(0L, 0L)
    var chatChannel: Int = 0
    var buttonIndex: Int = 0
    var buttonLabel: ByteArray = ByteArray(0)


    init {
        zeroCoded = true
    }

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, sessionId)
        packUUID(buffer, objectId)
        packInt(buffer, chatChannel)
        packInt(buffer, buttonIndex)
        packVariable(buffer, buttonLabel, 1)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        sessionId = unpackUUID(buffer)
        objectId = unpackUUID(buffer)
        chatChannel = unpackInt(buffer)
        buttonIndex = unpackInt(buffer)
        buttonLabel = unpackVariable(buffer, 1)
    }

    override fun getMessageID(): Int = 0xFFFF00BF

    override fun getMessageName(): String = "ScriptDialogReply"
}
