package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class EjectGroupMemberRequestMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var sessionId: UUID = UUID(0L, 0L)
    var groupId: UUID = UUID(0L, 0L)
    val ejectData: MutableList<EjectDataBlock> = mutableListOf()

    data class EjectDataBlock(
        var ejecteeId: UUID = UUID(0L, 0L)
    )


    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, sessionId)
        packUUID(buffer, groupId)
        require(ejectData.size <= 0xFF) { "EjectData size exceeds 255 (" + ejectData.size + ")" }
        packByte(buffer, ejectData.size)
        ejectData.forEach { entry ->
            packUUID(buffer, entry.ejecteeId)
        }
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        sessionId = unpackUUID(buffer)
        groupId = unpackUUID(buffer)
        run {
            val count = unpackByte(buffer)
            ejectData.clear()
            repeat(count) {
                val entry = EjectDataBlock()
                entry.ejecteeId = unpackUUID(buffer)
                ejectData += entry
            }
        }
    }

    override fun getMessageID(): Int = 0xFFFF0159.toInt()

    override fun getMessageName(): String = "EjectGroupMemberRequest"
}
