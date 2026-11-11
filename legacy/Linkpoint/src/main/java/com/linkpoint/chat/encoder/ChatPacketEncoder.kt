package com.linkpoint.chat.encoder

import com.linkpoint.protocol.core.AgentIdentity
import com.linkpoint.protocol.messages.SLMessagePackers
import java.util.UUID

/**
 * Spatial / Local chat packet encoder.
 * Handles encoding for nearby and spatial in-world chat messages via SLMessagePackers.packChatFromViewer.
 */
class ChatPacketEncoder(
    initialSequence: Int = 1,
    agentIdentity: AgentIdentity = DEFAULT_AGENT_IDENTITY
) : AbstractChatPacketEncoder(initialSequence, agentIdentity) {

    companion object {
        const val SPATIAL_HEADER_FLAGS: Byte = 0x01
        const val TYPE_SPATIAL_CHAT: Byte = 0x01
    }

    override fun getHeaderFlags(): Byte = SPATIAL_HEADER_FLAGS

    override fun getChannelTypeId(): Byte = TYPE_SPATIAL_CHAT

    override fun getChannelSessionUuid(): UUID? = null

    override fun getChannelSessionBytes(): ByteArray = ByteArray(16)

    override fun buildPayload(message: String, channel: Int): ByteArray {
        return SLMessagePackers.packChatFromViewer(
            identity = agentIdentity,
            message = message,
            type = TYPE_SPATIAL_CHAT.toInt(),
            channel = channel
        )
    }

    /**
     * Helper to encode spatial chat message.
     */
    fun encodeSpatialChat(message: String, channel: Int = 0): EncodedChatPacket {
        return encodePacket(message, channel)
    }
}
