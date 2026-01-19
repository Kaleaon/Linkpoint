package com.linkpoint.protocol.messages

/**
 * Functional interface for handling Second Life protocol messages.
 * 
 * Used by UDPConnectionFixed to register message handlers that process
 * incoming packets. This interface enables SAM (Single Abstract Method)
 * conversion for lambda expressions.
 * 
 * Usage example:
 * ```kotlin
 * messageHandlers[messageId] = MessageHandler { msgId, data ->
 *     // Process message data
 * }
 * ```
 */
fun interface MessageHandler {
    /**
     * Handle an incoming message.
     * 
     * @param messageId The message ID identifying the message type
     * @param data The raw message payload data
     */
    fun handle(messageId: Int, data: ByteArray)
}
