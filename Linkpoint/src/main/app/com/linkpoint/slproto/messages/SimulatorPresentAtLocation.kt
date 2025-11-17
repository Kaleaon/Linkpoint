package com.linkpoint.slproto.messages

import com.google.common.base.Ascii
import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.types.LLVector3
import java.net.Inet4Address
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.UUID

class SimulatorPresentAtLocation : SLMessage {
    NeighborBlock[] NeighborBlock_Fields = NeighborBlock[4]
    SimulatorBlock SimulatorBlock_Field
    SimulatorPublicHostBlock SimulatorPublicHostBlock_Field
    ArrayList<TelehubBlock> TelehubBlock_Fields = ArrayList<>()

    class NeighborBlock {
        Inet4Address IP
        Int Port
    }

    class SimulatorBlock {
        Int EstateID
        Int ParentEstateID
        Int RegionFlags
        UUID RegionID
        Int SimAccess
        ByteArray SimName
    }

    class SimulatorPublicHostBlock {
        Int GridX
        Int GridY
        Int Port
        Inet4Address SimulatorIP
    }

    class TelehubBlock {
        Boolean HasTelehub
        LLVector3 TelehubPos
    }

    SimulatorPresentAtLocation() {
        this.zeroCoded = false
        this.SimulatorPublicHostBlock_Field = SimulatorPublicHostBlock()
        for (Int i = 0; i < 4; i++) {
            this.NeighborBlock_Fields[i] = NeighborBlock()
        }
        this.SimulatorBlock_Field = SimulatorBlock()
    }

    Int CalcPayloadSize() {
        return this.SimulatorBlock_Field.SimName.length + 1 + 1 + 4 + 16 + 4 + 4 + 42 + 1 + (this.TelehubBlock_Fields.size() * 13)
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleSimulatorPresentAtLocation(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put(Ascii.VT)
        packShort(byteBuffer, (Short) this.SimulatorPublicHostBlock_Field.Port)
        packIPAddress(byteBuffer, this.SimulatorPublicHostBlock_Field.SimulatorIP)
        packInt(byteBuffer, this.SimulatorPublicHostBlock_Field.GridX)
        packInt(byteBuffer, this.SimulatorPublicHostBlock_Field.GridY)
        for (Int i = 0; i < 4; i++) {
            packIPAddress(byteBuffer, this.NeighborBlock_Fields[i].IP)
            packShort(byteBuffer, (Short) this.NeighborBlock_Fields[i].Port)
        }
        packVariable(byteBuffer, this.SimulatorBlock_Field.SimName, 1)
        packByte(byteBuffer, (Byte) this.SimulatorBlock_Field.SimAccess)
        packInt(byteBuffer, this.SimulatorBlock_Field.RegionFlags)
        packUUID(byteBuffer, this.SimulatorBlock_Field.RegionID)
        packInt(byteBuffer, this.SimulatorBlock_Field.EstateID)
        packInt(byteBuffer, this.SimulatorBlock_Field.ParentEstateID)
        byteBuffer.put((Byte) this.TelehubBlock_Fields.size())
        for (TelehubBlock telehubBlock : this.TelehubBlock_Fields) {
            packBoolean(byteBuffer, telehubBlock.HasTelehub)
            packLLVector3(byteBuffer, telehubBlock.TelehubPos)
        }
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.SimulatorPublicHostBlock_Field.Port = unpackShort(byteBuffer) & 65535
        this.SimulatorPublicHostBlock_Field.SimulatorIP = unpackIPAddress(byteBuffer)
        this.SimulatorPublicHostBlock_Field.GridX = unpackInt(byteBuffer)
        this.SimulatorPublicHostBlock_Field.GridY = unpackInt(byteBuffer)
        for (Int i = 0; i < 4; i++) {
            this.NeighborBlock_Fields[i].IP = unpackIPAddress(byteBuffer)
            this.NeighborBlock_Fields[i].Port = unpackShort(byteBuffer) & 65535
        }
        this.SimulatorBlock_Field.SimName = unpackVariable(byteBuffer, 1)
        this.SimulatorBlock_Field.SimAccess = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
        this.SimulatorBlock_Field.RegionFlags = unpackInt(byteBuffer)
        this.SimulatorBlock_Field.RegionID = unpackUUID(byteBuffer)
        this.SimulatorBlock_Field.EstateID = unpackInt(byteBuffer)
        this.SimulatorBlock_Field.ParentEstateID = unpackInt(byteBuffer)
        Byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i2 = 0; i2 < b; i2++) {
            TelehubBlock telehubBlock = TelehubBlock()
            telehubBlock.HasTelehub = unpackBoolean(byteBuffer)
            telehubBlock.TelehubPos = unpackLLVector3(byteBuffer)
            this.TelehubBlock_Fields.add(telehubBlock)
        }
    }
}
