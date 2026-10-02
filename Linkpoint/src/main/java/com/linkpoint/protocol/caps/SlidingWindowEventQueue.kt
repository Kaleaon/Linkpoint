package com.linkpoint.protocol.caps

import android.util.Log
import com.linkpoint.network.NetworkLogger
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentSkipListMap

/**
 * Sliding Window Queue for EventQueue events and Group Chat delivery.
 *
 * Buffers out-of-order incoming events before releasing them to consumers,
 * deduplicates repeated events, and enforces a strict memory cap of 5 MB per active session.
 */
class SlidingWindowEventQueue(
    private val maxMemoryBytes: Long = MAX_MEMORY_BYTES
) {
    companion object {
        private const val TAG = "SlidingWindowEventQueue"
        const val MAX_MEMORY_BYTES = 5 * 1024 * 1024L // 5 MB per session guardrail
        private const val MAX_DEDUPE_HISTORY = 1000
    }

    private var lastDeliveredSequenceId: Int = 0
    private val pendingEvents = ConcurrentSkipListMap<Int, CapEventQueue.Event>()
    private val processedSequenceIds = ConcurrentHashMap.newKeySet<Int>()
    private val processedMessageDedupeKeys = ConcurrentHashMap.newKeySet<String>()
    
    @Volatile
    private var currentMemoryBytes: Long = 0L

    /**
     * Sets the baseline highest acknowledged sequence ID.
     */
    @Synchronized
    fun setBaselineSequenceId(seqId: Int) {
        if (seqId > lastDeliveredSequenceId) {
            lastDeliveredSequenceId = seqId
        }
    }

    /**
     * Gets the last delivered sequence ID.
     */
    fun getLastDeliveredSequenceId(): Int = lastDeliveredSequenceId

    /**
     * Offers an event with an explicit sequence ID.
     * Returns a list of in-order events ready for rendering/processing.
     */
    @Synchronized
    fun offer(sequenceId: Int, event: CapEventQueue.Event): List<CapEventQueue.Event> {
        val dedupeKey = generateDedupeKey(sequenceId, event)
        if (sequenceId > 0 && sequenceId <= lastDeliveredSequenceId) {
            NetworkLogger.log(
                NetworkLogger.Level.DEBUG,
                NetworkLogger.Category.UDP,
                "SlidingWindow: Discarding duplicate/old sequence ID $sequenceId (lastDelivered=$lastDeliveredSequenceId)"
            )
            return emptyList()
        }

        if (processedSequenceIds.contains(sequenceId) || (dedupeKey != null && processedMessageDedupeKeys.contains(dedupeKey))) {
            NetworkLogger.log(
                NetworkLogger.Level.DEBUG,
                NetworkLogger.Category.UDP,
                "SlidingWindow: Discarding duplicate event with key $dedupeKey / seq $sequenceId"
            )
            return emptyList()
        }

        // Calculate approximate size of event
        val eventSize = estimateEventSize(event)
        
        // Enforce memory guardrail (5 MB)
        if (currentMemoryBytes + eventSize > maxMemoryBytes) {
            NetworkLogger.log(
                NetworkLogger.Level.WARN,
                NetworkLogger.Category.UDP,
                "SlidingWindow: Memory usage (${currentMemoryBytes + eventSize} bytes) exceeds 5 MB limit. Evicting oldest pending events."
            )
            evictOldestToFit(eventSize)
        }

        if (sequenceId > 0) {
            pendingEvents[sequenceId] = event
            currentMemoryBytes += eventSize
        } else {
            // Sequence ID not provided or 0: deliver immediately if not duplicate
            if (dedupeKey != null) {
                rememberDedupeKey(dedupeKey)
            }
            return listOf(event)
        }

        return drainInOrderEvents()
    }

    /**
     * Offers a batch of events associated with a batch sequence ID.
     */
    @Synchronized
    fun offerBatch(batchSequenceId: Int, events: List<CapEventQueue.Event>): List<CapEventQueue.Event> {
        if (events.isEmpty()) {
            if (batchSequenceId > lastDeliveredSequenceId) {
                lastDeliveredSequenceId = batchSequenceId
            }
            return emptyList()
        }

        val readyEvents = mutableListOf<CapEventQueue.Event>()
        for ((index, event) in events.withIndex()) {
            val eventSeqId = if (batchSequenceId > 0) batchSequenceId else 0
            val ready = offer(eventSeqId, event)
            readyEvents.addAll(ready)
        }

        if (batchSequenceId > lastDeliveredSequenceId) {
            lastDeliveredSequenceId = batchSequenceId
        }

        return readyEvents
    }

    /**
     * Drains contiguous in-order events starting from lastDeliveredSequenceId + 1.
     */
    @Synchronized
    private fun drainInOrderEvents(): List<CapEventQueue.Event> {
        val ready = mutableListOf<CapEventQueue.Event>()
        
        if (lastDeliveredSequenceId == 0 && pendingEvents.isNotEmpty()) {
            // Initialize lastDeliveredSequenceId to lowest pending - 1 if uninitialized
            val lowestKey = pendingEvents.firstKey()
            lastDeliveredSequenceId = lowestKey - 1
        }

        var nextExpected = lastDeliveredSequenceId + 1
        while (pendingEvents.containsKey(nextExpected)) {
            val event = pendingEvents.remove(nextExpected) ?: break
            val eventSize = estimateEventSize(event)
            currentMemoryBytes = maxOf(0L, currentMemoryBytes - eventSize)
            
            lastDeliveredSequenceId = nextExpected
            processedSequenceIds.add(nextExpected)
            
            val dedupeKey = generateDedupeKey(nextExpected, event)
            if (dedupeKey != null) {
                rememberDedupeKey(dedupeKey)
            }

            ready.add(event)
            nextExpected++
        }

        // Clean up processed ID sets if too large
        if (processedSequenceIds.size > MAX_DEDUPE_HISTORY) {
            val minToKeep = lastDeliveredSequenceId - 100
            processedSequenceIds.removeIf { it < minToKeep }
        }

        return ready
    }

    private fun rememberDedupeKey(key: String) {
        processedMessageDedupeKeys.add(key)
        if (processedMessageDedupeKeys.size > MAX_DEDUPE_HISTORY) {
            processedMessageDedupeKeys.clear()
        }
    }

    private fun generateDedupeKey(seqId: Int, event: CapEventQueue.Event): String? {
        val eventData = event.eventData
        val msgText = eventData["message"]?.toString() ?: eventData["text"]?.toString()
        val fromId = eventData["from_id"]?.toString() ?: eventData["from_agent_id"]?.toString()
        val timestamp = eventData["timestamp"]?.toString() ?: event.timestamp.toString()
        
        return if (msgText != null || fromId != null) {
            "${event.eventType}_${fromId}_${msgText}_$timestamp"
        } else if (seqId > 0) {
            "${event.eventType}_$seqId"
        } else {
            null
        }
    }

    private fun estimateEventSize(event: CapEventQueue.Event): Long {
        var size = 128L // Object overhead
        size += event.eventType.length * 2L
        for ((k, v) in event.eventData) {
            size += k.length * 2L
            size += v.toString().length * 2L
        }
        return size
    }

    private fun evictOldestToFit(requiredBytes: Long) {
        while (pendingEvents.isNotEmpty() && currentMemoryBytes + requiredBytes > maxMemoryBytes) {
            val oldestKey = pendingEvents.firstKey()
            val evicted = pendingEvents.remove(oldestKey)
            if (evicted != null) {
                val evictedSize = estimateEventSize(evicted)
                currentMemoryBytes = maxOf(0L, currentMemoryBytes - evictedSize)
                NetworkLogger.log(NetworkLogger.Level.WARN, NetworkLogger.Category.UDP, "Evicted event $oldestKey to preserve memory limit")
            }
        }
    }

    /**
     * Returns current memory usage in bytes.
     */
    fun getMemoryUsageBytes(): Long = currentMemoryBytes

    /**
     * Returns number of buffered pending events.
     */
    fun getPendingCount(): Int = pendingEvents.size

    /**
     * Clears all state.
     */
    @Synchronized
    fun clear() {
        pendingEvents.clear()
        processedSequenceIds.clear()
        processedMessageDedupeKeys.clear()
        currentMemoryBytes = 0L
        lastDeliveredSequenceId = 0
    }
}
