package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.types.LLQuaternion
import java.nio.ByteBuffer
import java.util.UUID

class ObjectAttachMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var sessionId: UUID = UUID(0L, 0L)
    var attachmentPoint: Int = 0
    val objectData: MutableList<ObjectDataBlock> = mutableListOf()

    data class ObjectDataBlock(
        var objectLocalId: Int = 0,
        var rotation: LLQuaternion = LLQuaternion()
    )


    init {
        zeroCoded = true
    }

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, sessionId)
        packByte(buffer, attachmentPoint)
        require(objectData.size <= 0xFF) { "ObjectData size exceeds 255 (" + objectData.size + ")" }
        packByte(buffer, objectData.size)
        objectData.forEach { entry ->
            packInt(buffer, entry.objectLocalId)
            entry.rotation.pack(buffer)
        }
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        sessionId = unpackUUID(buffer)
        attachmentPoint = unpackByte(buffer)
        run {
            val count = unpackByte(buffer)
            objectData.clear()
            repeat(count) {
                val entry = ObjectDataBlock()
                entry.objectLocalId = unpackInt(buffer)
                entry.rotation = LLQuaternion.unpack(buffer)
                objectData += entry
            }
        }
    }

    override fun getMessageID(): Int = 0xFFFF0070

    override fun getMessageName(): String = "ObjectAttach"
}
