package com.linkpoint.chat.encoder

import com.linkpoint.protocol.core.AgentIdentity
import java.util.UUID
import java.util.concurrent.atomic.AtomicInteger

/**
 * Abstract base class for chat packet serialization across spatial chat,
 * direct instant messaging, and group chat channels.
 *
 * Encapsulates sequence tracking, standard Second Life 32-byte AgentData header framing,
 * and delegates SL UDP message body serialization to [com.linkpoint.protocol.messages.SLMessagePackers]
 * using the Template Method pattern.
 */
abstract class AbstractChatPacketEncoder(
    initialSequence: Int = 1,
    var agentIdentity: AgentIdentity = DEFAULT_AGENT_IDENTITY
) {
    private val sequenceCounter = AtomicInteger(initialSequence)

    companion object {
        const val HEADER_SIZE = 32
        const val MAX_PAYLOAD_SIZE = 4096

        val DEFAULT_AGENT_IDENTITY = AgentIdentity(
            agentId = UUID.fromString("00000000-0000-0000-0000-000000000001"),
            sessionId = UUID.fromString("00000000-0000-0000-0000-000000000002")
        )
    }

    /**
     * Template method: Channel-specific header flags (Byte 0).
     */
    abstract fun getHeaderFlags(): Byte

    /**
     * Template method: Channel type identifier (Byte 5).
     */
    abstract fun getChannelTypeId(): Byte

    /**
     * Template method: Channel or session context identifier as full 128-bit UUID.
     */
    abstract fun getChannelSessionUuid(): UUID?

    /**
     * Template method: Channel or session context identifier as 16-byte array.
     */
    abstract fun getChannelSessionBytes(): ByteArray

    /**
     * Builds the standard Second Life UDP wire format body payload using SLMessagePackers.
     */
    protected abstract fun buildPayload(message: String, channel: Int): ByteArray

    /**
     * Resets or sets the sequence counter state safely.
     */
    fun setSequenceNumber(sequence: Int) {
        sequenceCounter.set(sequence)
    }

    /**
     * Gets current sequence number without incrementing.
     */
    fun getCurrentSequenceNumber(): Int = sequenceCounter.get()

    /**
     * Increments and returns the next sequence number in a thread-safe manner.
     */
    protected fun getNextSequenceNumber(): Int = sequenceCounter.getAndIncrement()

    /**
     * Encodes a chat message payload into a fully framed network packet.
     */
    fun encodePacket(message: String, channel: Int = 0): EncodedChatPacket {
        require(message.length <= MAX_PAYLOAD_SIZE) {
            "Message length exceeds maximum allowed limit of $MAX_PAYLOAD_SIZE characters"
        }

        val seq = getNextSequenceNumber()
        val payload = buildPayload(message, channel)
        val sessionUuid = getChannelSessionUuid()

        return EncodedChatPacket(
            sequenceNumber = seq,
            headerFlags = getHeaderFlags(),
            channelTypeId = getChannelTypeId(),
            channel = channel,
            payload = payload,
            sessionUuid = sessionUuid,
            groupUuid = if (getHeaderFlags() == GroupMessageEncoder.GROUP_HEADER_FLAGS) sessionUuid else null
        )
    }
}

/**
 * Data class representing a fully encoded chat packet.
 */
data class EncodedChatPacket(
    val sequenceNumber: Int,
    val headerFlags: Byte,
    val channelTypeId: Byte,
    val channel: Int,
    val payload: ByteArray,
    val sessionUuid: UUID? = null,
    val groupUuid: UUID? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is EncodedChatPacket) return false
        return sequenceNumber == other.sequenceNumber &&
                headerFlags == other.headerFlags &&
                channelTypeId == other.channelTypeId &&
                channel == other.channel &&
                payload.contentEquals(other.payload) &&
                sessionUuid == other.sessionUuid &&
                groupUuid == other.groupUuid
    }

    override fun hashCode(): Int {
        var result = sequenceNumber
        result = 31 * result + headerFlags
        result = 31 * result + channelTypeId
        result = 31 * result + channel
        result = 31 * result + payload.contentHashCode()
        result = 31 * result + (sessionUuid?.hashCode() ?: 0)
        result = 31 * result + (groupUuid?.hashCode() ?: 0)
        return result
    }
}
