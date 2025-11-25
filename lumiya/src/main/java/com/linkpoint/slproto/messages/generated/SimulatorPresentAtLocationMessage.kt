package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.types.LLVector3
import java.net.Inet4Address
import java.nio.ByteBuffer
import java.util.UUID

class SimulatorPresentAtLocationMessage : SLMessage() {
    var port: Int = 0
    var simulatorIp: Inet4Address? = null
    var gridX: Int = 0
    var gridY: Int = 0
    val neighborBlock: MutableList<NeighborBlock> = MutableList(4) { NeighborBlock() }
    var simName: ByteArray = ByteArray(0)
    var simAccess: Int = 0
    var regionFlags: Int = 0
    var regionId: UUID = UUID(0L, 0L)
    var estateId: Int = 0
    var parentEstateId: Int = 0
    val telehubBlock: MutableList<TelehubBlock> = mutableListOf()

    data class NeighborBlock(
        var ip: Inet4Address? = null,
        var port: Int = 0
    )
    data class TelehubBlock(
        var hasTelehub: Boolean = false,
        var telehubPos: LLVector3 = LLVector3()
    )


    override fun packPayload(buffer: ByteBuffer) {
        packUInt16(buffer, port)
        packIPAddress(buffer, simulatorIp)
        packInt(buffer, gridX)
        packInt(buffer, gridY)
        require(neighborBlock.size == 4) { "NeighborBlock must contain exactly 4 entries" }
        neighborBlock.forEach { entry ->
            packIPAddress(buffer, entry.ip)
            packUInt16(buffer, entry.port)
        }
        packVariable(buffer, simName, 1)
        packByte(buffer, simAccess)
        packInt(buffer, regionFlags)
        packUUID(buffer, regionId)
        packInt(buffer, estateId)
        packInt(buffer, parentEstateId)
        require(telehubBlock.size <= 0xFF) { "TelehubBlock size exceeds 255 (" + telehubBlock.size + ")" }
        packByte(buffer, telehubBlock.size)
        telehubBlock.forEach { entry ->
            packBoolean(buffer, entry.hasTelehub)
            entry.telehubPos.pack(buffer)
        }
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        port = unpackUInt16(buffer)
        simulatorIp = unpackIPAddress(buffer)
        gridX = unpackInt(buffer)
        gridY = unpackInt(buffer)
        neighborBlock.clear()
        repeat(4) {
            val entry = NeighborBlock()
            entry.ip = unpackIPAddress(buffer)
            entry.port = unpackUInt16(buffer)
            neighborBlock += entry
        }
        simName = unpackVariable(buffer, 1)
        simAccess = unpackByte(buffer)
        regionFlags = unpackInt(buffer)
        regionId = unpackUUID(buffer)
        estateId = unpackInt(buffer)
        parentEstateId = unpackInt(buffer)
        run {
            val count = unpackByte(buffer)
            telehubBlock.clear()
            repeat(count) {
                val entry = TelehubBlock()
                entry.hasTelehub = unpackBoolean(buffer)
                entry.telehubPos = LLVector3.unpack(buffer)
                telehubBlock += entry
            }
        }
    }

    override fun getMessageID(): Int = 0xFFFF000B.toInt()

    override fun getMessageName(): String = "SimulatorPresentAtLocation"
}
