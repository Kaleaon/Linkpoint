package com.linkpoint.network.events

import android.util.Log
import com.linkpoint.network.NetworkLogger
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

/**
 * Event Bus
 * 
 * Centralized event distribution system for reactive programming.
 * Based on Lumiya's EventBus implementation.
 * 
 * Features:
 * - Type-safe event publishing and subscribing
 * - Flow-based event streams
 * - Priority-based event handling
 * - Thread-safe operations
 * 
 * Mobile-First Considerations:
 * - Efficient event distribution
 * - Coroutine-based for mobile performance
 * - Memory-conscious subscriber management
 * - Backpressure support
 */
object EventBus {
    
    private const val TAG = "EventBus"
    private const val DEFAULT_CHANNEL_CAPACITY = 100
    
    /**
     * Event channels by type
     */
    private val channels = ConcurrentHashMap<KClass<*>, BroadcastChannel<Any>>()
    
    /**
     * Subscriber counts by type
     */
    private val subscriberCounts = ConcurrentHashMap<KClass<*>, Int>()
    
    /**
     * Mutex for thread-safe operations
     */
    private val mutex = Mutex()
    
    /**
     * Statistics
     */
    private var totalEventsPublished = 0
    private var totalEventsDelivered = 0
    
    /**
     * Publish an event to all subscribers
     * 
     * @param event The event to publish
     */
    suspend fun <T : Any> publish(event: T) {
        val eventClass = event::class
        totalEventsPublished++
        
        NetworkLogger.log(TAG, "Publishing event: ${eventClass.simpleName}")
        
        val channel = mutex.withLock {
            channels.getOrPut(eventClass) {
                BroadcastChannel<Any>(DEFAULT_CHANNEL_CAPACITY).also {
                    subscriberCounts[eventClass] = 0
                }
            }
        }
        
        try {
            channel.send(event)
            totalEventsDelivered++
        } catch (e: Exception) {
            NetworkLogger.log(TAG, "Failed to publish event ${eventClass.simpleName}: ${e.message}", Log.ERROR)
        }
    }
    
    /**
     * Subscribe to events of a specific type
     * 
     * @param eventType The event type to subscribe to
     * @param scope The coroutine scope for the subscription
     * @param handler The handler for received events
     * @return Job that can be cancelled to unsubscribe
     */
    fun <T : Any> subscribe(
        eventType: KClass<T>,
        scope: CoroutineScope,
        handler: suspend (T) -> Unit
    ): Job {
        return scope.launch {
            val channel = mutex.withLock {
                channels.getOrPut(eventType) {
                    BroadcastChannel<Any>(DEFAULT_CHANNEL_CAPACITY).also {
                        subscriberCounts[eventType] = 0
                    }
                }.also {
                    subscriberCounts[eventType] = subscriberCounts.getOrDefault(eventType, 0) + 1
                }
            }
            
            NetworkLogger.log(TAG, "Subscribed to ${eventType.simpleName}")
            
            try {
                channel.asFlow()
                    .filter { eventType.isInstance(it) }
                    .map { @Suppress("UNCHECKED_CAST") it as T }
                    .collect { event ->
                        try {
                            handler(event)
                        } catch (e: Exception) {
                            NetworkLogger.log(TAG, "Handler error for ${eventType.simpleName}: ${e.message}", Log.ERROR)
                        }
                    }
            } catch (e: Exception) {
                NetworkLogger.log(TAG, "Subscription error for ${eventType.simpleName}: ${e.message}", Log.ERROR)
            } finally {
                mutex.withLock {
                    val count = subscriberCounts.getOrDefault(eventType, 0) - 1
                    if (count <= 0) {
                        channels.remove(eventType)?.close()
                        subscriberCounts.remove(eventType)
                    } else {
                        subscriberCounts[eventType] = count
                    }
                }
                NetworkLogger.log(TAG, "Unsubscribed from ${eventType.simpleName}")
            }
        }
    }
    
    /**
     * Get a flow of events of a specific type
     * 
     * @param eventType The event type
     * @return Flow of events
     */
    fun <T : Any> getEvents(eventType: KClass<T>): Flow<T> {
        val channel = mutex.withLock {
            channels.getOrPut(eventType) {
                BroadcastChannel<Any>(DEFAULT_CHANNEL_CAPACITY).also {
                    subscriberCounts[eventType] = 0
                }
            }
        }
        
        return channel.asFlow()
            .filter { eventType.isInstance(it) }
            .map { @Suppress("UNCHECKED_CAST") it as T }
    }
    
    /**
     * Clear all channels and subscribers
     */
    suspend fun clearAll() {
        mutex.withLock {
            channels.values.forEach { it.close() }
            channels.clear()
            subscriberCounts.clear()
            totalEventsPublished = 0
            totalEventsDelivered = 0
        }
        NetworkLogger.log(TAG, "All channels cleared")
    }
    
    /**
     * Get statistics
     */
    fun getStatistics(): Map<String, Any> {
        return mapOf(
            "totalEventsPublished" to totalEventsPublished,
            "totalEventsDelivered" to totalEventsDelivered,
            "activeChannels" to channels.size,
            "totalSubscribers" to subscriberCounts.values.sum()
        )
    }
}

/**
 * Common event types
 */

/**
 * Message received event
 */
data class MessageReceivedEvent(
    val messageId: Int,
    val data: ByteArray,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Connection state change event
 */
data class ConnectionStateChangedEvent(
    val oldState: ConnectionState,
    val newState: ConnectionState,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Circuit established event
 */
data class CircuitEstablishedEvent(
    val circuitCode: Int,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Texture download event
 */
data class TextureDownloadEvent(
    val textureId: Long,
    val progress: Float,
    val data: ByteArray? = null,
    val error: String? = null
)

/**
 * Mesh download event
 */
data class MeshDownloadEvent(
    val meshId: Long,
    val progress: Float,
    val data: ByteArray? = null,
    val error: String? = null
)

/**
 * Connection states
 */
enum class ConnectionState {
    DISCONNECTED,
    CONNECTING,
    CONNECTED,
    ERROR
}