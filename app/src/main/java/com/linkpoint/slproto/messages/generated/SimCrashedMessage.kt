package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class SimCrashedMessage : SLMessage() {
    var regionX: Int = 0
    var regionY: Int = 0
    val users: MutableList<UsersBlock> = mutableListOf()

    data class UsersBlock(
        var agentId: UUID = UUID(0L, 0L)
    )


    override fun packPayload(buffer: ByteBuffer) {
        packInt(buffer, regionX)
        packInt(buffer, regionY)
        require(users.size <= 0xFF) { "Users size exceeds 255 (" + users.size + ")" }
        packByte(buffer, users.size)
        users.forEach { entry ->
            packUUID(buffer, entry.agentId)
        }
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        regionX = unpackInt(buffer)
        regionY = unpackInt(buffer)
        run {
            val count = unpackByte(buffer)
            users.clear()
            repeat(count) {
                val entry = UsersBlock()
                entry.agentId = unpackUUID(buffer)
                users += entry
            }
        }
    }

    override fun getMessageID(): Int = 0xFFFF0148

    override fun getMessageName(): String = "SimCrashed"
}
