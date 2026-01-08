package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class UuidNameRequestMessage : SLMessage() {
    val uuidNameBlock: MutableList<UuidNameBlock> = mutableListOf()

    data class UuidNameBlock(
        var id: UUID = UUID(0L, 0L)
    )


    override fun packPayload(buffer: ByteBuffer) {
        require(uuidNameBlock.size <= 0xFF) { "UUIDNameBlock size exceeds 255 (" + uuidNameBlock.size + ")" }
        packByte(buffer, uuidNameBlock.size)
        uuidNameBlock.forEach { entry ->
            packUUID(buffer, entry.id)
        }
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        run {
            val count = unpackByte(buffer)
            uuidNameBlock.clear()
            repeat(count) {
                val entry = UuidNameBlock()
                entry.id = unpackUUID(buffer)
                uuidNameBlock += entry
            }
        }
    }

    override fun getMessageID(): Int = 0xFFFF00EB.toInt()

    override fun getMessageName(): String = "UUIDNameRequest"
}
