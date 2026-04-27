package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.Debug
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit
import java.nio.ByteBuffer
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import androidx.annotation.NonNull

/**
 * Converted from Java to Kotlin
 * SLMessageHandler - Handles incoming and outgoing Second Life protocol messages
 */
class SLMessageHandler(private val agentCircuit: SLAgentCircuit) {

    companion object {
        private const val TAG = "SLMessageHandler"

        // Message types
        const val MSG_AGENT_UPDATE = 4
        const val MSG_CHAT_FROM_VIEWER = 80
        const val MSG_OBJECT_SELECT = 13
        const val MSG_OBJECT_DESELECT = 14
        const val MSG_AGENT_ANIMATION = 20
        const val MSG_AGENT_REQUEST_SIT = 21
        const val MSG_AGENT_SIT = 22
        const val MSG_TELEPORT_REQUEST = 23
        const val MSG_IMPROVED_INSTANT_MESSAGE = 254
        const val MSG_REQUEST_OBJECT_PROPERTIES_FAMILY = 347
    }

    interface MessageListener {
        fun onMessageReceived(messageType: Int, data: ByteBuffer)
    }

    private val messageListeners = ConcurrentHashMap<Int, MutableList<MessageListener>>()
    private val messageQueue = ArrayDeque<PendingMessage>()
    private val messageSequence = java.util.concurrent.atomic.AtomicInteger(0)

    data class PendingMessage(
        val messageType: Int,
        val data: ByteBuffer,
        val sequenceNumber: Int,
        val timestamp: Long = System.currentTimeMillis()
    )

    data class MessageHeader(
        val flags: Byte,
        val sequenceNumber: Int,
        val extraLength: Byte = 0
    )

    fun registerListener(messageType: Int, listener: MessageListener) {
        val listeners = messageListeners.getOrPut(messageType) { mutableListOf() }
        listeners.add(listener)
    }

    fun unregisterListener(messageType: Int, listener: MessageListener) {
        messageListeners[messageType]?.remove(listener)
    }

    fun sendMessage(messageType: Int, data: ByteBuffer) {
        val sequenceNumber = messageSequence.incrementAndGet()
        val message = PendingMessage(messageType, data, sequenceNumber)
        
        synchronized(messageQueue) {
            messageQueue.addLast(message)
        }
        
        processOutgoingMessages()
    }

    fun handleIncomingMessage(data: ByteBuffer) {
        try {
            val header = parseHeader(data)
            val messageType = data.getInt()
            
            Debug.d(TAG, "Received message type: $messageType")
            
            val listeners = messageListeners[messageType]
            listeners?.forEach { listener ->
                try {
                    listener.onMessageReceived(messageType, data)
                } catch (e: Exception) {
                    Debug.e(TAG, "Error in message listener", e)
                }
            }
        } catch (e: Exception) {
            Debug.e(TAG, "Error handling incoming message", e)
        }
    }

    private fun parseHeader(data: ByteBuffer): MessageHeader {
        val flags = data.get()
        val sequenceNumber = data.getInt()
        val extraLength = if ((flags.toInt() and 0x80) != 0) data.get() else 0
        return MessageHeader(flags, sequenceNumber, extraLength)
    }

    private fun processOutgoingMessages() {
        synchronized(messageQueue) {
            while (messageQueue.isNotEmpty()) {
                val message = messageQueue.removeFirst()
                sendMessageInternal(message)
            }
        }
    }

    private fun sendMessageInternal(message: PendingMessage) {
        try {
            val buffer = ByteBuffer.allocate(message.data.remaining() + 12)
            
            // Write header
            buffer.put(0x00.toByte()) // flags
            buffer.putInt(message.sequenceNumber)
            buffer.putInt(message.messageType)
            
            // Write message data
            buffer.put(message.data)
            
            buffer.flip()
            agentCircuit.sendPacket(buffer.array())
            
            Debug.d(TAG, "Sent message type: ${message.messageType}, seq: ${message.sequenceNumber}")
        } catch (e: Exception) {
            Debug.e(TAG, "Error sending message", e)
        }
    }

    fun sendAgentUpdate(bodyRotation: FloatArray, headRotation: FloatArray, cameraPosition: FloatArray, 
                       cameraAtAxis: FloatArray, cameraLeftAxis: FloatArray, cameraUpAxis: FloatArray,
                       controlFlags: Int, flags: Byte) {
        val buffer = ByteBuffer.allocate(128)
        
        // Agent ID and Session ID
        putUUID(buffer, agentCircuit.agentId)
        putUUID(buffer, agentCircuit.sessionId)
        
        // Body rotation (quaternion)
        buffer.putFloat(bodyRotation[0])
        buffer.putFloat(bodyRotation[1])
        buffer.putFloat(bodyRotation[2])
        buffer.putFloat(bodyRotation[3])
        
        // Head rotation (quaternion)
        buffer.putFloat(headRotation[0])
        buffer.putFloat(headRotation[1])
        buffer.putFloat(headRotation[2])
        buffer.putFloat(headRotation[3])
        
        // Camera position
        buffer.putFloat(cameraPosition[0])
        buffer.putFloat(cameraPosition[1])
        buffer.putFloat(cameraPosition[2])
        
        // Camera axes
        buffer.putFloat(cameraAtAxis[0])
        buffer.putFloat(cameraAtAxis[1])
        buffer.putFloat(cameraAtAxis[2])
        
        buffer.putFloat(cameraLeftAxis[0])
        buffer.putFloat(cameraLeftAxis[1])
        buffer.putFloat(cameraLeftAxis[2])
        
        buffer.putFloat(cameraUpAxis[0])
        buffer.putFloat(cameraUpAxis[1])
        buffer.putFloat(cameraUpAxis[2])
        
        // Control flags and state
        buffer.putInt(controlFlags)
        buffer.put(flags)
        
        buffer.flip()
        sendMessage(MSG_AGENT_UPDATE, buffer)
    }

    fun sendChatMessage(message: String, chatType: Int, channel: Int) {
        val messageBytes = message.toByteArray(Charsets.UTF_8)
        val buffer = ByteBuffer.allocate(256 + messageBytes.size)
        
        putUUID(buffer, agentCircuit.agentId)
        putUUID(buffer, agentCircuit.sessionId)
        buffer.put(message.toByte())
        buffer.putInt(chatType)
        buffer.putInt(channel)
        buffer.putShort(messageBytes.size.toShort())
        buffer.put(messageBytes)
        
        buffer.flip()
        sendMessage(MSG_CHAT_FROM_VIEWER, buffer)
    }

    fun sendImprovedInstantMessage(toAgentId: UUID, fromAgentName: String, message: String,
                                   dialog: Byte, imSessionId: UUID, offline: Byte) {
        val messageBytes = message.toByteArray(Charsets.UTF_8)
        val nameBytes = fromAgentName.toByteArray(Charsets.UTF_8)
        val buffer = ByteBuffer.allocate(512)
        
        putUUID(buffer, agentCircuit.agentId)
        putUUID(buffer, agentCircuit.sessionId)
        putUUID(buffer, toAgentId)
        putUUID(buffer, agentCircuit.agentId)  // from agent
        buffer.put(dialog)
        putUUID(buffer, imSessionId)
        buffer.putInt((System.currentTimeMillis() / 1000).toInt())
        buffer.putShort(nameBytes.size.toShort())
        buffer.put(nameBytes)
        buffer.putShort(messageBytes.size.toShort())
        buffer.put(messageBytes)
        buffer.put(offline)
        
        buffer.flip()
        sendMessage(MSG_IMPROVED_INSTANT_MESSAGE, buffer)
    }

    private fun putUUID(buffer: ByteBuffer, uuid: UUID) {
        buffer.putLong(uuid.mostSignificantBits)
        buffer.putLong(uuid.leastSignificantBits)
    }

    fun clearMessageQueue() {
        synchronized(messageQueue) {
            messageQueue.clear()
        }
    }

    fun getQueueSize(): Int {
        synchronized(messageQueue) {
            return messageQueue.size
        }
    }
}
