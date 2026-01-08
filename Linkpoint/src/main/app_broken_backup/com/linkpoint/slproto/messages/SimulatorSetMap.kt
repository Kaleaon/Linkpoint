package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class SimulatorSetMap : SLMessage {
    MapData MapData_Field = MapData()

    class MapData {
        UUID MapImage
        Long RegionHandle
        Int Type
    }

    SimulatorSetMap() {
        this.zeroCoded = false
    }

    fun CalcPayloadSize(): Int {
        return 32
    }

    fun Handle(SLMessageHandler sLMessageHandler): Unit {
        sLMessageHandler.HandleSimulatorSetMap(this)
    }

    fun PackPayload(ByteBuffer byteBuffer): Unit {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) 6)
        packLong(byteBuffer, this.MapData_Field.RegionHandle)
        packInt(byteBuffer, this.MapData_Field.Type)
        packUUID(byteBuffer, this.MapData_Field.MapImage)
    }

    fun UnpackPayload(ByteBuffer byteBuffer): Unit {
        this.MapData_Field.RegionHandle = unpackLong(byteBuffer)
        this.MapData_Field.Type = unpackInt(byteBuffer)
        this.MapData_Field.MapImage = unpackUUID(byteBuffer)
    }
}
