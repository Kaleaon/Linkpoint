package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class ScriptQuestionMessage : SLMessage() {
    var taskId: UUID = UUID(0L, 0L)
    var itemId: UUID = UUID(0L, 0L)
    var objectName: ByteArray = ByteArray(0)
    var objectOwner: ByteArray = ByteArray(0)
    var questions: Int = 0
    var experienceId: UUID = UUID(0L, 0L)


    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, taskId)
        packUUID(buffer, itemId)
        packVariable(buffer, objectName, 1)
        packVariable(buffer, objectOwner, 1)
        packInt(buffer, questions)
        packUUID(buffer, experienceId)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        taskId = unpackUUID(buffer)
        itemId = unpackUUID(buffer)
        objectName = unpackVariable(buffer, 1)
        objectOwner = unpackVariable(buffer, 1)
        questions = unpackInt(buffer)
        experienceId = unpackUUID(buffer)
    }

    override fun getMessageID(): Int = 0xFFFF00BC

    override fun getMessageName(): String = "ScriptQuestion"
}
