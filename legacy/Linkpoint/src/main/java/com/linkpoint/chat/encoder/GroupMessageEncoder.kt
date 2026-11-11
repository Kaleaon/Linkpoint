package com.linkpoint.chat.encoder

import com.linkpoint.linden.llmessage.IMType
import com.linkpoint.protocol.core.AgentIdentity
import com.linkpoint.protocol.messages.SLMessagePackers
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.UUID

/**
 * Group Chat packet encoder.
 * Handles encoding for multi-user group chat messages via SLMessagePackers.packImprovedInstantMessage.
 */
class GroupMessageEncoder(
    initialSequence: Int = 1,
    agentIdentity: AgentIdentity = DEFAULT_AGENT_IDENTITY,
    private var groupUuid: UUID = UUID.fromString("00000000-0000-0000-0000-000000000000")
) : AbstractChatPacketEncoder(initialSequence, agentIdentity) {

    companion object {
        const val GROUP_HEADER_FLAGS: Byte = 0x04
        const val TYPE_GROUP_CHAT: Byte = 0x03

        /**
         * Converts a group UUID into a full 16-byte group context without bit-shifting truncation.
         */
        fun groupUuidToBytes(uuid: UUID): ByteArray {
            val buf = ByteBuffer.allocate(16).order(ByteOrder.BIG_ENDIAN)
            buf.putLong(uuid.mostSignificantBits)
            buf.putLong(uuid.leastSignificantBits)
            return buf.array()
        }

        /**
         * Converts a 16-byte group context back to a UUID.
         */
        fun groupBytesToUuid(bytes: ByteArray): UUID {
            require(bytes.size == 16 || bytes.size == 6) {
                "Group context bytes length must be 16 or 6 bytes, found ${bytes.size}"
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
        groupContext: ByteArray,
        agentIdentity: AgentIdentity = DEFAULT_AGENT_IDENTITY
    ) : this(
        initialSequence = initialSequence,
        agentIdentity = agentIdentity,
        groupUuid = groupBytesToUuid(groupContext)
    )

    constructor(
        initialSequence: Int = 1,
        groupUuid: UUID
    ) : this(
        initialSequence = initialSequence,
        agentIdentity = DEFAULT_AGENT_IDENTITY,
        groupUuid = groupUuid
    )

    fun setGroupUuid(groupUuid: UUID) {
        this.groupUuid = groupUuid
    }

    fun getGroupUuid(): UUID = groupUuid

    override fun getHeaderFlags(): Byte = GROUP_HEADER_FLAGS

    override fun getChannelTypeId(): Byte = TYPE_GROUP_CHAT

    override fun getChannelSessionUuid(): UUID = groupUuid

    override fun getChannelSessionBytes(): ByteArray = groupUuidToBytes(groupUuid)

    override fun buildPayload(message: String, channel: Int): ByteArray {
        val ts = (System.currentTimeMillis() / 1000).toInt()
        return SLMessagePackers.packImprovedInstantMessage(
            identity = agentIdentity,
            fromGroup = false,
            toAgentId = groupUuid,
            dialog = IMType.SESSION_SEND,
            id = groupUuid,
            timestamp = ts,
            fromAgentName = "You",
            message = message,
            binaryBucket = byteArrayOf(0)
        )
    }

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
