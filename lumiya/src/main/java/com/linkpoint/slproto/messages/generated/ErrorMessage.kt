package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class ErrorMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var code: Int = 0
    var token: ByteArray = ByteArray(0)
    var id: UUID = UUID(0L, 0L)
    var system: ByteArray = ByteArray(0)
    var message: ByteArray = ByteArray(0)
    var data: ByteArray = ByteArray(0)


    init {
        zeroCoded = true
    }

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packInt(buffer, code)
        packVariable(buffer, token, 1)
        packUUID(buffer, id)
        packVariable(buffer, system, 1)
        packVariable(buffer, message, 2)
        packVariable(buffer, data, 2)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        code = unpackInt(buffer)
        token = unpackVariable(buffer, 1)
        id = unpackUUID(buffer)
        system = unpackVariable(buffer, 1)
        message = unpackVariable(buffer, 2)
        data = unpackVariable(buffer, 2)
    }

    override fun getMessageID(): Int = 0xFFFF01A7.toInt()

    override fun getMessageName(): String = "Error"
}
