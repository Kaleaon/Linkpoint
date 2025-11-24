package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.types.LLVector3
import java.nio.ByteBuffer
import java.util.UUID

class RezObjectFromNotecardMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var sessionId: UUID = UUID(0L, 0L)
    var groupId: UUID = UUID(0L, 0L)
    var fromTaskId: UUID = UUID(0L, 0L)
    var bypassRaycast: Int = 0
    var rayStart: LLVector3 = LLVector3()
    var rayEnd: LLVector3 = LLVector3()
    var rayTargetId: UUID = UUID(0L, 0L)
    var rayEndIsIntersection: Boolean = false
    var rezSelected: Boolean = false
    var removeItem: Boolean = false
    var itemFlags: Int = 0
    var groupMask: Int = 0
    var everyoneMask: Int = 0
    var nextOwnerMask: Int = 0
    var notecardItemId: UUID = UUID(0L, 0L)
    var objectId: UUID = UUID(0L, 0L)
    val inventoryData: MutableList<InventoryDataBlock> = mutableListOf()

    data class InventoryDataBlock(
        var itemId: UUID = UUID(0L, 0L)
    )


    init {
        zeroCoded = true
    }

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, sessionId)
        packUUID(buffer, groupId)
        packUUID(buffer, fromTaskId)
        packByte(buffer, bypassRaycast)
        rayStart.pack(buffer)
        rayEnd.pack(buffer)
        packUUID(buffer, rayTargetId)
        packBoolean(buffer, rayEndIsIntersection)
        packBoolean(buffer, rezSelected)
        packBoolean(buffer, removeItem)
        packInt(buffer, itemFlags)
        packInt(buffer, groupMask)
        packInt(buffer, everyoneMask)
        packInt(buffer, nextOwnerMask)
        packUUID(buffer, notecardItemId)
        packUUID(buffer, objectId)
        require(inventoryData.size <= 0xFF) { "InventoryData size exceeds 255 (" + inventoryData.size + ")" }
        packByte(buffer, inventoryData.size)
        inventoryData.forEach { entry ->
            packUUID(buffer, entry.itemId)
        }
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        sessionId = unpackUUID(buffer)
        groupId = unpackUUID(buffer)
        fromTaskId = unpackUUID(buffer)
        bypassRaycast = unpackByte(buffer)
        rayStart = LLVector3.unpack(buffer)
        rayEnd = LLVector3.unpack(buffer)
        rayTargetId = unpackUUID(buffer)
        rayEndIsIntersection = unpackBoolean(buffer)
        rezSelected = unpackBoolean(buffer)
        removeItem = unpackBoolean(buffer)
        itemFlags = unpackInt(buffer)
        groupMask = unpackInt(buffer)
        everyoneMask = unpackInt(buffer)
        nextOwnerMask = unpackInt(buffer)
        notecardItemId = unpackUUID(buffer)
        objectId = unpackUUID(buffer)
        run {
            val count = unpackByte(buffer)
            inventoryData.clear()
            repeat(count) {
                val entry = InventoryDataBlock()
                entry.itemId = unpackUUID(buffer)
                inventoryData += entry
            }
        }
    }

    override fun getMessageID(): Int = 0xFFFF0126.toInt()

    override fun getMessageName(): String = "RezObjectFromNotecard"
}
