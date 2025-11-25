package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class LogTextMessage : SLMessage() {
    val dataBlock: MutableList<DataBlock> = mutableListOf()

    data class DataBlock(
        var fromAgentId: UUID = UUID(0L, 0L),
        var toAgentId: UUID = UUID(0L, 0L),
        var globalX: Double = 0.0,
        var globalY: Double = 0.0,
        var time: Int = 0,
        var message: ByteArray = ByteArray(0)
    )


    init {
        zeroCoded = true
    }

    override fun packPayload(buffer: ByteBuffer) {
        require(dataBlock.size <= 0xFF) { "DataBlock size exceeds 255 (" + dataBlock.size + ")" }
        packByte(buffer, dataBlock.size)
        dataBlock.forEach { entry ->
            packUUID(buffer, entry.fromAgentId)
            packUUID(buffer, entry.toAgentId)
            packDouble(buffer, entry.globalX)
            packDouble(buffer, entry.globalY)
            packInt(buffer, entry.time)
            packVariable(buffer, entry.message, 2)
        }
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        run {
            val count = unpackByte(buffer)
            dataBlock.clear()
            repeat(count) {
                val entry = DataBlock()
                entry.fromAgentId = unpackUUID(buffer)
                entry.toAgentId = unpackUUID(buffer)
                entry.globalX = unpackDouble(buffer)
                entry.globalY = unpackDouble(buffer)
                entry.time = unpackInt(buffer)
                entry.message = unpackVariable(buffer, 2)
                dataBlock += entry
            }
        }
    }

    override fun getMessageID(): Int = 0xFFFF0187.toInt()

    override fun getMessageName(): String = "LogTextMessage"
}
