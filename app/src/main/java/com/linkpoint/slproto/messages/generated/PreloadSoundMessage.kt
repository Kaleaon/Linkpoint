package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class PreloadSoundMessage : SLMessage() {
    val dataBlock: MutableList<DataBlock> = mutableListOf()

    data class DataBlock(
        var objectId: UUID = UUID(0L, 0L),
        var ownerId: UUID = UUID(0L, 0L),
        var soundId: UUID = UUID(0L, 0L)
    )


    override fun packPayload(buffer: ByteBuffer) {
        require(dataBlock.size <= 0xFF) { "DataBlock size exceeds 255 (" + dataBlock.size + ")" }
        packByte(buffer, dataBlock.size)
        dataBlock.forEach { entry ->
            packUUID(buffer, entry.objectId)
            packUUID(buffer, entry.ownerId)
            packUUID(buffer, entry.soundId)
        }
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        run {
            val count = unpackByte(buffer)
            dataBlock.clear()
            repeat(count) {
                val entry = DataBlock()
                entry.objectId = unpackUUID(buffer)
                entry.ownerId = unpackUUID(buffer)
                entry.soundId = unpackUUID(buffer)
                dataBlock += entry
            }
        }
    }

    override fun getMessageID(): Int = 0x0000FF0F

    override fun getMessageName(): String = "PreloadSound"
}
