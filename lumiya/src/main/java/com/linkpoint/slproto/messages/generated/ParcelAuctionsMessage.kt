package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class ParcelAuctionsMessage : SLMessage() {
    val parcelData: MutableList<ParcelDataBlock> = mutableListOf()

    data class ParcelDataBlock(
        var parcelId: UUID = UUID(0L, 0L),
        var winnerId: UUID = UUID(0L, 0L)
    )


    override fun packPayload(buffer: ByteBuffer) {
        require(parcelData.size <= 0xFF) { "ParcelData size exceeds 255 (" + parcelData.size + ")" }
        packByte(buffer, parcelData.size)
        parcelData.forEach { entry ->
            packUUID(buffer, entry.parcelId)
            packUUID(buffer, entry.winnerId)
        }
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        run {
            val count = unpackByte(buffer)
            parcelData.clear()
            repeat(count) {
                val entry = ParcelDataBlock()
                entry.parcelId = unpackUUID(buffer)
                entry.winnerId = unpackUUID(buffer)
                parcelData += entry
            }
        }
    }

    override fun getMessageID(): Int = 0xFFFF00EA

    override fun getMessageName(): String = "ParcelAuctions"
}
