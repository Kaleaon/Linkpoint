package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class UpdateInventoryItemMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var sessionId: UUID = UUID(0L, 0L)
    var transactionId: UUID = UUID(0L, 0L)
    val inventoryData: MutableList<InventoryDataBlock> = mutableListOf()

    data class InventoryDataBlock(
        var itemId: UUID = UUID(0L, 0L),
        var folderId: UUID = UUID(0L, 0L),
        var callbackId: Int = 0,
        var creatorId: UUID = UUID(0L, 0L),
        var ownerId: UUID = UUID(0L, 0L),
        var groupId: UUID = UUID(0L, 0L),
        var baseMask: Int = 0,
        var ownerMask: Int = 0,
        var groupMask: Int = 0,
        var everyoneMask: Int = 0,
        var nextOwnerMask: Int = 0,
        var groupOwned: Boolean = false,
        var transactionId: UUID = UUID(0L, 0L),
        var type: Int = 0,
        var invType: Int = 0,
        var flags: Int = 0,
        var saleType: Int = 0,
        var salePrice: Int = 0,
        var name: ByteArray = ByteArray(0),
        var description: ByteArray = ByteArray(0),
        var creationDate: Int = 0,
        var crc: Int = 0
    )


    init {
        zeroCoded = true
    }

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, sessionId)
        packUUID(buffer, transactionId)
        require(inventoryData.size <= 0xFF) { "InventoryData size exceeds 255 (" + inventoryData.size + ")" }
        packByte(buffer, inventoryData.size)
        inventoryData.forEach { entry ->
            packUUID(buffer, entry.itemId)
            packUUID(buffer, entry.folderId)
            packInt(buffer, entry.callbackId)
            packUUID(buffer, entry.creatorId)
            packUUID(buffer, entry.ownerId)
            packUUID(buffer, entry.groupId)
            packInt(buffer, entry.baseMask)
            packInt(buffer, entry.ownerMask)
            packInt(buffer, entry.groupMask)
            packInt(buffer, entry.everyoneMask)
            packInt(buffer, entry.nextOwnerMask)
            packBoolean(buffer, entry.groupOwned)
            packUUID(buffer, entry.transactionId)
            packByte(buffer, entry.type)
            packByte(buffer, entry.invType)
            packInt(buffer, entry.flags)
            packByte(buffer, entry.saleType)
            packInt(buffer, entry.salePrice)
            packVariable(buffer, entry.name, 1)
            packVariable(buffer, entry.description, 1)
            packInt(buffer, entry.creationDate)
            packInt(buffer, entry.crc)
        }
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        sessionId = unpackUUID(buffer)
        transactionId = unpackUUID(buffer)
        run {
            val count = unpackByte(buffer)
            inventoryData.clear()
            repeat(count) {
                val entry = InventoryDataBlock()
                entry.itemId = unpackUUID(buffer)
                entry.folderId = unpackUUID(buffer)
                entry.callbackId = unpackInt(buffer)
                entry.creatorId = unpackUUID(buffer)
                entry.ownerId = unpackUUID(buffer)
                entry.groupId = unpackUUID(buffer)
                entry.baseMask = unpackInt(buffer)
                entry.ownerMask = unpackInt(buffer)
                entry.groupMask = unpackInt(buffer)
                entry.everyoneMask = unpackInt(buffer)
                entry.nextOwnerMask = unpackInt(buffer)
                entry.groupOwned = unpackBoolean(buffer)
                entry.transactionId = unpackUUID(buffer)
                entry.type = unpackByte(buffer)
                entry.invType = unpackByte(buffer)
                entry.flags = unpackInt(buffer)
                entry.saleType = unpackByte(buffer)
                entry.salePrice = unpackInt(buffer)
                entry.name = unpackVariable(buffer, 1)
                entry.description = unpackVariable(buffer, 1)
                entry.creationDate = unpackInt(buffer)
                entry.crc = unpackInt(buffer)
                inventoryData += entry
            }
        }
    }

    override fun getMessageID(): Int = 0xFFFF010A

    override fun getMessageName(): String = "UpdateInventoryItem"
}
