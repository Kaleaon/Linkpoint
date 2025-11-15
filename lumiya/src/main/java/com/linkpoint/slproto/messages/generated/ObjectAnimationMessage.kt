package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class ObjectAnimationMessage : SLMessage() {
    var id: UUID = UUID(0L, 0L)
    val animationList: MutableList<AnimationListBlock> = mutableListOf()

    data class AnimationListBlock(
        var animId: UUID = UUID(0L, 0L),
        var animSequenceId: Int = 0
    )


    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, id)
        require(animationList.size <= 0xFF) { "AnimationList size exceeds 255 (" + animationList.size + ")" }
        packByte(buffer, animationList.size)
        animationList.forEach { entry ->
            packUUID(buffer, entry.animId)
            packInt(buffer, entry.animSequenceId)
        }
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        id = unpackUUID(buffer)
        run {
            val count = unpackByte(buffer)
            animationList.clear()
            repeat(count) {
                val entry = AnimationListBlock()
                entry.animId = unpackUUID(buffer)
                entry.animSequenceId = unpackInt(buffer)
                animationList += entry
            }
        }
    }

    override fun getMessageID(): Int = 0x0000001E

    override fun getMessageName(): String = "ObjectAnimation"
}
