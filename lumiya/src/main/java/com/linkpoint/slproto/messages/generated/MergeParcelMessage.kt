package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class MergeParcelMessage : SLMessage() {
    var masterId: UUID = UUID(0L, 0L)
    val slaveParcelData: MutableList<SlaveParcelDataBlock> = mutableListOf()

    data class SlaveParcelDataBlock(
        var slaveId: UUID = UUID(0L, 0L)
    )


    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, masterId)
        require(slaveParcelData.size <= 0xFF) { "SlaveParcelData size exceeds 255 (" + slaveParcelData.size + ")" }
        packByte(buffer, slaveParcelData.size)
        slaveParcelData.forEach { entry ->
            packUUID(buffer, entry.slaveId)
        }
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        masterId = unpackUUID(buffer)
        run {
            val count = unpackByte(buffer)
            slaveParcelData.clear()
            repeat(count) {
                val entry = SlaveParcelDataBlock()
                entry.slaveId = unpackUUID(buffer)
                slaveParcelData += entry
            }
        }
    }

    override fun getMessageID(): Int = 0xFFFF00DF

    override fun getMessageName(): String = "MergeParcel"
}
