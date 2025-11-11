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
        const val CONFIRM_ENABLE_SIMULATOR = 0xFF08

        // Agent messages
        const val AGENT_UPDATE = 0x04
        const val AGENT_ANIMATION = 0x14
        const val NEIGHBOR_LIST = 0x03
        const val AGENT_REQUEST_SIT = 0x1E
        const val AGENT_SIT = 0x1F
        const val CHILD_AGENT_UPDATE = 0x19
        const val CHILD_AGENT_ALIVE = 0x1A
        const val CHILD_AGENT_POSITION_UPDATE = 0x1B
        const val CHILD_AGENT_DYING = 0xFFFF00F0.toInt()
        const val CHILD_AGENT_UNKNOWN = 0xFFFF00F1.toInt()
        const val KILL_CHILD_AGENTS = 0xFFFF00F2.toInt()
        const val AGENT_THROTTLE = 0x51
        const val AGENT_HEIGHT_WIDTH = 0x53
        const val AGENT_FOV = 0x52
        const val AGENT_PAUSE = 0x4E
        const val AGENT_RESUME = 0x4F
        const val AGENT_SET_APPEARANCE = 0x54
        const val AGENT_QUIT_COPY = 0x55
        const val AGENT_WEARABLES_REQUEST = 0x7D
        const val AGENT_WEARABLES_UPDATE = 0x7E
        const val AGENT_IS_NOW_WEARING = 0x7F
        const val AGENT_CACHED_TEXTURE = 0x80
        const val AGENT_CACHED_TEXTURE_RESPONSE = 0x81
        const val AGENT_DATA_UPDATE = 0x83
        const val AGENT_DATA_UPDATE_REQUEST = 0x82
        const val AGENT_DROP_GROUP = 0x86
        const val AGENT_ALERT = 0x87
        const val AGENT_GROUP_DATA_UPDATE = 0x85
        const val AGENT_MOVEMENT_COMPLETE = 0xFA

        // Region and location updates
        const val COARSE_LOCATION_UPDATE = 0xFF06
        const val CROSSED_REGION = 0xFF07
        const val SIMULATOR_VIEWER_TIME = -65386
        const val ENABLE_SIMULATOR = -65385
        const val DISABLE_SIMULATOR = -65384

        // Terrain and asset streaming
        const val IMAGE_DATA = 0x09
        const val IMAGE_PACKET = 0x0A
        const val LAYER_DATA = 0x0B
        const val REQUEST_IMAGE = 0x08

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
        registerMessage(MessageIDs.CONFIRM_ENABLE_SIMULATOR) { ConfirmEnableSimulatorMessage() }
        registerMessage(MessageIDs.NEIGHBOR_LIST) { NeighborListMessage() }

        // Agent messages
        registerMessage(MessageIDs.AGENT_UPDATE) { AgentUpdateMessage() }
        registerMessage(MessageIDs.AGENT_ANIMATION) { AgentAnimationMessage() }
        registerMessage(MessageIDs.AGENT_REQUEST_SIT) { AgentRequestSitMessage() }
        registerMessage(MessageIDs.AGENT_SIT) { AgentSitMessage() }
        registerMessage(MessageIDs.CHILD_AGENT_UPDATE) { ChildAgentUpdateMessage() }
        registerMessage(MessageIDs.CHILD_AGENT_ALIVE) { ChildAgentAliveMessage() }
        registerMessage(MessageIDs.CHILD_AGENT_POSITION_UPDATE) { ChildAgentPositionUpdateMessage() }
        registerMessage(MessageIDs.CHILD_AGENT_DYING) { ChildAgentDyingMessage() }
        registerMessage(MessageIDs.CHILD_AGENT_UNKNOWN) { ChildAgentUnknownMessage() }
        registerMessage(MessageIDs.KILL_CHILD_AGENTS) { KillChildAgentsMessage() }
        registerMessage(MessageIDs.AGENT_THROTTLE) { AgentThrottleMessage() }
        registerMessage(MessageIDs.AGENT_FOV) { AgentFovMessage() }
        registerMessage(MessageIDs.AGENT_PAUSE) { AgentPauseMessage() }
        registerMessage(MessageIDs.AGENT_RESUME) { AgentResumeMessage() }
        registerMessage(MessageIDs.AGENT_HEIGHT_WIDTH) { AgentHeightWidthMessage() }
        registerMessage(MessageIDs.AGENT_SET_APPEARANCE) { AgentSetAppearanceMessage() }
        registerMessage(MessageIDs.AGENT_QUIT_COPY) { AgentQuitCopyMessage() }
        registerMessage(MessageIDs.AGENT_WEARABLES_REQUEST) { AgentWearablesRequestMessage() }
        registerMessage(MessageIDs.AGENT_WEARABLES_UPDATE) { AgentWearablesUpdateMessage() }
        registerMessage(MessageIDs.AGENT_IS_NOW_WEARING) { AgentIsNowWearingMessage() }
        registerMessage(MessageIDs.AGENT_CACHED_TEXTURE) { AgentCachedTextureMessage() }
        registerMessage(MessageIDs.AGENT_CACHED_TEXTURE_RESPONSE) { AgentCachedTextureResponseMessage() }
        registerMessage(MessageIDs.AGENT_DATA_UPDATE) { AgentDataUpdateMessage() }
        registerMessage(MessageIDs.AGENT_DATA_UPDATE_REQUEST) { AgentDataUpdateRequestMessage() }
        registerMessage(MessageIDs.AGENT_GROUP_DATA_UPDATE) { AgentGroupDataUpdateMessage() }
        registerMessage(MessageIDs.AGENT_DROP_GROUP) { AgentDropGroupMessage() }
        registerMessage(MessageIDs.AGENT_ALERT) { AgentAlertMessage() }
        registerMessage(MessageIDs.AGENT_MOVEMENT_COMPLETE) { AgentMovementCompleteMessage() }

        // Location updates
        registerMessage(MessageIDs.COARSE_LOCATION_UPDATE) { CoarseLocationUpdateMessage() }
        registerMessage(MessageIDs.CROSSED_REGION) { CrossedRegionMessage() }
        registerMessage(MessageIDs.SIMULATOR_VIEWER_TIME) { SimulatorViewerTimeMessage() }
        registerMessage(MessageIDs.ENABLE_SIMULATOR) { EnableSimulatorMessage() }
        registerMessage(MessageIDs.DISABLE_SIMULATOR) { DisableSimulatorMessage() }

        // Asset streaming
        registerMessage(MessageIDs.REQUEST_IMAGE) { RequestImageMessage() }
        registerMessage(MessageIDs.IMAGE_DATA) { ImageDataMessage() }
        registerMessage(MessageIDs.IMAGE_PACKET) { ImagePacketMessage() }
        registerMessage(MessageIDs.LAYER_DATA) { LayerDataMessage() }

        // Chat messages
        registerMessage(MessageIDs.CHAT_FROM_SIMULATOR) { ChatFromSimulatorMessage() }
        registerMessage(MessageIDs.CHAT_FROM_VIEWER) { ChatFromViewerMessage() }

        // Object messages
        registerMessage(MessageIDs.OBJECT_UPDATE) { ObjectUpdateMessage() }
        registerMessage(MessageIDs.OBJECT_UPDATE_COMPRESSED) { ObjectUpdateCompressedMessage() }
        registerMessage(MessageIDs.OBJECT_UPDATE_CACHED) { ObjectUpdateCachedMessage() }
        registerMessage(MessageIDs.KILL_OBJECT) { KillObjectMessage() }

        // Region messages
        registerMessage(MessageIDs.REGION_HANDSHAKE) { RegionHandshakeMessage() }
        registerMessage(MessageIDs.REGION_HANDSHAKE_REPLY) { RegionHandshakeReplyMessage() }

        // Teleport messages
        registerMessage(MessageIDs.TELEPORT_REQUEST) { TeleportRequestMessage() }
        registerMessage(MessageIDs.TELEPORT_START) { TeleportStartMessage() }
        registerMessage(MessageIDs.TELEPORT_PROGRESS) { TeleportProgressMessage() }
        registerMessage(MessageIDs.TELEPORT_FINISH) { TeleportFinishMessage() }
        registerMessage(MessageIDs.TELEPORT_FAILED) { TeleportFailedMessage() }

        // IM messages
        registerMessage(MessageIDs.IMPROVED_IM) { ImprovedIMMessage() }

        // Inventory messages
        registerMessage(MessageIDs.FETCH_INVENTORY) { FetchInventoryMessage() }
        registerMessage(MessageIDs.FETCH_INVENTORY_REPLY) { FetchInventoryReplyMessage() }

        // Avatar messages
        registerMessage(MessageIDs.AVATAR_APPEARANCE) { AvatarAppearanceMessage() }
        registerMessage(MessageIDs.AVATAR_PROPERTIES_REQUEST) { AvatarPropertiesRequestMessage() }
        registerMessage(MessageIDs.AVATAR_PROPERTIES_REPLY) { AvatarPropertiesReplyMessage() }

        // Asset transfer messages
        registerMessage(MessageIDs.TRANSFER_REQUEST) { TransferRequestMessage() }
        registerMessage(MessageIDs.TRANSFER_INFO) { TransferInfoMessage() }
        registerMessage(MessageIDs.TRANSFER_PACKET) { TransferPacketMessage() }
        registerMessage(MessageIDs.TRANSFER_ABORT) { TransferAbortMessage() }
    }
}
