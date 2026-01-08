package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class AvatarAnimationMessage : SLMessage() {
    var id: UUID = UUID(0L, 0L)
    val animationList: MutableList<AnimationListBlock> = mutableListOf()
    val animationSourceList: MutableList<AnimationSourceListBlock> = mutableListOf()
    val physicalAvatarEventList: MutableList<PhysicalAvatarEventListBlock> = mutableListOf()

    data class AnimationListBlock(
        var animId: UUID = UUID(0L, 0L),
        var animSequenceId: Int = 0
    )
    data class AnimationSourceListBlock(
        var objectId: UUID = UUID(0L, 0L)
    )
    data class PhysicalAvatarEventListBlock(
        var typeData: ByteArray = ByteArray(0)
    )


    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, id)
        require(animationList.size <= 0xFF) { "AnimationList size exceeds 255 (" + animationList.size + ")" }
        packByte(buffer, animationList.size)
        animationList.forEach { entry ->
            packUUID(buffer, entry.animId)
            packInt(buffer, entry.animSequenceId)
        }
        require(animationSourceList.size <= 0xFF) { "AnimationSourceList size exceeds 255 (" + animationSourceList.size + ")" }
        packByte(buffer, animationSourceList.size)
        animationSourceList.forEach { entry ->
            packUUID(buffer, entry.objectId)
        }
        require(physicalAvatarEventList.size <= 0xFF) { "PhysicalAvatarEventList size exceeds 255 (" + physicalAvatarEventList.size + ")" }
        packByte(buffer, physicalAvatarEventList.size)
        physicalAvatarEventList.forEach { entry ->
            packVariable(buffer, entry.typeData, 1)
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
        run {
            val count = unpackByte(buffer)
            animationSourceList.clear()
            repeat(count) {
                val entry = AnimationSourceListBlock()
                entry.objectId = unpackUUID(buffer)
                animationSourceList += entry
            }
        }
        run {
            val count = unpackByte(buffer)
            physicalAvatarEventList.clear()
            repeat(count) {
                val entry = PhysicalAvatarEventListBlock()
                entry.typeData = unpackVariable(buffer, 1)
                physicalAvatarEventList += entry
            }
        }
    }

    override fun getMessageID(): Int = 0x00000014

    override fun getMessageName(): String = "AvatarAnimation"
}
