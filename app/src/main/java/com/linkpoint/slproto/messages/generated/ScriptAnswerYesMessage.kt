package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class ScriptAnswerYesMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var sessionId: UUID = UUID(0L, 0L)
    var taskId: UUID = UUID(0L, 0L)
    var itemId: UUID = UUID(0L, 0L)
    var questions: Int = 0


    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, sessionId)
        packUUID(buffer, taskId)
        packUUID(buffer, itemId)
        packInt(buffer, questions)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        sessionId = unpackUUID(buffer)
        taskId = unpackUUID(buffer)
        itemId = unpackUUID(buffer)
        questions = unpackInt(buffer)
    }

    override fun getMessageID(): Int = 0xFFFF0084

    override fun getMessageName(): String = "ScriptAnswerYes"
}
