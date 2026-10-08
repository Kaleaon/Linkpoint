package com.linkpoint.chat.queue

import java.util.UUID

/**
 * Thread-safe, bounded ring buffer queue for high-rate inbound chat packets.
 *
 * Incoming messages from simulator network dispatchers are enqueued off the UI thread.
 * If pending item counts exceed safety limits ([backpressureThreshold]), backpressure logic
 * trims excess older items while strictly preserving sequence ordering and injecting a
 * system notice item to inform the user without UI stutters.
 */
class ChatRingBuffer(
    val capacity: Int = DEFAULT_CAPACITY,
    val backpressureThreshold: Int = DEFAULT_BACKPRESSURE_THRESHOLD,
) {
    companion object {
        const val DEFAULT_CAPACITY = 1000
        const val DEFAULT_BACKPRESSURE_THRESHOLD = 500
    }

    private val lock = Any()
    private val buffer = Array<QueuedChatMessage?>(capacity) { null }
    private var head = 0
    private var tail = 0
    private var count = 0

    /**
     * Enqueue a new message item off the UI thread.
     * Applies backpressure if capacity or threshold limits are met.
     */
    fun enqueue(item: QueuedChatMessage): Boolean = synchronized(lock) {
        if (count >= backpressureThreshold) {
            applyBackpressureLocked()
        }
        if (count >= capacity) {
            // Buffer full even after backpressure attempt; overwrite/drop oldest
            buffer[head] = null
            head = (head + 1) % capacity
            count--
        }
        buffer[tail] = item
        tail = (tail + 1) % capacity
        count++
        return true
    }

    /**
     * Bulk enqueue message items.
     */
    fun enqueueAll(items: List<QueuedChatMessage>) = synchronized(lock) {
        for (item in items) {
            enqueue(item)
        }
    }

    /**
     * Drain all pending items accumulated in the ring buffer.
     * Returns a list of items in strict FIFO sequence order.
     */
    fun drainAll(): List<QueuedChatMessage> = synchronized(lock) {
        if (count == 0) return emptyList()

        if (count >= backpressureThreshold) {
            applyBackpressureLocked()
        }

        val result = ArrayList<QueuedChatMessage>(count)
        var idx = head
        for (i in 0 until count) {
            buffer[idx]?.let { result.add(it) }
            buffer[idx] = null
            idx = (idx + 1) % capacity
        }

        head = 0
        tail = 0
        count = 0
        return result
    }

    /**
     * Current number of pending items in the ring buffer.
     */
    val size: Int
        get() = synchronized(lock) { count }

    /**
     * Clear all pending items.
     */
    fun clear() = synchronized(lock) {
        for (i in 0 until capacity) {
            buffer[i] = null
        }
        head = 0
        tail = 0
        count = 0
    }

    private fun applyBackpressureLocked() {
        if (count < backpressureThreshold) return

        val targetCount = backpressureThreshold / 2
        val excess = count - targetCount
        if (excess <= 0) return

        // Drop oldest excess items from head
        for (i in 0 until excess) {
            buffer[head] = null
            head = (head + 1) % capacity
            count--
        }

        // Inject a system notice at the head position
        val notice = QueuedChatMessage.SystemNotice(
            id = UUID.randomUUID().toString(),
            noticeText = "[System] High chat load detected: $excess older messages merged to maintain smooth performance.",
        )

        // Prepend notice at head
        head = (head - 1 + capacity) % capacity
        buffer[head] = notice
        count++
    }
}
