package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class UuidNameReplyMessage : SLMessage() {
    val uuidNameBlock: MutableList<UuidNameBlock> = mutableListOf()

    data class UuidNameBlock(
        var id: UUID = UUID(0L, 0L),
        var firstName: ByteArray = ByteArray(0),
        var lastName: ByteArray = ByteArray(0)
    )


    override fun packPayload(buffer: ByteBuffer) {
        require(uuidNameBlock.size <= 0xFF) { "UUIDNameBlock size exceeds 255 (" + uuidNameBlock.size + ")" }
        packByte(buffer, uuidNameBlock.size)
        uuidNameBlock.forEach { entry ->
            packUUID(buffer, entry.id)
            packVariable(buffer, entry.firstName, 1)
            packVariable(buffer, entry.lastName, 1)
        }
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        run {
            val count = unpackByte(buffer)
            uuidNameBlock.clear()
            repeat(count) {
                val entry = UuidNameBlock()
                entry.id = unpackUUID(buffer)
                entry.firstName = unpackVariable(buffer, 1)
                entry.lastName = unpackVariable(buffer, 1)
                uuidNameBlock += entry
            }
        }
    }

    override fun getMessageID(): Int = 0xFFFF00EC.toInt()

    override fun getMessageName(): String = "UUIDNameReply"
}
