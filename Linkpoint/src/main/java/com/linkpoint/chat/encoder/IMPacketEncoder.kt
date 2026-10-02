package com.linkpoint.chat.encoder

import java.util.UUID

/**
 * Direct Instant Messaging (IM) packet encoder.
 * Handles encoding for 1-on-1 private instant messages.
 */
class IMPacketEncoder(
    initialSequence: Int = 1,
    private var sessionContext: ByteArray = ByteArray(6)
) : AbstractChatPacketEncoder(initialSequence) {

    companion object {
        const val IM_HEADER_FLAGS: Byte = 0x02
        const val TYPE_DIRECT_IM: Byte = 0x02

        /**
         * Converts a UUID into a 6-byte session header context.
         */
        fun uuidToSessionBytes(uuid: UUID): ByteArray {
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
        require(sessionContext.size == 6) { "Session context must be exactly 6 bytes" }
    }

    constructor(initialSequence: Int = 1, sessionUuid: UUID) : this(
        initialSequence = initialSequence,
        sessionContext = uuidToSessionBytes(sessionUuid)
    )

    fun setSessionUuid(sessionUuid: UUID) {
        this.sessionContext = uuidToSessionBytes(sessionUuid)
    }

    override fun getHeaderFlags(): Byte = IM_HEADER_FLAGS

    override fun getChannelTypeId(): Byte = TYPE_DIRECT_IM

    override fun getChannelSessionBytes(): ByteArray = sessionContext

    /**
     * Helper to encode a direct instant message packet for a specific session.
     */
    fun encodeDirectIM(message: String, sessionUuid: UUID? = null, channel: Int = 0): EncodedChatPacket {
        if (sessionUuid != null) {
            setSessionUuid(sessionUuid)
        }
        return encodePacket(message, channel)
    }
}
