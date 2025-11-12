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
        packInt(buffer, circuitCode)
        packUUID(buffer, sessionId)
        packUUID(buffer, agentId)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        circuitCode = unpackInt(buffer)
        sessionId = unpackUUID(buffer)
        agentId = unpackUUID(buffer)
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

/**
 * Use circuit code message
 */
class UseCircuitCodeMessage : SLMessage() {
    var circuitCode: Int = 0
    var sessionId: UUID = UUID.randomUUID()
    var agentId: UUID = UUID.randomUUID()

    override fun packPayload(buffer: ByteBuffer) {
        packInt(buffer, circuitCode)
        packUUID(buffer, sessionId)
        packUUID(buffer, agentId)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        circuitCode = unpackInt(buffer)
        sessionId = unpackUUID(buffer)
        agentId = unpackUUID(buffer)
    }

    override fun getMessageID(): Int = SLMessageFactory.MessageIDs.USE_CIRCUIT_CODE

    override fun getMessageName(): String = "UseCircuitCode"
}

/**
 * Complete agent movement message
 */
class CompleteAgentMovementMessage : SLMessage() {
    var agentId: UUID = UUID.randomUUID()
    var sessionId: UUID = UUID.randomUUID()
    var circuitCode: Int = 0

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, sessionId)
        packInt(buffer, circuitCode)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        sessionId = unpackUUID(buffer)
        circuitCode = unpackInt(buffer)
    }

    override fun getMessageID(): Int = SLMessageFactory.MessageIDs.COMPLETE_AGENT_MOVEMENT

    override fun getMessageName(): String = "CompleteAgentMovement"
}

/**
 * Start ping check message
 */
class StartPingCheckMessage : SLMessage() {
    var pingId: Int = 0
    var oldestUnacked: Int = 0

    override fun packPayload(buffer: ByteBuffer) {
        packByte(buffer, 1) // block frequency identifier
        require(pingId in 0..0xFF) { "pingId out of range ($pingId)" }
        packByte(buffer, pingId)
        packInt(buffer, oldestUnacked)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        /* frequency */ unpackByte(buffer)
        pingId = unpackByte(buffer)
        oldestUnacked = unpackInt(buffer)
    }

    override fun getMessageID(): Int = SLMessageFactory.MessageIDs.START_PING_CHECK

    override fun getMessageName(): String = "StartPingCheck"
}

/**
 * Complete ping check message
 */
class CompletePingCheckMessage : SLMessage() {
    var pingId: Int = 0

    override fun packPayload(buffer: ByteBuffer) {
        packByte(buffer, 2) // block frequency identifier
        require(pingId in 0..0xFF) { "pingId out of range ($pingId)" }
        packByte(buffer, pingId)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        /* frequency */ unpackByte(buffer)
        pingId = unpackByte(buffer)
    }

    override fun getMessageID(): Int = SLMessageFactory.MessageIDs.COMPLETE_PING_CHECK

    override fun getMessageName(): String = "CompletePingCheck"
}
