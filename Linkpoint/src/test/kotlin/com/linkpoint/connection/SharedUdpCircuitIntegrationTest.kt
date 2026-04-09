package com.linkpoint.connection

import com.linkpoint.network.core.AgentCircuit
import com.linkpoint.network.core.CircuitState
import com.linkpoint.network.core.TempCircuit
import com.linkpoint.protocol.auth.AuthReply
import com.linkpoint.protocol.messages.UDPConnectionFixed
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.UUID

class SharedUdpCircuitIntegrationTest {

    @Test
    fun `agent and temp circuit establishment do not open second socket`() = runBlocking {
        val sharedConnection = UDPConnectionFixed()
        sharedConnection.setSessionInfo(UUID.randomUUID(), UUID.randomUUID())

        val authReply = AuthReply(
            sessionId = UUID.randomUUID(),
            agentId = UUID.randomUUID(),
            circuitCode = 123456,
            simIP = "127.0.0.1",
            simPort = 13000,
            seedCapability = "https://example.com/seed"
        )

        val agentCircuit = AgentCircuit(authReply, sharedConnection = sharedConnection)
        val tempCircuit = TempCircuit(authReply, sharedConnection = sharedConnection)

        repeat(20) {
            if (agentCircuit.circuitState.value == CircuitState.CIRCUIT_READY &&
                tempCircuit.circuitState.value == TempCircuit.CircuitState.ACTIVE
            ) {
                return@repeat
            }
            delay(25)
        }

        assertTrue(
            "Agent circuit should become ready on shared connection",
            agentCircuit.circuitState.value == CircuitState.CIRCUIT_READY
        )
        assertTrue(
            "Temp circuit should become active on shared connection",
            tempCircuit.circuitState.value == TempCircuit.CircuitState.ACTIVE
        )
        assertEquals(
            "No private UDP socket should be opened during circuit establishment",
            0,
            sharedConnection.getConnectAttemptCount()
        )

        tempCircuit.close()
        agentCircuit.close()
    }
}
