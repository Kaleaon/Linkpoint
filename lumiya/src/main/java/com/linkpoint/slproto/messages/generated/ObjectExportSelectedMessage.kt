package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class ObjectExportSelectedMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var requestId: UUID = UUID(0L, 0L)
    var volumeDetail: Int = 0
    val objectData: MutableList<ObjectDataBlock> = mutableListOf()

    data class ObjectDataBlock(
        var objectId: UUID = UUID(0L, 0L)
    )


    init {
        zeroCoded = true
    }

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, requestId)
        packShort(buffer, volumeDetail)
        require(objectData.size <= 0xFF) { "ObjectData size exceeds 255 (" + objectData.size + ")" }
        packByte(buffer, objectData.size)
        objectData.forEach { entry ->
            packUUID(buffer, entry.objectId)
        }
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        requestId = unpackUUID(buffer)
        volumeDetail = unpackShort(buffer)
        run {
            val count = unpackByte(buffer)
            objectData.clear()
            repeat(count) {
                val entry = ObjectDataBlock()
                entry.objectId = unpackUUID(buffer)
                objectData += entry
            }
        }
    }

    override fun getMessageID(): Int = 0xFFFF007B.toInt()

    override fun getMessageName(): String = "ObjectExportSelected"
}
