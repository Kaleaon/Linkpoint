package com.linkpoint.protocol.messages

/**
 * Parser registry mapping message IDs to parser handlers.
 * This replaces large conditional parser blocks with composable per-domain handlers.
 */
object MessageParserRegistry {
    private val handlers = mutableMapOf<Int, (ByteArray) -> Any?>()

    init {
        register(MessageIds.OBJECT_UPDATE) { ObjectMessageParsers.parseObjectUpdate(it) }
        register(MessageIds.OBJECT_UPDATE_COMPRESSED) { ObjectMessageParsers.parseObjectUpdateCompressed(it) }
        register(MessageIds.OBJECT_UPDATE_CACHED) { ObjectMessageParsers.parseObjectUpdateCached(it) }
        register(MessageIds.IMPROVED_TERSE_OBJECT_UPDATE) { ObjectMessageParsers.parseTerseObjectUpdate(it) }
        register(MessageIds.OBJECT_PROPERTIES) { MessageParser.parseObjectProperties(it) }

        register(MessageIds.AVATAR_ANIMATION) { AvatarMessageParsers.parseAvatarAnimation(it) }
        register(MessageIds.CHAT_FROM_SIMULATOR) { ChatMessageParsers.parseChatFromSimulator(it) }
        register(MessageIds.IMPROVED_INSTANT_MESSAGE) { MessageParser.parseImprovedInstantMessage(it) }

        register(MessageIds.TELEPORT_FINISH) { TeleportMessageParsers.parseTeleportFinish(it) }
        register(MessageIds.TELEPORT_FAILED) { TeleportMessageParsers.parseTeleportFailed(it) }
        register(MessageIds.TELEPORT_PROGRESS) { TeleportMessageParsers.parseTeleportProgress(it) }
        register(MessageIds.ENABLE_SIMULATOR) { TeleportMessageParsers.parseEnableSimulator(it) }
        register(MessageIds.CROSSED_REGION) { TeleportMessageParsers.parseCrossedRegion(it) }

        register(MessageIds.INVENTORY_DESCENDENTS) { AdditionalMessageParsers.parseInventoryDescendents(it) }
    }

    fun register(messageId: Int, handler: (ByteArray) -> Any?) {
        handlers[messageId] = handler
    }

    fun parse(messageId: Int, payload: ByteArray): Any? = handlers[messageId]?.invoke(payload)
    fun hasHandler(messageId: Int): Boolean = handlers.containsKey(messageId)
}
