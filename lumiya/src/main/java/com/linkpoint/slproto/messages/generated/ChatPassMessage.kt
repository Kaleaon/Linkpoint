package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.types.LLVector3
import java.nio.ByteBuffer
import java.util.UUID

class ChatPassMessage : SLMessage() {
    var channel: Int = 0
    var position: LLVector3 = LLVector3()
    var id: UUID = UUID(0L, 0L)
    var ownerId: UUID = UUID(0L, 0L)
    var name: ByteArray = ByteArray(0)
    var sourceType: Int = 0
    var type: Int = 0
    var radius: Float = 0f
    var simAccess: Int = 0
    var message: ByteArray = ByteArray(0)


    init {
        zeroCoded = true
    }

    override fun packPayload(buffer: ByteBuffer) {
        packInt(buffer, channel)
        position.pack(buffer)
        packUUID(buffer, id)
        packUUID(buffer, ownerId)
        packVariable(buffer, name, 1)
        packByte(buffer, sourceType)
        packByte(buffer, type)
        packFloat(buffer, radius)
        packByte(buffer, simAccess)
        packVariable(buffer, message, 2)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        channel = unpackInt(buffer)
        position = LLVector3.unpack(buffer)
        id = unpackUUID(buffer)
        ownerId = unpackUUID(buffer)
        name = unpackVariable(buffer, 1)
        sourceType = unpackByte(buffer)
        type = unpackByte(buffer)
        radius = unpackFloat(buffer)
        simAccess = unpackByte(buffer)
        message = unpackVariable(buffer, 2)
    }

    override fun getMessageID(): Int = 0xFFFF00EF.toInt()

    override fun getMessageName(): String = "ChatPass"
}
