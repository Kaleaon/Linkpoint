package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class SystemMessage : SLMessage() {
    var method: ByteArray = ByteArray(0)
    var invoice: UUID = UUID(0L, 0L)
    var digest: ByteArray = ByteArray(32)
    val paramList: MutableList<ParamListBlock> = mutableListOf()

    data class ParamListBlock(
        var parameter: ByteArray = ByteArray(0)
    )


    init {
        zeroCoded = true
    }

    override fun packPayload(buffer: ByteBuffer) {
        packVariable(buffer, method, 1)
        packUUID(buffer, invoice)
        packFixed(buffer, digest, 32)
        require(paramList.size <= 0xFF) { "ParamList size exceeds 255 (" + paramList.size + ")" }
        packByte(buffer, paramList.size)
        paramList.forEach { entry ->
            packVariable(buffer, entry.parameter, 1)
        }
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        method = unpackVariable(buffer, 1)
        invoice = unpackUUID(buffer)
        digest = unpackFixed(buffer, 32)
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

    override fun getMessageID(): Int = 0xFFFF0194.toInt()

    override fun getMessageName(): String = "SystemMessage"
}
