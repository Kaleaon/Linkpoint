package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class RemoveInventoryFolderMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var sessionId: UUID = UUID(0L, 0L)
    val folderData: MutableList<FolderDataBlock> = mutableListOf()

    data class FolderDataBlock(
        var folderId: UUID = UUID(0L, 0L)
    )


    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, sessionId)
        require(folderData.size <= 0xFF) { "FolderData size exceeds 255 (" + folderData.size + ")" }
        packByte(buffer, folderData.size)
        folderData.forEach { entry ->
            packUUID(buffer, entry.folderId)
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
    }

    override fun getMessageID(): Int = 0xFFFF0114.toInt()

    override fun getMessageName(): String = "RemoveInventoryFolder"
}
