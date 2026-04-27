package com.linkpoint.protocol.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import java.util.UUID

class AgentIdentityTest {

    @Test
    fun packetIdentityValidation_failsFastForZeroAgentId() {
        val identity = AgentIdentity(
            agentId = UUID(0L, 0L),
            sessionId = UUID.randomUUID(),
            circuitCode = 100
        )

        try {
            identity.requireValid("test")
            fail("Expected invalid identity to throw")
        } catch (e: IllegalArgumentException) {
            assertTrue(e.message?.contains("agentId is zero UUID") == true)
        }
    }

    @Test
    fun packetIdentityValidation_failsFastForMissingCircuitAndRegion() {
        val identity = AgentIdentity(
            agentId = UUID.randomUUID(),
            sessionId = UUID.randomUUID(),
            circuitCode = 0
        )

        try {
            identity.requireValid("test")
            fail("Expected invalid identity to throw")
        } catch (e: IllegalArgumentException) {
            assertTrue(e.message?.contains("regionId/circuit is missing or invalid") == true)
        }
    }

    @Test
    fun packetIdentityValidation_succeedsForValidIdentity() {
        val identity = AgentIdentity(
            agentId = UUID.randomUUID(),
            sessionId = UUID.randomUUID(),
            circuitCode = 42
        ).requireValid("test")

        assertEquals(42, identity.circuitCode)
    }
}

