package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class ViewerEffectMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var sessionId: UUID = UUID(0L, 0L)
    val effect: MutableList<EffectBlock> = mutableListOf()

    data class EffectBlock(
        var id: UUID = UUID(0L, 0L),
        var agentId: UUID = UUID(0L, 0L),
        var type: Int = 0,
        var duration: Float = 0f,
        var color: ByteArray = ByteArray(4),
        var typeData: ByteArray = ByteArray(0)
    )


    init {
        zeroCoded = true
    }

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, sessionId)
        require(effect.size <= 0xFF) { "Effect size exceeds 255 (" + effect.size + ")" }
        packByte(buffer, effect.size)
        effect.forEach { entry ->
            packUUID(buffer, entry.id)
            packUUID(buffer, entry.agentId)
            packByte(buffer, entry.type)
            packFloat(buffer, entry.duration)
            packFixed(buffer, entry.color, 4)
            packVariable(buffer, entry.typeData, 1)
        }
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        sessionId = unpackUUID(buffer)
        run {
            val count = unpackByte(buffer)
            effect.clear()
            repeat(count) {
                val entry = EffectBlock()
                entry.id = unpackUUID(buffer)
                entry.agentId = unpackUUID(buffer)
                entry.type = unpackByte(buffer)
                entry.duration = unpackFloat(buffer)
                entry.color = unpackFixed(buffer, 4)
                entry.typeData = unpackVariable(buffer, 1)
                effect += entry
            }
        }
    }

    override fun getMessageID(): Int = 0x0000FF11

    override fun getMessageName(): String = "ViewerEffect"
}
