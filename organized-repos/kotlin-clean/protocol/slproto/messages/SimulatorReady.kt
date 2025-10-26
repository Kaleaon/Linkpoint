package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.types.LLVector3
import java.nio.ByteBuffer
import java.util.UUID

class SimulatorReady : SLMessage() {
    public SimulatorBlock SimulatorBlock_Field = SimulatorBlock()
    public TelehubBlock TelehubBlock_Field = TelehubBlock()

    @JvmStatic
    class SimulatorBlock {
        public Int EstateID
        public Int ParentEstateID
        public Int RegionFlags
        public UUID RegionID
        public Int SimAccess
        public ByteArray SimName
    }

    @JvmStatic
    class TelehubBlock {
        public Boolean HasTelehub
        public LLVector3 TelehubPos
    }

    public SimulatorReady() {
        this.zeroCoded = true
    }

    public Int CalcPayloadSize() {
        return this.SimulatorBlock_Field.SimName.length + 1 + 1 + 4 + 16 + 4 + 4 + 4 + 13
    }

    fun Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleSimulatorReady(this)
    }

    fun PackPayload(ByteBuffer byteBuffer) {
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

    fun UnpackPayload(ByteBuffer byteBuffer) {
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
