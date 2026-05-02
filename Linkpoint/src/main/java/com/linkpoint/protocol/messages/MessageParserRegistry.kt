package com.linkpoint.protocol.messages

/**
 * Parser registry mapping message IDs to parser handlers.
 * This replaces large conditional parser blocks with composable per-domain handlers.
 */
object MessageParserRegistry {
    private val handlers = mutableMapOf<Int, (ByteArray) -> Any?>()

    init {
        register(MessageIdRegistry.OBJECT_UPDATE) { ObjectMessageParsers.parseObjectUpdate(it) }
        register(MessageIdRegistry.OBJECT_UPDATE_COMPRESSED) { ObjectMessageParsers.parseObjectUpdateCompressed(it) }
        register(MessageIdRegistry.OBJECT_UPDATE_CACHED) { ObjectMessageParsers.parseObjectUpdateCached(it) }
        register(MessageIdRegistry.IMPROVED_TERSE_OBJECT_UPDATE) { ObjectMessageParsers.parseTerseObjectUpdate(it) }
        register(MessageIdRegistry.OBJECT_PROPERTIES) { MessageParser.parseObjectProperties(it) }

        register(MessageIdRegistry.AVATAR_ANIMATION) { AvatarMessageParsers.parseAvatarAnimation(it) }
        register(MessageIdRegistry.CHAT_FROM_SIMULATOR) { ChatMessageParsers.parseChatFromSimulator(it) }
        register(MessageIdRegistry.IMPROVED_INSTANT_MESSAGE) { MessageParser.parseImprovedInstantMessage(it) }

        register(MessageIdRegistry.TELEPORT_FINISH) { TeleportMessageParsers.parseTeleportFinish(it) }
        register(MessageIdRegistry.TELEPORT_FAILED) { TeleportMessageParsers.parseTeleportFailed(it) }
        register(MessageIdRegistry.TELEPORT_PROGRESS) { TeleportMessageParsers.parseTeleportProgress(it) }
        register(MessageIdRegistry.ENABLE_SIMULATOR) { TeleportMessageParsers.parseEnableSimulator(it) }
        register(MessageIdRegistry.CROSSED_REGION) { TeleportMessageParsers.parseCrossedRegion(it) }

        register(MessageIdRegistry.INVENTORY_DESCENDENTS) { AdditionalMessageParsers.parseInventoryDescendents(it) }
        register(MessageIdRegistry.FETCH_INVENTORY_DESCENDENTS) { DeclaredMessageSlices.parseFetchInventoryDescendents(it) }
        register(MessageIdRegistry.FETCH_INVENTORY) { DeclaredMessageSlices.parseFetchInventory(it) }
        register(MessageIdRegistry.REQUEST_PAY_PRICE) { DeclaredMessageSlices.parseRequestPayPrice(it) }
        register(MessageIdRegistry.DIR_FIND_QUERY) { DeclaredMessageSlices.parseDirFindQuery(it) }
        register(MessageIdRegistry.GROUP_TITLES_REQUEST) { DeclaredMessageSlices.parseGroupTitlesRequest(it) }
        register(MessageIdRegistry.MAP_NAME_REQUEST) { DeclaredMessageSlices.parseMapNameRequest(it) }
        register(MessageIdRegistry.AGENT_PAUSE) { DeclaredMessageSlices.parseAgentPause(it) }
        register(MessageIdRegistry.AGENT_RESUME) { DeclaredMessageSlices.parseAgentResume(it) }
    }

    fun register(messageId: Int, handler: (ByteArray) -> Any?) {
        handlers[messageId] = handler
    }

    fun parse(messageId: Int, payload: ByteArray): Any? = handlers[messageId]?.invoke(payload)
    fun hasHandler(messageId: Int): Boolean = handlers.containsKey(messageId)
}
