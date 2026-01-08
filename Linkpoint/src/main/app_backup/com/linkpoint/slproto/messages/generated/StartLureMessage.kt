package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class StartLureMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var sessionId: UUID = UUID(0L, 0L)
    var lureType: Int = 0
    var message: ByteArray = ByteArray(0)
    val targetData: MutableList<TargetDataBlock> = mutableListOf()

    data class TargetDataBlock(
        var targetId: UUID = UUID(0L, 0L)
    )


    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, sessionId)
        packByte(buffer, lureType)
        packVariable(buffer, message, 1)
        require(targetData.size <= 0xFF) { "TargetData size exceeds 255 (" + targetData.size + ")" }
        packByte(buffer, targetData.size)
        targetData.forEach { entry ->
            packUUID(buffer, entry.targetId)
        }
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        sessionId = unpackUUID(buffer)
        lureType = unpackByte(buffer)
        message = unpackVariable(buffer, 1)
        run {
            val count = unpackByte(buffer)
            targetData.clear()
            repeat(count) {
                val entry = TargetDataBlock()
                entry.targetId = unpackUUID(buffer)
                targetData += entry
            }
        }
    }

    override fun getMessageID(): Int = 0xFFFF0046.toInt()

    override fun getMessageName(): String = "StartLure"
}
