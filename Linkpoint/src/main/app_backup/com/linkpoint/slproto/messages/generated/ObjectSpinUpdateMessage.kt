package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.types.LLQuaternion
import java.nio.ByteBuffer
import java.util.UUID

class ObjectSpinUpdateMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var sessionId: UUID = UUID(0L, 0L)
    var objectId: UUID = UUID(0L, 0L)
    var rotation: LLQuaternion = LLQuaternion()


    init {
        zeroCoded = true
    }

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, sessionId)
        packUUID(buffer, objectId)
        rotation.pack(buffer)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        sessionId = unpackUUID(buffer)
        objectId = unpackUUID(buffer)
        rotation = LLQuaternion.unpack(buffer)
    }

    override fun getMessageID(): Int = 0xFFFF0079.toInt()

    override fun getMessageName(): String = "ObjectSpinUpdate"
}
