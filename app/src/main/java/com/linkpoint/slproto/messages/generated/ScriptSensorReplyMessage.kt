package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.types.LLQuaternion
import com.linkpoint.slproto.types.LLVector3
import java.nio.ByteBuffer
import java.util.UUID

class ScriptSensorReplyMessage : SLMessage() {
    var sourceId: UUID = UUID(0L, 0L)
    val sensedData: MutableList<SensedDataBlock> = mutableListOf()

    data class SensedDataBlock(
        var objectId: UUID = UUID(0L, 0L),
        var ownerId: UUID = UUID(0L, 0L),
        var groupId: UUID = UUID(0L, 0L),
        var position: LLVector3 = LLVector3(),
        var velocity: LLVector3 = LLVector3(),
        var rotation: LLQuaternion = LLQuaternion(),
        var name: ByteArray = ByteArray(0),
        var type: Int = 0,
        var range: Float = 0f
    )


    init {
        zeroCoded = true
    }

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, sourceId)
        require(sensedData.size <= 0xFF) { "SensedData size exceeds 255 (" + sensedData.size + ")" }
        packByte(buffer, sensedData.size)
        sensedData.forEach { entry ->
            packUUID(buffer, entry.objectId)
            packUUID(buffer, entry.ownerId)
            packUUID(buffer, entry.groupId)
            entry.position.pack(buffer)
            entry.velocity.pack(buffer)
            entry.rotation.pack(buffer)
            packVariable(buffer, entry.name, 1)
            packInt(buffer, entry.type)
            packFloat(buffer, entry.range)
        }
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        sourceId = unpackUUID(buffer)
        run {
            val count = unpackByte(buffer)
            sensedData.clear()
            repeat(count) {
                val entry = SensedDataBlock()
                entry.objectId = unpackUUID(buffer)
                entry.ownerId = unpackUUID(buffer)
                entry.groupId = unpackUUID(buffer)
                entry.position = LLVector3.unpack(buffer)
                entry.velocity = LLVector3.unpack(buffer)
                entry.rotation = LLQuaternion.unpack(buffer)
                entry.name = unpackVariable(buffer, 1)
                entry.type = unpackInt(buffer)
                entry.range = unpackFloat(buffer)
                sensedData += entry
            }
        }
    }

    override fun getMessageID(): Int = 0xFFFF00F8

    override fun getMessageName(): String = "ScriptSensorReply"
}
