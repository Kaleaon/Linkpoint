package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class ParcelSelectObjectsMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var sessionId: UUID = UUID(0L, 0L)
    var localId: Int = 0
    var returnType: Int = 0
    val returnIDs: MutableList<ReturnIDsBlock> = mutableListOf()

    data class ReturnIDsBlock(
        var returnId: UUID = UUID(0L, 0L)
    )


    init {
        zeroCoded = true
    }

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, sessionId)
        packInt(buffer, localId)
        packInt(buffer, returnType)
        require(returnIDs.size <= 0xFF) { "ReturnIDs size exceeds 255 (" + returnIDs.size + ")" }
        packByte(buffer, returnIDs.size)
        returnIDs.forEach { entry ->
            packUUID(buffer, entry.returnId)
        }
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        sessionId = unpackUUID(buffer)
        localId = unpackInt(buffer)
        returnType = unpackInt(buffer)
        run {
            val count = unpackByte(buffer)
            returnIDs.clear()
            repeat(count) {
                val entry = ReturnIDsBlock()
                entry.returnId = unpackUUID(buffer)
                returnIDs += entry
            }
        }
    }

    override fun getMessageID(): Int = 0xFFFF00CA

    override fun getMessageName(): String = "ParcelSelectObjects"
}
