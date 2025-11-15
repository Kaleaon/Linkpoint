package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer

class ScriptDataReplyMessage : SLMessage() {
    val dataBlock: MutableList<DataBlock> = mutableListOf()

    data class DataBlock(
        var hash: Long = 0L,
        var reply: ByteArray = ByteArray(0)
    )


    override fun packPayload(buffer: ByteBuffer) {
        require(dataBlock.size <= 0xFF) { "DataBlock size exceeds 255 (" + dataBlock.size + ")" }
        packByte(buffer, dataBlock.size)
        dataBlock.forEach { entry ->
            packLong(buffer, entry.hash)
            packVariable(buffer, entry.reply, 2)
        }
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        run {
            val count = unpackByte(buffer)
            dataBlock.clear()
            repeat(count) {
                val entry = DataBlock()
                entry.hash = unpackLong(buffer)
                entry.reply = unpackVariable(buffer, 2)
                dataBlock += entry
            }
        }
    }

    override fun getMessageID(): Int = 0xFFFF0152

    override fun getMessageName(): String = "ScriptDataReply"
}
