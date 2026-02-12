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

        /**
         * Whether this handler should be offloaded to a secondary queue.
         */
        fun isHeavy(): Boolean = false
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
     * Register a handler for a specific message ID.
     * This is a suspend function for API compatibility but uses @Synchronized internally.
     *
     * @param messageId The message ID to handle
     * @param handler The handler to register
     */
    suspend fun registerHandler(messageId: Int, handler: Handler) {
        registerHandlerSync(messageId, handler)
    }

    /**
     * Register a handler synchronously without requiring a coroutine context.
     * Safe to call from any thread including the main thread during initialization.
     */
    @Synchronized
    fun registerHandlerSync(messageId: Int, handler: Handler) {
        addHandlerInternal(messageId, handler)
    }

    /**
     * Unregister a handler for a specific message ID
     *
     * @param messageId The message ID
     * @param handler The handler to unregister
     */
    suspend fun unregisterHandler(messageId: Int, handler: Handler) {
        synchronized(this) {
            val handlerList = handlers[messageId]
            if (handlerList != null) {
                handlerList.remove(handler)
                if (handlerList.isEmpty()) {
                    handlers.remove(messageId)
                }
                NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP, "Unregistered handler for message $messageId")
            }
        }
    }

    /**
     * Route a message to its handlers.
     * This is a suspend function for API compatibility but uses @Synchronized internally.
     *
     * @param messageId The message ID
     * @param data The message data
     * @return true if message was handled, false otherwise
     */
    suspend fun routeMessage(
        messageId: Int,
        data: ByteArray,
        heavyQueue: CircuitTaskQueue? = null
    ): Boolean {
        return routeMessageInternal(messageId, data, heavyQueue)
    }

    /**
     * Route a message synchronously without requiring coroutine context.
     * Called from the dedicated I/O thread where we can't use suspend functions.
     */
    fun routeMessageSync(messageId: Int, data: ByteArray): Boolean {
        return routeMessageInternal(messageId, data, null)
    }

    /**
     * Internal routing implementation. Copy handler list under lock,
     * then invoke handlers outside the lock to avoid holding it during processing.
     */
    @Synchronized
    private fun routeMessageInternal(
        messageId: Int,
        data: ByteArray,
        heavyQueue: CircuitTaskQueue?
    ): Boolean {
        totalMessagesRouted++

        val handlerList = handlers[messageId]?.toList() // Copy under lock
        if (handlerList.isNullOrEmpty()) {
            failedRoutes++
            return false
        }

        var handled = false
        for (handler in handlerList) {
            try {
                if (handler.isHeavy() && heavyQueue != null) {
                    heavyQueue.enqueue {
                        try {
                            if (handler.handleMessage(messageId, data)) {
                                NetworkLogger.log(
                                    NetworkLogger.Level.DEBUG,
                                    NetworkLogger.Category.UDP,
                                    "Message $messageId handled successfully (heavy queue)"
                                )
                            }
                        } catch (e: Exception) {
                            NetworkLogger.log(
                                NetworkLogger.Level.ERROR,
                                NetworkLogger.Category.UDP,
                                "Handler error for message $messageId (heavy queue): ${e.message}"
                            )
                        }
                    }
                    handled = true
                } else if (handler.handleMessage(messageId, data)) {
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
    suspend fun getHandlerCount(): Int = synchronized(this) {
        handlers.values.sumOf { it.size }
    }

    /**
     * Get the number of messages with handlers
     */
    suspend fun getMessageCount(): Int = synchronized(this) {
        handlers.size
    }

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
    suspend fun clearAll() {
        synchronized(this) {
            handlers.clear()
            totalMessagesRouted = 0
            successfulRoutes = 0
            failedRoutes = 0
            NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP, "All handlers cleared")
        }
    }
}
