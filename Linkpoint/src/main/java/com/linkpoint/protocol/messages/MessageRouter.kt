package com.linkpoint.protocol.messages

import com.linkpoint.network.NetworkLogger

/**
 * Message Router
 *
 * Routes incoming Second Life protocol messages to appropriate handlers.
 * Based on the reference viewer's SLMessageRouter implementation.
 *
 * Features:
 * - Message ID to handler mapping
 * - Priority-based handler selection
 * - Handler registration and removal
 * - Message statistics tracking
 *
 * Thread Safety:
 * Uses synchronized blocks consistently for ALL operations on the handlers map.
 * Previous implementation mixed coroutine Mutex and @Synchronized which
 * are independent lock mechanisms - they don't interlock, creating a data race.
 * Following Lumiya's pattern of using a single synchronization mechanism.
 */
class MessageRouter {

    companion object {
        private const val TAG = "MessageRouter"
        private const val MAX_HANDLERS_PER_MESSAGE = 10
    }

    /**
     * Message handler interface
     */
    interface Handler {
        /**
         * Handle an incoming message
         *
         * @param messageId The message ID
         * @param data The message data
         * @return true if handled successfully, false otherwise
         */
        fun handleMessage(messageId: Int, data: ByteArray): Boolean

        /**
         * Get the priority of this handler (lower = higher priority)
         */
        fun getPriority(): Int = 0
    }


    interface ParsedHandler {
        fun handleParsedMessage(messageId: Int, payload: ByteArray, parsed: Any?): Boolean
        fun getPriority(): Int = 0
    }

    /**
     * Registered handlers: message ID -> list of handlers.
     * All access synchronized via @Synchronized on this MessageRouter instance.
     */
    private val handlers = mutableMapOf<Int, MutableList<Handler>>()

    /**
     * Statistics
     */
    @Volatile private var totalMessagesRouted = 0
    @Volatile private var successfulRoutes = 0
    @Volatile private var failedRoutes = 0

    /**
     * Internal handler addition - must be called under @Synchronized
     */
    private fun addHandlerInternal(messageId: Int, handler: Handler) {
        val handlerList = handlers.getOrPut(messageId) { mutableListOf() }

        if (handlerList.size >= MAX_HANDLERS_PER_MESSAGE) {
            NetworkLogger.log(NetworkLogger.Level.WARN, NetworkLogger.Category.UDP, "Max handlers reached for message $messageId")
            return
        }

        // Add handler and sort by priority
        handlerList.add(handler)
        handlerList.sortBy { it.getPriority() }

        NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP, "Registered handler for message $messageId (priority ${handler.getPriority()})")
    }

    /**
     * Register a handler for a specific message ID. Safe to call from any
     * thread; addHandlerInternal is guarded by @Synchronized.
     */
    @Synchronized
    fun registerHandler(messageId: Int, handler: Handler) {
        addHandlerInternal(messageId, handler)
    }


    @Synchronized
    fun registerParsedHandlerSync(messageId: Int, handler: ParsedHandler) {
        addHandlerInternal(messageId, object : Handler {
            override fun handleMessage(messageId: Int, data: ByteArray): Boolean {
                val payload = MessageParser.extractPayload(data) ?: return false
                val parsed = MessageParser.parseByMessageId(messageId, payload)
                return handler.handleParsedMessage(messageId, payload, parsed)
            }

            override fun getPriority(): Int = handler.getPriority()
        })
    }

    /**
     * Unregister a handler for a specific message ID.
     */
    @Synchronized
    fun unregisterHandler(messageId: Int, handler: Handler) {
        val handlerList = handlers[messageId]
        if (handlerList != null) {
            handlerList.remove(handler)
            if (handlerList.isEmpty()) {
                handlers.remove(messageId)
            }
            NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP, "Unregistered handler for message $messageId")
        }
    }

    /**
     * Route a message to its handlers. Called from the I/O thread; handlers
     * run synchronously on the caller's thread (Lumiya's pattern).
     */
    @Synchronized
    fun routeMessageSync(messageId: Int, data: ByteArray): Boolean {
        totalMessagesRouted++

        val handlerList = handlers[messageId]?.toList() // Copy under lock
        if (handlerList.isNullOrEmpty()) {
            failedRoutes++
            return false
        }

        var handled = false
        for (handler in handlerList) {
            try {
                if (handler.handleMessage(messageId, data)) {
                    handled = true
                }
            } catch (e: Exception) {
                NetworkLogger.log(NetworkLogger.Level.ERROR, NetworkLogger.Category.UDP, "Handler error for message $messageId: ${e.message}")
            }
        }

        if (handled) successfulRoutes++ else failedRoutes++
        return handled
    }

    /**
     * Get the number of registered handlers
     */
    @Synchronized
    fun getHandlerCount(): Int = handlers.values.sumOf { it.size }

    /**
     * Get the number of messages with handlers
     */
    @Synchronized
    fun getMessageCount(): Int = handlers.size

    /**
     * Get router statistics
     */
    fun getStatistics(): Map<String, Any> {
        return mapOf(
            "totalMessagesRouted" to totalMessagesRouted,
            "successfulRoutes" to successfulRoutes,
            "failedRoutes" to failedRoutes,
            "successRate" to if (totalMessagesRouted > 0) {
                (successfulRoutes.toFloat() / totalMessagesRouted * 100)
            } else {
                0f
            }
        )
    }

    /**
     * Clear all handlers
     */
    @Synchronized
    fun clearAll() {
        handlers.clear()
        totalMessagesRouted = 0
        successfulRoutes = 0
        failedRoutes = 0
        NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP, "All handlers cleared")
    }
}
