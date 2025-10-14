package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.*

/**
 * Packet acknowledgment message
 */
class PacketAckMessage : SLMessage() {
    val packets = mutableListOf<Int>()

    override fun packPayload(buffer: ByteBuffer) {
        buffer.put(packets.size.toByte())
        for (packetId in packets) {
            buffer.putInt(packetId)
        }
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        val count = buffer.get().toInt() and 0xFF
        packets.clear()
        for (i in 0 until count) {
            packets.add(buffer.getInt())
        }
    }

    override fun getMessageID(): Int = SLMessageFactory.MessageIDs.PACKET_ACK

    override fun getMessageName(): String = "PacketAck"
}

/**
 * Open circuit message
 */
class OpenCircuitMessage : SLMessage() {
    var circuitCode: Int = 0
    var sessionId: UUID = UUID.randomUUID()
    var agentId: UUID = UUID.randomUUID()

    override fun packPayload(buffer: ByteBuffer) {
        buffer.putInt(circuitCode)
        buffer.putLong(sessionId.mostSignificantBits)
        buffer.putLong(sessionId.leastSignificantBits)
        buffer.putLong(agentId.mostSignificantBits)
        buffer.putLong(agentId.leastSignificantBits)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        circuitCode = buffer.getInt()
        val sessionMsb = buffer.getLong()
        val sessionLsb = buffer.getLong()
        sessionId = UUID(sessionMsb, sessionLsb)
        val agentMsb = buffer.getLong()
        val agentLsb = buffer.getLong()
        agentId = UUID(agentMsb, agentLsb)
    }

    override fun getMessageID(): Int = SLMessageFactory.MessageIDs.OPEN_CIRCUIT

    override fun getMessageName(): String = "OpenCircuit"
}

/**
 * Close circuit message
 */
class CloseCircuitMessage : SLMessage() {
    override fun packPayload(buffer: ByteBuffer) {
        // No payload
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        // No payload
    }

    override fun getMessageID(): Int = SLMessageFactory.MessageIDs.CLOSE_CIRCUIT

    override fun getMessageName(): String = "CloseCircuit"
}
