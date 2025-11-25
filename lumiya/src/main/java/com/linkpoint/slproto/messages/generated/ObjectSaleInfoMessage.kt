package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class ObjectSaleInfoMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var sessionId: UUID = UUID(0L, 0L)
    val objectData: MutableList<ObjectDataBlock> = mutableListOf()

    data class ObjectDataBlock(
        var localId: Int = 0,
        var saleType: Int = 0,
        var salePrice: Int = 0
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
            packInt(buffer, entry.localId)
            packByte(buffer, entry.saleType)
            packInt(buffer, entry.salePrice)
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
                entry.localId = unpackInt(buffer)
                entry.saleType = unpackByte(buffer)
                entry.salePrice = unpackInt(buffer)
                objectData += entry
            }
        }
    }

    override fun getMessageID(): Int = 0xFFFF006A.toInt()

    override fun getMessageName(): String = "ObjectSaleInfo"
}
