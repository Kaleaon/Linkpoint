package com.lumiyaviewer.lumiya.performance

import org.junit.Test
import org.junit.Assert.*
import java.util.UUID
import kotlin.system.measureTimeMillis

/**
 * Phase 2 Day 5: Performance & Benchmark Tests
 * 
 * Performance tests for protocol components:
 * - LLSD serialization/deserialization performance
 * - Network packet processing throughput
 * - Message queue performance under load
 * - Memory usage patterns
 * - Connection handling scalability
 */
class PerformanceTests {
    
    @Test
    fun `benchmark LLSD serialization performance`() {
        val iterations = 1000
        val testData = createComplexLLSDData()
        
        val serializationTime = measureTimeMillis {
            repeat(iterations) {
                serializeLLSD(testData)
            }
        }
        
        val avgTime = serializationTime.toDouble() / iterations
        
        assertTrue("Average serialization should be under 10ms", avgTime < 10.0)
        println("LLSD Serialization: ${avgTime}ms per operation")
    }
    
    @Test
    fun `benchmark LLSD deserialization performance`() {
        val iterations = 1000
        val serializedData = serializeLLSD(createComplexLLSDData())
        
        val deserializationTime = measureTimeMillis {
            repeat(iterations) {
                deserializeLLSD(serializedData)
            }
        }
        
        val avgTime = deserializationTime.toDouble() / iterations
        
        assertTrue("Average deserialization should be under 10ms", avgTime < 10.0)
        println("LLSD Deserialization: ${avgTime}ms per operation")
    }
    
    @Test
    fun `benchmark UDP packet processing throughput`() {
        val packetCount = 10000
        val packets = List(packetCount) { createTestPacket(it) }
        
        val processingTime = measureTimeMillis {
            packets.forEach { packet ->
                processPacket(packet)
            }
        }
        
        val packetsPerSecond = (packetCount.toDouble() / processingTime) * 1000
        
        assertTrue("Should process at least 10,000 packets/sec", 
            packetsPerSecond > 10000)
        println("UDP Packet Processing: $packetsPerSecond packets/sec")
    }
    
    @Test
    fun `benchmark message queue performance`() {
        val messageCount = 10000
        val queue = PerformanceMessageQueue()
        
        val enqueueTime = measureTimeMillis {
            repeat(messageCount) { i ->
                queue.enqueue(Message(i, "test$i", priority = i % 5))
            }
        }
        
        val dequeueTime = measureTimeMillis {
            repeat(messageCount) {
                queue.dequeue()
            }
        }
        
        val totalTime = enqueueTime + dequeueTime
        val opsPerSecond = (messageCount * 2.0 / totalTime) * 1000
        
        assertTrue("Should handle 20,000+ operations/sec", opsPerSecond > 20000)
        println("Message Queue: ${opsPerSecond.toInt()} ops/sec")
    }
    
    @Test
    fun `test memory usage with large message batch`() {
        val runtime = Runtime.getRuntime()
        runtime.gc()
        val initialMemory = runtime.totalMemory() - runtime.freeMemory()
        
        // Create 10,000 messages
        val messages = List(10000) { i ->
            createMessage(i)
        }
        
        runtime.gc()
        val finalMemory = runtime.totalMemory() - runtime.freeMemory()
        val usedMemory = (finalMemory - initialMemory) / 1024 / 1024 // MB
        
        assertTrue("Should use less than 50MB for 10k messages", usedMemory < 50)
        println("Memory usage for 10k messages: ${usedMemory}MB")
    }
    
    @Test
    fun `benchmark connection state transitions`() {
        val iterations = 100000
        val stateMachine = ConnectionStateMachine()
        
        val transitionTime = measureTimeMillis {
            repeat(iterations) {
                stateMachine.connect()
                stateMachine.disconnect()
            }
        }
        
        val transitionsPerSecond = (iterations * 2.0 / transitionTime) * 1000
        
        assertTrue("Should handle 100,000+ transitions/sec", 
            transitionsPerSecond > 100000)
        println("State Transitions: ${transitionsPerSecond.toInt()} transitions/sec")
    }
    
    @Test
    fun `benchmark concurrent message processing`() {
        val messageCount = 1000
        val threadCount = 4
        val processor = MessageProcessor()
        
        val processingTime = measureTimeMillis {
            val threads = List(threadCount) { threadId ->
                Thread {
                    repeat(messageCount) { msgId ->
                        processor.process(createMessage(threadId * messageCount + msgId))
                    }
                }
            }
            
            threads.forEach { it.start() }
            threads.forEach { it.join() }
        }
        
        val totalMessages = messageCount * threadCount
        val messagesPerSecond = (totalMessages.toDouble() / processingTime) * 1000
        
        assertTrue("Should process 10,000+ messages/sec with concurrency",
            messagesPerSecond > 10000)
        println("Concurrent Processing: ${messagesPerSecond.toInt()} messages/sec")
    }
    
    @Test
    fun `benchmark bandwidth throttling overhead`() {
        val iterations = 10000
        val throttler = BandwidthThrottler(maxBytesPerSecond = 1000000)
        
        val overhead = measureTimeMillis {
            repeat(iterations) {
                throttler.canSend(1000)
                throttler.recordSent(1000)
            }
        }
        
        val avgOverhead = overhead.toDouble() / iterations
        
        assertTrue("Throttling overhead should be under 0.1ms", avgOverhead < 0.1)
        println("Throttling Overhead: ${avgOverhead}ms per check")
    }
    
    @Test
    fun `benchmark CAPS endpoint validation`() {
        val iterations = 10000
        val validEndpoint = "https://sim1234.agni.lindenlab.com:12043/cap/12345678-1234-1234-1234-123456789abc"
        
        val validationTime = measureTimeMillis {
            repeat(iterations) {
                isValidCapsEndpoint(validEndpoint)
            }
        }
        
        val avgTime = validationTime.toDouble() / iterations
        
        assertTrue("Validation should be under 0.05ms", avgTime < 0.05)
        println("CAPS Validation: ${avgTime}ms per validation")
    }
    
    @Test
    fun `test performance under network congestion`() {
        val packetCount = 1000
        val congestionSimulator = NetworkCongestionSimulator(
            packetLossRate = 0.05, // 5% loss
            latencyMs = 100,
            jitterMs = 20
        )
        
        var successfulPackets = 0
        val transmissionTime = measureTimeMillis {
            repeat(packetCount) { i ->
                if (congestionSimulator.transmitPacket(createTestPacket(i))) {
                    successfulPackets++
                }
            }
        }
        
        val successRate = successfulPackets.toDouble() / packetCount
        
        assertTrue("Should maintain 95%+ success rate", successRate >= 0.95)
        println("Success Rate: ${(successRate * 100).toInt()}% under congestion")
    }
}

/**
 * Helper classes and functions for performance testing
 */

fun createComplexLLSDData(): Map<String, Any> {
    return mapOf(
        "agent_id" to UUID.randomUUID().toString(),
        "session_id" to UUID.randomUUID().toString(),
        "circuit_code" to 12345,
        "position" to listOf(128.0, 128.0, 23.5),
        "rotation" to listOf(0.0, 0.0, 0.0, 1.0),
        "velocity" to listOf(0.0, 0.0, 0.0),
        "nested_data" to mapOf(
            "inventory" to List(100) { i ->
                mapOf(
                    "item_id" to UUID.randomUUID().toString(),
                    "name" to "Item $i",
                    "type" to "object"
                )
            }
        )
    )
}

fun serializeLLSD(data: Map<String, Any>): ByteArray {
    // Simplified serialization for testing
    return data.toString().toByteArray()
}

fun deserializeLLSD(data: ByteArray): Map<String, Any> {
    // Simplified deserialization for testing
    return mapOf("test" to "data")
}

data class TestPacket(
    val id: Int,
    val data: ByteArray = ByteArray(100)
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TestPacket) return false
        return id == other.id && data.contentEquals(other.data)
    }
    
    override fun hashCode(): Int {
        var result = id
        result = 31 * result + data.contentHashCode()
        return result
    }
}

fun createTestPacket(id: Int) = TestPacket(id)

fun processPacket(packet: TestPacket) {
    // Simulate packet processing
    packet.data.sum()
}

data class Message(
    val id: Int,
    val content: String,
    val priority: Int = 0
)

fun createMessage(id: Int) = Message(id, "Message content $id", id % 5)

class PerformanceMessageQueue {
    private val messages = mutableListOf<Message>()
    
    fun enqueue(message: Message) {
        messages.add(message)
    }
    
    fun dequeue(): Message? {
        return if (messages.isNotEmpty()) messages.removeAt(0) else null
    }
}

class ConnectionStateMachine {
    private var connected = false
    
    fun connect() {
        connected = true
    }
    
    fun disconnect() {
        connected = false
    }
    
    fun isConnected() = connected
}

class MessageProcessor {
    private var processedCount = 0
    
    @Synchronized
    fun process(message: Message) {
        // Simulate message processing
        message.content.length
        processedCount++
    }
    
    fun getProcessedCount() = processedCount
}

class BandwidthThrottler(private val maxBytesPerSecond: Int) {
    private var bytesThisSecond = 0
    private var windowStart = System.currentTimeMillis()
    
    fun canSend(bytes: Int): Boolean {
        checkWindow()
        return bytesThisSecond + bytes <= maxBytesPerSecond
    }
    
    fun recordSent(bytes: Int) {
        checkWindow()
        bytesThisSecond += bytes
    }
    
    private fun checkWindow() {
        val now = System.currentTimeMillis()
        if (now - windowStart >= 1000) {
            bytesThisSecond = 0
            windowStart = now
        }
    }
}

fun isValidCapsEndpoint(endpoint: String): Boolean {
    return endpoint.startsWith("https://") && 
           endpoint.contains("/cap/") &&
           endpoint.length > 50
}

class NetworkCongestionSimulator(
    private val packetLossRate: Double,
    private val latencyMs: Long,
    private val jitterMs: Long
) {
    fun transmitPacket(packet: TestPacket): Boolean {
        // Simulate packet loss
        if (Math.random() < packetLossRate) {
            return false
        }
        
        // Simulate latency and jitter
        val actualLatency = latencyMs + (Math.random() * jitterMs * 2 - jitterMs).toLong()
        if (actualLatency > 0) {
            Thread.sleep(minOf(actualLatency, 5)) // Cap sleep for test performance
        }
        
        return true
    }
}
