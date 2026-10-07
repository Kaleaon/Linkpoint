package com.linkpoint.chat.encoder

import com.linkpoint.protocol.core.AgentIdentity
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.UUID
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Dispatcher service for chat channel network communications.
 *
 * Coordinates packet encoding via [ChatPacketEncoder], [IMPacketEncoder],
 * and [GroupMessageEncoder], verifying standard 32-byte AgentData headers, sequence state,
 * and routing payloads to registered network dispatch listeners.
 */
class ChatNetworkService(
    val spatialEncoder: ChatPacketEncoder = ChatPacketEncoder(),
    val imEncoder: IMPacketEncoder = IMPacketEncoder(),
    val groupEncoder: GroupMessageEncoder = GroupMessageEncoder()
) {
    fun interface PacketDispatchListener {
        fun onPacketDispatched(packet: EncodedChatPacket)
    }

    private val listeners = CopyOnWriteArrayList<PacketDispatchListener>()

    fun registerListener(listener: PacketDispatchListener) {
        listeners.add(listener)
    }

    fun unregisterListener(listener: PacketDispatchListener) {
        listeners.remove(listener)
    }

    /**
     * Dispatches spatial chat message using [ChatPacketEncoder].
     */
    fun dispatchSpatialChat(message: String, channel: Int = 0): EncodedChatPacket {
        val packet = spatialEncoder.encodeSpatialChat(message, channel)
        validateHeader(packet)
        notifyListeners(packet)
        return packet
    }

    /**
     * Dispatches direct 1-on-1 instant message using [IMPacketEncoder].
     */
    fun dispatchDirectIM(message: String, sessionUuid: UUID, channel: Int = 0): EncodedChatPacket {
        val packet = imEncoder.encodeDirectIM(message, sessionUuid, channel)
        validateHeader(packet)
        notifyListeners(packet)
        return packet
    }

    /**
     * Dispatches group chat message using [GroupMessageEncoder].
     */
    fun dispatchGroupMessage(message: String, groupUuid: UUID, channel: Int = 0): EncodedChatPacket {
        val packet = groupEncoder.encodeGroupMessage(message, groupUuid, channel)
        validateHeader(packet)
        notifyListeners(packet)
        return packet
    }

    /**
     * Validates that an encoded packet strictly conforms to standard Second Life 32-byte AgentData header specifications.
     */
    private fun validateHeader(packet: EncodedChatPacket) {
        require(packet.payload.size >= AbstractChatPacketEncoder.HEADER_SIZE) {
            "Packet payload size ${packet.payload.size} is less than header size ${AbstractChatPacketEncoder.HEADER_SIZE}"
        }
        val buffer = ByteBuffer.wrap(packet.payload).order(ByteOrder.BIG_ENDIAN)
        val agentIdMsb = buffer.long
        val agentIdLsb = buffer.long
        val sessionIdMsb = buffer.long
        val sessionIdLsb = buffer.long
        val agentId = UUID(agentIdMsb, agentIdLsb)
        val sessionId = UUID(sessionIdMsb, sessionIdLsb)
        require(agentId != AgentIdentity.ZERO_UUID) {
            "Header validation failed: AgentID in 32-byte AgentData header is zero UUID"
        }
        require(sessionId != AgentIdentity.ZERO_UUID) {
            "Header validation failed: SessionID in 32-byte AgentData header is zero UUID"
        }
    }

    private fun notifyListeners(packet: EncodedChatPacket) {
        for (listener in listeners) {
            listener.onPacketDispatched(packet)
        }
    }
}
