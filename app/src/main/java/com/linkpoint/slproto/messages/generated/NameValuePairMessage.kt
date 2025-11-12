package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class NameValuePairMessage : SLMessage() {
    var id: UUID = UUID(0L, 0L)
    val nameValueData: MutableList<NameValueDataBlock> = mutableListOf()

    data class NameValueDataBlock(
        var nvPair: ByteArray = ByteArray(0)
    )


    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, id)
        require(nameValueData.size <= 0xFF) { "NameValueData size exceeds 255 (" + nameValueData.size + ")" }
        packByte(buffer, nameValueData.size)
        nameValueData.forEach { entry ->
            packVariable(buffer, entry.nvPair, 2)
        }
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        id = unpackUUID(buffer)
        run {
            val count = unpackByte(buffer)
            nameValueData.clear()
            repeat(count) {
                val entry = NameValueDataBlock()
                entry.nvPair = unpackVariable(buffer, 2)
                nameValueData += entry
            }
        }
    }

    override fun getMessageID(): Int = 0xFFFF0149

    override fun getMessageName(): String = "NameValuePair"
}
