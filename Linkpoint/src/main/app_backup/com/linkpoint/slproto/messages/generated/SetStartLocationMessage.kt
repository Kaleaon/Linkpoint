package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.types.LLVector3
import java.nio.ByteBuffer
import java.util.UUID

class SetStartLocationMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var regionId: UUID = UUID(0L, 0L)
    var locationId: Int = 0
    var regionHandle: Long = 0L
    var locationPos: LLVector3 = LLVector3()
    var locationLookAt: LLVector3 = LLVector3()


    init {
        zeroCoded = true
    }

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, regionId)
        packInt(buffer, locationId)
        packLong(buffer, regionHandle)
        locationPos.pack(buffer)
        locationLookAt.pack(buffer)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        regionId = unpackUUID(buffer)
        locationId = unpackInt(buffer)
        regionHandle = unpackLong(buffer)
        locationPos = LLVector3.unpack(buffer)
        locationLookAt = LLVector3.unpack(buffer)
    }

    override fun getMessageID(): Int = 0xFFFF0145.toInt()

    override fun getMessageName(): String = "SetStartLocation"
}
