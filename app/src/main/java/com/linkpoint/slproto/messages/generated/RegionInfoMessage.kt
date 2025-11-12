package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class RegionInfoMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var sessionId: UUID = UUID(0L, 0L)
    var simName: ByteArray = ByteArray(0)
    var estateId: Int = 0
    var parentEstateId: Int = 0
    var regionFlags: Int = 0
    var simAccess: Int = 0
    var maxAgents: Int = 0
    var billableFactor: Float = 0f
    var objectBonusFactor: Float = 0f
    var waterHeight: Float = 0f
    var terrainRaiseLimit: Float = 0f
    var terrainLowerLimit: Float = 0f
    var pricePerMeter: Int = 0
    var redirectGridX: Int = 0
    var redirectGridY: Int = 0
    var useEstateSun: Boolean = false
    var sunHour: Float = 0f
    var productSku: ByteArray = ByteArray(0)
    var productName: ByteArray = ByteArray(0)
    var maxAgents32: Int = 0
    var hardMaxAgents: Int = 0
    var hardMaxObjects: Int = 0
    val regionInfo3: MutableList<RegionInfo3Block> = mutableListOf()
    val regionInfo5: MutableList<RegionInfo5Block> = mutableListOf()
    val combatSettings: MutableList<CombatSettingsBlock> = mutableListOf()

    data class RegionInfo3Block(
        var regionFlagsExtended: Long = 0L
    )
    data class RegionInfo5Block(
        var chatWhisperRange: Float = 0f,
        var chatNormalRange: Float = 0f,
        var chatShoutRange: Float = 0f,
        var chatWhisperOffset: Float = 0f,
        var chatNormalOffset: Float = 0f,
        var chatShoutOffset: Float = 0f,
        var chatFlags: Int = 0
    )
    data class CombatSettingsBlock(
        var combatFlags: Int = 0,
        var onDeath: Int = 0,
        var damageThrottle: Float = 0f,
        var regenerationRate: Float = 0f,
        var invulnerabilyTime: Float = 0f,
        var damageLimit: Float = 0f
    )


    init {
        zeroCoded = true
    }

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, sessionId)
        packVariable(buffer, simName, 1)
        packInt(buffer, estateId)
        packInt(buffer, parentEstateId)
        packInt(buffer, regionFlags)
        packByte(buffer, simAccess)
        packByte(buffer, maxAgents)
        packFloat(buffer, billableFactor)
        packFloat(buffer, objectBonusFactor)
        packFloat(buffer, waterHeight)
        packFloat(buffer, terrainRaiseLimit)
        packFloat(buffer, terrainLowerLimit)
        packInt(buffer, pricePerMeter)
        packInt(buffer, redirectGridX)
        packInt(buffer, redirectGridY)
        packBoolean(buffer, useEstateSun)
        packFloat(buffer, sunHour)
        packVariable(buffer, productSku, 1)
        packVariable(buffer, productName, 1)
        packInt(buffer, maxAgents32)
        packInt(buffer, hardMaxAgents)
        packInt(buffer, hardMaxObjects)
        require(regionInfo3.size <= 0xFF) { "RegionInfo3 size exceeds 255 (" + regionInfo3.size + ")" }
        packByte(buffer, regionInfo3.size)
        regionInfo3.forEach { entry ->
            packLong(buffer, entry.regionFlagsExtended)
        }
        require(regionInfo5.size <= 0xFF) { "RegionInfo5 size exceeds 255 (" + regionInfo5.size + ")" }
        packByte(buffer, regionInfo5.size)
        regionInfo5.forEach { entry ->
            packFloat(buffer, entry.chatWhisperRange)
            packFloat(buffer, entry.chatNormalRange)
            packFloat(buffer, entry.chatShoutRange)
            packFloat(buffer, entry.chatWhisperOffset)
            packFloat(buffer, entry.chatNormalOffset)
            packFloat(buffer, entry.chatShoutOffset)
            packInt(buffer, entry.chatFlags)
        }
        require(combatSettings.size <= 0xFF) { "CombatSettings size exceeds 255 (" + combatSettings.size + ")" }
        packByte(buffer, combatSettings.size)
        combatSettings.forEach { entry ->
            packInt(buffer, entry.combatFlags)
            packByte(buffer, entry.onDeath)
            packFloat(buffer, entry.damageThrottle)
            packFloat(buffer, entry.regenerationRate)
            packFloat(buffer, entry.invulnerabilyTime)
            packFloat(buffer, entry.damageLimit)
        }
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        sessionId = unpackUUID(buffer)
        simName = unpackVariable(buffer, 1)
        estateId = unpackInt(buffer)
        parentEstateId = unpackInt(buffer)
        regionFlags = unpackInt(buffer)
        simAccess = unpackByte(buffer)
        maxAgents = unpackByte(buffer)
        billableFactor = unpackFloat(buffer)
        objectBonusFactor = unpackFloat(buffer)
        waterHeight = unpackFloat(buffer)
        terrainRaiseLimit = unpackFloat(buffer)
        terrainLowerLimit = unpackFloat(buffer)
        pricePerMeter = unpackInt(buffer)
        redirectGridX = unpackInt(buffer)
        redirectGridY = unpackInt(buffer)
        useEstateSun = unpackBoolean(buffer)
        sunHour = unpackFloat(buffer)
        productSku = unpackVariable(buffer, 1)
        productName = unpackVariable(buffer, 1)
        maxAgents32 = unpackInt(buffer)
        hardMaxAgents = unpackInt(buffer)
        hardMaxObjects = unpackInt(buffer)
        run {
            val count = unpackByte(buffer)
            regionInfo3.clear()
            repeat(count) {
                val entry = RegionInfo3Block()
                entry.regionFlagsExtended = unpackLong(buffer)
                regionInfo3 += entry
            }
        }
        run {
            val count = unpackByte(buffer)
            regionInfo5.clear()
            repeat(count) {
                val entry = RegionInfo5Block()
                entry.chatWhisperRange = unpackFloat(buffer)
                entry.chatNormalRange = unpackFloat(buffer)
                entry.chatShoutRange = unpackFloat(buffer)
                entry.chatWhisperOffset = unpackFloat(buffer)
                entry.chatNormalOffset = unpackFloat(buffer)
                entry.chatShoutOffset = unpackFloat(buffer)
                entry.chatFlags = unpackInt(buffer)
                regionInfo5 += entry
            }
        }
        run {
            val count = unpackByte(buffer)
            combatSettings.clear()
            repeat(count) {
                val entry = CombatSettingsBlock()
                entry.combatFlags = unpackInt(buffer)
                entry.onDeath = unpackByte(buffer)
                entry.damageThrottle = unpackFloat(buffer)
                entry.regenerationRate = unpackFloat(buffer)
                entry.invulnerabilyTime = unpackFloat(buffer)
                entry.damageLimit = unpackFloat(buffer)
                combatSettings += entry
            }
        }
    }

    override fun getMessageID(): Int = 0xFFFF008E

    override fun getMessageName(): String = "RegionInfo"
}
