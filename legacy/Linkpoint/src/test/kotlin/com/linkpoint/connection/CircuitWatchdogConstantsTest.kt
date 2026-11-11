package com.linkpoint.connection

import com.linkpoint.protocol.circuit.LinkpointConstants
import com.linkpoint.protocol.messages.UDPConnectionFixed
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Pins the circuit watchdog constants and the network-state transition enum
 * to Lumiya-parity values. Regressing these reintroduces the
 * 2026-04-25 Athanasia failure: 4 unanswered pings, no recovery, because
 * the threshold sat at 5 and the diagnostic wasn't wired.
 */
class CircuitWatchdogConstantsTest {

    @Test
    fun `UNANSWERED_PINGS_DISCONNECT matches Lumiya's UNANSWERED_PINGS = 3`() {
        // Lumiya's SLCircuit.java:39 uses UNANSWERED_PINGS = 3.
        // 3 * PING_INTERVAL_MS (5s) + NEED_PING_TIMEOUT_MS (10s) = 25s worst-case
        // dead-time before reconnect, which is the right balance for cellular:
        // long enough to avoid false-fires on transient handoffs, short enough
        // that the user sees recovery rather than abandonment.
        assertEquals(3, LinkpointConstants.UNANSWERED_PINGS_DISCONNECT)
    }

    @Test
    fun `ping watchdog timing matches Lumiya constants`() {
        // SLCircuit.java:36-37
        assertEquals(10_000L, LinkpointConstants.NEED_PING_TIMEOUT_MS)
        assertEquals(5_000L, LinkpointConstants.PING_INTERVAL_MS)
    }

    @Test
    fun `reliable message lifecycle matches Lumiya constants`() {
        // SLCircuit.java:34-35
        assertEquals(5_000L, LinkpointConstants.MESSAGE_TIMEOUT_MS)
        assertEquals(3, LinkpointConstants.MESSAGE_MAX_RETRIES)
    }

    @Test
    fun `POST_RECONNECT_VERIFY_MS escalates faster than the inbound stall watchdog`() {
        // The post-reconnect silence check must fire before the (rebind cooldown +
        // inbound stall) path; otherwise the user sits 75s on a dead circuit.
        // It also needs slack over typical UseCircuitCode RTT on cellular (~5–8s
        // observed in capture) to avoid false escalations on slow handshakes.
        val verifyMs = UDPConnectionFixed.POST_RECONNECT_VERIFY_MS
        assertTrue("POST_RECONNECT_VERIFY_MS must be > 8s slack", verifyMs > 8_000L)
        assertTrue(
            "POST_RECONNECT_VERIFY_MS must be < SOCKET_REBIND_COOLDOWN_MS to escalate first",
            verifyMs < UDPConnectionFixed.SOCKET_REBIND_COOLDOWN_MS
        )
    }

    @Test
    fun `NetworkStateTransition exposes the three transitions LinkpointApp wires`() {
        // The enum is the contract between the UDP layer's watchdog and the
        // NetworkStateManager. Removing or renaming any of these values breaks
        // the diagnostic counter.
        val values = UDPConnectionFixed.NetworkStateTransition.values().map { it.name }.toSet()
        assertTrue("RECONNECTING transition must exist", values.contains("RECONNECTING"))
        assertTrue("CONNECTED transition must exist", values.contains("CONNECTED"))
        assertTrue("FAULTED transition must exist", values.contains("FAULTED"))
        assertEquals("Exactly three transitions", 3, values.size)
    }
}
