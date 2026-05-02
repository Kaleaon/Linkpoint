package com.linkpoint.linden.llmessage

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LLCircuitAckSemanticsTest {

    private fun circuit(): LLCircuit = LLCircuit(Host.fromString("127.0.0.1:13000")!!)

    @Test
    fun `recordInboundSequenceForAck only records pending ACKs`() {
        val circuit = circuit()
        val seq = 42u

        circuit.queueForResend(seq, byteArrayOf(1, 2, 3))
        circuit.recordInboundSequenceForAck(seq)

        assertTrue(circuit.getPendingAcks().contains(seq))
        assertTrue(circuit.needsResend(seq))
    }

    @Test
    fun `handleAckForOutboundSequence only clears resend tracking`() {
        val circuit = circuit()
        val seq = 7u

        circuit.recordInboundSequenceForAck(seq)
        circuit.queueForResend(seq, byteArrayOf(9, 9, 9))
        circuit.handleAckForOutboundSequence(seq)

        assertFalse(circuit.needsResend(seq))
        assertTrue(circuit.getPendingAcks().contains(seq))
    }

    @Test
    fun `legacy ackPacket preserves old combined behavior`() {
        val circuit = circuit()
        val seq = 99u

        circuit.queueForResend(seq, byteArrayOf(8, 8, 8))
        circuit.ackPacket(seq)

        assertTrue(circuit.getPendingAcks().contains(seq))
        assertFalse(circuit.needsResend(seq))
    }
}
