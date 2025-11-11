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
        const val START_PING_CHECK = 0x00000001
        const val COMPLETE_PING_CHECK = 0x00000002
        const val CONFIRM_ENABLE_SIMULATOR = 0x0000FF08

        // Agent messages
        const val AGENT_UPDATE = 0x00000004
        const val AGENT_ANIMATION = 0x00000005
        const val NEIGHBOR_LIST = 0x00000003
        const val AGENT_REQUEST_SIT = 0x00000006
        const val AGENT_SIT = 0x00000007
        const val CHILD_AGENT_UPDATE = 0x00000019
        const val CHILD_AGENT_ALIVE = 0x0000001A
        const val CHILD_AGENT_POSITION_UPDATE = 0x0000001B
        const val CHILD_AGENT_DYING = 0xFFFF00F0.toInt()
        const val CHILD_AGENT_UNKNOWN = 0xFFFF00F1.toInt()
        const val KILL_CHILD_AGENTS = 0xFFFF00F2.toInt()
        const val CAMERA_CONSTRAINT = 0x00000016
        const val PARCEL_PROPERTIES = 0x00000017
        const val DIR_FIND_QUERY = 0xFFFF001F.toInt()
        const val DIR_FIND_QUERY_BACKEND = 0xFFFF0020.toInt()
        const val DIR_PLACES_QUERY = 0xFFFF0021.toInt()
        const val DIR_PLACES_QUERY_BACKEND = 0xFFFF0022.toInt()
        const val DIR_PLACES_REPLY = 0xFFFF0023.toInt()
        const val DIR_PEOPLE_REPLY = 0xFFFF0024.toInt()
        const val DIR_EVENTS_REPLY = 0xFFFF0025.toInt()
        const val DIR_GROUPS_REPLY = 0xFFFF0026.toInt()
        const val DIR_CLASSIFIED_QUERY = 0xFFFF0027.toInt()
        const val DIR_CLASSIFIED_QUERY_BACKEND = 0xFFFF0028.toInt()
        const val DIR_CLASSIFIED_REPLY = 0xFFFF0029.toInt()
        const val DIR_LAND_QUERY = 0xFFFF0030.toInt()
        const val DIR_LAND_QUERY_BACKEND = 0xFFFF0031.toInt()
        const val DIR_LAND_REPLY = 0xFFFF0032.toInt()
        const val DIR_POPULAR_QUERY = 0xFFFF0033.toInt()
        const val DIR_POPULAR_QUERY_BACKEND = 0xFFFF0034.toInt()
        const val DIR_POPULAR_REPLY = 0xFFFF0035.toInt()
        const val AGENT_THROTTLE = 0xFFFF0051.toInt()
        const val AGENT_HEIGHT_WIDTH = 0xFFFF0053.toInt()
        const val AGENT_FOV = 0xFFFF0052.toInt()
        const val AGENT_PAUSE = 0xFFFF004E.toInt()
        const val AGENT_RESUME = 0xFFFF004F.toInt()
        const val AGENT_SET_APPEARANCE = 0xFFFF0054.toInt()
        const val AGENT_QUIT_COPY = 0xFFFF0055.toInt()
        const val AGENT_WEARABLES_REQUEST = 0xFFFF017D.toInt()
        const val AGENT_WEARABLES_UPDATE = 0xFFFF017E.toInt()
        const val AGENT_IS_NOW_WEARING = 0xFFFF017F.toInt()
        const val AGENT_CACHED_TEXTURE = 0xFFFF0180.toInt()
        const val AGENT_CACHED_TEXTURE_RESPONSE = 0xFFFF0181.toInt()
        const val AGENT_DATA_UPDATE = 0xFFFF0183.toInt()
        const val AGENT_DATA_UPDATE_REQUEST = 0xFFFF0182.toInt()
        const val AGENT_DROP_GROUP = 0xFFFF0186.toInt()
        const val AGENT_ALERT = 0xFFFF0087.toInt()
        const val AGENT_GROUP_DATA_UPDATE = 0xFFFF0185.toInt()
        const val AGENT_MOVEMENT_COMPLETE = 0xFFFF00FA.toInt()

        // Region and location updates
        const val COARSE_LOCATION_UPDATE = 0x0000FF06
        const val CROSSED_REGION = 0x0000FF07
        const val SIMULATOR_VIEWER_TIME = 0xFFFF0096.toInt()
        const val ENABLE_SIMULATOR = 0xFFFF0097.toInt()
        const val DISABLE_SIMULATOR = 0xFFFF0098.toInt()

        // Terrain and asset streaming
        const val REQUEST_IMAGE = 0x00000008
        const val IMAGE_DATA = 0x00000009
        const val IMAGE_PACKET = 0x0000000A
        const val LAYER_DATA = 0x0000000B

        // Chat messages
        const val CHAT_FROM_SIMULATOR = 0xFFFF008B.toInt()
        const val CHAT_FROM_VIEWER = 0xFFFF0050.toInt()

        // Object messages
        const val OBJECT_UPDATE = 0x0000000C
        const val OBJECT_UPDATE_COMPRESSED = 0x0000000D
        const val OBJECT_UPDATE_CACHED = 0x0000000E
        const val KILL_OBJECT = 0x00000010

        // Region messages
        const val REGION_HANDSHAKE = 0xFFFF0094.toInt()
        const val REGION_HANDSHAKE_REPLY = 0xFFFF0095.toInt()

        // Teleport messages
        const val TELEPORT_REQUEST = 0xFFFF003E.toInt()
        const val TELEPORT_START = 0xFFFF0049.toInt()
        const val TELEPORT_PROGRESS = 0xFFFF0042.toInt()
        const val TELEPORT_FINISH = 0xFFFF0045.toInt()
        const val TELEPORT_FAILED = 0xFFFF004A.toInt()

        // Inventory messages
        const val FETCH_INVENTORY = 0xFFFF0117.toInt()
        const val FETCH_INVENTORY_REPLY = 0xFFFF0118.toInt()

        // Avatar messages
        const val AVATAR_APPEARANCE = 0xFFFF009E.toInt()
        const val AVATAR_PROPERTIES_REQUEST = 0xFFFF00A9.toInt()
        const val AVATAR_PROPERTIES_REPLY = 0xFFFF00AB.toInt()

        // IM messages
        const val IMPROVED_IM = 0xFFFF00FE.toInt()

        // Asset messages
        const val TRANSFER_REQUEST = 0xFFFF0099.toInt()
        const val TRANSFER_INFO = 0xFFFF009A.toInt()
        const val TRANSFER_PACKET = 0x00000011
        const val TRANSFER_ABORT = 0xFFFF009B.toInt()
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
        registerMessage(MessageIDs.DIR_FIND_QUERY) { DirFindQueryMessage() }
        registerMessage(MessageIDs.DIR_FIND_QUERY_BACKEND) { DirFindQueryBackendMessage() }
        registerMessage(MessageIDs.DIR_PLACES_QUERY) { DirPlacesQueryMessage() }
        registerMessage(MessageIDs.DIR_PLACES_QUERY_BACKEND) { DirPlacesQueryBackendMessage() }
        registerMessage(MessageIDs.DIR_PLACES_REPLY) { DirPlacesReplyMessage() }
        registerMessage(MessageIDs.DIR_PEOPLE_REPLY) { DirPeopleReplyMessage() }
        registerMessage(MessageIDs.DIR_EVENTS_REPLY) { DirEventsReplyMessage() }
        registerMessage(MessageIDs.DIR_GROUPS_REPLY) { DirGroupsReplyMessage() }
        registerMessage(MessageIDs.DIR_CLASSIFIED_QUERY) { DirClassifiedQueryMessage() }
        registerMessage(MessageIDs.DIR_CLASSIFIED_QUERY_BACKEND) { DirClassifiedQueryBackendMessage() }
        registerMessage(MessageIDs.DIR_CLASSIFIED_REPLY) { DirClassifiedReplyMessage() }
        registerMessage(MessageIDs.DIR_LAND_QUERY) { DirLandQueryMessage() }
        registerMessage(MessageIDs.DIR_LAND_QUERY_BACKEND) { DirLandQueryBackendMessage() }
        registerMessage(MessageIDs.DIR_LAND_REPLY) { DirLandReplyMessage() }
        registerMessage(MessageIDs.DIR_POPULAR_QUERY) { DirPopularQueryMessage() }
        registerMessage(MessageIDs.DIR_POPULAR_QUERY_BACKEND) { DirPopularQueryBackendMessage() }
        registerMessage(MessageIDs.DIR_POPULAR_REPLY) { DirPopularReplyMessage() }
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
        registerMessage(MessageIDs.CAMERA_CONSTRAINT) { CameraConstraintMessage() }
        registerMessage(MessageIDs.PARCEL_PROPERTIES) { ParcelPropertiesMessage() }

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
