package com.lumiyaviewer.lumiya.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.lumiyaviewer.lumiya.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class UpdateSimulator : SLMessage {
    SimulatorInfo SimulatorInfo_Field = SimulatorInfo()

    class SimulatorInfo {
        Int EstateID
        UUID RegionID
        Int SimAccess
        ByteArray SimName
    }

    UpdateSimulator() {
        this.zeroCoded = false
    }

    Int CalcPayloadSize() {
        return this.SimulatorInfo_Field.SimName.length + 17 + 4 + 1 + 4
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleUpdateSimulator(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) 17)
        packUUID(byteBuffer, this.SimulatorInfo_Field.RegionID)
        packVariable(byteBuffer, this.SimulatorInfo_Field.SimName, 1)
        packInt(byteBuffer, this.SimulatorInfo_Field.EstateID)
        packByte(byteBuffer, (Byte) this.SimulatorInfo_Field.SimAccess)
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.SimulatorInfo_Field.RegionID = unpackUUID(byteBuffer)
        this.SimulatorInfo_Field.SimName = unpackVariable(byteBuffer, 1)
        this.SimulatorInfo_Field.EstateID = unpackInt(byteBuffer)
        this.SimulatorInfo_Field.SimAccess = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
    }
}
