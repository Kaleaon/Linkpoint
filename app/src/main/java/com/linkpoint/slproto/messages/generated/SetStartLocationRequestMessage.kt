package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.types.LLVector3
import java.nio.ByteBuffer
import java.util.UUID

class SetStartLocationRequestMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var sessionId: UUID = UUID(0L, 0L)
    var simName: ByteArray = ByteArray(0)
    var locationId: Int = 0
    var locationPos: LLVector3 = LLVector3()
    var locationLookAt: LLVector3 = LLVector3()


    init {
        zeroCoded = true
    }

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, sessionId)
        packVariable(buffer, simName, 1)
        packInt(buffer, locationId)
        locationPos.pack(buffer)
        locationLookAt.pack(buffer)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        sessionId = unpackUUID(buffer)
        simName = unpackVariable(buffer, 1)
        locationId = unpackInt(buffer)
        locationPos = LLVector3.unpack(buffer)
        locationLookAt = LLVector3.unpack(buffer)
    }

    override fun getMessageID(): Int = 0xFFFF0144

    override fun getMessageName(): String = "SetStartLocationRequest"
}
