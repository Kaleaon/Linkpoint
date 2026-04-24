package com.linkpoint.protocol.messages

import com.linkpoint.protocol.types.getUUID
import com.linkpoint.protocol.types.putUUID
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.UUID

/**
 * Parser/writer coverage for messages that previously lived in the declared-only parity bucket.
 */
object DeclaredMessageSlices {

    data class FetchInventoryDescendentsMessage(
        val agentId: UUID,
        val sessionId: UUID,
        val folderId: UUID,
        val ownerId: UUID,
        val sortOrder: Int,
        val fetchFolders: Boolean,
        val fetchItems: Boolean,
    )

    data class FetchInventoryItemRequest(
        val ownerId: UUID,
        val itemId: UUID,
    )

    data class FetchInventoryMessage(
        val agentId: UUID,
        val sessionId: UUID,
        val entries: List<FetchInventoryItemRequest>,
    )

    data class RequestPayPriceMessage(val objectId: UUID)

    data class DirFindQueryMessage(
        val agentId: UUID,
        val sessionId: UUID,
        val queryId: UUID,
        val queryText: String,
        val queryFlags: Long,
        val queryStart: Int,
    )

    data class GroupTitlesRequestMessage(
        val agentId: UUID,
        val sessionId: UUID,
        val groupId: UUID,
        val requestId: UUID,
    )

    data class MapNameRequestMessage(
        val agentId: UUID,
        val sessionId: UUID,
        val flags: Long,
        val estateId: Long,
        val godlike: Boolean,
        val name: String,
    )

    data class AgentPauseMessage(
        val agentId: UUID,
        val sessionId: UUID,
        val serialNum: Long,
    )

    data class AgentResumeMessage(
        val agentId: UUID,
        val sessionId: UUID,
        val serialNum: Long,
    )

    fun writeFetchInventoryDescendents(message: FetchInventoryDescendentsMessage): ByteArray =
        ByteBuffer.allocate(70).order(ByteOrder.LITTLE_ENDIAN).apply {
            putUUID(message.agentId)
            putUUID(message.sessionId)
            putUUID(message.folderId)
            putUUID(message.ownerId)
            putInt(message.sortOrder)
            put(if (message.fetchFolders) 1 else 0)
            put(if (message.fetchItems) 1 else 0)
        }.array()

    fun parseFetchInventoryDescendents(payload: ByteArray): FetchInventoryDescendentsMessage {
        val buffer = ByteBuffer.wrap(payload).order(ByteOrder.LITTLE_ENDIAN)
        return FetchInventoryDescendentsMessage(
            agentId = buffer.getUUID(),
            sessionId = buffer.getUUID(),
            folderId = buffer.getUUID(),
            ownerId = buffer.getUUID(),
            sortOrder = buffer.int,
            fetchFolders = buffer.get().toInt() != 0,
            fetchItems = buffer.get().toInt() != 0,
        )
    }

    fun writeFetchInventory(message: FetchInventoryMessage): ByteArray {
        val buffer = ByteBuffer.allocate(32 + 1 + (message.entries.size * 32)).order(ByteOrder.LITTLE_ENDIAN)
        buffer.putUUID(message.agentId)
        buffer.putUUID(message.sessionId)
        buffer.put(message.entries.size.toByte())
        message.entries.forEach { entry ->
            buffer.putUUID(entry.ownerId)
            buffer.putUUID(entry.itemId)
        }
        return buffer.array()
    }

    fun parseFetchInventory(payload: ByteArray): FetchInventoryMessage {
        val buffer = ByteBuffer.wrap(payload).order(ByteOrder.LITTLE_ENDIAN)
        val agentId = buffer.getUUID()
        val sessionId = buffer.getUUID()
        val count = buffer.get().toInt() and 0xFF
        val entries = buildList(count) {
            repeat(count) {
                add(FetchInventoryItemRequest(ownerId = buffer.getUUID(), itemId = buffer.getUUID()))
            }
        }
        return FetchInventoryMessage(
            agentId = agentId,
            sessionId = sessionId,
            entries = entries,
        )
    }

    fun writeRequestPayPrice(message: RequestPayPriceMessage): ByteArray =
        ByteBuffer.allocate(16).order(ByteOrder.LITTLE_ENDIAN).apply { putUUID(message.objectId) }.array()

    fun parseRequestPayPrice(payload: ByteArray): RequestPayPriceMessage {
        val buffer = ByteBuffer.wrap(payload).order(ByteOrder.LITTLE_ENDIAN)
        return RequestPayPriceMessage(objectId = buffer.getUUID())
    }

    fun writeDirFindQuery(message: DirFindQueryMessage): ByteArray {
        val queryBytes = message.queryText.toByteArray(Charsets.UTF_8)
        val buffer = ByteBuffer.allocate(32 + 16 + 1 + queryBytes.size + 4 + 4).order(ByteOrder.LITTLE_ENDIAN)
        buffer.putUUID(message.agentId)
        buffer.putUUID(message.sessionId)
        buffer.putUUID(message.queryId)
        buffer.putString1(queryBytes)
        buffer.putInt(message.queryFlags.toInt())
        buffer.putInt(message.queryStart)
        return buffer.array()
    }

    fun parseDirFindQuery(payload: ByteArray): DirFindQueryMessage {
        val buffer = ByteBuffer.wrap(payload).order(ByteOrder.LITTLE_ENDIAN)
        return DirFindQueryMessage(
            agentId = buffer.getUUID(),
            sessionId = buffer.getUUID(),
            queryId = buffer.getUUID(),
            queryText = buffer.readString1(),
            queryFlags = buffer.int.toLong() and 0xFFFFFFFFL,
            queryStart = buffer.int,
        )
    }

    fun writeGroupTitlesRequest(message: GroupTitlesRequestMessage): ByteArray =
        ByteBuffer.allocate(64).order(ByteOrder.LITTLE_ENDIAN).apply {
            putUUID(message.agentId)
            putUUID(message.sessionId)
            putUUID(message.groupId)
            putUUID(message.requestId)
        }.array()

    fun parseGroupTitlesRequest(payload: ByteArray): GroupTitlesRequestMessage {
        val buffer = ByteBuffer.wrap(payload).order(ByteOrder.LITTLE_ENDIAN)
        return GroupTitlesRequestMessage(
            agentId = buffer.getUUID(),
            sessionId = buffer.getUUID(),
            groupId = buffer.getUUID(),
            requestId = buffer.getUUID(),
        )
    }

    fun writeMapNameRequest(message: MapNameRequestMessage): ByteArray {
        val nameBytes = message.name.toByteArray(Charsets.UTF_8)
        val buffer = ByteBuffer.allocate(32 + 4 + 4 + 1 + 1 + nameBytes.size).order(ByteOrder.LITTLE_ENDIAN)
        buffer.putUUID(message.agentId)
        buffer.putUUID(message.sessionId)
        buffer.putInt(message.flags.toInt())
        buffer.putInt(message.estateId.toInt())
        buffer.put(if (message.godlike) 1 else 0)
        buffer.putString1(nameBytes)
        return buffer.array()
    }

    fun parseMapNameRequest(payload: ByteArray): MapNameRequestMessage {
        val buffer = ByteBuffer.wrap(payload).order(ByteOrder.LITTLE_ENDIAN)
        return MapNameRequestMessage(
            agentId = buffer.getUUID(),
            sessionId = buffer.getUUID(),
            flags = buffer.int.toLong() and 0xFFFFFFFFL,
            estateId = buffer.int.toLong() and 0xFFFFFFFFL,
            godlike = buffer.get().toInt() != 0,
            name = buffer.readString1(),
        )
    }

    fun writeAgentPause(message: AgentPauseMessage): ByteArray =
        ByteBuffer.allocate(36).order(ByteOrder.LITTLE_ENDIAN).apply {
            putUUID(message.agentId)
            putUUID(message.sessionId)
            putInt(message.serialNum.toInt())
        }.array()

    fun parseAgentPause(payload: ByteArray): AgentPauseMessage {
        val buffer = ByteBuffer.wrap(payload).order(ByteOrder.LITTLE_ENDIAN)
        return AgentPauseMessage(
            agentId = buffer.getUUID(),
            sessionId = buffer.getUUID(),
            serialNum = buffer.int.toLong() and 0xFFFFFFFFL,
        )
    }

    fun writeAgentResume(message: AgentResumeMessage): ByteArray =
        writeAgentPause(AgentPauseMessage(message.agentId, message.sessionId, message.serialNum))

    fun parseAgentResume(payload: ByteArray): AgentResumeMessage {
        val pause = parseAgentPause(payload)
        return AgentResumeMessage(pause.agentId, pause.sessionId, pause.serialNum)
    }

    fun handle(messageId: Int, rawPacket: ByteArray, onHandled: (String) -> Unit): Boolean {
        val payload = MessageParser.extractPayload(rawPacket) ?: return false
        val summary = when (messageId) {
            MessageIds.FETCH_INVENTORY_DESCENDENTS -> parseFetchInventoryDescendents(payload).let { "FetchInventoryDescendents folder=${it.folderId}" }
            MessageIds.FETCH_INVENTORY -> parseFetchInventory(payload).let { "FetchInventory items=${it.entries.size}" }
            MessageIds.REQUEST_PAY_PRICE -> parseRequestPayPrice(payload).let { "RequestPayPrice object=${it.objectId}" }
            MessageIds.DIR_FIND_QUERY -> parseDirFindQuery(payload).let { "DirFindQuery text='${it.queryText}'" }
            MessageIds.GROUP_TITLES_REQUEST -> parseGroupTitlesRequest(payload).let { "GroupTitlesRequest group=${it.groupId}" }
            MessageIds.MAP_NAME_REQUEST -> parseMapNameRequest(payload).let { "MapNameRequest name='${it.name}'" }
            MessageIds.AGENT_PAUSE -> parseAgentPause(payload).let { "AgentPause serial=${it.serialNum}" }
            MessageIds.AGENT_RESUME -> parseAgentResume(payload).let { "AgentResume serial=${it.serialNum}" }
            else -> return false
        }
        onHandled(summary)
        return true
    }

    private fun ByteBuffer.readString1(): String {
        val len = get().toInt() and 0xFF
        if (len == 0) return ""
        val bytes = ByteArray(len - 1)
        get(bytes)
        get()
        return String(bytes, Charsets.UTF_8)
    }

    private fun ByteBuffer.putString1(content: ByteArray) {
        put((content.size + 1).toByte())
        put(content)
        put(0)
    }
}
