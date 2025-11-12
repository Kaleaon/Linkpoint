package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class DeRezObjectMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var sessionId: UUID = UUID(0L, 0L)
    var groupId: UUID = UUID(0L, 0L)
    var destination: Int = 0
    var destinationId: UUID = UUID(0L, 0L)
    var transactionId: UUID = UUID(0L, 0L)
    var packetCount: Int = 0
    var packetNumber: Int = 0
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
        packByte(buffer, destination)
        packUUID(buffer, destinationId)
        packUUID(buffer, transactionId)
        packByte(buffer, packetCount)
        packByte(buffer, packetNumber)
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
        destination = unpackByte(buffer)
        destinationId = unpackUUID(buffer)
        transactionId = unpackUUID(buffer)
        packetCount = unpackByte(buffer)
        packetNumber = unpackByte(buffer)
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

    override fun getMessageID(): Int = 0xFFFF0123

    override fun getMessageName(): String = "DeRezObject"
}
