package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.types.LLVector3
import java.nio.ByteBuffer
import java.util.UUID

class SimulatorReady : SLMessage {
    SimulatorBlock SimulatorBlock_Field = SimulatorBlock()
    TelehubBlock TelehubBlock_Field = TelehubBlock()

    class SimulatorBlock {
        Int EstateID
        Int ParentEstateID
        Int RegionFlags
        UUID RegionID
        Int SimAccess
        ByteArray SimName
    }

    class TelehubBlock {
        Boolean HasTelehub
        LLVector3 TelehubPos
    }

    SimulatorReady() {
        this.zeroCoded = true
    }

    Int CalcPayloadSize() {
        return this.SimulatorBlock_Field.SimName.length + 1 + 1 + 4 + 16 + 4 + 4 + 4 + 13
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleSimulatorReady(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) 9)
        packVariable(byteBuffer, this.SimulatorBlock_Field.SimName, 1)
        packByte(byteBuffer, (Byte) this.SimulatorBlock_Field.SimAccess)
        packInt(byteBuffer, this.SimulatorBlock_Field.RegionFlags)
        packUUID(byteBuffer, this.SimulatorBlock_Field.RegionID)
        packInt(byteBuffer, this.SimulatorBlock_Field.EstateID)
        packInt(byteBuffer, this.SimulatorBlock_Field.ParentEstateID)
        packBoolean(byteBuffer, this.TelehubBlock_Field.HasTelehub)
        packLLVector3(byteBuffer, this.TelehubBlock_Field.TelehubPos)
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.SimulatorBlock_Field.SimName = unpackVariable(byteBuffer, 1)
        this.SimulatorBlock_Field.SimAccess = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
        this.SimulatorBlock_Field.RegionFlags = unpackInt(byteBuffer)
        this.SimulatorBlock_Field.RegionID = unpackUUID(byteBuffer)
        this.SimulatorBlock_Field.EstateID = unpackInt(byteBuffer)
        this.SimulatorBlock_Field.ParentEstateID = unpackInt(byteBuffer)
        this.TelehubBlock_Field.HasTelehub = unpackBoolean(byteBuffer)
        this.TelehubBlock_Field.TelehubPos = unpackLLVector3(byteBuffer)
    }
}
