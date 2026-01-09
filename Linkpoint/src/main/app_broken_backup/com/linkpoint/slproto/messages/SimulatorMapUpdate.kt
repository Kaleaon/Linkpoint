package com.linkpoint.slproto.messages
import java.util.*

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer

class SimulatorMapUpdate : SLMessage {
    MapData MapData_Field = MapData()

    class MapData {
        Int Flags
    }

    SimulatorMapUpdate() {
        this.zeroCoded = false
    }

    fun CalcPayloadSize(): Int {
        return 8
    }

    fun Handle(SLMessageHandler sLMessageHandler)  {
        sLMessageHandler.HandleSimulatorMapUpdate(this)
    }

    fun PackPayload(ByteBuffer byteBuffer)  {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) 5)
        packInt(byteBuffer, this.MapData_Field.Flags)
    }

    fun UnpackPayload(ByteBuffer byteBuffer)  {
        this.MapData_Field.Flags = unpackInt(byteBuffer)
    }
}
