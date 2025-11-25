package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class GodlikeMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var sessionId: UUID = UUID(0L, 0L)
    var transactionId: UUID = UUID(0L, 0L)
    var method: ByteArray = ByteArray(0)
    var invoice: UUID = UUID(0L, 0L)
    val paramList: MutableList<ParamListBlock> = mutableListOf()

    data class ParamListBlock(
        var parameter: ByteArray = ByteArray(0)
    )


    init {
        zeroCoded = true
    }

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, sessionId)
        packUUID(buffer, transactionId)
        packVariable(buffer, method, 1)
        packUUID(buffer, invoice)
        require(paramList.size <= 0xFF) { "ParamList size exceeds 255 (" + paramList.size + ")" }
        packByte(buffer, paramList.size)
        paramList.forEach { entry ->
            packVariable(buffer, entry.parameter, 1)
        }
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        sessionId = unpackUUID(buffer)
        transactionId = unpackUUID(buffer)
        method = unpackVariable(buffer, 1)
        invoice = unpackUUID(buffer)
        run {
            val count = unpackByte(buffer)
            paramList.clear()
            repeat(count) {
                val entry = ParamListBlock()
                entry.parameter = unpackVariable(buffer, 1)
                paramList += entry
            }
        }
    }

    override fun getMessageID(): Int = 0xFFFF0103.toInt()

    override fun getMessageName(): String = "GodlikeMessage"
}
