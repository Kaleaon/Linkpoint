package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.types.LLVector3
import java.nio.ByteBuffer
import java.util.UUID

class SimulatorReadyMessage : SLMessage() {
    var simName: ByteArray = ByteArray(0)
    var simAccess: Int = 0
    var regionFlags: Int = 0
    var regionId: UUID = UUID(0L, 0L)
    var estateId: Int = 0
    var parentEstateId: Int = 0
    var hasTelehub: Boolean = false
    var telehubPos: LLVector3 = LLVector3()


    init {
        zeroCoded = true
    }

    override fun packPayload(buffer: ByteBuffer) {
        packVariable(buffer, simName, 1)
        packByte(buffer, simAccess)
        packInt(buffer, regionFlags)
        packUUID(buffer, regionId)
        packInt(buffer, estateId)
        packInt(buffer, parentEstateId)
        packBoolean(buffer, hasTelehub)
        telehubPos.pack(buffer)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        simName = unpackVariable(buffer, 1)
        simAccess = unpackByte(buffer)
        regionFlags = unpackInt(buffer)
        regionId = unpackUUID(buffer)
        estateId = unpackInt(buffer)
        parentEstateId = unpackInt(buffer)
        hasTelehub = unpackBoolean(buffer)
        telehubPos = LLVector3.unpack(buffer)
    }

    override fun getMessageID(): Int = 0xFFFF0009.toInt()

    override fun getMessageName(): String = "SimulatorReady"
}
