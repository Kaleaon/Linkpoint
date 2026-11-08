package com.linkpoint.chat.encoder

import java.util.UUID

/**
 * Group Chat packet encoder.
 * Handles encoding for multi-user group chat messages.
 */
class GroupMessageEncoder(
    initialSequence: Int = 1,
    private var groupContext: ByteArray = ByteArray(6)
) : AbstractChatPacketEncoder(initialSequence) {

    companion object {
        const val GROUP_HEADER_FLAGS: Byte = 0x04
        const val TYPE_GROUP_CHAT: Byte = 0x03

        /**
         * Converts a group UUID into a 6-byte group header context.
         */
        fun groupUuidToBytes(uuid: UUID): ByteArray {
            val bytes = ByteArray(6)
            val msb = uuid.mostSignificantBits
            bytes[0] = ((msb shr 40) and 0xFF).toByte()
            bytes[1] = ((msb shr 32) and 0xFF).toByte()
            bytes[2] = ((msb shr 24) and 0xFF).toByte()
            bytes[3] = ((msb shr 16) and 0xFF).toByte()
            bytes[4] = ((msb shr 8) and 0xFF).toByte()
            bytes[5] = (msb and 0xFF).toByte()
            return bytes
        }
    }

    init {
        require(groupContext.size == 6) { "Group context must be exactly 6 bytes" }
    }

    constructor(initialSequence: Int = 1, groupUuid: UUID) : this(
        initialSequence = initialSequence,
        groupContext = groupUuidToBytes(groupUuid)
    )

    fun setGroupUuid(groupUuid: UUID) {
        this.groupContext = groupUuidToBytes(groupUuid)
    }

    override fun getHeaderFlags(): Byte = GROUP_HEADER_FLAGS

    override fun getChannelTypeId(): Byte = TYPE_GROUP_CHAT

    override fun getChannelSessionBytes(): ByteArray = groupContext

    /**
     * Helper to encode a group chat packet for a specific group.
     */
    fun encodeGroupMessage(message: String, groupUuid: UUID? = null, channel: Int = 0): EncodedChatPacket {
        if (groupUuid != null) {
            setGroupUuid(groupUuid)
        }
        return encodePacket(message, channel)
    }
}
