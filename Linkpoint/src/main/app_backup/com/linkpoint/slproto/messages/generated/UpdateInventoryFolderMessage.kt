package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class UpdateInventoryFolderMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var sessionId: UUID = UUID(0L, 0L)
    val folderData: MutableList<FolderDataBlock> = mutableListOf()

    data class FolderDataBlock(
        var folderId: UUID = UUID(0L, 0L),
        var parentId: UUID = UUID(0L, 0L),
        var type: Int = 0,
        var name: ByteArray = ByteArray(0)
    )


    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, sessionId)
        require(folderData.size <= 0xFF) { "FolderData size exceeds 255 (" + folderData.size + ")" }
        packByte(buffer, folderData.size)
        folderData.forEach { entry ->
            packUUID(buffer, entry.folderId)
            packUUID(buffer, entry.parentId)
            packByte(buffer, entry.type)
            packVariable(buffer, entry.name, 1)
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
                entry.parentId = unpackUUID(buffer)
                entry.type = unpackByte(buffer)
                entry.name = unpackVariable(buffer, 1)
                folderData += entry
            }
        }
    }

    override fun getMessageID(): Int = 0xFFFF0112.toInt()

    override fun getMessageName(): String = "UpdateInventoryFolder"
}
