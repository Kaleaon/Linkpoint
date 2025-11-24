package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class ParcelRenameMessage : SLMessage() {
    val parcelData: MutableList<ParcelDataBlock> = mutableListOf()

    data class ParcelDataBlock(
        var parcelId: UUID = UUID(0L, 0L),
        var newName: ByteArray = ByteArray(0)
    )


    override fun packPayload(buffer: ByteBuffer) {
        require(parcelData.size <= 0xFF) { "ParcelData size exceeds 255 (" + parcelData.size + ")" }
        packByte(buffer, parcelData.size)
        parcelData.forEach { entry ->
            packUUID(buffer, entry.parcelId)
            packVariable(buffer, entry.newName, 1)
        }
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        run {
            val count = unpackByte(buffer)
            parcelData.clear()
            repeat(count) {
                val entry = ParcelDataBlock()
                entry.parcelId = unpackUUID(buffer)
                entry.newName = unpackVariable(buffer, 1)
                parcelData += entry
            }
        }
    }

    override fun getMessageID(): Int = 0xFFFF0192.toInt()

    override fun getMessageName(): String = "ParcelRename"
}
