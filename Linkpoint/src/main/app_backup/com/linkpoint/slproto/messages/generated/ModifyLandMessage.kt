package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class ModifyLandMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var sessionId: UUID = UUID(0L, 0L)
    var action: Int = 0
    var brushSize: Int = 0
    var seconds: Float = 0f
    var height: Float = 0f
    val parcelData: MutableList<ParcelDataBlock> = mutableListOf()
    val modifyBlockExtended: MutableList<ModifyBlockExtendedBlock> = mutableListOf()

    data class ParcelDataBlock(
        var localId: Int = 0,
        var west: Float = 0f,
        var south: Float = 0f,
        var east: Float = 0f,
        var north: Float = 0f
    )
    data class ModifyBlockExtendedBlock(
        var brushSize: Float = 0f
    )


    init {
        zeroCoded = true
    }

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, sessionId)
        packByte(buffer, action)
        packByte(buffer, brushSize)
        packFloat(buffer, seconds)
        packFloat(buffer, height)
        require(parcelData.size <= 0xFF) { "ParcelData size exceeds 255 (" + parcelData.size + ")" }
        packByte(buffer, parcelData.size)
        parcelData.forEach { entry ->
            packInt(buffer, entry.localId)
            packFloat(buffer, entry.west)
            packFloat(buffer, entry.south)
            packFloat(buffer, entry.east)
            packFloat(buffer, entry.north)
        }
        require(modifyBlockExtended.size <= 0xFF) { "ModifyBlockExtended size exceeds 255 (" + modifyBlockExtended.size + ")" }
        packByte(buffer, modifyBlockExtended.size)
        modifyBlockExtended.forEach { entry ->
            packFloat(buffer, entry.brushSize)
        }
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        sessionId = unpackUUID(buffer)
        action = unpackByte(buffer)
        brushSize = unpackByte(buffer)
        seconds = unpackFloat(buffer)
        height = unpackFloat(buffer)
        run {
            val count = unpackByte(buffer)
            parcelData.clear()
            repeat(count) {
                val entry = ParcelDataBlock()
                entry.localId = unpackInt(buffer)
                entry.west = unpackFloat(buffer)
                entry.south = unpackFloat(buffer)
                entry.east = unpackFloat(buffer)
                entry.north = unpackFloat(buffer)
                parcelData += entry
            }
        }
        run {
            val count = unpackByte(buffer)
            modifyBlockExtended.clear()
            repeat(count) {
                val entry = ModifyBlockExtendedBlock()
                entry.brushSize = unpackFloat(buffer)
                modifyBlockExtended += entry
            }
        }
    }

    override fun getMessageID(): Int = 0xFFFF007C.toInt()

    override fun getMessageName(): String = "ModifyLand"
}
