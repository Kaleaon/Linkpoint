package com.linkpoint.protocol.caps

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class CapEventQueueSequenceTrackingTest {

    private lateinit var eventQueue: CapEventQueue

    @Before
    fun setUp() {
        eventQueue = CapEventQueue()
    }

    @Test
    fun testSequenceIdIncrementsAndPersistsOnErrors() {
        // Initial sequence ID is 0
        assertEquals(0, eventQueue.getHighestAckSequenceId())

        // Set sequence ID simulating successful response from grid simulator
        eventQueue.setHighestAckSequenceId(105)
        assertEquals(105, eventQueue.getHighestAckSequenceId())

        // Simulating sequence update to 106
        eventQueue.setHighestAckSequenceId(106)
        assertEquals(106, eventQueue.getHighestAckSequenceId())

        // Ensure attempting to set a lower sequence ID does not cause regression
        eventQueue.setHighestAckSequenceId(50)
        assertEquals(106, eventQueue.getHighestAckSequenceId())
    }

    @Test
    fun testNetworkInterfaceChangedTrigger() {
        eventQueue.setHighestAckSequenceId(42)

        // Calling network interface changed should not reset sequence ID
        eventQueue.onNetworkInterfaceChanged()
        assertEquals(42, eventQueue.getHighestAckSequenceId())
    }

    @Test
    fun testStatisticsContainSequenceAndMemoryInfo() {
        eventQueue.setHighestAckSequenceId(88)
        val stats = eventQueue.getStatistics()

        assertEquals(88, stats["highestAckSequenceId"])
        assertNotNull(stats["slidingWindowPending"])
        assertNotNull(stats["slidingWindowMemoryBytes"])
    }
}
