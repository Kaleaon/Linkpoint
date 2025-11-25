package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class GroupNoticeAddMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var toGroupId: UUID = UUID(0L, 0L)
    var id: UUID = UUID(0L, 0L)
    var dialog: Int = 0
    var fromAgentName: ByteArray = ByteArray(0)
    var message: ByteArray = ByteArray(0)
    var binaryBucket: ByteArray = ByteArray(0)


    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, toGroupId)
        packUUID(buffer, id)
        packByte(buffer, dialog)
        packVariable(buffer, fromAgentName, 1)
        packVariable(buffer, message, 2)
        packVariable(buffer, binaryBucket, 2)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        toGroupId = unpackUUID(buffer)
        id = unpackUUID(buffer)
        dialog = unpackByte(buffer)
        fromAgentName = unpackVariable(buffer, 1)
        message = unpackVariable(buffer, 2)
        binaryBucket = unpackVariable(buffer, 2)
    }

    override fun getMessageID(): Int = 0xFFFF003D.toInt()

    override fun getMessageName(): String = "GroupNoticeAdd"
}
