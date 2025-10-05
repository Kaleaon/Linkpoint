package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class UpdateSimulator : SLMessage() {
    val SimulatorInfo_Field = SimulatorInfo()

    class SimulatorInfo {
        var RegionID: UUID? = null
        lateinit var SimName: ByteArray
        var EstateID: Int = 0
        var SimAccess: Int = 0
    }

    init {
        zeroCoded = false
    }

    override fun CalcPayloadSize(): Int {
        return SimulatorInfo_Field.SimName.size + 17 + 4 + 1 + 4
    }

    override fun Handle(handler: SLMessageHandler) {
        handler.HandleUpdateSimulator(this)
    }

    override fun PackPayload(buffer: ByteBuffer) {
        buffer.putShort(-1)
        buffer.put(0.toByte())
        buffer.put(17.toByte())
        packUUID(buffer, SimulatorInfo_Field.RegionID)
        packVariable(buffer, SimulatorInfo_Field.SimName, 1)
        packInt(buffer, SimulatorInfo_Field.EstateID)
        packByte(buffer, SimulatorInfo_Field.SimAccess.toByte())
    }

    override fun UnpackPayload(buffer: ByteBuffer) {
        SimulatorInfo_Field.RegionID = unpackUUID(buffer)
        SimulatorInfo_Field.SimName = unpackVariable(buffer, 1)
        SimulatorInfo_Field.EstateID = unpackInt(buffer)
        SimulatorInfo_Field.SimAccess = unpackByte(buffer).toInt() and UnsignedBytes.MAX_VALUE.toInt()
    }
}