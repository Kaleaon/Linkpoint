package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.net.Inet4Address
import java.nio.ByteBuffer
import java.util.UUID

class ViewerStatsMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var sessionId: UUID = UUID(0L, 0L)
    var ip: Inet4Address? = null
    var startTime: Int = 0
    var runTime: Float = 0f
    var simFps: Float = 0f
    var fps: Float = 0f
    var agentsInView: Int = 0
    var ping: Float = 0f
    var metersTraveled: Double = 0.0
    var regionsVisited: Int = 0
    var sysRam: Int = 0
    var sysOs: ByteArray = ByteArray(0)
    var sysCpu: ByteArray = ByteArray(0)
    var sysGpu: ByteArray = ByteArray(0)
    var world: Int = 0
    var objects: Int = 0
    var textures: Int = 0
    val netStats: MutableList<NetStatsBlock> = MutableList(2) { NetStatsBlock() }
    var sendPacket: Int = 0
    var dropped: Int = 0
    var resent: Int = 0
    var failedResends: Int = 0
    var offCircuit: Int = 0
    var invalid: Int = 0
    val miscStats: MutableList<MiscStatsBlock> = mutableListOf()

    data class NetStatsBlock(
        var bytes: Int = 0,
        var packets: Int = 0,
        var compressed: Int = 0,
        var savings: Int = 0
    )
    data class MiscStatsBlock(
        var type: Int = 0,
        var value: Double = 0.0
    )


    init {
        zeroCoded = true
    }

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, sessionId)
        packIPAddress(buffer, ip)
        packInt(buffer, startTime)
        packFloat(buffer, runTime)
        packFloat(buffer, simFps)
        packFloat(buffer, fps)
        packByte(buffer, agentsInView)
        packFloat(buffer, ping)
        packDouble(buffer, metersTraveled)
        packInt(buffer, regionsVisited)
        packInt(buffer, sysRam)
        packVariable(buffer, sysOs, 1)
        packVariable(buffer, sysCpu, 1)
        packVariable(buffer, sysGpu, 1)
        packInt(buffer, world)
        packInt(buffer, objects)
        packInt(buffer, textures)
        require(netStats.size == 2) { "NetStats must contain exactly 2 entries" }
        netStats.forEach { entry ->
            packInt(buffer, entry.bytes)
            packInt(buffer, entry.packets)
            packInt(buffer, entry.compressed)
            packInt(buffer, entry.savings)
        }
        packInt(buffer, sendPacket)
        packInt(buffer, dropped)
        packInt(buffer, resent)
        packInt(buffer, failedResends)
        packInt(buffer, offCircuit)
        packInt(buffer, invalid)
        require(miscStats.size <= 0xFF) { "MiscStats size exceeds 255 (" + miscStats.size + ")" }
        packByte(buffer, miscStats.size)
        miscStats.forEach { entry ->
            packInt(buffer, entry.type)
            packDouble(buffer, entry.value)
        }
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        sessionId = unpackUUID(buffer)
        ip = unpackIPAddress(buffer)
        startTime = unpackInt(buffer)
        runTime = unpackFloat(buffer)
        simFps = unpackFloat(buffer)
        fps = unpackFloat(buffer)
        agentsInView = unpackByte(buffer)
        ping = unpackFloat(buffer)
        metersTraveled = unpackDouble(buffer)
        regionsVisited = unpackInt(buffer)
        sysRam = unpackInt(buffer)
        sysOs = unpackVariable(buffer, 1)
        sysCpu = unpackVariable(buffer, 1)
        sysGpu = unpackVariable(buffer, 1)
        world = unpackInt(buffer)
        objects = unpackInt(buffer)
        textures = unpackInt(buffer)
        netStats.clear()
        repeat(2) {
            val entry = NetStatsBlock()
            entry.bytes = unpackInt(buffer)
            entry.packets = unpackInt(buffer)
            entry.compressed = unpackInt(buffer)
            entry.savings = unpackInt(buffer)
            netStats += entry
        }
        sendPacket = unpackInt(buffer)
        dropped = unpackInt(buffer)
        resent = unpackInt(buffer)
        failedResends = unpackInt(buffer)
        offCircuit = unpackInt(buffer)
        invalid = unpackInt(buffer)
        run {
            val count = unpackByte(buffer)
            miscStats.clear()
            repeat(count) {
                val entry = MiscStatsBlock()
                entry.type = unpackInt(buffer)
                entry.value = unpackDouble(buffer)
                miscStats += entry
            }
        }
    }

    override fun getMessageID(): Int = 0xFFFF0083

    override fun getMessageName(): String = "ViewerStats"
}
