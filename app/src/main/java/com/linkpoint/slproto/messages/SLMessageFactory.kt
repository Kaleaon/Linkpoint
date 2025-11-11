package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage

/**
 * Factory for creating SL protocol messages by ID
 */
object SLMessageFactory {
    // Message ID constants
    object MessageIDs {
        // System messages
        const val PACKET_ACK = 0xFFFFFFFB.toInt()
        const val OPEN_CIRCUIT = 0xFFFFFFFC.toInt()
        const val CLOSE_CIRCUIT = 0xFFFFFFFD.toInt()
        const val USE_CIRCUIT_CODE = 0xFFFF0003.toInt()
        const val COMPLETE_AGENT_MOVEMENT = 0xFFFF00F9.toInt()
        const val START_PING_CHECK = 0x01
        const val COMPLETE_PING_CHECK = 0x02

        // Agent messages
        const val AGENT_UPDATE = 0x04
        const val AGENT_ANIMATION = 0x14
        const val AGENT_REQUEST_SIT = 0x1E
        const val AGENT_SIT = 0x1F

        // Chat messages
        const val CHAT_FROM_SIMULATOR = 0x8B
        const val CHAT_FROM_VIEWER = 0x50

        // Object messages
        const val OBJECT_UPDATE = 0x0C
        const val OBJECT_UPDATE_COMPRESSED = 0x0D
        const val OBJECT_UPDATE_CACHED = 0x0E
        const val KILL_OBJECT = 0x0F

        // Region messages
        const val REGION_HANDSHAKE = 0x94
        const val REGION_HANDSHAKE_REPLY = 0x95

        // Teleport messages
        const val TELEPORT_REQUEST = 0x56
        const val TELEPORT_START = 0x57
        const val TELEPORT_PROGRESS = 0x58
        const val TELEPORT_FINISH = 0x59
        const val TELEPORT_FAILED = 0x5A

        // Inventory messages
        const val FETCH_INVENTORY = 0x66
        const val FETCH_INVENTORY_REPLY = 0x67

        // Avatar messages
        const val AVATAR_APPEARANCE = 0x9E
        const val AVATAR_PROPERTIES_REQUEST = 0xA0
        const val AVATAR_PROPERTIES_REPLY = 0xA1

        // IM messages
        const val IMPROVED_IM = 0xFE

        // Asset messages
        const val TRANSFER_REQUEST = 0x68
        const val TRANSFER_INFO = 0x69
        const val TRANSFER_PACKET = 0x6A
        const val TRANSFER_ABORT = 0x6B
    }

    private val messageRegistry = mutableMapOf<Int, () -> SLMessage>()

    init {
        // Register message types
        registerMessages()
    }

    /**
     * Create a message instance by ID
     */
    fun createByID(messageId: Int): SLMessage? {
        return messageRegistry[messageId]?.invoke()
    }

    /**
     * Register a message type
     */
    fun registerMessage(
        messageId: Int,
        factory: () -> SLMessage,
    ) {
        messageRegistry[messageId] = factory
    }

    /**
     * Register all known message types
     */
    private fun registerMessages() {
        // System messages
        registerMessage(MessageIDs.PACKET_ACK) { PacketAckMessage() }
        registerMessage(MessageIDs.OPEN_CIRCUIT) { OpenCircuitMessage() }
        registerMessage(MessageIDs.CLOSE_CIRCUIT) { CloseCircuitMessage() }
        registerMessage(MessageIDs.USE_CIRCUIT_CODE) { UseCircuitCodeMessage() }
        registerMessage(MessageIDs.COMPLETE_AGENT_MOVEMENT) { CompleteAgentMovementMessage() }
        registerMessage(MessageIDs.START_PING_CHECK) { StartPingCheckMessage() }
        registerMessage(MessageIDs.COMPLETE_PING_CHECK) { CompletePingCheckMessage() }

        // Agent messages
        registerMessage(MessageIDs.AGENT_UPDATE) { AgentUpdateMessage() }
        registerMessage(MessageIDs.AGENT_ANIMATION) { AgentAnimationMessage() }

        // Chat messages
        registerMessage(MessageIDs.CHAT_FROM_SIMULATOR) { ChatFromSimulatorMessage() }
        registerMessage(MessageIDs.CHAT_FROM_VIEWER) { ChatFromViewerMessage() }

        // Object messages
        registerMessage(MessageIDs.OBJECT_UPDATE) { ObjectUpdateMessage() }
        registerMessage(MessageIDs.OBJECT_UPDATE_COMPRESSED) { ObjectUpdateCompressedMessage() }
        registerMessage(MessageIDs.KILL_OBJECT) { KillObjectMessage() }

        // Region messages
        registerMessage(MessageIDs.REGION_HANDSHAKE) { RegionHandshakeMessage() }
        registerMessage(MessageIDs.REGION_HANDSHAKE_REPLY) { RegionHandshakeReplyMessage() }

        // Teleport messages
        registerMessage(MessageIDs.TELEPORT_REQUEST) { TeleportRequestMessage() }
        registerMessage(MessageIDs.TELEPORT_START) { TeleportStartMessage() }
        registerMessage(MessageIDs.TELEPORT_FINISH) { TeleportFinishMessage() }

        // IM messages
        registerMessage(MessageIDs.IMPROVED_IM) { ImprovedIMMessage() }
    }
}
