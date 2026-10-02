package com.linkpoint.protocol.caps

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class SlidingWindowEventQueueTest {

    private lateinit var slidingWindow: SlidingWindowEventQueue

    @Before
    fun setUp() {
        slidingWindow = SlidingWindowEventQueue()
    }

    @Test
    fun testInOrderEventsReleasedImmediately() {
        val event1 = CapEventQueue.Event("ChatterBoxInvitation", mapOf("id" to "session-1", "message" to "Hello"))
        val event2 = CapEventQueue.Event("ChatterBoxInvitation", mapOf("id" to "session-2", "message" to "World"))

        val released1 = slidingWindow.offer(1, event1)
        assertEquals(1, released1.size)
        assertEquals(event1, released1[0])
        assertEquals(1, slidingWindow.getLastDeliveredSequenceId())

        val released2 = slidingWindow.offer(2, event2)
        assertEquals(1, released2.size)
        assertEquals(event2, released2[0])
        assertEquals(2, slidingWindow.getLastDeliveredSequenceId())
    }

    @Test
    fun testOutOfOrderEventsBufferedUntilGapFilled() {
        val event1 = CapEventQueue.Event("ChatterBoxInvitation", mapOf("id" to "1", "message" to "First"))
        val event2 = CapEventQueue.Event("ChatterBoxInvitation", mapOf("id" to "2", "message" to "Second"))
        val event3 = CapEventQueue.Event("ChatterBoxInvitation", mapOf("id" to "3", "message" to "Third"))

        // Offer seq 1
        val released1 = slidingWindow.offer(1, event1)
        assertEquals(1, released1.size)

        // Offer seq 3 (out of order, seq 2 missing)
        val released3 = slidingWindow.offer(3, event3)
        assertTrue(released3.isEmpty())
        assertEquals(1, slidingWindow.getPendingCount())

        // Offer missing seq 2 -> should release seq 2 and seq 3 in order
        val released2 = slidingWindow.offer(2, event2)
        assertEquals(2, released2.size)
        assertEquals("Second", released2[0].eventData["message"])
        assertEquals("Third", released2[1].eventData["message"])
        assertEquals(0, slidingWindow.getPendingCount())
        assertEquals(3, slidingWindow.getLastDeliveredSequenceId())
    }

    @Test
    fun testDuplicatesAreDropped() {
        val event1 = CapEventQueue.Event("ChatterBoxInvitation", mapOf("message" to "Message 1"))

        val releasedFirst = slidingWindow.offer(10, event1)
        assertEquals(1, releasedFirst.size)

        // Offer same sequence ID again -> duplicate dropped
        val releasedDup = slidingWindow.offer(10, event1)
        assertTrue(releasedDup.isEmpty())

        // Offer earlier sequence ID -> dropped
        val releasedOld = slidingWindow.offer(5, event1)
        assertTrue(releasedOld.isEmpty())
    }

    @Test
    fun testBatchOffering() {
        val events = listOf(
            CapEventQueue.Event("EventA", mapOf("idx" to 1)),
            CapEventQueue.Event("EventB", mapOf("idx" to 2))
        )

        val released = slidingWindow.offerBatch(5, events)
        assertNotNull(released)
        assertEquals(5, slidingWindow.getLastDeliveredSequenceId())
    }

    @Test
    fun testMemoryGuardrailEnforcement() {
        // Create small window with 1 KB cap for testing
        val smallWindow = SlidingWindowEventQueue(maxMemoryBytes = 1024L)

        // Fill window with out-of-order events
        for (i in 100 downTo 2) {
            val largeMsg = "A".repeat(100)
            val event = CapEventQueue.Event("ChatterBox", mapOf("data" to largeMsg))
            smallWindow.offer(i, event)
        }

        // Verify memory remains below cap
        assertTrue("Memory usage ${smallWindow.getMemoryUsageBytes()} should be <= 1024", smallWindow.getMemoryUsageBytes() <= 1024L)
    }
}
