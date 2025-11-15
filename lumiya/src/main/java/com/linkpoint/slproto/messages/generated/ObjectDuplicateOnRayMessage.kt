package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.types.LLVector3
import java.nio.ByteBuffer
import java.util.UUID

class ObjectDuplicateOnRayMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var sessionId: UUID = UUID(0L, 0L)
    var groupId: UUID = UUID(0L, 0L)
    var rayStart: LLVector3 = LLVector3()
    var rayEnd: LLVector3 = LLVector3()
    var bypassRaycast: Boolean = false
    var rayEndIsIntersection: Boolean = false
    var copyCenters: Boolean = false
    var copyRotates: Boolean = false
    var rayTargetId: UUID = UUID(0L, 0L)
    var duplicateFlags: Int = 0
    val objectData: MutableList<ObjectDataBlock> = mutableListOf()

    data class ObjectDataBlock(
        var objectLocalId: Int = 0
    )


    init {
        zeroCoded = true
    }

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, sessionId)
        packUUID(buffer, groupId)
        rayStart.pack(buffer)
        rayEnd.pack(buffer)
        packBoolean(buffer, bypassRaycast)
        packBoolean(buffer, rayEndIsIntersection)
        packBoolean(buffer, copyCenters)
        packBoolean(buffer, copyRotates)
        packUUID(buffer, rayTargetId)
        packInt(buffer, duplicateFlags)
        require(objectData.size <= 0xFF) { "ObjectData size exceeds 255 (" + objectData.size + ")" }
        packByte(buffer, objectData.size)
        objectData.forEach { entry ->
            packInt(buffer, entry.objectLocalId)
        }
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        sessionId = unpackUUID(buffer)
        groupId = unpackUUID(buffer)
        rayStart = LLVector3.unpack(buffer)
        rayEnd = LLVector3.unpack(buffer)
        bypassRaycast = unpackBoolean(buffer)
        rayEndIsIntersection = unpackBoolean(buffer)
        copyCenters = unpackBoolean(buffer)
        copyRotates = unpackBoolean(buffer)
        rayTargetId = unpackUUID(buffer)
        duplicateFlags = unpackInt(buffer)
        run {
            val count = unpackByte(buffer)
            objectData.clear()
            repeat(count) {
                val entry = ObjectDataBlock()
                entry.objectLocalId = unpackInt(buffer)
                objectData += entry
            }
        }
    }

    override fun getMessageID(): Int = 0xFFFF005B

    override fun getMessageName(): String = "ObjectDuplicateOnRay"
}
