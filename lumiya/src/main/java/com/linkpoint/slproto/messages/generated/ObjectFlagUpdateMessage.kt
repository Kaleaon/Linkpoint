package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class ObjectFlagUpdateMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var sessionId: UUID = UUID(0L, 0L)
    var objectLocalId: Int = 0
    var usePhysics: Boolean = false
    var isTemporary: Boolean = false
    var isPhantom: Boolean = false
    var castsShadows: Boolean = false
    val extraPhysics: MutableList<ExtraPhysicsBlock> = mutableListOf()

    data class ExtraPhysicsBlock(
        var physicsShapeType: Int = 0,
        var density: Float = 0f,
        var friction: Float = 0f,
        var restitution: Float = 0f,
        var gravityMultiplier: Float = 0f
    )


    init {
        zeroCoded = true
    }

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, sessionId)
        packInt(buffer, objectLocalId)
        packBoolean(buffer, usePhysics)
        packBoolean(buffer, isTemporary)
        packBoolean(buffer, isPhantom)
        packBoolean(buffer, castsShadows)
        require(extraPhysics.size <= 0xFF) { "ExtraPhysics size exceeds 255 (" + extraPhysics.size + ")" }
        packByte(buffer, extraPhysics.size)
        extraPhysics.forEach { entry ->
            packByte(buffer, entry.physicsShapeType)
            packFloat(buffer, entry.density)
            packFloat(buffer, entry.friction)
            packFloat(buffer, entry.restitution)
            packFloat(buffer, entry.gravityMultiplier)
        }
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        sessionId = unpackUUID(buffer)
        objectLocalId = unpackInt(buffer)
        usePhysics = unpackBoolean(buffer)
        isTemporary = unpackBoolean(buffer)
        isPhantom = unpackBoolean(buffer)
        castsShadows = unpackBoolean(buffer)
        run {
            val count = unpackByte(buffer)
            extraPhysics.clear()
            repeat(count) {
                val entry = ExtraPhysicsBlock()
                entry.physicsShapeType = unpackByte(buffer)
                entry.density = unpackFloat(buffer)
                entry.friction = unpackFloat(buffer)
                entry.restitution = unpackFloat(buffer)
                entry.gravityMultiplier = unpackFloat(buffer)
                extraPhysics += entry
            }
        }
    }

    override fun getMessageID(): Int = 0xFFFF005E.toInt()

    override fun getMessageName(): String = "ObjectFlagUpdate"
}
