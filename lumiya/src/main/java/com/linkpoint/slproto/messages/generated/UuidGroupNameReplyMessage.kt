package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class UuidGroupNameReplyMessage : SLMessage() {
    val uuidNameBlock: MutableList<UuidNameBlock> = mutableListOf()

    data class UuidNameBlock(
        var id: UUID = UUID(0L, 0L),
        var groupName: ByteArray = ByteArray(0)
    )


    override fun packPayload(buffer: ByteBuffer) {
        require(uuidNameBlock.size <= 0xFF) { "UUIDNameBlock size exceeds 255 (" + uuidNameBlock.size + ")" }
        packByte(buffer, uuidNameBlock.size)
        uuidNameBlock.forEach { entry ->
            packUUID(buffer, entry.id)
            packVariable(buffer, entry.groupName, 1)
        }
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        run {
            val count = unpackByte(buffer)
            uuidNameBlock.clear()
            repeat(count) {
                val entry = UuidNameBlock()
                entry.id = unpackUUID(buffer)
                entry.groupName = unpackVariable(buffer, 1)
                uuidNameBlock += entry
            }
        }
    }

    override fun getMessageID(): Int = 0xFFFF00EE.toInt()

    override fun getMessageName(): String = "UUIDGroupNameReply"
}
