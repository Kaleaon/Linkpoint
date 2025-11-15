package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.types.LLVector3
import java.net.Inet4Address
import java.nio.ByteBuffer
import java.util.UUID

/**
 * Simulator viewer time message (0x96)
 */
class SimulatorViewerTimeMessage : SLMessage() {
    var usecSinceStart: Long = 0L
    var secondsPerDay: Int = 0
    var secondsPerYear: Int = 0
    var sunDirection: LLVector3 = LLVector3()
    var sunPhase: Float = 0f
    var sunAngularVelocity: LLVector3 = LLVector3()

    override fun packPayload(buffer: ByteBuffer) {
        packLong(buffer, usecSinceStart)
        packInt(buffer, secondsPerDay)
        packInt(buffer, secondsPerYear)
        sunDirection.pack(buffer)
        packFloat(buffer, sunPhase)
        sunAngularVelocity.pack(buffer)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        usecSinceStart = unpackLong(buffer)
        secondsPerDay = unpackInt(buffer)
        secondsPerYear = unpackInt(buffer)
        sunDirection = LLVector3.unpack(buffer)
        sunPhase = unpackFloat(buffer)
        sunAngularVelocity = LLVector3.unpack(buffer)
    }

    override fun getMessageID(): Int = SLMessageFactory.MessageIDs.SIMULATOR_VIEWER_TIME

    override fun getMessageName(): String = "SimulatorViewerTimeMessage"
}

/**
 * Enable simulator message (0x97)
 */
class EnableSimulatorMessage : SLMessage() {
    var regionHandle: Long = 0L
    var simAddress: Inet4Address? = null
    var simPort: Int = 0

    override fun packPayload(buffer: ByteBuffer) {
        packLong(buffer, regionHandle)
        packIPAddress(buffer, simAddress)
        require(simPort in 0..0xFFFF) { "simPort out of range ($simPort)" }
        packUInt16(buffer, simPort)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        regionHandle = unpackLong(buffer)
        simAddress = unpackIPAddress(buffer)
        simPort = unpackUInt16(buffer)
    }

    override fun getMessageID(): Int = SLMessageFactory.MessageIDs.ENABLE_SIMULATOR

    override fun getMessageName(): String = "EnableSimulator"
}

/**
 * Disable simulator message (0x98)
 */
class DisableSimulatorMessage : SLMessage() {
    override fun packPayload(buffer: ByteBuffer) {
        // No payload
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        // No payload
    }

    override fun getMessageID(): Int = SLMessageFactory.MessageIDs.DISABLE_SIMULATOR

    override fun getMessageName(): String = "DisableSimulator"
}

/**
 * Confirm enable simulator message (0x08)
 */
class ConfirmEnableSimulatorMessage : SLMessage() {
    var agentId: UUID = UUID.randomUUID()
    var sessionId: UUID = UUID.randomUUID()

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, sessionId)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        sessionId = unpackUUID(buffer)
    }

    override fun getMessageID(): Int = SLMessageFactory.MessageIDs.CONFIRM_ENABLE_SIMULATOR

    override fun getMessageName(): String = "ConfirmEnableSimulator"
}
