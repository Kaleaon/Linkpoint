package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class RemoveInventoryObjectsMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var sessionId: UUID = UUID(0L, 0L)
    val folderData: MutableList<FolderDataBlock> = mutableListOf()
    val itemData: MutableList<ItemDataBlock> = mutableListOf()

    data class FolderDataBlock(
        var folderId: UUID = UUID(0L, 0L)
    )
    data class ItemDataBlock(
        var itemId: UUID = UUID(0L, 0L)
    )


    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, sessionId)
        require(folderData.size <= 0xFF) { "FolderData size exceeds 255 (" + folderData.size + ")" }
        packByte(buffer, folderData.size)
        folderData.forEach { entry ->
            packUUID(buffer, entry.folderId)
        }
        require(itemData.size <= 0xFF) { "ItemData size exceeds 255 (" + itemData.size + ")" }
        packByte(buffer, itemData.size)
        itemData.forEach { entry ->
            packUUID(buffer, entry.itemId)
        }
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        sessionId = unpackUUID(buffer)
        run {
            val count = unpackByte(buffer)
            folderData.clear()
            repeat(count) {
                val entry = FolderDataBlock()
                entry.folderId = unpackUUID(buffer)
                folderData += entry
            }
        }
        run {
            val count = unpackByte(buffer)
            itemData.clear()
            repeat(count) {
                val entry = ItemDataBlock()
                entry.itemId = unpackUUID(buffer)
                itemData += entry
            }
        }
    }

    override fun getMessageID(): Int = 0xFFFF011C.toInt()

    override fun getMessageName(): String = "RemoveInventoryObjects"
}
