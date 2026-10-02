package com.linkpoint.chat.encoder

/**
 * Spatial / Local chat packet encoder.
 * Handles encoding for nearby and spatial in-world chat messages.
 */
class ChatPacketEncoder(
    initialSequence: Int = 1
) : AbstractChatPacketEncoder(initialSequence) {

    companion object {
        const val SPATIAL_HEADER_FLAGS: Byte = 0x01
        const val TYPE_SPATIAL_CHAT: Byte = 0x01
    }

    override fun getHeaderFlags(): Byte = SPATIAL_HEADER_FLAGS

    override fun getChannelTypeId(): Byte = TYPE_SPATIAL_CHAT

    override fun getChannelSessionBytes(): ByteArray = ByteArray(6)

    /**
     * Helper to encode spatial chat message.
     */
    fun encodeSpatialChat(message: String, channel: Int = 0): EncodedChatPacket {
        return encodePacket(message, channel)
    }
}
