package com.linkpoint.chat.encoder

import com.linkpoint.linden.llmessage.IMType
import com.linkpoint.protocol.core.AgentIdentity
import com.linkpoint.protocol.messages.SLMessagePackers
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.UUID

/**
 * Direct Instant Messaging (IM) packet encoder.
 * Handles encoding for 1-on-1 private instant messages via SLMessagePackers.packImprovedInstantMessage.
 */
class IMPacketEncoder(
    initialSequence: Int = 1,
    agentIdentity: AgentIdentity = DEFAULT_AGENT_IDENTITY,
    private var sessionUuid: UUID = UUID.fromString("00000000-0000-0000-0000-000000000000")
) : AbstractChatPacketEncoder(initialSequence, agentIdentity) {

    companion object {
        const val IM_HEADER_FLAGS: Byte = 0x02
        const val TYPE_DIRECT_IM: Byte = 0x02

        /**
         * Converts a UUID into a full 16-byte session context without bit-shifting truncation.
         */
        fun uuidToSessionBytes(uuid: UUID): ByteArray {
            val buf = ByteBuffer.allocate(16).order(ByteOrder.BIG_ENDIAN)
            buf.putLong(uuid.mostSignificantBits)
            buf.putLong(uuid.leastSignificantBits)
            return buf.array()
        }

        /**
         * Converts a 16-byte session context back to a UUID.
         */
        fun sessionBytesToUuid(bytes: ByteArray): UUID {
            require(bytes.size == 16 || bytes.size == 6) {
                "Session bytes length must be 16 or 6 bytes, found ${bytes.size}"
            }
            if (bytes.size == 6) {
                var msb = 0L
                for (i in 0 until 6) {
                    msb = (msb shl 8) or (bytes[i].toLong() and 0xFF)
                }
                return UUID(msb shl 16, 0L)
            }
            val buf = ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN)
            return UUID(buf.long, buf.long)
        }
    }

    constructor(
        initialSequence: Int = 1,
        sessionContext: ByteArray,
        agentIdentity: AgentIdentity = DEFAULT_AGENT_IDENTITY
    ) : this(
        initialSequence = initialSequence,
        agentIdentity = agentIdentity,
        sessionUuid = sessionBytesToUuid(sessionContext)
    )

    constructor(
        initialSequence: Int = 1,
        sessionUuid: UUID
    ) : this(
        initialSequence = initialSequence,
        agentIdentity = DEFAULT_AGENT_IDENTITY,
        sessionUuid = sessionUuid
    )

    fun setSessionUuid(sessionUuid: UUID) {
        this.sessionUuid = sessionUuid
    }

    fun getSessionUuid(): UUID = sessionUuid

    override fun getHeaderFlags(): Byte = IM_HEADER_FLAGS

    override fun getChannelTypeId(): Byte = TYPE_DIRECT_IM

    override fun getChannelSessionUuid(): UUID = sessionUuid

    override fun getChannelSessionBytes(): ByteArray = uuidToSessionBytes(sessionUuid)

    override fun buildPayload(message: String, channel: Int): ByteArray {
        val ts = (System.currentTimeMillis() / 1000).toInt()
        return SLMessagePackers.packImprovedInstantMessage(
            identity = agentIdentity,
            fromGroup = false,
            toAgentId = sessionUuid,
            dialog = IMType.NOTHING_SPECIAL,
            id = sessionUuid,
            timestamp = ts,
            fromAgentName = "You",
            message = message,
            binaryBucket = ByteArray(0)
        )
    }

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
