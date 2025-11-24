package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class ParcelAccessListUpdateMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var sessionId: UUID = UUID(0L, 0L)
    var flags: Int = 0
    var localId: Int = 0
    var transactionId: UUID = UUID(0L, 0L)
    var sequenceId: Int = 0
    var sections: Int = 0
    val list: MutableList<ListBlock> = mutableListOf()

    data class ListBlock(
        var id: UUID = UUID(0L, 0L),
        var time: Int = 0,
        var flags: Int = 0
    )


    init {
        zeroCoded = true
    }

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, sessionId)
        packInt(buffer, flags)
        packInt(buffer, localId)
        packUUID(buffer, transactionId)
        packInt(buffer, sequenceId)
        packInt(buffer, sections)
        require(list.size <= 0xFF) { "List size exceeds 255 (" + list.size + ")" }
        packByte(buffer, list.size)
        list.forEach { entry ->
            packUUID(buffer, entry.id)
            packInt(buffer, entry.time)
            packInt(buffer, entry.flags)
        }
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        sessionId = unpackUUID(buffer)
        flags = unpackInt(buffer)
        localId = unpackInt(buffer)
        transactionId = unpackUUID(buffer)
        sequenceId = unpackInt(buffer)
        sections = unpackInt(buffer)
        run {
            val count = unpackByte(buffer)
            list.clear()
            repeat(count) {
                val entry = ListBlock()
                entry.id = unpackUUID(buffer)
                entry.time = unpackInt(buffer)
                entry.flags = unpackInt(buffer)
                list += entry
            }
        }
    }

    override fun getMessageID(): Int = 0xFFFF00D9.toInt()

    override fun getMessageName(): String = "ParcelAccessListUpdate"
}
