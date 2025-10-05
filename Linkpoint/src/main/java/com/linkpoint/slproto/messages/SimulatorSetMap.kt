package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class SimulatorSetMap : SLMessage() {
    val MapData_Field = MapData()

    class MapData {
        var RegionHandle: Long = 0
        var Type: Int = 0
        var MapImage: UUID? = null
    }

    init {
        zeroCoded = false
    }

    override fun CalcPayloadSize(): Int = 32

    override fun Handle(handler: SLMessageHandler) {
        handler.HandleSimulatorSetMap(this)
    }

    override fun PackPayload(buffer: ByteBuffer) {
        buffer.putShort(-1)
        buffer.put(0.toByte())
        buffer.put(6.toByte())
        packLong(buffer, MapData_Field.RegionHandle)
        packInt(buffer, MapData_Field.Type)
        packUUID(buffer, MapData_Field.MapImage)
    }

    override fun UnpackPayload(buffer: ByteBuffer) {
        MapData_Field.RegionHandle = unpackLong(buffer)
        MapData_Field.Type = unpackInt(buffer)
        MapData_Field.MapImage = unpackUUID(buffer)
    }
}