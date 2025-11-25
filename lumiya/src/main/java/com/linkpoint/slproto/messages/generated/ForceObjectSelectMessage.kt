package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer

class ForceObjectSelectMessage : SLMessage() {
    var resetList: Boolean = false
    val data: MutableList<DataBlock> = mutableListOf()

    data class DataBlock(
        var localId: Int = 0
    )


    override fun packPayload(buffer: ByteBuffer) {
        packBoolean(buffer, resetList)
        require(data.size <= 0xFF) { "Data size exceeds 255 (" + data.size + ")" }
        packByte(buffer, data.size)
        data.forEach { entry ->
            packInt(buffer, entry.localId)
        }
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        resetList = unpackBoolean(buffer)
        run {
            val count = unpackByte(buffer)
            data.clear()
            repeat(count) {
                val entry = DataBlock()
                entry.localId = unpackInt(buffer)
                data += entry
            }
        }
    }

    override fun getMessageID(): Int = 0xFFFF00CD.toInt()

    override fun getMessageName(): String = "ForceObjectSelect"
}
