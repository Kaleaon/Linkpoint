package com.lumiyaviewer.lumiya.slproto.messages
import java.util.*

import com.lumiyaviewer.lumiya.slproto.SLMessage
import java.nio.ByteBuffer

class SimulatorMapUpdate : SLMessage {
    MapData MapData_Field = MapData()

    class MapData {
        Int Flags
    }

    SimulatorMapUpdate() {
        this.zeroCoded = false
    }

    Int CalcPayloadSize() {
        return 8
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleSimulatorMapUpdate(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) 5)
        packInt(byteBuffer, this.MapData_Field.Flags)
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.MapData_Field.Flags = unpackInt(byteBuffer)
    }
}
