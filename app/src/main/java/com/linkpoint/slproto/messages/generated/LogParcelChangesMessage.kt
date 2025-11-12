package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class LogParcelChangesMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var regionHandle: Long = 0L
    val parcelData: MutableList<ParcelDataBlock> = mutableListOf()

    data class ParcelDataBlock(
        var parcelId: UUID = UUID(0L, 0L),
        var ownerId: UUID = UUID(0L, 0L),
        var isOwnerGroup: Boolean = false,
        var actualArea: Int = 0,
        var action: Int = 0,
        var transactionId: UUID = UUID(0L, 0L)
    )


    init {
        zeroCoded = true
    }

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packLong(buffer, regionHandle)
        require(parcelData.size <= 0xFF) { "ParcelData size exceeds 255 (" + parcelData.size + ")" }
        packByte(buffer, parcelData.size)
        parcelData.forEach { entry ->
            packUUID(buffer, entry.parcelId)
            packUUID(buffer, entry.ownerId)
            packBoolean(buffer, entry.isOwnerGroup)
            packInt(buffer, entry.actualArea)
            packByte(buffer, entry.action)
            packUUID(buffer, entry.transactionId)
        }
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        regionHandle = unpackLong(buffer)
        run {
            val count = unpackByte(buffer)
            parcelData.clear()
            repeat(count) {
                val entry = ParcelDataBlock()
                entry.parcelId = unpackUUID(buffer)
                entry.ownerId = unpackUUID(buffer)
                entry.isOwnerGroup = unpackBoolean(buffer)
                entry.actualArea = unpackInt(buffer)
                entry.action = unpackByte(buffer)
                entry.transactionId = unpackUUID(buffer)
                parcelData += entry
            }
        }
    }

    override fun getMessageID(): Int = 0xFFFF00E0

    override fun getMessageName(): String = "LogParcelChanges"
}
