package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class UpdateSimulatorMessage : SLMessage() {
    var regionId: UUID = UUID(0L, 0L)
    var simName: ByteArray = ByteArray(0)
    var estateId: Int = 0
    var simAccess: Int = 0


    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, regionId)
        packVariable(buffer, simName, 1)
        packInt(buffer, estateId)
        packByte(buffer, simAccess)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        regionId = unpackUUID(buffer)
        simName = unpackVariable(buffer, 1)
        estateId = unpackInt(buffer)
        simAccess = unpackByte(buffer)
    }

    override fun getMessageID(): Int = 0xFFFF0011.toInt()

    override fun getMessageName(): String = "UpdateSimulator"
}
