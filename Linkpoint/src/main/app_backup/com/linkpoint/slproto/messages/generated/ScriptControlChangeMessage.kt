package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer

class ScriptControlChangeMessage : SLMessage() {
    val data: MutableList<DataBlock> = mutableListOf()

    data class DataBlock(
        var takeControls: Boolean = false,
        var controls: Int = 0,
        var passToAgent: Boolean = false
    )


    override fun packPayload(buffer: ByteBuffer) {
        require(data.size <= 0xFF) { "Data size exceeds 255 (" + data.size + ")" }
        packByte(buffer, data.size)
        data.forEach { entry ->
            packBoolean(buffer, entry.takeControls)
            packInt(buffer, entry.controls)
            packBoolean(buffer, entry.passToAgent)
        }
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        run {
            val count = unpackByte(buffer)
            data.clear()
            repeat(count) {
                val entry = DataBlock()
                entry.takeControls = unpackBoolean(buffer)
                entry.controls = unpackInt(buffer)
                entry.passToAgent = unpackBoolean(buffer)
                data += entry
            }
        }
    }

    override fun getMessageID(): Int = 0xFFFF00BD.toInt()

    override fun getMessageName(): String = "ScriptControlChange"
}
