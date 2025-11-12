package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class SimulatorSetMapMessage : SLMessage() {
    var regionHandle: Long = 0L
    var type: Int = 0
    var mapImage: UUID = UUID(0L, 0L)


    override fun packPayload(buffer: ByteBuffer) {
        packLong(buffer, regionHandle)
        packInt(buffer, type)
        packUUID(buffer, mapImage)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        regionHandle = unpackLong(buffer)
        type = unpackInt(buffer)
        mapImage = unpackUUID(buffer)
    }

    override fun getMessageID(): Int = 0xFFFF0006

    override fun getMessageName(): String = "SimulatorSetMap"
}
