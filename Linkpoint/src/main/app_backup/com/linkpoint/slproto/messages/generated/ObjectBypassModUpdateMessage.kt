package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class ObjectBypassModUpdateMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var sessionId: UUID = UUID(0L, 0L)
    val objectData: MutableList<ObjectDataBlock> = mutableListOf()

    data class ObjectDataBlock(
        var objectLocalId: Int = 0,
        var propertyId: Int = 0,
        var value: ByteArray = ByteArray(0)
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
            packByte(buffer, entry.propertyId)
            packVariable(buffer, entry.value, 2)
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
                entry.propertyId = unpackByte(buffer)
                entry.value = unpackVariable(buffer, 2)
                objectData += entry
            }
        }
    }

    override fun getMessageID(): Int = 0xFFFF01AF.toInt()

    override fun getMessageName(): String = "ObjectBypassModUpdate"
}
