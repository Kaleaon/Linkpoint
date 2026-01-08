package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer

class NameMessage : SLMessage() {
    val description: MutableList<DescriptionBlock> = mutableListOf()

    class DescriptionBlock

    override fun packPayload(buffer: ByteBuffer) {
        require(description.size <= 0xFF) { "Description size exceeds 255 (" + description.size + ")" }
        packByte(buffer, description.size)
        description.forEach { entry ->
        }
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        run {
            val count = unpackByte(buffer)
            description.clear()
            repeat(count) {
                val entry = DescriptionBlock()
                description += entry
            }
        }
    }

    override fun getMessageID(): Int = 0x00000001

    override fun getMessageName(): String = "Name"
}
