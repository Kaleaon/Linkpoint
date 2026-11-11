package com.linkpoint.linden.llmessage

import java.util.concurrent.ArrayBlockingQueue

class LLPacketRing(private val capacity: Int = 256) {
    enum class DropPolicy { DROP_TAIL, DROP_HEAD }

    private val queue = ArrayBlockingQueue<ByteArray>(capacity)
    var dropPolicy: DropPolicy = DropPolicy.DROP_TAIL
    var totalDropped: Long = 0L
    var totalBytes: Long = 0L

    fun enqueue(packet: ByteArray): Boolean {
        if (queue.offer(packet)) {
            totalBytes += packet.size
            return true
        }
        return when (dropPolicy) {
            DropPolicy.DROP_TAIL -> { totalDropped++; false }
            DropPolicy.DROP_HEAD -> {
                queue.poll()
                totalDropped++
                queue.offer(packet).also { if (it) totalBytes += packet.size }
            }
        }
    }

    fun dequeue(): ByteArray? = queue.poll()

    fun peek(): ByteArray? = queue.peek()

    fun size(): Int = queue.size

    fun isEmpty(): Boolean = queue.isEmpty()

    fun isFull(): Boolean = queue.remainingCapacity() == 0

    fun clear() = queue.clear()

    fun drainTo(dest: MutableList<ByteArray>, maxItems: Int = Int.MAX_VALUE): Int =
        queue.drainTo(dest, maxItems)
}
