package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class ObjectShapeMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var sessionId: UUID = UUID(0L, 0L)
    val objectData: MutableList<ObjectDataBlock> = mutableListOf()

    data class ObjectDataBlock(
        var objectLocalId: Int = 0,
        var pathCurve: Int = 0,
        var profileCurve: Int = 0,
        var pathBegin: Int = 0,
        var pathEnd: Int = 0,
        var pathScaleX: Int = 0,
        var pathScaleY: Int = 0,
        var pathShearX: Int = 0,
        var pathShearY: Int = 0,
        var pathTwist: Int = 0,
        var pathTwistBegin: Int = 0,
        var pathRadiusOffset: Int = 0,
        var pathTaperX: Int = 0,
        var pathTaperY: Int = 0,
        var pathRevolutions: Int = 0,
        var pathSkew: Int = 0,
        var profileBegin: Int = 0,
        var profileEnd: Int = 0,
        var profileHollow: Int = 0
    )


    init {
        zeroCoded = true
    }

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, sessionId)
        require(objectData.size <= 0xFF) { "ObjectData size exceeds 255 (" + objectData.size + ")" }
        packByte(buffer, objectData.size)
        objectData.forEach { entry ->
            packInt(buffer, entry.objectLocalId)
            packByte(buffer, entry.pathCurve)
            packByte(buffer, entry.profileCurve)
            packUInt16(buffer, entry.pathBegin)
            packUInt16(buffer, entry.pathEnd)
            packByte(buffer, entry.pathScaleX)
            packByte(buffer, entry.pathScaleY)
            packByte(buffer, entry.pathShearX)
            packByte(buffer, entry.pathShearY)
            packByte(buffer, entry.pathTwist)
            packByte(buffer, entry.pathTwistBegin)
            packByte(buffer, entry.pathRadiusOffset)
            packByte(buffer, entry.pathTaperX)
            packByte(buffer, entry.pathTaperY)
            packByte(buffer, entry.pathRevolutions)
            packByte(buffer, entry.pathSkew)
            packUInt16(buffer, entry.profileBegin)
            packUInt16(buffer, entry.profileEnd)
            packUInt16(buffer, entry.profileHollow)
        }
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        sessionId = unpackUUID(buffer)
        run {
            val count = unpackByte(buffer)
            objectData.clear()
            repeat(count) {
                val entry = ObjectDataBlock()
                entry.objectLocalId = unpackInt(buffer)
                entry.pathCurve = unpackByte(buffer)
                entry.profileCurve = unpackByte(buffer)
                entry.pathBegin = unpackUInt16(buffer)
                entry.pathEnd = unpackUInt16(buffer)
                entry.pathScaleX = unpackByte(buffer)
                entry.pathScaleY = unpackByte(buffer)
                entry.pathShearX = unpackByte(buffer)
                entry.pathShearY = unpackByte(buffer)
                entry.pathTwist = unpackByte(buffer)
                entry.pathTwistBegin = unpackByte(buffer)
                entry.pathRadiusOffset = unpackByte(buffer)
                entry.pathTaperX = unpackByte(buffer)
                entry.pathTaperY = unpackByte(buffer)
                entry.pathRevolutions = unpackByte(buffer)
                entry.pathSkew = unpackByte(buffer)
                entry.profileBegin = unpackUInt16(buffer)
                entry.profileEnd = unpackUInt16(buffer)
                entry.profileHollow = unpackUInt16(buffer)
                objectData += entry
            }
        }
    }

    override fun getMessageID(): Int = 0xFFFF0062.toInt()

    override fun getMessageName(): String = "ObjectShape"
}
