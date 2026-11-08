package com.linkpoint.chat.encoder

import java.util.UUID
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Dispatcher service for chat channel network communications.
 *
 * Coordinates packet encoding via [ChatPacketEncoder], [IMPacketEncoder],
 * and [GroupMessageEncoder], verifying standard 12-byte headers, sequence state,
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
     * Validates that an encoded packet strictly conforms to 12-byte header specifications.
     */
    private fun validateHeader(packet: EncodedChatPacket) {
        require(packet.payload.size >= AbstractChatPacketEncoder.HEADER_SIZE) {
            "Packet payload size ${packet.payload.size} is less than header size ${AbstractChatPacketEncoder.HEADER_SIZE}"
        }
        val headerFlags = packet.payload[0]
        val typeId = packet.payload[5]
        require(headerFlags == packet.headerFlags) {
            "Header flags mismatch: byte 0 (${headerFlags}) != expected (${packet.headerFlags})"
        }
        require(typeId == packet.channelTypeId) {
            "Channel type ID mismatch: byte 5 (${typeId}) != expected (${packet.channelTypeId})"
        }
    }

    private fun notifyListeners(packet: EncodedChatPacket) {
        for (listener in listeners) {
            listener.onPacketDispatched(packet)
        }
    }
}
