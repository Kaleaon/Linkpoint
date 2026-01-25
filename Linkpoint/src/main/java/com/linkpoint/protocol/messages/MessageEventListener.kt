package com.linkpoint.protocol.messages

/**
 * Event listener for message acknowledgment and timeout events.
 * Based on Lumiya's SLMessageEventListener interface.
 * 
 * This interface allows callers to receive callbacks when reliable messages
 * are acknowledged by the server or when they timeout after max retries.
 * 
 * This is critical for implementing the circuit establishment state machine,
 * as it allows the connection to progress through states in response to
 * server acknowledgments.
 */
interface MessageEventListener {
    
    /**
     * Called when a reliable message is acknowledged by the server.
     * 
     * @param sequenceNumber The sequence number of the acknowledged message
     * @param messageId The message ID that was acknowledged
     */
    fun onMessageAcknowledged(sequenceNumber: Int, messageId: Int)
    
    /**
     * Called when a reliable message times out (max retries exceeded).
     * 
     * @param sequenceNumber The sequence number of the timed out message
     * @param messageId The message ID that timed out
     */
    fun onMessageTimeout(sequenceNumber: Int, messageId: Int)
}