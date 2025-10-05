package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer

class SimulatorMapUpdate : SLMessage() {
    val MapData_Field = MapData()

    class MapData {
        var Flags: Int = 0
    }

    init {
        zeroCoded = false
    }

    override fun CalcPayloadSize(): Int = 8

    override fun Handle(handler: SLMessageHandler) {
        handler.HandleSimulatorMapUpdate(this)
    }

    override fun PackPayload(buffer: ByteBuffer) {
        buffer.putShort(-1)
        buffer.put(0.toByte())
        buffer.put(5.toByte())
        packInt(buffer, MapData_Field.Flags)
    }

    override fun UnpackPayload(buffer: ByteBuffer) {
        MapData_Field.Flags = unpackInt(buffer)
    }
}