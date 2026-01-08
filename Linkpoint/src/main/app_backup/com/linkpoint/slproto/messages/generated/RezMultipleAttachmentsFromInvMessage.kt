package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class RezMultipleAttachmentsFromInvMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var sessionId: UUID = UUID(0L, 0L)
    var compoundMsgId: UUID = UUID(0L, 0L)
    var totalObjects: Int = 0
    var firstDetachAll: Boolean = false
    val objectData: MutableList<ObjectDataBlock> = mutableListOf()

    data class ObjectDataBlock(
        var itemId: UUID = UUID(0L, 0L),
        var ownerId: UUID = UUID(0L, 0L),
        var attachmentPt: Int = 0,
        var itemFlags: Int = 0,
        var groupMask: Int = 0,
        var everyoneMask: Int = 0,
        var nextOwnerMask: Int = 0,
        var name: ByteArray = ByteArray(0),
        var description: ByteArray = ByteArray(0)
    )


    init {
        zeroCoded = true
    }

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, sessionId)
        packUUID(buffer, compoundMsgId)
        packByte(buffer, totalObjects)
        packBoolean(buffer, firstDetachAll)
        require(objectData.size <= 0xFF) { "ObjectData size exceeds 255 (" + objectData.size + ")" }
        packByte(buffer, objectData.size)
        objectData.forEach { entry ->
            packUUID(buffer, entry.itemId)
            packUUID(buffer, entry.ownerId)
            packByte(buffer, entry.attachmentPt)
            packInt(buffer, entry.itemFlags)
            packInt(buffer, entry.groupMask)
            packInt(buffer, entry.everyoneMask)
            packInt(buffer, entry.nextOwnerMask)
            packVariable(buffer, entry.name, 1)
            packVariable(buffer, entry.description, 1)
        }
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        sessionId = unpackUUID(buffer)
        compoundMsgId = unpackUUID(buffer)
        totalObjects = unpackByte(buffer)
        firstDetachAll = unpackBoolean(buffer)
        run {
            val count = unpackByte(buffer)
            objectData.clear()
            repeat(count) {
                val entry = ObjectDataBlock()
                entry.itemId = unpackUUID(buffer)
                entry.ownerId = unpackUUID(buffer)
                entry.attachmentPt = unpackByte(buffer)
                entry.itemFlags = unpackInt(buffer)
                entry.groupMask = unpackInt(buffer)
                entry.everyoneMask = unpackInt(buffer)
                entry.nextOwnerMask = unpackInt(buffer)
                entry.name = unpackVariable(buffer, 1)
                entry.description = unpackVariable(buffer, 1)
                objectData += entry
            }
        }
    }

    override fun getMessageID(): Int = 0xFFFF018C.toInt()

    override fun getMessageName(): String = "RezMultipleAttachmentsFromInv"
}
