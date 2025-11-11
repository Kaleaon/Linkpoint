package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.types.LLVector3
import java.net.Inet4Address
import java.nio.ByteBuffer
import java.nio.charset.Charset
import java.util.*

/**
 * Region handshake message
 */
class RegionHandshakeMessage : SLMessage() {
    var regionFlags: Int = 0
    var simAccess: Int = 0
    var simName: String = ""
    var simOwner: UUID = UUID.randomUUID()
    var isEstateManager: Boolean = false
    var waterHeight: Float = 0f
    var billableFactor: Float = 0f
    var cacheId: UUID = UUID.randomUUID()
    var terrainBase0: UUID = UUID.randomUUID()
    var terrainBase1: UUID = UUID.randomUUID()
    var terrainBase2: UUID = UUID.randomUUID()
    var terrainBase3: UUID = UUID.randomUUID()
    var terrainDetail0: UUID = UUID.randomUUID()
    var terrainDetail1: UUID = UUID.randomUUID()
    var terrainDetail2: UUID = UUID.randomUUID()
    var terrainDetail3: UUID = UUID.randomUUID()
    var terrainStartHeight00: Float = 0f
    var terrainStartHeight01: Float = 0f
    var terrainStartHeight10: Float = 0f
    var terrainStartHeight11: Float = 0f
    var terrainHeightRange00: Float = 0f
    var terrainHeightRange01: Float = 0f
    var terrainHeightRange10: Float = 0f
    var terrainHeightRange11: Float = 0f
    var regionId: UUID = UUID.randomUUID()
    var cpuClassId: Int = 0
    var cpuRatio: Int = 0
    var coloName: String = ""
    var productSku: String = ""
    var productName: String = ""
    val extendedRegionInfo: MutableList<RegionInfoExtended> = mutableListOf()

    data class RegionInfoExtended(
        var regionFlagsExtended: Long = 0L,
        var regionProtocols: Long = 0L,
    )

    private val charset: Charset = Charsets.UTF_8

    init {
        zeroCoded = true
    }

    override fun packPayload(buffer: ByteBuffer) {
        packInt(buffer, regionFlags)
        require(simAccess in 0..0xFF) { "simAccess out of range ($simAccess)" }
        packByte(buffer, simAccess)
        packVariable(buffer, simName.toByteArray(charset), 1)
        packUUID(buffer, simOwner)
        packBoolean(buffer, isEstateManager)
        packFloat(buffer, waterHeight)
        packFloat(buffer, billableFactor)
        packUUID(buffer, cacheId)
        packUUID(buffer, terrainBase0)
        packUUID(buffer, terrainBase1)
        packUUID(buffer, terrainBase2)
        packUUID(buffer, terrainBase3)
        packUUID(buffer, terrainDetail0)
        packUUID(buffer, terrainDetail1)
        packUUID(buffer, terrainDetail2)
        packUUID(buffer, terrainDetail3)
        packFloat(buffer, terrainStartHeight00)
        packFloat(buffer, terrainStartHeight01)
        packFloat(buffer, terrainStartHeight10)
        packFloat(buffer, terrainStartHeight11)
        packFloat(buffer, terrainHeightRange00)
        packFloat(buffer, terrainHeightRange01)
        packFloat(buffer, terrainHeightRange10)
        packFloat(buffer, terrainHeightRange11)
        packUUID(buffer, regionId)
        packInt(buffer, cpuClassId)
        packInt(buffer, cpuRatio)
        packVariable(buffer, coloName.toByteArray(charset), 1)
        packVariable(buffer, productSku.toByteArray(charset), 1)
        packVariable(buffer, productName.toByteArray(charset), 1)
        require(extendedRegionInfo.size <= 0xFF) { "Extended region info list too large (${extendedRegionInfo.size})" }
        packByte(buffer, extendedRegionInfo.size)
        extendedRegionInfo.forEach { entry ->
            packLong(buffer, entry.regionFlagsExtended)
            packLong(buffer, entry.regionProtocols)
        }
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        regionFlags = unpackInt(buffer)
        simAccess = unpackByte(buffer)
        simName = String(unpackVariable(buffer, 1), charset)
        simOwner = unpackUUID(buffer)
        isEstateManager = unpackBoolean(buffer)
        waterHeight = unpackFloat(buffer)
        billableFactor = unpackFloat(buffer)
        cacheId = unpackUUID(buffer)
        terrainBase0 = unpackUUID(buffer)
        terrainBase1 = unpackUUID(buffer)
        terrainBase2 = unpackUUID(buffer)
        terrainBase3 = unpackUUID(buffer)
        terrainDetail0 = unpackUUID(buffer)
        terrainDetail1 = unpackUUID(buffer)
        terrainDetail2 = unpackUUID(buffer)
        terrainDetail3 = unpackUUID(buffer)
        terrainStartHeight00 = unpackFloat(buffer)
        terrainStartHeight01 = unpackFloat(buffer)
        terrainStartHeight10 = unpackFloat(buffer)
        terrainStartHeight11 = unpackFloat(buffer)
        terrainHeightRange00 = unpackFloat(buffer)
        terrainHeightRange01 = unpackFloat(buffer)
        terrainHeightRange10 = unpackFloat(buffer)
        terrainHeightRange11 = unpackFloat(buffer)
        regionId = unpackUUID(buffer)
        cpuClassId = unpackInt(buffer)
        cpuRatio = unpackInt(buffer)
        coloName = String(unpackVariable(buffer, 1), charset)
        productSku = String(unpackVariable(buffer, 1), charset)
        productName = String(unpackVariable(buffer, 1), charset)
        val extendedCount = unpackByte(buffer)
        extendedRegionInfo.clear()
        repeat(extendedCount) {
            extendedRegionInfo += RegionInfoExtended(
                regionFlagsExtended = unpackLong(buffer),
                regionProtocols = unpackLong(buffer),
            )
        }
    }

    override fun getMessageID(): Int = SLMessageFactory.MessageIDs.REGION_HANDSHAKE

    override fun getMessageName(): String = "RegionHandshake"
}

/**
 * Region handshake reply message
 */
class RegionHandshakeReplyMessage : SLMessage() {
    var agentId: UUID = UUID.randomUUID()
    var sessionId: UUID = UUID.randomUUID()
    var flags: Int = 0

    init {
        zeroCoded = true
    }

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, sessionId)
        packInt(buffer, flags)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        sessionId = unpackUUID(buffer)
        flags = unpackInt(buffer)
    }

    override fun getMessageID(): Int = SLMessageFactory.MessageIDs.REGION_HANDSHAKE_REPLY

    override fun getMessageName(): String = "RegionHandshakeReply"
}

/**
 * Crossed region message (0x07)
 */
class CrossedRegionMessage : SLMessage() {
    var agentId: UUID = UUID.randomUUID()
    var sessionId: UUID = UUID.randomUUID()
    var simAddress: Inet4Address? = null
    var simPort: Int = 0
    var regionHandle: Long = 0L
    var seedCapability: ByteArray = ByteArray(0)
    var position: LLVector3 = LLVector3()
    var lookAt: LLVector3 = LLVector3()

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, sessionId)
        packIPAddress(buffer, simAddress)
        require(simPort in 0..0xFFFF) { "simPort out of range ($simPort)" }
        packUInt16(buffer, simPort)
        packLong(buffer, regionHandle)
        packVariable(buffer, seedCapability, 2)
        position.pack(buffer)
        lookAt.pack(buffer)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        sessionId = unpackUUID(buffer)
        simAddress = unpackIPAddress(buffer)
        simPort = unpackUInt16(buffer)
        regionHandle = unpackLong(buffer)
        seedCapability = unpackVariable(buffer, 2)
        position = LLVector3.unpack(buffer)
        lookAt = LLVector3.unpack(buffer)
    }

    override fun getMessageID(): Int = SLMessageFactory.MessageIDs.CROSSED_REGION

    override fun getMessageName(): String = "CrossedRegion"
}
