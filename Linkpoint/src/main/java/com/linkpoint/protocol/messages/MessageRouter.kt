package com.linkpoint.protocol.messages

import com.linkpoint.network.NetworkLogger
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

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
 * Mobile-First Considerations:
 * - Efficient handler lookup
 * - Thread-safe operations
 * - Minimal memory overhead
 * - Fast routing decisions
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
     * Registered handlers: message ID -> list of handlers
     */
    private val handlers = mutableMapOf<Int, MutableList<Handler>>()
    
    /**
     * Mutex for thread-safe operations
     */
    private val mutex = Mutex()
    
    /**
     * Statistics
     */
    private var totalMessagesRouted = 0
    private var successfulRoutes = 0
    private var failedRoutes = 0
    
    /**
     * Register a handler for a specific message ID
     * 
     * @param messageId The message ID to handle
     * @param handler The handler to register
     */
    suspend fun registerHandler(messageId: Int, handler: Handler) {
        mutex.withLock {
            addHandlerInternal(messageId, handler)
        }
    }

    /**
     * Register a handler synchronously without requiring a coroutine context.
     * Use this during initialization to avoid runBlocking deadlocks on the main thread.
     */
    @Synchronized
    fun registerHandlerSync(messageId: Int, handler: Handler) {
        addHandlerInternal(messageId, handler)
    }

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
     * Unregister a handler for a specific message ID
     * 
     * @param messageId The message ID
     * @param handler The handler to unregister
     */
    suspend fun unregisterHandler(messageId: Int, handler: Handler) {
        mutex.withLock {
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
     * Route a message to its handlers
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
        totalMessagesRouted++
        
        val handlerList = mutex.withLock {
            handlers[messageId]?.toList() // Copy to avoid holding lock during handling
        }
        
        if (handlerList.isNullOrEmpty()) {
            NetworkLogger.log(NetworkLogger.Level.WARN, NetworkLogger.Category.UDP, "No handler registered for message $messageId")
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
                    NetworkLogger.log(
                        NetworkLogger.Level.DEBUG,
                        NetworkLogger.Category.UDP,
                        "Message $messageId handled successfully"
                    )
                }
            } catch (e: Exception) {
                NetworkLogger.log(NetworkLogger.Level.ERROR, NetworkLogger.Category.UDP, "Handler error for message $messageId: ${e.message}")
            }
        }
        
        if (handled) {
            successfulRoutes++
        } else {
            failedRoutes++
            NetworkLogger.log(NetworkLogger.Level.WARN, NetworkLogger.Category.UDP, "Message $messageId not handled by any handler")
        }
        
        return handled
    }
    
    /**
     * Get the number of registered handlers
     */
    suspend fun getHandlerCount(): Int = mutex.withLock {
        handlers.values.sumOf { it.size }
    }
    
    /**
     * Get the number of messages with handlers
     */
    suspend fun getMessageCount(): Int = mutex.withLock {
        handlers.size
    }
    
    /**
     * Route a message synchronously without requiring coroutine context.
     * Called from the dedicated I/O thread where we can't use suspend functions.
     * Uses @Synchronized to match registerHandlerSync's thread safety model.
     */
    @Synchronized
    fun routeMessageSync(messageId: Int, data: ByteArray): Boolean {
        totalMessagesRouted++

        val handlerList = handlers[messageId]?.toList()
        if (handlerList.isNullOrEmpty()) {
            return false
        }

        var handled = false
        for (handler in handlerList) {
            try {
                if (handler.handleMessage(messageId, data)) {
                    handled = true
                }
            } catch (e: Exception) {
                NetworkLogger.log(NetworkLogger.Level.ERROR, NetworkLogger.Category.UDP,
                    "Handler error for message $messageId: ${e.message}")
            }
        }

        if (handled) successfulRoutes++ else failedRoutes++
        return handled
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
        mutex.withLock {
            handlers.clear()
            totalMessagesRouted = 0
            successfulRoutes = 0
            failedRoutes = 0
            NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP, "All handlers cleared")
        }
    }
}
