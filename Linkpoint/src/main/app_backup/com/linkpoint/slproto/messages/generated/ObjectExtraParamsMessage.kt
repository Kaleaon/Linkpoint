package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class ObjectExtraParamsMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var sessionId: UUID = UUID(0L, 0L)
    val objectData: MutableList<ObjectDataBlock> = mutableListOf()

    data class ObjectDataBlock(
        var objectLocalId: Int = 0,
        var paramType: Int = 0,
        var paramInUse: Boolean = false,
        var paramSize: Int = 0,
        var paramData: ByteArray = ByteArray(0)
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
            packUInt16(buffer, entry.paramType)
            packBoolean(buffer, entry.paramInUse)
            packInt(buffer, entry.paramSize)
            packVariable(buffer, entry.paramData, 1)
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
                entry.paramType = unpackUInt16(buffer)
                entry.paramInUse = unpackBoolean(buffer)
                entry.paramSize = unpackInt(buffer)
                entry.paramData = unpackVariable(buffer, 1)
                objectData += entry
            }
        }
    }

    override fun getMessageID(): Int = 0xFFFF0063.toInt()

    override fun getMessageName(): String = "ObjectExtraParams"
}
